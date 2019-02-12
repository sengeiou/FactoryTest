package com.fengmi.usertest;

import android.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class PQUtil {
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
}
