package com.fm.fengmicomm.usb.task;

import android.util.Log;

import com.fm.fengmicomm.usb.USBContext;
import com.fm.fengmicomm.usb.command.CP210xCommand;
import com.fm.fengmicomm.usb.port.CommunicatePort;
import com.fm.fengmicomm.usb.port.USBCommunicatePort;

import java.io.IOException;
import java.util.Arrays;

public class UsbCommTask extends Thread {
    // private static final String TAG = "UsbCommTask";
    // //通讯线程运行状态
    // private static volatile boolean running = true;
    //
    // private static volatile boolean stop = false;
    // private CommunicatePort commPort;
    // //接收数据长度
    // private int recvLen = 0;
    // //读取字节数
    // private int recvBytes = 0;
    // //缓冲区长度
    // private int bufferLen = 80;
    // //缓冲区数组
    // private byte[] recvBuff = new byte[bufferLen];
    // //读取数据的起始位置
    // private int recvCMDStart = 0;
    // //帧头格式
    // private byte FRAME_HEAD = 0x79;
    // //帧尾格式
    // private byte FRAME_TAIL = (byte) 0xFE;
    // //最小数据帧长度
    // private int FRAME_LEN_MIN = 9;
    //
    // public void taskInit() {
    //     commPort = new USBCommunicatePort();
    //     running = true;
    //     stop = false;
    // }
    //
    // @Override
    // public void run() {
    //     try {
    //         if (commPort == null || !running || stop) {
    //             throw new RuntimeException("UsbCommTask init error comport = " + commPort
    //                     + "running = " + false + " stop = " + stop);
    //         }
    //         while (running && !stop) {
    //             int val = recvBytes = commPort.readData(recvBuff, 300);
    //
    //             if (val >= 9) {
    //                 Log.d(TAG, "dataAvailable : " + val + Arrays.toString(recvBuff));
    //
    //                 if (recvBuff[recvCMDStart] == FRAME_HEAD) {
    //                     int cmdLen;
    //                     //包含完整数据帧
    //                     if ((cmdLen = (FRAME_LEN_MIN + recvBuff[recvCMDStart + 4])) <= val) {
    //                         //CRC 校验 pass
    //                         byte crc = CP210xCommand.calCRC(recvBuff, recvCMDStart, cmdLen);
    //                         if (recvBuff[recvCMDStart + cmdLen - 2] == crc) {
    //                             Log.d(TAG, "Received new CP210xCommand data !!");
    //                             receivedCommand(recvBuff, recvCMDStart, cmdLen);
    //                         } else {
    //                             Log.d(TAG, "CRC is not correct !!");
    //                             Log.d(TAG, "received CRC is " + recvBuff[recvCMDStart + cmdLen - 2]);
    //                             Log.d(TAG, "calc CRC is " + crc);
    //                             byte[] nack = CP210xCommand.generateACKCMD(false, recvBuff[recvCMDStart + 2], recvBuff[recvCMDStart + 3]);
    //                             USBContext.commandTxQueue.add(new CP210xCommand(nack));
    //                         }
    //                         recvCMDStart = 0;
    //                         Arrays.fill(recvBuff, (byte) 0);
    //                     }
    //                 } else {
    //                     if (recvCMDStart < recvLen - 1) {
    //                         //坏数据帧，丢弃
    //                         recvCMDStart += 1;
    //                     }
    //                 }
    //
    //             } else {
    //                 Arrays.fill(recvBuff, (byte) 0);
    //                 if (USBContext.commandTxQueue != null) {
    //                     int size = USBContext.commandTxQueue.size();
    //                     //Log.d(TAG, "USBContext.commandTxQueue " + size);
    //                     while (size > 0) {
    //                         if (USBContext.commandTxQueue != null) {
    //                             CP210xCommand cmd = USBContext.commandTxQueue.poll();
    //                             size = USBContext.commandTxQueue.size();
    //                             if (cmd != null) {
    //                                 Log.d(TAG, "sending  data ");
    //                                 sendCommand(cmd);
    //                             }
    //                         } else {
    //                             running = false;
    //                             break;
    //                         }
    //                     }
    //                 } else {
    //                     running = false;
    //                     break;
    //                 }
    //             }
    //
    //         }
    //         running = false;
    //         //接收区清空
    //         recvLen = 0;
    //         recvCMDStart = 0;
    //         Arrays.fill(recvBuff, (byte) 0);
    //         Log.d(TAG, "Usb Comm Task Ended");
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         if (commPort != null) {
    //             commPort.closePort();
    //         }
    //     }
    // }
    //
    // /**
    //  * 数据发送
    //  *
    //  * @param cmd CP210xCommand 对象
    //  * @throws IOException IO Exception
    //  */
    // private void sendCommand(CP210xCommand cmd) throws IOException {
    //     int cmdLen = cmd.getDataLen() + 9;
    //     byte[] send = new byte[cmdLen];
    //
    //     send[0] = FRAME_HEAD;
    //     send[1] = cmd.getCmdType();
    //     send[2] = cmd.getCmdID_Left();
    //     send[3] = cmd.getCmdID_Right();
    //     send[4] = cmd.getDataLen();
    //
    //     if (cmd.getDataLen() > 0) {
    //         System.arraycopy(cmd.getData(), 0, send, 5, cmd.getDataLen());
    //     }
    //     send[cmdLen - 4] = cmd.getCmdNum();
    //     send[cmdLen - 3] = cmd.getCmdSum();
    //     send[cmdLen - 2] = CP210xCommand.calCRC(send);
    //     send[cmdLen - 1] = FRAME_TAIL;
    //
    //     Log.d(TAG, "send data ::: " + Arrays.toString(send));
    //
    //     commPort.writeData(send);
    // }
    //
    // /**
    //  * 数据帧接收
    //  *
    //  * @param recdata 数据集合
    //  * @param start   有效数据的起始位置
    //  * @param len     有效数据长度
    //  */
    // private void receivedCommand(byte[] recdata, int start, int len) {
    //     byte[] cmd = new byte[len];
    //     System.arraycopy(recdata, start, cmd, 0, len);
    //     CP210xCommand command = new CP210xCommand(cmd);
    //     Log.d(TAG, "receivedCommand" + command.toString());
    //     if (USBContext.commandRxQueue != null) {
    //         Log.d(TAG, "receivedCommand  .commandRxQueue " + USBContext.commandRxQueue);
    //         USBContext.commandRxQueue.add(command);
    //     } else {
    //         running = false;
    //     }
    // }
    //
    // /**
    //  * 结束通讯
    //  */
    // public void killComm() {
    //     //running = false;
    //     stop = true;
    //     // while (State.RUNNABLE != Thread.currentThread().getState()) {
    //     //     Log.d(TAG, "waiting for thread ended");
    //     // }
    // }
}
