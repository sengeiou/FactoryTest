package com.fm.factorytest.comm.bean;


import android.hardware.SerialPort;

import com.fm.factorytest.comm.base.CommunicatePort;
import com.fm.factorytest.utils.ShellUtil;

import java.io.File;
import java.io.IOException;



/**
 * @author lijie
 * @create 2019-02-22 13:33
 **/
public class CP2102CommunicatePort extends CommunicatePort {
    private static final String SERIAL_PORT_0 = "/dev/ttyUSB0";
    private static final String SERIAL_PORT_1 = "/dev/ttyUSB1";
    private static SerialPort port;


    @Override
    public int readData(byte[] recvBuffer, int off, int len) {
        return 0;
    }

    @Override
    public void writeData(byte[] data) {

    }

    @Override
    public void initPort() {
        String curPort = null;
        if (port != null) {
            System.out.println("已初始化串口");
            return;
        }
        File file = new File(SERIAL_PORT_0);
        if (file.exists()) {
            curPort = SERIAL_PORT_0;
        } else {
            file = new File(SERIAL_PORT_1);
            if (file.exists()) {
                curPort = SERIAL_PORT_1;
            }
        }

        if (curPort == null) {
            System.out.println("未找到可用串口：");
            return;
        }

        try {
            ShellUtil.execCommand("/system/xbin/su");
            ShellUtil.execCommand("chmod 666 "+curPort);
            ShellUtil.execCommand("chown system "+curPort);
            ShellUtil.execCommand("chgrp system "+curPort);

            // port = new SerialPort(file,
            //         115200, SerialPort.DATAB.CS8.getDataBit(),
            //         SerialPort.STOPB.B1.getStopBit(),
            //         SerialPort.PARITY.NONE.getParity(), 0, 0);
            // inputStream = port.getInputStream();
            // outputStream = port.getOutputStream();

            // SerialHelper helper = new SerialHelper(curPort,115200) {
            //     @Override
            //     protected void onDataReceived(ComBean paramComBean) {
            //
            //     }
            // };
            // helper.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closePort() {
        try {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            inputStream = null;
            outputStream = null;
        }

        if (port != null) {
            //port.close();
            port = null;
        }
    }

    @Override
    public int dataAvailable() {
        return 0;
    }
}
