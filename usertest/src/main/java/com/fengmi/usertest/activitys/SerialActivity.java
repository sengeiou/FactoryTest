package com.fengmi.usertest.activitys;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.fengmi.usertest.R;

import java.util.Arrays;

import me.zhouzhuo810.okusb.USB;

public class SerialActivity extends BaseActivity {
    private static final String TAG = "SerialActivity";

    private USB usb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial);

        usb = new USB.USBBuilder(this)
                .setBaudRate(115200)
                .setDataBits(8)
                .setParity(1)
                .setStopBits(0)
                .setMaxReadBytes(80)
                .setReadDuration(20)
                .build();

        usb.setOnUsbChangeListener(new USB.OnUsbChangeListener() {
            @Override
            public void onUsbConnect() {
                Log.d(TAG, "onUsbConnect");
            }

            @Override
            public void onUsbDisconnect() {
                Log.d(TAG, "onUsbDisconnect");
            }

            @Override
            public void onDataReceive(byte[] bytes) {
                Log.d(TAG, Arrays.toString(bytes));
            }

            @Override
            public void onUsbConnectFailed() {
                Log.d(TAG, "onUsbConnectFailed");
            }

            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "onPermissionGranted");
            }

            @Override
            public void onPermissionRefused() {
                Log.d(TAG, "onPermissionRefused");
            }

            @Override
            public void onDriverNotSupport() {
                Log.d(TAG, "onDriverNotSupport");
            }

            @Override
            public void onWriteDataFailed(String s) {
                Log.d(TAG, "onWriteDataFailed == " + s);
            }

            @Override
            public void onWriteSuccess(int i) {
                Log.d(TAG, "onWriteSuccess");
            }
        });

    }


    public void sendData(View view) {
        new Thread() {
            @Override
            public void run() {
                byte[] hb = new byte[9];
                hb[0] = 0x79;
                hb[1] = 0b01111111;
                hb[2] = (byte) 0x14;
                hb[3] = (byte) 0x11;
                hb[4] = 0;
                hb[5] = 0;
                hb[6] = 1;
                hb[7] = 0x34;
                hb[8] = (byte) 0xFE;
                usb.writeData(hb, 3000);
            }
        }.start();

    }
}
