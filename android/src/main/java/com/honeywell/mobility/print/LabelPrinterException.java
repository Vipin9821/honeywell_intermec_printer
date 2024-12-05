package com.honeywell.mobility.print;

public class LabelPrinterException extends PrinterException {
   static final long serialVersionUID = 1L;

   public LabelPrinterException(String aMsg, int aErrorCode) {
      super(aMsg, aErrorCode);
   }
}
