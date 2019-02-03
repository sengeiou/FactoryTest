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
 * 本接口定义了如下信息：
 * 1. get light sensor value。读取light sensor的值。（lightSensorGetValue)
 * 2. system reset。系统执行复位，并重启。（systemReset)
 * 3. disable/enable touchpad。停止/开始响应touchpad。（touchpadSetStatus)
 * 4. disable/enable remote control。停止/开始响应遥控器。（remoteControlSetLock）
 * 5. disable/enable system sleep。允许/停止系统休眠。（systemSleepSetStatus）
 * 6. Partition check。分区检查。（checkPartition)
 * 7. Led flash setting. led闪烁起停及方式。
 * 8. switch mode from Factory to user. 从工厂到用户模式的切换
 * 9. reboot system。系统重启
 * 10. shutdown system。系统关机，但是不再启动。
 * 11. boot times。系统启动次数。
 * 12. set the product saled region.设置产品的销售区域
 * 13. get the region product saled.获取产品的销售区域设置
 * ..
 * 21. get product name.获取产品名字（40/55共用代码，可以用于区分40：hancock/55：gladiator，getProductName）
 * 22. disable screen saver and sleep mode. 关闭屏保和待机休眠.工厂下默认是关闭的,该接口适用所有产品.
 * 23. reboot recovery
 * 24. set system mode to user mode
 * 25. check system mode is user mod
 * 26. create a rid used by application team
 * 27. get key test sequence
 * 28. get woofer status
 * 29. set PowerStandby status
 * 30. set I2C port Low/High(SDA and SCK pin)
 * 31. set/get vcom command to i2c
 */

package com.fm.middlewareimpl.interf;

import android.content.Context;



public abstract class UtilManagerAbs extends BaseMiddleware {
    public UtilManagerAbs(Context context) {
        super(context);
    }

    /*
     * -1-
     * get light sensor value.
     *
     * @return success or no.
     */
    public abstract int lightSensorGetValue();

    /**
     * -2-
     * system reset.
     *
     * @return value in integer type.
     */
    public abstract boolean systemReset();

    /**
     * -3-
     * isable/enable touchpad.
     *
     * @return success or no.
     */
    public abstract boolean touchpadSetStatus();

    /**
     * -4-
     * enable/disable DUT have the ability to repond to RC.
     * param:
     * 1: disable
     * 0: enable
     *
     * @return success or no.
     */
    public abstract boolean remoteControlSetLock(String lock);

    /**
     * -5-
     * disable/enable system sleep.
     *
     * @return success or no.
     */
    public abstract boolean systemSleepSetStatus();

    /**
     * -6-
     * Partition check.
     *
     * @return success or no.
     */
    public abstract boolean checkSystemPartition();

    /**
     * -7-
     * led flash start by period.
     *
     * @return success or no.
     */
    public abstract boolean ledStartFlash(int period);

    /**
     * -7.1-
     * led show given stat(light steady, breath, off).
     *
     * @return success or no.
     */
    public abstract boolean setLedLightStat(String style);

    /**
     * -8-
     * led flash stop.
     *
     * @return success or no.
     */
    public abstract boolean ledStopFlash();

    /**
     * -9-
     * switch mode from factory to user
     *
     * @return success or no.
     */
    public abstract boolean systemSwitchMode();

    /**
     * -39-
     * switch mode from factory to user
     *
     * @return success or no.
     */
    public abstract boolean systemSwitchModeNew();

    /**
     * -10-
     * reboot DUT
     *
     * @return success or no.
     */
    public abstract boolean systemReboot();

    /**
     * -11-
     * shutdown system (it would not start up automatically)
     *
     * @return success or no.
     */
    public abstract boolean systemShutdown();

    /**
     * -12-
     * note the system boot up times
     *
     * @return success or no.
     */
    public abstract boolean systemUpdateBootTimes();

    /**
     * -13-
     * return the system boot up times
     *
     * @return bootup times in int type.
     */
    public abstract int systemGetBootTimes();

    // /**
    //  * -14-
    //  * set the region product will be saled at.
    //  *
    //  * @return pass/fail.
    //  */
    // public abstract boolean setProductRegion(String region);
    //
    // /**
    //  * -15-
    //  * get the region product will be saled at.
    //  *
    //  * @return region name in string type
    //  */
    // public abstract String getProductRegion();

    /**
     * -16-
     * force system come into sleep mode.
     *
     * @return pass/fail
     */
    public abstract boolean sleepSystem();

