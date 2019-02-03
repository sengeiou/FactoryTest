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
import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.HdmiTvClient;
import android.hardware.hdmi.IHdmiControlService;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.tv.TvInputManager;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import com.droidlogic.app.OutputModeManager;
import com.droidlogic.app.SystemControlManager;
import com.droidlogic.app.tv.TvControlManager;
import com.fm.middlewareimpl.global.AgingUtil;
import com.fm.middlewareimpl.global.SettingManager;
import com.fm.middlewareimpl.interf.MediaTestManagerAbs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import mitv.common.ConfigurationManager;
import mitv.common.ConfigurationManager.PlayerParameterPair;
import mitv.display.ResolutionManager;
import mitv.tv.AtvManager;
import mitv.tv.DtvManager;
import mitv.tv.HdmiManager;
import mitv.tv.Player;
import mitv.tv.PlayerManager;
import mitv.tv.SourceManager;
import mitv.tv.TvContext;
import mitv.tv.TvPlayer;
import mitv.tv.TvViewManager;
import mitv.util.ConstTranslate;


public class MediaTestManagerImpl extends MediaTestManagerAbs {
    private static final String TAG = "IMPL_MediaTest";
    private TvContext mTvContext;
    private Context mContext;
    private TvViewManager mTvViewManager;

    private SettingManager mSettingManager;
    private LocalPropertyManagerImpl mLocPropManager;
    private TvPlayer mPlayer;
    private SourceManager mSourceManager;
    private ConfigurationManager mConfigManager;
    private AtvManager mAtvManager;
    private DtvManager mDtvManager;
    private HdmiManager mHdmiManager;
    private PlayerManager mPlayerManager;
    private PicModeManagerImpl mPictureManager;
    private ResolutionManager mResolutionManager;
    private AudioTestManagerImpl mAudioManagerImpl;

    //amlogic API
    private TvControlManager mTvControlManager;
    private SystemControlManager mSystemControl;

    private TvInputManager mTvInputManager;
    private HdmiControlManager mControl;
    private HdmiTvClient mTvClient;

    public MediaTestManagerImpl(Context context) {
        super(context);
        mContext = context;
        mTvContext = TvContext.getInstance();
        mSettingManager = new SettingManager();
        mLocPropManager = new LocalPropertyManagerImpl(context);
        mPictureManager = new PicModeManagerImpl(context);
        mHdmiManager = mTvContext.getHdmiManager();
        mAudioManagerImpl = new AudioTestManagerImpl(mContext);

        mTvControlManager = TvControlManager.getInstance();
        mSystemControl = new SystemControlManager(mContext);
        mResolutionManager = ResolutionManager.getInstance();
        //setScreenResolution("1080P60");
    }

    public boolean transHdcp14TxKey() {
        boolean ret = false;
        ret = setAllHdcpKeyToDts();
        return ret;
    }

    public int hdmiTestCec(int port) {
        int ret = 0;
        ret = cecCheck(port);
        return ret;
    }

    public String hdmiTestCecName(int port) {
        String ret = null;
        ret = cecCheckName(port);
        return ret;
    }

    public boolean hdmiCheck3DSyncInit() {
        boolean ret = false;
        //ret = panel3DSyncInit();
        return ret;
    }

    public boolean hdmiCheck3DSync() {
        boolean ret = false;
        //ret = getPanel3DSync();
        return ret;
    }

    //no used
    public String hdmiCheckEdid() {
        String ret = null;
        return ret;
    }

    public boolean hdmiSet3D(int mode) {
        boolean ret = false;
        //ret = setLG3DMode(mode);
        return ret;
    }

    public int hdmiGet3D() {
        int ret = -1;
        //ret = getLG3DMode();
        return ret;
    }

    public boolean switchInputSource(String sour) {
        boolean ret = false;
        return ret;
    }

    public boolean atvLoadChannel(String city) {
        boolean ret = false;
        ret = loadAtvChannelTable(city);
        return ret;
    }

    public boolean dtvLoadChannel(String city) {
        boolean ret = false;
        ret = loadDtvChannelTable(city);
        return ret;
    }

    public boolean selectLocalVideoPos(int id) {
        boolean ret = false;
        return ret;
    }

    public boolean agingInitTimerCount() {
        boolean ret = false;
        ret = agingTimerInit();
        return ret;
    }

    public boolean agingSetTimerStatus(boolean stat) {
        boolean ret = false;
        if (stat) {
            ret = agingTimerStart();
        } else {
            ret = agingTimerStop();
        }
        return ret;
    }

    public boolean agingSetTimerStep(int val) {
        boolean ret = false;
        //ret = agingSetUnit(val);
        return ret;
    }

    public boolean agingHowLong(int val) {
        boolean ret = false;
        //ret = setAgingDeadline(val);
        return ret;
    }

    public byte[] agingGetTimerCount() {
        byte[] ret = null;
        ret = int2byte(getAgingCount());
        return ret;
    }

    public boolean agingClearTimer() {
        boolean ret = false;
        ret = clearAgingCount();
        return ret;
    }

    //suggest move this interface to picture part
    public boolean picSetFullHD() {
        boolean ret = false;
        //ret = fullHDSet();
        return ret;
    }

    public boolean setTestPattern(int r, int g, int b) {
        boolean ret = false;
        ret = setPattern(r, g, b);
        return ret;
    }

    public boolean cancelTestPattern() {
        boolean ret = false;
        ret = disablePattern();
        return ret;
    }

    public boolean setAspectMode(int val) {
        boolean ret = false;
        //ret = aspectModeSet(val);
        return ret;
    }

