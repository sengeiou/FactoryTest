package com.fm.factorytest.comm.base;

import android.annotation.Nullable;

public interface GlobalCommandReceiveListener {
    void onRXWrapperReceived(String cmdID,@Nullable byte[] data);
}
