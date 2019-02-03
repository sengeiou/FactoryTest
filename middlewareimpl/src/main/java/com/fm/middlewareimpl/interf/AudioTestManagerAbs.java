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
/**
 * 本接口定义了如下信息的存取：
 * -------------- audio -----------------
 * 1 audioGetSoundVolume: 得到当前的音量设置
 * 2 audioSetSoundVolume: 设置音量，并立即生效
 * 3 audioGetSoundBalance: 得到当前的声音平衡功能设置
 * 4 audioSetSoundBalance: 设置声音平衡功能
 * 5 audioGetSoundMode: 得到当前的声音模式设置
 * 6 audioSetSoundMode: 设置当前的声音模式设置
 * 7 audioResetSoundMode: 复位声音模式回到默认值
 * 8 audioSwitchSpdif: spdif开关
 * 9 audioSwitchSpeaker: 喇叭开关
 * 10 audioSetSoundMute: 设置喇叭无声
 * 11 close DTS/DOLBY : 关闭DTS/DOLBY
 **/

package com.fm.middlewareimpl.interf;

import android.content.Context;


public abstract class AudioTestManagerAbs extends BaseMiddleware {

    public AudioTestManagerAbs(Context context) {
        super(context);
    }

    /**
     * get current audio volume value.
     *
     * @return value in int type.
     */
    public abstract int audioGetSoundVolume();

    /**
     * set current audio volume value.
     *
     * @return pass or fail.
     */
    public abstract boolean audioSetSoundVolume(int val);

    /**
     * get sound balance value.
     *
     * @return value in int type.
     */
    public abstract int audioGetSoundBalance();

    /**
     * set sound balance value.
     *
     * @return pass or fail.
     * @para: 0:both,1:left,2:right
     */
    public abstract boolean audioSetSoundBalance(int val);

    /**
     * get sound mode value.
     *
     * @return value in int type.
     */
    public abstract int audioGetSoundMode();

    /**
     * set sound mode value.
     *
     * @return pass or fail.
     * @SOUND_EFFECT_MODE_MINI = 0
     * @SOUND_EFFECT_MODE_DEFAULT = 0
     * @SOUND_EFFECT_MODE_MOVIE = 1
     * @SOUND_EFFECT_MODE_NEWS = 2
     * @SOUND_EFFECT_MODE_MAX = 2
     */
    public abstract boolean audioSetSoundMode(int val);

    /**
     * reset sound mode to default mode.
     *
     * @return pass or fail.
     */
    public abstract boolean audioResetSoundMode();

    /**
     * set spdif on/off.
     *
     * @return pass/fail.
     * @para: SPDIF-PCM(0);SPDIF_OUTPUT_NONPCM(1);SPDIF_OUTPUT_OFF(2)
     */
    public abstract boolean audioSwitchSpdif(int stat);

    /**
     * set speaker on/off.
     *
     * @return pass/fail.
     */
    public abstract boolean audioSwitchSpeaker(boolean enable);

    /**
     * set sound mute.
     *
     * @return pass/fail.
     */
    public abstract boolean audioSetSoundMute();

    /**
     * -11-
     * close DTS/DOLBY
     *
     * @return pass/fail for operation.
     */
    public abstract boolean closeDTS_DOLBY();

    /**
     * set arc on/off.
     *
     * @return pass/fail.
     * @para: ARC_ON(0);ARC_OFF(2)
     */
    public abstract boolean audioSwitchArc(int stat);

    /**
     * set output mode.
     *
     * @return pass/fail.
     * @para: speaker(0);spdif(1);arc(2)
     */
    public abstract boolean audioOutputMode(int stat);

    /**
     * set speakers enable/disable.
     *
     * @return pass/fail.
     * @para: 0b 1          1         1           1          1         1
     * left    lefttop  leftcenter  rightcenter  righttop   right
     */
    public abstract boolean speakerswitch(int stat);

    /**
     * close dap in dsp.
     *
     * @return pass/fail.
     */
    public abstract boolean closeDap();

    public abstract boolean audioSwitchLineOut(boolean state);

    public abstract int audioGetMaxSoundVolume();

    public abstract boolean audioSetSoundEffectMode(int mode);

    public abstract int audioGetSoundEffectMode();

}
