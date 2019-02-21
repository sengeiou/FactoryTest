package com.fengmi.usertest.activitys;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.droidlogic.app.KeyManager;
import com.fengmi.usertest.R;

import java.lang.ref.WeakReference;

public class InfoWriteActivity extends BaseActivity {
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

    private TextView tvPID;
    private TextView tvUIID;
    private EditText etMN;
    private EditText etSN;

    private StringBuilder info;
    private SparseArray<String> keyArray;

    private String pid;
    private String uiid;
    private String sn;
    private String mn;

    private Button btnInfoWrite;
    private AlertDialog writeDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_write);

        keyManager = new KeyManager(this);

        tvPID = findViewById(R.id.tv_pid);
        tvUIID = findViewById(R.id.tv_uiid);
        etMN = findViewById(R.id.et_manufacture_number);
        etSN = findViewById(R.id.et_serial_number);

        btnInfoWrite = findViewById(R.id.btn_info_write);

        pid = keyManager.aml_key_read("product_id_fact", 0);
        uiid = keyManager.aml_key_read("product_id", 0);
        tvPID.setText("PID:" + pid);
        tvUIID.setText("UIID:" + uiid);

        infoHandler = new InfoHandler(this);

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etMN.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etSN.getWindowToken(), 0);

        info = new StringBuilder();
        keyArrayInit();

        btnInfoWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeInfo();
            }
        });

        writeDialog = new AlertDialog
                .Builder(this)
                .setTitle("SN 和 MN 写入")
                .setPositiveButton("写入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        keyManager.aml_key_write("assm_sn", sn, 0);
                        keyManager.aml_key_write("assm_mn", mn, 0);
                        SystemClock.sleep(500);
                    }
                })
                .setNegativeButton("取消", null)
                .create();
    }

    private void writeInfo() {
        sn = etSN.getText().toString();
        mn = etMN.getText().toString();
        if (sn.length() == 0) {
            Toast.makeText(this, "SN 为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mn.length() == 0) {
            Toast.makeText(this, "MN 为空", Toast.LENGTH_SHORT).show();
            return;
        }
        sn = sn.split("/")[1];
        Log.d(TAG, "sn = " + sn);
        Log.d(TAG, "mn = " + mn);

        writeDialog.setMessage("请确认信息是否正确" + "\n"
                + "SN = " + sn + "\n"
                + "MN = " + mn);
        writeDialog.show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.ACTION_UP && event.getAction() == KeyEvent.KEYCODE_MENU) {

        }
        if (event.getDevice().getName().contains("Honeywell")) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                Log.d(TAG, "KeyCode :: " + event.getKeyCode());
                Log.d(TAG, "KeyCode :: " + event.getDevice().getName());
                int keyCode = event.getKeyCode();
                String s = keyMatch(keyCode);
                Log.d(TAG, "key info :: " + s);
                if (!"err".equals(s)) {
                    info.append(s);
                }
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String res = info.toString();
                    Log.d(TAG, "scan info :: " + res);
                    if (snVerify(res, "19101")) {
                        etSN.setText(res);
                    }
                    if (mnVerify(res)) {
                        etMN.setText(res);
                    }

                    info.delete(0, info.length());
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private boolean snVerify(String sn, String pid) {
        if (sn.startsWith(pid) && sn.contains("/")) {
            String[] sns = sn.split("/");
            if (sns.length < 2) {
                return false;
            }
            if (sns[1].length() == 8 && TextUtils.isDigitsOnly(sns[1])) {
                return true;
            }
        }
        return false;
    }

    private boolean mnVerify(String mn) {
        if (mn.length() == 17) {
            String prefix = mn.substring(0, 3);
            String suffix = mn.substring(10, 17);
            Log.d(TAG, "mn[0-3]=" + prefix + " mn[10-17]=" + suffix);
            return TextUtils.isDigitsOnly(suffix) && prefix.matches("[a-zA-Z]{3}");
        }
        return false;
    }

    private void keyArrayInit() {
        keyArray = new SparseArray<>();
        keyArray.append(KeyEvent.KEYCODE_0, "0");
        keyArray.append(KeyEvent.KEYCODE_1, "1");
        keyArray.append(KeyEvent.KEYCODE_2, "2");
        keyArray.append(KeyEvent.KEYCODE_3, "3");
        keyArray.append(KeyEvent.KEYCODE_4, "4");
        keyArray.append(KeyEvent.KEYCODE_5, "5");
        keyArray.append(KeyEvent.KEYCODE_6, "6");
        keyArray.append(KeyEvent.KEYCODE_7, "7");
        keyArray.append(KeyEvent.KEYCODE_8, "8");
        keyArray.append(KeyEvent.KEYCODE_9, "9");
        keyArray.append(KeyEvent.KEYCODE_A, "A");
        keyArray.append(KeyEvent.KEYCODE_B, "B");
        keyArray.append(KeyEvent.KEYCODE_C, "C");
        keyArray.append(KeyEvent.KEYCODE_D, "D");
        keyArray.append(KeyEvent.KEYCODE_E, "E");
        keyArray.append(KeyEvent.KEYCODE_F, "F");
        keyArray.append(KeyEvent.KEYCODE_G, "G");
        keyArray.append(KeyEvent.KEYCODE_H, "H");
        keyArray.append(KeyEvent.KEYCODE_I, "I");
        keyArray.append(KeyEvent.KEYCODE_J, "J");
        keyArray.append(KeyEvent.KEYCODE_K, "K");
        keyArray.append(KeyEvent.KEYCODE_L, "L");
        keyArray.append(KeyEvent.KEYCODE_M, "M");
        keyArray.append(KeyEvent.KEYCODE_N, "N");
        keyArray.append(KeyEvent.KEYCODE_O, "O");
        keyArray.append(KeyEvent.KEYCODE_P, "P");
        keyArray.append(KeyEvent.KEYCODE_Q, "Q");
        keyArray.append(KeyEvent.KEYCODE_R, "R");
        keyArray.append(KeyEvent.KEYCODE_S, "S");
        keyArray.append(KeyEvent.KEYCODE_T, "T");
        keyArray.append(KeyEvent.KEYCODE_U, "U");
        keyArray.append(KeyEvent.KEYCODE_V, "V");
        keyArray.append(KeyEvent.KEYCODE_W, "W");
        keyArray.append(KeyEvent.KEYCODE_X, "X");
        keyArray.append(KeyEvent.KEYCODE_Y, "Y");
        keyArray.append(KeyEvent.KEYCODE_Z, "Z");
        keyArray.append(KeyEvent.KEYCODE_SLASH, "/");
        keyArray.append(KeyEvent.KEYCODE_ENTER, "");
    }

    private String keyMatch(int key) {
        String val = keyArray.get(key, "err");
        return val;
    }


    /**
     * 注册 U盘 监听
     */
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
                        //info.dialog.show();
                        break;
                    case CONFIG_DIALOG_CANCEL:
                        //info.dialog.cancel();
                        //info.readTask = null;
                        break;
                    case USB_DETECT:
                        //info.readConfig();
                        break;
                    case CONFIG_NOT_EXIST:
                        Toast.makeText(info, "未检测到配置文件", Toast.LENGTH_SHORT).show();
                        break;
                    case CONFIG_PARSE_FINISH:
                        //info.freshView();
                        break;
                    case CONFIG_PARSE_ERROR:
                        Toast.makeText(info, "配置文件解析错误", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
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
