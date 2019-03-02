package com.fm.fengmicomm.usb.task;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fm.fengmicomm.usb.USBContext;
import com.fm.fengmicomm.usb.command.Command;
import com.fm.fengmicomm.usb.command.CommandRxWrapper;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import static com.fm.fengmicomm.usb.USBContext.TYPE_ACK;
import static com.fm.fengmicomm.usb.USBContext.TYPE_CTL;
import static com.fm.fengmicomm.usb.USBContext.TYPE_DATA;
import static com.fm.fengmicomm.usb.USBContext.TYPE_FUNC;
import static com.fm.fengmicomm.usb.USBContext.TYPE_N_ACK;
import static com.fm.fengmicomm.usb.USBContext.commandRxQueue;
import static com.fm.fengmicomm.usb.USBContext.commandTxQueue;

public class UsbProtocolTask extends Thread {
    private static final String TAG = "UsbProtocolTask";
    private static volatile boolean running = false;
    private static volatile boolean stop = false;
    private final int RX_BUFFER = 1024;
    private final int TX_BUFFER = 1024;
    private CommandRxWrapper wrapper;

    @Override
    public void run() {
        if (!running) {
            throw new RuntimeException("running states is false,you should call taskInit()");
        }

        while (running && !stop) {

            while (USBContext.commandRxQueue.size() > 0) {
                if (!running) {
                    break;
                }
                //1. 处理接收区数据
                Command cmd = USBContext.commandRxQueue.poll();
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
    private void sendACK(@NonNull Command cmd) {
        Command ack = Command.generateACKCMD(true, cmd.getCommandID());
        commandTxQueue.add(ack);
    }

    /**
     * 数据接收，包括多帧数据的处理
     *
     * @param command 接收到的数据帧
     */
    private void receivedData(Command command) {
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
    private void receivedACK(Command ack, boolean isAck) {
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
        if (USBContext.usb == null) {
            throw new RuntimeException("USB Context , usb is null");
        }
        commandRxQueue = new ArrayBlockingQueue<>(RX_BUFFER);
        commandTxQueue = new ArrayBlockingQueue<>(TX_BUFFER);

        USBContext.ackMap = new ConcurrentHashMap<>();
        USBContext.nackMap = new ConcurrentHashMap<>();

        running = true;
        stop = false;
    }

    /**
     * 任务中止
     */
    public void killProtocol() {
        running = false;
        stop = true;
        if (commandRxQueue != null) {
            commandRxQueue.clear();
        }
        if (commandTxQueue != null) {
            commandTxQueue.clear();
        }
        commandTxQueue = null;
        commandRxQueue = null;

        // while (State.TERMINATED != Thread.currentThread().getState()) {
        //     Log.d(TAG, "waiting for thread ended");
        // }
    }
}
