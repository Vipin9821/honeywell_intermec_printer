package com.honeywell.mobility.print;

import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

class JsonRpcUtil {
   public static final String EVENT_NAME_PRINT_PROGRESS = "lp.printProgressEvent";
   public static final String KEY_NAME_JSONRPC = "jsonrpc";
   public static final String KEY_NAME_METHOD = "method";
   public static final String KEY_NAME_PARAMS = "params";
   public static final String KEY_NAME_ID = "id";
   public static final String KEY_NAME_RESULT = "result";
   public static final String KEY_NAME_ERROR = "error";
   public static final String METHOD_CONNECT = "lp.connect";
   public static final String METHOD_DISCONNECT = "lp.disconnect";
   public static final String METHOD_END_DOC = "lp.endDoc";
   public static final String METHOD_FLUSH = "lp.flush";
   public static final String METHOD_FORM_FEED = "lp.formFeed";
   public static final String METHOD_GET_BYTES_WRITTEN = "lp.getBytesWritten";
   public static final String METHOD_GET_STATUS = "lp.getStatus";
   public static final String METHOD_NEW_LINE = "lp.newLine";
   public static final String METHOD_OBTAIN_PRINT_HANDLE = "lp.obtainPrintHandle";
   public static final String METHOD_RELEASE_PRINT_HANDLE = "lp.releasePrintHandle";
   public static final String METHOD_SEND_CUSTOM_CMD = "lp.sendCustomCommand";
   public static final String METHOD_SET_BOLD = "lp.setBold";
   public static final String METHOD_SET_COMPRESS = "lp.setCompress";
   public static final String METHOD_SET_DOUBLE_HIGH = "lp.setDoubleHigh";
   public static final String METHOD_SET_DOUBLE_WIDE = "lp.setDoubleWide";
   public static final String METHOD_SET_ITALIC = "lp.setItalic";
   public static final String METHOD_SET_SETTING_BOOL = "lp.setSettingBool";
   public static final String METHOD_SET_SETTING_BYTES = "lp.setSettingBytes";
   public static final String METHOD_SET_SETTING_NUM = "lp.setSettingNum";
   public static final String METHOD_SET_STRIKEOUT = "lp.setStrikeout";
   public static final String METHOD_SET_SETTING_STRING = "lp.setSettingString";
   public static final String METHOD_START_FILE_ECHO = "lp.startFileEcho";
   public static final String METHOD_STOP_FILE_ECHO = "lp.stopFileEcho";
   public static final String METHOD_SET_UNDERLINE = "lp.setUnderline";
   public static final String METHOD_WRITE_STR = "lp.writeStr";
   public static final String METHOD_WRITE_BYTES = "lp.writeBytes";
   public static final String METHOD_WRITE_GRAPHIC = "lp.writeGraphic";
   public static final String METHOD_WRITE_GRAPHIC_BASE64 = "lp.writeGraphicBase64";
   public static final String METHOD_WRITE_BARCODE = "lp.writeBarcode";
   public static final String METHOD_WRITE_LABEL = "lp.writeLabel";
   public static final String PARAM_PRINT_HANDLE = "prtHandle";
   public static final String PARAM_CONFIG_FILE_PATH = "configFilePath";
   public static final String PARAM_PRINTER_ENTRY = "printerEntry";
   public static final String PARAM_PRINTER_URI = "printerURI";
   public static final String PARAM_DATA = "data";
   public static final String PARAM_NUM_OF_LINES = "numLines";
   public static final String PARAM_ENABLED = "enabled";
   public static final String PARAM_PROGRESS = "progress";
   public static final String PARAM_GRAPHIC_FILE = "graphicFile";
   public static final String PARAM_ROTATION = "rotation";
   public static final String PARAM_WIDTH = "width";
   public static final String PARAM_HEIGHT = "height";
   public static final String PARAM_XOFFSET = "xOffset";
   public static final String PARAM_CMD_ID = "commandID";
   public static final String PARAM_NAME = "name";
   public static final String PARAM_VALUE = "value";
   public static final String PARAM_SYMBOLOGY = "symbology";
   public static final String PARAM_SIZE = "size";
   public static final String PARAM_ECHO_FILE_PATH = "echoFilePath";
   public static final String PARAM_CLEAR_FILE = "clearFile";
   public static final String PARAM_IMAGE_DATA_BASE64 = "imageDataBase64";
   public static final String PARAM_FORMAT = "format";
   public static final String PARAM_DICTIONARY = "dictionary";
   public static final String RESULT_BYTES_WRITTEN = "bytesWritten";
   public static final String RESULT_MSG_NUMBERS = "msgNumbers";
   public static final String RESULT_PRINT_HANDLE = "prtHandle";
   public static final String ERROR_OBJ_CODE = "code";
   public static final String ERROR_OBJ_MESSAGE = "message";
   public static final String EVENT_PARAM_PROGRESS_CANCEL = "cancel";
   public static final String EVENT_PARAM_PROGRESS_COMPLETE = "complete";
   public static final String EVENT_PARAM_PROGRESS_ENDDOC = "endDoc";
   public static final String EVENT_PARAM_PROGRESS_ENDPAGE = "endPage";
   public static final String EVENT_PARAM_PROGRESS_FINISHED = "finished";
   public static final String EVENT_PARAM_PROGRESS_NONE = "none";
   public static final String EVENT_PARAM_PROGRESS_STARTDOC = "startDoc";
   public static final String EVENT_PARAM_PROGRESS_STARTPAGE = "startPage";
   public static final String EVENT_PARAM_PROGRESS_USERCANCEL = "userCancel";
   public static final int JSON_NULL_METHOD_ID = -1;
   private static int s_iMethodID = 0;
   private static Hashtable<Integer, String> s_WriteBarcodeSymbologyMap = null;

