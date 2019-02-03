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
 * 这里主要有video, ktv, source, aging方面的操作
 * 本接口定义了如下信息的存取：
 * -------------- Source -----------------
 * 1. CEC test。测试各个HDMI接口的CEC。（hdmiTestCec)
 * 1.1 CEC test name。测试各个HDMI接口的CEC(验证name)。（hdmiTestCecName)
 * 2. the 3D sync signal on BT。蓝牙中的3D同步信号检查。（hdmiCheck3DSync)
 * 3. check EDID。检查电视的EDID。（hdmiCheckEdid)（保留）
 * 4. set Hdmi 3D mode。设置Hdmi的3D模式。（hdmiSet3D）
 * 5 get Hdmi 3D mode。读取Hdmi的3D模式。（hdmiGet3D）
 * 6. switch source。切换电视的视频输入源。（switchInputSource)
 * 7. load ATV channel table。载入ATV信号的预设频道表。（atvLoadChannel）
 * 8. load DTV channel table。载入DTV信号的预设频道表。(dtvLoadChannel)
 * -------------- video -----------------
 * 9. select local video position。选择本地视频源位置。（selectLocalVideoPos)
 * -------------- aging -----------------
 * 10. init aging timer。初始化烤机计时器。（agingInitTimerCount）
 * 11. start/stop Aging Timer。启动/停止烤机的计时器。（agingSetTimerStatus)
 * 12. set aging timer step。设置计数器的步进（即几秒记一次）。（agingSetTimerStep）
 * 13. set aging timer MAX in time type。设置烤机多长时间。（agingHowLong）
 * 13.1. fetch the timer count。读取烤机计时器记录。（agingGetTImerCount）
 * 14. clear aging timer。清除记录结果。（agingClearTimer）
 * <p>
 * -------------- ktv (预留）-----------------
 * 15. set KTV state。启动/停止NFC。（setKTVStatus)
 * 16. set FullHD output。设置为full HD 的输出模式（1080p）。（picSetFullHd)
 * 17. set test pattern. 设置不同的画面，纯色画面用于测试。(setTestPattern)
 * 18. cancel test pattern.取消设置的用于测试的纯色画面。(cancelTestPattern)
 * 37. reset Hdmi Hpd, actually, the 4030 chip is reset.
 */

package com.fm.middlewareimpl.interf;

import android.content.Context;
import android.view.SurfaceHolder;
import android.media.MediaPlayer;
import android.view.View;



public abstract class MediaTestManagerAbs extends BaseMiddleware {
    public MediaTestManagerAbs(Context context) {
        super(context);
    }

    /**
     * -1-
     * test all hdmi interface's cec function.
     *
     * @return success or no.
     */
    public abstract int hdmiTestCec(int port);

    /**
     * -1.1-
     * test all hdmi interface's cec name function.
     *
     * @return cec name.
     */
    public abstract String hdmiTestCecName(int port);

    /**
     * -2-
     * the 3D sync signal on BT.
     * if just one port has 3D, the default is 0.
     * now just HuaXing Panel.
     *
     * @return success or no.
     */
    public abstract boolean hdmiCheck3DSync();

    /**
     * -3-
     * check Edid.
     * all HDMI port use the same Edid
     *
     * @return Edid in String type.
     */
    public abstract String hdmiCheckEdid();

    /**
     * -4-
     * set hdmi 3D mode.
     *
     * @return success or no.
     */
    public abstract boolean hdmiSet3D(int mode);

    /**
     * -5-
     * get hdmi 3D mode.
     *
     * @return success or no.
     * OFF(0);AUTO(1);SIDE_BY_SIDE(2);TOP_AND_BOTTOM(3);FRAMEPACKING(4)
     * abnormal return value: -1
     */
    public abstract int hdmiGet3D();

    /**
     * -6-
     * switch current input source.
     *
     * @return success or no.
     */
    public abstract boolean switchInputSource(String sour);

