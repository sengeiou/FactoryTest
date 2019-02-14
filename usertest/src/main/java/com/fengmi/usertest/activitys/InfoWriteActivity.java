package com.fengmi.usertest.activitys;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.droidlogic.app.KeyManager;
import com.fengmi.usertest.R;
import com.fengmi.usertest.Util;
import com.fengmi.usertest.bean.Config;
import com.fengmi.usertest.bean.MN;
import com.fengmi.usertest.bean.SN;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Enumeration;

import dalvik.system.DexFile;

public class InfoWriteActivity extends Activity {
    private static final int USB_DETECT = 0;
    private static final int CONFIG_NOT_EXIST = 1;
    private static final int CONFIG_PARSING = 2;
    private static final int CONFIG_PARSE_FINISH = 3;
    private static final int CONFIG_PARSE_ERROR = 4;
    private static final int CONFIG_DIALOG_CANCEL = 5;

    private static final String TAG = "InfoWriteActivity";
    private static final String PROJECT = "L246-factory";
    private static String usbFilePath = "unknow";
    private KeyManager keyManager;
    private InfoHandler infoHandler;
    private Config mConfig = null;

    private TextView tvPID;
    private TextView tvUIID;
    private EditText etMN;
    private EditText etSN;

    private Button btnReadConfig;
    private Button btnWriteConfig;
    private ProgressDialog dialog;
    private ConfigReadTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_write);

        keyManager = new KeyManager(this);

        tvPID = findViewById(R.id.tv_pid);
        tvUIID = findViewById(R.id.tv_uiid);
        etMN = findViewById(R.id.et_manufacture_number);
        etSN = findViewById(R.id.et_serial_number);
        btnReadConfig = findViewById(R.id.btn_read_conf);
        btnWriteConfig = findViewById(R.id.btn_write_conf);

        dialog = new ProgressDialog(this);

        dialog.setTitle("读取" + PROJECT + "配置信息");

        tvPID.setText("PID:" + keyManager.aml_key_read("product_id_fact", 0));
        tvUIID.setText("UIID:" + keyManager.aml_key_read("product_id", 0));

        infoHandler = new InfoHandler(this);
        usbRegister();

        btnReadConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task == null) {
                    task = new ConfigReadTask();
                    task.start();
                }
            }
        });

        btnWriteConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        Config config = new Config();
                        config.setSn(new SN());
                        config.setMn(new MN());
                        Util.writeConfig(config, "/data/ll.xml");
                    }
                }.start();
            }
        });
    }

    private void freshView() {
        String mn = mConfig.getMn().formatMN();
        String sn = mConfig.getSn().getValue();
        if (mn != null) {
            etMN.setText(mn);
        } else {
            etMN.setText("未能正确读取到 MN");
        }
        if (sn != null) {
            etSN.setText(sn);
        } else {
            etSN.setText("未能正确读取到 SN");
        }
    }


    private boolean checkConfigExist(String path) {
        boolean exist;
        if (path.contains("unknow")) {
            exist = false;
        } else {
            File file = new File(path);
            if (!file.exists()) {
                exist = false;
            } else {
                exist = true;
            }
        }

        return exist;
    }

    private void readConfig() {
        infoHandler.sendEmptyMessage(CONFIG_PARSING);
        String path = usbFilePath + "/config/" + PROJECT + ".xml";
        Log.d(TAG, "config path :: " + path);
        if (checkConfigExist(path)) {
            mConfig = (Config) Util.readConfig(path);
            if (mConfig != null) {
                SystemClock.sleep(1500);
                infoHandler.sendEmptyMessage(CONFIG_PARSE_FINISH);
            } else {
                SystemClock.sleep(1500);
                infoHandler.sendEmptyMessage(CONFIG_PARSE_ERROR);
            }

        } else {
            infoHandler.sendEmptyMessage(CONFIG_NOT_EXIST);
        }
        infoHandler.sendEmptyMessage(CONFIG_DIALOG_CANCEL);
    }

    private void usbRegister() {
        UsbStatesReceiver usbStatesReceiver = new UsbStatesReceiver();

        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addDataScheme("file");

        registerReceiver(usbStatesReceiver, filter);
    }

    private static class InfoHandler extends Handler {
        WeakReference<InfoWriteActivity> weakReference;

        InfoHandler(InfoWriteActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            InfoWriteActivity info;
            info = weakReference.get();
            if (info != null) {
                switch (msg.what) {
                    case CONFIG_PARSING:
                        info.dialog.show();
                        break;
                    case CONFIG_DIALOG_CANCEL:
                        info.dialog.cancel();
                        info.task = null;
                        break;
                    case USB_DETECT:
                        info.readConfig();
                        break;
                    case CONFIG_NOT_EXIST:
                        Toast.makeText(info, "未检测到配置文件", Toast.LENGTH_SHORT).show();
                        break;
                    case CONFIG_PARSE_FINISH:
                        info.freshView();
                        break;
                    case CONFIG_PARSE_ERROR:
                        Toast.makeText(info, "配置文件解析错误", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        }
    }

    class ConfigReadTask extends Thread {
        @Override
        public void run() {
            readConfig();
        }
    }

    class UsbStatesReceiver extends BroadcastReceiver {
        private static final String TAG = "UsbStatesReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                Uri data = intent.getData();
                if (data != null) {
                    usbFilePath = data.getPath();
                    Log.d(TAG, "usbFilePath :: " + usbFilePath);
                    infoHandler.sendEmptyMessage(USB_DETECT);
                }
            }
        }
    }
}
