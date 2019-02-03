package com.fm.factorytest.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

import com.fm.factorytest.global.FactorySetting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/* receive command from remoter, maybe network, broadcast receiver ....*/

public class CommandSource {
    private static final String FAKE_COMMAN_ACTION = "com.duokan.command.fake";
    private static final String TAG = "FactoryCommandSource";
    private static final int Dida_timer = 1000;//1s
    OnCommandListener mCmdListener;
    LocalSocketAddress address;
    private boolean isConnected = false;
    Context mContext;
    LocalSocket mSocket;
    InputStream mIn;
    OutputStream mOut;
    int fail_counter = 0;
    String mParam = "";
    int last_no = -1;
    String last_comid = "";

    /**
     * 判断是否正在运行
     */
    private boolean running = false;
    private boolean serverConnected = false;

    CommandSource(Context context, OnCommandListener listener) {
        mContext = context;
        mCmdListener = listener;
        //start net thread and get commands

        //start register intent filter to get commands
        IntentFilter filter = new IntentFilter(FAKE_COMMAN_ACTION);
        mContext.registerReceiver(fakeCommandReceiver, filter);
        //创建socket
        mSocket = new LocalSocket();
        //设置连接地址
        address = new LocalSocketAddress("fiu", LocalSocketAddress.Namespace.RESERVED);
        Log.i(TAG, "Socket fiu !");

        // 将控制器running设置为true
        running = true;

        // 启动连接线程
        new Thread(Socket_connect).start();
    }

