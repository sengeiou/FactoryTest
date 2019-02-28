package com.fm.factorytest.comm.bean;


import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.fm.factorytest.comm.base.CommandWrapper;
import com.fm.factorytest.comm.base.PLMContext;
import com.fm.factorytest.comm.server.CommandServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import static com.fm.factorytest.comm.factory.CommandFactory.CMD_FUNC;
import static com.fm.factorytest.comm.factory.CommandFactory.generateCommandByID;
import static com.fm.factorytest.comm.factory.CommandFactory.generateCommandBySource;


/**
 * 命令发送包装类
 *
 * @author lijie
 * @create 2019-02-21 13:41
 **/
public class CommandTxWrapper extends CommandWrapper {
    public static final int DATA_FILE = 0;
    public static final int DATA_STRING = 1;
    public static final int DATA_BYTES = 2;

    private byte cmdType;

    public CommandTxWrapper(@NonNull String cmdID, String data, int dataType) {
        this(cmdID, data, dataType, CMD_FUNC);
    }

    public CommandTxWrapper(@NonNull String cmdID, String data, int dataType, int cmdType) {
        cmdList = new LinkedList<>();

        this.cmdID = cmdID.toUpperCase();
        cmd_left = (byte) Integer.parseInt(cmdID.substring(0, 2), 16);
        cmd_right = (byte) Integer.parseInt(cmdID.substring(2, 4), 16);
        this.cmdType = (byte) cmdType;

        if (data == null) {
            Command cmd = generateCommandByID(cmdID);
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

    public CommandTxWrapper(@NonNull String cmdID, byte[] data, int cmdType) {
        cmdList = new LinkedList<>();
        this.cmdID = cmdID.toUpperCase();
        cmd_left = (byte) Integer.parseInt(cmdID.substring(0, 2), 16);
        cmd_right = (byte) Integer.parseInt(cmdID.substring(2, 4), 16);
        this.cmdType = (byte) cmdType;
        if (data == null) {
            Command cmd = generateCommandByID(cmdID);
            cmdList.add(cmd);
        } else {
            bytesSplit(data);
        }
    }

    public void send() {
        new Thread() {
            @Override
            public void run() {
                Command cmd;
                System.out.println("数据帧数量：" + cmdList.size());
                while (cmdList.size() > 0 && PLMContext.commandServer != null) {
                    cmd = cmdList.pop();
                    cmd.setCmdType(cmdType);

                    PLMContext.commandServer.sendCommand(cmd);
                    SystemClock.sleep(300);
                    //重发机制
                    for (int i = 0; i < 3; i++) {
                        Command ack = null;
                        int count = 0;
                        while (ack == null && count < 3) {
                            SystemClock.sleep(50);
                            ack = CommandServer.ackList.get(cmdID);
                            count++;
                        }
                        if (ack == null) {
                            System.out.println("未收到 ACK 回复，重复发送");
                            PLMContext.commandServer.sendCommand(cmd);
                            SystemClock.sleep(300);
                        } else {
                            System.out.println("收到 ACK 回复");
                            CommandServer.ackList.remove(cmdID);
                            break;
                        }
                    }
                }
                cmdList.clear();
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
            Command cmd;
            for (int i = 0; i < sum; i++) {
                if (i < (sum - 1)) {
                    data = new byte[64];
                    System.arraycopy(datas, i * 64, data, 0, 64);
                } else {
                    data = new byte[suffix];
                    System.arraycopy(datas, (i - 1) * 64, data, 0, suffix);
                }
                cmd = generateCommandBySource(data, (byte) i, (byte) sum, cmd_left, cmd_right);
                cmdList.add(cmd);
            }

        } else {
            Command cmd = generateCommandBySource(datas, (byte) 0, (byte) 1, cmd_left, cmd_right);
            cmdList.add(cmd);
        }
    }
}
