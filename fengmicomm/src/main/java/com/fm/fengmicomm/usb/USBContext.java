package com.fm.fengmicomm.usb;

import com.fm.fengmicomm.usb.callback.CL200RxDataCallBack;
import com.fm.fengmicomm.usb.command.CL200Command;
import com.fm.fengmicomm.usb.command.CP210xCommand;
import com.fm.fengmicomm.usb.task.CL200CommTask;
import com.fm.fengmicomm.usb.task.CL200ProtocolTask;
import com.fm.fengmicomm.usb.task.CP210xCommTask;
import com.fm.fengmicomm.usb.task.CP210xProtocolTask;
import com.fm.fengmicomm.usb.task.UsbCommTask;
import com.fm.fengmicomm.usb.task.UsbProtocolTask;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public final class USBContext {
    public static final byte TYPE_ACK = 0b00010000;
    public static final byte TYPE_N_ACK = 0b00100000;
    public static final byte TYPE_FUNC = 0b00000000;
    public static final byte TYPE_DATA = 0b00000001;
    public static final byte TYPE_CTL = 0b00000010;

    /****************************************************************************
     *                                                                          *
     *                              CL200 相关变量                               *
     *                                                                          *
     * **************************************************************************/
    public static volatile USB cl200Usb;
    public static CL200ProtocolTask cl200ProtocolTask;
    public static CL200CommTask cl200CommTask;
    public static volatile ArrayBlockingQueue<CL200Command> cl200RxQueue;
    public static volatile ArrayBlockingQueue<CL200Command> cl200TxQueue;
    public static CL200RxDataCallBack cl200RxDataCallBack;
    /****************************************************************************
     *                                                                          *
     *                              CP210x 相关变量                              *
     *                                                                          *
     * **************************************************************************/
    public static volatile USB cp210xUsb;
    public static CP210xCommTask cp210xCommTask;
    public static CP210xProtocolTask cp210xProtocolTask;
    public static volatile ArrayBlockingQueue<CP210xCommand> cp210xRxQueue;
    public static volatile ArrayBlockingQueue<CP210xCommand> cp210xTxQueue;
    public static volatile ConcurrentHashMap<String, CP210xCommand> ackMap;
    public static volatile ConcurrentHashMap<String, CP210xCommand> nackMap;
}
