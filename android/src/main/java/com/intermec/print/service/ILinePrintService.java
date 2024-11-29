package com.intermec.print.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ILinePrintService extends IInterface {
   String execute(String var1) throws RemoteException;

   void addLinePrintListener(ILinePrintListener var1) throws RemoteException;

   void removeLinePrintListener(ILinePrintListener var1) throws RemoteException;

   public abstract static class Stub extends Binder implements ILinePrintService {
      private static final String DESCRIPTOR = "com.intermec.print.service.ILinePrintService";
      static final int TRANSACTION_execute = 1;
      static final int TRANSACTION_addLinePrintListener = 2;
      static final int TRANSACTION_removeLinePrintListener = 3;

      public Stub() {
         this.attachInterface(this, "com.intermec.print.service.ILinePrintService");
      }

      public static ILinePrintService asInterface(IBinder obj) {
         if (obj == null) {
            return null;
         } else {
            IInterface iin = obj.queryLocalInterface("com.intermec.print.service.ILinePrintService");
            return (ILinePrintService)(iin != null && iin instanceof ILinePrintService ? (ILinePrintService)iin : new ILinePrintService.Stub.Proxy(obj));
         }
      }

      public IBinder asBinder() {
         return this;
      }

      public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
         ILinePrintListener _arg0;
         switch(code) {
         case 1:
            data.enforceInterface("com.intermec.print.service.ILinePrintService");
             String _arg1 = data.readString();
            String _result = this.execute(_arg1);
            // String _arg0 = data.readString();
            // String _result = this.execute(_arg0);
            reply.writeNoException();
            reply.writeString(_result);
            return true;
         case 2:
            data.enforceInterface("com.intermec.print.service.ILinePrintService");
            _arg0 = ILinePrintListener.Stub.asInterface(data.readStrongBinder());
            this.addLinePrintListener(_arg0);
            reply.writeNoException();
            return true;
         case 3:
            data.enforceInterface("com.intermec.print.service.ILinePrintService");
            _arg0 = ILinePrintListener.Stub.asInterface(data.readStrongBinder());
            this.removeLinePrintListener(_arg0);
            reply.writeNoException();
            return true;
         case 1598968902:
            reply.writeString("com.intermec.print.service.ILinePrintService");
            return true;
         default:
            return super.onTransact(code, data, reply, flags);
         }
      }

      private static class Proxy implements ILinePrintService {
         private IBinder mRemote;

         Proxy(IBinder remote) {
            this.mRemote = remote;
         }

         public IBinder asBinder() {
            return this.mRemote;
         }

         public String getInterfaceDescriptor() {
            return "com.intermec.print.service.ILinePrintService";
         }

         public String execute(String aInputStr) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();

            String _result;
            try {
               _data.writeInterfaceToken("com.intermec.print.service.ILinePrintService");
               _data.writeString(aInputStr);
               this.mRemote.transact(1, _data, _reply, 0);
               _reply.readException();
               _result = _reply.readString();
            } finally {
               _reply.recycle();
               _data.recycle();
            }

            return _result;
         }

         public void addLinePrintListener(ILinePrintListener aListener) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();

            try {
               _data.writeInterfaceToken("com.intermec.print.service.ILinePrintService");
               _data.writeStrongBinder(aListener != null ? aListener.asBinder() : null);
               this.mRemote.transact(2, _data, _reply, 0);
               _reply.readException();
            } finally {
               _reply.recycle();
               _data.recycle();
            }

         }

         public void removeLinePrintListener(ILinePrintListener aListener) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();

            try {
               _data.writeInterfaceToken("com.intermec.print.service.ILinePrintService");
               _data.writeStrongBinder(aListener != null ? aListener.asBinder() : null);
               this.mRemote.transact(3, _data, _reply, 0);
               _reply.readException();
            } finally {
               _reply.recycle();
               _data.recycle();
            }

         }
      }
   }
}
