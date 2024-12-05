package com.honeywell.mobility.print;

public class PrinterException extends Exception {
   static final long serialVersionUID = 1L;
   protected int m_iErrorCode = 0;
   public static final int ERROR_NO_CONNECTION;
   public static final int ERROR_ALREADY_CONNECTED;
   public static final int ERROR_FILE_NOT_FOUND = -1996423166;
   public static final int ERROR_INVALID_PRINTER_ID = -1996421367;
   public static final int ERROR_INVALID_NET_ADDRESS = -1996421954;
   public static final int ERROR_FILE_INVALID = -1996422162;

   public PrinterException(String aMsg, int aErrorCode) {
      super(aMsg);
      this.m_iErrorCode = aErrorCode;
   }

   public int getErrorCode() {
      return this.m_iErrorCode;
   }

   protected static int formatError(int aErrorCode, int aFacilityCode) {
      int retCode = 0;
      if (aErrorCode != 0 && (aErrorCode & -65536) == 0) {
         retCode = Integer.MIN_VALUE | aFacilityCode << 16 | aErrorCode;
      }

      return retCode;
   }

   static {
      ERROR_NO_CONNECTION = IPrinterProxy.ERROR_NO_CONNECTION;
      ERROR_ALREADY_CONNECTED = IPrinterProxy.ERROR_ALREADY_CONNECTED;
   }
}