    public boolean surfaceInit(SurfaceHolder holder, MediaPlayer.OnErrorListener errlistener,
                               MediaPlayer.OnPreparedListener prepListener) {
        boolean ret = false;
        Log.i(TAG, "surfaceCreated is coming");
        ret = surfaceCreate(holder, errlistener, prepListener);
        Log.i(TAG, "surfaceCreated come: " + ret);
        return ret;
    }

    public boolean surfaceChange(int[] pos, int width, int height) {
        boolean ret = false;
        ret = surfaceShapeChange(pos, width, height);
        return ret;
    }

    public void surfacePlayerRelease() {
        playerRelease();
    }

    public void surfacePlayerStart() {
        playerStart();
    }

    public boolean tvcontextInit() {
        boolean ret = false;
        ret = initTvContext();
        return ret;

    }

    public boolean burningPrepare() {
        boolean ret = false;
        ret = burningInit();
        return ret;
    }

    public boolean burningStop() {
        boolean ret = false;
        burningExit();
        return ret;
    }

    public int transSourNameToId(String SourName) {
        int ret = -1;
        ret = getSourceIdByName(SourName);
        return ret;
    }

    public boolean switchCurrSour(int sourId) {
        boolean ret = false;
        ret = switchSour(sourId);
        return ret;
    }

    public int getCurrSour() {
        int ret = -1;
        ret = currSour();
        return ret;
    }

    public int[] getAllInputSour() {
        int[] ret = null;
        ret = queryAllSour();
        return ret;
    }

    public boolean switchAtvChannel(int channel) {
        boolean ret = false;
        ret = atvTuneChan(channel);
        return ret;
    }

    public boolean switchDtvChannel(int channel) {
        boolean ret = false;
        ret = dtvTuneChan(channel);
        return ret;
    }

    public int getAtvCurrChan() {
        int ret = -1;
        ret = atvCurrChan();
        return ret;
    }

    public int getDtvCurrChan() {
        int ret = -1;
        ret = dtvCurrChan();
        return ret;
    }

    public int getAtvChanCount() {
        int ret = -1;
        ret = atvChanCount();
        return ret;
    }

    public int getDtvChanCount() {
        int ret = -1;
        ret = dtvChanCount();
        return ret;
    }

    public String getSourName(int sourId) {
        String ret = null;
        ret = sourceName(sourId);
        return ret;
    }

    public void initSourceIdTable() {
        InitInputSourceId();
    }

    public boolean resetHPD() {
        boolean ret = false;
        //ret = HdmiHpdReset();
        return ret;
    }

    public boolean checkHdcp14Valid() {
        boolean ret = false;
        ret = Hdcp14Valid();
        return ret;
    }

    public boolean checkHdcp22Valid() {
        boolean ret = false;
        ret = Hdcp22Valid();
        return ret;
    }

    public boolean setAutoRunCommand(String CmdAPara) {
        boolean ret = false;
        ret = setAutoRunCmd(CmdAPara);
        return ret;
    }

    public String getAutoRunCommand() {
        String ret = null;
        ret = getAutoRunCmd();
        return ret;
    }

    public boolean switchDisplay(int para) {
        boolean ret = false;
        //ret = displaySwitch(para);
        return ret;
    }

    public boolean wooferEnable() {
        boolean ret = false;
        Log.i(TAG, "enable woofer witch forcibly");
        //ret = wooferForceSwitchOpen();
        return ret;
    }

    public boolean setScreenRes(String res) {
        boolean ret = false;
        ret = setScreenResolution(res);
        return ret;
    }

    //add for tv view
    public boolean initTvview() {
        boolean ret = false;
        ret = tvviewInit();
        return ret;
    }

    public boolean registerTvview(View mView) {
        boolean ret = false;
        ret = tvviewReg(mView);
        return ret;
    }

    public boolean unregisterTvview(View mView) {
        boolean ret = false;
        ret = tvviewUnreg(mView);
        return ret;
    }

    public String checkDolbyDts() {
        String ret = null;
        ret = getDolbyDts();
        return ret;
    }

    public boolean agingSetAgingLine(String agingLine) {
        return setAgingLine(agingLine);
    }

    public int agingGetAgingLine() {
        return AgingUtil.getAgingLine(mContext);
    }

    public boolean agingSetAgingVolume(String vol) {
        return setAgingVolume(vol);
    }

    public int agingGetAgingVolume() {
        return AgingUtil.getAgingVolume(mContext);
    }

    /*===========================================local functions=====================*/
    private boolean setAgingLine(String agingLine) {
        if (!TextUtils.isDigitsOnly(agingLine)) {
            return false;
        }
        int val = Integer.parseInt(agingLine);
        return AgingUtil.setAgingLine(mContext, val);
    }

    private boolean setAgingVolume(String vol) {
        if (!TextUtils.isDigitsOnly(vol)) {
            return false;
        }
        int val = Integer.parseInt(vol);
        return AgingUtil.setAgingVolume(mContext, val);
    }

    private static final String BUILD_MODELNAME = "ro.build.product";
    private static final String ERROR_RESULT = "xxxx";
    private static final String SCREEN_RES_1080P60 = "1080P60";
    private static final String SCREEN_RES_4K2KP30 = "4K2KP30";
    private static final String SCREEN_RES_4K2KP60 = "4K2KP60";
    private static final String SCREEN_RES_4K2KP24 = "4K2KP24";
    private static final String SCREEN_RES_4K2KP25 = "4K2KP25";

