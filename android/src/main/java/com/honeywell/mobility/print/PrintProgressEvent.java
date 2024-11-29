package com.honeywell.mobility.print;

import java.util.EventObject;

public class PrintProgressEvent extends EventObject {
   static final long serialVersionUID = 1L;
   private int m_iMsgType = 0;

   public PrintProgressEvent(Printer source, int aMsgType) {
      super(source);
      this.m_iMsgType = aMsgType;
   }

   public int getMessageType() {
      return this.m_iMsgType;
   }

   public class MessageTypes {
      public static final int NONE = 0;
      public static final int STARTDOC = 1;
      public static final int ENDDOC = 4;
      public static final int CANCEL = 6;
      public static final int FINISHED = 7;
      public static final int COMPLETE = 8;

      private MessageTypes() {
      }
   }
}
