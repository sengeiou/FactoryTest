package com.fm.fengmicomm.usb.task;

import android.util.Log;

import com.fm.fengmicomm.usb.USBContext;
import com.fm.fengmicomm.usb.command.CL200Command;

import java.util.Arrays;

import static com.fm.fengmicomm.usb.USBContext.cl200TxQueue;
import static com.fm.fengmicomm.usb.USBContext.cl200Usb;

public class CL200CommTask extends Thread {
    private static final String TAG = "CL200-COMM";
    private static volatile boolean running = false;
    private static volatile boolean stop = false;
    private byte[] recvBuffer = new byte[1024];
    private byte[] tempBuffer = new byte[256];
    private int recvLen;

    private int cmdStart = 0;

    public void initTask() {
        running = true;
        stop = false;
    }

    @Override
    public void run() {
        if (cl200Usb == null) {
            throw new RuntimeException("CL200 USB is null");
        }
        while (running && !stop) {
            if (cl200Usb != null) {
                int aval = cl200Usb.readData(tempBuffer, 1000);
                if (aval > 0) {
                    Log.d(TAG, aval + " <== len temp recv data ==> " + Arrays.toString(tempBuffer));
                    byte[] realBytes = new byte[aval];
                    System.arraycopy(tempBuffer, 0, realBytes, 0, aval);
                    Arrays.fill(tempBuffer, (byte) 0);

                    System.arraycopy(realBytes, 0, recvBuffer, recvLen, aval);
                    recvLen += aval;
                    Log.d(TAG, "received Len = " + recvLen);
                    Log.d(TAG, "received recvBuffer = " + Arrays.toString(recvBuffer));

                    while (true) {
                        if (recvLen < 14) {
                            System.arraycopy(recvBuffer, cmdStart, recvBuffer, 0, recvLen);
                            cmdStart = 0;
                            break;
                        } else {
                            if (recvBuffer[cmdStart] == 0x02) {
                                if ((cmdStart + 13) < recvLen &&
                                        recvBuffer[cmdStart + 12] == 0x0D &&
                                        recvBuffer[cmdStart + 13] == 0x0A) {
                                    receivedMsg(recvBuffer, cmdStart, 14);
                                    recvLen -= 14;
                                    cmdStart += 14;

                                } else if ((cmdStart + 31) < recvLen &&
                                        recvBuffer[cmdStart + 30] == 0x0D &&
                                        recvBuffer[cmdStart + 31] == 0x0A) {
                                    receivedMsg(recvBuffer, cmdStart, 32);
                                    recvLen -= 32;
                                    cmdStart += 32;
                                } else {
                                    System.arraycopy(recvBuffer, cmdStart, recvBuffer, 0, recvLen);
                                    break;
                                }
                            } else {
                                recvLen--;
                                cmdStart++;
                            }
                        }
                    }

                } else {
                    if (cl200TxQueue != null) {
                        while (cl200TxQueue.size() > 0) {
                            CL200Command cmd = cl200TxQueue.poll();
                            if (cmd != null) {
                                cl200Usb.writeData(cmd.toByteArray(), 1000);
                            }
                        }
                    }
                }
            }
        }

    }

    private void receivedMsg(byte[] origin, int start, int len) {
        byte[] data = new byte[len];
        System.arraycopy(origin, start, data, 0, len);

        //Log.d(TAG, len + "<== len received str = " + new String(data));
        Log.d(TAG, len + "<== len received bytes = " + Arrays.toString(data));
        Log.d(TAG, len + "<== len received str = " + new String(data));
        CL200Command cl200 = CL200Command.formatReceivedCommand(data);
        if (cl200 != null) {
            USBContext.cl200RxQueue.add(cl200);
            Log.d(TAG, cl200.toString());
        } else {
            Log.d(TAG, "format failed");
        }
    }

    public void killComm() {
        running = false;
        stop = true;
    }
}
