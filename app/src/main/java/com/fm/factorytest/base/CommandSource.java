package com.fm.factorytest.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.fm.factorytest.comm.server.CommandServer;
import com.fm.factorytest.comm.vo.USB;
import com.fm.factorytest.global.FactorySetting;

import java.io.PrintWriter;

import static com.fm.factorytest.comm.factory.IOFactory.initPort;
import static com.fm.factorytest.comm.factory.IOFactory.usb;

/* receive command from remoter, maybe network, broadcast receiver ....*/

public class CommandSource {
    private static final String FAKE_COMMAN_ACTION = "com.duokan.command.fake";
    private static final String TAG = "FactoryCommandSource";
    private OnCommandListener mCmdListener;
    private final BroadcastReceiver fakeCommandReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(FAKE_COMMAN_ACTION)) {
                String para0 = intent.getStringExtra(FactorySetting.EXTRA_CMDID);
                String para1 = intent.getStringExtra(FactorySetting.EXTRA_CMDPARA);
                //String paradbg = intent.getStringExtra(FactorySetting.EXTRA_CMDPARAMDBG);
                // if (paradbg != null) {
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

    private Context mContext;

    CommandSource(Context context, OnCommandListener listener) {
        mContext = context;
        mCmdListener = listener;
        registerFakeCommand();
    }


    private void registerFakeCommand() {
        IntentFilter filter = new IntentFilter(FAKE_COMMAN_ACTION);
        mContext.registerReceiver(fakeCommandReceiver, filter);
    }


    public void finishCommandSouce() {
        mContext.unregisterReceiver(fakeCommandReceiver);
    }

    public void sendMsg(byte[] comd, int len) {

    }

    public interface OnCommandListener {
        //handle receiver command
        void handleCommand(String msgid, String param);

        //return the result
        void setResultOuter(PrintWriter writer);
    }
}
