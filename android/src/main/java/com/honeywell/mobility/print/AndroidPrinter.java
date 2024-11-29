package com.honeywell.mobility.print;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import com.intermec.print.service.ILinePrintListener;
import com.intermec.print.service.ILinePrintService;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import android.util.Log;

class AndroidPrinter implements IPrinterProxy {
   static final String LOG_TAG = "AndroidPrinter";
   private static ILinePrintService s_LinePrintService = null;
   private static boolean s_LinePrintServiceBound = false;
   private static Vector<Integer> s_InstanceHashCodes = new Vector();
   private static Context s_AppContext = null;
   private static Object s_ServiceLock = new Object();
   private static String ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE = "Print Service is not available";
   private static String ERROR_MSG_NO_PRINTER_CONNECTION = "No existing printer connection";
   private static String ERROR_MSG_PRINTER_CLOSED = "Printer already closed";
   private Printer m_Printer = null;
   private Integer m_HashCode = null;
   private int m_iPrintHandle = 0;
   private String m_sConfigFilePath = null;
   private String m_sPrinterID = null;
   private String m_sPrinterURI = null;
   private boolean m_fConnected = false;
   private Vector<PrintProgressListener> m_PrintProgressListeners = new Vector();
   private static ServiceConnection s_ServiceConnection = new ServiceConnection() {
      public void onServiceConnected(ComponentName className, IBinder service) {
         Log.d("AndroidPrinter", "LinePrintService connected");
         Logger.d("AndroidPrinter", "LinePrintService connected");
         synchronized(AndroidPrinter.s_ServiceLock) {
            AndroidPrinter.s_LinePrintService = ILinePrintService.Stub.asInterface(service);
            AndroidPrinter.s_ServiceLock.notify();
         }
      }

      public void onServiceDisconnected(ComponentName className) {
         Logger.d("AndroidPrinter", "LinePrintService disconnected");
         AndroidPrinter.s_LinePrintService = null;
      }
   };
   private ILinePrintListener m_LinePrintListener = new ILinePrintListener.Stub() {
      public void handleEvent(String aJSONStr) {
         Logger.d("AndroidPrinter", "handleEvent " + aJSONStr);

         try {
            JsonRpcUtil.PrintEvent event = new JsonRpcUtil.PrintEvent(aJSONStr);
            if (event.isPrintProgressEvent()) {
               JsonRpcUtil.PrintProgressEvent eventProgress = new JsonRpcUtil.PrintProgressEvent(aJSONStr);
               if (eventProgress.prtHandle == AndroidPrinter.this.m_iPrintHandle) {
                  AndroidPrinter.this.firePrintProgressEvent(eventProgress.progress);
               }
            }
         } catch (Exception var4) {
            Logger.d("AndroidPrinter", "ILinePrintListener exception: " + var4.getMessage());
         }

      }
   };

   public AndroidPrinter() {
      int iHashCode = this.hashCode();
      this.m_HashCode = iHashCode;
      Logger.d("AndroidPrinter", "AndroidPrinter constructor, hashCode = " + iHashCode);
      s_InstanceHashCodes.add(this.m_HashCode);
   }

