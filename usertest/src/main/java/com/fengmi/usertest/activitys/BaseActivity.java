package com.fengmi.usertest.activitys;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.fm.fengmicomm.usb.USB;
import com.fm.fengmicomm.usb.task.CL200CommTask;
import com.fm.fengmicomm.usb.task.CL200ProtocolTask;

import static com.fm.fengmicomm.usb.USBContext.cl200CommTask;
import static com.fm.fengmicomm.usb.USBContext.cl200ProtocolTask;
import static com.fm.fengmicomm.usb.USBContext.cl200Usb;

public class BaseActivity extends Activity {
    protected static final String TAG = "UserTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
