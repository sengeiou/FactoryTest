package com.fm.factorytest.comm.server;

import android.support.annotation.NonNull;

import com.fm.factorytest.comm.base.CommunicatePort;
import com.fm.factorytest.comm.bean.Command;
import com.fm.factorytest.comm.factory.CommandFactory;
import com.fm.factorytest.comm.factory.IOFactory;

import java.io.IOException;
import java.util.Arrays;

import static com.fm.factorytest.comm.factory.CommandFactory.ACK_TYPE;
import static com.fm.factorytest.comm.factory.CommandFactory.CMD_CONTROL;
import static com.fm.factorytest.comm.factory.CommandFactory.CMD_DATA;
import static com.fm.factorytest.comm.factory.CommandFactory.CMD_FUNC;
import static com.fm.factorytest.comm.factory.CommandFactory.HEART_BEAT;
import static com.fm.factorytest.comm.factory.CommandFactory.generateACKCMD;
import static com.fm.factorytest.comm.server.CommandServer.ackList;
import static com.fm.factorytest.comm.server.CommandServer.notifyDataReceived;
import static com.fm.factorytest.comm.server.CommandServer.sendList;


/**
 * 通讯引擎
 *
 * @author lijie
 * @create 2018-10-30 18:23
 **/
public class CommunicateEngine extends Thread {
    private CommunicatePort commPort;
    //通讯线程运行状态
    private static volatile boolean running = true;
    //接收数据长度
    private int recvLen = 0;
    //读取字节数
    private int recvBytes = 0;
    //缓冲区长度
    private int bufferLen = 1024;
    //缓冲区数组
    private byte[] recvBuff = new byte[bufferLen];
    //读取数据的起始位置
    private int recvCMDStart = 0;
    //帧头格式
    private byte FRAME_HEAD = 0x79;
    //帧尾格式
    private byte FRAME_TAIL = (byte) 0xFE;
    //最小数据帧长度
    private int FRAME_LEN_MIN = 9;

    public CommunicateEngine(@NonNull CommunicatePort port) {
        commPort = port;
    }

    @Override
    public void run() {
        try {

            while (running) {

                if (commPort.dataAvailable() > 0) {
                    //根据当前缓冲区剩余数据累加读取
                    recvBytes = commPort.readData(recvBuff, recvLen, bufferLen - recvLen);
                    recvLen += recvBytes;

                    while (true) {
                        // 如果长度大于空数据帧长度,开始解析
                        if (recvLen >= FRAME_LEN_MIN) {
                            int cmdLen;

                            if (recvBuff[recvCMDStart] == FRAME_HEAD) {
                                //包含完整数据帧
                                if ((cmdLen = (FRAME_LEN_MIN + recvBuff[recvCMDStart + 4])) <= recvLen) {
                                    //CRC 校验 pass
                                    if (recvBuff[recvCMDStart + cmdLen - 2] == CommandFactory.calCRC(recvBuff, recvCMDStart, cmdLen)) {
                                        System.out.println("Received new Command data !!");

                                        receivedCommand(recvBuff, recvCMDStart, cmdLen);

                                        recvCMDStart += cmdLen;
                                        recvLen -= cmdLen;
                                    } else {
                                        System.out.println("CRC is not correct !!");
                                        System.out.println("received CRC is " + recvBuff[recvCMDStart + cmdLen - 2]);
                                        System.out.println("calc CRC is " + CommandFactory.calCRC(recvBuff, recvCMDStart, cmdLen));
                                        System.out.println(Arrays.toString(recvBuff));
                                        byte[] nack = generateACKCMD(false, recvBuff[recvCMDStart + 2], recvBuff[recvCMDStart + 3]);
                                        sendList.add(new Command(nack));
                                        break;
                                    }
                                }

                                System.out.println("remove received data !!");
                                if (recvCMDStart > 0) {
                                    System.arraycopy(recvBuff, recvCMDStart, recvBuff, 0, recvLen);
                                    recvCMDStart = 0;
                                    break;
                                }

                            } else {
                                //坏数据帧，丢弃
                                recvCMDStart += 1;
                                recvLen -= 1;
                            }

                        } else {
                            //保存碎片数据帧
                            System.arraycopy(recvBuff, recvCMDStart, recvBuff, 0, recvLen);
                            break;
                        }
                    }

                } else {
                    //处理发射缓冲区
                    while (sendList.size() > 0) {
                        sendCommand(sendList.pop());
                    }

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            IOFactory.initPort().closePort();
        }
    }

    /**
     * 数据发送
     *
     * @param cmd Command 对象
     * @throws IOException IO Exception
     */
    private void sendCommand(Command cmd) throws IOException {
        int cmdLen = cmd.getDataLen() + 9;
        byte[] send = new byte[cmdLen];

        send[0] = FRAME_HEAD;
        send[1] = cmd.getCmdType();
        send[2] = cmd.getCmdID_Left();
        send[3] = cmd.getCmdID_Right();
        send[4] = cmd.getDataLen();

        if (cmd.getDataLen() > 0) {
            System.arraycopy(cmd.getData(), 0, send, 5, cmd.getDataLen());
        }
        send[cmdLen - 4] = cmd.getCmdNum();
        send[cmdLen - 3] = cmd.getCmdSum();
        send[cmdLen - 2] = CommandFactory.calCRC(send);
        send[cmdLen - 1] = FRAME_TAIL;

        System.out.println("send data ::: " + Arrays.toString(send));

        commPort.writeData(send);
    }

    /**
     * 数据帧接收
     *
     * @param recdata 数据集合
     * @param start   有效数据的起始位置
     * @param len     有效数据长度
     */
    private void receivedCommand(byte[] recdata, int start, int len) {
        byte[] cmd = new byte[len];
        System.arraycopy(recdata, start, cmd, 0, len);

        switch (cmd[1]) {
            case HEART_BEAT:
                //重置守护线程
                break;
            case CMD_CONTROL:
            case CMD_DATA:
            case CMD_FUNC:
                System.out.println("we received Data Command,so we send ACK back");
                Command ack = new Command(cmd[2], cmd[3], ACK_TYPE, (byte) 0, null, (byte) 0, (byte) 1);
                sendList.add(ack);
                //通知 Server
                notifyDataReceived(cmd);
                break;
            case ACK_TYPE:
                Command c = new Command(cmd);
                ackList.put(c.getCommandID().toUpperCase(), c);
                System.out.println("we received ACK Command");
                break;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cmd.length; i++) {
            sb.append(Integer.toHexString(cmd[i]).toUpperCase()).append(",");
        }
        System.out.println(sb.toString());

    }

    public void closePort() {
        IOFactory.initPort().closePort();
        running = false;
    }
}