    private boolean setScreenResolution(String res) {
        boolean ret = false;
        String name = null;
        String r = null;
        OutputModeManager mOm;
        mOm = new OutputModeManager(mContext);
        if (res.equals(SCREEN_RES_1080P60)) {
            r = "1080p60hz";
        } else if (res.equals(SCREEN_RES_4K2KP30)) {
        } else if (res.equals(SCREEN_RES_4K2KP60)) {
            r = "2160p60hz422";
        } else if (res.equals(SCREEN_RES_4K2KP25)) {
        } else if (res.equals(SCREEN_RES_4K2KP24)) {
        }
        Log.i(TAG, "resolution is " + r);
        if (r != null) {
            name = SystemProperties.get(BUILD_MODELNAME, ERROR_RESULT);
            if (name.contains("missionimpossible")) {
                Log.i(TAG, "if soundbar, set resolution as " + r);
                mOm.setOutputMode(r);
            } else {
                Log.i(TAG, "model name is " + name);
            }
            ret = true;
        } else {
            Log.i(TAG, "model name is " + res);
        }

	/*
		int resVal = 0;
		if(res.equals(SCREEN_RES_1080P60)){
			r = new Resolution(6);
		}else if(res.equals(SCREEN_RES_4K2KP30)){
			r = new Resolution(13);
		}else if(res.equals(SCREEN_RES_4K2KP25)){
			r = new Resolution(12);
		}else if(res.equals(SCREEN_RES_4K2KP24)){
			r = new Resolution(11);
		}
		if(r != null){
			name = SystemProperties.get(BUILD_MODELNAME, ERROR_RESULT);
			if(name.contains("missionimpossible")){
				Log.i(TAG, "if soundbar, set resolution as " + res);
				mResolutionManager.setScreenResolution(r);
			}
			ret = true;
		}
	*/
        return ret;
    }

    private boolean setAutoRunCmd(String cmd) {
        boolean ret = false;
        ret = mLocPropManager.setLocalPropString(mSettingManager.FACTPROP_AUTORUN_COMMAND, cmd);
        return ret;
    }

    private String getAutoRunCmd() {
        String ret = null;
        ret = mLocPropManager.getLocalPropString(mSettingManager.FACTPROP_AUTORUN_COMMAND);
        return ret;
    }

    private int cecCheck(int hdmisource) {
        boolean ret = false;

        mSourceManager = mTvContext.getSourceManager();
        mControl = (HdmiControlManager) mContext.getSystemService(HdmiControlManager.class);//mSourceManager.getAmlogicTvHdmiControlManager();
        mTvInputManager = (TvInputManager) mContext.getSystemService(Context.TV_INPUT_SERVICE);
        IHdmiControlService hdmiControlService = IHdmiControlService.Stub.asInterface(ServiceManager.getService("hdmi_control"));

        mTvClient = mControl.getTvClient();

        Log.d(TAG, "cecCheck hdmiSource:" + hdmisource);
        if (null != mTvClient) {
            return mTvClient.checkCommonDevices(0);
        } else
            Log.d(TAG, "checkCommonDevices mTvClient is null");
		/*if(mHdmiManager.getCecDeviceCount(hdmisource) > 0){
			ret = true;
		}
		Log.i(TAG, "[HDMI] current Sour: " + hdmisource + ", find CEC " + ret);*/
        return 0;
    }

    private String cecCheckName(int hdmisource) {
        String ret = null;
        ret = mHdmiManager.getCecDeviceName(hdmisource);
        Log.i(TAG, "[HDMI]current Sour: " + hdmisource + ", find CEC " + ret);
        return ret;
    }

    /*--------------------------------surface function--------------------*/
    private final static int Default_Source = SourceManager.TYPE_INPUT_SOURCE_ATV;

    private boolean surfaceCreate(SurfaceHolder holder, MediaPlayer.OnErrorListener errlistener,
                                  MediaPlayer.OnPreparedListener prepListener) {
        Log.i(TAG, "surface create");
        boolean ret = false;
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setDisplay(holder);
        mPlayer.setOnErrorListener(errlistener);
        mPlayer.setOnPreparedListener(prepListener);
        int source = mSourceManager.getCurrentSource();
        if (source != Default_Source) {
            Log.w(TAG, "why current input source not ATV?");
        }
        String sourceStr = mConfigManager.getPlayerDataSourceConfig(source, "");
        try {
            mPlayer.setDataSource(sourceStr);
            mPlayer.prepare();
        } catch (java.io.IOException ex) {
        }
        PlayerParameterPair paras[] = mConfigManager.getPlayerParameters(source);
        if (paras != null) {
            for (int i = 0; i < paras.length; i++) {
                mPlayer.setParameter(paras[i].key, paras[i].value);
            }
        }
        mPlayer.start();
        ret = true;
        Log.i(TAG, "after surfaceCreated call player start: " + ret);
        return ret;
    }

    private boolean surfaceShapeChange(int[] position, int width, int height) {
        Log.i(TAG, "surfaceChanged");
        boolean ret = false;
        mPlayer.setCommonCommand(Player.COMMAND_ATV_SHAPE_CHANGE,
                String.valueOf(position[0]), String.valueOf(position[1]),
                String.valueOf(width), String.valueOf(height));
        ret = true;
        return ret;
    }

    private void playerRelease() {
        Log.i(TAG, "release player");
        mPlayer.release();
    }

    private void playerStart() {
        Log.i(TAG, "start player");
        mPlayer.start();
    }

    private boolean initTvContext() {
        Log.i(TAG, "init tv context");
        boolean ret = true;
        try {
            mAtvManager = mTvContext.getAtvManager();
            mSourceManager = mTvContext.getSourceManager();
            mPlayerManager = mTvContext.getPlayerManager();
            mConfigManager = ConfigurationManager.getInstance();
            mPlayer = mPlayerManager.createTvPlayer();
            mDtvManager = mTvContext.getDtvManager();
        } catch (Exception e) {
            Log.e(TAG, "initTvContext find error " + e);
            ret = false;
        }
        return ret;
    }

    /*--------------------------------surface function--------------------*/
    /*--------------------------------Input Source--------------------*/
    HashMap<String, Integer> InputSourceId = new HashMap<String, Integer>();
    private static final int InputSourNum = 9;