   public static String getConnectJsonStr(int aHandle, String aPrinterURI) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.connect");
         jsonParamsObj.put("prtHandle", aHandle);
         if (aPrinterURI != null) {
            jsonParamsObj.put("printerURI", aPrinterURI);
         } else {
            jsonParamsObj.put("printerURI", JSONObject.NULL);
         }

         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var5) {
         throw new PrinterException(var5.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getDisconnectJsonStr(int aHandle) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.disconnect");
         jsonParamsObj.put("prtHandle", aHandle);
         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var4) {
         throw new PrinterException(var4.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getEndDocJsonStr(int aHandle) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.endDoc");
         jsonParamsObj.put("prtHandle", aHandle);
         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var4) {
         throw new PrinterException(var4.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getFlushJsonStr(int aHandle) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.flush");
         jsonParamsObj.put("prtHandle", aHandle);
         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var4) {
         throw new PrinterException(var4.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getFormFeedJsonStr(int aHandle) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.formFeed");
         jsonParamsObj.put("prtHandle", aHandle);
         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var4) {
         throw new PrinterException(var4.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getBytesWrittenJsonStr(int aHandle) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.getBytesWritten");
         jsonParamsObj.put("prtHandle", aHandle);
         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var4) {
         throw new PrinterException(var4.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getGetStatusJsonStr(int aHandle) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.getStatus");
         jsonParamsObj.put("prtHandle", aHandle);
         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var4) {
         throw new PrinterException(var4.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getNewLineJsonStr(int aHandle, int aNumOfLines) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.newLine");
         jsonParamsObj.put("prtHandle", aHandle);
         jsonParamsObj.put("numLines", aNumOfLines);
         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var5) {
         throw new PrinterException(var5.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getObtainPrintHandleJsonStr(String aConfigFilePath, String aPrinterID) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParmsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.obtainPrintHandle");
         if (aConfigFilePath != null) {
            jsonParmsObj.put("configFilePath", aConfigFilePath);
         } else {
            jsonParmsObj.put("configFilePath", JSONObject.NULL);
         }

         if (aPrinterID != null) {
            jsonParmsObj.put("printerEntry", aPrinterID);
         } else {
            jsonParmsObj.put("printerEntry", JSONObject.NULL);
         }

         jsonObj.put("params", jsonParmsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var5) {
         throw new PrinterException(var5.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getReleasePrintHandleJsonStr(int aHandle) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParmsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.releasePrintHandle");
         jsonParmsObj.put("prtHandle", aHandle);
         jsonObj.put("params", jsonParmsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var4) {
         throw new PrinterException(var4.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getSendCustomCmdJsonStr(int aHandle, String aCommandID) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.sendCustomCommand");
         jsonParamsObj.put("prtHandle", aHandle);
         if (aCommandID != null) {
            jsonParamsObj.put("commandID", aCommandID);
         } else {
            jsonParamsObj.put("commandID", JSONObject.NULL);
         }

         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var5) {
         throw new PrinterException(var5.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getSetFontJsonStr(int aHandle, String aSetFontMethodName, boolean enabled) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", aSetFontMethodName);
         jsonParamsObj.put("prtHandle", aHandle);
         jsonParamsObj.put("enabled", enabled);
         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var6) {
         throw new PrinterException(var6.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getSetSettingBoolJsonStr(int aHandle, String aSettingName, boolean aBoolValue) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.setSettingBool");
         jsonParamsObj.put("prtHandle", aHandle);
         if (aSettingName != null) {
            jsonParamsObj.put("name", aSettingName);
         } else {
            jsonParamsObj.put("name", JSONObject.NULL);
         }

         jsonParamsObj.put("value", aBoolValue);
         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var6) {
         throw new PrinterException(var6.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getSetSettingBytesJsonStr(int aHandle, String aSettingName, byte[] aBytesValue) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.setSettingBytes");
         jsonParamsObj.put("prtHandle", aHandle);
         if (aSettingName != null) {
            jsonParamsObj.put("name", aSettingName);
         } else {
            jsonParamsObj.put("name", JSONObject.NULL);
         }

         if (aBytesValue != null) {
            jsonParamsObj.put("value", new String(aBytesValue));
         } else {
            jsonParamsObj.put("value", JSONObject.NULL);
         }

         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var6) {
         throw new PrinterException(var6.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getSetSettingNumJsonStr(int aHandle, String aSettingName, int aIntValue) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.setSettingNum");
         jsonParamsObj.put("prtHandle", aHandle);
         if (aSettingName != null) {
            jsonParamsObj.put("name", aSettingName);
         } else {
            jsonParamsObj.put("name", JSONObject.NULL);
         }

         jsonParamsObj.put("value", aIntValue);
         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var6) {
         throw new PrinterException(var6.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getSetSettingStringJsonStr(int aHandle, String aSettingName, String aStrValue) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.setSettingString");
         jsonParamsObj.put("prtHandle", aHandle);
         if (aSettingName != null) {
            jsonParamsObj.put("name", aSettingName);
         } else {
            jsonParamsObj.put("name", JSONObject.NULL);
         }

         if (aStrValue != null) {
            jsonParamsObj.put("value", aStrValue);
         } else {
            jsonParamsObj.put("value", JSONObject.NULL);
         }

         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var6) {
         throw new PrinterException(var6.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getStartFileEchoJsonStr(int aHandle, String aFilePath, boolean clearFile) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.startFileEcho");
         jsonParamsObj.put("prtHandle", aHandle);
         if (aFilePath != null) {
            jsonParamsObj.put("echoFilePath", aFilePath);
         } else {
            jsonParamsObj.put("echoFilePath", JSONObject.NULL);
         }

         jsonParamsObj.put("clearFile", clearFile);
         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var6) {
         throw new PrinterException(var6.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getStopFileEchoJsonStr(int aHandle) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.stopFileEcho");
         jsonParamsObj.put("prtHandle", aHandle);
         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var4) {
         throw new PrinterException(var4.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getWriteBytesJsonStr(int aHandle, byte[] aByteArray) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.writeBytes");
         jsonParamsObj.put("prtHandle", aHandle);
         if (aByteArray == null) {
            jsonParamsObj.put("data", JSONObject.NULL);
         } else {
            boolean bNeedEncode = false;

            for(int i = 0; i < aByteArray.length; ++i) {
               if (aByteArray[i] >= -128) {
                  bNeedEncode = true;
                  break;
               }
            }

            String dataStr = bNeedEncode ? new String(encodeUTF8(aByteArray)) : new String(aByteArray);
            jsonParamsObj.put("data", dataStr);
         }

         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var6) {
         throw new PrinterException(var6.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getWriteGraphicJsonStr(int aHandle, String aGraphicFilePath, int aRotation, int aXOffset, int aWidth, int aHeight) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.writeGraphic");
         jsonParamsObj.put("prtHandle", aHandle);
         if (aGraphicFilePath != null) {
            jsonParamsObj.put("graphicFile", aGraphicFilePath);
         } else {
            jsonParamsObj.put("graphicFile", JSONObject.NULL);
         }

         jsonParamsObj.put("rotation", aRotation);
         jsonParamsObj.put("xOffset", aXOffset);
         jsonParamsObj.put("width", aWidth);
         jsonParamsObj.put("height", aHeight);
         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var9) {
         throw new PrinterException(var9.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getWriteGraphicBase64JsonStr(int aHandle, String aBase64Image, int aRotation, int aXOffset, int aWidth, int aHeight) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.writeGraphicBase64");
         jsonParamsObj.put("prtHandle", aHandle);
         if (aBase64Image != null) {
            jsonParamsObj.put("imageDataBase64", aBase64Image);
         } else {
            jsonParamsObj.put("imageDataBase64", JSONObject.NULL);
         }

         jsonParamsObj.put("rotation", aRotation);
         jsonParamsObj.put("xOffset", aXOffset);
         jsonParamsObj.put("width", aWidth);
         jsonParamsObj.put("height", aHeight);
         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var9) {
         throw new PrinterException(var9.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getWriteBarcodeJsonStr(int aHandle, int aSymbology, String aData, int aSize, int aXOffset) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.writeBarcode");
         jsonParamsObj.put("prtHandle", aHandle);
         jsonParamsObj.put("symbology", getBarcodeSymbologyName(aSymbology));
         if (aData != null) {
            jsonParamsObj.put("data", aData);
         } else {
            jsonParamsObj.put("data", JSONObject.NULL);
         }

         jsonParamsObj.put("size", aSize);
         jsonParamsObj.put("xOffset", aXOffset);
         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var8) {
         throw new PrinterException(var8.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getWriteStrJsonStr(int aHandle, String aDataStr) throws PrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.writeStr");
         jsonParamsObj.put("prtHandle", aHandle);
         if (aDataStr != null) {
            jsonParamsObj.put("data", aDataStr);
         } else {
            jsonParamsObj.put("data", JSONObject.NULL);
         }

         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
      } catch (JSONException var5) {
         throw new PrinterException(var5.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }

      return jsonObj.toString();
   }

   public static String getWriteLabelJsonStr(int aHandle, String aFormat, Hashtable aDictionary) throws LabelPrinterException {
      JSONObject jsonObj = new JSONObject();
      JSONObject jsonParamsObj = new JSONObject();
      JSONObject jsonDictObj = new JSONObject();

      try {
         jsonObj.put("jsonrpc", "2.0");
         jsonObj.put("method", "lp.writeLabel");
         jsonParamsObj.put("prtHandle", aHandle);
         if (aFormat != null) {
            jsonParamsObj.put("format", aFormat);
         } else {
            jsonParamsObj.put("format", JSONObject.NULL);
         }

         Iterator it = aDictionary.entrySet().iterator();

         while(it.hasNext()) {
            Entry entry = (Entry)it.next();
            String key = null;
            String value = null;
            if (!(entry.getKey() instanceof String)) {
               throw new LabelPrinterException("Dictionary key not a String object", IPrinterProxy.ERROR_INVALID_PARAMETER);
            }

            key = (String)entry.getKey();
            if (!(entry.getValue() instanceof String)) {
               throw new LabelPrinterException("Dictionary value not a String object", IPrinterProxy.ERROR_INVALID_PARAMETER);
            }

            value = (String)entry.getValue();
            if (key == null || value == null) {
               throw new LabelPrinterException("Null dictionary key/value", IPrinterProxy.ERROR_INVALID_PARAMETER);
            }

            jsonDictObj.put(key, value);
         }

         jsonParamsObj.put("dictionary", jsonDictObj);
         jsonObj.put("params", jsonParamsObj);
         jsonObj.put("id", getMethodID());
         return jsonObj.toString();
      } catch (JSONException var10) {
         throw new LabelPrinterException(var10.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
      }
   }

   public static int getMethodID() {
      if (s_iMethodID < Integer.MAX_VALUE) {
         ++s_iMethodID;
      } else {
         s_iMethodID = 1;
      }

      return s_iMethodID;
   }

   public static void initBarcodeSymbologyMap() {
      if (null == s_WriteBarcodeSymbologyMap) {
         s_WriteBarcodeSymbologyMap = new Hashtable(10);
         if (s_WriteBarcodeSymbologyMap != null) {
            s_WriteBarcodeSymbologyMap.put(1, "Code39");
            s_WriteBarcodeSymbologyMap.put(2, "Code128");
         }
      }

   }

   public static String getBarcodeSymbologyName(int aSymbId) {
      initBarcodeSymbologyMap();
      return (String)s_WriteBarcodeSymbologyMap.get(aSymbId);
   }

   public static byte[] encodeUTF8(byte[] source) {
      ByteBuffer bb = ByteBuffer.allocate(source.length * 2);
      bb.mark();
      byte[] utf8ByteArray = null;

      int arrayLen;
      for(arrayLen = 0; arrayLen < source.length; ++arrayLen) {
         int b = source[arrayLen] & 255;
         if (b >= 128) {
            bb.put((byte)(192 | b >> 6));
            bb.put((byte)(128 | b & 63));
         } else {
            bb.put((byte)b);
         }
      }

      arrayLen = bb.position();
       utf8ByteArray = new byte[arrayLen];
         // byte[] utf8ByteArray = new byte[arrayLen];
      bb.reset();
      bb.get(utf8ByteArray, 0, arrayLen);
      return utf8ByteArray;
   }

   static class PrintProgressEvent extends JsonRpcUtil.PrintEvent {
      public int prtHandle;
      public int progress;
      private String m_sProgress = null;

      public PrintProgressEvent(String aEventMsg) throws PrinterException {
         super(aEventMsg);

         try {
            JSONObject jsObj = (JSONObject)(new JSONTokener(aEventMsg)).nextValue();
            if (jsObj.isNull("params")) {
               throw new PrinterException("JSON-RPC print progress event does not contain params object", IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
            }

            JSONObject jsParams = jsObj.getJSONObject("params");
            this.prtHandle = jsParams.getInt("prtHandle");
            this.m_sProgress = jsParams.getString("progress");
         } catch (JSONException var4) {
            throw new PrinterException(var4.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
         }

         if (this.m_sProgress != null) {
            if (this.m_sProgress.equals("cancel")) {
               this.progress = 6;
            } else if (this.m_sProgress.equals("complete")) {
               this.progress = 8;
            } else if (this.m_sProgress.equals("endDoc")) {
               this.progress = 4;
            } else if (this.m_sProgress.equals("finished")) {
               this.progress = 7;
            } else if (this.m_sProgress.equals("startDoc")) {
               this.progress = 1;
            } else if (this.m_sProgress.equals("none")) {
               this.progress = 0;
            } else {
               Logger.d("AndroidPrinter", "JSON-RPC event unsupported progress: " + this.m_sProgress);
               this.progress = 0;
            }

         } else {
            throw new PrinterException("JSON-RPC print progress event null progress", IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
         }
      }
   }

   static class PrintEvent {
      public String jsonRpcVersion = null;
      public String eventName;

      public PrintEvent(String aEventMsg) throws PrinterException {
         if (aEventMsg != null) {
            try {
               JSONObject jsObj = (JSONObject)(new JSONTokener(aEventMsg)).nextValue();
               this.jsonRpcVersion = jsObj.getString("jsonrpc");
               if (!jsObj.isNull("method")) {
                  this.eventName = jsObj.getString("method");
               } else {
                  throw new PrinterException("JSON-RPC event does not contain method object", IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
               }
            } catch (JSONException var3) {
               throw new PrinterException(var3.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
            }
         } else {
            throw new PrinterException("Null JSON-RPC event message", IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
         }
      }

      public boolean isPrintProgressEvent() {
         return this.eventName != null && this.eventName.equals("lp.printProgressEvent");
      }
   }

   static class PrinterStatusResponse extends JsonRpcUtil.JsonRpcResponse {
      public int[] msgNumbers = null;

      public PrinterStatusResponse(String aResponse) throws PrinterException {
         super(aResponse);
         if (this.result == null) {
            throw new PrinterException("JSON-RPC lp.getStatus response contains null result", IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
         } else {
            try {
               JSONArray array = this.result.getJSONArray("msgNumbers");
               if (array != null) {
                  this.msgNumbers = new int[array.length()];

                  for(int i = 0; i < array.length(); ++i) {
                     this.msgNumbers[i] = array.optInt(i);
                  }
               }

            } catch (JSONException var4) {
               throw new PrinterException(var4.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
            }
         }
      }
   }

   static class BytesWrittenResponse extends JsonRpcUtil.JsonRpcResponse {
      public int bytesWritten = 0;

      public BytesWrittenResponse(String aResponse) throws PrinterException {
         super(aResponse);
         if (this.result != null) {
            try {
               this.bytesWritten = this.result.getInt("bytesWritten");
            } catch (JSONException var3) {
               throw new PrinterException(var3.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
            }
         } else {
            throw new PrinterException("JSON-RPC lp.getBytesWritten response contains null result", IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
         }
      }
   }

   static class PrintHandleResponse extends JsonRpcUtil.JsonRpcResponse {
      public int prtHandle = 0;

      public PrintHandleResponse(String aResponse) throws PrinterException {
         super(aResponse);
         if (this.result != null) {
            try {
               this.prtHandle = this.result.getInt("prtHandle");
            } catch (JSONException var3) {
               throw new PrinterException(var3.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
            }
         } else {
            throw new PrinterException("JSON-RPC lp.obtainPrintHandle response contains null result", IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
         }
      }
   }

   static class JsonRpcResponse {
      public String jsonRpcVersion = null;
      public JSONObject result = null;
      public JsonRpcUtil.JsonRpcError error = null;
      public int methodID = -1;
      protected boolean m_fHasResult = false;

      public JsonRpcResponse(String aResponse) throws PrinterException {
         if (aResponse != null) {
            Logger.d("JsonRpcResponse", aResponse);

            try {
               JSONObject jsObj = (JSONObject)(new JSONTokener(aResponse)).nextValue();
               this.jsonRpcVersion = jsObj.getString("jsonrpc");
               if (jsObj.has("result")) {
                  this.m_fHasResult = true;
                  if (!jsObj.isNull("result")) {
                     this.result = jsObj.getJSONObject("result");
                  }
               }

               if (!jsObj.isNull("error")) {
                  JSONObject jsErrorObj = jsObj.getJSONObject("error");
                  this.error = new JsonRpcUtil.JsonRpcError();
                  this.error.code = jsErrorObj.getInt("code");
                  this.error.message = jsErrorObj.getString("message");
               }

               if (!jsObj.isNull("id")) {
                  this.methodID = jsObj.getInt("id");
               }

               if (this.hasError()) {
                  throw new PrinterException(this.error.message, this.error.code);
               } else if (!this.m_fHasResult) {
                  throw new PrinterException("JSON-RPC response does not contain result", IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
               }
            } catch (JSONException var4) {
               throw new PrinterException(var4.getMessage(), IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
            }
         } else {
            throw new PrinterException("Null JSON-RPC response", IPrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
         }
      }

      public boolean hasError() {
         return this.error != null;
      }

      public boolean hasResult() {
         return this.m_fHasResult;
      }

      public boolean isMethodIDNull() {
         return this.methodID == -1;
      }
   }

   static class JsonRpcError {
      public int code = 0;
      public String message = null;
   }
}