   public void init(Printer aPrinter, String aConfigFilePath, String aPrinterID, String aPrinterURI, Printer.ExtraSettings extraSettings) throws PrinterException {
      Log.d("AndroidPrinter", "init");
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (null == extraSettings) {
         throw new PrinterException("extraSettings parameter is required on Android", ERROR_INVALID_PARAMETER);
      } else {
         this.m_Printer = aPrinter;
         this.m_sConfigFilePath = aConfigFilePath;
         this.m_sPrinterID = aPrinterID;
         this.m_sPrinterURI = aPrinterURI;
         if (null == s_AppContext) {
              Log.d("-------SETTING CONTEXT VIA extraSettings.getContext-----", " ");
            Object context = extraSettings.getContext();
              Log.d("------- CONTEXT SETTING DONE-----", context.toString());
  Log.d("------- init SERVICE -----", "");
            initService(context);
              Log.d("------- DONE init SERVICE -----", "");
         }

         if (s_LinePrintService != null) {


            try {
                Log.d("------- INSIDE s_LinePrintService -----", "");
               s_LinePrintService.addLinePrintListener(this.m_LinePrintListener);
               Log.d("-------DONE,  EXITING  s_LinePrintService-----", "");
            } catch (RemoteException var7) {
               throw new PrinterException(var7.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            this.getPrintHandle();
             Log.d("-------INIT COMPELETED-----", "");
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      }
   }

   public void addPrintProgressListener(PrintProgressListener aHandler) {
      synchronized(this.m_PrintProgressListeners) {
         if (aHandler != null && !this.m_PrintProgressListeners.contains(aHandler)) {
            this.m_PrintProgressListeners.add(aHandler);
         }

      }
   }

   public void close() throws PrinterException {
      Logger.d("AndroidPrinter", "close");
      this.disconnect();
      if (s_LinePrintService != null) {
         this.releasePrintHandle();

         try {
            s_LinePrintService.removeLinePrintListener(this.m_LinePrintListener);
         } catch (RemoteException var3) {
            Logger.d("AndroidPrinter", "close RemoteException, " + var3.getMessage());
         }

         try {
            if (s_InstanceHashCodes.size() > 0) {
               s_InstanceHashCodes.remove(this.m_HashCode);
            }

            if (s_InstanceHashCodes.size() == 0 && s_LinePrintServiceBound) {
               Logger.d("AndroidPrinter", "Unbinding LinePrintService");
               s_AppContext.unbindService(s_ServiceConnection);
               s_LinePrintServiceBound = false;
               s_AppContext = null;
               s_LinePrintService = null;
            }
         } catch (Exception var2) {
            Logger.d("AndroidPrinter", "close exception, " + var2.getMessage());
         }

         this.m_Printer = null;
      } else {
         throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
      }
   }

   public void connect() throws PrinterException {
      Logger.d("AndroidPrinter", "connect");
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_fConnected) {
         throw new PrinterException("An active printer connection already exists", ERROR_ALREADY_CONNECTED);
      } else if (s_LinePrintService != null) {
         String sJsonRpcRequest = JsonRpcUtil.getConnectJsonStr(this.m_iPrintHandle, this.m_sPrinterURI);

         String sJsonRpcResponse;
         try {
            sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
         } catch (RemoteException var4) {
            throw new PrinterException(var4.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
         }

         new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         this.m_fConnected = true;
      } else {
         throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
      }
   }

   public boolean disconnect() throws PrinterException {
      Logger.d("AndroidPrinter", "disconnect");
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_fConnected && this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getDisconnectJsonStr(this.m_iPrintHandle);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var4) {
               throw new PrinterException(var4.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
            this.m_fConnected = false;
            return true;
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         return false;
      }
   }

   public void endDoc() throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_fConnected && this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getEndDocJsonStr(this.m_iPrintHandle);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var4) {
               throw new PrinterException(var4.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_NO_PRINTER_CONNECTION, ERROR_NO_CONNECTION);
      }
   }

   public void flush() throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_fConnected && this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getFlushJsonStr(this.m_iPrintHandle);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var4) {
               throw new PrinterException(var4.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_NO_PRINTER_CONNECTION, ERROR_NO_CONNECTION);
      }
   }

   public void formFeed() throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_fConnected && this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getFormFeedJsonStr(this.m_iPrintHandle);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var4) {
               throw new PrinterException(var4.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_NO_PRINTER_CONNECTION, ERROR_NO_CONNECTION);
      }
   }

   public int getBytesWritten() throws PrinterException {
      // int iBytesWritten = false;
        int iBytesWritten = 0;
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_fConnected && this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getBytesWrittenJsonStr(this.m_iPrintHandle);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var5) {
               throw new PrinterException(var5.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            JsonRpcUtil.BytesWrittenResponse resp = new JsonRpcUtil.BytesWrittenResponse(sJsonRpcResponse);
             iBytesWritten = resp.bytesWritten;
            // int iBytesWritten = resp.bytesWritten;
            return iBytesWritten;
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_NO_PRINTER_CONNECTION, ERROR_NO_CONNECTION);
      }
   }

   public int[] getStatus() throws PrinterException {
      int[] msgNumbers = null;
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_fConnected && this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getGetStatusJsonStr(this.m_iPrintHandle);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var5) {
               throw new PrinterException(var5.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            JsonRpcUtil.PrinterStatusResponse resp = new JsonRpcUtil.PrinterStatusResponse(sJsonRpcResponse);
             msgNumbers = resp.msgNumbers;
            // int[] msgNumbers = resp.msgNumbers;
            return msgNumbers;
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_NO_PRINTER_CONNECTION, ERROR_NO_CONNECTION);
      }
   }

   public void newLine(int numLines) throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_fConnected && this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getNewLineJsonStr(this.m_iPrintHandle, numLines);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var5) {
               throw new PrinterException(var5.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_NO_PRINTER_CONNECTION, ERROR_NO_CONNECTION);
      }
   }

   public void removePrintProgressListener(PrintProgressListener aHandler) {
      synchronized(this.m_PrintProgressListeners) {
         if (aHandler != null) {
            this.m_PrintProgressListeners.remove(aHandler);
         }

      }
   }

   public void sendCustomCommand(String aCommandID) throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_fConnected && this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getSendCustomCmdJsonStr(this.m_iPrintHandle, aCommandID);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var5) {
               throw new PrinterException(var5.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_NO_PRINTER_CONNECTION, ERROR_NO_CONNECTION);
      }
   }

   public void setBold(boolean enabled) throws PrinterException {
      this.setFont("lp.setBold", enabled);
   }

   public void setCompress(boolean enabled) throws PrinterException {
      this.setFont("lp.setCompress", enabled);
   }

   public void setDoubleHigh(boolean enabled) throws PrinterException {
      this.setFont("lp.setDoubleHigh", enabled);
   }

   public void setDoubleWide(boolean enabled) throws PrinterException {
      this.setFont("lp.setDoubleWide", enabled);
   }

   public void setItalic(boolean enabled) throws PrinterException {
      this.setFont("lp.setItalic", enabled);
   }

   public void setSettingBool(String aSettingName, boolean aBoolValue) throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getSetSettingBoolJsonStr(this.m_iPrintHandle, aSettingName, aBoolValue);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var6) {
               throw new PrinterException(var6.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_PRINTER_CLOSED, ERROR_ALREADY_CLOSED);
      }
   }

   public void setSettingBytes(String aSettingName, byte[] aBytesValue) throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getSetSettingBytesJsonStr(this.m_iPrintHandle, aSettingName, aBytesValue);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var6) {
               throw new PrinterException(var6.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_PRINTER_CLOSED, ERROR_ALREADY_CLOSED);
      }
   }

   public void setSettingNum(String aSettingName, int aIntValue) throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getSetSettingNumJsonStr(this.m_iPrintHandle, aSettingName, aIntValue);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var6) {
               throw new PrinterException(var6.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_PRINTER_CLOSED, ERROR_ALREADY_CLOSED);
      }
   }

   public void setSettingString(String aSettingName, String aStrValue) throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getSetSettingStringJsonStr(this.m_iPrintHandle, aSettingName, aStrValue);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var6) {
               throw new PrinterException(var6.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_PRINTER_CLOSED, ERROR_ALREADY_CLOSED);
      }
   }

   public void setStrikeout(boolean enabled) throws PrinterException {
      this.setFont("lp.setStrikeout", enabled);
   }

   public void setUnderline(boolean enabled) throws PrinterException {
      this.setFont("lp.setUnderline", enabled);
   }

   public void startFileEcho(String aFilePath, boolean clearFile) throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getStartFileEchoJsonStr(this.m_iPrintHandle, aFilePath, clearFile);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var6) {
               throw new PrinterException(var6.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_PRINTER_CLOSED, ERROR_ALREADY_CLOSED);
      }
   }

   public void stopFileEcho() throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getStopFileEchoJsonStr(this.m_iPrintHandle);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var4) {
               throw new PrinterException(var4.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_PRINTER_CLOSED, ERROR_ALREADY_CLOSED);
      }
   }

   public void write(byte[] bytes) throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_fConnected && this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getWriteBytesJsonStr(this.m_iPrintHandle, bytes);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var5) {
               throw new PrinterException(var5.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_NO_PRINTER_CONNECTION, ERROR_NO_CONNECTION);
      }
   }

   public void write(String aStr) throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_fConnected && this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getWriteStrJsonStr(this.m_iPrintHandle, aStr);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var5) {
               throw new PrinterException(var5.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_NO_PRINTER_CONNECTION, ERROR_NO_CONNECTION);
      }
   }

   public void writeGraphic(String aGraphicFilePath, int aRotation, int aXOffset, int aWidth, int aHeight) throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_fConnected && this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getWriteGraphicJsonStr(this.m_iPrintHandle, aGraphicFilePath, aRotation, aXOffset, aWidth, aHeight);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var9) {
               throw new PrinterException(var9.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_NO_PRINTER_CONNECTION, ERROR_NO_CONNECTION);
      }
   }

   public void writeGraphicBase64(String aBase64Image, int aRotation, int aXOffset, int aWidth, int aHeight) throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_fConnected && this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getWriteGraphicBase64JsonStr(this.m_iPrintHandle, aBase64Image, aRotation, aXOffset, aWidth, aHeight);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var9) {
               throw new PrinterException(var9.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_NO_PRINTER_CONNECTION, ERROR_NO_CONNECTION);
      }
   }

   public void writeBarcode(int aSymbology, String aData, int aSize, int aXOffset) throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_fConnected && this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getWriteBarcodeJsonStr(this.m_iPrintHandle, aSymbology, aData, aSize, aXOffset);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var8) {
               throw new PrinterException(var8.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_NO_PRINTER_CONNECTION, ERROR_NO_CONNECTION);
      }
   }

   public void writeLabel(String aFormat, Hashtable aDictionary) throws PrinterException {
      if (this.isMainUIThread()) {
         throw new LabelPrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_fConnected && this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getWriteLabelJsonStr(this.m_iPrintHandle, aFormat, aDictionary);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var6) {
               throw new LabelPrinterException(var6.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new LabelPrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new LabelPrinterException(ERROR_MSG_NO_PRINTER_CONNECTION, ERROR_NO_CONNECTION);
      }
   }

   public void writeLine(String aStr) throws PrinterException {
      try {
         this.write(aStr);
      } catch (PrinterException var3) {
         throw new PrinterException(var3.getMessage(), var3.getErrorCode());
      }

      this.newLine(1);
   }

   private static void initService(Object aContext) throws PrinterException {
      if (aContext instanceof Context) {
         s_AppContext = ((Context)aContext).getApplicationContext();
         if (null == s_LinePrintService) {
            synchronized(s_ServiceLock) {
               Intent serviceIntent = new Intent(ILinePrintService.class.getName());
               serviceIntent.setComponent(new ComponentName("com.intermec.print.service", "com.intermec.print.service.LinePrintService"));
               s_LinePrintServiceBound = s_AppContext.bindService(serviceIntent, s_ServiceConnection, 1);
               if (s_LinePrintServiceBound) {
                  Logger.d("AndroidPrinter", "Bind LinePrintService succeeded");
                  Logger.d("AndroidPrinter", "Waiting for service connected...");

                  try {
                     s_ServiceLock.wait(10000L);
                  } catch (Exception var5) {
                     Logger.d("AndroidPrinter", var5.getMessage());
                  }
               }
            }
         }

      } else {
         throw new PrinterException("Invalid context specified in extraSettings parameter", ERROR_INVALID_CONTEXT);
      }
   }

   private boolean isMainUIThread() {
      return Looper.getMainLooper() == Looper.myLooper();
   }

   private void getPrintHandle() throws PrinterException {
      String sJsonRpcRequest = JsonRpcUtil.getObtainPrintHandleJsonStr(this.m_sConfigFilePath, this.m_sPrinterID);

      String sJsonRpcResponse;
      try {
         sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
      } catch (RemoteException var4) {
         throw new PrinterException(var4.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
      }

      JsonRpcUtil.PrintHandleResponse resp = new JsonRpcUtil.PrintHandleResponse(sJsonRpcResponse);
      this.m_iPrintHandle = resp.prtHandle;
   }

   private void releasePrintHandle() throws PrinterException {
      if (this.m_iPrintHandle != 0) {
         String sJsonRpcRequest = JsonRpcUtil.getReleasePrintHandleJsonStr(this.m_iPrintHandle);

         String sJsonRpcResponse;
         try {
            sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
         } catch (RemoteException var4) {
            throw new PrinterException(var4.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
         }

         new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         this.m_iPrintHandle = 0;
      }

   }

   private void firePrintProgressEvent(int aProgress) {
      synchronized(this.m_PrintProgressListeners) {
         if (this.m_PrintProgressListeners.size() > 0) {
            PrintProgressEvent event = new PrintProgressEvent(this.m_Printer, aProgress);
            Iterator i = this.m_PrintProgressListeners.iterator();

            while(i.hasNext()) {
               ((PrintProgressListener)i.next()).receivedStatus(event);
            }
         }

      }
   }

   private void setFont(String aJsonFontMethod, boolean enabled) throws PrinterException {
      if (this.isMainUIThread()) {
         throw new PrinterException("Method was called from UI thread", ERROR_INVALID_THREAD);
      } else if (this.m_fConnected && this.m_iPrintHandle != 0) {
         if (s_LinePrintService != null) {
            String sJsonRpcRequest = JsonRpcUtil.getSetFontJsonStr(this.m_iPrintHandle, aJsonFontMethod, enabled);

            String sJsonRpcResponse;
            try {
               sJsonRpcResponse = s_LinePrintService.execute(sJsonRpcRequest);
            } catch (RemoteException var6) {
               throw new PrinterException(var6.getMessage(), ERROR_UNEXPECTED_EXCEPTION);
            }

            new JsonRpcUtil.JsonRpcResponse(sJsonRpcResponse);
         } else {
            throw new PrinterException(ERROR_MSG_PRINT_SERVICE_NOT_AVAILABLE, ERROR_SERVICE_NOT_AVAILABLE);
         }
      } else {
         throw new PrinterException(ERROR_MSG_NO_PRINTER_CONNECTION, ERROR_NO_CONNECTION);
      }
   }
}
