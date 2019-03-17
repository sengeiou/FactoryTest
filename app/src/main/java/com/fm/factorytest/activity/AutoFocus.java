package com.fm.factorytest.activity;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import com.fengmi.IMotorFocusService;
import com.fm.factorytest.R;
import com.fm.factorytest.base.BaseActivity;
import com.fm.factorytest.global.FactorySetting;
import com.fm.factorytest.utils.MotorUtil;

public class AutoFocus extends BaseActivity implements MotorUtil.AFCallback{
    private static final String TAG = "AutoFocus";
    private TextView tv = null;

    private IMotorFocusService motorService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_focus);
        tv = (TextView)findViewById(R.id.tv_auto_focus);

        motorService =  MotorUtil.getMotorService();
        if (motorService != null) {
            MotorUtil.setEventCallback(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MotorUtil.unsetEventCallback();
    }
    @Override
    public void onAFStart() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText("自动对焦中，请等待");
            }
        });
    }
    @Override
    public void onAFFinish() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText("自动对焦完成，请继续");
            }
        });
    }

    public void handleCommand(String cmdid, String param) {
    }
    public void handleControlMsg(int cmdtype, String cmdid, String cmdpara) {
        if(FactorySetting.COMMAND_TASK_STOP == cmdtype){
            finish();
            setResult(cmdid, PASS, true);
        }else{
            int cmd = Integer.parseInt(cmdid,16);
            if (cmd == 0x14D5) {
                startAF();
                setResult(cmdid, PASS, false);
            }
        }
    }

    public void startAF(){
        try {
            if (motorService != null){
                motorService.startAutoFocus();
            }else {
                Log.d(TAG,"ready to startAutoFocus() but MotorService is null");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d(TAG,"RemoteException :: " + e.getMessage());
        }
    }

    public void stopAF(){
        try {
            if (motorService!=null){
                motorService.stopAutoFocus();
            }else {
                Log.d(TAG,"ready to stopAutoFocus() but MotorService is null");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d(TAG,"RemoteException :: " + e.getMessage());
        }
    }
}
