package com.fengmi.usertest.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import mitv.powermanagement.ScreenSaverManager;

public class BaseActivity extends Activity {
    protected static final String TAG = "UserTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ScreenSaverManager manager = ScreenSaverManager.getInstance();
        manager.setScreenSaverEnabled(false);
    }
}
