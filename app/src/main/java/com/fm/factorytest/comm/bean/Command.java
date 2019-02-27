package com.fm.factorytest.comm.bean;


import android.annotation.Nullable;

import com.fm.factorytest.comm.factory.CommandFactory;

/**
 * command
 *
 * @author lijie
 * @create 2019-01-07 15:01
 **/
public class Command {
    private byte cmdID_Left;
    private byte cmdID_Right;
    private byte cmdType;
    private byte dataLen;
    private byte[] data;
    private byte cmdNum;
    private byte cmdSum;

    public Command(byte[] origin) {
        if ((origin.length >= 9) && origin[0] == (byte) 0x79 && origin[origin.length - 1] == (byte) 0xFE) {
            init(origin[2], origin[3], origin[1], origin[4], null, origin[origin.length - 4], origin[origin.length - 3]);
            if (origin[4] > 0) {
                int len = origin[4];
                data = new byte[len];
                System.arraycopy(origin, 5, data, 0, len);
            }
        } else {
            System.out.println("error cmd structure");
        }
    }

    public Command(byte cmdID_Left, byte cmdID_Right, byte cmdType, byte dataLen, @Nullable byte[] data, byte cmdNum, byte cmdSum) {
        init(cmdID_Left, cmdID_Right, cmdType, dataLen, data, cmdNum, cmdSum);
    }

    private void init(byte cmdID_Left, byte cmdID_Right, byte cmdType, byte dataLen, @Nullable byte[] data, byte cmdNum, byte cmdSum) {
        this.cmdID_Left = cmdID_Left;
        this.cmdID_Right = cmdID_Right;
        this.cmdType = cmdType;
        this.dataLen = dataLen;
        this.data = data;
        this.cmdNum = cmdNum;
        this.cmdSum = cmdSum;
    }

    public byte getCmdNum() {
        return cmdNum;
    }

    public byte getCmdSum() {
        return cmdSum;
    }

    public byte getCmdID_Left() {
        return cmdID_Left;
    }

    public byte getCmdID_Right() {
        return cmdID_Right;
    }

    public byte getCmdType() {
        return cmdType;
    }

    public void setCmdType(byte cmdType) {
        this.cmdType = cmdType;
    }

    public byte getDataLen() {
        return dataLen;
    }

    public byte[] getData() {
        return data;
    }

    public String getCommandID() {
        return Integer.toHexString((cmdID_Left << 8) + (cmdID_Right & 0xff)).toUpperCase();
    }

    public byte[] toByteArray(){
        int cmdLen = this.getDataLen() + 9;
        byte[] send = new byte[cmdLen];

        send[0] = 0x79;
        send[1] = this.getCmdType();
        send[2] = this.getCmdID_Left();
        send[3] = this.getCmdID_Right();
        send[4] = this.getDataLen();

        if (this.getDataLen() > 0) {
            System.arraycopy(this.getData(), 0, send, 5, this.getDataLen());
        }
        send[cmdLen - 4] = this.getCmdNum();
        send[cmdLen - 3] = this.getCmdSum();
        send[cmdLen - 2] = CommandFactory.calCRC(send);
        send[cmdLen - 1] = (byte) 0xFE;

        return send;
    }

}