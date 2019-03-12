package com.fengmi.usertest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fengmi.usertest.activitys.AutoPQActivity;
import com.fengmi.usertest.activitys.BaseActivity;
import com.fengmi.usertest.activitys.InfoWriteActivity;

public class UserActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
    }

    public void gotoInfo(View view) {
        startActivity(new Intent(this, InfoWriteActivity.class));
        finish();
    }

    public void gotoPQ(View view) {
        startActivity(new Intent(this, AutoPQActivity.class));
        finish();
    }
}
