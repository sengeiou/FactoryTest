package com.fengmi.usertest.activitys;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.fengmi.usertest.R;
import com.fengmi.usertest.Util;

import java.io.File;
import java.lang.ref.WeakReference;

import lee.hua.xmlparse.api.XMLAPI;

public class InfoWriteActivity extends Activity {
    private static final String TAG = "InfoWriteActivity";
    private static final int USB_DETECT = 0;
    private static String usbFilePath = "unknow";
    private InfoHandler infoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_write);
        infoHandler = new InfoHandler(this);
        usbRegister();
    }

    private boolean checkConfigExist() {
        boolean exist;
        if ("unknow".equals(usbFilePath)) {
            exist = false;
        } else {
            File file = new File(usbFilePath + "/config/246-factory.xml");
            if (!file.exists()) {
                exist = false;
            } else {
                exist = true;
            }
        }

        return exist;
    }

    private void readConfig(){
        if (checkConfigExist()){
            Util.readConfig(usbFilePath);
        }
    }

    private void usbRegister() {
        UsbStatesReceiver usbStatesReceiver = new UsbStatesReceiver();

        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addDataScheme("file");

        registerReceiver(usbStatesReceiver, filter);
    }

    private static class InfoHandler extends Handler {
        WeakReference<InfoWriteActivity> weakReference;

        InfoHandler(InfoWriteActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            InfoWriteActivity info;
            info = weakReference.get();
            if (info != null) {
                switch (msg.what) {
                    case USB_DETECT:

                        break;

                }
            }
        }
    }

    class UsbStatesReceiver extends BroadcastReceiver {
        private static final String TAG = "UsbStatesReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                Uri data = intent.getData();
                if (data != null) {
                    usbFilePath = data.getPath();
                    Log.d(TAG, "usbFilePath :: " + usbFilePath);
                    infoHandler.sendEmptyMessage(USB_DETECT);
                }
            }
        }
    }
}
