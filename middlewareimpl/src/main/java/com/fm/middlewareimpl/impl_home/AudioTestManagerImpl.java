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

package com.fm.middlewareimpl.impl_home;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;

import com.droidlogic.app.tv.TvControlManager;
import com.droidlogic.app.tv.TvHdmiArc;
import com.fm.middlewareimpl.global.SettingManager;
import com.fm.middlewareimpl.interf.AudioTestManagerAbs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import mitv.sound.SoundManager;

public class AudioTestManagerImpl extends AudioTestManagerAbs {
    private static final String TAG = "IMPL_AudioTest";
    private AudioManager mAudioManager;
    private SettingManager mSetManager;
    //
    //amlogic API
    private TvControlManager mTvControlManager;
    private TvHdmiArc mTvHdmiArc;
    //Mitv SDK
    private SoundManager mSoundManager = null;

    public AudioTestManagerImpl(Context context) {
        super(context);
        mTvControlManager = TvControlManager.getInstance();
        mTvHdmiArc = new TvHdmiArc(context);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mSetManager = new SettingManager();
        mSoundManager = SoundManager.getInstance();
    }

    public int audioGetSoundVolume() {
        int ret = 0;
        ret = getVolume();
        return ret;
    }

    public boolean audioSetSoundVolume(int val) {
        boolean ret = false;
        ret = setVolume(val);
        return ret;
    }

    public int audioGetMaxSoundVolume() {
        int ret = getMaxVolume();
        return ret;
    }

    public int audioGetSoundBalance() {
        int ret = -1;
        ret = soundBalanceGet();
        return 0;
    }

    public boolean audioSetSoundBalance(int val) {
        boolean ret = false;
        ret = soundBalanceSet(val);
        return ret;
    }

    public int audioGetSoundMode() {
        //	int ret = 0;
        return audioGetSoundEffectMode();
        //	ret = soundModeGet();
        //	return ret;
    }

    public boolean audioSetSoundMode(int val) {
        return audioSetSoundEffectMode(val);
        //	boolean ret = false;
        //	ret = soundModeSet(val);
        //	return ret;
    }

    public boolean audioResetSoundMode() {
        boolean ret = false;
        ret = soundModeReset();
        return ret;
    }

    public boolean audioSwitchSpdif(int stat) {
        boolean ret = false;
        ret = setSpdifState(stat);
        return ret;
    }

    public boolean audioSwitchSpeaker(boolean enable) {
        boolean ret = false;
        ret = setSpeakerState(enable);
        return ret;
    }

    public boolean audioSwitchLineOut(boolean state) {
        boolean ret = false;
        ret = setLineoutState(state);
        return ret;
    }

    public boolean audioSetSoundMute() {
        boolean ret = false;
        ret = setSoundMute();
        return ret;
    }

    public boolean closeDTS_DOLBY() {
        boolean ret = false;
        //ret = setDapOff();
        return ret;
    }

    public boolean audioSwitchArc(int stat) {
        boolean ret = false;
        ret = setArcState(stat);
        return ret;
    }

    public boolean audioOutputMode(int stat) {
        boolean ret = true;
        ret = switchOutMode(stat);
        return ret;
    }

    public boolean speakerswitch(int stat) {
        boolean ret = true;
        int i;
        String[][] channel = new String[6][2];
        String command = null;

        channel[0][0] = "17";
        channel[1][0] = "33";
        channel[2][0] = "25";
        channel[3][0] = "24";
        channel[4][0] = "32";
        channel[5][0] = "16";

        for (i = 0; i < 6; i++) {
            if ((stat & (1 << i)) > 0)
                channel[i][1] = "1";
            else
                channel[i][1] = "0";
            command = "/system/bin/tinymix " + channel[i][0] + " " + channel[i][1];
            Log.i(TAG, "command is " + command);
            try {
                Runtime.getRuntime().exec(command);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public boolean closeDap() {
        boolean ret = true;
        String command = "";
        Log.i(TAG, "closeDap !");

        command = "echo on > /sys/class/dsp/c_factory";
        Log.i(TAG, "command is " + command);
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

		/*
		command = "/system/bin/tinymix 37 0xBA000000 0x00000000";
		Log.i(TAG, "command is " + command);
		try{
			Runtime.getRuntime().exec(command);
		} catch(IOException e){
			e.printStackTrace();
		}
		command = "/system/bin/tinymix 37 0xDC0003C1 0xffffffff";
		Log.i(TAG, "command is " + command);
		try{
			Runtime.getRuntime().exec(command);
		} catch(IOException e){
			e.printStackTrace();
		}
		*/
        return ret;
    }

    /*===========================================local functions=====================*/
    private boolean setVolume(int val) {
        boolean ret = false;
        Log.i(TAG, "set voice volume is " + val);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, val, 0);
        ret = true;
        return ret;
    }

    private int getVolume() {
        int ret = -1;
        ret = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "get voice volume is " + ret);
        return ret;
    }

    private int getMaxVolume() {
        int ret = 0;
        ret = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "getMaxVolume:" + ret);
        return ret;
    }

    private boolean soundBalanceSet(int val) {
        boolean ret = false;
        Log.i(TAG, "set sound balance is " + val);
        if (mTvControlManager.SetAudioBalance(val) == 0) {
            ret = true;
        }
        return ret;
    }

