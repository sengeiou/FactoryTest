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
 * -------------- WIFI -----------------
 * 1. get backlight value。读取背光的值。（picGetBacklight)
 * 2. set backlight value。设置背光的值。（picSetBacklight)
 * 3. get Brightness value。读取亮度的值。（picGetBrightness)
 * 4. set Brightness value。设置亮度的值。（picSetBrightness)
 * 5. get Contrast value。读取对比度的值。（picGetContrast)
 * 6. set Contrast value。设置对比度的值。（picSetContrast)
 * 7. get Sharpness value。读取清晰度的值。（picGetSharpness)
 * 8. set Sharpness value。设置清晰度的值。（picSetSharpness)
 * 9. get Hue value。读取色调的值。（picGetHue)
 * 10. set Hue value。设置色调的值。（picSetHue)
 * 11. get Satuation value。读取饱和度的值。（picGetSatuation)
 * 12. set Satuation value。设置饱和度的值。（picSetSatuation)
 * 13. get ColorTemp value。读取色温的值。（picGetColorTemp)
 * 14. set ColorTemp value。设置色温的值。（picSetColorTemp)
 * 18. read the whitebalance red gain. 读取白平衡的红色增益。(picGetPostRedGain)
 * 19. set the whitebalance red gain. 设置白平衡的红色增益。(picSetPostRedGain)
 * 20. read the whitebalance green gain. 读取白平衡的绿色增益。(picGetPostGreenGain)
 * 21. set the whitebalance green gain. 设置白平衡的绿色增益。(picSetPostGreenGain)
 * 22. read the whitebalance blue gain. 读取白平衡的蓝色增益。(picGetPostBlueGain)
 * 23. set the whitebalance blue gain. 设置白平衡的蓝色增益。(picSetPostBlueGain)
 * 24. read whitebalance red offset. 读取白平衡的红色偏移量。(picGetPostRedOffset)
 * 25. set whitebalance red offset. 设置白平衡的红色偏移量。(picSetPostRedOffset)
 * 26. read whitebalance green offset. 读取白平衡的绿色偏移量。(picGetPostGreenOffset)
 * 27. set whitebalance green offset. 设置白平衡的绿色偏移量。(picSetPostGreenOffset)
 * 28. read whitebalance blue offset. 读取白平衡的蓝色偏移量。(picGetPostBlueOffset)
 * 29. set whitebalance blue offset. 设置白平衡的蓝色偏移量。(picSetPostBlueOffset)
 * 30. set picture mode (scene). 设置图像模式（情景模式）。
 * 31. get picture mode (scene). 获取图像模式（情景模式）。
 * 32. reset picture mode (scene). 复位图像模式到默认状态（情景模式）。
 */

package com.fm.middlewareimpl.interf;

import android.content.Context;



public abstract class PicModeManagerAbs extends BaseMiddleware {
    public PicModeManagerAbs(Context context) {
        super(context);
    }

    /**
     * -1-
     * get Backlight value.
     *
     * @return value in int type.
     */
    public abstract int picGetBacklight();

    /**
     * -2-
     * set Backlight value.
     *
     * @return pass or fail.
     */
    public abstract boolean picSetBacklight(int val);

    /**
     * -3-
     * get Brightness value.
     *
     * @return value in int type.
     */
    public abstract int picGetBrightness();

    /**
     * -4-
     * set Brightness value.
     *
     * @return pass or fail.
     */
    public abstract boolean picSetBrightness(int val);

    /**
     * -5-
     * get Contrast value.
     *
     * @return value in int type.
     */
    public abstract int picGetContrast();

    /**
     * -6-
     * set Contrast value.
     *
     * @return pass or fail.
     */
    public abstract boolean picSetContrast(int val);

    /**
     * -7-
     * get Sharpness value.
     *
     * @return value in int type.
     */
    public abstract int picGetSharpness();

    /**
     * -8-
     * set Sharpness value.
     *
     * @return pass or fail.
     */
    public abstract boolean picSetSharpness(int val);

    /**
     * -9-
     * get Hue value.
     *
     * @return value in int type.
     */
    public abstract int picGetHue();

    /**
     * -10-
     * set Hue value.
     *
     * @return pass or fail.
     */
    public abstract boolean picSetHue(int val);

    /**
     * -11-
     * get Satuation value.
     *
     * @return value in int type.
     */
    public abstract int picGetSatuation();

