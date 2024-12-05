package com.honeywell.mobility.print;

import java.lang.reflect.Constructor;

public abstract class Printer {
   protected static final int JAVA_PLATFORM_NOT_SET = 0;
   protected static final int JAVA_PLATFORM_ANDROID = 1;
   protected static final int JAVA_PLATFORM_ME = 2;
   protected IPrinterProxy m_PrinterProxy = null;
   protected int m_iJavaPlatformType = 0;

   protected Printer() {
   }

   public void addPrintProgressListener(PrintProgressListener aHandler) {
      this.m_PrinterProxy.addPrintProgressListener(aHandler);
   } 

   public void close() throws PrinterException {
      try {
         this.m_PrinterProxy.close();
      } catch (PrinterException var2) {
         throw this.newPrinterException(var2);
      }
   }

   public void connect() throws PrinterException {
      try {
         this.m_PrinterProxy.connect();
      } catch (PrinterException var2) {
         throw this.newPrinterException(var2);
      }
   }

   public boolean disconnect() throws PrinterException {
      try {
         return this.m_PrinterProxy.disconnect();
      } catch (PrinterException var2) {
         throw this.newPrinterException(var2);
      }
   }

   public void endDoc() throws PrinterException {
      try {
         this.m_PrinterProxy.endDoc();
      } catch (PrinterException var2) {
         throw this.newPrinterException(var2);
      }
   }

   public void flush() throws PrinterException {
      try {
         this.m_PrinterProxy.flush();
      } catch (PrinterException var2) {
         throw this.newPrinterException(var2);
      }
   }

   public void formFeed() throws PrinterException {
      try {
         this.m_PrinterProxy.formFeed();
      } catch (PrinterException var2) {
         throw this.newPrinterException(var2);
      }
   }

   public int getBytesWritten() throws PrinterException {
      try {
         return this.m_PrinterProxy.getBytesWritten();
      } catch (PrinterException var2) {
         throw this.newPrinterException(var2);
      }
   }

   public int[] getStatus() throws PrinterException {
      try {
         return this.m_PrinterProxy.getStatus();
      } catch (PrinterException var2) {
         throw this.newPrinterException(var2);
      }
   }

   public void removePrintProgressListener(PrintProgressListener aHandler) {
      this.m_PrinterProxy.removePrintProgressListener(aHandler);
   }

   public void sendCustomCommand(String aCommandID) throws PrinterException {
      if (null == aCommandID) {
         throw this.newPrinterException("null command identifier", IPrinterProxy.ERROR_INVALID_PARAMETER);
      } else {
         try {
            this.m_PrinterProxy.sendCustomCommand(aCommandID);
         } catch (PrinterException var3) {
            throw this.newPrinterException(var3);
         }
      }
   }

   public void setSettingBool(String aSettingName, boolean aBoolValue) throws PrinterException {
      if (null != aSettingName && aSettingName.length() != 0) {
         try {
            this.m_PrinterProxy.setSettingBool(aSettingName, aBoolValue);
         } catch (PrinterException var4) {
            throw this.newPrinterException(var4);
         }
      } else {
         throw this.newPrinterException("Setting name cannot be null or empty.", IPrinterProxy.ERROR_INVALID_PARAMETER);
      }
   }

   public void setSettingBytes(String aSettingName, byte[] aBytesValue) throws PrinterException {
      if (null != aSettingName && aSettingName.length() != 0) {
         if (null != aBytesValue && aBytesValue.length != 0) {
            try {
               this.m_PrinterProxy.setSettingBytes(aSettingName, aBytesValue);
            } catch (PrinterException var4) {
               throw this.newPrinterException(var4);
            }
         } else {
            throw this.newPrinterException("Byte array setting value cannot be null or empty.", IPrinterProxy.ERROR_INVALID_PARAMETER);
         }
      } else {
         throw this.newPrinterException("Setting name cannot be null or empty.", IPrinterProxy.ERROR_INVALID_PARAMETER);
      }
   }

   public void setSettingNum(String aSettingName, int aIntValue) throws PrinterException {
      if (null != aSettingName && aSettingName.length() != 0) {
         try {
            this.m_PrinterProxy.setSettingNum(aSettingName, aIntValue);
         } catch (PrinterException var4) {
            throw this.newPrinterException(var4);
         }
      } else {
         throw this.newPrinterException("Setting name cannot be null or empty.", IPrinterProxy.ERROR_INVALID_PARAMETER);
      }
   }