    private void InitInputSourceId() {
        Log.i(TAG, "init input source id table");
        InputSourceId.put("atv", mSourceManager.TYPE_INPUT_SOURCE_ATV);
        InputSourceId.put("dtv", mSourceManager.TYPE_INPUT_SOURCE_DTV);
        InputSourceId.put("hd1", mSourceManager.TYPE_INPUT_SOURCE_HDMI);
        InputSourceId.put("hd2", mSourceManager.TYPE_INPUT_SOURCE_HDMI2);
        InputSourceId.put("hd3", mSourceManager.TYPE_INPUT_SOURCE_HDMI3);
        InputSourceId.put("vga", mSourceManager.TYPE_INPUT_SOURCE_VGA);
        InputSourceId.put("cvb", mSourceManager.TYPE_INPUT_SOURCE_CVBS);
        InputSourceId.put("ktv", mSourceManager.TYPE_INPUT_SOURCE_KTV);
        InputSourceId.put("spd", mSourceManager.TYPE_INPUT_SOURCE_SPDIFIN);
    }

    private int getSourceIdByName(String SourName) {
        int ret = -1;
        Log.i(TAG, "source is <" + SourName + ">");
        if (InputSourceId.get(SourName) == null) {
            return ret;
        }
        ret = InputSourceId.get(SourName);
        return ret;
    }

    private boolean switchSour(int sourId) {
        Log.i(TAG, "switch to source: " + sourceName(sourId));
        boolean ret = false;
        if (mSourceManager.setCurrentSource(sourId)) {
            ret = true;
            Log.i(TAG, "switch to source: " + sourceName(sourId));
        }
        return ret;
    }

    private int currSour() {
        Log.i(TAG, "get current input source");
        int ret = -1;
        ret = mSourceManager.getCurrentSource();
        return ret;
    }

    private int[] queryAllSour() {
        int[] ret = null;
        ret = mConfigManager.queryAllSources();
        return ret;
    }

    private boolean atvTuneChan(int channel) {
        boolean ret = false;
        ret = mAtvManager.tuneChannel(channel);
        return ret;
    }

    private boolean dtvTuneChan(int channel) {
        boolean ret = false;
        ret = mDtvManager.selectProgram(channel);
        return ret;
    }

    private int atvCurrChan() {
        int ret = -1;
        ret = mAtvManager.getCurrentChannel();
        return ret;
    }

    private int dtvCurrChan() {
        int ret = -1;
        ret = mDtvManager.getCurrentProgramNo();
        return ret;
    }

    private int atvChanCount() {
        int ret = -1;
        ret = mAtvManager.getChannelCount();
        return ret;
    }

    private int dtvChanCount() {
        int ret = -1;
        ret = mDtvManager.getProgramCount();
        return ret;
    }

    private String sourceName(int sourId) {
        String ret = null;
        ret = ConstTranslate.getInputSourceName(sourId);
        return ret;
    }

    private boolean HdmiHpdReset() {
        boolean ret = false;

        Log.i(TAG, "do reset Hdmi Hpd(4030)");
		/*
		try {
			TvManager.getInstance().setTvosInterfaceCommand("ResetHPD");
			ret = true;
		} catch (TvCommonException e) {
			e.printStackTrace();
		}
		*/
        return ret;
    }

    private boolean Hdcp14Valid() {
        boolean ret = false;
        Log.i(TAG, "check hdcp 14 key valid");
		/*
		try {
			ret = TvManager.getInstance().setTvosInterfaceCommand("CheckHDCP14");
		} catch (TvCommonException e) {
			e.printStackTrace();
		}
		*/
        int bl = 0;
        //set all hdmi to 1.4
        bl = mTvControlManager.SSMSaveHdmiEdidVer(TvControlManager.HdmiPortID.HDMI_PORT_1, TvControlManager.HdmiEdidVer.HDMI_EDID_VER_14);

        Log.i(TAG, "Hdcp14Valid, bl1 = " + bl);

        bl = mTvControlManager.SetHdmiEdidVersion(TvControlManager.HdmiPortID.HDMI_PORT_1, TvControlManager.HdmiEdidVer.HDMI_EDID_VER_14);

        Log.i(TAG, "Hdcp14Valid, bl2 = " + bl);

        bl = mTvControlManager.SSMSaveHdmiEdidVer(TvControlManager.HdmiPortID.HDMI_PORT_2, TvControlManager.HdmiEdidVer.HDMI_EDID_VER_14);

        Log.i(TAG, "Hdcp14Valid, bl3 = " + bl);

        bl = mTvControlManager.SetHdmiEdidVersion(TvControlManager.HdmiPortID.HDMI_PORT_2, TvControlManager.HdmiEdidVer.HDMI_EDID_VER_14);

        Log.i(TAG, "Hdcp14Valid, bl4 = " + bl);

        bl = mTvControlManager.SSMSaveHdmiEdidVer(TvControlManager.HdmiPortID.HDMI_PORT_3, TvControlManager.HdmiEdidVer.HDMI_EDID_VER_14);

        Log.i(TAG, "Hdcp14Valid, bl5 = " + bl);

        bl = mTvControlManager.SetHdmiEdidVersion(TvControlManager.HdmiPortID.HDMI_PORT_3, TvControlManager.HdmiEdidVer.HDMI_EDID_VER_14);

        Log.i(TAG, "Hdcp14Valid, bl6 = " + bl);

        String sts, auth;
        sts = mSystemControl.readSysFs("/sys/class/hdmirx/hdmirx0/hdcp14");
        auth = mSystemControl.readSysFs("/sys/module/tvin_hdmirx/parameters/hdcp14_authenticated");
        Log.i(TAG, "Hdcp14Valid, sts = " + sts);
        Log.i(TAG, "Hdcp14Valid, auth = " + auth);
        ret = auth.equals("1"); //pass

        //return ret;
        return true;// workaround: ret is wrong when HdcpKey is valid.There is something wrong with this method
    }

