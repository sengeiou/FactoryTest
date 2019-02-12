/*
 * Copyright (C) 2013 XiaoMi Open Source Project

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fengmi.usertest;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;

import com.droidlogic.app.tv.TvControlManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import mitv.display.PictureSettingsManager;

//amlogic API

public class PicModeManagerImpl {
    private static final String TAG = "IMPL_PicMode";
    /*===========================================local functions=====================*/
    private static final String KEY_PROPERTY_PQ = "persist.sys.brightness_mode";
    private static final String TYPE_PROJ = "Proj";
    /*-------------------------------PQ calibration start ---------------------------*/
    //attention1: in PQ calibration, we are using source type from amlogic,
    //but not source id from xiaomi SDK.
    //attention2: in amlogic definition, source ID 0x0 is ATV.
    //private final static TvControlManager.SourceInput_Type DEFAULT_SOURCE_ATV =
    //					TvControlManager.SourceInput_Type.SOURCE_TYPE_TV;
    private final static int DEFAULT_SOURCE_ATV = 0x0;
    private final static int MAX_COLOR_TEMP = 3;
    private final static int MAX_SOURCE_TYPE = 7;
    private final static String PQBYPASS_NODE = "/sys/class/amvecm/pc_mode";
    private PictureSettingsManager mPicSetManager;
    //amlogic API
    private TvControlManager mTvControlManager;

    public PicModeManagerImpl(Context context) {
        mPicSetManager = PictureSettingsManager.getInstance();
        mTvControlManager = TvControlManager.getInstance();
    }

    public int picGetBrightness() {
        int ret = -1;
        //ret = getBrightness();
        return ret;
    }

    public boolean picSetBrightness(int val) {
        boolean ret = false;
        //ret = setBrightness(val);
        return ret;
    }

    public int picGetContrast() {
        int ret = -1;
        ret = getContrast();
        return ret;
    }

    public boolean picSetContrast(int val) {
        boolean ret = false;
        ret = setContrast(val);
        return ret;
    }

    public int picGetSharpness() {
        int ret = -1;
        ret = getSharpness();
        return ret;
    }

    public boolean picSetSharpness(int val) {
        boolean ret = false;
        ret = setSharpness(val);
        return ret;
    }

    public int picGetHue() {
        int ret = 0;
        ret = getHue();
        return ret;
    }

    public boolean picSetHue(int val) {
        boolean ret = false;
        ret = setHue(val);
        return ret;
    }

    public int picGetSatuation() {
        int ret = -1;
        ret = getSaturation();
        return ret;
    }

    public boolean picSetSatuation(int val) {
        boolean ret = false;
        ret = setSaturation(val);
        return ret;
    }
    //PQ API

    public int picGetColorTemp() {
        int ret = 0;
        ret = getColorTemp();
        return ret;
    }

    public boolean picSetColorTemp(int val) {
        boolean ret = false;
        ret = setColorTemp(val);
        return ret;
    }

    public boolean picModeSet(int stat) {
        boolean ret = false;
        ret = setSceneMode(stat);
        return ret;
    }

    public int picModeGet() {
        int ret = -1;
        ret = getSceneMode();
        return ret;
    }

    public boolean picModeReset() {
        boolean ret = false;
        ret = resetSceneMode();
        return ret;
    }

    public boolean picPanelSelect(String sel) {
        boolean ret = false;
        //ret = panelSet(sel);
        return ret;
    }

    public int picGetPostRedGain(int colortemp) {
        int ret = -1;
        ret = getRedGain(colortemp);
        return ret;
    }

    public boolean picSetPostRedGain(String gain) {
        boolean ret = false;
        ret = setRedGain(gain);
        return ret;
    }

    public int picGetPostGreenGain(int colortemp) {
        int ret = -1;
        ret = getGreenGain(colortemp);
        return ret;
    }

    public boolean picSetPostGreenGain(String gain) {
        boolean ret = false;
        ret = setGreenGain(gain);
        return ret;
    }

    public int picGetPostBlueGain(int colortemp) {
        int ret = -1;
        ret = getBlueGain(colortemp);
        return ret;
    }

    public boolean picSetPostBlueGain(String gain) {
        boolean ret = false;
        ret = setBlueGain(gain);
        return ret;
    }

    public int picGetPostRedOffset(int colortemp) {
        int ret = -1;
        ret = getRedOffs(colortemp);
        return ret;
    }

    public boolean picSetPostRedOffset(String offset) {
        boolean ret = false;
        ret = setRedOffs(offset);
        return ret;
    }

    public int picGetPostGreenOffset(int colortemp) {
        int ret = -1;
        ret = getGreenOffs(colortemp);
        return ret;
    }

    public boolean picSetPostGreenOffset(String offset) {
        boolean ret = false;
        ret = setGreenOffs(offset);
        return ret;
    }

    public int picGetPostBlueOffset(int colortemp) {
        int ret = -1;
        ret = getBlueOffs(colortemp);
        return ret;
    }

    public boolean picSetPostBlueOffset(String offset) {
        boolean ret = false;
        ret = setBlueOffs(offset);
        return ret;
    }

    //PQ big API (just 3 API: gain, offset and save)
    public boolean picGeneralWBSave() {
        boolean ret = false;
        //ret = whiteBalanceSave();
        return ret;
    }

    public boolean picGeneralWBGain(String gain) {
        boolean ret = false;
        //ret = whiteBalanceRGBGainSet(gain);
        return ret;
    }

    public boolean picGeneralWBOffset(String offset) {
        boolean ret = false;
        //ret = whiteBalanceRGBOffsSet(offset);
        return ret;
    }

    public boolean picTransPQDataToDB() {
        boolean ret = false;
        ret = pqSaveDatabase();
        return ret;
    }

    public boolean picSwitchPicUserMode() {
        boolean ret = false;
        ret = switchPicUserMode();
        return ret;

    }

    public boolean byPassPQ(String bypass) {
        boolean ret = false;
        ret = setPQbypass(bypass);
        return ret;
    }

    public boolean picEnablePQ() {
        boolean ret = false;
        try {
            ret = setPQStatus(true);
        } catch (Exception e) {
        }
        return ret;
    }

    public boolean picDisablePQ() {
        boolean ret = false;
        try {
            ret = setPQStatus(false);
        } catch (Exception e) {
        }
        return ret;
    }

    private boolean setPQStatus(boolean enable) {
        if (enable) {
            SystemProperties.set(KEY_PROPERTY_PQ, "255");
        } else {
            SystemProperties.set(KEY_PROPERTY_PQ, "300");
        }
        mPicSetManager.setColorTemp(mPicSetManager.getColorTemp());
        return true;
    }

    private boolean switchPicUserMode() {
        boolean ret = false;
        ret = mPicSetManager.setSceneMode(mPicSetManager.SCENE_MODE_USER);
        Log.i(TAG, "the scene mode setting result is " + ret);
        return ret;
    }

    private int getColorTemp() {
        int ret = -1;
        ret = mPicSetManager.getColorTemp();
        Log.i(TAG, "the color temp for current source is " + ret);
        return ret;
    }

    private boolean setColorTemp(int val) {
        boolean ret = false;
        ret = mPicSetManager.setColorTemp(val);
        Log.i(TAG, "the color temperature set to current source is " + ret);
        return ret;
    }


    private int getContrast() {
        int ret = -1;
        ret = mPicSetManager.getContrast();
        Log.i(TAG, "the contrast for current source is " + ret);
        return ret;
    }

    private boolean setContrast(int val) {
        boolean ret = false;
        ret = mPicSetManager.setContrast(val);
        Log.i(TAG, "the contrast set to current source is " + ret);
        return ret;
    }

    private int getSharpness() {
        int ret = -1;
        ret = mPicSetManager.getSharpness();
        Log.i(TAG, "the sharpness for current source is " + ret);
        return ret;
    }

    private boolean setSharpness(int val) {
        boolean ret = false;
        ret = mPicSetManager.setSharpness(val);
        Log.i(TAG, "the sharpness set to current source is " + ret);
        return ret;
    }

    private int getHue() {
        int ret = -1;
        ret = mPicSetManager.getHue();
        Log.i(TAG, "the hue for current source is " + ret);
        return ret;
    }

    private boolean setHue(int val) {
        boolean ret = false;
        ret = mPicSetManager.setHue(val);
        Log.i(TAG, "the hue set to current source is " + ret);
        return ret;
    }

    private int getSaturation() {
        int ret = -1;
        ret = mPicSetManager.getSaturation();
        Log.i(TAG, "the saturation for current source is " + ret);
        return ret;
    }

    private boolean setSaturation(int val) {
        boolean ret = false;
        ret = mPicSetManager.setSaturation(val);
        Log.i(TAG, "the saturation set to current source is " + ret);
        return ret;
    }

    private int getBrightness() {
        int ret = -1;
        ret = mPicSetManager.getIntensity();
        Log.i(TAG, "the brightness for current source is " + ret);
        return ret;
    }

    private boolean setBrightness(int val) {
        boolean ret = false;
        ret = mPicSetManager.setIntensity(val);
        Log.i(TAG, "the brightness set to current source is " + ret);
        return ret;
    }

    private boolean setSceneMode(int stat) {
        boolean ret = false;
        ret = mPicSetManager.setSceneMode(stat);
        Log.i(TAG, "the scene mode set to current source is " + ret);
        return ret;
    }

    private int getSceneMode() {
        int ret = -1;
        ret = mPicSetManager.getSceneMode();
        Log.i(TAG, "the scene mode for current source is " + ret);
        return ret;
    }

    private boolean resetSceneMode() {
        boolean ret = false;
        ret = mPicSetManager.setSceneMode(mPicSetManager.SCENE_MODE_USER);
        Log.i(TAG, "the scene mode reset operation is " + ret);
        return ret;
    }

    /*================ PQ interface implement start==============*/
    private boolean setRedGain(String gain) {
        boolean ret = false;
        Log.i(TAG, "set post red gain: " + gain);
        int r = mTvControlManager.FactoryWhiteBalanceSetRedGain(
                DEFAULT_SOURCE_ATV, getColorTempFrmParam(gain), getGainFrmParam(gain));
        if (r == 0) {
            ret = true;
        }
        return ret;
    }

    public boolean setRedGain(int colortemp, int gain) {
        boolean ret = false;
        Log.i(TAG, "set post red gain: " + gain);
        int r = mTvControlManager.FactoryWhiteBalanceSetRedGain(
                DEFAULT_SOURCE_ATV, convertColorTemp(convertColorTemp(colortemp)), gain);
        if (r == 0) {
            ret = true;
        }
        return ret;
    }

    private boolean setGreenGain(String gain) {
        boolean ret = false;
        Log.i(TAG, "set post green gain: " + gain);
        int r = mTvControlManager.FactoryWhiteBalanceSetGreenGain(
                DEFAULT_SOURCE_ATV, getColorTempFrmParam(gain), getGainFrmParam(gain));
        if (r == 0) {
            ret = true;
        }
        return ret;
    }

    public boolean setGreenGain(int colortemp, int gain) {
        boolean ret = false;
        Log.i(TAG, "set post green gain: " + gain);
        int r = mTvControlManager.FactoryWhiteBalanceSetGreenGain(
                DEFAULT_SOURCE_ATV, convertColorTemp(convertColorTemp(colortemp)), gain);
        if (r == 0) {
            ret = true;
        }
        return ret;
    }

    private boolean setBlueGain(String gain) {
        boolean ret = false;
        Log.i(TAG, "set post blue gain: " + gain);
        int r = mTvControlManager.FactoryWhiteBalanceSetBlueGain(
                DEFAULT_SOURCE_ATV, getColorTempFrmParam(gain), getGainFrmParam(gain));
        if (r == 0) {
            ret = true;
        }
        return ret;
    }

    public boolean setBlueGain(int colortemp, int gain) {
        boolean ret = false;
        Log.i(TAG, "set post blue gain: " + gain);
        int r = mTvControlManager.FactoryWhiteBalanceSetBlueGain(
                DEFAULT_SOURCE_ATV, convertColorTemp(convertColorTemp(colortemp)), gain);
        if (r == 0) {
            ret = true;
        }
        return ret;
    }

    private boolean setRedOffs(String offs) {
        boolean ret = false;
        Log.i(TAG, "set post red offs: " + offs);
        int r = mTvControlManager.FactoryWhiteBalanceSetRedOffset(
                DEFAULT_SOURCE_ATV, getColorTempFrmParam(offs), getOffsetFrmParam(offs));
        if (r == 0) {
            ret = true;
        }
        return ret;
    }

    public boolean setRedOffs(int colortemp, int offs) {
        boolean ret = false;
        Log.i(TAG, "set post red offs: " + offs);
        int r = mTvControlManager.FactoryWhiteBalanceSetRedOffset(
                DEFAULT_SOURCE_ATV, convertColorTemp(convertColorTemp(colortemp)), offs);
        if (r == 0) {
            ret = true;
        }
        return ret;
    }

    private boolean setGreenOffs(String offs) {
        boolean ret = false;
        Log.i(TAG, "set post green offs: " + offs);
        int r = mTvControlManager.FactoryWhiteBalanceSetGreenOffset(
                DEFAULT_SOURCE_ATV, getColorTempFrmParam(offs), getOffsetFrmParam(offs));
        if (r == 0) {
            ret = true;
        }
        return ret;
    }
    public boolean setGreenOffs(int colortemp, int offs) {
        boolean ret = false;
        Log.i(TAG, "set post green offs: " + offs);
        int r = mTvControlManager.FactoryWhiteBalanceSetGreenOffset(
                DEFAULT_SOURCE_ATV, convertColorTemp(convertColorTemp(colortemp)), offs);
        if (r == 0) {
            ret = true;
        }
        return ret;
    }

    private boolean setBlueOffs(String offs) {
        boolean ret = false;
        Log.i(TAG, "set post blue offs: " + offs);
        int r = mTvControlManager.FactoryWhiteBalanceSetBlueOffset(
                DEFAULT_SOURCE_ATV, getColorTempFrmParam(offs), getOffsetFrmParam(offs));
        if (r == 0) {
            ret = true;
        }
        return ret;
    }
    public boolean setBlueOffs(int colortemp, int offs) {
        boolean ret = false;
        Log.i(TAG, "set post blue offs: " + offs);
        int r = mTvControlManager.FactoryWhiteBalanceSetBlueOffset(
                DEFAULT_SOURCE_ATV, convertColorTemp(convertColorTemp(colortemp)), offs);
        if (r == 0) {
            ret = true;
        }
        return ret;
    }

    //get
    private int getRedGain(int colortemp) {
        int ret = -1;
        ret = mTvControlManager.FactoryWhiteBalanceGetRedGain(
                DEFAULT_SOURCE_ATV, convertColorTemp(convertColorTemp(colortemp)));
        Log.i(TAG, "get post red colortemp gain: " + ret);
        return ret;
    }

    private int getGreenGain(int colortemp) {
        int ret = -1;
        ret = mTvControlManager.FactoryWhiteBalanceGetGreenGain(
                DEFAULT_SOURCE_ATV, convertColorTemp(convertColorTemp(colortemp)));
        Log.i(TAG, "get post green colortemp gain: " + ret);
        return ret;
    }

    private int getBlueGain(int colortemp) {
        int ret = -1;
        ret = mTvControlManager.FactoryWhiteBalanceGetBlueGain(
                DEFAULT_SOURCE_ATV, convertColorTemp(convertColorTemp(colortemp)));
        Log.i(TAG, "get post blue colortemp gain: " + ret);
        return ret;
    }

    private int getRedOffs(int colortemp) {
        int ret = -1;
        ret = mTvControlManager.FactoryWhiteBalanceGetRedOffset(
                DEFAULT_SOURCE_ATV, convertColorTemp(convertColorTemp(colortemp))) + 1024;
        Log.i(TAG, "get post red colortemp offset: " + ret);
        return ret;
    }

    private int getGreenOffs(int colortemp) {
        int ret = -1;
        ret = mTvControlManager.FactoryWhiteBalanceGetGreenOffset(
                DEFAULT_SOURCE_ATV, convertColorTemp(convertColorTemp(colortemp))) + 1024;
        Log.i(TAG, "get post green colortemp offset: " + ret);
        return ret;
    }

    private int getBlueOffs(int colortemp) {
        int ret = -1;
        ret = mTvControlManager.FactoryWhiteBalanceGetBlueOffset(
                DEFAULT_SOURCE_ATV, convertColorTemp(convertColorTemp(colortemp))) + 1024;
        Log.i(TAG, "get post blue colortemp offset: " + ret);
        return ret;
    }

    private boolean pqSaveDatabase() {
        boolean ret = false;
        //the method: read out the ATV and save to the others
        Log.i(TAG, "fetch PQ from atv and save to all the others");
        int rg, gg, bg, ro, go, bo;
        for (int i = 0; i < 3; i++) {
            // i replace the colo temperature
            rg = mTvControlManager.FactoryWhiteBalanceGetRedGain(
                    DEFAULT_SOURCE_ATV, i);
            gg = mTvControlManager.FactoryWhiteBalanceGetGreenGain(
                    DEFAULT_SOURCE_ATV, i);
            bg = mTvControlManager.FactoryWhiteBalanceGetBlueGain(
                    DEFAULT_SOURCE_ATV, i);
            ro = mTvControlManager.FactoryWhiteBalanceGetRedOffset(
                    DEFAULT_SOURCE_ATV, i);
            go = mTvControlManager.FactoryWhiteBalanceGetGreenOffset(
                    DEFAULT_SOURCE_ATV, i);
            bo = mTvControlManager.FactoryWhiteBalanceGetBlueOffset(
                    DEFAULT_SOURCE_ATV, i);
            for (int j = 0; j < 7; j++) {
                Log.i(TAG, "/----------------------------------/");
                mTvControlManager.FactoryWhiteBalanceSaveParameters(
                        j, i, rg, gg, bg, ro, go, bo);
                ret = true;
            }
        }
        return ret;
    }

    /*=============== PQ interface implement stop=================*/
    /*=============== PQ interface tools functions start=================*/
    //parse parameters
    //the gain string definition: [0]: color temp;[1]:wb high;[2] wb low
    private int getColorTempFrmParam(String gain) {
        int ret = -1;
        String gainRaw[] = gain.split(",");
        Log.i(TAG, "WhiteB Parameters for color temp: [" + gain + "]" + "gain.length[" + gainRaw.length + "]");
        if (gainRaw.length != 3) {
            Log.e(TAG, "gain value length error");
            return ret;
        }
        ret = Integer.parseInt(gainRaw[0]);
        ret = convertColorTemp(convertColorTemp(ret));
        return ret;
    }

    private int getGainFrmParam(String gain) {
        int ret = -1;
        String gainRaw[] = gain.split(",");
        Log.i(TAG, "WhiteB Set Value: [" + gain + "]" + "gain.length[" + gainRaw.length + "]");
        if (gainRaw.length != 3) {
            Log.e(TAG, "gain value length error");
            return ret;
        }
        int g = Integer.parseInt(gainRaw[1]) * 256 + Integer.parseInt(gainRaw[2]);
        return g;
    }

    private int getOffsetFrmParam(String offset) {
        int ret = -1;
        String offsetRaw[] = offset.split(",");
        Log.i(TAG, "WhiteB Set Value: [" + offset + "]" + "offsetRaw.length[" + offsetRaw.length + "]");
        if (offsetRaw.length != 3) {
            Log.e(TAG, "offset value length error");
            return ret;
        }
        int g = Integer.parseInt(offsetRaw[1]) * 256 + Integer.parseInt(offsetRaw[2]) - 1024;
        return g;
    }

    //transfer function for PQ
    //source
    private TvControlManager.SourceInput_Type convertSourceType(int type) {
        TvControlManager.SourceInput_Type sour = TvControlManager.SourceInput_Type.SOURCE_TYPE_TV;
        if (type == 0x0) {
            sour = TvControlManager.SourceInput_Type.SOURCE_TYPE_TV;
        } else if (type == 0x1) {
            sour = TvControlManager.SourceInput_Type.SOURCE_TYPE_AV;
        } else if (type == 0x3) {
            sour = TvControlManager.SourceInput_Type.SOURCE_TYPE_HDMI;
        } else if (type == 0x4) {
            sour = TvControlManager.SourceInput_Type.SOURCE_TYPE_VGA;
        } else if (type == 0x6) {
            sour = TvControlManager.SourceInput_Type.SOURCE_TYPE_DTV;
        } else if (type == 0x8) {
            sour = TvControlManager.SourceInput_Type.SOURCE_TYPE_HDMI_4K2K;
        } else {
            Log.i(TAG, "error input " + type);
        }
        return sour;
    }

    private int convertSourceType(TvControlManager.SourceInput_Type type) {
        int sour = type.toInt();
        return sour;
    }

    //color temp
    private TvControlManager.color_temperature convertColorTemp(int mode) {
        TvControlManager.color_temperature color = TvControlManager.color_temperature.COLOR_TEMP_COLD;
        if (mode == 0x0) {
            color = TvControlManager.color_temperature.COLOR_TEMP_COLD;
        } else if (mode == 0x1) {
            color = TvControlManager.color_temperature.COLOR_TEMP_STANDARD;
        } else if (mode == 0x2) {
            color = TvControlManager.color_temperature.COLOR_TEMP_WARM;
        }
        return color;
    }

    private int convertColorTemp(TvControlManager.color_temperature mode) {
        int color = mode.toInt();
        return color;
    }

    private boolean setPQbypass(String pqbypass) {
        boolean ret = false;
        Log.i(TAG, "setPQbypass [" + pqbypass + "]");
        ret = echoEntry(PQBYPASS_NODE, pqbypass);
        return ret;
    }

    /*=============== PQ interface tools functions start=================*/
    /*-------------------------------PQ calibration stop ---------------------------*/
    /*===========================================local functions=====================*/
    /*===========================================tool functions=====================*/
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
    /*===========================================tool functions=====================*/
}
