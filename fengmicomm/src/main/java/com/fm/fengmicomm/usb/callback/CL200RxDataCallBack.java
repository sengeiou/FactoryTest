package com.fm.fengmicomm.usb.callback;

public interface CL200RxDataCallBack {
    void onDataReceived(boolean valid, String Ev, String x, String y);
}
