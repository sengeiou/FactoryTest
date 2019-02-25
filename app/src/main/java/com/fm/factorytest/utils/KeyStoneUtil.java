package com.fm.factorytest.utils;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.Log;

import mitv.keystone.IKeystoneService;
import mitv.keystone.KeystoneManager;
import mitv.keystone.KeystonePoint;

public class KeyStoneUtil {
    private static final String TAG = "KeyStoneUtil";
    private static IKeystoneService keystoneService = null;

    private static IKeystoneService getKeyStoneService() {

        IBinder service = ServiceManager.getService("KeystoneCorrect");
        if (service != null) {
            keystoneService = IKeystoneService.Stub.asInterface(service);
        }
        return keystoneService;
    }

    public static boolean setKeyStoneMode(String param) throws RemoteException {
        if (keystoneService == null) {
            keystoneService = getKeyStoneService();
        }
        int res = -1;
        if (param.equals("0")) {
            //enable keystone mode
            keystoneService.SetKeystoneInit();
            res = keystoneService.SetKeystoneSelectMode(KeystoneManager.KEYSTONE_8_POINTS_MODE);
        }
        if (param.equals("1")) {
            res = keystoneService.SetKeystoneReset();
        }
        Log.d(TAG, "0 is ON param=" + param + " res=" + res);
        return res == 0;
    }

    public static boolean setKeyStoneDirect(String param) throws RemoteException {
        if (keystoneService == null) {
            keystoneService = getKeyStoneService();
        }
        if (keystoneService == null) {
            return false;
        }
        String[] datas = param.split(",");
        if (datas.length != 3) {
            return false;
        }
        for (String data : datas) {
            if (!TextUtils.isDigitsOnly(data)) {
                return false;
            }
        }
        int res = keystoneService.SetKeystoneSet(
                Integer.parseInt(datas[0]),
                Integer.parseInt(datas[1]),
                Integer.parseInt(datas[2])
        );
        keystoneService.SetKeystoneLoad();

        KeystonePoint[] points = keystoneService.GetKeystoneSets();
        for (KeystonePoint point : points) {
            Log.d(TAG, "KeystonePoint: " + point.toString());
        }

        Log.d(TAG, "SetKeystoneSet " + res);
        return true;
    }

    public static boolean setKeyStoneSets() throws RemoteException {
        if (keystoneService == null) {
            keystoneService = getKeyStoneService();
        }
        if (keystoneService == null) {
            return false;
        }
        KeystonePoint[] points = keystoneService.GetKeystoneSets();
        if (points.length != 8) {
            return false;
        }
        for (KeystonePoint point : points) {
            Log.d(TAG, "origin KeystonePoint: " + point.toString());
        }
        /*
        P0########P1###########P2
        #######################
        P3#####################P4
        #######################
        P5########P6###########P7
         */
        //P1 形变
        points[1].y = 200;
        //P3 形变
        points[3].x = 100;
        //P4 形变
        points[4].x = 3640;
        //P6 形变
        points[6].y = 2000;

        int res = keystoneService.SetKeystoneSets(points);

        keystoneService.SetKeystoneLoad();

        for (KeystonePoint point : points) {
            Log.d(TAG, "new KeystonePoint: " + point.toString());
        }

        return res == 0;
    }
}