   public void setSettingString(String aSettingName, String aStrValue) throws PrinterException {
      if (null != aSettingName && aSettingName.length() != 0) {
         if (null != aStrValue && aStrValue.length() != 0) {
            try {
               this.m_PrinterProxy.setSettingString(aSettingName, aStrValue);
            } catch (PrinterException var4) {
               throw this.newPrinterException(var4);
            }
         } else {
            throw this.newPrinterException("String setting value cannot be null or empty.", IPrinterProxy.ERROR_INVALID_PARAMETER);
         }
      } else {
         throw this.newPrinterException("Setting name cannot be null or empty.", IPrinterProxy.ERROR_INVALID_PARAMETER);
      }
   }

   public void startFileEcho(String aFilePath, boolean clearFile) throws PrinterException {
      if (null != aFilePath && aFilePath.length() != 0) {
         try {
            this.m_PrinterProxy.startFileEcho(aFilePath, clearFile);
         } catch (PrinterException var4) {
            throw this.newPrinterException(var4);
         }
      } else {
         throw this.newPrinterException("Echo file path cannot be null or empty.", IPrinterProxy.ERROR_INVALID_PARAMETER);
      }
   }

   public void stopFileEcho() throws PrinterException {
      try {
         this.m_PrinterProxy.stopFileEcho();
      } catch (PrinterException var2) {
         throw this.newPrinterException(var2);
      }
   }

   public void write(byte[] bytes) throws PrinterException {
      if (null != bytes && bytes.length != 0) {
         try {
            this.m_PrinterProxy.write(bytes);
         } catch (PrinterException var3) {
            throw this.newPrinterException(var3);
         }
      } else {
         throw this.newPrinterException("Byte buffer to write cannot be null or empty.", IPrinterProxy.ERROR_INVALID_PARAMETER);
      }
   }

   public void write(String aStr) throws PrinterException {
      if (null != aStr && aStr.length() != 0) {
         try {
            this.m_PrinterProxy.write(aStr);
         } catch (PrinterException var3) {
            throw this.newPrinterException(var3);
         }
      } else {
         throw this.newPrinterException("String to write cannot be null or empty.", IPrinterProxy.ERROR_INVALID_PARAMETER);
      }
   }

   protected void newPrinter(String aCmdAttribFilePath, String aPrinterID, String aPrinterURI, Printer.ExtraSettings extraSettings) throws PrinterException {
      this.getJavaPlatformType();
      if (this.m_iJavaPlatformType == 1) {
         if (null == extraSettings) {
            throw new PrinterException("extraSettings parameter is required on Android", IPrinterProxy.ERROR_INVALID_PARAMETER);
         }

         if (null == aCmdAttribFilePath) {
            throw new PrinterException("null commands and attributes file path", IPrinterProxy.ERROR_INVALID_PARAMETER);
         }

         this.m_PrinterProxy = this.getPrinterProxy(this.m_iJavaPlatformType);
      }

      if (this.m_PrinterProxy != null) {
         this.m_PrinterProxy.init(this, aCmdAttribFilePath, aPrinterID, aPrinterURI, extraSettings);
      } else {
         throw new PrinterException("platform not supported", IPrinterProxy.ERROR_NOT_SUPPORTED);
      }
   }

   protected PrinterException newPrinterException(String aMsg, int aErrorCode) {
      return new PrinterException(aMsg, aErrorCode);
   }

   protected PrinterException newPrinterException(PrinterException aPrinterException) {
      return aPrinterException;
   }

   private int getJavaPlatformType() {
      if (this.m_iJavaPlatformType == 0) {
         Class androidAppClass = null;

         try {
            androidAppClass = Class.forName("android.app.Application");
         } catch (ClassNotFoundException var3) {
         }

         if (androidAppClass != null) {
            this.m_iJavaPlatformType = 1;
         } else {
            this.m_iJavaPlatformType = 2;
         }
      }

      return this.m_iJavaPlatformType;
   }

   private IPrinterProxy getPrinterProxy(int aJavaPlatformType) throws PrinterException {
      Class androidLPClass = null;
      IPrinterProxy printerProxy = null;
      if (aJavaPlatformType == 1) {
         try {
            androidLPClass = Class.forName("com.honeywell.mobility.print.AndroidPrinter");
            Class[] types = new Class[0];
            Constructor constructor = androidLPClass.getConstructor(types);
            Class[] args = new Class[0];
            Object instance = constructor.newInstance(args);
            if (!(instance instanceof IPrinterProxy)) {
               throw new PrinterException("Not an instance of IPrinterProxy", IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
            }

            printerProxy = (IPrinterProxy)instance;
         } catch (Exception var8) {
            throw new PrinterException(var8.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
         }
      }

      return printerProxy;
   }

   public static class ExtraSettings {
      private Object m_Context = null;

      public Object getContext() {
         return this.m_Context;
      }

      public void setContext(Object aContext) {
         this.m_Context = aContext;
      }
   }
}
