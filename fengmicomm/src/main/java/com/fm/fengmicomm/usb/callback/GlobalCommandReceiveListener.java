package com.fm.fengmicomm.usb.callback;


import android.support.annotation.Nullable;

public interface GlobalCommandReceiveListener {
    void onRXWrapperReceived(String cmdID, @Nullable byte[] data);
}