    // 连接线程
    Thread Socket_connect = new Thread() {
        public void run() {
            while (!isConnected && running) {
                try {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "Socket_connect !");
                    //建立连接
                    mSocket.connect(address);
                    isConnected = true;
                    fail_counter = 0;
                    // 启动接收线程
                    new Thread(Socket_receive).start();
                    // 启动滴答线程
                    new Thread(Socket_dida).start();

                } catch (IOException e) {
                    //e.printStackTrace();
                    fail_counter++;
                }
            }
        }
    };

    // 接收线程
    Thread Socket_receive = new Thread() {
        public void run() {
            while (isConnected && running) {
                try {
                    //Thread.sleep(1000);
                    //Log.i(TAG, "Socket_receive !" );
                    mSocket.setReceiveBufferSize(73);
                    mSocket.setSendBufferSize(73);
                    mIn = mSocket.getInputStream();
                    byte[] data;
                    data = new byte[4];
                    mIn.read(data);
                    int len = data[3];
                    //Log.i(TAG, "receive data len : " + len);
                    if (len <= 0 || len > 69) {
                        Log.i(TAG, "abnormal command");
                        break;
                    }
                    byte[] data_raw;
                    int cmdId_h, cmdId_l;
                    data_raw = new byte[len];
                    mIn.read(data_raw, 0, len);
                    for (int i = 0; i < len; i++) {
                        Log.i(TAG, "raw data: [" + i + "] : " + data_raw[i]);
                    }
                    String comd = "";
                    cmdId_h = data_raw[0];
                    if (data_raw[0] < 0) {
                        cmdId_h = (0xff + (int) data_raw[0] + 1) | 0xff;
                        Log.i(TAG, "raw command Id H: " + "[" + cmdId_h + "]");
                    }
                    cmdId_l = data_raw[1];
                    if (data_raw[1] < 0) {
                        cmdId_l = (0xff + (int) data_raw[1] + 1) & 0xff;
                        Log.i(TAG, "raw command Id L: " + "[" + cmdId_l + "]");
                    }
                    comd = Integer.toHexString((cmdId_h / 16) & 0xff) + Integer.toHexString((cmdId_h % 16) & 0xff)
                            + Integer.toHexString((cmdId_l / 16) & 0xff) + Integer.toHexString((cmdId_l % 16) & 0xff);
                    String para = "";
                    for (int i = 0; i < data_raw[2]; i++) {
                        if (i == (data_raw[2] - 1)) {
                            para += (data_raw[i + 3] & 0xff);
                        } else {
                            para += (data_raw[i + 3] & 0xff) + ",";
                        }
                    }
                    Log.i(TAG, "handleCommand comd : [" + comd + "],para : [" + para + "]" + "last_cmdid :[" + last_comid + "]");
                    if (comd.equals("ffff")) {
                        Log.i(TAG, "Uartservice restart reconnect !");
                        isConnected = false;
                        try {
                            mSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //创建socket
                        mSocket = new LocalSocket();
                        //设置连接地址
                        address = new LocalSocketAddress("fiu", LocalSocketAddress.Namespace.RESERVED);
                        // 启动连接线程
                        new Thread(Socket_connect).start();
                        break;
                    }
                    int data_no = data_raw[len - 2];
                    int data_sum = data_raw[len - 1];
                    Log.i(TAG, "handleCommand data_no : [" + data_no + "],data_sum : [" + data_sum + "]" + "last_no :[" + last_no + "]");
                    if (data_no == last_no + 1) {
                        if (data_no == data_sum - 1) {
                            if (data_no == 0)
                                mParam += para;
                            else
                                mParam += ',' + para;
                            mCmdListener.handleCommand(comd.toUpperCase(), mParam);
                            mParam = "";
                            last_no = -1;
                        } else if (data_no < data_sum) {
                            if (data_no == 0)
                                last_comid = comd;
                            if (comd.equals(last_comid) && (data_no == last_no + 1)) {
                                last_no = data_no;
                                if (data_no == 0)
                                    mParam += para;
                                else
                                    mParam += ',' + para;
                            }
                        }
                    }
                    // 将i设为0
                } catch (IOException e) {
                    e.printStackTrace();
                    fail_counter++;
                }
            }
            try {
                mIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    // 滴答线程
    Thread Socket_dida = new Thread() {
        public void run() {
            try {
                //获取数据输出流 可以写数据
                mOut = mSocket.getOutputStream();
                byte[] comd = new byte[5];
                //Factory test app started command 0x03FF
                comd[0] = 0x03;
                comd[1] = -1;
                comd[2] = 0x00;
                comd[3] = 0x00;
                comd[4] = 0x01;
                sendMsg(comd, 5);
            } catch (IOException e) {
                e.printStackTrace();
                fail_counter++;
            }

            while (isConnected && running) {
                if (fail_counter >= 3) {
                    fail_counter = 0;
                    isConnected = false;
                    try {
                        mSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //创建socket
                    mSocket = new LocalSocket();
                    //设置连接地址
                    address = new LocalSocketAddress("fiu", LocalSocketAddress.Namespace.RESERVED);
                    Log.i(TAG, "Socket fiu !");
                    // 启动连接线程
                    new Thread(Socket_connect).start();
                }
                try {
                    Thread.sleep(Dida_timer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Log.i(TAG, "dida command send !" );
                byte[] comd = new byte[5];
                //dida command 0x0001
                comd[0] = 0x00;
                comd[1] = 0x01;
                comd[2] = 0x00;
                comd[3] = 0x00;
                comd[4] = 0x01;
                sendMsg(comd, 5);
            }
        }
    };

    public void sendMsg(byte[] comd, int len) {
        byte[] comd_send = new byte[len + 4];
        //Log.i(TAG, "sendMsg, len:" + len );

        if (!isConnected) {
            Log.i(TAG, "sendMsg isConnected failed: " + isConnected);
            return;
        }
        if (len > 69) {
            Log.i(TAG, "sendMsg len too big : " + len);
            return;
        }
        try {
            for (int i = 0; i < 3; i++) {
                comd_send[i] = 0;
            }
            comd_send[3] = (byte) len;
            for (int j = 0; j < len; j++) {
                comd_send[j + 4] = comd[j];
            }
            mSocket.setReceiveBufferSize(73);
            mSocket.setSendBufferSize(73);
            mOut.write(comd_send);
            mOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            fail_counter++;
        }
    }

    public void finishCommandSouce() {
        //stop net thread
        running = false;
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mContext.unregisterReceiver(fakeCommandReceiver);
    }

    public interface OnCommandListener {
        //handle receiver command
        void handleCommand(String msgid, String param);

        //return the result
        void setResultOuter(PrintWriter writer);
    }

    private BroadcastReceiver fakeCommandReceiver = new BroadcastReceiver() {
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
}
