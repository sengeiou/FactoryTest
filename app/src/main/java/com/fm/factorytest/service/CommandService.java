package com.fm.factorytest.service;

import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;

import com.fm.factorytest.base.BaseCmdService;
import com.fm.factorytest.global.BoxCommandDescription;
import com.fm.factorytest.global.FactorySetting;
import com.fm.factorytest.global.TvCommandDescription;
import com.fm.factorytest.utils.KeyStoneUtil;
import com.fm.factorytest.utils.MotorUtil;

import java.util.List;

import mitv.sound.SoundManager;


public class CommandService extends BaseCmdService {
    protected static final String TAG = "FactoryCmdService";
    //Burning(Aging) parameters
    private static final String BURNINGSTOP = "BurningStop";
    private static final String BURNING_CLEARCOUNT = "BurningClearCount";
    private static final String BURNING_READCOUNT = "BurningReadCount";
    private static final int HDMI1_ID = 23;
    private static final int HDMI2_ID = 24;
    private static final int HDMI3_ID = 25;
    private static final int HDMI4_ID = 26;
    private static final String READ_ERR = "read error";
    private String productType;
    private Handler mHandler = new Handler();
    private Runnable mSwitchSoundEffect = new Runnable() {
        public void run() {
            mAudioImpl.audioSetSoundEffectMode(SoundManager.SOUND_EFFECT_DOLBY_MUSIC);
            Log.i(TAG, "SetSoundEffectMode DOLBY_MUSIC when initing");
        }
    };
    private Runnable mDisableBodyDetect = new Runnable() {
        public void run() {
            boolean ret = mUtilImpl.setBodyDetectStatus("1");//disable Body Detect
            if (!ret) {
                mHandler.postDelayed(mDisableBodyDetect, 1000);//retry
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        //set first boot flag and start led flash
        Log.d(TAG, "Factory command service");
        productType = SystemProperties.get(mFactorySetting.SYSPROP_PRODUCTMODEL);
        prepareFactorytest();
    }

    private void prepareFactorytest() {
        if (productType.contains(FactorySetting.NAME_PRODUCT_TYPE_BOX)) {
            prepareFactorytestBOX();
        } else if (productType.contains(FactorySetting.NAME_PRODUCT_TYPE_TV)) {
            prepareFactorytestTV();
        } else if (productType.contains(FactorySetting.NAME_PRODUCT_TYPE_PROJECTOR)) {
            prepareFactorytestTV();
            //FM JIRA PROJECTOR-412
            mHandler.postDelayed(mSwitchSoundEffect, 3000);
            mHandler.postDelayed(mDisableBodyDetect, 10);//diable
        } else {
            Log.e(TAG, "here a new product type appears.");
        }
    }

    //TV
    private void prepareFactorytestTV() {
        //1.0 init TvCommandDescription
        //1.1 update Boot Times
        Log.i(TAG, "CMDSERVICE: update boot times");
        mLocalPropImpl.initLocalProperty();
        mUtilImpl.systemUpdateBootTimes();
        //2. Led flash
        if (mUtilImpl.systemGetBootTimes() == 1) {
            Log.i(TAG, "first  boot up");
            SystemClock.sleep(11000);
            mUtilImpl.ledStartFlash(2500);
            //mUtilImpl.setLedLightStat("2");
            //4. prop init
            Log.i(TAG, "CMDSERVICE: init local prop");
            initLocalPropTV();
            if (!mUtilImpl.setApplicationRid()) {
                Log.e(TAG, "can't create rid");
            }
        }
        //3. disable BT
        Log.i(TAG, "CMDSERVICE: set bt disable");
        mRfNetImpl.btSetStatus(false);
        //6. if huaxing, start panel sync init
        Log.i(TAG, "CMDSERVICE: init 3D sync");
        //mMediaImpl.hdmiCheck3DSyncInit();
        //5. autoruncmd
        Log.i(TAG, "CMDSERVICE: check and set aging");
        autoRunCommand();
        //8. set system boot up directly next time
        mUtilImpl.bootupSystemDirect();
        //9. set SoundVolume
        int val = mAudioImpl.audioGetSoundVolume() > 25 ? mAudioImpl.audioGetSoundVolume() : 50;
        Log.e(TAG, "CMDSERVICE:	default sound val is" + mAudioImpl.audioGetSoundVolume() + "current sound val is " + val);
        mAudioImpl.audioSetSoundVolume(val);
        if ("conan".equals(Build.DEVICE)) {
            //10. set backlight as normal
            mPicModeImpl.picSetBacklight(2);
        } else {
            //10. set backlight as MAX (100) bright_mode = 400
            mPicModeImpl.picSetBacklight(100);
        }
        //11. close DTS/DOLBY
        mAudioImpl.closeDTS_DOLBY();
        //12. if soundbar, set resolution as 1080p60 for capture card
        mMediaImpl.setScreenRes("1080P60");
    }

    //BOX
    private void prepareFactorytestBOX() {
        //1.0 init TvCommandDescription
        //1.1 update Boot Times
        Log.i(TAG, "CMDSERVICE: update boot times");
        mLocalPropImpl.initLocalProperty();
        mUtilImpl.systemUpdateBootTimes();
        //2. Led flash
        if (mUtilImpl.systemGetBootTimes() == 1) {
            Log.i(TAG, "first  boot up");
            SystemClock.sleep(11000);
            mUtilImpl.ledStartFlash(2500);
            //mUtilImpl.setLedLightStat("2");
            //4. prop init
            Log.i(TAG, "CMDSERVICE: init local prop");
            initLocalPropBOX();
            if (!mUtilImpl.setApplicationRid()) {
                Log.e(TAG, "can't create rid");
            }
        }
        //3. disable BT
        Log.i(TAG, "CMDSERVICE: set bt disable");
        mRfNetImpl.btSetStatus(false);
        //6. if huaxing, start panel sync init
        Log.i(TAG, "CMDSERVICE: init 3D sync");
        //mMediaImpl.hdmiCheck3DSyncInit();
        //5. autoruncmd
        Log.i(TAG, "CMDSERVICE: check and set aging");
        autoRunCommand();
        //8. set system boot up directly next time
        mUtilImpl.bootupSystemDirect();
        //9. set SoundVolume
        mAudioImpl.audioSetSoundVolume(75);
        //10. set backlight as MAX (100)
        mPicModeImpl.picSetBacklight(100);
        //11. close DTS/DOLBY
        mAudioImpl.closeDTS_DOLBY();
    }

    private void initLocalPropTV() {
        int val = 0;
        mLocalPropImpl.setLocalPropInt(mFactorySetting.FACTPROP_AGINGTIMERCOUNT, 0);
        mLocalPropImpl.setLocalPropInt(mFactorySetting.FACTPROP_BACKLIGHT, 0);
        mLocalPropImpl.setLocalPropInt(mFactorySetting.FACTPROP_BRIGHTNESS, 0);
        mLocalPropImpl.setLocalPropInt(mFactorySetting.FACTPROP_CONTRAST, 0);
        mLocalPropImpl.setLocalPropInt(mFactorySetting.FACTPROP_3D, 0);
        mLocalPropImpl.setLocalPropInt(mFactorySetting.FACTPROP_BURNINGSOUR, 0);
        mLocalPropImpl.setLocalPropBool(mFactorySetting.FACTPROP_AUTORUN_STATUS, false);
        String defaultAutorun = "1222" + "/";
        mLocalPropImpl.setLocalPropString(mSettingManager.FACTPROP_AUTORUN_COMMAND, defaultAutorun);
    }

    private void initLocalPropBOX() {
        int val = 0;
        mLocalPropImpl.setLocalPropInt(mFactorySetting.FACTPROP_AGINGTIMERCOUNT, 0);
        mLocalPropImpl.setLocalPropInt(mFactorySetting.FACTPROP_BACKLIGHT, 0);
        mLocalPropImpl.setLocalPropInt(mFactorySetting.FACTPROP_BRIGHTNESS, 0);
        mLocalPropImpl.setLocalPropInt(mFactorySetting.FACTPROP_CONTRAST, 0);
        mLocalPropImpl.setLocalPropInt(mFactorySetting.FACTPROP_3D, 0);
        mLocalPropImpl.setLocalPropInt(mFactorySetting.FACTPROP_BURNINGSOUR, 0);
        mLocalPropImpl.setLocalPropBool(mFactorySetting.FACTPROP_AUTORUN_STATUS, false);
        String defaultAutorun = "2222" + "/" + "52,107,50,107"; //aging(4k2k)
        mLocalPropImpl.setLocalPropString(mSettingManager.FACTPROP_AUTORUN_COMMAND, defaultAutorun);
    }

    /**
     * test for factory middleware
     */
    private void autoRunCommand() {
        //1. Burning Mode should auto run, if it doesn't be close in last cycle
        String[] val = null;
        String cmd = null;
        String id = null, para = null;
        SystemClock.sleep(2000);
        cmd = mLocalPropImpl.getLocalPropString(mFactorySetting.FACTPROP_AUTORUN_COMMAND);
        if (cmd == null) {
            return;
        }
        val = cmd.split("/");
        id = val[0];
        if (val.length > 1) {
            para = val[1];
        }
        if (mLocalPropImpl.getLocalPropBool(mFactorySetting.FACTPROP_AUTORUN_STATUS)) {
            Log.i(TAG, "CMDSERVICE: do auto run <" + cmd + ">");
            handleCommand(id, para);
        }
    }

    //TODO finish your business
    public void handleCommand(String cmdid, String Param) {
        Log.i(TAG, "[CMD] handleCommand cmdid : " + cmdid + " para " + Param);
        if (FactorySetting.COMMAND_PRODUCT_TYPE_TV.equals(cmdid.substring(0, 1))) {
            Log.i(TAG, "[CMD] TV Command");
            handleCommandTv(cmdid, Param);
        } else if (FactorySetting.COMMAND_PRODUCT_TYPE_BOX.equals(cmdid.substring(0, 1))) {
            Log.i(TAG, "[CMD] BOX Command");
            handleCommandBox(cmdid, Param);
        } else {
            Log.i(TAG, "[CMD] can't find this command");
        }
    }

    private void handleCommandTv(String cmdid, String Param) {
        //print some command description information
        String[] cmdInfo;
        cmdInfo = mTvCd.getCmdDescByID(cmdid);
        if (cmdInfo == null) {
            Log.i(TAG, "[CMD] can't find this command");
            return;
        }
        Log.i(TAG, "[CMD] Description: " + cmdInfo[1] + ",[CMD] Type: " + cmdInfo[2]);
        //check currently whether window is working.
        if (checkTvWindowInOperating(cmdid)) {
            return;
        }

        String param = "";
        if (Param != null && !Param.equals(""))
            param = changeStringToAscII(Param);
        Log.i(TAG, "handleCommand cmdid : " + cmdid + " para [" + param + "]");
        //now command status list just manager the activity function, so normal and
        //innActivity command should not be put into list
        Command c = null;
        if (cmdInfo[2] == TvCommandDescription.CMD_TYPE_ACTIVITY_ON) {
            c = addRunningCommand(cmdid, param);
        }
        int id = -1;
        String result = null;
        try {
            id = Integer.parseInt(cmdid, 16);
        } catch (NumberFormatException e) {
        }
        byte[] value = new byte[1];
        String[] val;
        int colorTemp = 0;
        int gain = 0;
        int offset = 0, temp;
        boolean retFlag = false;
        Log.i(TAG, "handleCommand cmdid : " + id);
        //do your business here
        switch (id) {
            case TvCommandDescription.CMDID_KEYSTONE_ENABLE:
                try {
                    mBinder.setResult_bool(cmdid, KeyStoneUtil.setKeyStoneMode(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_KEYSTONE_SET:
                try {
                    mBinder.setResult_bool(cmdid, KeyStoneUtil.setKeyStoneDirect(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            /**新增led控制*/
            case TvCommandDescription.CMDID_LED_TEST:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.setLedLightStat(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_READ_DLP_VERSION:
                try {
                    mBinder.setResult_string(cmdid, mSysAccessImpl.readDLPVersion());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_AGING_LINE:
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.agingSetAgingLine(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_AGING_LINE:
                try {
                    mBinder.setResult_string(cmdid, mMediaImpl.agingGetAgingLine() + "");
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_AGING_LINE_VOL:
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.agingSetAgingVolume(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_AGING_LINE_VOL:
                try {
                    mBinder.setResult_string(cmdid, mMediaImpl.agingGetAgingVolume() + "");
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_READ_GSENSOR_HORIZONTAL:
                try {
                    mBinder.setResult_string(cmdid, mSysAccessImpl.readGSensorHorizontalData());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_ENABLE_AUTO_FOCUS://switch input Source test
                if (getTvRunningWindCmd() != null)
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_BUSINESS, cmdid, param);
                break;
            case TvCommandDescription.CMDID_START_AUTO_FOCUS:
                try {
                    mBinder.setResult_bool(cmdid, true);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_STOP_AUTO_FOCUS:
                if (getTvRunningWindCmd() != null) {
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_STOP, cmdid, param);
                }
                break;
            case TvCommandDescription.CMDID_RESOLUTION_PIC_OPEN:
                try {
                    mBinder.setResult_bool(cmdid, true);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_RESOLUTION_PIC_OFF:
                if (getTvRunningWindCmd() != null) {
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_STOP, cmdid, param);
                }
                break;
            case TvCommandDescription.CMDID_HDCP_14_MD5_READ:
                try {
                    mBinder.setResult_string(cmdid, mInfoImpl.readHdcp14Md5());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDCP_22_MD5_READ:
                try {
                    mBinder.setResult_string(cmdid, mInfoImpl.readHdcp22Md5());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_RGB_LED_CURRENT_READ:
                try {
                    mBinder.setResult_string(cmdid, mUtilImpl.readRGBLEDCurrent());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_LED_TEMP_READ:
                try {
                    mBinder.setResult_string(cmdid, mUtilImpl.readLEDTemperature(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_XPR_PIC_OPEN:
                try {
                    mBinder.setResult_bool(cmdid, true);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_XPR_PIC_OFF:
                if (getTvRunningWindCmd() != null) {
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_STOP, cmdid, param);
                }
                break;
            case TvCommandDescription.CMDID_READ_G_SENSOR_DATA:
                try {
                    mBinder.setResult_string(cmdid, mSysAccessImpl.readGSensorStandard());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SAVE_G_SENSOR_DATA:
                try {
                    mBinder.setResult_bool(cmdid, mSysAccessImpl.saveGSensorStandard());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_START_COLLECT_G_SENSOR:
                try {
                    mBinder.setResult_bool(cmdid, mSysAccessImpl.startGSensorCollect());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_G_SENSOR_DATA://此处将取数据据改为直接判定gsensor功能是否正常
                try {
                    mBinder.setResult_bool(cmdid, mSysAccessImpl.checkGSensorFunc());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_XPR_SHAKE_STATUS:
                try {
                    mBinder.setResult_bool(cmdid, mSysAccessImpl.enableXPRShake(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_LED_STEP_MOTOR_RESET:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.resetLEDStepMotor());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_FAN_SPEED:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.setFanSpeed(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_CAMERA_TEST_ON:
                try {
                    mBinder.setResult_bool(cmdid, true);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_CAMERA_TEST_OFF:
                if (getTvRunningWindCmd() != null) {
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_STOP, cmdid, param);
                }
                break;
            case TvCommandDescription.CMDID_CAMERA_TEST_OPEN:
                if (getTvRunningWindCmd() != null) {
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_BUSINESS, cmdid, param);
                }
                break;
            case TvCommandDescription.CMDID_CAMERA_TEST_CAPTURE:
                if (getTvRunningWindCmd() != null) {
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_BUSINESS, cmdid, param);
                }
                break;
            /***test set XPR status*/
            case TvCommandDescription.CMDID_SET_XPR_STATUS:
                try {
                    mBinder.setResult_bool(cmdid, mSysAccessImpl.enableXPRCheck(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            /***test write factory pid*/
            case TvCommandDescription.CMDID_WRITE_LOOK_SELECT:
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setLookSelect(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            /**test read factory pid**/
            case TvCommandDescription.CMDID_READ_LOOK_SELECT:
                result = null;
                result = mInfoImpl.getLookSelect();
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_READ_ROM_TOTAL_SIZE:
                try {
                    mBinder.setResult_string(cmdid, mUtilImpl.readRomTotalSpace());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_READ_ROM_AVAIL_SIZE:
                try {
                    mBinder.setResult_string(cmdid, mUtilImpl.readRomAvailSpace());
                } catch (android.os.RemoteException ex) {
                }
                break;
            /***test write factory pid*/
            case TvCommandDescription.CMDID_FACTORY_PID_WRITE:
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setFactoryPID(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            /**test read factory pid**/
            case TvCommandDescription.CMDID_FACTORY_PID_READ:
                result = null;
                result = mInfoImpl.getFactoryPID();
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_UDISK_2_CONTENT_TEST://detect tf card
                try {
                    mBinder.setResult_bool(cmdid, mStorageImpl.usbContent2SpeedTest(param, "2"));
                } catch (android.os.RemoteException ex) {
                }

                break;
            case TvCommandDescription.CMDID_UDISK_3_CONTENT_TEST:
                try {
                    mBinder.setResult_bool(cmdid, mStorageImpl.usbContent2SpeedTest(param, "3"));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_UDISK_DECT_3_0://U disk 3.0 test
                try {
                    mBinder.setResult_bool(cmdid, mStorageImpl.usbHost30Test());
                } catch (android.os.RemoteException ex) {
                }

                break;
            case TvCommandDescription.CMDID_UDISK_FILE_CHECK:
                try {
                    mBinder.setResult_bool(cmdid, mStorageImpl.usbFileCheck(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_UDISK_DECT_2_0://U disk 2.0 test
                break;
            case TvCommandDescription.CMDID_KTV_TEST://KTV test
                break;
            case TvCommandDescription.CMDID_UDISK_CHECK_SPEED:
                result = null;
                result = mStorageImpl.usbSpeedCheck(param);
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SOUR_START://start Source test
                retFlag = true;
                //1. firstly, open woofer
                mMediaImpl.wooferEnable();
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SOUR_SWITCH://switch input Source test
                String pSour = "SOURCE:" + param;
                if (getTvRunningWindCmd() != null)
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_BUSINESS, cmdid, pSour);
                break;
            case TvCommandDescription.CMDID_SOUR_STOP://stop Source test
                Log.i(TAG, "[CMD] running Cmd: " + getTvRunningWindCmd().cmdid);
                Log.i(TAG, "[CMD] curr cmd:" + cmdid);
                Log.i(TAG, "[CMD] para: " + param);
                if (getTvRunningWindCmd() != null)
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_STOP, cmdid, param);
                retFlag = true;
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDMI_1_CEC://HDMI_1 CEC test
                value[0] = (byte) mMediaImpl.hdmiTestCec(HDMI1_ID);
                try {
                    mBinder.setResult_byte(cmdid, value);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDMI_2_CEC://HDMI_2 CEC test
                value[0] = (byte) mMediaImpl.hdmiTestCec(HDMI2_ID);
                try {
                    mBinder.setResult_byte(cmdid, value);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_MHL_CEC_3://MHL 3 CEC test
                value[0] = (byte) mMediaImpl.hdmiTestCec(HDMI3_ID);
                try {
                    mBinder.setResult_byte(cmdid, value);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDMI_1_CEC_NAME://HDMI_1 CEC NAME test
                result = null;
                result = mMediaImpl.hdmiTestCecName(HDMI1_ID);
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDMI_2_CEC_NAME://HDMI_2 CEC NAME test
                result = null;
                result = mMediaImpl.hdmiTestCecName(HDMI2_ID);
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_MHL_CEC_3_NAME://MHL 3 CEC NAME test
                result = null;
                result = mMediaImpl.hdmiTestCecName(HDMI3_ID);
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDMI_EDID://HDMI_EDID test
                break;
            case TvCommandDescription.CMDID_LINE_OUT_ENABLE:
                try {
                    mBinder.setResult_bool(cmdid, mAudioImpl.audioSwitchLineOut(true));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_LINE_OUT_DISABLE:
                try {
                    mBinder.setResult_bool(cmdid, mAudioImpl.audioSwitchLineOut(false));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_ENABLE_SPEAKER://ENABLE_SPEAKER test
                try {
                    mBinder.setResult_bool(cmdid, mAudioImpl.audioSwitchSpeaker(false));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_DISABLE_SPEAKER://DISABLE_SPEAKER test
                try {
                    mBinder.setResult_bool(cmdid, mAudioImpl.audioSwitchSpeaker(true));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_PLAY_SPEAKER_AUDIO://PLAY_SPEAKER_AUDIO test
                break;
            case TvCommandDescription.CMDID_ENABLE_SPDIF://SPDIF enable test
                try {
                    mBinder.setResult_bool(cmdid, mAudioImpl.audioSwitchSpdif(0));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDMI_ARC_ON://HDMI_1 ARC test
                try {
                    mBinder.setResult_bool(cmdid, mAudioImpl.audioSwitchArc(0));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_DISABLE_SPDIF:// SPDIF disable test
                try {
                    mBinder.setResult_bool(cmdid, mAudioImpl.audioSwitchSpdif(2));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDMI_ARC_OFF://HDMI_1 ARC test
                try {
                    mBinder.setResult_bool(cmdid, mAudioImpl.audioSwitchArc(1));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_PLAY_SPDIF_AUDIO:// SPDIF play test
                break;
            case TvCommandDescription.CMDID_WIFI_QUICKCONNECT:
                result = null;
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.wifiConnectAp(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_WIFI_IPADDR_GET:
                result = null;
                result = mRfNetImpl.wifiGetIpAddr();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_WIFI_PING://WIFI ping test
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.wifiPingAp(changeStringToIP(Param)));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_BT_SCAN://Bluetooth scan test
                byte btRssi[] = new byte[1];
                //btRssi[0] = (byte)mBTSignal.findBTDeviceScanned(param);
                btRssi[0] = (byte) mRfNetImpl.btGetRssi(param);
                try {
                    mBinder.setResult_byte(cmdid, btRssi);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_BT_FIND_DEVICE:
                //retFlag = !mRfNetImpl.btGetList().isEmpty();
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_ETH_PING://Ethernet ping test
                String ip = null;
                ip = changeStringToIP(Param);
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.ethernetPingAp(ip));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_ETH_STATE:
                boolean ethStat = false;
                if (Integer.parseInt(param) == 0) {
                    ethStat = true;
                } else {
                    ethStat = false;
                }
                Log.i(TAG, "set Ethernet status: " + ethStat);
                retFlag = mRfNetImpl.ethernetSetStatus(ethStat);
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_ETH_STATE:
                retFlag = mRfNetImpl.ethernetGetStatus();
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_PRODUCT_FEATURE:
                //retFlag = mLocalPropImpl.writeProductFeatures(param);
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_CHECK_PRODUCT_FEATURE:
                //retFlag = mLocalPropImpl.checkProductFeatures(param);
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_IR_START://IR start test
                retFlag = true;
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_IR_STOP://IR stop test
                retFlag = false;
                if (getTvRunningWindCmd() != null)
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_BUSINESS, cmdid, param);

                break;
            case TvCommandDescription.CMDID_N_TEST_PATTERN_START://start test pattern
                retFlag = true;
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_TEST_PATTERN_STOP://stop test pattern
                retFlag = false;
                if (getTvRunningWindCmd() != null)
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_BUSINESS, cmdid, param);

                break;
            case TvCommandDescription.CMDID_SERIAL_WRITE://serial write
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setPcbaSerialNumber(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SERIAL_READ://serial read test
                result = null;
                result = mInfoImpl.getPcbaSerialNumber();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_BT_MAC_WRITE://bt mac write
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setBluetoothMac(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_BT_MAC_READ://bluetooth mac read test
                result = null;
                result = mInfoImpl.getBluetoothMac();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_ETH_MAC_WRITE://eth mac write
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setEthernetMac(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_ETH_MAC_READ://eth mac read test
                result = null;
                result = mInfoImpl.getEthernetMac();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_WIFI_MAC_WRITE://wifi mac write
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setWifiMac(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_WIFI_MAC_READ://wifi mac read test
                result = null;
                result = mInfoImpl.getWifiMac();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDCP_KEY_WRITE://HDCP key write test
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setHdcp14Key(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDCP_KEY_READ://HDCP key read
                byte[] key = new byte[308];
                key = mInfoImpl.getHdcp14Key();
                try {
                    mBinder.setResult_byte(cmdid, key);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_MIRACAST_KEY_WRITE://miracast key write
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setHdcp20Key(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_MIRACAST_KEY_TRANSFER://miracast key transfer write
                break;
            case TvCommandDescription.CMDID_MIRACAST_KEY_READ://miracast key read
                byte[] Mkey = new byte[1008];
                Mkey = mInfoImpl.getHdcp20Key();
                try {
                    mBinder.setResult_byte(cmdid, Mkey);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_MANUFACTURE_ID_WRITE:
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setPcbaManufactureNumber(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDCP14_TX_KEY_WRITE:
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setHdcp14TxKey(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDCP14_TX_KEY_TRANSFER:
                try {
                    boolean active = false;
                    active = mMediaImpl.transHdcp14TxKey();
                    if (active) {
                        active = mInfoImpl.writeHdcpMd5();
                        mBinder.setResult_bool(cmdid, active);
                    } else {
                        mBinder.setResult_bool(cmdid, active);
                    }
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDCP14_TX_KEY_READ:
                byte[] txkey = new byte[304];
                txkey = mInfoImpl.getHdcp14TxKey();
                try {
                    mBinder.setResult_byte(cmdid, txkey);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDCP22_TX_KEY_WRITE:
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setHdcp22TxKey(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDCP22_TX_KEY_READ:
                byte[] TxMkey = new byte[1008];
                TxMkey = mInfoImpl.getHdcp22TxKey();
                try {
                    mBinder.setResult_byte(cmdid, TxMkey);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDCP22_TX_KEY_TRANSFER:
                break;
            case TvCommandDescription.CMDID_MANUFACTURE_ID_READ:
                result = null;
                result = mInfoImpl.getPcbaManufactureNumber();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            //--------------------info MiTV4~XXXXX begin-----------------------
            case TvCommandDescription.CMDID_N_PCBA_SERIAL_WRITE:
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setPcbaSerialNumber(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_PCBA_SERIAL_READ:
                result = null;
                result = mInfoImpl.getPcbaSerialNumber();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_PCBA_MANU_WRITE:
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setPcbaManufactureNumber(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_PCBA_MANU_READ:
                result = null;
                result = mInfoImpl.getPcbaManufactureNumber();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_ASSM_SERIAL_WRITE:
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setAssmSerialNumber(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_ASSM_SERIAL_READ:
                result = null;
                result = mInfoImpl.getAssmSerialNumber();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_ASSM_MANU_WRITE:
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setAssmManufactureNumber(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_ASSM_MANU_READ:
                result = null;
                result = mInfoImpl.getAssmManufactureNumber();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_BT_MAC_WRITE:
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setBluetoothMac(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_BT_MAC_READ:
                result = null;
                result = mInfoImpl.getBluetoothMac();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_ETH_MAC_WRITE:
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setEthernetMac(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_ETH_MAC_READ:
                result = null;
                result = mInfoImpl.getEthernetMac();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            //--------------------info MiTV4~XXXXX end-----------------------
            //--------------------media MiTV4~XXXXX start-----------------------
            case TvCommandDescription.CMDID_N_TVVIEW_SOUR_START://start Source test
                retFlag = true;
                //1. firstly, open woofer
                //mMediaImpl.wooferEnable();
                //the result move to inputsourceForTvview
                break;
            case TvCommandDescription.CMDID_N_TVVIEW_SOUR_SWITCH://switch input Source test
                String sour = "SOURCE:" + param;
                if (getTvRunningWindCmd() != null)
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_BUSINESS, cmdid, sour);
                break;
            case TvCommandDescription.CMDID_N_TVVIEW_SOUR_STOP://stop Source test
                Log.i(TAG, "[CMD] running Cmd: " + getTvRunningWindCmd().cmdid);
                Log.i(TAG, "[CMD] curr cmd:" + cmdid);
                Log.i(TAG, "[CMD] para: " + param);
                if (getTvRunningWindCmd() != null)
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_STOP, cmdid, param);
                retFlag = true;
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_AMLOGIC_AGING_ON:
                //1. lock remote control
                mUtilImpl.remoteControlSetLock("1");
                //2. start burn timer
                mMediaImpl.agingSetTimerStatus(true);
                //3. set suto run command
                String autoruncmd = "1480" + "/";
                mLocalPropImpl.setLocalPropString(mSettingManager.FACTPROP_AUTORUN_COMMAND, autoruncmd);
                //4. open autorun switch
                mLocalPropImpl.setLocalPropBool(mSettingManager.FACTPROP_AUTORUN_STATUS, true);
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.setTestPattern(0xFF, 0xFF, 0xFF));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_AMLOGIC_AGING_OFF:
                //1. lock remote control
                mUtilImpl.remoteControlSetLock("0");
                //2. start burn timer
                mMediaImpl.agingSetTimerStatus(false);
                mMediaImpl.agingInitTimerCount();
                mLocalPropImpl.setLocalPropBool(mSettingManager.FACTPROP_AUTORUN_STATUS, false);
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.cancelTestPattern());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_SET_SCREEN_RESOLUTION:
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.setScreenRes(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            //--------------------media MiTV4~XXXXX start-----------------------
            //--------------------util MiTV4 ~ XXXXX begin --------------
            case TvCommandDescription.CMDID_N_SET_GPIO_OUT_STAT:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.setGpioOut(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_GET_GPIO_IN_STAT:
                byte in[] = new byte[1];
                in[0] = (byte) mUtilImpl.getGpioInStat(param);
                try {
                    mBinder.setResult_byte(cmdid, in);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_SET_VCOM_I2C:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.setVcom(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_GET_VCOM_I2C:
                byte vcom[] = new byte[1];
                vcom[0] = (byte) mUtilImpl.getVcom(Param);
                try {
                    mBinder.setResult_byte(cmdid, vcom);
                } catch (android.os.RemoteException ex) {
                }
                break;
            //--------------------util MiTV4 ~ XXXXX end --------------
            case TvCommandDescription.CMDID_KTV_ENABLE://enable ktv function for assmbly test
                break;
            case TvCommandDescription.CMDID_ATV_CHANNEL_CHANGE://change atv channel
                String pAChan = "CHANNEL:" + Param;
                if (getTvRunningWindCmd() != null)
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_BUSINESS, cmdid, pAChan);
                break;
            case TvCommandDescription.CMDID_ATV_CHANNEL_LOAD_TAB://scan atv channel
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.atvLoadChannel(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_DTV_CHANNEL_CHANGE://change dtv channel
                String pDChan = "CHANNEL:" + Param;
                if (getTvRunningWindCmd() != null)
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_BUSINESS, cmdid, pDChan);
                break;
            case TvCommandDescription.CMDID_DTV_CHANNEL_LOAD_TAB://scan dtv channel
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.dtvLoadChannel(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_WIFI_CONNECT_24AP:
                break;
            case TvCommandDescription.CMDID_WIFI_CONNECT_5AP:
                break;
            case TvCommandDescription.CMDID_WIFI_SPEED_START:
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.wifiThroughputStart());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_WIFI_SPEED_PARA:
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.wifiThroughputWithParameter(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_WIFI_SPEED_STOP:
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.wifiThroughputStop());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_WIFI_WAKEUP:
                break;
            case TvCommandDescription.CMDID_BT_PCM_LOOPBACK:
                break;
            case TvCommandDescription.CMDID_BT_THROUGH_OUTPUT:
                break;
            case TvCommandDescription.CMDID_BT_FORCE_MATCH:
                break;
            case TvCommandDescription.CMDID_BT_WAKEUP:
                break;
            case TvCommandDescription.CMDID_BACKLIGHT_SET:
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picSetBacklight(Integer.parseInt(param)));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_BACKLIGHT_GET:
                value[0] = (byte) mPicModeImpl.picGetBacklight();
                try {
                    mBinder.setResult_byte(cmdid, value);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_LIGHT_SENSOR:
                int senValue = mUtilImpl.lightSensorGetValue();
                try {
                    mBinder.setResult_string(cmdid, Integer.toString(senValue));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_AGING_ON:
                //1. lock remote control
                mUtilImpl.remoteControlSetLock("1");
                //2. start burn timer
                mMediaImpl.agingSetTimerStatus(true);
                //3. set suto run command
                String autorunCommand = "1222" + "/";
                mLocalPropImpl.setLocalPropString(mSettingManager.FACTPROP_AUTORUN_COMMAND, autorunCommand);
                //4. open autorun switch
                mLocalPropImpl.setLocalPropBool(mSettingManager.FACTPROP_AUTORUN_STATUS, true);
                retFlag = true;
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_AGING_OFF:
                //1. stop remote control
                mUtilImpl.remoteControlSetLock("0");
                mMediaImpl.agingSetTimerStatus(false);
                //7. init aging
                mMediaImpl.agingInitTimerCount();
                mLocalPropImpl.setLocalPropBool(mSettingManager.FACTPROP_AUTORUN_STATUS, false);
                if (getTvRunningWindCmd() != null)
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_BUSINESS, cmdid, BURNINGSTOP);
                retFlag = true;
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_AUTORUN_STATUS:
                if (Integer.parseInt(param) == 0) {
                    Log.i(TAG, "set auto run flag as true");
                    retFlag = mLocalPropImpl.setLocalPropBool(mSettingManager.FACTPROP_AUTORUN_STATUS, true);
                } else if (Integer.parseInt(param) == 1) {
                    Log.i(TAG, "set auto run flag as false");
                    //7. init aging
                    mMediaImpl.agingInitTimerCount();
                    retFlag = mLocalPropImpl.setLocalPropBool(mSettingManager.FACTPROP_AUTORUN_STATUS, false);
                }
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_AUTORUN_STATUS:
                retFlag = mLocalPropImpl.getLocalPropBool(mSettingManager.FACTPROP_AUTORUN_STATUS);
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_AUTORUN_COMMAND:
                retFlag = mMediaImpl.setAutoRunCommand(param);
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_AUTORUN_COMMAND:
                String autorunCmd = null;
                autorunCmd = mMediaImpl.getAutoRunCommand();
                try {
                    mBinder.setResult_string(cmdid, autorunCmd);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_AGING_RESET_TIMER:
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.agingInitTimerCount());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_AGING_GET_TIMER:
                try {
                    mBinder.setResult_byte(cmdid, mMediaImpl.agingGetTimerCount());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_AGING_TIMER_START:
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.agingSetTimerStatus(true));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_AGING_TIMER_STOP:
				/*
				mFacComCmd.burnTimerStop();
				try{
					mBinder.setResult_bool(cmdid,true);
				} catch (android.os.RemoteException ex) {}
				*/
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.agingSetTimerStatus(false));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_PATTERN_SET:
                int r = 255, g = 255, b = 255;
                val = param.split(":");
                try {
                    r = Integer.parseInt(val[0]);
                    g = Integer.parseInt(val[1]);
                    b = Integer.parseInt(val[2]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "red:" + val[0] + ">>green:" + val[1] + ">>blue: " + val[2]);
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.setTestPattern(r, g, b));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_PATTERN_DISABLE:
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.cancelTestPattern());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_FACTORY_RESET:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.systemReset());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SYSTEM_MODE_CHANGE:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.systemSwitchMode());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SYSTEM_MODE_CHANGE_NEW:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.systemSwitchModeNew());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SYSTEM_MODE_SET:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.systemModeSet());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SYSTEM_MODE_GET:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.systemModeGet());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SYSTEM_REBOOT:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.systemReboot());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SYSTEM_REBOOT_RECOVERY:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.systemRebootRecovery());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SYSTEM_MASTER_CLEAR:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.systemMasterClear());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SYSTEM_SHUTDOWN:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.systemShutdown());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_PIC_MODE_RESET:
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picModeReset());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_PIC_MODE:
                value[0] = (byte) mPicModeImpl.picModeGet();
                try {
                    mBinder.setResult_byte(cmdid, value);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_PIC_MODE:
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picModeSet(Integer.parseInt(param)));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_PIC_BRIGHTNESS:
                value[0] = (byte) mPicModeImpl.picGetBrightness();
                try {
                    mBinder.setResult_byte(cmdid, value);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_PIC_BRIGHTNESS:
                temp = Integer.parseInt(param);
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picSetBrightness(temp));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_PIC_CONTRAST:
                value[0] = (byte) mPicModeImpl.picGetContrast();
                try {
                    mBinder.setResult_byte(cmdid, value);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_PIC_CONTRAST:
                temp = Integer.parseInt(param);
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picSetContrast(temp));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_PIC_SHARPNESS:
                value[0] = (byte) mPicModeImpl.picGetSharpness();
                try {
                    mBinder.setResult_byte(cmdid, value);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_PIC_SHARPNESS:
                temp = Integer.parseInt(param);
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picSetSharpness(temp));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_PIC_HUE:
                value[0] = (byte) mPicModeImpl.picGetHue();
                try {
                    mBinder.setResult_byte(cmdid, value);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_PIC_HUE:
                temp = Integer.parseInt(param);
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picSetHue(temp));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_PIC_SATURATION:
                value[0] = (byte) mPicModeImpl.picGetSatuation();
                try {
                    mBinder.setResult_byte(cmdid, value);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_PIC_SATURATION:
                temp = Integer.parseInt(param);
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picSetSatuation(temp));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_PIC_COLORTEMP:
                value[0] = (byte) mPicModeImpl.picGetColorTemp();
                try {
                    mBinder.setResult_byte(cmdid, value);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_PIC_COLORTEMP:
                temp = Integer.parseInt(param);
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picSetColorTemp(temp));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_POSTGAIN_RED_SET:
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picSetPostRedGain(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_POSTGAIN_GREEN_SET:
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picSetPostGreenGain(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_POSTGAIN_BLUE_SET:
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picSetPostBlueGain(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_POSTGAIN_RED_GET:
                try {
                    colorTemp = Integer.parseInt(param);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                gain = mPicModeImpl.picGetPostRedGain(colorTemp);
                try {
                    mBinder.setResult_string(cmdid, changeInt2String(gain));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_POSTGAIN_GREEN_GET:
                try {
                    colorTemp = Integer.parseInt(param);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                gain = mPicModeImpl.picGetPostGreenGain(colorTemp);
                try {
                    mBinder.setResult_string(cmdid, changeInt2String(gain));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_POSTGAIN_BLUE_GET:
                try {
                    colorTemp = Integer.parseInt(param);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                gain = mPicModeImpl.picGetPostBlueGain(colorTemp);
                try {
                    mBinder.setResult_string(cmdid, changeInt2String(gain));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_POSTOFFS_RED_GET:
                try {
                    colorTemp = Integer.parseInt(param);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                offset = mPicModeImpl.picGetPostRedOffset(colorTemp);
                try {
                    mBinder.setResult_string(cmdid, changeInt2String(offset));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_POSTOFFS_GREEN_GET:
                try {
                    colorTemp = Integer.parseInt(param);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                offset = mPicModeImpl.picGetPostGreenOffset(colorTemp);
                try {
                    mBinder.setResult_string(cmdid, changeInt2String(offset));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_POSTOFFS_BLUE_GET:
                try {
                    colorTemp = Integer.parseInt(param);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                offset = mPicModeImpl.picGetPostBlueOffset(colorTemp);
                try {
                    mBinder.setResult_string(cmdid, changeInt2String(offset));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_POSTOFFS_RED_SET:
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picSetPostRedOffset(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_POSTOFFS_GREEN_SET:
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picSetPostGreenOffset(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_POSTOFFS_BLUE_SET:
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picSetPostBlueOffset(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_POSTGAIN_RGB_SET:
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picGeneralWBGain(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_POSTOFFS_RGB_SET:
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picGeneralWBOffset(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_WHITEBALANCE_SAVE:
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picGeneralWBSave());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_RESET_SOUND_MODE:
                try {
                    mBinder.setResult_bool(cmdid, mAudioImpl.audioResetSoundMode());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_SOUND_MODE:
                try {
                    mBinder.setResult_bool(cmdid, mAudioImpl.audioSetSoundMode(Integer.parseInt(param)));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_SOUND_MODE:
                try {
                    mBinder.setResult_string(cmdid, String.valueOf(mAudioImpl.audioGetSoundMode()));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_SOUND_OUTPUT_MODE:
                try {
                    mBinder.setResult_bool(cmdid, mAudioImpl.audioOutputMode(Integer.parseInt(param)));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_SPEAKER_SWITCH:
                try {
                    mBinder.setResult_bool(cmdid, mAudioImpl.speakerswitch(Integer.parseInt(param)));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_3D_MODE:
                value[0] = (byte) mMediaImpl.hdmiGet3D();
                try {
                    mBinder.setResult_byte(cmdid, value);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_3D_MODE:
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.hdmiSet3D(Integer.parseInt(param)));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_BT_3D_SYNC_FLAG:
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.hdmiCheck3DSync());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_PIC_FULLHD:
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.picSetFullHD());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_RC_LOCK:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.remoteControlSetLock(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_SOUND_BALANCE:
                try {
                    mBinder.setResult_bool(cmdid, mAudioImpl.audioSetSoundBalance(Integer.parseInt(param)));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_ASPECT_MODE_SET:
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.setAspectMode(Integer.parseInt(param)));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_MODEL_NAME_GET:
                result = null;
                result = mInfoImpl.getModelName();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_SYSTEM_FW_VER:
                result = null;
                result = mInfoImpl.getFirmwareVer();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_SOUND_MUTE:
                try {
                    mBinder.setResult_bool(cmdid, mAudioImpl.audioSetSoundMute());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDCP_KSV_GET:
                byte ksv[] = new byte[5];
                ksv = mInfoImpl.getHdcpKsv();
                try {
                    mBinder.setResult_byte(cmdid, ksv);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_WIFI_RSSI_GET:
                byte rssi[] = new byte[1];
                rssi[0] = (byte) mRfNetImpl.wifiGetRssi();
                try {
                    mBinder.setResult_byte(cmdid, rssi);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_WIFI_STATE:
                boolean wifiStat = false;
                if (Integer.parseInt(param) == 0) {
                    wifiStat = true;
                } else {
                    wifiStat = false;
                }
                Log.i(TAG, "set wifi status: " + wifiStat);
                retFlag = mRfNetImpl.wifiSetStatus(wifiStat);
                try {
                    //mBinder.setResult_bool(cmdid,mRfNetImpl.wifiSetStatus(wifiStat));
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_WIFI_SCAN_START:
                retFlag = mRfNetImpl.wifiStartScan();
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_CHECK_WIFI_SCAN_RESULT:
                List<ScanResult> scanresults = null;

                scanresults = mRfNetImpl.wifiGetScanList();
                for (ScanResult scanResult : scanresults) {
                    retFlag = scanResult.SSID.equals(param);
                    if (retFlag)
                        break;
                }
                Log.i(TAG, "scan result: " + retFlag);
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_BT_STATE:
                boolean btStat = false;
                if (Integer.parseInt(param) == 0) {
                    btStat = true;
                } else {
                    btStat = false;
                }
                Log.i(TAG, "set BT status: " + btStat);
                retFlag = mRfNetImpl.btSetStatus(btStat);
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_BT_STATE_BLE:
                btStat = false;
                if (Integer.parseInt(param) == 0) {
                    btStat = true;
                } else {
                    btStat = false;
                }
                Log.i(TAG, "set BT status: " + btStat);
                retFlag = mRfNetImpl.btSetStatusBLE(btStat);
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_GET_SOUND_VOLUME:
                byte vol[] = new byte[1];
                vol[0] = (byte) mAudioImpl.audioGetSoundVolume();
                try {
                    mBinder.setResult_byte(cmdid, vol);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_SOUND_VOLUME:
                try {
                    mBinder.setResult_bool(cmdid, mAudioImpl.audioSetSoundVolume(Integer.parseInt(param)));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_PQ_DB_SAVE:
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picTransPQDataToDB());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SYSTEM_SLEEP:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.sleepSystem());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SYSTEM_PARTITION_CHECK:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.checkSystemPartition());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_UDISK_UNMOUNT_3_0:
                try {
                    mBinder.setResult_bool(cmdid, mStorageImpl.usbHost30Unmount());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_PQ_PANEL_SET:
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picPanelSelect(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_TOUCHPAD_TEST://Get TV touch pad status
                try {
                    mBinder.setResult_bool(cmdid, TouchFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_PRODUCTREGION:
                // try{
                //     mBinder.setResult_bool(cmdid,mUtilImpl.setProductRegion(param));
                // } catch (android.os.RemoteException ex) {}
                break;
            case TvCommandDescription.CMDID_GET_PRODUCTREGION:
                // result = null;
                // result = mUtilImpl.getProductRegion();
                // if(null == result){
                //     result = READ_ERR;
                // }
                // try{
                //     mBinder.setResult_string(cmdid,result);
                // } catch (android.os.RemoteException ex) {}
                break;
            case TvCommandDescription.CMDID_MEDIA_PLAY://autovideo
                retFlag = true;
                //2. start burn timer
                mMediaImpl.agingSetTimerStatus(true);
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_MEDIA_STOP://stop autovide
                if (getTvRunningWindCmd() != null)
                    TvSetControlMsg(getTvRunningWindCmd(), FactorySetting.COMMAND_TASK_STOP, cmdid, param);
                retFlag = true;
                //2. stop burn timer
                mMediaImpl.agingSetTimerStatus(false);
                try {
                    mBinder.setResult_bool(cmdid, retFlag);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_BTRC_MAC_SET:
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.setBtRcMac(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_BTRC_MAC_GET:
                result = null;
                result = mUtilImpl.getBtRcMac();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_RESET_PANEL_SEL:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.resetTvPanelSelect());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_WIFI_DISCONNECT:
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.wifiDisconnect());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_POWERSTANDBY:
                Log.i(TAG, "param is [" + param + "]");
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.setPowerStandbyStat(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_WOOFER_PLUGIN:
                boolean wooferStat = false;
                wooferStat = mUtilImpl.getSubWooferStat();
                try {
                    mBinder.setResult_bool(cmdid, wooferStat);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_HDMI_HPD_RESET:
                boolean resethpd = false;
                resethpd = mMediaImpl.resetHPD();
                try {
                    mBinder.setResult_bool(cmdid, resethpd);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_UPDATE_I2C_PORT_TEST:
                Log.i(TAG, "param is [" + param + "]");
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.setI2CPinStat(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_CHECK_HDCPKEY14_VALID:
                boolean hdcp14 = false;
                hdcp14 = mMediaImpl.checkHdcp14Valid();
                try {
                    mBinder.setResult_bool(cmdid, hdcp14);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_CHECK_HDCPKEY22_VALID:
                boolean hdcp22 = false;
                hdcp22 = mMediaImpl.checkHdcp22Valid();
                try {
                    mBinder.setResult_bool(cmdid, hdcp22);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_DOLBY_DTS_CHECK:
                result = null;
                result = mMediaImpl.checkDolbyDts();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_GET_PANEL_ID_TAG:
                result = null;
                result = mUtilImpl.checkPanelIdTag();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_CLOSE_DSP_DAP:
                boolean closedap = false;
                closedap = mAudioImpl.closeDap();
                try {
                    mBinder.setResult_bool(cmdid, closedap);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_N_BYPASS_PQ:
                boolean bypasspq = false;
                bypasspq = mPicModeImpl.byPassPQ(param);
                try {
                    mBinder.setResult_bool(cmdid, bypasspq);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_PRODUCT_UIID_WRITE:
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setPID(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_PRODUCT_UIID_READ:
                result = null;
                result = mInfoImpl.getPID();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_DLP_SN_READ:
                result = null;
                result = mUtilImpl.getDlpSn();
                if (null == result || result.length() == 0) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_BODY_DETECT:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.setBodyDetectStatus(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_BODY_DETECT_STATUS:
                int bodyDetect = mUtilImpl.getBodyDetectStatus();
                try {
                    mBinder.setResult_string(cmdid, String.valueOf(bodyDetect));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_DLP_SCREEN_CHECK:
                int mode = -1;
                try {
                    mode = Integer.valueOf(param);
                } catch (Exception e) {
                }
                try {
                    mBinder.setResult_bool(cmdid, mSysAccessImpl.screenCheck(mode));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_DLP_SCREEN_CHECK:
                try {
                    mBinder.setResult_bool(cmdid, mSysAccessImpl.enableScreenCheck(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_SET_MOTOR_SCALE:
                int scale = 0;
                try {
                    scale = Integer.valueOf(param);
                } catch (Exception e) {
                }

                try {
                    if ("conan".equals(Build.DEVICE)) {
                        mBinder.setResult_bool(cmdid, MotorUtil.setMotorScale(scale));
                    } else {
                        mBinder.setResult_bool(cmdid, mUtilImpl.setMotorScale(scale));
                    }
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_BODY_DETECT_COUNT:
                boolean isLeft = true;
                try {
                    int i = Integer.valueOf(param);
                    isLeft = (i == 0);
                } catch (Exception e) {
                }
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.getBodyDetectCount(isLeft) > 0);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_STOP_BODY_DETECT:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.stopBodyDetectTest());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_START_BODY_DETECT:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.startBodyDetectTest());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_PQ_ENABLE:
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picEnablePQ());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_PQ_DISABLE:
                try {
                    mBinder.setResult_bool(cmdid, mPicModeImpl.picDisablePQ());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_DLP_INFO_SYNC:
                try {
                    mBinder.setResult_bool(cmdid, mSysAccessImpl.syncDlpInfo());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_DLP_INFO_SAVE:
                try {
                    mBinder.setResult_bool(cmdid, mSysAccessImpl.saveDlpInfo());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_WHEEL_DELAY_READ:
                try {
                    mBinder.setResult_string(cmdid, String.valueOf(mSysAccessImpl.getWheelDelay()));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case TvCommandDescription.CMDID_WHEEL_DELAY_WRITE:
                int delay = -1;
                try {
                    delay = Integer.valueOf(param);
                } catch (Exception e) {
                }
                if (delay != -1) {
                    try {
                        mBinder.setResult_bool(cmdid, mSysAccessImpl.setWheelDelay(delay));
                    } catch (android.os.RemoteException ex) {
                    }
                } else {
                    try {
                        mBinder.setResult_bool(cmdid, false);
                    } catch (android.os.RemoteException ex) {
                    }
                }
                break;
            default:
                Log.e(TAG, "handleCommand command ID not support yet!");
                break;
        }

        if (mTvCd.getCmdTypeByID(cmdid).equals(TvCommandDescription.CMD_TYPE_ACTIVITY_ON)) {
            Log.i(TAG, "do activity operations");
            TvhandleCommandForActivity(c);
        }
    }

    private void handleCommandBox(String cmdid, String Param) {
        //print some command description information
        String[] cmdInfo;
        cmdInfo = mBoxCd.getCmdDescByID(cmdid);
        if (cmdInfo == null) {
            Log.i(TAG, "[CMD] can't find this command");
            return;
        }
        Log.i(TAG, "[CMD] Description: " + cmdInfo[1] + ",[CMD] Type: " + cmdInfo[2]);
        //check currently whether window is working.
        if (checkBoxWindowInOperating(cmdid)) {
            return;
        }

        String param = "";
        if (Param != null && !Param.equals(""))
            param = changeStringToAscII(Param);
        Log.i(TAG, "handleCommand cmdid : " + cmdid + " para [" + param + "]");
        //now command status list just manager the activity function, so normal and
        //innActivity command should not be put into list
        Command c = null;
        if (cmdInfo[2] == BoxCommandDescription.CMD_TYPE_ACTIVITY_ON) {
            c = addRunningCommand(cmdid, param);
        }
        int id = -1;
        String result = null;
        try {
            id = Integer.parseInt(cmdid, 16);
        } catch (NumberFormatException e) {
        }
        byte[] retByteArr = null;
        String[] val;
        boolean retBool = false;
        char[] retCharArr = null;
        String retString = null;
        byte retByte = 0;
        int retInt = 0;
        Log.i(TAG, "handleCommand cmdid : " + id);
        //do your business here
        switch (id) {
            case BoxCommandDescription.CMDID_PCBA_SERIAL_WRITE://serial write
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setPcbaSerialNumber(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_PCBA_SERIAL_READ://serial read test
                result = null;
                result = mInfoImpl.getPcbaSerialNumber();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_ASSM_SERIAL_WRITE://serial write
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setAssmSerialNumber(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_ASSM_SERIAL_READ://serial read test
                result = null;
                result = mInfoImpl.getAssmSerialNumber();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_MODEL_NAME_GET:
                result = null;
                result = mInfoImpl.getModelName();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_GET_SYSTEM_FW_VER:
                result = null;
                result = mInfoImpl.getFirmwareVer();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_PCBA_MANUFACTURE_ID_WRITE:
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setPcbaManufactureNumber(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_PCBA_MANUFACTURE_ID_READ:
                result = null;
                result = mInfoImpl.getPcbaManufactureNumber();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_ASSM_MANUFACTURE_ID_WRITE:
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setAssmManufactureNumber(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_ASSM_MANUFACTURE_ID_READ:
                result = null;
                result = mInfoImpl.getAssmManufactureNumber();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_BT_MAC_WRITE://bt mac write
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setBluetoothMac(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_BT_MAC_READ://bluetooth mac read test
                result = null;
                result = mInfoImpl.getBluetoothMac();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_WIFI_MAC_WRITE://wifi mac write
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setWifiMac(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_WIFI_MAC_READ://wifi mac read test
                result = null;
                result = mInfoImpl.getWifiMac();
                if (null == result) {
                    result = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, result);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_HDCP_KEY_WRITE://HDCP key write to /persist
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setHdcpKeyM11(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_HDCP_KEY_READ://HDCP key read from /persist
                byte[] key = new byte[2480];
                key = mInfoImpl.getHdcpKeyM11();
                try {
                    mBinder.setResult_byte(cmdid, key);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_HDCP_KEY_TRANS://trans hdcp key to mtk drm
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.transHdcpKeyM11());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_HDCP_KEY_VERIFY://verify hdcp key
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.verHdcpKeyM11());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_SYSTEM_MODE_CHANGE:
                try {
                    mLocalPropImpl.setLocalPropBool(mSettingManager.FACTPROP_AUTORUN_STATUS, false);
                    mBinder.setResult_bool(cmdid, mUtilImpl.systemSwitchMode());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_ENTER_WIFI_RF:
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.enterWifi());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_EXIT_WIFI_RF:
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.exitWifi());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_WIFI_RX_START:
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.startWifiRx(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_WIFI_RX_STOP:
                try {
                    mBinder.setResult_string(cmdid, mRfNetImpl.stopWifiRx());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_WIFI_TX_START:
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.startWifiTx(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_WIFI_TX_STOP:
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.stopWifiTx());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_WIFI_FREQ_OFFSET_WRITE:
                try {
                    mBinder.setResult_bool(cmdid, mInfoImpl.setWifiFreqOffset(Param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_WIFI_FREQ_OFFSET_READ:
                byte btOffset[] = new byte[1];
                btOffset[0] = mInfoImpl.getWifiFreqOffset();
                try {
                    mBinder.setResult_byte(cmdid, btOffset);
                } catch (android.os.RemoteException ex) {
                }
                break;
			/*
			case BoxCommandDescription.CMDID_BT_TX_START:
				try{
					mBinder.setResult_bool(cmdid,mRfNetImpl.startBtTx(param));
				} catch (android.os.RemoteException ex) {}
				break;
			case BoxCommandDescription.CMDID_BT_TX_STOP:
				try{
					mBinder.setResult_bool(cmdid,mRfNetImpl.stopBtTx());
				} catch (android.os.RemoteException ex) {}
				break;
				*/
            case BoxCommandDescription.CMDID_MEDIA_PLAY://autovideo
                retBool = true;
                try {
                    mBinder.setResult_bool(cmdid, retBool);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_MEDIA_STOP://stop autovide
                if (getBoxRunningWindCmd() != null)
                    Log.i(TAG, "now some windows is running, we want close it");
                BoxSetControlMsg(getBoxRunningWindCmd(), FactorySetting.COMMAND_TASK_STOP, cmdid, param);
                break;
            case BoxCommandDescription.CMDID_SET_AUTORUN_STATUS:
                if (Integer.parseInt(param.trim()) == 0) {
                    Log.i(TAG, "set auto run flag as true");
                    retBool = mLocalPropImpl.setLocalPropBool(mSettingManager.FACTPROP_AUTORUN_STATUS, true);
                } else if (Integer.parseInt(param.trim()) == 1) {
                    Log.i(TAG, "set auto run flag as false");
                    //7. init aging
                    retBool = mLocalPropImpl.setLocalPropBool(mSettingManager.FACTPROP_AUTORUN_STATUS, false);
                }
                try {
                    mBinder.setResult_bool(cmdid, retBool);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_GET_AUTORUN_STATUS:
                retBool = mLocalPropImpl.getLocalPropBool(mSettingManager.FACTPROP_AUTORUN_STATUS);
                try {
                    mBinder.setResult_bool(cmdid, retBool);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_SET_AUTORUN_COMMAND:
                retBool = mMediaImpl.setAutoRunCommand(param);
                try {
                    mBinder.setResult_bool(cmdid, retBool);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_GET_AUTORUN_COMMAND:
                String autorunCmd = null;
                autorunCmd = mMediaImpl.getAutoRunCommand();
                try {
                    mBinder.setResult_string(cmdid, autorunCmd);
                } catch (android.os.RemoteException ex) {
                }
                break;
            //bt nonsignal test (returned int is transfer to string)
            case BoxCommandDescription.CMDID_BT_NONSIG_GETCHIPID:
                retInt = mRfNetImpl.nonSigBleGetChipId();
                try {
                    mBinder.setResult_string(cmdid, Integer.toString(retInt));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_BT_NONSIG_INIT:
                retInt = mRfNetImpl.nonSigInitBt();
                try {
                    mBinder.setResult_string(cmdid, Integer.toString(retInt));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_BT_NONSIG_UNINIT:
                retInt = mRfNetImpl.nonSigUninitBt();
                try {
                    mBinder.setResult_string(cmdid, Integer.toString(retInt));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_BT_NONSIG_CHECKBLESUPPORT:
                retInt = mRfNetImpl.nonSigIsBleSupport();
                try {
                    mBinder.setResult_string(cmdid, Integer.toString(retInt));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_BT_NONSIG_CLOSENORMAL:
                retBool = mRfNetImpl.nonSigCloseNormalBt();
                try {
                    mBinder.setResult_bool(cmdid, retBool);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_BT_NONSIG_OPENNORMAL:
                retBool = mRfNetImpl.nonSigOpenNormalBt();
                try {
                    mBinder.setResult_bool(cmdid, retBool);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_BT_NONSIG_HCICMDRUN:
                retByteArr = mRfNetImpl.nonSigHciCmdRun(Param);
                if (retByteArr == null) {
                    try {
                        mBinder.setResult_bool(cmdid, false);
                    } catch (android.os.RemoteException ex) {
                    }
                } else {
                    try {
                        mBinder.setResult_byte(cmdid, retByteArr);
                    } catch (android.os.RemoteException ex) {
                    }
                }
                break;
            //RF signal
            case BoxCommandDescription.CMDID_WIFI_RSSI_GET:
                byte rssi[] = new byte[2];
                rssi = mRfNetImpl.wifiGetRssiBox();
                try {
                    mBinder.setResult_byte(cmdid, rssi);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_SET_WIFI_STATE:
                boolean wifiStat = false;
                if (Integer.parseInt(param) == 0) {
                    wifiStat = true;
                } else {
                    wifiStat = false;
                }
                Log.i(TAG, "set wifi status: " + wifiStat);
                retBool = mRfNetImpl.wifiSetStatus(wifiStat);
                try {
                    mBinder.setResult_bool(cmdid, retBool);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_SET_BT_STATE:
                boolean btStat = false;
                if (Integer.parseInt(param) == 0) {
                    btStat = true;
                } else {
                    btStat = false;
                }
                Log.i(TAG, "set BT status: " + btStat);
                retBool = mRfNetImpl.btSetStatus(btStat);
                try {
                    mBinder.setResult_bool(cmdid, retBool);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_SET_BT_STATE_BLE:
                boolean bleStat = false;
                if (Integer.parseInt(param) == 0) {
                    bleStat = true;
                } else {
                    bleStat = false;
                }
                Log.i(TAG, "set BLE status: " + bleStat);
                retBool = mRfNetImpl.btSetStatusBLE(bleStat);
                try {
                    mBinder.setResult_bool(cmdid, retBool);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_WIFI_QUICKCONNECT:
                retBool = mRfNetImpl.ethernetSetStatus(false);
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.wifiConnectAp(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_WIFI_DISCONNECT:
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.wifiDisconnect());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_WIFI_IPADDR_GET:
                retString = mRfNetImpl.wifiGetIpAddr();
                if (null == retString) {
                    retString = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, retString);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_WIFI_PING://WIFI ping test
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.wifiPingAp(changeStringToIP(Param)));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_BT_SCAN://Bluetooth scan test
                byte btRssi[] = new byte[1];
                //btRssi[0] = (byte)mBTSignal.findBTDeviceScanned(param);
                btRssi[0] = (byte) mRfNetImpl.btGetRssi(param);
                try {
                    mBinder.setResult_byte(cmdid, btRssi);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_WIFI_SPEED_START:
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.wifiThroughputStart());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_WIFI_SPEED_STOP:
                try {
                    mBinder.setResult_bool(cmdid, mRfNetImpl.wifiThroughputStop());
                } catch (android.os.RemoteException ex) {
                }
                break;
            //others
            case BoxCommandDescription.CMDID_LED_TEST:
                retBool = mUtilImpl.setLedLightStat(param);
                try {
                    mBinder.setResult_bool(cmdid, retBool);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_SET_FAN_STAT:
                retBool = mUtilImpl.setFanStat(param);
                try {
                    mBinder.setResult_bool(cmdid, retBool);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_GET_CPU_TEMP:
                try {
                    mBinder.setResult_string(cmdid, mUtilImpl.getCpuTemp());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_BTRC_MAC_SET:
                try {
                    mBinder.setResult_bool(cmdid, mUtilImpl.setBtRcMac(param));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_BTRC_MAC_GET:
                retString = mUtilImpl.getBtRcMac();
                if (null == retString) {
                    retString = READ_ERR;
                }
                try {
                    mBinder.setResult_string(cmdid, retString);
                } catch (android.os.RemoteException ex) {
                }
                break;
            //burning timer
            case BoxCommandDescription.CMDID_AGING_ON:
                //1. lock remote control
                mUtilImpl.remoteControlSetLock("1");
                //2. start burn timer
                mMediaImpl.agingSetTimerStatus(true);
                //3. set suto run command
                String autorunCommand = "2222" + "/" + Param;
                mLocalPropImpl.setLocalPropString(mSettingManager.FACTPROP_AUTORUN_COMMAND, autorunCommand);
                //4. open auto run switch
                mLocalPropImpl.setLocalPropBool(mSettingManager.FACTPROP_AUTORUN_STATUS, true);
                retBool = true;
                try {
                    mBinder.setResult_bool(cmdid, retBool);
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_AGING_OFF:
                //1. stop remote control
                mUtilImpl.remoteControlSetLock("0");
                mMediaImpl.agingSetTimerStatus(false);
                //7. init aging
                mMediaImpl.agingInitTimerCount();
                mLocalPropImpl.setLocalPropBool(mSettingManager.FACTPROP_AUTORUN_STATUS, false);
                if (getBoxRunningWindCmd() != null)
                    Log.i(TAG, "now some windows is running, we want close it");
                BoxSetControlMsg(getBoxRunningWindCmd(), FactorySetting.COMMAND_TASK_STOP, cmdid, param);
                break;
            case BoxCommandDescription.CMDID_AGING_RESET_TIMER:
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.agingInitTimerCount());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_AGING_GET_TIMER:
                try {
                    mBinder.setResult_byte(cmdid, mMediaImpl.agingGetTimerCount());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_AGING_TIMER_START:
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.agingSetTimerStatus(true));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_AGING_TIMER_STOP:
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.agingSetTimerStatus(false));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_UDISK_DECT_2_0://U disk 2.0 test
                try {
                    mBinder.setResult_bool(cmdid, mStorageImpl.usbHost20Test());
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_DISPLAY_SWITCH://Switch display
                try {
                    mBinder.setResult_bool(cmdid, mMediaImpl.switchDisplay(Integer.parseInt(param)));
                } catch (android.os.RemoteException ex) {
                }
                break;
            case BoxCommandDescription.CMDID_ADB_TO_USB://Switch adb to usb
                try {
                    mBinder.setResult_bool(cmdid, mStorageImpl.adbToUsb());
                } catch (android.os.RemoteException ex) {
                }
                break;
            default:
                Log.e(TAG, "handleCommand command ID not support yet!");
                break;
        }

        if (mBoxCd.getCmdTypeByID(cmdid).equals(BoxCommandDescription.CMD_TYPE_ACTIVITY_ON)) {
            Log.i(TAG, "do activity operations");
            BoxhandleCommandForActivity(c);
        }
    }

    public String changeInt2String(int src) {
        return src + "";
    }

    //by command parameter protocol, from interface layer to factorytest layer,
    //the command parameter would seperate by ",", that is to say, if command is
    //ASCII, the "," would be insert into command character by character.
    //so if the splited sub-string length is bigger than 0x1, the command won't come from
    //Interface layer.
    public String changeStringToAscII(String src) {
        int dest_byte;
        String dest = "";
        String[] srcs = null;
        srcs = src.split(",");
        for (int i = 0; i < srcs.length; i++) {
            Log.i(TAG, "srcs[" + i + "] = [" + srcs[i] + "]");
            if (srcs[i] != null && srcs[i].matches("[0-9]+")) {
                dest_byte = Integer.parseInt(srcs[i]);
                dest += (char) dest_byte;
            } else {
                Log.i(TAG, "paras is abnormal <" + srcs[i] + ">");
            }
        }
        return dest;
    }

    public String changeStringToIP(String src) {
        int dest_byte;
        String dest = "";
        Log.i(TAG, "changeStringToIP : src [" + src + "]");
        String srcs[] = src.split(",");
        for (int i = 0; i < srcs.length - 1; i++) {
            dest_byte = Integer.parseInt(srcs[i]);
            Log.i(TAG, "changeStringToIP : dest_byte [" + dest_byte + "]");
            dest += dest_byte + ".";
        }
        dest += Integer.parseInt(srcs[srcs.length - 1]);
        Log.i(TAG, "changeStringToIP : dest [" + dest + "]");
        return dest;
    }
}