    /**
     * -7-
     * load ATV channel table.
     * params:
     * now have 3 city (factory): WZS, SZ, BJ
     * the params should be uppercase.
     * BJ: for beijing design cite
     *
     * @return success or no.
     */
    public abstract boolean atvLoadChannel(String city);

    /**
     * -8-
     * load DTV channel table.
     * now have 3 city (factory): WZS, SZ, BJ
     * the params should be uppercase.
     * BJ: for beijing design cite
     *
     * @return success or no.
     */
    public abstract boolean dtvLoadChannel(String city);

    /**
     * -9-
     * select local video source.
     *
     * @return success or no.
     */
    public abstract boolean selectLocalVideoPos(int id);

    /**
     * -10-
     * init burning(aging) timer count.
     *
     * @return success or no.
     */
    public abstract boolean agingInitTimerCount();

    /**
     * -11-
     * start/stop aging timer.
     *
     * @return success or no.
     */
    public abstract boolean agingSetTimerStatus(boolean stat);

    /**
     * -12-
     * set the timer count step.
     * the unit is "second".
     *
     * @return success or no.
     */
    public abstract boolean agingSetTimerStep(int val);

    /**
     * -13-
     * set how long the timer works.
     *
     * @return pass/fail.
     */
    public abstract boolean agingHowLong(int val);

    /**
     * -13.1-
     * fetch the timer count.
     *
     * @return the count value of timer.
     */
    public abstract byte[] agingGetTimerCount();

    /**
     * -14-
     * clear the timer count.
     *
     * @return success or no.
     */
    public abstract boolean agingClearTimer();

    /**
     * -16-
     * set FullHD output mode.
     *
     * @return pass or fail.
     */
    public abstract boolean picSetFullHD();

    /**
     * -17-
     * set different pattern on panel output.
     *
     * @return pass or fail.
     */
    public abstract boolean setTestPattern(int r, int g, int b);

    /**
     * -18-
     * cancel pattern shows on panel.
     *
     * @return pass or fail.
     */
    public abstract boolean cancelTestPattern();

    /**
     * -19-
     * set aspect strecth ratio
     * set AspectRatio(0:unknow,1:keep(原始),2:scretch(全屏),3:auto scale(智能缩放),
     * 4:auto scretch(智能拉伸),5:enlarge(等比例放大),6:overscan large)
     *
     * @return pass or fail.
     */
    public abstract boolean setAspectMode(int val);

    /**
     * -20-
     * init aging surface
     *
     * @return pass or fail.
     */
    public abstract boolean surfaceInit(SurfaceHolder holder, MediaPlayer.OnErrorListener errlistener,
                               MediaPlayer.OnPreparedListener prepListener);

    /**
     * -21-
     * do surface change setting
     *
     * @return pass or fail.
     */
    public abstract boolean surfaceChange(int[] pos, int width, int height);

    /**
     * -22-
     * release a tvplayer
     */
    public abstract void surfacePlayerRelease();

    /**
     * -22.1-
     * start tv player.
     *
     * @return
     */
    public abstract void surfacePlayerStart();

    /**
     * -23-
     * do prepare work before aging (burning)
     *
     * @return pass or fail.
     */
    public abstract boolean burningPrepare();

    /**
     * -24-
     * stop aging
     *
     * @return pass or fail.
     */
    public abstract boolean burningStop();

    /**
     * -25-
     * init tv context
     *
     * @return pass or fail.
     */
    public abstract boolean tvcontextInit();

    /**
     * -26-
     * the Control para is source name, here transfer it to source id.
     *
     * @return integer.
     */
    public abstract int transSourNameToId(String SourName);

    /**
     * -27-
     * switch to pointed source by para (source id)
     *
     * @return boolean.
     */
    public abstract boolean switchCurrSour(int sourId);

