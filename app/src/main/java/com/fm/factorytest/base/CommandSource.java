package com.fm.factorytest.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.fm.factorytest.global.FactorySetting;
import com.fm.fengmicomm.usb.USB;
import com.fm.fengmicomm.usb.USBContext;
import com.fm.fengmicomm.usb.callback.GlobalCommandReceiveListener;
import com.fm.fengmicomm.usb.command.CommandRxWrapper;
import com.fm.fengmicomm.usb.command.CommandTxWrapper;
import com.fm.fengmicomm.usb.task.CL200CommTask;
import com.fm.fengmicomm.usb.task.CL200ProtocolTask;
import com.fm.fengmicomm.usb.task.CP210xCommTask;
import com.fm.fengmicomm.usb.task.CP210xProtocolTask;

import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import static com.fm.fengmicomm.usb.USBContext.cl200CommTask;
import static com.fm.fengmicomm.usb.USBContext.cl200ProtocolTask;
import static com.fm.fengmicomm.usb.USBContext.cl200Usb;
import static com.fm.fengmicomm.usb.USBContext.cp210xUsb;


/* receive command from remoter, maybe network, broadcast receiver ....*/

public class CommandSource implements GlobalCommandReceiveListener {
    private static final String FAKE_COMMAN_ACTION = "com.duokan.command.fake";
    private static final String TAG = "FactoryCommandSource";
    private OnCommandListener mCmdListener;
    private final BroadcastReceiver fakeCommandReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(FAKE_COMMAN_ACTION)) {
                String para0 = intent.getStringExtra(FactorySetting.EXTRA_CMDID);
                String para1 = intent.getStringExtra(FactorySetting.EXTRA_CMDPARA);
                if (para0 != null) {
                    para0 = para0.toUpperCase();
                    try {
                        Integer.parseInt(para0, 16);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, e.toString());
                        return;
                    }
                }
                if (para1 != null) {
                    para1 = stringToAscii(para1);
                }
                // }
                Log.i(TAG, "Got FAKE_COMMAN_ACTION, para0 : [ " + para0 + " ], para1 : [" + para1 + " ]");
                mCmdListener.handleCommand(para0, para1);
            }
        }

        public String stringToAscii(String value) {
            StringBuffer sbu = new StringBuffer();
            char[] chars = value.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (i != chars.length - 1) {
                    sbu.append((int) chars[i]).append(",");
                } else {
                    sbu.append((int) chars[i]);
                }
            }
            return sbu.toString();
        }
    };

    private WeakReference<Context> ctx;
    private CommandTxWrapper txWrapper;

    CommandSource(Context context, OnCommandListener listener) {
        ctx = new WeakReference<>(context);
        mCmdListener = listener;
        registerFakeCommand();

        initCL200A(context);
        initCP210x(context);

        CommandRxWrapper.addGlobalRXListener(this);
    }


    private void registerFakeCommand() {
        Context context = ctx.get();
        if (context != null) {
            IntentFilter filter = new IntentFilter(FAKE_COMMAN_ACTION);
            context.registerReceiver(fakeCommandReceiver, filter);
        }
    }


    public void finishCommandSouce() {
        Context context = ctx.get();
        if (context != null) {
            context.unregisterReceiver(fakeCommandReceiver);
        }
        if (cp210xUsb != null) {
            cp210xUsb.destroy(context);
            cp210xUsb = null;
        }
    }

    public void sendMsg(String cmdID, byte[] data) {
        txWrapper = CommandTxWrapper.initTX(cmdID, null,
                data, CommandTxWrapper.DATA_BYTES, USBContext.TYPE_FUNC);
        txWrapper.send();
    }

    /**
     * 初始化 USB 端口
     */
    private void initCP210x(Context context) {
        if (cp210xUsb == null) {
            cp210xUsb = new USB.USBBuilder(context)
                    .setBaudRate(115200)
                    .setDataBits(8)
                    .setParity(1)
                    .setStopBits(0)
                    .setMaxReadBytes(80)
                    .setVID(4292)
                    .setPID(60000)
                    .build();
            cp210xUsb.setOnUsbChangeListener(new USB.OnUsbChangeListener() {
                @Override
                public void onUsbConnect() {
                    Log.d(TAG, "onUsbConnect");
                    if (USBContext.cp210xProtocolTask != null || USBContext.cp210xCommTask != null) {
                        killServer();
                    }
                    startServer();
                }

                @Override
                public void onUsbDisconnect() {
                    killServer();
                    Log.d(TAG, "onUsbDisconnect");
                }

                @Override
                public void onUsbConnectFailed() {
                    Log.d(TAG, "onUsbConnectFailed");
                }

                @Override
                public void onPermissionGranted() {
                    Log.d(TAG, "onPermissionGranted");
                }

                @Override
                public void onPermissionRefused() {
                    Log.d(TAG, "onPermissionRefused");
                }

                @Override
                public void onDriverNotSupport() {
                    Log.d(TAG, "onDriverNotSupport");
                }

                @Override
                public void onWriteDataFailed(String s) {
                    Log.d(TAG, "onWriteDataFailed == " + s);
                }

                @Override
                public void onWriteSuccess(int i) {
                    Log.d(TAG, "onWriteSuccess");
                }
            });
            if (cp210xUsb.getTargetDevice() != null) {
                //调用此方法先触发一次USB检测
                cp210xUsb.afterGetUsbPermission(cp210xUsb.getTargetDevice());
            }
        }
    }

    private void initCL200A(Context context) {
        cl200Usb = new USB.USBBuilder(context)
                .setBaudRate(9600)
                .setDataBits(7)
                .setParity(2)//EVEN
                .setStopBits(1)
                .setMaxReadBytes(80)
                .setVID(1027)
                .setPID(24577)
                .build();
        cl200Usb.setOnUsbChangeListener(new USB.OnUsbChangeListener() {
            @Override
            public void onUsbConnect() {
                if (cl200ProtocolTask == null) {
                    cl200ProtocolTask = new CL200ProtocolTask();
                    cl200ProtocolTask.initTask();
                    cl200ProtocolTask.start();
                }
                if (cl200CommTask == null) {
                    cl200CommTask = new CL200CommTask();
                    cl200CommTask.initTask();
                    cl200CommTask.start();
                }
            }

            @Override
            public void onUsbDisconnect() {
                if (cl200CommTask != null) {
                    cl200CommTask.killComm();
                    cl200CommTask = null;
                }
                if (cl200ProtocolTask != null) {
                    cl200ProtocolTask.killProtocol();
                    cl200ProtocolTask = null;
                }
            }

            @Override
            public void onUsbConnectFailed() {
                Log.d(TAG, "onUsbConnectFailed");
            }

            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "onPermissionGranted");
            }

            @Override
            public void onPermissionRefused() {
                Log.d(TAG, "onPermissionRefused");
            }

            @Override
            public void onDriverNotSupport() {
                Log.d(TAG, "onDriverNotSupport");
            }

            @Override
            public void onWriteDataFailed(String s) {
                Log.d(TAG, "onWriteDataFailed == " + s);
            }

            @Override
            public void onWriteSuccess(int i) {
                Log.d(TAG, "onWriteSuccess");
            }
        });
    }

    @Override
    public void onRXWrapperReceived(String cmdID, byte[] data) {
        Log.d(TAG, "we received cmd id = " + cmdID + ", value is " + (data == null ? "null" : Arrays.toString(data)));
        String P = "";
        if (data != null) {
            StringBuilder par = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                par.append(data[i]).append(",");
            }
            P = par.toString();
        }
        mCmdListener.handleCommand(cmdID, P);
    }

    private void killServer() {
        if (USBContext.cp210xCommTask != null) {
            USBContext.cp210xCommTask.killComm();
            USBContext.cp210xCommTask = null;
        }
        if (USBContext.cp210xProtocolTask != null) {
            USBContext.cp210xProtocolTask.killProtocol();
            USBContext.cp210xProtocolTask = null;
        }
    }

    private void startServer() {
        USBContext.cp210xProtocolTask = new CP210xProtocolTask();
        USBContext.cp210xProtocolTask.taskInit();
        USBContext.cp210xProtocolTask.start();

        USBContext.cp210xCommTask = new CP210xCommTask();
        USBContext.cp210xCommTask.initTask();
        USBContext.cp210xCommTask.start();
    }

    public interface OnCommandListener {
        //handle receiver command
        void handleCommand(String msgid, String param);

        //return the result
        void setResultOuter(PrintWriter writer);
    }
}