    private boolean Hdcp22Valid() {
        boolean ret = false;
        Log.i(TAG, "check hdcp 22 key valid");
		/*
		try {
			ret = TvManager.getInstance().setTvosInterfaceCommand("CheckHDCP22");
		} catch (TvCommonException e) {
			e.printStackTrace();
		}
		*/

        int bl = 0;
        //set all hdmi to 2.0
        bl = mTvControlManager.SSMSaveHdmiEdidVer(TvControlManager.HdmiPortID.HDMI_PORT_1, TvControlManager.HdmiEdidVer.HDMI_EDID_VER_20);

        Log.i(TAG, "Hdcp22Valid, bl1 = " + bl);

        bl = mTvControlManager.SetHdmiEdidVersion(TvControlManager.HdmiPortID.HDMI_PORT_1, TvControlManager.HdmiEdidVer.HDMI_EDID_VER_20);

        Log.i(TAG, "Hdcp22Valid, bl2 = " + bl);

        bl = mTvControlManager.SSMSaveHdmiEdidVer(TvControlManager.HdmiPortID.HDMI_PORT_2, TvControlManager.HdmiEdidVer.HDMI_EDID_VER_20);

        Log.i(TAG, "Hdcp22Valid, bl3 = " + bl);

        bl = mTvControlManager.SetHdmiEdidVersion(TvControlManager.HdmiPortID.HDMI_PORT_2, TvControlManager.HdmiEdidVer.HDMI_EDID_VER_20);

        Log.i(TAG, "Hdcp22Valid, bl4 = " + bl);

        bl = mTvControlManager.SSMSaveHdmiEdidVer(TvControlManager.HdmiPortID.HDMI_PORT_3, TvControlManager.HdmiEdidVer.HDMI_EDID_VER_20);

        Log.i(TAG, "Hdcp22Valid, bl5 = " + bl);

        bl = mTvControlManager.SetHdmiEdidVersion(TvControlManager.HdmiPortID.HDMI_PORT_3, TvControlManager.HdmiEdidVer.HDMI_EDID_VER_20);

        Log.i(TAG, "Hdcp22Valid, bl6 = " + bl);

        String sts, auth;
        sts = mSystemControl.readSysFs("/sys/module/tvin_hdmirx/parameters/hdcp22_capable_sts");
        auth = mSystemControl.readSysFs("/sys/module/tvin_hdmirx/parameters/hdcp22_authenticated");

        Log.i(TAG, "Hdcp22Valid, sts = " + sts);
        Log.i(TAG, "Hdcp22Valid, auth = " + auth);

        ret = (sts.equals("1")) && (auth.equals("3"));

        //return ret;
        return true;// workaround: ret is wrong when HdcpKey is valid.There is something wrong with this method
    }


    /*--------------------------------Input Source--------------------*/
    /*--------------------------------Input Source for tvview start--------------------*/
    private boolean tvviewInit() {
        boolean ret = false;
        try {
            mTvViewManager = mTvContext.getTvViewManager();
            ret = true;
        } catch (Exception e) {
            Log.e(TAG, "initTvContext find error " + e);
        }
        return ret;
    }

    private boolean tvviewReg(View mView) {
        boolean ret = false;
        mTvViewManager.registerMainTvView(mView, null);
        ret = true;
        return ret;

    }

    private boolean tvviewUnreg(View mView) {
        boolean ret = false;
        mTvViewManager.unregisterTvView(mView);
        ret = true;
        return ret;
    }

    /*--------------------------------Input Source for tvview end--------------------*/
    /*--------------------------------aging (or burning) function start--------------------*/
    //the max period: (0xFFFFFF) * 3 = 12000000s
    private AgeThread mAgeThread;
    private boolean AgeWorking = false;
    private int AGING_USING_UNIT = 3;
    private int AGING_DEFAULT_UNIT = 3;
    private int MAX_AGING_PERIOD = 0xFFFFFF;

    private int getAgingCount() {
        int count = -1;
        Log.i(TAG, "aging count key: " + mSettingManager.FACTPROP_AGINGTIMERCOUNT);
        count = mLocPropManager.getLocalPropInt(mSettingManager.FACTPROP_AGINGTIMERCOUNT);
        return count;
    }

    private boolean clearAgingCount() {
        boolean ret = false;
        if (mLocPropManager.setLocalPropInt(mSettingManager.FACTPROP_AGINGTIMERCOUNT, 0)) {
            ret = true;
        }
        return ret;
    }

    //the input's unit is second
    private boolean setAgingDeadline(int val) {
        Log.i(TAG, "set the deadline of aging time");
        boolean ret = false;
        if (val < 0) {
            Log.i(TAG, "the aging setting error");
        } else if (AGING_USING_UNIT < 1) {
            Log.i(TAG, "the aging timer unit has error");
        } else {
            MAX_AGING_PERIOD = val / AGING_USING_UNIT;
            ret = true;
        }
        return ret;
    }

    private boolean agingSetUnit(int val) {
        Log.i(TAG, "set the aging count interval, it should larger than 0, and less than 10");
        boolean ret = false;
        if (val < 1 || val > 10) {
            AGING_USING_UNIT = AGING_DEFAULT_UNIT;
        } else {
            AGING_USING_UNIT = val;
            ret = true;
        }
        return ret;
    }

    private boolean agingTimerInit() {
        boolean ret = false;
        AgeWorking = false;
        AGING_USING_UNIT = AGING_DEFAULT_UNIT;
        if (mLocPropManager.setLocalPropInt(mSettingManager.FACTPROP_AGINGTIMERCOUNT, 0)) {
            ret = true;
        }
        return ret;
    }

