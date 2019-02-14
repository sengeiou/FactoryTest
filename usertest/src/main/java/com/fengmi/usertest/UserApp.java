package com.fengmi.usertest;

import android.app.Application;

import com.fengmi.usertest.bean.Config;
import com.fengmi.usertest.bean.MN;
import com.fengmi.usertest.bean.SN;

import lee.hua.xmlparse.xml.Globals;

public class UserApp extends Application {
    private Class[] clazzs = new Class[]{
            Config.class, MN.class, SN.class
    };

    @Override
    public void onCreate() {
        super.onCreate();
        initClass();
    }

    /**
     * Mi system 未能获取到 class 信息，此处手动添加 class
     */
    private void initClass() {
        Globals.setClassPathMap("Config", clazzs[0].getName());
        Globals.setClassPathMap("MN", clazzs[1].getName());
        Globals.setClassPathMap("SN", clazzs[2].getName());
    }
}
