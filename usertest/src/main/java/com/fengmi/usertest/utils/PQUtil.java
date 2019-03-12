package com.fengmi.usertest.utils;

import android.content.Context;
import android.util.Log;

import com.fengmi.usertest.PicModeManagerImpl;

public final class PQUtil {
    public static final int R_GAIN = 0;
    public static final int G_GAIN = 1;
    public static final int B_GAIN = 2;
    public static final int R_OFF = 10;
    public static final int G_OFF = 11;
    public static final int B_OFF = 12;
    private static final String TAG = "PQUtil";
    private static PicModeManagerImpl picModeManager;

    public static void init(Context context) {
        if (picModeManager == null) {
            picModeManager = new PicModeManagerImpl(context);
        }
    }

    public static void setColorTemp(int colorTemp) {
        if (picModeManager != null) {
            picModeManager.picSetColorTemp(colorTemp);
        }
    }

    public static void updatePQValue(int pqType, boolean add) {
        if (picModeManager != null) {
            if (add) {
                pqIncrease(pqType);
            } else {
                pqDecrease(pqType);
            }
        }
    }

    private static void pqIncrease(int pqType) {
        int val = queryPQValue(pqType);
        val += Util.PQ_ADJUST_STEP;
        Log.d(TAG, "pqIncrease :: " + val);
        setPQValue(pqType, val);
    }

    private static void pqDecrease(int pqType) {
        int val = queryPQValue(pqType);
        val -= Util.PQ_ADJUST_STEP;
        Log.d(TAG, "pqDecrease :: " + val);
        setPQValue(pqType, val);
    }

    private static int queryPQValue(int type) {
        int val = -1;
        int colorTemp = picModeManager.picGetColorTemp();
        Log.d(TAG, "picGetColorTemp :: " + colorTemp);
        switch (type) {
            case R_GAIN:
                val = picModeManager.picGetPostRedGain(colorTemp);
                break;
            case G_GAIN:
                val = picModeManager.picGetPostGreenGain(colorTemp);
                break;
            case B_GAIN:
                val = picModeManager.picGetPostBlueGain(colorTemp);
                break;
            case R_OFF:
                val = picModeManager.picGetPostRedOffset(colorTemp);
                break;
            case G_OFF:
                val = picModeManager.picGetPostGreenOffset(colorTemp);
                break;
            case B_OFF:
                val = picModeManager.picGetPostBlueOffset(colorTemp);
                break;
        }
        return val;
    }

    private static boolean setPQValue(int type, int data) {
        int colorTemp = picModeManager.picGetColorTemp();
        Log.d(TAG, "picGetColorTemp :: " + colorTemp);
        boolean val = false;
        switch (type) {
            case R_GAIN:
                val = picModeManager.setRedGain(colorTemp, data);
                break;
            case G_GAIN:
                val = picModeManager.setGreenGain(colorTemp, data);
                break;
            case B_GAIN:
                val = picModeManager.setBlueGain(colorTemp, data);
                break;
            case R_OFF:
                val = picModeManager.setRedOffs(colorTemp, data - 1024);
                break;
            case G_OFF:
                val = picModeManager.setGreenOffs(colorTemp, data - 1024);
                break;
            case B_OFF:
                val = picModeManager.setBlueOffs(colorTemp, data - 1024);
                break;
        }
        return val;
    }

    public static void resetPQ() {
        for (int i = 0; i < 3; i++) {
            setColorTemp(i);
            setPQValue(R_GAIN, 1024);
            setPQValue(B_GAIN, 1024);
            setPQValue(R_OFF, 1024);
            setPQValue(B_OFF, 1024);
        }
    }
}
