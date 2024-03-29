package com.fm.middlewareimpl.impl_home;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.droidlogic.app.SystemControlManager;
import com.fm.middlewareimpl.global.ShellUtil;
import com.fm.middlewareimpl.interf.SysAccessManagerAbs;

import mitv.internal.TvUtils;
import mitv.keystone.KeystoneManager;
import mitv.keystone.KeystonePoint;
import mitv.projector.IProjectorService;
import mitv.tv.TvContext;

public class SysAccessManagerImpl extends SysAccessManagerAbs {

    private String[] IMAGE_MODES = null;
    private SystemControlManager mControllManager;
    private DLPCmd dlmCMD;

    private AsyncTask<Void, Void, Boolean> mSaveTask = null;
    private Projector_Sensor ps = null;
    private Context mCtx;

    private KeystoneManager keystoneManager;

    private IProjectorService projectorService;

    public SysAccessManagerImpl(Context context) {
        super(context);
        this.mControllManager = new SystemControlManager(context);
        this.dlmCMD = DLPCmd.initDLPCmd(Build.DEVICE);
        this.IMAGE_MODES = dlmCMD.getImageModes();
        keystoneManager = TvContext.getInstance().getKeystoneManager();
        mCtx = context;

        IBinder projectorBinder = TvUtils.getAccessoryService(TvUtils.PROJECTOR_SERVICE_NAME);
        projectorService = IProjectorService.Stub.asInterface(projectorBinder);
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
    private static final String[] projector_type = new String[]{
            "/sys/class/projector/laser-projector/",
            "/sys/class/projector/led-projector/",
    };
    private static final String[] projector_node = new String[]{
            "serial_write",
            "projector-0-temp",
            "projector-1-temp",
            "projector-2-temp",
            "i2c_read",
            "i2c_write",
            "i2c_busy",
            "fan1_control",
            "fan2_control",
            "fan3_control",
            "fan_level",
            "dlp_brightness",
            "dlp_status",
            "look_select",
    };
    @Override
    public boolean writeProjectorCMD(String param) {
        if(param == null){
            return false;
        }
        Log.d(TAG,"write projector CMD : "+param);
        String[] ss = param.split(",");
        //判断格式是否符合
        if (ss.length==3 && TextUtils.isDigitsOnly(ss[0]) && TextUtils.isDigitsOnly(ss[1])){
            int prefix = Integer.parseInt(ss[0]);
            int suffix = Integer.parseInt(ss[1]);
            if (prefix < projector_type.length && suffix < projector_node.length){
                //向指定节点写入数据
                return mControllManager.writeSysFs(projector_type[prefix]+projector_node[suffix],ss[2]);
            }
            return false;
        }
        return false;
    }

    @Override
    public String readProjectorCMD(String param) {
        if(param == null){
            return "error";
        }
        Log.d(TAG,"read projector CMD : "+param);
        String[] ss = param.split(",");
        //判断格式是否符合
        if (ss.length==2 && TextUtils.isDigitsOnly(ss[0]) && TextUtils.isDigitsOnly(ss[1])){
            int prefix = Integer.parseInt(ss[0]);
            int suffix = Integer.parseInt(ss[1]);
            if (prefix < projector_type.length && suffix < projector_node.length){
                //向指定节点写入数据
                return mControllManager.readSysFs(projector_type[prefix]+projector_node[suffix]);
            }
            return "error";
        }
        return "error";
    }

    private static final String[] i2c_prefix = new String[]{
            "/sys/class/i2c1/",
            "/sys/class/i2c2/",
            "/sys/class/i2c3/",
    };
    private static final String[] i2c_suffix = new String[]{
            "debug",
            "mode",
            "slave",
            "speed",
            "trig_gpio",
    };
    @Override
    public boolean writeI2CCMD(String param) {
        if(param == null){
            return false;
        }
        Log.d(TAG,"write I2C CMD : "+param);
        String[] ss = param.split(",");
        //判断格式是否符合
        if (ss.length==3 && TextUtils.isDigitsOnly(ss[0]) && TextUtils.isDigitsOnly(ss[1])){
            int prefix = Integer.parseInt(ss[0]);
            int suffix = Integer.parseInt(ss[1]);
            if (prefix < i2c_prefix.length && suffix < i2c_suffix.length){
                //向指定节点写入数据
                return mControllManager.writeSysFs(i2c_prefix[prefix]+i2c_suffix[suffix],ss[2]);
            }
            return false;
        }
        return false;
    }

    @Override
    public String readI2CCMD(String param) {
        if(param == null){
            return "error";
        }
        Log.d(TAG,"read I2C CMD : "+param);
        String[] ss = param.split(",");
        //判断格式是否符合
        if (ss.length==2 && TextUtils.isDigitsOnly(ss[0]) && TextUtils.isDigitsOnly(ss[1])){
            int prefix = Integer.parseInt(ss[0]);
            int suffix = Integer.parseInt(ss[1]);
            if (prefix < i2c_prefix.length && suffix < i2c_suffix.length){
                //向指定节点写入数据
                return mControllManager.readSysFs(i2c_prefix[prefix]+i2c_suffix[suffix]);
            }
            return "error";
        }
        return "error";
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
        String version;
        if (projectorService != null){
            try {
                version = projectorService.GetProjectorInfo().GetFlashBuildVersion();
                String[] vers = version.split(",");
                String[] vs = new String[3];
                for (String ver : vers) {
                    if (ver.contains("major: ")){
                        vs[0] = (ver.replace("major: ","").trim());
                    }
                    if (ver.contains("minor: ")){
                        vs[1] = (ver.replace("minor: ","").trim());
                    }
                    if (ver.contains("patch: ")){
                        vs[2] = (ver.replace("patch: ","").trim());
                    }
                }
                version =vs[0]+"."+vs[1]+"."+vs[2];
            } catch (RemoteException e) {
                e.printStackTrace();
                version = "read error";
            }

        }else {
            mControllManager.writeSysFs("/sys/class/projector/led-projector/i2c_read", "d9 4");
            version = mControllManager.readSysFs("/sys/class/projector/led-projector/i2c_read");
            Log.i(TAG, "read original dlp version info :: " + version);
            if (!version.trim().equals("")) {
                if (version.contains("echo")) {
                    version = version.split("echo")[0];
                } else {
                    version = version.substring(0, 8);
                }
            }
            Log.i("DlpVersion", "read dlp version info :: " + version);
        }
        return version;
    }
}