    private int soundBalanceGet() {
        int ret = -1;
        ret = mTvControlManager.GetSaveAudioBalance();
        Log.i(TAG, "get set sound balance is " + ret);
        return ret;
    }

    private boolean soundModeSet(int val) {
        boolean ret = false;
        Log.i(TAG, "set sound mode is " + val);
		/*
		if(mTvControlManager.SetAudioSoundMode(
					mTvControlManager.Sound_Mode(val)) == 0){
			ret = true;
		}*/
        return ret;
    }

    private int soundModeGet() {
        int ret = -1;
        //ret = mTvControlManager.GetCurAudioSoundMode();
        return ret;
    }

    private boolean soundModeReset() {
        boolean ret = false;
        return ret;
    }

    private boolean setSpdifState(int st) {
        boolean ret = false;
        //public static final int AUDIO_MUTE_ON = 0;
        //public static final int AUDIO_MUTE_OFF = 1;
        if (st == 2) {
            st = 1;
        }
        Log.i(TAG, "set spidf state is " + st);
        if (mTvControlManager.SetAudioSPDIFMute(st) == 0) {
            ret = true;
        }
        return ret;
    }

    private boolean setArcState(int st) {
        boolean ret = false;
        //public static final int AUDIO_MUTE_ON = 0;
        //public static final int AUDIO_MUTE_OFF = 1;
        Log.i(TAG, "set HDMI ARC state is " + st);
        if (st == 0) {
            mTvHdmiArc.setArcEnable(true);
            ret = true;
        } else if (st == 1) {
            //1. switch to lineout
            switchOutMode(3);
            //2. mute line out
            setLineoutState(false);
            //3. mute arc
            mTvHdmiArc.setArcEnable(false);
            ret = true;
        } else {
            ret = false;
        }
		/*
		if(st == 2){
			SystemProperties.set(mSetManager.SYSPROP_ARC, "false");
		}else if(st == 0){
			SystemProperties.set(mSetManager.SYSPROP_ARC, "true");
		}
		*/
        return ret;
    }

    private boolean setSpeakerState(boolean enable) {
        boolean ret = false;
        int st = 0;
        Log.i(TAG, "set speaker state is " + enable);
        if (enable) {
            st = 1;
        }
        if (mTvControlManager.SetAudioMuteKeyStatus(st) == 0) {
            ret = true;
        }
        return ret;
    }

    private boolean setLineoutState(boolean state) {
        Log.i(TAG, "set line out state:" + state);
        if ("rainman".equals(Build.DEVICE) || "batman".equals(Build.DEVICE)) {
            if (state) {
                echoEntry("/sys/class/saradc/wooferctrl", "2");
                echoEntry("/sys/class/surround/woofer_ctrl", "1");
                SystemProperties.set("sys.subwoofer.detect", "0");
                return true;
            } else {
                echoEntry("/sys/class/saradc/wooferctrl", "3");
                echoEntry("/sys/class/surround/woofer_ctrl", "0");
                SystemProperties.set("sys.subwoofer.detect", "1");
                return true;
            }
        } else if ("conan".equals(Build.DEVICE)) {
            if (state) {
                switchOutMode(1);
            } else {
                switchOutMode(0);//off line 会自动切到speaker
            }
            return true;
        }
        return false;
    }

    private boolean setSoundMute() {
        boolean ret = false;
        if (mTvControlManager.GetAudioMuteKeyStatus() == 0) {
            ret = true;
        }
        Log.i(TAG, "spidf state  is " + ret);
        return ret;
    }

    private boolean switchOutMode(int stat) {
        if ("rainman".equals(Build.DEVICE) || "batman".equals(Build.DEVICE)) {
            TvControlManager.AudioOutputEnum mode = TvControlManager.AudioOutputEnum.values()[stat];
            Log.i(TAG, "rainman or batman set output mode is " + mode);
            mTvControlManager.setAudioOutputMode(mode);
            return true;
        } else if ("conan".equals(Build.DEVICE)) {
            Log.i(TAG, "conan set output stat is " + stat);
            if (stat == 0) {
                //0 for SPEAKER
                echoEntry("/sys/class/ntp8825/ntp8825/ntp8825_headset", "off");
            } else if (stat == 1) {
                //1 for HEADSET
                echoEntry("/sys/class/ntp8825/ntp8825/ntp8825_headset", "on");
            } else {
                echoEntry("/sys/class/ntp8825/ntp8825/ntp8825_headset", "off");
            }
            return true;
        }
        return false;
    }

    /*===========================================local functions=====================*/
    /*===========================================tool functions=====================*/
    /*===========================================tool functions=====================*/
    static int echoEntry(final String entry, String rts) {
        OutputStreamWriter osw = null;
        Log.i(TAG, "echoEntry " + entry + " " + rts);
        try {
            osw = new OutputStreamWriter(new FileOutputStream(entry));
            osw.write(rts, 0, rts.length());
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
        return 0;
    }


    public boolean audioSetSoundEffectMode(int mode) {
        try {
            return mSoundManager.setSoundEffectMode(mode);
        } catch (Exception e) {
            Log.e(TAG, "SoundManager.setSoundEffectMode", e);
            return false;
        }
    }

    public int audioGetSoundEffectMode() {
        try {
            return mSoundManager.getSoundEffectMode();
        } catch (Exception e) {
            Log.e(TAG, "SoundManager.getSoundEffectMode", e);
            return -10;
        }
    }

}
