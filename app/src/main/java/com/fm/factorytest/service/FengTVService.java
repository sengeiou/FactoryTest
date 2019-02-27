package com.fm.factorytest.service;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SerialManager;
import android.hardware.SerialPort;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.SystemClock;
import android.util.Log;

import com.fm.factorytest.base.BaseCmdService;
import com.fm.factorytest.comm.factory.CommandFactory;
import com.fm.factorytest.comm.server.CommandServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

public class FengTVService extends BaseCmdService {


    public FengTVService() {
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate() {
        super.onCreate();
    }

}
