package com.fengmi.usertest.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.fm.fengmicomm.usb.USBContext;
import com.fm.fengmicomm.usb.callback.CL200RxDataCallBack;

import mitv.powermanagement.ScreenSaverManager;

public abstract class BaseActivity extends Activity implements CL200RxDataCallBack {
    protected static final String TAG = "UserTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ScreenSaverManager manager = ScreenSaverManager.getInstance();
        manager.setScreenSaverEnabled(false);
        USBContext.cl200RxDataCallBack = this;
    }

    @Override
    public void onDataReceived(boolean valid, String Ev, String x, String y) {
        onCL200Received(valid, Ev, x, y);
    }

    protected void onCL200Received(boolean valid, String Ev, String x, String y){

    }
}
