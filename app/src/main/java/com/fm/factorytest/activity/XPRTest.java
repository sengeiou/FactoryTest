package com.fm.factorytest.activity;

import android.os.Bundle;

import com.fm.factorytest.R;
import com.fm.factorytest.base.BaseActivity;
import com.fm.factorytest.global.FactorySetting;

public class XPRTest extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xpr_test);
    }

    public void handleCommand(String cmdid, String param) {
    }
    public void handleControlMsg(int cmdtype, String cmdid, String cmdpara) {
        if(FactorySetting.COMMAND_TASK_STOP == cmdtype){
            finish();
            setResult(cmdid, PASS, true);
        }
    }
}
