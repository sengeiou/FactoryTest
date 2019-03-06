package com.fm.fengmicomm.usb.task;

import android.os.SystemClock;
import android.util.Log;

import java.util.Arrays;

import static com.fm.fengmicomm.usb.USBContext.cl200Usb;

public class CL200CommTask extends Thread {
    private static final String TAG = "CL200-COMM";
    private byte[] recvBuffer = new byte[128];
    private byte[] sendData = new byte[]{
            0x02,
            0x30,
            0x30,
            0x35,
            0x34,
            0x31,
            0x20,
            0x20,
            0x20,
            0x03,
            0x31,
            0x33,
            0x0D,
            0x0A
    };
    private int count = 1;

    @Override
    public void run() {
        if (cl200Usb == null) {
            throw new RuntimeException("CL200 USB is null");
        }
        while (true) {
            int aval = cl200Usb.readData(recvBuffer, 300);
            if (aval > 0) {
                Log.d(TAG, "recv data == " + Arrays.toString(recvBuffer));

                Arrays.fill(recvBuffer, (byte) 0);
            } else {
                if (count % 5 == 0) {
                    cl200Usb.writeData(sendData, 300);
                    SystemClock.sleep(500);
                }
                SystemClock.sleep(100);
                count++;
            }
        }

    }
}