    /**
     * -12-
     * set Satuation value.
     *
     * @return pass or fail.
     */
    public abstract boolean picSetSatuation(int val);

    /**
     * -13-
     * get ColorTemp value.
     *
     * @return value in int type.
     */
    public abstract int picGetColorTemp();

    /**
     * -14-
     * set ColorTemp value.
     *
     * @return pass or fail.
     */
    public abstract boolean picSetColorTemp(int val);

    /**
     * -15-
     * get whitebalance red gain
     *
     * @return the gain value of given color temperature.
     * @para: color temperature
     */
    public abstract int picGetPostRedGain(int colortemp);

    /**
     * -16-
     * set whitebalance red gain
     *
     * @return pass/fail
     * @para: gain
     */
    public abstract boolean picSetPostRedGain(String gain);

    /**
     * -17-
     * get whitebalance green gain
     *
     * @return the gain value of given color temperature.
     * @para: color temperature
     */
    public abstract int picGetPostGreenGain(int colortemp);

    /**
     * -18-
     * set whitebalance green gain
     *
     * @return pass/fail
     * @para: gain
     */
    public abstract boolean picSetPostGreenGain(String gain);

    /**
     * -19-
     * get whitebalance blue gain
     *
     * @return the gain value of given color temperature.
     * @para: color temperature
     */
    public abstract int picGetPostBlueGain(int colortemp);

    /**
     * -20-
     * set whitebalance blue gain
     *
     * @return pass/fail
     * @para: gain
     */
    public abstract boolean picSetPostBlueGain(String gain);

    /**
     * -21-
     * get whitebalance red offset
     *
     * @return the offset value of given color temperature.
     * @para: color temperature
     */
    public abstract int picGetPostRedOffset(int colortemp);

    /**
     * -22-
     * set whitebalance red offset
     *
     * @return pass/fail
     * @para: offset
     */
    public abstract boolean picSetPostRedOffset(String offset);

    /**
     * -23-
     * get whitebalance green offset
     *
     * @return the offset value of given color temperature.
     * @para: color temperature
     */
    public abstract int picGetPostGreenOffset(int colortemp);

    /**
     * -24-
     * set whitebalance green offset
     *
     * @return pass/fail
     * @para: offset
     */
    public abstract boolean picSetPostGreenOffset(String offset);

    /**
     * -25-
     * get whitebalance blue offset
     *
     * @return the offset value of given color temperature.
     * @para: color temperature
     */
    public abstract int picGetPostBlueOffset(int colortemp);

    /**
     * -26-
     * set whitebalance blue offset
     *
     * @return pass/fail
     * @para: offset
     */
    public abstract boolean picSetPostBlueOffset(String offset);

    /**
     * -27-
     * save whitebalance data to database.
     *
     * @return pass/fail
     * @para: offset
     */
    public abstract boolean picGeneralWBSave();

    /**
     * -28-
     * set whitebalance gain, it's a general API
     *
     * @return pass/fail
     * @para: gain
     */
    public abstract boolean picGeneralWBGain(String gain);

    /**
     * -29-
     * set whitebalance offset
     *
     * @return pass/fail
     * @para: offset
     */
    public abstract boolean picGeneralWBOffset(String offset);

    /**
     * -30-
     * set picture mode (scene)
     *
     * @return pass/fail
     * @para: DEFAULT(0);USER(1);MONITOR(2);MOVIE(3);GAME(4);PICTURE(5);SPORTS(6);
     */
    public abstract boolean picModeSet(int stat);

    /**
     * -31-
     * get picture mode
     *
     * @return picture mode in int type
     */
    public abstract int picModeGet();

    /**
     * -32-
     * reset picture mode to default setting
     *
     * @return pass/fail
     */
    public abstract boolean picModeReset();

    /**
     * -33-
     * PQ: transfer pq calibration data from curr to persist
     *
     * @return pass/fail
     */
    public abstract boolean picTransPQDataToDB();

    /**
     * -34-
     * Pic Set: slect the panel type
     *
     * @return pass/fail
     */
    public abstract boolean picPanelSelect(String sel);

    /**
     * -36-
     * switch pic mode to user mode
     *
     * @return pass/fail
     */
    public abstract boolean picSwitchPicUserMode();

    /**
     * -37-
     * set/unset pq bypass (0:bypass/1:not bypass)
     *
     * @return pass/fail
     */
    public abstract boolean byPassPQ(String bypass);

    public abstract boolean picEnablePQ();

    public abstract boolean picDisablePQ();
}
