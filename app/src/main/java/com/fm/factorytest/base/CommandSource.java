package com.fm.factorytest.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.fm.factorytest.comm.base.GlobalCommandReceiveListener;
import com.fm.factorytest.comm.base.PLMContext;
import com.fm.factorytest.comm.bean.CommandRxWrapper;
import com.fm.factorytest.comm.bean.CommandTxWrapper;
import com.fm.factorytest.comm.server.CommandServer;
import com.fm.factorytest.comm.vo.USB;
import com.fm.factorytest.global.FactorySetting;

import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import static com.fm.factorytest.comm.base.PLMContext.usb;
import static com.fm.factorytest.comm.factory.CommandFactory.CMD_FUNC;

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
                para1 = stringToAscii(para1);
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

        initUSB(context);

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
        usb.destroy();
        usb = null;
    }

    public void sendMsg(String cmdID, byte[] data) {
        txWrapper = new CommandTxWrapper(cmdID, data, CMD_FUNC);
        txWrapper.send();
    }

    /**
     * 初始化 USB 端口
     */
    private void initUSB(Context context) {
        if (usb == null) {
            usb = new USB.USBBuilder(context)
                    .setBaudRate(115200)
                    .setDataBits(8)
                    .setParity(1)
                    .setStopBits(0)
                    .setMaxReadBytes(80)
                    .build();
            usb.setOnUsbChangeListener(new USB.OnUsbChangeListener() {
                @Override
                public void onUsbConnect() {
                    Log.d(TAG, "onUsbConnect");
                    PLMContext.commandServer = new CommandServer();
                    PLMContext.commandServer.init();
                }

                @Override
                public void onUsbDisconnect() {
                    Log.d(TAG, "onUsbDisconnect");
                    if (PLMContext.commandServer != null) {
                        PLMContext.commandServer.close();
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
            //调用此方法先触发一次USB检测
            usb.afterGetUsbPermission(usb.getTargetDevice());
        }
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
        mCmdListener.handleCommand(cmdID,P);
    }

    public interface OnCommandListener {
        //handle receiver command
        void handleCommand(String msgid, String param);

        //return the result
        void setResultOuter(PrintWriter writer);
    }
}