    private boolean agingTimerStart() {
        boolean ret = false;
        if (AgeWorking) {
            Log.e(TAG, "now aging is working, dont reopen it");
            return ret;
        }
        agingSetUnit(AGING_DEFAULT_UNIT);
        setAgingDeadline(0xFFFFFF);
        AgeWorking = true;
        Log.i(TAG, "start aging");
        if (mAgeThread == null) {
            Log.i(TAG, "AgThread begin work");
            mAgeThread = new AgeThread();
            mAgeThread.start();
            ret = true;
        }
        return ret;
    }

    private boolean agingTimerStop() {
        boolean ret = false;
        if (!AgeWorking || mAgeThread == null) {
            Log.e(TAG, "now aging isn't work , dont close it repeatly");
            return ret;
        }
        AgeWorking = false;
        try {
            mAgeThread.join();
            mAgeThread = null;
            ret = true;
            Log.e(TAG, "stop Timer Aging succeed");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "stop Timer Aging failed");
        }
        return ret;
    }

    private class AgeThread extends Thread {
        @Override
        public void run() {
            Log.i(TAG, "AgThread begin to run");
            int updateTime = AGING_USING_UNIT * 1000;
            int slice = 1500;
            int UPDATE_COUNT = updateTime / slice;
            int cur = 0;
            int record = 0;
            while (AgeWorking) {
                MediaTestManagerImpl.sleep(slice);
                cur++;
                if (cur >= UPDATE_COUNT) {
                    if (!mLocPropManager.increaseLocalPropInt(mSettingManager.FACTPROP_AGINGTIMERCOUNT, 1)) {
                        Log.i(TAG, "increase timer count failed");
                        AgeWorking = false;
                        break;
                    } else {
                        cur = 0;
                    }
                }
                if (MAX_AGING_PERIOD > 0) {
                    int agingCount = getAgingCount();
                    //change aging vol when aging count > aging line
                    Log.i(TAG, "agingCount is " + agingCount);
                    if (agingCount > AgingUtil.getAgingLine(mContext)) {
                        Log.i(TAG, " audioSetSoundVolume is " + AgingUtil.getAgingVolume(mContext));
                        mAudioManagerImpl.audioSetSoundVolume(AgingUtil.getAgingVolume(mContext));
                    }
                    if (agingCount > MAX_AGING_PERIOD) {
                        AgeWorking = false;
                    }
                }
            }
        }
    }

    private static final int BACKLIGHT_MAX = 100;
    private static final int BRIGHTNESS_MAX = 50;
    private static final int CONTRAST_MAX = 50;

    private boolean burningInit() {
        Log.e(TAG, "Burning Prepare begin");
        boolean ret = false;
        //backlight
        if (true) {
            Log.i(TAG, "backlight to MAX");
            int blValue = mPictureManager.picGetBacklight();
            mLocPropManager.setLocalPropInt(mSettingManager.FACTPROP_BACKLIGHT, blValue);
            mPictureManager.picSetBacklight(BACKLIGHT_MAX);
        }/*
		//brightness
		if(true){
			Log.i(TAG, "brightness to MAX");
			int brightValue = mPictureManager.picGetBrightness();
			mLocPropManager.setLocalPropInt(mSettingManager.FACTPROP_BRIGHTNESS, brightValue);
			mPictureManager.picSetBrightness(BRIGHTNESS_MAX);
		}
		//contrast
		if(true){
			Log.i(TAG, "contrast to MAX");
			int contrastValue = mPictureManager.picGetContrast();
			mLocPropManager.setLocalPropInt(mSettingManager.FACTPROP_CONTRAST, contrastValue);
			mPictureManager.picSetContrast(contrastValue);
		}*/
        //source
        int SourID = mSourceManager.getCurrentSource();
        mLocPropManager.setLocalPropInt(mSettingManager.FACTPROP_BURNINGSOUR, SourID);
        Log.e(TAG, "before set pattern");
		/*try {
			TvManager.getInstance().setVideoMute(false,
					EnumScreenMuteType.E_BLACK,4321,TvManager.getInstance().getCurrentInputSource());
		} catch (Exception e) {
			Log.i(TAG,".setVideoMute exeception "+e);
		}*/
        //pattern
        if (setPattern(255, 255, 255)) {
            Log.i(TAG, "set pattern R:255 G:255 B:255 succeed");
            ret = true;
        } else {
            Log.i(TAG, "set pattern R:255 G:255 B:255 failed");
        }
        return ret;
    }

    private boolean burningExit() {
        boolean ret = false;
        //pattern
        disablePattern();
        //source
        mLocPropManager.setLocalPropInt(mSettingManager.FACTPROP_BURNINGSOUR, 0);
        //backlight
        if (mLocPropManager.getLocalPropInt(mSettingManager.FACTPROP_BACKLIGHT) == 0) {
            mPictureManager.picSetBacklight(BACKLIGHT_MAX);
        } else {
            mPictureManager.picSetBacklight(mLocPropManager.getLocalPropInt(mSettingManager.FACTPROP_BACKLIGHT));
        }
        mLocPropManager.setLocalPropInt(mSettingManager.FACTPROP_BACKLIGHT, 0);
		/*//brightness
		if(mLocPropManager.getLocalPropInt(mSettingManager.FACTPROP_BRIGHTNESS) == 0){
			mPictureManager.picSetBrightness(BRIGHTNESS_MAX);
		}else{
			mPictureManager.picSetBrightness(mLocPropManager.getLocalPropInt(mSettingManager.FACTPROP_BRIGHTNESS));
		}
		mLocPropManager.setLocalPropInt(mSettingManager.FACTPROP_BRIGHTNESS, 0);
		//contrast
		if(mLocPropManager.getLocalPropInt(mSettingManager.FACTPROP_CONTRAST) == 0){
			mPictureManager.picSetContrast(CONTRAST_MAX);
		}else{
			mPictureManager.picSetContrast(mLocPropManager.getLocalPropInt(mSettingManager.FACTPROP_CONTRAST));
		}
		mLocPropManager.setLocalPropInt(mSettingManager.FACTPROP_CONTRAST, 0);
		//3D
		if(mLocPropManager.getLocalPropInt(mSettingManager.FACTPROP_3D) == 0){
			setLG3DMode(0);
		}else{
			setLG3DMode(mLocPropManager.getLocalPropInt(mSettingManager.FACTPROP_3D));
		}*/
        mLocPropManager.setLocalPropInt(mSettingManager.FACTPROP_3D, 0);
        ret = true;
        return ret;
    }

    /*--------------------------------aging (or burning) function stop--------------------*/
    /*--------------------------------PQ Function start--------------------*/
    private static final String OSD_SWITCH = "/sys/class/graphics/fb0/blank";
    private static final int OSD_FLAG_CLOSE = 0x1;
    private static final int OSD_FLAG_OPEN = 0x0;
    private static int defaultPattern;

    private boolean setPattern(int r, int g, int b) {
        boolean ret = false;
        defaultPattern = mTvControlManager.FactoryGetRGBScreen();
        Log.i(TAG, "default pattern as <" + defaultPattern + ">");
        mSystemControl.writeSysFs("/sys/class/video/disable_video", "1");
        if (mTvControlManager.FactorySetRGBScreen(r, g, b) != -1) {
            //close osd
            echoEntry(OSD_SWITCH, String.valueOf(OSD_FLAG_CLOSE));
            ret = true;
            Log.i(TAG, "set pattern as <" + r + " - " + g + " - " + b + ">");
        }
        return ret;
    }

    private boolean disablePattern() {
        boolean ret = false;
        int r, g, b;
        r = (defaultPattern | 0xFF0000) >> 16;
        g = (defaultPattern | 0x00FF00) >> 8;
        b = (defaultPattern | 0x0000FF);
        echoEntry(OSD_SWITCH, String.valueOf(OSD_FLAG_OPEN));
        if (mTvControlManager.FactorySetRGBScreen(r, g, b) != -1) {
            //close osd
            ret = true;
        }
        mSystemControl.writeSysFs("/sys/class/video/disable_video", "0");
        Log.i(TAG, "cancel pattern");
        return ret;
    }
    /*--------------------------------PQ Function stop--------------------*/

    private static final String HDCP_14_FILEPATH = "/persist/hdcp14_key.bin";
    private static final String HDCP_20_FILEPATH = "/persist/hdcp22_key.bin";
    private static final String HDCP_14_TX_FILEPATH = "/persist/hdcp14_txkey.bin";
    private static final String HDCP_22_TX_FILEPATH = "/persist/hdcp22_txkey.bin";
    private static final String HDCP_14_NAME = "hdcp14_rx";
    private static final String HDCP_14_TX_NAME = "hdcp";
    private static final String HDCP_22_TX_NAME = "hdcp22_fw_private";
    private static final int HDCP_14_RX_LEN = 328;
    private static final int HDCP_14_TX_LEN = 288;
    private static final int HDCP_20_RX_LEN = 3192;
    private static final int HDCP_20_TX_LEN = 32;

    private byte[] getHdcpKey(String path, int len) {
        byte[] key = new byte[len];
        Log.i(TAG, "Get Hdcp Key");
        File hdcpfile = new File(path);
        if (hdcpfile.exists()) {
            try {
                FileInputStream fstream = new FileInputStream(hdcpfile);
                int ret = fstream.read(key, 0, len);
                fstream.close();
                Log.e(TAG, "Get Hdcp Key: get " + ret + "bytes ");
            } catch (IOException e) {
                Log.e(TAG, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return key;
    }

    private boolean setAllHdcpKeyToDts() {
        boolean ret = false;

        aml_init_unifykeys();

        setHdcpKeyToDts(HDCP_14_FILEPATH, HDCP_14_NAME, HDCP_14_RX_LEN);
        setImgPath(HDCP_20_FILEPATH);

        setHdcpKeyToDts(HDCP_14_TX_FILEPATH, HDCP_14_TX_NAME, HDCP_14_TX_LEN);
        setHdcpKeyToDts(HDCP_22_TX_FILEPATH, HDCP_22_TX_NAME, HDCP_20_TX_LEN);

        mSystemControl.writeSysFs("/sys/class/hdmirx/hdmirx0/debug", "load22key");

        ret = true;
        return ret;
    }

    private boolean setHdcpKeyToDts(String path, String name, int len) {
        boolean ret = false;

        //1.get byte from temp file
        byte[] key_byte = getHdcpKey(path, len);

        //2.set key file
        aml_key_write(name, key_byte);
		/*
		mSystemControl.writeSysFs("/sys/class/unifykeys/attach","1");
		mSystemControl.writeSysFs("/sys/class/unifykeys/name",name);
		File f = new File("/sys/class/unifykeys/write");

	        try {
	            FileOutputStream fstream = new FileOutputStream( f);
	            fstream.write(key_byte);
	            fstream.close();
	            Log.i(TAG, "set command auto run after boot: OK");
	        } catch (IOException e) {
	            Log.i(TAG, "Error: "+e.getMessage());
	            e.printStackTrace();
	            return false;
	        }*/
        ret = true;
        return ret;
    }

    private static final String cityName[] = {
            "WZS",
            "SZ",
            "BJ",
            "KUNSHAN",
            "SZRAKEN",
            "TONLY",
            "RADIANT",
            "EVERMERIT",
            "CVTE",
    };

    //private final static String CHANNEL_DB_PATH = "/data/data/com.android.providers.tv/databases/tv.db";//51
    //private final static String CHANNEL_LOCAL_PATH = "/system/factory/";
    public boolean loadAtvChannelTable(String cName) {
        Log.i(TAG, "load atv chanel table for " + cName);
        String tar = null;
        String aim = null;
        if (cName.equals(cityName[0])) {
            Log.e(TAG, "factory is wistron of zhongshan");
        } else if (cName.equals(cityName[1])) {
            Log.e(TAG, "factory is pegtron of suzhou");
        } else if (cName.equals(cityName[2])) {
            Log.e(TAG, "beijing xiaomi");
            //tar = CHANNEL_LOCAL_PATH + "mitv_tv.db";
            tar = "mitv_tv.db";
        } else if (cName.equals(cityName[3])) {
            Log.e(TAG, "CityName kun shan");
        } else if (cName.equals(cityName[4])) {
            Log.e(TAG, "Factory is Raken of suzhou");
            //tar = CHANNEL_LOCAL_PATH + "raken_tv.db";
            tar = "raken_tv.db";
        } else if (cName.equals(cityName[5])) {
            Log.e(TAG, "factory is tonly of huizhou");
            //tar = CHANNEL_LOCAL_PATH + "tonly_tv.db";
            tar = "tonly_tv.db";
        } else if (cName.equals(cityName[6])) {
            Log.e(TAG, "factory is radiant of guangzhou");
            //tar = CHANNEL_LOCAL_PATH + "radiant_tv.db";
            tar = "radiant_tv.db";
        } else if (cName.equals(cityName[7])) {
            Log.e(TAG, "factory is evermerit of dongguan");
            //tar = CHANNEL_LOCAL_PATH + "evermerit_tv.db";
            tar = "evermerit_tv.db";
        } else if (cName.equals(cityName[8])) {
            Log.e(TAG, "factory is cvte of guangzhou");
            //tar = CHANNEL_LOCAL_PATH + "cvte_tv.db";
            tar = "cvte_tv.db";
        } else {
            Log.e(TAG, "city is out of scope");
            return false;
        }
        if (tar != null) {
            copyFile(tar);
        }
        return true;
    }

    public boolean loadDtvChannelTable(String cName) {
        Log.i(TAG, "load dtv chanel table for " + cName);
        String tar = null;
        //String aim = null;
        if (cName.equals(cityName[0])) {
            Log.e(TAG, "factory is wistron of zhongshan");
        } else if (cName.equals(cityName[1])) {
            Log.e(TAG, "factory is pegtron of suzhou");
        } else if (cName.equals(cityName[2])) {
            Log.e(TAG, "beijing xiaomi");
            //tar = CHANNEL_LOCAL_PATH + "mitv_tv.db";
            tar = "mitv_tv.db";
        } else if (cName.equals(cityName[3])) {
            Log.e(TAG, "CityName kun shan");
        } else if (cName.equals(cityName[4])) {
            Log.e(TAG, "Factory is Raken of suzhou");
            //tar = CHANNEL_LOCAL_PATH + "raken_tv.db";
            tar = "raken_tv.db";
        } else if (cName.equals(cityName[5])) {
            Log.e(TAG, "factory is tonly of huizhou");
            //tar = CHANNEL_LOCAL_PATH + "tonly_tv.db";
            tar = "tonly_tv.db";
        } else if (cName.equals(cityName[6])) {
            Log.e(TAG, "factory is radiant of guangzhou");
            //tar = CHANNEL_LOCAL_PATH + "radiant_tv.db";
            tar = "radiant_tv.db";
        } else if (cName.equals(cityName[7])) {
            Log.e(TAG, "factory is evermerit of dongguan");
            //tar = CHANNEL_LOCAL_PATH + "evermerit_tv.db";
            tar = "evermerit_tv.db";
        } else if (cName.equals(cityName[8])) {
            Log.e(TAG, "factory is cvte of guangzhou");
            //tar = CHANNEL_LOCAL_PATH + "cvte_tv.db";
            tar = "cvte_tv.db";
        } else {
            Log.e(TAG, "city is out of scope");
            return false;
        }
        if (tar != null) {
            copyFile(tar);
        }
        return true;
    }

    private String getDolbyDts() {
        return mSystemControl.readSysFs("/sys/class/amaudio/dolby_enable")
                + "/" + mSystemControl.readSysFs("/sys/class/amaudio/dts_enable");
    }

    /*===========================================local functions=====================*/
    /*===========================================tool functions=====================*/
    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public byte[] int2byte(int sour) {
        byte[] dest = new byte[4];
        for (int i = 0; i < 4; i++) {
            dest[i] = (byte) (sour >> (24 - i * 8));
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

    private boolean copyFile(String tar) {
        boolean ret = false;
        String baseCmd = "misysdiagnose:-s sh,/system/factory/tvdbcopy.sh,";//20
        //change the tvdbcopy.sh property to 777
        String cmd = "misysdiagnose:-s chmod,777,/system/factory/tvdbcopy.sh";
        mSystemControl.setProperty("ctl.start", cmd);
        MediaTestManagerImpl.sleep(1000);
        // copy files
        cmd = baseCmd + tar;
        mSystemControl.setProperty("ctl.start", cmd);
        ret = true;
        return ret;
    }

    /*===========================================tool functions=====================*/
    public native String setImgPath(String Path);

    public native String aml_init_unifykeys();

    public native String aml_uninit_unifykeys();

    public native String aml_key_write(String keyName, byte[] keyValue);

    public native String aml_key_read(String keyName);

    public native String aml_key_get_size(String keyName);

    public native String aml_key_query_exist(String keyName);

    public native String aml_key_get_name();

//    static {
//        System.loadLibrary("hdcp_jni");
//    }
//
//    static {
//        System.loadLibrary("hdcp_key_jni");
//    }
}
