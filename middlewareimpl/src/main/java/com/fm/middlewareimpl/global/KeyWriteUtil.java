package com.fm.middlewareimpl.global;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.fm.middlewareimpl.interf.BaseMiddleware;

public final class KeyWriteUtil {
    private static final int WRITE_SYSFS_BIN = IBinder.FIRST_CALL_TRANSACTION + 36;
    private static final String TOKEN = "droidlogic.ISystemControlService";
    private static IBinder mBinder = null;

    /**
     * System Control 未提供此接口，在此处实现
     *
     * @param keyName key name
     * @param values  key 字节数据
     * @param size    key len
     * @return success
     */
    public static boolean writeSysFSBin(String keyName, byte[] values, int size) {
        init();
        try {
            if (null != mBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();

                data.writeInterfaceToken(TOKEN);
                data.writeString(keyName);
                data.writeByteArray(values);
                //data.writeCharArray(getChars(values));
                data.writeInt(size);

                boolean res = mBinder.transact(WRITE_SYSFS_BIN, data, reply, 0);
                int result = reply.readInt();

                Log.d(BaseMiddleware.TAG, "transact :: " + res + result);

                data.recycle();
                reply.recycle();

                return result != 0;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void init() {
        if (mBinder == null) {
            mBinder = ServiceManager.getService("system_control");
            Log.d(BaseMiddleware.TAG, "system_control :: " + mBinder.toString());
        }
    }
}
