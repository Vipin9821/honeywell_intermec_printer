package com.honeywell.mobility.print;

public class LinePrinterException extends PrinterException {
   static final long serialVersionUID = 1L;

   public LinePrinterException(String aMsg, int aErrorCode) {
      super(aMsg, aErrorCode);
   }
}
