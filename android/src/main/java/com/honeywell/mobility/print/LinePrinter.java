package com.honeywell.mobility.print;

public class LinePrinter extends Printer {
   public LinePrinter(String aCmdAttribFilePath, String aPrinterID, String aPrinterURI, LinePrinter.ExtraSettings extraSettings) throws LinePrinterException {
      try {
         super.newPrinter(aCmdAttribFilePath, aPrinterID, aPrinterURI, extraSettings);
      } catch (PrinterException var6) {
         throw new LinePrinterException(var6.getMessage(), var6.getErrorCode());
      }
   }

   public void newLine(int numLines) throws LinePrinterException {
      try {
         this.m_PrinterProxy.newLine(numLines);
      } catch (PrinterException var3) {
         throw this.newLinePrinterException(var3);
      }
   }

   public void setBold(boolean enabled) throws LinePrinterException {
      try {
         this.m_PrinterProxy.setBold(enabled);
      } catch (PrinterException var3) {
         throw this.newLinePrinterException(var3);
      }
   }

   public void setCompress(boolean enabled) throws LinePrinterException {
      try {
         this.m_PrinterProxy.setCompress(enabled);
      } catch (PrinterException var3) {
         throw this.newLinePrinterException(var3);
      }
   }

   public void setDoubleHigh(boolean enabled) throws LinePrinterException {
      try {
         this.m_PrinterProxy.setDoubleHigh(enabled);
      } catch (PrinterException var3) {
         throw this.newLinePrinterException(var3);
      }
   }

   public void setDoubleWide(boolean enabled) throws LinePrinterException {
      try {
         this.m_PrinterProxy.setDoubleWide(enabled);
      } catch (PrinterException var3) {
         throw this.newLinePrinterException(var3);
      }
   }

   public void setItalic(boolean enabled) throws LinePrinterException {
      try {
         this.m_PrinterProxy.setItalic(enabled);
      } catch (PrinterException var3) {
         throw this.newLinePrinterException(var3);
      }
   }

   public void setStrikeout(boolean enabled) throws LinePrinterException {
      try {
         this.m_PrinterProxy.setStrikeout(enabled);
      } catch (PrinterException var3) {
         throw this.newLinePrinterException(var3);
      }
   }

   public void setUnderline(boolean enabled) throws LinePrinterException {
      try {
         this.m_PrinterProxy.setUnderline(enabled);
      } catch (PrinterException var3) {
         throw this.newLinePrinterException(var3);
      }
   }

   public void writeBarcode(int aSymbology, String aData, int aSize, int aXOffset) throws LinePrinterException {
      if (aSymbology <= 0) {
         throw new LinePrinterException("The specified symbology is invalid.", IPrinterProxy.ERROR_INVALID_PARAMETER);
      } else if (null != aData && aData.length() != 0) {
         try {
            this.m_PrinterProxy.writeBarcode(aSymbology, aData, aSize, aXOffset);
         } catch (PrinterException var6) {
            throw this.newLinePrinterException(var6);
         }
      } else {
         throw new LinePrinterException("Bar code data cannot be null or empty.", IPrinterProxy.ERROR_INVALID_PARAMETER);
      }
   }

   public void writeGraphic(String aGraphicFilePath, int aRotation, int aXOffset, int aWidth, int aHeight) throws LinePrinterException {
      if (null != aGraphicFilePath && aGraphicFilePath.length() != 0) {
         try {
            this.m_PrinterProxy.writeGraphic(aGraphicFilePath, aRotation, aXOffset, aWidth, aHeight);
         } catch (PrinterException var7) {
            throw this.newLinePrinterException(var7);
         }
      } else {
         throw new LinePrinterException("Graphic file path cannot be null or empty.", IPrinterProxy.ERROR_INVALID_PARAMETER);
      }
   }

   public void writeGraphicBase64(String aBase64Image, int aRotation, int aXOffset, int aWidth, int aHeight) throws LinePrinterException {
      if (null != aBase64Image && aBase64Image.length() != 0) {
         try {
            this.m_PrinterProxy.writeGraphicBase64(aBase64Image, aRotation, aXOffset, aWidth, aHeight);
         } catch (PrinterException var7) {
            throw this.newLinePrinterException(var7);
         }
      } else {
         throw new LinePrinterException("Image data cannot be null or empty.", IPrinterProxy.ERROR_INVALID_PARAMETER);
      }
   }

   public void writeLine(String aStr) throws LinePrinterException {
      try {
         if (aStr != null) {
            this.m_PrinterProxy.writeLine(aStr);
         } else {
            this.m_PrinterProxy.newLine(1);
         }

      } catch (PrinterException var3) {
         throw this.newLinePrinterException(var3);
      }
   }

   protected PrinterException newPrinterException(String aMsg, int aErrorCode) {
      return new LinePrinterException(aMsg, aErrorCode);
   }

   protected PrinterException newPrinterException(PrinterException aPrinterException) {
      return new LinePrinterException(aPrinterException.getMessage(), aPrinterException.getErrorCode());
   }

   private LinePrinterException newLinePrinterException(PrinterException aPrinterException) {
      return new LinePrinterException(aPrinterException.getMessage(), aPrinterException.getErrorCode());
   }

   public class BarcodeSymbologies {
      public static final int SYMBOLOGY_CODE39 = 1;
      public static final int SYMBOLOGY_CODE128 = 2;

      private BarcodeSymbologies() {
      }
   }

   public class GraphicRotationDegrees {
      public static final int DEGREE_0 = 0;
      public static final int DEGREE_90 = 90;
      public static final int DEGREE_180 = 180;
      public static final int DEGREE_270 = 270;

      private GraphicRotationDegrees() {
      }
   }

   public static class ExtraSettings extends Printer.ExtraSettings {
   }
}
