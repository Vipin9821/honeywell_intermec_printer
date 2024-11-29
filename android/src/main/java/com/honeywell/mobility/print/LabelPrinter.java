package com.honeywell.mobility.print;

import java.util.Hashtable;
import java.util.Set;

public class LabelPrinter extends Printer {
   public LabelPrinter(String aCmdAttribFilePath, String aPrinterID, String aPrinterURI, LabelPrinter.ExtraSettings extraSettings) throws LabelPrinterException {
      try {
         super.newPrinter(aCmdAttribFilePath, aPrinterID, aPrinterURI, extraSettings);
      } catch (PrinterException var6) {
         throw new LabelPrinterException(var6.getMessage(), var6.getErrorCode());
      }
   }

   public void writeLabel(String aFormat, LabelPrinter.VarDictionary aDictionary) throws LabelPrinterException {
      try {
         this.m_PrinterProxy.writeLabel(aFormat, aDictionary.m_Dictionary);
      } catch (PrinterException var4) {
         throw this.newLabelPrinterException(var4);
      }
   }

   protected PrinterException newPrinterException(String aMsg, int aErrorCode) {
      return new LabelPrinterException(aMsg, aErrorCode);
   }

   protected PrinterException newPrinterException(PrinterException aPrinterException) {
      return new LabelPrinterException(aPrinterException.getMessage(), aPrinterException.getErrorCode());
   }

   private LabelPrinterException newLabelPrinterException(PrinterException aPrinterException) {
      return new LabelPrinterException(aPrinterException.getMessage(), aPrinterException.getErrorCode());
   }

   public static class VarDictionary {
      private Hashtable m_Dictionary = null;

      public VarDictionary() {
         this.m_Dictionary = new Hashtable();
      }

      public void clear() {
         this.m_Dictionary.clear();
      }

      public boolean containsVarName(String varName) {
         return this.m_Dictionary.containsKey(varName);
      }

      public boolean containsVarValue(String varValue) {
         return this.m_Dictionary.containsValue(varValue);
      }

      public Set entrySet() {
         return this.m_Dictionary.entrySet();
      }

      public String get(String aVarName) {
         return (String)this.m_Dictionary.get(aVarName);
      }

      public void put(String aVarName, String aVarValue) throws LabelPrinterException {
         if (null != aVarName && aVarName.length() != 0) {
            if (null == aVarValue) {
               throw new LabelPrinterException("Dictionary entry var value is null.", IPrinterProxy.ERROR_INVALID_PARAMETER);
            } else {
               this.m_Dictionary.put(aVarName, aVarValue);
            }
         } else {
            throw new LabelPrinterException("Dictionary entry var name is null or empty.", IPrinterProxy.ERROR_INVALID_PARAMETER);
         }
      }

      public void remove(String aVarName) {
         this.m_Dictionary.remove(aVarName);
      }
   }

   public static class ExtraSettings extends Printer.ExtraSettings {
   }
}
