package com.fengmi.usertest.utils;

import android.annotation.NonNull;

import com.fengmi.usertest.IRadioCheckListener;
import com.fengmi.usertest.bean.Config;

import java.io.File;
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

    public static Object readConfig(String path){
        Object res = null;
        try {
            res = XMLAPI.readXML(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    public static boolean writeConfig(Config config,String path){

        boolean res = false;
        try {
            File file = new File(path);
            if (!file.exists()){
                file.createNewFile();
            }
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
