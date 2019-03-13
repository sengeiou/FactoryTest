package com.fm.fengmicomm.usb;

import com.fm.fengmicomm.usb.callback.CL200RxDataCallBack;
import com.fm.fengmicomm.usb.command.CL200Command;
import com.fm.fengmicomm.usb.command.Command;
import com.fm.fengmicomm.usb.task.CL200CommTask;
import com.fm.fengmicomm.usb.task.CL200ProtocolTask;
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
    public static volatile ArrayBlockingQueue<Command> commandRxQueue;
    public static volatile ArrayBlockingQueue<Command> commandTxQueue;
    public static volatile ConcurrentHashMap<String, Command> ackMap;
    public static volatile ConcurrentHashMap<String, Command> nackMap;

    public static volatile USB usb;
    public static UsbProtocolTask usbProtocolTask;
    public static UsbCommTask usbCommTask;

    public static volatile USB cl200Usb;
    public static CL200ProtocolTask cl200ProtocolTask;
    public static CL200CommTask cl200CommTask;
    public static volatile ArrayBlockingQueue<CL200Command> cl200RxQueue;
    public static volatile ArrayBlockingQueue<CL200Command> cl200TxQueue;
    public static CL200RxDataCallBack cl200RxDataCallBack;
}
