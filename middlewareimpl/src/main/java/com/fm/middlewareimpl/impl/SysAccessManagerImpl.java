package com.fm.middlewareimpl.impl;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.droidlogic.app.SystemControlManager;
import com.fm.middlewareimpl.global.ShellUtil;
import com.fm.middlewareimpl.interf.SysAccessManagerAbs;

import mitv.keystone.IKeystoneService;
import mitv.keystone.KeystoneManager;
import mitv.keystone.KeystonePoint;
import mitv.tv.TvContext;

public class SysAccessManagerImpl extends SysAccessManagerAbs {

    private String[] IMAGE_MODES = null;
    private SystemControlManager mControllManager;
    private DLPCmd dlmCMD;

    private AsyncTask<Void, Void, Boolean> mSaveTask = null;
    private Projector_Sensor ps = null;
    private Context mCtx;

    private KeystoneManager keystoneManager;

    public SysAccessManagerImpl(Context context) {
        super(context);
        this.mControllManager = new SystemControlManager(context);
        this.dlmCMD = DLPCmd.initDLPCmd(Build.DEVICE);
        this.IMAGE_MODES = dlmCMD.getImageModes();
        keystoneManager = TvContext.getInstance().getKeystoneManager();
        mCtx = context;
    }

    public boolean screenCheck(int mode) {
        boolean ret = false;
        ret = doCheckScreen(mode);
        return ret;
    }

    public boolean enableScreenCheck(String param) {
        boolean ret = false;
        ret = enableCheckScreen(param);
        return ret;
    }

    public boolean syncDlpInfo() {
        try {
            ShellUtil.execCommand("dlp_metadata --sync");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean saveDlpInfo() {
        try {
            return startSaveProgress();
        } catch (Exception e) {
            return false;
        }

    }

    public boolean setWheelDelay(int delay) {
        mControllManager.writeSysFs("/sys/class/projector/laser-projector/laser_seq", String.valueOf(delay));
        return true;
    }

    public int getWheelDelay() {
        try {
            String value = ShellUtil.execCommand("cat /sys/class/projector/laser-projector/laser_seq");
            int v = ShellUtil.formatDelayNumber(value);
            return v;
        } catch (Exception e) {
            e.printStackTrace();
            return 1000;
        }

    }

    public boolean enableXPRCheck(String param) {
        if ("0".equals(param)) {
            mControllManager.writeSysFs(dlmCMD.getImageCMDPath(), dlmCMD.getXPRCheckInitCmd(true));
        } else {
            mControllManager.writeSysFs(dlmCMD.getImageCMDPath(), dlmCMD.getXPRCheckInitCmd(false));
        }
        return true;
    }

    public boolean enableXPRShake(String param) {
        if ("0".equals(param)) {
            mControllManager.writeSysFs(dlmCMD.getImageCMDPath(), dlmCMD.getXPRShakeCmd(true));
        } else {
            mControllManager.writeSysFs(dlmCMD.getImageCMDPath(), dlmCMD.getXPRShakeCmd(false));
        }
        return true;
    }

    public boolean checkGSensorFunc() {
        if (ps == null) {
            ps = new Projector_Sensor(mCtx);
        }
        Log.i("GSensor", "gSensor startCollect data ");
        if (ps.isCompleted()) {
            //float[] res = ps.getSensorResult();
            //return Arrays.toString(res);
            return ps.getSensorResult();
        }
        return false;
    }

    public boolean startGSensorCollect() {
        if (ps == null) {
            ps = new Projector_Sensor(mCtx);
        }
        ps.startCollect();
        return true;
    }

    public boolean saveGSensorStandard() {
        if (ps == null) {
            ps = new Projector_Sensor(mCtx);
        }
        return ps.saveStandardData();
    }

    public String readGSensorStandard() {
        if (ps == null) {
            ps = new Projector_Sensor(mCtx);
        }
        return ps.readGsensorData();
    }

    public String readGSensorHorizontalData() {
        if (ps == null) {
            ps = new Projector_Sensor(mCtx);
        }
        return ps.readHorizontal();
    }

    public String readDLPVersion() {
        return getDlpVersion();
    }

    @Override
    public boolean setKeyStoneMode(String param) {
        int res = -1;
        if (param.equals("0")) {
            //enable keystone mode
            keystoneManager.SetKeystoneInit();
            res = keystoneManager.SetKeystoneSelectMode(KeystoneManager.KEYSTONE_8_POINTS_MODE);
        }
        if (param.equals("1")) {
            res = keystoneManager.SetKeystoneReset();
        }
        Log.d(TAG, "0 is ON param=" + param + " res=" + res);
        return res == 0;
    }

    @Override
    public boolean setKeyStoneDirect(String param) {
        String[] datas = param.split(",");
        if (datas.length != 3) {
            return false;
        }
        for (String data : datas) {
            if (!TextUtils.isDigitsOnly(data)) {
                return false;
            }
        }
        int res = keystoneManager.SetKeystoneSet(
                Integer.parseInt(datas[0]),
                Integer.parseInt(datas[1]),
                Integer.parseInt(datas[2])
        );
        keystoneManager.SetKeystoneLoad();

        KeystonePoint[] points = keystoneManager.GetKeystoneSets();
        for (KeystonePoint point : points) {
            Log.d(TAG, "KeystonePoint: " + point.toString());
        }

        Log.d(TAG, "SetKeystoneSet " + res);
        return true;
    }

//========================================

    public boolean startSaveProgress() {
        if (mSaveTask != null) {
            return false;
        }
        mSaveTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    String execCommand = ShellUtil.execCommand("cat /sys/class/projector/laser-projector/laser_seq");
                    int value = ShellUtil.formatDelayNumber(execCommand);
                    Log.i("IMPL_SYSACC", "laser_seq is " + value);
                    ShellUtil.execCommand("dlp_metadata --set dlp_index_delay " + value);
                    ShellUtil.execCommand("dlp_metadata --save");
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                mSaveTask = null;
            }

        };
        mSaveTask.execute();
        return true;
    }


    public boolean doCheckScreen(int mode) {
        if (mode < 0 || mode >= IMAGE_MODES.length) {
            return false;
        }
        mControllManager.writeSysFs(dlmCMD.getImageCMDPath(), IMAGE_MODES[mode]);
        return true;
    }

    private boolean enableCheckScreen(String param) {
        if ("0".equals(param)) {
            mControllManager.writeSysFs(dlmCMD.getImageCMDPath(), dlmCMD.getScreenCheckInitCmd(true));
        } else {
            mControllManager.writeSysFs(dlmCMD.getImageCMDPath(), dlmCMD.getScreenCheckInitCmd(false));
        }
        return true;
    }

    private String getDlpVersion() {
        String version = "";
        mControllManager.writeSysFs("/sys/class/projector/led-projector/i2c_read", "d9 4");
        version = mControllManager.readSysFs("/sys/class/projector/led-projector/i2c_read");
        Log.i("DlpVersion", "read original dlp version info :: " + version);
        if (!version.trim().equals("")) {
            if (version.contains("echo")) {
                version = version.split("echo")[0];
            } else {
                version = version.substring(0, 8);
            }
        }
        Log.i("DlpVersion", "read dlp version info :: " + version);
        return version;
    }
}

