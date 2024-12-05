package com.intermec.print.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ILinePrintListener extends IInterface {
   void handleEvent(String var1) throws RemoteException;

   public abstract static class Stub extends Binder implements ILinePrintListener {
      private static final String DESCRIPTOR = "com.intermec.print.service.ILinePrintListener";
      static final int TRANSACTION_handleEvent = 1;

      public Stub() {
         this.attachInterface(this, "com.intermec.print.service.ILinePrintListener");
      }

      public static ILinePrintListener asInterface(IBinder obj) {
         if (obj == null) {
            return null;
         } else {
            IInterface iin = obj.queryLocalInterface("com.intermec.print.service.ILinePrintListener");
            return (ILinePrintListener)(iin != null && iin instanceof ILinePrintListener ? (ILinePrintListener)iin : new ILinePrintListener.Stub.Proxy(obj));
         }
      }

      public IBinder asBinder() {
         return this;
      }

      public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
         switch(code) {
         case 1:
            data.enforceInterface("com.intermec.print.service.ILinePrintListener");
            String _arg0 = data.readString();
            this.handleEvent(_arg0);
            reply.writeNoException();
            return true;
         case 1598968902:
            reply.writeString("com.intermec.print.service.ILinePrintListener");
            return true;
         default:
            return super.onTransact(code, data, reply, flags);
         }
      }

      private static class Proxy implements ILinePrintListener {
         private IBinder mRemote;

         Proxy(IBinder remote) {
            this.mRemote = remote;
         }

         public IBinder asBinder() {
            return this.mRemote;
         }

         public String getInterfaceDescriptor() {
            return "com.intermec.print.service.ILinePrintListener";
         }

         public void handleEvent(String aJSONStr) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();

            try {
               _data.writeInterfaceToken("com.intermec.print.service.ILinePrintListener");
               _data.writeString(aJSONStr);
               this.mRemote.transact(1, _data, _reply, 0);
               _reply.readException();
            } finally {
               _reply.recycle();
               _data.recycle();
            }

         }
      }
   }
}
