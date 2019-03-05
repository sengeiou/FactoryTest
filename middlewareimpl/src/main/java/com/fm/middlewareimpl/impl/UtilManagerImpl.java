/*
 * Copyright (C) 2013 XiaoMi Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fm.middlewareimpl.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;

import com.droidlogic.app.KeyManager;
import com.droidlogic.app.SystemControlManager;
import com.droidlogic.app.tv.TvControlManager;
import com.fm.middlewareimpl.global.SettingManager;
import com.fm.middlewareimpl.interf.UtilManagerAbs;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;

import mitv.motor.MotorManager;
import mitv.powermanagement.ScreenSaverManager;
import mitv.projector.ProjectorManager;
import mitv.tv.TvContext;

//amlogic API
//xiaomi API
//MD5

public class UtilManagerImpl extends UtilManagerAbs {
    private static final String TAG = "IMPL_Util";
    private SettingManager mSetManager;
    private LocalPropertyManagerImpl mLPropManager;
    private LedFlashThread mLedFlashThread;
    private String mProductModel;
    //amlogic API
    private TvControlManager mTvControlManager;
    private KeyManager mKeyManager;
    private SystemControlManager mSystemControl;
    private Handler mHandler = new Handler();
    private ProjectorManager mProjectorManager;

    public UtilManagerImpl(Context context) {
        super(context);
        mSetManager = new SettingManager();
        mLPropManager = new LocalPropertyManagerImpl(context);
        mProductModel = getProductModel();
        //amlogic API
        mTvControlManager = TvControlManager.getInstance();
        mKeyManager = new KeyManager(context);
        mKeyManager.aml_init_unifykeys();
        mSystemControl = new SystemControlManager(context);
        try {
            TvContext instance = TvContext.getInstance();
            mProjectorManager = instance.getProjectorManager();
        } catch (Exception e) {
            Log.e(TAG, "getProjectorManager error");
            e.printStackTrace();
        }
    }

    public int[] getKeyTestSequence() {
        int[] ret = null;
        ret = keyTestSequence();
        return ret;
    }

    public int lightSensorGetValue() {
        int ret = -1;
        ret = readLightSensorValue();
        return ret;
    }

    public boolean systemReset() {
        boolean ret = false;
        ret = resetSystem();
        return ret;
    }

    public boolean touchpadSetStatus() {
        boolean ret = false;
        return ret;
    }

    public boolean remoteControlSetLock(String lock) {
        boolean ret = false;
        ret = remoteLockController(lock);
        return ret;
    }

    public boolean systemSleepSetStatus() {
        boolean ret = false;
        return ret;
    }

    public boolean checkSystemPartition() {
        boolean ret = false;
        ret = checkPartition();
        return ret;
    }

    public boolean setLedLightStat(String style) {
        boolean ret = false;
        ret = LedController(style);
        return ret;
    }

    public boolean ledStartFlash(int period) {
        boolean ret = false;
        ret = ledFlashStart(period);
        return ret;
    }

    public boolean ledStopFlash() {
        boolean ret = false;
        ret = ledFlashStop();
        return ret;
    }

    public boolean systemSwitchMode() {
        boolean ret = false;
        ret = switchSystem();
        return ret;
    }

    public boolean systemSwitchModeNew() {
        boolean ret = false;
        ret = switchSystemNew();
        return ret;
    }

    public boolean systemModeSet() {
        boolean ret = false;
        ret = setSystemMode();
        return ret;
    }

    public boolean systemModeGet() {
        boolean ret = false;
        ret = getSystemMode();
        return ret;
    }

    public boolean systemReboot() {
        boolean ret = false;
        ret = rebootSystem();
        return ret;
    }

    public boolean systemRebootRecovery() {
        boolean ret = false;
        ret = rebootRecovery();
        return ret;
    }

    public boolean systemShutdown() {
        boolean ret = false;
        ret = shutdownSystem();
        return ret;
    }

    public boolean systemMasterClear() {
        boolean ret = false;
        ret = systemClearAllShutdown();
        return ret;
    }

    //call it in commandservice's oncreate
    public boolean systemUpdateBootTimes() {
        boolean ret = false;
        ret = updateBootTimes();
        return ret;
    }

    public int systemGetBootTimes() {
        int ret = -1;
        ret = getBootTimes();
        return ret;
    }

    // public boolean setProductRegion(String region) {
    //     boolean ret = false;
    //     ret = setRegion(region);
    //     return ret;
    // }
    //
    // public String getProductRegion() {
    //     String ret = null;
    //     ret = getRegion();
    //     return ret;
    // }

    public boolean sleepSystem() {
        boolean ret = false;
        ret = sleepTv();
        return ret;
    }

    public boolean resetTvPanelSelect() {
        boolean ret = false;
        ret = panelSelect();
        return ret;
    }

    public boolean setBtRcMac(String mac) {
        boolean ret = false;
        ret = writeBtRcAddr(mac);
        return ret;
    }

    public String getBtRcMac() {
        String ret = null;
        ret = readBtRcAddr();
        return ret;
    }

    public void bootupSystemDirect() {
        bootDirect();
    }

    public String getProductModel() {
        String ret = null;
        ret = getModel();
        return ret;
    }

    public boolean closeScreenSave2Sleep() {
        boolean ret = false;
        ret = disableScreenSaver2Sleep();
        return ret;
    }

    public boolean setApplicationRid() {
        boolean ret = false;
        ret = locSetApplicationRid();
        return ret;
    }

    public boolean getSubWooferStat() {
        boolean ret = false;
        ret = subWooferPlugInStat();
        return ret;
    }

    public boolean setPowerStandbyStat(String stat) {
        boolean ret = false;
        ret = setPowerStandby(stat);
        return ret;
    }

    public boolean setI2CPinStat(String stat) {
        boolean ret = false;
        //ret = setI2CPin(stat);
        return ret;
    }

    //empty function for compatible box
    public String getCpuTemp() {
        String ret = null;
        return ret;
    }

    public boolean setFanStat(String speed) {
        boolean ret = false;
        return ret;
    }

    public boolean setGpioOut(String portAstat) {
        boolean ret = false;
        ret = setGpioStat(portAstat);
        return ret;
    }

    public int getGpioInStat(String port) {
        int ret = -1;
        ret = getGpioInValue(port);
        return ret;
    }

    public String checkPanelIdTag() {
        String ret = null;
        ret = readPanelIdTag();
        return ret;
    }

    public boolean setVcom(String para) {
        boolean ret = false;
        ret = writeVcom(para);
        return ret;
    }

    public byte getVcom(String para) {
        byte ret = (byte) 0xff;
        ret = readVcom(para);
        return ret;
    }

    public String readRomTotalSpace() {
        return readRomSpace("T");
    }

    public String readRomAvailSpace() {
        return readRomSpace("A");
    }

    public boolean setFanSpeed(String level) {
        return changeFanSpeed(level);
    }

    public boolean resetLEDStepMotor() {
        return resetStepMotor();
    }

    public String readLEDTemperature(String param) {
        return readLedTemperature(param);
    }

    public String readRGBLEDCurrent() {
        return readRGBCurrent();
    }

    /*===========================================local functions=====================*/
    private String readRGBCurrent() {
        String res = "";
        res = mSystemControl.readSysFs("/sys/class/projector/led-projector/rgb_led_current");
        Log.e(TAG, "read RGB LED CURRENT : " + res);
        return res;
    }

    private String readLedTemperature(String param) {
        String temp;
        switch (param) {
            case "0":
                temp = mSystemControl.readSysFs("/sys/class/projector/led-projector/projector-0-temp/temp");
                break;
            case "1":
                temp = mSystemControl.readSysFs("/sys/class/projector/led-projector/projector-1-temp/temp");
                break;
            case "2":
                temp = mSystemControl.readSysFs("/sys/class/projector/led-projector/projector-2-temp/temp");
                break;
            default:
                temp = "error";
                break;
        }
        return temp;
    }

    private boolean resetStepMotor() {
        return echoEntry("/sys/class/vgsm2028/vgsm2028/proj_motor_calibration", "1");
    }

    private boolean changeFanSpeed(String level) {
        String fanLevel;
        switch (level) {
            case "0":
                //min
                fanLevel = "0";
                break;
            case "1":
                //mid
                fanLevel = "7";
                break;
            case "2":
                //max
                fanLevel = "15";
                break;
            default:
                fanLevel = "0";
                break;

        }
        return echoEntry("/sys/class/projector/led-projector/fan1_control", fanLevel);
    }

    private String readRomSpace(String type) {
        Log.e(TAG, "Read Rom Space type : " + type);
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long blockCount;
        String romSpace = "Error";

        if ("T".equals(type)) {
            blockCount = stat.getBlockCountLong();
            romSpace = Formatter.formatFileSize(context, blockCount * blockSize);
        }
        if ("A".equals(type)) {
            blockCount = stat.getAvailableBlocksLong();
            romSpace = Formatter.formatFileSize(context, blockCount * blockSize);
        }
        Log.e(TAG, "Read Rom Space size : " + type);
        return romSpace;
    }

    private boolean writeVcom(String para) {
        boolean ret = false;
        Log.i(TAG, "set Vcom command");
        //mSystemControl.writeSysFs("sys/class/gpio/gpio199/value","1");
        //SystemClock.sleep(100);
        byte command_byte[] = toHexString(para).getBytes();
        try {
            FileOutputStream fstream = new FileOutputStream("/sys/class/i2c/slave");
            fstream.write(command_byte, 0, command_byte.length - 1);
            //fstream.getFD().sync();
            fstream.close();
            Log.e(TAG, "set Vcom command: OK");
            ret = true;
        } catch (IOException e) {
            Log.e(TAG, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            FileOutputStream fstream = new FileOutputStream("/sys/class/i2c1/mi_slave");
            fstream.write(command_byte, 0, command_byte.length - 1);
            //fstream.getFD().sync();
            fstream.close();
            Log.e(TAG, "set Vcom command: OK");
            ret = true;
        } catch (IOException e) {
            Log.e(TAG, "Error: " + e.getMessage());
            e.printStackTrace();
        }

        //SystemClock.sleep(100);
        //mSystemControl.writeSysFs("sys/class/gpio/gpio199/value","0");
        return ret;
    }

    private byte readVcom(String para) {
        boolean ret1 = false;
        byte ret = (byte) 0xff;
        String value = "";
        Log.i(TAG, "get vcom data from i2c");
        ret1 = writeVcom(para);
        if (ret1) {
            value = mSystemControl.readSysFs("/sys/class/i2c/slave");
            String buf = value.substring(2, 4);
            ret = (byte) Integer.parseInt(buf, 16);
        }
        Log.i(TAG, "get vcom data from i2c : " + ret);
        if (ret == 0) {
            value = mSystemControl.readSysFs("/sys/class/i2c1/mi_slave");
            String buf = value.substring(2, 4);
            ret = (byte) Integer.parseInt(buf, 16);
        }
        Log.i(TAG, "get vcom data from i2c : " + ret);
        return ret;
    }

    private int readLightSensorValue() {
        Process proc = null;
        long lastTime = SystemClock.uptimeMillis();
        long currentTime = 0;
        boolean captureTimes = true;
        int ret = -1;
        Log.i(TAG, "get Light Sensor Result");
        try {
            String msg;
            Runtime rt = Runtime.getRuntime();
            proc = rt.exec("/system/bin/getals 2");
            proc.waitFor();
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while (captureTimes) {
                msg = in.readLine();
                if (msg.contains("lux")) {
                    String[] buf = msg.split("= ");
                    ret = Integer.parseInt(buf[1]);
                    //if valid capture, switch the flag and jump out cycle.
                    captureTimes = false;
                }
                currentTime = SystemClock.uptimeMillis();
                if ((currentTime - lastTime) > 4000) {
                    break;
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private boolean rebootSystem() {
        Log.i(TAG, "reboot system");
        //mContext.sendBroadcast(new Intent(mSetManager.INTENT_REBOOTSYS));

        Process proc = null;
        try {
            String cmd = "reboot";
            proc = java.lang.Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean rebootRecovery() {
        Process proc = null;
        try {
            String cmd = "reboot recovery";
            proc = java.lang.Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean resetSystem() {
        Log.i(TAG, "reset system (master clear)");
        SystemProperties.set(mSetManager.SYSPROP_CONSOLEDISABLE, "1");
        SystemProperties.set(mSetManager.SYSPROP_FACTORYPOWERMODE, POWER_HOLD);
        while (!SystemProperties.get(mSetManager.SYSPROP_CONSOLEDISABLE).equals("1") ||
                !SystemProperties.get(mSetManager.SYSPROP_FACTORYPOWERMODE).equals(POWER_HOLD)) {
            SystemClock.sleep(100);
        }
        Intent resetIntent = new Intent(mSetManager.INTENT_MASTERCLEARSYS);
        //resetIntent.putExtra("com.xiaomi.tv.WIPE_INSTALLED_APPS", true);
        context.sendBroadcast(resetIntent);
        return true;
    }

    private boolean resetSystem_clearAll() {
        Log.i(TAG, "reset system (master clear)");
        Intent resetIntent = new Intent(mSetManager.INTENT_MASTERCLEARSYS);
        resetIntent.putExtra("com.xiaomi.tv.WIPE_INSTALLED_APPS", true);
        context.sendBroadcast(resetIntent);
        return true;
    }

    //amlogic tv
    private final static String switchCmd =
            "--locale=zh_CN\n--restore\n--fact2user";
    private final static String switchCmdPath = "/cache/recovery/command";

    private boolean switchsys() {
        Log.i(TAG, "reset system (master clear), and restore user system from backup partition");
        echoEntry(switchCmdPath, switchCmd);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        pm.reboot("recovery");
        Log.i(TAG, "switch system end");
        return true;
    }

    //bootloader路径：
    // /dev/block/bootloader
    // /dev/block/mmcblk0boot0
    // /dev/block/mmcblk0boot1
    //dtb路径：
    // /dev/dtb (/dev/block/mmcblk0 offset=40M)
    //boot.img路径：
    // /dev/block/boot
    //logo路径：
    // /dev/block/logo
    //switchtouser
    //   /data/md5sum.txt
    //bootloader 851968 d80259f2f0bb8203697b4228ae42ea8e
    //dtb.img 100352 de96c30b017f94e0298ced3c6c9a5e99
    //boot.img 8697856 aa73bae4e38e61f2189fb09ce9dd273b
    //logo.img 480192 175983193c9feb1a20a341a4d153d85f
    private boolean switchsysNew() {
        Log.i(TAG, "switch remove system1(factory) switch to system0(user), and check partition md5");
        //1,use swithtouser(root)
        String cmd = "misysdiagnose:-s switchtouser";
        SystemProperties.set("ctl.start", cmd);
        SystemClock.sleep(1000);

        //2,check md5(bootloader/dtb.img/boot.img/logo.img)
        String propPath = "/data/md5sum.txt";
        java.io.BufferedReader br = null;
        String bootloader_size = "";
        String bootloader_md5 = "";
        String dtbimg_size = "";
        String dtbimg_md5 = "";
        String bootimg_size = "";
        String bootimg_md5 = "";
        String logoimg_size = "";
        String logoimg_md5 = "";
        try {
            br = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(propPath)), 512);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.contains("bootloader")) {
                    String srcs1[] = line.split(" ");
                    bootloader_size = srcs1[1];
                    bootloader_md5 = srcs1[2];
                } else if (line.contains("dtb.img")) {
                    String srcs1[] = line.split(" ");
                    dtbimg_size = srcs1[1];
                    dtbimg_md5 = srcs1[2];
                } else if (line.contains("boot.img")) {
                    String srcs1[] = line.split(" ");
                    bootimg_size = srcs1[1];
                    bootimg_md5 = srcs1[2];
                } else if (line.contains("logo.img")) {
                    String srcs1[] = line.split(" ");
                    logoimg_size = srcs1[1];
                    logoimg_md5 = srcs1[2];
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "read data exception for " + propPath, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
            }
        }
        Log.i(TAG, "bootloader_md5 :" + bootloader_md5);
        Log.i(TAG, "dtbimg_md5 :" + dtbimg_md5);
        Log.i(TAG, "bootimg_md5 :" + bootimg_md5);
        Log.i(TAG, "logoimg_md5 :" + logoimg_md5);
        Log.i(TAG, "bootloader_size :" + bootloader_size);
        Log.i(TAG, "dtbimg_size :" + dtbimg_size);
        Log.i(TAG, "bootimg_size :" + bootimg_size);
        Log.i(TAG, "logoimg_size :" + logoimg_size);

        if (!bootloader_md5.equals(string2MD5(getByte("/dev/block/bootloader", Integer.parseInt(bootloader_size), 512))) ||
                !bootloader_md5.equals(string2MD5(getByte("/dev/block/mmcblk0boot0", Integer.parseInt(bootloader_size), 512))) ||
                !bootloader_md5.equals(string2MD5(getByte("/dev/block/mmcblk0boot1", Integer.parseInt(bootloader_size), 512)))) {
            Log.i(TAG, "check bootloader failed");
            return false;
        }
        if (!dtbimg_md5.equals(string2MD5(getByte("/dev/block/mmcblk0", Integer.parseInt(dtbimg_size), 41943040)))) {
            Log.i(TAG, "check dtbimg failed");
            return false;
        }
        if (!bootimg_md5.equals(string2MD5(getByte("/dev/block/boot", Integer.parseInt(bootimg_size))))) {
            Log.i(TAG, "check bootimg failed");
            return false;
        }
        if (!logoimg_md5.equals(string2MD5(getByte("/dev/block/logo", Integer.parseInt(logoimg_size))))) {
            Log.i(TAG, "check logoimg failed");
            return false;
        }

        Log.i(TAG, "switch system end");
        return true;
    }

    private byte[] getByte(String path, int len, int offset) {
        byte[] key = new byte[len];
        byte[] temp = new byte[offset];
        Log.i(TAG, "Get byte from file len:" + len + " offset:" + offset);
        Log.i(TAG, "Get byte from file length:" + key.length);
        File keyfile = new File(path);
        if (keyfile.exists()) {
            try {
                FileInputStream fstream = new FileInputStream(keyfile);
                int ret = fstream.read(temp, 0, offset);
                ret = fstream.read(key, 0, len);
                fstream.close();
                Log.e(TAG, "Get Bytes: get " + ret + "bytes ");
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return key;
    }

    private byte[] getByte(String path, int len) {
        byte[] key = new byte[len];
        Log.i(TAG, "Get byte from file length:" + key.length);
        File keyfile = new File(path);
        if (keyfile.exists()) {
            try {
                FileInputStream fstream = new FileInputStream(keyfile);
                int ret = fstream.read(key, 0, len);
                fstream.close();
                Log.e(TAG, "Get Bytes: get " + ret + "bytes ");
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return key;
    }

    /***
     * MD5加码 生成32位md5码
     */
    private String string2MD5(byte[] key) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            return "";
        }

        byte[] md5Bytes = md5.digest(key);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        Log.i(TAG, "string2MD5 :" + hexValue.toString());
        return hexValue.toString();
    }

    private boolean systemClearAllShutdown() {
        Log.i(TAG, "reset system (master clear)");
        //set power mode as secondary
        SystemProperties.set(mSetManager.SYSPROP_FACTORYPOWERMODE, POWER_HOLD);
        //masterclear
        Intent resetIntent = new Intent(mSetManager.INTENT_MASTERCLEARSYS);
        resetIntent.putExtra("com.xiaomi.tv.WIPE_INSTALLED_APPS", true);
        resetIntent.putExtra("shutdown", true);
        context.sendBroadcast(resetIntent);
        return true;
    }

    private static final String POWER_HOLD = "secondary";
    private static final String POWER_ON_DIR = "direct";
    private static final String UNIFYKEY_POWER_MODE = "factory_power_mode";

    private void bootDirect() {
        String stat = null;
        mKeyManager.aml_key_write(UNIFYKEY_POWER_MODE, "direct", 0x0);
        stat = mKeyManager.aml_key_read(UNIFYKEY_POWER_MODE, 0x0);
        Log.i(TAG, "factory power mode : " + stat);
    }

    private boolean writePowerMode() {
        String stat = null;
        String target = null;
        if ("conan".equals(Build.DEVICE)) {
            //conan 需要按键开机
            mKeyManager.aml_key_write(UNIFYKEY_POWER_MODE, POWER_HOLD, 0x0);
            target = POWER_HOLD;
        } else {
            mKeyManager.aml_key_write(UNIFYKEY_POWER_MODE, POWER_ON_DIR, 0x0);
            target = POWER_ON_DIR;
        }
        stat = mKeyManager.aml_key_read(UNIFYKEY_POWER_MODE, 0x0);
        Log.i(TAG, "factory power mode : " + stat);
        return stat.equals(target);
    }

    private boolean shutdownSystem() {
        boolean ret = false;
        if (writePowerMode()){
            ScreenSaverManager manager = ScreenSaverManager.getInstance();
            manager.postSystemShutdownDelayed(0, false);
            ret = true;
        }
        Log.i(TAG, "Set TV sleep (shutdown): " + ret);
        return ret;
    }

    private boolean switchSystem() {
        boolean ret = false;
        Log.i(TAG, "switch system");
        if (writePowerMode()){
            int delay = 1000;
            Log.i(TAG, "reboot after " + delay + "ms");
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Log.i(TAG, "postDelayed switchsys");
                    switchsys();
                }
            }, 1000);
            ret = true;
        }
        return ret;

    }

    private boolean switchSystemNew() {
        boolean ret = false;
        Log.i(TAG, "switch system to user");
        if (writePowerMode()){
            ret = switchsysNew();
        }
        return ret;

    }

    private boolean setSystemMode() {
        //SystemProperties.set(mSetManager.SYSPROP_FACTORYMODE, "0");
        SystemProperties.set(mSetManager.SYSPROP_BOOTSYSTEM, "0");
        SystemProperties.set(mSetManager.SYSPROP_CONSOLEDISABLE, "1");
        SystemProperties.set(mSetManager.SYSPROP_FACTORYPOWERMODE, POWER_HOLD);
        return true;
    }

    private boolean getSystemMode() {
        if (!SystemProperties.get(mSetManager.SYSPROP_FACTORYMODE).equals("0") ||
                !SystemProperties.get(mSetManager.SYSPROP_CONSOLEDISABLE).equals("1") ||
                !SystemProperties.get(mSetManager.SYSPROP_FACTORYPOWERMODE).equals(POWER_HOLD)) {
            return false;
        }
        return true;
    }

    //the nation or area name define is at "readmeForNationAndAreaName" in doc directory
    private static final String[] REGION = {
            "HK", //hong kong
            "CN", //chinese
            "TW", //Tai Wan
            "MO", //Macau
    };

    // private boolean saveRegionProp(java.util.Locale locale) {
    //     String names[] = libcore.icu.TimeZoneNames.forLocale(locale);
    //     if (names == null)
    //         return false;
    //     String propPath = "/persist/factory.prop";
    //     java.io.File file = new java.io.File(propPath);
    //     if (file.exists()) {
    //         file.delete();
    //     }
    //     try {
    //         file.createNewFile();
    //         int perms = android.os.FileUtils.S_IRUSR | android.os.FileUtils.S_IWUSR
    //                 | android.os.FileUtils.S_IRGRP | android.os.FileUtils.S_IWGRP
    //                 | android.os.FileUtils.S_IROTH | android.os.FileUtils.S_IWOTH;
    //         android.os.FileUtils.setPermissions(propPath, perms, -1, -1);
    //     } catch (java.io.IOException e) {
    //         Log.e(TAG, "create Error: " + e.getMessage());
    //         e.printStackTrace();
    //         return false;
    //     }
    //
    //     java.util.Properties prop = new java.util.Properties();
    //     prop.setProperty("persist.sys.language", locale.getLanguage());
    //     prop.setProperty("persist.sys.country", locale.getCountry());
    //     prop.setProperty("persist.sys.timezone", names[0]);
    //     prop.setProperty("ro.mitv.region", locale.getCountry());
    //     prop.setProperty("ro.mitv.region", locale.getCountry());
    //     java.io.FileOutputStream outputFile = null;
    //
    //     try {
    //         outputFile = new java.io.FileOutputStream(propPath);
    //         prop.store(outputFile, "factory region info");
    //         outputFile.getFD().sync();
    //     } catch (java.io.FileNotFoundException e) {
    //         Log.e(TAG, "Error: " + e.getMessage());
    //         return false;
    //     } catch (java.io.IOException e) {
    //         Log.e(TAG, "output Error: " + e.getMessage());
    //         return false;
    //     } finally {
    //         if (outputFile != null) {
    //             try {
    //                 outputFile.close();
    //             } catch (Exception e1) {
    //             }
    //         }
    //     }
    //     return true;
    // }


    // private String getRegion() {
    //     String propPath = "/persist/factory.prop";
    //     java.io.BufferedReader br = null;
    //     String language = "";
    //     String region = "";
    //     try {
    //         br = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(propPath)), 512);
    //         String line = null;
    //         while ((line = br.readLine()) != null) {
    //             if (line.contains("persist.sys.country")) {
    //                 region = line.substring(line.indexOf("=") + 1);
    //             } else if (line.contains("persist.sys.language")) {
    //                 language = line.substring(line.indexOf("=") + 1);
    //             }
    //         }
    //     } catch (Exception e) {
    //         Log.e(TAG, "read data exception for " + propPath, e);
    //     } finally {
    //         if (br != null) {
    //             try {
    //                 br.close();
    //             } catch (Exception e) {
    //             }
    //         }
    //     }
    //     return language + "_" + region;
    // }


    // private boolean setRegion(String inputStr) {
    //     if (inputStr == null) return false;
    //     if (inputStr.length() != 5 || inputStr.indexOf("_") != 2) {
    //         Log.w(TAG, "input string " + inputStr + " not meet our requirements, must be lan_REGION format");
    //         return false;
    //     }
    //     String language = inputStr.substring(0, 2).toLowerCase();
    //     String region = inputStr.substring(3, 5).toUpperCase();
    //     java.util.Locale locale = new java.util.Locale(language, region);
    //     String names[] = libcore.icu.TimeZoneNames.forLocale(locale);
    //     /*
    //     Log.d(TAG,"try to save locale "+locale);
    //     for (String name : names) {
    //         Log.d(TAG," timezone "+name);
    //     }*/
    //     return saveRegionProp(locale);
    // }

    private boolean checkPartition() {
        boolean ret = true;
        if (SystemProperties.get(mSetManager.SYSPROP_PARTITIONCHECK).equals("1")) {
            ret = false;
        }
        Log.i(TAG, "check partition state: " + ret);
        return ret;
    }

    //set led controller (touch logo led)
    //2: set LED light steady
    //1: set LED breath
    //0: set LED disenable
    private final static String LED_STEADY = "2";
    private final static String LED_BREATH = "1";
    private final static String LED_DISABLE = "0";
    private String LEDPath_55 = "/sys/module/tp_i2c/parameters/led_enable";
    private String LEDPath_40 = "/sys/class/leds/mstar:white/brightness";
    private String LEDPath_48 = "/sys/class/timed_output/breathled_r/enable";
    private String LEDPath_Tv3_55_G = "/sys/class/timed_output/breathled_g/enable";
    private String LEDPath_Tv3_55_B = "/sys/class/timed_output/breathled_b/enable";
    private String LEDPath_Tv3_55_P = "/sys/module/timed_breathled_incptn/parameters/breathperiod";
    private String ProductModel_TV2_55 = "MiTV2-55";
    private String ProductModel_TV2S_48 = "MiTV2S-48";
    private String ProductModel_TV2_40 = "MiTV2-40";
    private String ProductModel_TV3_55 = "MiTV3";

    private boolean LedController(String stat) {
        Log.i(TAG, "LedController: " + "[" + stat + "]");
        boolean ret = false;
        //led cmd路径
        String ledFM10Path = "/sys/class/leds/led_pwm1/brightness";
        String triggerPath = "/sys/class/leds/led_pwm1/trigger";
        mSystemControl.writeSysFs(triggerPath, "none");
        //echoEntry(triggerPath,"none");
        switch (stat) {
            case "0":
                break;
            case "1":
                stat = "255";
                break;
            default:
                stat = "255";
                break;
        }
        String cmd = "echo " + stat + " > " + ledFM10Path;
        Log.i(TAG, cmd);
        //ret = echoEntry(ledFM10Path,stat);
        ret = mSystemControl.writeSysFs(ledFM10Path, stat);
        return ret;
    }

    private boolean BOOTTIMES_NOTEFLAG = false;

    private boolean updateBootTimes() {
        boolean ret = false;
        ret = mLPropManager.increaseLocalPropInt(mSetManager.FACTPROP_BOOTCOUNT, 1);
        return ret;
    }

    private int getBootTimes() {
        int ret = -1;
        ret = mLPropManager.getLocalPropInt(mSetManager.FACTPROP_BOOTCOUNT);
        return ret;
    }

    private boolean LedFlashFlag = false;
    private static final int DEFAULT_PERIOD = 500;
    private int FlashPeriod = 0;

    private boolean ledFlashStart(int period) {
        boolean ret = false;
        if (mProductModel.equals(ProductModel_TV3_55)) {
            FileWriter f;
            final int LED_CLOSE = 0;
            final int LED_FLICKER = 1;
            final int LED_STATIC_LIGHT = 2;
            String LedPeriodPath = "/sys/module/timed_breathled_incptn/parameters/breathperiod";
            String LedEnablePath = "/sys/class/timed_output/breathled_b/enable";
            echoEntry(LedPeriodPath, String.valueOf(period));
            echoEntry(LedEnablePath, String.valueOf(LED_STATIC_LIGHT));
        } else {
            if (mLedFlashThread == null) {
                Log.i(TAG, "LedFlashThread begin work");
                LedFlashFlag = true;
                if (period > 0) {
                    FlashPeriod = period;
                } else {
                    FlashPeriod = DEFAULT_PERIOD;
                }
                mLedFlashThread = new LedFlashThread();
                mLedFlashThread.start();
                ret = true;
            }
        }
        return ret;
    }

    private boolean ledFlashStop() {
        boolean ret = false;
        if (!LedFlashFlag || mLedFlashThread == null) {
            Log.e(TAG, "now aging isn't work , dont close it repeatly");
            return ret;
        }
        LedFlashFlag = false;
        try {
            mLedFlashThread.join();
            mLedFlashThread = null;
            ret = true;
            Log.i(TAG, "stop Led Flash success!");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "stop Led Flash failed");
        }
        return ret;
    }

    private class LedFlashThread extends Thread {
        @Override
        public void run() {
            Log.i(TAG, "enter Led Flash ...");
            boolean switchFlag = false;
            String brightValue = null;
            while (LedFlashFlag) {
                if (switchFlag) {
                    switchFlag = false;
                    brightValue = "0";
                } else {
                    switchFlag = true;
                    brightValue = "2";
                }
                LedController(brightValue);
                UtilManagerImpl.sleep(FlashPeriod);
            }
        }
    }

    private String IRLockPath = "/sys/module/rc_core/parameters/offir_debug";

    private boolean remoteLockController(String stat) {
        boolean ret = false;
        Log.i(TAG, "KeyLock: " + "[" + stat + "]");
        if (!stat.equals("0") && !stat.equals("1")) {
            return ret;
        }
        String cmd = "misysdiagnose:-s echo," + stat + ",>" + IRLockPath;
        SystemProperties.set("ctl.start", cmd);
        ret = true;
/*		File IRLockFile = new File(IRLockPath);
		FileWriter irlockfwr;
		try{
			irlockfwr = new FileWriter(IRLockFile);
			irlockfwr.write(stat);
			irlockfwr.flush();
			irlockfwr.close();
			ret = true;
		}catch(IOException e){
			e.printStackTrace();
		}
*/
        return ret;
    }

    private boolean sleepTv() {
        boolean ret = false;
		/*
		ScreenSaverManager manager = ScreenSaverManager.getInstance();
		manager.postSystemSleepDelayed(0);
		ret = true;
		Log.i(TAG, "Set TV sleep: " + ret);
		*/
        return ret;
    }

    private boolean disableScreenSaver2Sleep() {
        boolean ret = false;
        //close sleep
        android.provider.Settings.System.putInt(context.getContentResolver(), "screen_off_timeout", 2147483647);
        //close screen saver
        android.provider.Settings.System.putInt(context.getContentResolver(), "screen_saver_timeout", -2);
        //ScreenSaverManager manager = ScreenSaverManager.getInstance();
        //manager.setScreenSaverEnabled(false);
        ret = true;
        return ret;
    }

    //now 55 panel have two sources, and the direct is opposite.
    //when we reset this prop, system will read it from T-CON.
    //read the direct flag needs 3s, so system wouldn't read everytime.
    private boolean panelSelect() {
        boolean ret = false;
        int j = 0;
        Log.i(TAG, "reset tv panel select");
        SystemProperties.set(mSetManager.SYSPROP_55TVPANELSELECT, "0");
        for (j = 0; j < 10; j++) {
            if (SystemProperties.get(mSetManager.SYSPROP_55TVPANELSELECT).equals("0")) {
                break;
            } else {
                SystemClock.sleep(100);
            }
        }
        if (j < 10) {
            ret = true;
        }
        return ret;
    }


    private static final String BTRCMACFILEPATH = "/tvinfo/RemoteControllerBtMac";

    private boolean writeBtRcAddr(String mac) {
        boolean ret = false;
        int j = 0;
        Log.i(TAG, "predetermine the remote control mac address: " + mac);
        FileWriter f;
        File rcbtmacfile = new File(BTRCMACFILEPATH);
        if (!rcbtmacfile.exists()) {
            try {
                rcbtmacfile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        try {
            f = new FileWriter(rcbtmacfile);
            f.write(mac);
            f.flush();
            f.close();
            ret = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private String readBtRcAddr() {
        String ret = "00:00:00:00:00:00";
        byte[] buf = new byte[17];
        Log.i(TAG, "get the predetermined remote control mac address");
        File rcbtmacfile = new File(BTRCMACFILEPATH);
        if (rcbtmacfile.exists()) {
            try {
                FileInputStream fstream = new FileInputStream(rcbtmacfile);
                fstream.read(buf, 0, 17);
                ret = new String(buf);
                fstream.close();
                Log.e(TAG, "Get rc bt mac: " + ret);
            } catch (IOException e) {
                Log.e(TAG, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "can't find the rc bt mac file");
        }
        return ret;
    }

    private String getModel() {
        return SystemProperties.get(mSetManager.SYSPROP_PRODUCTMODEL);
    }

    private final String WOOFER_IN = "1";
    private final String WOOFER_OUT = "0";

    private boolean subWooferPlugInStat() {
        boolean ret = false;
        if (WOOFER_IN.equals(SystemProperties.get(mSetManager.SYSPROP_WOOFERPLUGIN))) {
            ret = true;
        }
        return ret;
    }

    private final String GPIO_LOW = "1";
    private final String GPIO_HIGH = "0";

    private boolean setPowerStandby(String stat) {
        boolean ret = false;
		/*
		if(stat.equals(GPIO_LOW)){
			try {
				TvManager.getInstance().setTvosCommonCommand("SetLowPowerOnoff");
				ret = true;
			} catch (TvCommonException e) {
				e.printStackTrace();
			}
		}else if(stat.equals(GPIO_HIGH)){
			try {
				TvManager.getInstance().setTvosCommonCommand("SetHighPowerOnoff");
				ret = true;
			} catch (TvCommonException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	private boolean setI2CPin(String stat){
		boolean ret = false;
		if(stat.equals(GPIO_LOW)){
			try {
				TvManager.getInstance().setTvosCommonCommand("SetLowFactoryTest");
				ret = true;
			} catch (TvCommonException e) {
				e.printStackTrace();
			}
		}else if(stat.equals(GPIO_HIGH)){
			try {
				TvManager.getInstance().setTvosCommonCommand("SetHighFactoryTest");
				ret = true;
			} catch (TvCommonException e) {
				e.printStackTrace();
			}
		}
		*/
        return ret;
    }

    private boolean locSetApplicationRid() {
        boolean ret = false;
        String rid = buildRIDString();
        if (rid == null) {
            Log.e(TAG, "can fetch application rid");
        } else {
            Log.i(TAG, "application rid:" + rid);
            mLPropManager.setAppPropString(mSetManager.APPLPROP_RID, rid);
            String retrid = mLPropManager.getAppPropString(mSetManager.APPLPROP_RID);
            Log.i(TAG, "application rid read back:" + retrid);
            ret = true;
        }
        return ret;
    }

    private int[] keyTestSequence() {
        int[] ret = null;
        int[] TV_DefaultSeq = {KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN,
                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT};
        int[] TV3_55Seq = {KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS,
                KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_NEXT,
                KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_HEADSETHOOK};
        if (mProductModel.equals(ProductModel_TV3_55)) {
            Log.i(TAG, "Mitv 3 KeyTest Sequence");
            ret = TV3_55Seq;
        } else {
            Log.i(TAG, "default mitv keytest sequence");
            ret = TV_DefaultSeq;
        }
        return ret;
    }

    //the para structure is "stat + port", for example (HGPIOAO_13)
    private boolean setGpioStat(String portstat) {
        boolean ret = false;
        String stat = portstat.substring(0, 1);
        String port = portstat.substring(1, portstat.length());
        Log.i(TAG, port + " - setting for gpio set: " + stat);
        boolean OutputSetting = true;
        int intst = -1;
        if (stat.equals("H")) {
            intst = 1;
        } else if (stat.equals("L")) {
            intst = 0;
        } else {
            Log.e(TAG, "error setting for gpio set. " + portstat);
            return ret;
        }
        int value = mTvControlManager.handleGPIO(port, OutputSetting, intst);
        Log.i(TAG, "setGpioStat,result is :" + value);
        if (value == 1) {
            ret = true;
        }
        return ret;
    }

    //the para structure is "port", for example (GPIOAO_13)
    private int getGpioInValue(String port) {
        int ret = -1;
        Log.i(TAG, "get setting for gpio : " + port);
        ret = mTvControlManager.handleGPIO(port, false, 0);
        Log.i(TAG, "getGpioInValue:" + ret);
        return ret;
    }

    private String readPanelIdTag() {
        String ret = null;
        ret = SystemProperties.get(mSetManager.SYSPROP_PANEL_TYPE);
        Log.i(TAG, "the ID tag for panel type is " + ret);
        return ret;
    }
    /*===========================================local functions=====================*/
    /*===========================================tool functions=====================*/

    private String toHexString(String src) {
        String dest = "";
        String srcs[] = src.split(",");
        for (int i = 0; i < srcs.length; i++) {
            dest += String.format("%#x", Integer.parseInt(srcs[i])) + " ";
        }
        return dest;
    }

    private boolean echoEntry(final String entry, String rts) {
        boolean ret = false;
        OutputStreamWriter osw = null;
        Log.i(TAG, "echo [" + rts + "] to path [" + entry + "]");
        try {
            osw = new OutputStreamWriter(new FileOutputStream(entry));
            osw.write(rts, 0, rts.length());
            osw.flush();
            ret = true;
        } catch (Exception e) {
            Log.e(TAG, "final write data to file " + entry + " execetpion=" + e);
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                }
            }
        }
        return ret;
    }

    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * this function used for application team.
     * it can create a radom value for media server authentication
     */
    private String buildRIDString() {
        String retValue = "{\"rid\":\"1ab567a628374de5be40742834f0af17\",\"ssec\":\"ZW9SOt==4YeZZT&?\"}";
        Log.i("buildRIDString", "default retValue = " + retValue);
        try {
            /*UUID, 8-4-4-4-12;*/
            String uuid = UUID.randomUUID().toString();
            String tmprid = uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23) + uuid.substring(24, uuid.length());
            Log.i(TAG, "tmprid = " + tmprid);

            String ssecSeed = "0123456789abcefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@$%&?#-=";
            String tmpssec = new String();
            Random rand = new Random();

            for (int i = 0; i < 16; i++) {
                int pos = rand.nextInt(ssecSeed.length());
                tmpssec += ssecSeed.substring(pos, pos + 1);
            }

            Log.i("buildRIDString", "tmpssec = " + tmpssec);
            String JSON_KEY_SSEC = "ssec";
            String JSON_KEY_RID = "rid";
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(JSON_KEY_SSEC, tmpssec);
            jsonObject.put(JSON_KEY_RID, tmprid);
            retValue = jsonObject.toString();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("buildRIDString", "retValue = " + retValue);
        return retValue;
    }
    /*===========================================tool functions=====================*/

    public String getDlpSn() {
        String ret = null;
        try {
            ret = mProjectorManager.GetProjectorInfo().GetSerialNo();
        } catch (Exception e) {
            Log.e(TAG, "getDlpSn error");
            e.printStackTrace();
        }
        Log.i(TAG, "getDlpSn:" + ret);
        return ret;
    }

    public boolean setBodyDetectStatus(String para) {
        boolean ret = false;
        try {
            if ("0".equals(para)) {
                mProjectorManager.SetAutoBrightnessByIR(ProjectorManager.AUTO_BRIGHTNESS_BY_IR_ENABLE);
            } else {
                mProjectorManager.SetAutoBrightnessByIR(ProjectorManager.AUTO_BRIGHTNESS_BY_IR_DISABLE);
            }
            ret = true;
        } catch (Exception e) {
            Log.e(TAG, "setBodyDetectStatus error");
            e.printStackTrace();
        }
        return ret;
    }

    public int getBodyDetectStatus() {
        int ret = 2;
        try {
            int val = mProjectorManager.GetAutoBrightnessByIR();
            Log.i(TAG, "getBodyDetectStatus:" + val);
            if (val == ProjectorManager.AUTO_BRIGHTNESS_BY_IR_ENABLE) {
                ret = 0;
            } else {
                ret = 1;
            }
        } catch (Exception e) {
            Log.e(TAG, "getBodyDetectStatus error");
            e.printStackTrace();
            ret = 2;
        }
        return ret;
    }

    //=================================================================
    private int mRepeatCount = 0;
    private int mLeftEventCount = 0;
    private int mRightEventCount = 0;
    private Runnable mBodyDetectRunnable = new Runnable() {
        public void run() {
            if (mRepeatCount % 10 == 0) {
                Log.i(TAG, "mBodyDetectRunnable.run " + mRepeatCount);
            }
            int left = mProjectorManager.GetProjectorEventStatus(ProjectorManager.PROJECTOR_IR1_EVENT);
            if (left > 0) {
                mLeftEventCount++;
            }
            int right = mProjectorManager.GetProjectorEventStatus(ProjectorManager.PROJECTOR_IR2_EVENT);
            if (right > 0) {
                mRightEventCount++;
            }
            mRepeatCount++;
            mHandler.postDelayed(mBodyDetectRunnable, 200);
        }
    };

    public boolean startBodyDetectTest() {
        mHandler.removeCallbacks(mBodyDetectRunnable);
        mRepeatCount = 0;
        mLeftEventCount = 0;
        mRightEventCount = 0;
        mHandler.postDelayed(mBodyDetectRunnable, 200);
        return true;
    }

    public boolean stopBodyDetectTest() {
        mHandler.removeCallbacks(mBodyDetectRunnable);
        return true;
    }

    public int getBodyDetectCount(boolean isLeft) {
        int ret = 0;
        if (isLeft) {
            ret = mLeftEventCount;
            mLeftEventCount = 0;
        } else {
            ret = mRightEventCount;
            mRightEventCount = 0;
        }
        return ret;
    }

    //=================================================================
    private static final int MOTOR_STEP = 30;
    private MotorManager mMotorManager = null;

    public boolean setMotorScale(int delta) {
        if (delta > 0) {
            if (getMotorManager() == null) {
                return false;
            } else {
                mMotorManager.SetMotorConfig(MotorManager.DIR_NORMAL, MotorManager.SPEED_NORMAL, MOTOR_STEP * delta);
                mMotorManager.SetMotorStart();
                return true;
            }
        } else if (delta < 0) {
            if (getMotorManager() == null) {
                return false;
            } else {
                delta = 0 - delta;
                mMotorManager.SetMotorConfig(MotorManager.DIR_REVERSE, MotorManager.SPEED_NORMAL, MOTOR_STEP * delta);
                mMotorManager.SetMotorStart();
                return true;
            }
        }
        return false;
    }

    private MotorManager getMotorManager() {
        if (mMotorManager == null) {
            try {
                TvContext instance = TvContext.getInstance();
                mMotorManager = instance.getMotorManager();
            } catch (Exception e) {
                Log.e(TAG, "getMotorManager error");
                e.printStackTrace();
            }
        }
        return mMotorManager;
    }
//=================================================================
}
