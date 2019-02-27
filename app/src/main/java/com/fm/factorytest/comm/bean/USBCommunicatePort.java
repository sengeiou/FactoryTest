package com.fm.factorytest.comm.bean;

import com.fm.factorytest.comm.base.CommunicatePort;

import java.io.IOException;
import java.util.Arrays;

import static com.fm.factorytest.comm.factory.IOFactory.usb;


public class USBCommunicatePort extends CommunicatePort {
    private byte[] tempData = new byte[80];
    private int available = 0;

    @Override
    public int readData(byte[] recvBuffer, int off, int len) {
        int actual;
        try {
            if (!isDataEmpty(tempData)) {
                actual = available;
            } else {
                actual = usb.port.read(tempData, 300);
            }
            if (actual > len) {
                return -1;
            }
            System.arraycopy(tempData, 0, recvBuffer, off, actual);

        } catch (IOException e) {
            e.printStackTrace();
            actual = -1;
        }
        Arrays.fill(tempData, (byte) 0);
        return actual;
    }

    @Override
    public void writeData(byte[] data) {
        usb.writeData(data, 300);
    }

    @Override
    public void initPort() {

    }

    @Override
    public void closePort() {

    }

    @Override
    public int dataAvailable() {
        try {
            available = usb.port.read(tempData, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return available;
    }

    private boolean isDataEmpty(byte[] tempData) {
        boolean res = true;
        for (byte tempDatum : tempData) {
            if (tempDatum != (byte) 0) {
                res = false;
            }
        }

        return res;
    }
}
