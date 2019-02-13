package com.fengmi.usertest;

import android.annotation.NonNull;

import com.fengmi.usertest.bean.Config;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import lee.hua.xmlparse.api.XMLAPI;

public final class Util {
    public volatile static int PQ_ADJUST_STEP = 1;
    private static List<IRadioCheckListener> listeners = new ArrayList<>();

    public static void addCheckListener(@NonNull IRadioCheckListener listener){
        if (!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    public static void clearListener(){
        listeners.clear();
    }

    public static void notifyListeners(){
        for (IRadioCheckListener listener : listeners) {
            listener.onRadioChecked();
        }
    }

    public static Config readConfig(String path){
        Config res = null;
        try {
            XMLAPI.setXmlBeanScanPackage("com.fengmi.usertest.bean");
            res = (Config) XMLAPI.readXML(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return res;
    }

    public static boolean writeConfig(Config config,String path){
        boolean res = false;
        try {
            XMLAPI.writeObj2Xml(config,path);
            res = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return res;
    }
}
