package com.fm.fengmicomm.usb.task;

import android.util.Log;

import com.fm.fengmicomm.usb.command.CP210xCommand;

import java.util.Arrays;

import static com.fm.fengmicomm.usb.USBContext.cp210xRxQueue;
import static com.fm.fengmicomm.usb.USBContext.cp210xTxQueue;
import static com.fm.fengmicomm.usb.USBContext.cp210xUsb;

public class CP210xCommTask extends Thread {
    private static final String TAG = "CP210x-COMM";
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
        if (cp210xUsb == null) {
            throw new RuntimeException("CP210x USB is null");
        }
        while (running && !stop) {
            if (cp210xUsb != null) {
                int aval = cp210xUsb.readData(tempBuffer, 1000);
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
                        if (recvLen < 9) {
                            System.arraycopy(recvBuffer, cmdStart, recvBuffer, 0, recvLen);
                            cmdStart = 0;
                            break;
                        } else {
                            if (recvBuffer[cmdStart] == 0x79) {
                                if (cmdStart + 4 < recvLen) {
                                    //计算数据帧长度
                                    int frameLen = recvBuffer[cmdStart + 4] + 9;
                                    int endPos = cmdStart + frameLen - 1;
                                    //数据帧长度完整，且是合法的数据帧，开始处理
                                    if (frameLen < recvLen) {
                                        if (recvBuffer[endPos] == (byte) 0xFE) {
                                            receivedMsg(recvBuffer, cmdStart, frameLen);
                                            recvLen -= frameLen;
                                            cmdStart += frameLen;
                                        } else {
                                            //异常数据，过滤
                                            recvLen -= frameLen;
                                            cmdStart += frameLen;
                                        }
                                    } else {
                                        //不完整，保留碎片
                                        System.arraycopy(recvBuffer, cmdStart, recvBuffer, 0, recvLen);
                                        cmdStart = 0;
                                        break;
                                    }
                                }

                            } else {
                                recvLen--;
                                cmdStart++;
                            }
                        }
                    }

                }

            } else {
                if (cp210xTxQueue != null) {
                    while (cp210xTxQueue.size() > 0) {
                        CP210xCommand cmd = cp210xTxQueue.poll();
                        if (cmd != null) {
                            cp210xUsb.writeData(cmd.toByteArray(), 1000);
                        }
                    }
                }
            }
        }
    }


    private void receivedMsg(byte[] origin, int start, int len) {
        byte[] cmd = new byte[len];
        System.arraycopy(origin, start, cmd, 0, len);
        byte crc = CP210xCommand.calCRC(cmd);
        if (crc == cmd[len - 2]) {
            CP210xCommand cpCommand = new CP210xCommand(cmd);
            cp210xRxQueue.add(cpCommand);
        } else {
            byte[] nack = CP210xCommand.generateACKCMD(false, cmd[2], cmd[3]);
            cp210xTxQueue.add(new CP210xCommand(nack));
        }
    }

    public void killComm() {
        running = false;
        stop = true;
    }
}
