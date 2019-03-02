package com.fm.fengmicomm.usb.port;

import static com.fm.fengmicomm.usb.USBContext.usb;


public class USBCommunicatePort extends CommunicatePort {
    private byte[] tempData = new byte[80];
    private int available = 0;

    @Override
    public int readData(byte[] recvBuffer, int off, int len) {
        // int actual = -1;
        // if (usb != null) {
        //     if (!isDataEmpty(tempData)) {
        //         actual = available;
        //     } else {
        //         actual = usb.readData(tempData, 300);
        //     }
        //     if (actual > len) {
        //         return -1;
        //     }
        //     System.arraycopy(tempData, 0, recvBuffer, off, actual);
        //     Arrays.fill(tempData, (byte) 0);
        // }
        // return actual;
        int val = -1;
        if (usb != null) {
            byte[] temp = new byte[1024];
            val = usb.readData(temp, 300);
            System.arraycopy(temp, 0, recvBuffer, off, len);
        }
        return val;
    }

    @Override
    public int readData(byte[] recvBuffer, int timeoutMills) {
        int val = -1;
        if (usb != null) {
            val = usb.readData(recvBuffer, timeoutMills);
        }
        return val;
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
        available = usb.readData(tempData, 300);
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
