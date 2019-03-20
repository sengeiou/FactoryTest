package com.fm.fengmicomm.usb.task;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fm.fengmicomm.usb.USBContext;
import com.fm.fengmicomm.usb.command.CP210xCommand;
import com.fm.fengmicomm.usb.command.CommandRxWrapper;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import static com.fm.fengmicomm.usb.USBContext.TYPE_ACK;
import static com.fm.fengmicomm.usb.USBContext.TYPE_CTL;
import static com.fm.fengmicomm.usb.USBContext.TYPE_DATA;
import static com.fm.fengmicomm.usb.USBContext.TYPE_FUNC;
import static com.fm.fengmicomm.usb.USBContext.TYPE_N_ACK;
import static com.fm.fengmicomm.usb.USBContext.ackMap;
import static com.fm.fengmicomm.usb.USBContext.nackMap;
import static com.fm.fengmicomm.usb.USBContext.cp210xRxQueue;
import static com.fm.fengmicomm.usb.USBContext.cp210xTxQueue;

public class CP210xProtocolTask extends Thread {
    private static final String TAG = "UsbProtocolTask";
    private static final int RX_BUFFER = 1024;
    private static final int TX_BUFFER = 1024;
    private static volatile boolean running = false;
    private static volatile boolean stop = false;
    private CommandRxWrapper wrapper;

    @Override
    public void run() {
        if (!running) {
            throw new RuntimeException("running states is false,you should call taskInit()");
        }

        while (running && !stop) {

            while (cp210xRxQueue.size() > 0) {
                if (!running) {
                    break;
                }
                //1. 处理接收区数据
                CP210xCommand cmd = cp210xRxQueue.poll();
                if (cmd != null) {
                    Log.d(TAG, "UsbProtocolTask  received \n" + cmd.toString());
                    //判断指令类型
                    byte type = cmd.getCmdType();
                    switch (type) {
                        case TYPE_FUNC:
                        case TYPE_DATA:
                        case TYPE_CTL:
                            sendACK(cmd);
                            receivedData(cmd);
                            break;
                        case TYPE_ACK:
                            receivedACK(cmd, true);
                            break;
                        case TYPE_N_ACK:
                            receivedACK(cmd, false);
                            break;
                    }
                }
            }
        }

        running = false;
        Log.d(TAG, "Protocol Task ended");
    }

    /**
     * 收到数据指令后，优先回复 ACK
     *
     * @param cmd 收到的 cmd
     */
    private void sendACK(@NonNull CP210xCommand cmd) {
        CP210xCommand ack = CP210xCommand.generateACKCMD(true, cmd.getCommandID());
        cp210xTxQueue.add(ack);
    }

    /**
     * 数据接收，包括多帧数据的处理
     *
     * @param command 接收到的数据帧
     */
    private void receivedData(CP210xCommand command) {
        //如果是 null 或者 未处在接收状态
        if (wrapper == null || !wrapper.isReceiving()) {
            if (wrapper == null) {
                wrapper = new CommandRxWrapper();
            }
            wrapper.startReceiving();
            wrapper.setCmdID(command.getCommandID());
        }
        //判断 cmd ID 是否一致
        if (wrapper.getCmdID().equals(command.getCommandID())) {
            //如果是最后一帧，接收完毕
            if ((command.getCmdNum() + 1) == command.getCmdSum()) {
                wrapper.addCommand(command);
                wrapper.received();
            } else {
                //继续接收
                wrapper.addCommand(command);
            }
        } else {
            // cmd ID 不一致，清空集合，重新接收
            wrapper.clearCommands();
            wrapper.startReceiving();
            wrapper.setCmdID(command.getCommandID());

            //如果是最后一帧，或者只有一帧，接收完毕
            if ((command.getCmdNum() + 1) == command.getCmdSum()) {
                wrapper.addCommand(command);
                wrapper.received();
            } else {
                //继续接收
                wrapper.addCommand(command);
            }
        }
    }
    /**
     * 接收到 ACK 或者 N ACK
     *
     * @param ack   command
     * @param isAck true is ACK，false is N ACK
     */
    private void receivedACK(CP210xCommand ack, boolean isAck) {
        String cmdID = ack.getCommandID();
        if (isAck) {
            USBContext.ackMap.put(cmdID, ack);
        } else {
            USBContext.nackMap.put(cmdID, ack);
        }
    }


    /**
     * 任务初始化
     */
    public void taskInit() {
        if (USBContext.cp210xUsb == null) {
            throw new RuntimeException("USB Context , usb is null");
        }
        cp210xRxQueue = new ArrayBlockingQueue<>(RX_BUFFER);
        cp210xTxQueue = new ArrayBlockingQueue<>(TX_BUFFER);
        ackMap = new ConcurrentHashMap<>();
        nackMap = new ConcurrentHashMap<>();
        running = true;
        stop = false;
    }

    /**
     * 任务中止
     */
    public void killProtocol() {
        running = false;
        stop = true;
        if (cp210xRxQueue != null) {
            cp210xRxQueue.clear();
        }
        if (cp210xTxQueue != null) {
            cp210xTxQueue.clear();
        }
        cp210xTxQueue = null;
        cp210xRxQueue = null;

    }
}
