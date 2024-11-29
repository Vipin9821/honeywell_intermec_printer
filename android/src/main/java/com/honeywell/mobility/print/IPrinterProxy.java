package com.honeywell.mobility.print;

import java.util.Hashtable;

public interface IPrinterProxy {
   int ERROR_UNEXPECTED_EXCEPTION = PrinterException.formatError(65535, 2309);
   int ERROR_NO_CONNECTION = PrinterException.formatError(1229, 2309);
   int ERROR_ALREADY_CONNECTED = PrinterException.formatError(2402, 2309);
   int ERROR_ALREADY_CLOSED = PrinterException.formatError(1312, 2309);
   int ERROR_INVALID_PARAMETER = PrinterException.formatError(87, 2309);
   int ERROR_SERVICE_NOT_AVAILABLE = PrinterException.formatError(1060, 2309);
   int ERROR_NOT_SUPPORTED = PrinterException.formatError(50, 2309);
   int ERROR_INVALID_CONTEXT = PrinterException.formatError(14080, 2309);
   int ERROR_INVALID_THREAD = PrinterException.formatError(14081, 2309);

   void init(Printer var1, String var2, String var3, String var4, Printer.ExtraSettings var5) throws PrinterException;

   void addPrintProgressListener(PrintProgressListener var1);

   void close() throws PrinterException;

   void connect() throws PrinterException;

   boolean disconnect() throws PrinterException;

   void endDoc() throws PrinterException;

   void flush() throws PrinterException;

   void formFeed() throws PrinterException;

   int getBytesWritten() throws PrinterException;

   int[] getStatus() throws PrinterException;

   void newLine(int var1) throws PrinterException;

   void removePrintProgressListener(PrintProgressListener var1);

   void sendCustomCommand(String var1) throws PrinterException;

   void setBold(boolean var1) throws PrinterException;

   void setCompress(boolean var1) throws PrinterException;

   void setDoubleHigh(boolean var1) throws PrinterException;

   void setDoubleWide(boolean var1) throws PrinterException;

   void setItalic(boolean var1) throws PrinterException;

   void setSettingBool(String var1, boolean var2) throws PrinterException;

   void setSettingBytes(String var1, byte[] var2) throws PrinterException;

   void setSettingNum(String var1, int var2) throws PrinterException;

   void setStrikeout(boolean var1) throws PrinterException;

   void setSettingString(String var1, String var2) throws PrinterException;

   void setUnderline(boolean var1) throws PrinterException;

   void startFileEcho(String var1, boolean var2) throws PrinterException;

   void stopFileEcho() throws PrinterException;

   void write(byte[] var1) throws PrinterException;

   void write(String var1) throws PrinterException;

   void writeBarcode(int var1, String var2, int var3, int var4) throws PrinterException;

   void writeGraphic(String var1, int var2, int var3, int var4, int var5) throws PrinterException;

   void writeGraphicBase64(String var1, int var2, int var3, int var4, int var5) throws PrinterException;

   void writeLabel(String var1, Hashtable var2) throws PrinterException;

   void writeLine(String var1) throws PrinterException;

   public static class FacilityCodes {
      static final int CROSS_PLATFORM_C_LIB = 2305;
      static final int ANDROID_C_LIB = 2306;
      static final int LINEPRINT_SERVICE_JAVA = 2307;
      static final int LINEPRINT_SERVICE_JNI = 2308;
      static final int PRINTER_JAVA = 2309;
   }
}
