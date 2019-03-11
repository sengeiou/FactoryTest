package com.fm.fengmicomm.usb.task;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.fm.fengmicomm.usb.command.CL200Command;

import java.util.concurrent.ArrayBlockingQueue;

import static com.fm.fengmicomm.usb.USBContext.cl200RxDataCallBack;
import static com.fm.fengmicomm.usb.USBContext.cl200RxQueue;
import static com.fm.fengmicomm.usb.USBContext.cl200TxQueue;

public class CL200ProtocolTask extends Thread {
    private static final String TAG = "CL200-COMM";
    private static volatile boolean running = false;
    private static volatile boolean stop = true;
    private CL200Command pc = new CL200Command("00", "54", "1   ");
    private CL200Command hold = new CL200Command("99", "55", "1  0");
    private CL200Command ext = new CL200Command("00", "40", "10  ");
    private CL200Command measure = new CL200Command("99", "40", "21  ");
    private CL200Command read = new CL200Command("00", "02", "1200");

    private int count = 0;

    public void initTask() {
        if (cl200RxQueue == null) {
            cl200RxQueue = new ArrayBlockingQueue<>(1024);
        }
        if (cl200TxQueue == null) {
            cl200TxQueue = new ArrayBlockingQueue<>(1024);
        }

        cl200TxQueue.clear();
        cl200RxQueue.clear();

        running = true;
        stop = false;
    }

    @Override
    public void run() {
        cl200TxQueue.add(pc);
        SystemClock.sleep(1000);
        cl200TxQueue.add(hold);
        SystemClock.sleep(1000);
        cl200TxQueue.add(ext);
        SystemClock.sleep(1000);

        while (running && !stop) {
            if (cl200RxQueue.size() > 0) {
                CL200Command cmd = cl200RxQueue.poll();
                if (cmd != null) {
                    String datas = cmd.getData();
                    Log.d(TAG, "received CL200 Data :: " + datas);
                    if (datas.length() == 18) {
                        char sign1 = datas.charAt(0);
                        char sign2 = datas.charAt(6);
                        char sign3 = datas.charAt(12);
                        char dec1 = datas.charAt(5);
                        char dec2 = datas.charAt(11);
                        char dec3 = datas.charAt(17);
                        String val1 = datas.substring(1, 5).trim();
                        String val2 = datas.substring(7, 11).trim();
                        String val3 = datas.substring(13, 17).trim();

                        if (cl200RxDataCallBack != null) {
                            cl200RxDataCallBack.onDataReceived(
                                    cmd.isValid(),
                                    dataTrans(sign1, dec1, val1),
                                    dataTrans(sign2, dec2, val2),
                                    dataTrans(sign3, dec3, val3)
                            );
                        }
                    }

                }
            } else {
                cl200TxQueue.add(measure);
                SystemClock.sleep(500);

                cl200TxQueue.add(read);
                SystemClock.sleep(500);
            }
        }
    }

    private String dataTrans(char sign, char dec, String val) {
        String res = "------";
        float f = 0;
        switch (dec) {
            case '0':
                f = 0.0001f;
                break;
            case '1':
                f = 0.001f;
                break;
            case '2':
                f = 0.01f;
                break;
            case '3':
                f = 0.1f;
                break;
            case '4':
                f = 1f;
                break;
            case '5':
                f = 10f;
                break;
            case '6':
                f = 100;
                break;
            case '7':
                f = 1000;
                break;
            case '8':
                f = 10000;
                break;
            case '9':
                f = 100000;
                break;
        }
        if (TextUtils.isDigitsOnly(val)) {
            float v = Integer.parseInt(val) * f;
            res = ("=".equals(sign) ? "±" : sign) + Float.toString(v);
        }
        return res;

    }

    public void killProtocol() {
        running = false;
        stop = true;
    }
}
