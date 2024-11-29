package com.honeywell.mobility.print;

import java.util.EventListener;

public interface PrintProgressListener extends EventListener {
   void receivedStatus(PrintProgressEvent var1);
}
