package com.fm.fengmicomm.usb.command;


import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fm.fengmicomm.usb.USBContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;


/**
 * 命令发送包装类
 *
 * @author lijie
 * @create 2019-02-21 13:41
 **/
public class CommandTxWrapper {
    private static final String TAG = "CommandTxWrapper";
    public static final int DATA_FILE = 0;
    public static final int DATA_STRING = 1;
    public static final int DATA_BYTES = 2;
    private static CommandTxWrapper txWrapper;
    private byte cmd_left;
    private byte cmd_right;
    private LinkedList<CP210xCommand> cmdList;
    private byte cmdType;
    private String cmdID;

    private CommandTxWrapper(@NonNull String cmdID, String data, int dataType, int cmdType) {
        cmdList = new LinkedList<>();

        this.cmdID = cmdID.toUpperCase();
        cmd_left = (byte) Integer.parseInt(cmdID.substring(0, 2), 16);
        cmd_right = (byte) Integer.parseInt(cmdID.substring(2, 4), 16);
        this.cmdType = (byte) cmdType;

        if (data == null) {
            CP210xCommand cmd = CP210xCommand.generateCommandByID(cmdID);
            cmdList.add(cmd);
        } else {
            switch (dataType) {
                case DATA_FILE:
                    fileSplit(data);
                    break;
                case DATA_STRING:
                    stringSplit(data);
                    break;
            }
        }
    }

    private CommandTxWrapper(@NonNull String cmdID, byte[] data, int cmdType) {
        cmdList = new LinkedList<>();
        this.cmdID = cmdID.toUpperCase();
        cmd_left = (byte) Integer.parseInt(cmdID.substring(0, 2), 16);
        cmd_right = (byte) Integer.parseInt(cmdID.substring(2, 4), 16);
        this.cmdType = (byte) cmdType;
        if (data == null) {
            CP210xCommand cmd = CP210xCommand.generateCommandByID(cmdID);
            cmdList.add(cmd);
        } else {
            bytesSplit(data);
        }
    }

    /**
     * 初始化 Tx Wrapper
     *
     * @param cmdID    cmd id
     * @param data     data String 类型，可以是字符串数据，也可以是路径
     * @param datas    字节数组，可以为空，当不为空时，参数data无效
     * @param dataType 数据类型，文件、字节数组、字符串
     * @param cmdType  CP210xCommand 类型
     * @return Tx wrapper
     */
    public synchronized static CommandTxWrapper initTX(@NonNull String cmdID,
                                                       String data,
                                                       @Nullable byte[] datas,
                                                       int dataType, int cmdType) {
        if (txWrapper == null) {
            synchronized (CommandTxWrapper.class) {
                if (datas == null) {
                    txWrapper = new CommandTxWrapper(cmdID, data, dataType, cmdType);
                } else {
                    txWrapper = new CommandTxWrapper(cmdID, datas, cmdType);
                }
            }
        }else {
            txWrapper.cmdList.clear();
            txWrapper.cmdID = cmdID.toUpperCase();
            txWrapper.cmd_left = (byte) Integer.parseInt(cmdID.substring(0, 2), 16);
            txWrapper.cmd_right = (byte) Integer.parseInt(cmdID.substring(2, 4), 16);
            txWrapper.cmdType = (byte) cmdType;
            if (datas == null) {
                //处理字符串 data 数据
                if (data == null){
                    CP210xCommand cmd = CP210xCommand.generateCommandByID(cmdID);
                    txWrapper.cmdList.add(cmd);
                }else {
                    switch (dataType) {
                        case DATA_FILE:
                            txWrapper.fileSplit(data);
                            break;
                        case DATA_STRING:
                            txWrapper.stringSplit(data);
                            break;
                    }
                }
            } else {
                //处理字节数据
                txWrapper.bytesSplit(datas);
            }
        }

        return txWrapper;
    }

    public void send() {
        new Thread() {
            @Override
            public void run() {
                while (cmdList.size() > 0) {
                    CP210xCommand cmd = cmdList.poll();
                    if (cmd != null) {
                        Log.d(TAG, cmd.toString());
                        if (USBContext.cp210xTxQueue != null) {
                            USBContext.cp210xTxQueue.add(cmd);
                            SystemClock.sleep(300);
                            //重发机制
                            int retry = 0;
                            for (int i = 0; i < 3; i++) {
                                CP210xCommand ack = USBContext.ackMap.get(cmd.getCommandID());
                                if (ack != null) {
                                    USBContext.ackMap.remove(cmd.getCommandID());
                                    break;
                                } else {
                                    retry++;
                                    USBContext.cp210xTxQueue.add(cmd);
                                    SystemClock.sleep(1000);
                                }
                            }
                            //三次都没有收到回复，取消后续发送
                            if (retry == 3) {
                                Log.d(TAG, "we retry " + retry + " times,but we did not received ack back，do we cancel Tx");
                                cmdList.clear();
                                break;
                            }
                        }
                    }
                }
            }
        }.start();

    }


    private void stringSplit(String data) {
        byte[] datas = data.getBytes();
        bytesSplit(datas);
    }

    /**
     * 文件拆分为数据帧
     *
     * @param path 文件路径
     */
    private void fileSplit(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                InputStream is = new FileInputStream(file);
                int avail = is.available();
                byte[] fileData = new byte[avail];
                is.read(fileData);
                bytesSplit(fileData);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 字节数据拆分，满足一帧64字节数据
     *
     * @param datas 完整的字节数据
     */
    private void bytesSplit(byte[] datas) {
        if (datas.length > 64) {
            int sum = datas.length / 64;
            int suffix = datas.length % 64;
            if (suffix > 0) {
                sum += 1;
            }
            byte[] data;
            CP210xCommand cmd;
            for (int i = 0; i < sum; i++) {
                if (i < (sum - 1)) {
                    data = new byte[64];
                    System.arraycopy(datas, i * 64, data, 0, 64);
                } else {
                    data = new byte[suffix];
                    System.arraycopy(datas, (i - 1) * 64, data, 0, suffix);
                }
                cmd = CP210xCommand.generateCommandBySource(data, (byte) i, (byte) sum, cmd_left, cmd_right);
                cmdList.add(cmd);
            }

        } else {
            CP210xCommand cmd = CP210xCommand.generateCommandBySource(datas, (byte) 0, (byte) 1, cmd_left, cmd_right);
            cmdList.add(cmd);
        }
    }
}