    /**
     * -17-
     * reset flag to force read panel info directly from T-CON next bootup.
     *
     * @return pass/fail
     */
    public abstract boolean resetTvPanelSelect();

    /**
     * -18-
     * predetermine the bt rc mac to save pair time.
     *
     * @return pass/fail
     */
    public abstract boolean setBtRcMac(String mac);

    /**
     * -19-
     * return bt rc mac address.
     *
     * @return string
     */
    public abstract String getBtRcMac();

    /**
     * -20-
     * system bootup directly (need't powerkey or touch pad).
     *
     * @return string
     */
    public abstract void bootupSystemDirect();

    /**
     * -21-
     * get the model of product.
     *
     * @return product model in string type
     */
    public abstract String getProductModel();

    /**
     * -22-
     * in factory mode, the screen saver and sleep mode should be disabled.
     *
     * @return pass/fail
     */
    public abstract boolean closeScreenSave2Sleep();

    /**
     * -23-
     * reboot recovery DUT
     *
     * @return success or no.
     */
    public abstract boolean systemRebootRecovery();

    /**
     * -24-
     * switch mode from factory to user
     *
     * @return success or no.
     */
    public abstract boolean systemModeSet();

    /**
     * -25-
     * check mode is user
     *
     * @return success or no.
     */
    public abstract boolean systemModeGet();

    /**
     * -26-
     * create a radom value and save it as rid used by application team
     *
     * @return success or no.
     */
    public abstract boolean setApplicationRid();

    /**
     * -27-
     * return the key test sequence for current product
     *
     * @return key sequence.
     */
    public abstract int[] getKeyTestSequence();

    /**
     * -28-
     * get SubWoofer status
     *
     * @return subwoofer in or out.
     */
    public abstract boolean getSubWooferStat();

    /**
     * -29-
     * set PowerStandby status
     *
     * @return true/false for operation.
     */
    public abstract boolean setPowerStandbyStat(String stat);

    /**
     * -30-
     * set I2C port Low/High(SDA and SCK pin)
     *
     * @return true/false for operation.
     */
    public abstract boolean setI2CPinStat(String stat);

    /**
     * -31-
     * do master clear directly, no any pre-condition.
     *
     * @return true/false for operation.
     */
    public abstract boolean systemMasterClear();

    /**
     * -32-
     * get cpu temperature for box.
     *
     * @return true/false for operation.
     * note: empty function for compatible box
     */
    public abstract String getCpuTemp();

    /**
     * -33-
     * set fans stat for box.
     *
     * @return true/false for operation.
     * note: empty function for compatible box
     */
    public abstract boolean setFanStat(String speed);

    /**
     * -34-
     * set gpio stat (out stat).
     *
     * @return true/false for operation.
     */
    public abstract boolean setGpioOut(String portAstat);

    /**
     * -35-
     * get the panel type (it's a hw ID).
     *
     * @return the hw id for panel type.
     */
    public abstract String checkPanelIdTag();

    /**
     * -36-
     * get the gpio in status.
     *
     * @return the gpio in status.
     */
    public abstract int getGpioInStat(String port);

    /**
     * -37-
     * set vcom command to i2c.
     *
     * @return true/false for operation.
     */
    public abstract boolean setVcom(String para);

    /**
     * -38-
     * get data from vcom i2c.
     *
     * @return the vcom data.
     */
    public abstract byte getVcom(String para);

    /**
     * get sn from DLP info
     */
    public abstract String getDlpSn();

    /**
     * set Body Detect Status
     */
    public abstract boolean setBodyDetectStatus(String para);

    /**
     * get Body Detect Status
     */
    public abstract int getBodyDetectStatus();

    public abstract boolean startBodyDetectTest();

    public abstract boolean stopBodyDetectTest();

    public abstract int getBodyDetectCount(boolean isLeft);

    public abstract boolean setMotorScale(int delta);

    /**
     * 读取 Rom 全部空间大小
     *
     * @return
     */
    public abstract String readRomTotalSpace();

    /**
     * 读取 Rom 可用空间大小
     *
     * @return
     */
    public abstract String readRomAvailSpace();

    /**
     * set Fan Speed 0 for min;1 for mid;2 for max
     */
    public abstract boolean setFanSpeed(String level);

    /**
     * reset led step motor
     * echo 1 > proj_motor_calibration
     */
    public abstract boolean resetLEDStepMotor();

    public abstract String readLEDTemperature(String param);

    public abstract String readRGBLEDCurrent();
}