    /**
     * -28-
     * fetch current source's id.
     *
     * @return integer.
     */
    public abstract int getCurrSour();

    /**
     * -29-
     * fetch the source id list
     *
     * @return integer array.
     */
    public abstract int[] getAllInputSour();

    /**
     * -30-
     * tune to pointed atv channel (channel)
     *
     * @return boolean.
     */
    public abstract boolean switchAtvChannel(int channel);

    /**
     * -31-
     * tune to pointed dtv channel (channel)
     *
     * @return boolean.
     */
    public abstract boolean switchDtvChannel(int channel);

    /**
     * -32-
     * fetch current atv channel's id.
     *
     * @return integer.
     */
    public abstract int getAtvCurrChan();

    /**
     * -33-
     * fetch current dtv channel's id.
     *
     * @return integer.
     */
    public abstract int getDtvCurrChan();

    /**
     * -34-
     * fetch all atv channel number.
     *
     * @return integer.
     */
    public abstract int getAtvChanCount();

    /**
     * -35-
     * fetch all dtv channel number.
     *
     * @return integer.
     */
    public abstract int getDtvChanCount();

    /**
     * -36-
     * fetch source Name by Id.
     *
     * @return String.
     */
    public abstract String getSourName(int sourId);

    /**
     * -37-
     * init the hash table for source name and id.
     *
     * @return .
     */
    public abstract void initSourceIdTable();

    /**
     * -37-
     * reset Hdmi Hpd, actually, the 4030 chip is reset.
     *
     * @return pass/fail for operation.
     */
    public abstract boolean resetHPD();

    /**
     * -38-
     * check TV hdcp key 1.4 is valid or no.
     *
     * @return pass/fail for operation.
     */
    public abstract boolean checkHdcp14Valid();

    /**
     * -38-
     * check TV hdcp key 2.2 is valid or no.
     *
     * @return pass/fail for operation.
     */
    public abstract boolean checkHdcp22Valid();

    /**
     * -39-
     * save the auto run command (with parameter)
     *
     * @return pass/fail for operation.
     */
    public abstract boolean setAutoRunCommand(String CmdAPara);

    /**
     * -40-
     * read the auto run command (with parameter)
     *
     * @return pass/fail for operation.
     */
    public abstract String getAutoRunCommand();

    /**
     * -41-
     * switch Dispplay 1 : 1080P 60hz / 0 : 4K2K 30hz
     *
     * @return boolean.
     */
    public abstract boolean switchDisplay(int para);

    /**
     * -42-
     * switch woofer open (forcibly)
     *
     * @return boolean.
     */
    public abstract boolean wooferEnable();

    /**
     * -42-
     * init tvview
     *
     * @return boolean.
     */
    public abstract boolean initTvview();

    /**
     * -43-
     * register a tvview
     *
     * @return boolean.
     */
    public abstract boolean registerTvview(View mView);

    /**
     * -44-
     * unregister the tvview
     *
     * @return boolean.
     */
    public abstract boolean unregisterTvview(View mView);

    /**
     * -45-
     * check amlogic cpu for dolby dts
     *
     * @return boolean.
     */
    public abstract String checkDolbyDts();

    /**
     * -46-
     * set screen resolution
     *
     * @return boolean
     */
    public abstract boolean setScreenRes(String res);

    /**
     * -47-
     * set AgingLine , when aging count >=aging line ,we chage volume to agingvolume
     *
     * @return boolean
     */
    public abstract boolean agingSetAgingLine(String agingLine);

    /**
     * -48-
     * get AgingLine
     *
     * @return aging line
     */
    public abstract int agingGetAgingLine();

    /**
     * -49-
     * set AgingVolume
     *
     * @return success
     */
    public abstract boolean agingSetAgingVolume(String vol);

    /**
     * -50-
     * get AgingVolume
     *
     * @return volume
     */
    public abstract int agingGetAgingVolume();

    public abstract boolean transHdcp14TxKey();
}
