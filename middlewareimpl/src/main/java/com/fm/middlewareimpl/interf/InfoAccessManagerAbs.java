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
 * 1. PCBA SN的读写 （setPcbaSerialNumber, getPcbaSerialNumber）
 * 2. PCBA MN的读写 （setPcbaManufactureNumber, getPcbaManufactureNumber）
 * 1.1. ASSM SN的读写 （setSerialNumber, getSerialNumber）
 * 2.1. ASSM MN的读写 （setManufactureNumber, getManufactureNumber）
 * 3. BT MAC的读写（setBluetoothMac, getBluetoothMac）
 * 4. WIFI MAC的读写（setWifiMac, getWifiMac）
 * 5. Ethernet MAC的读写（setEthernetMac, getEthernetMac）
 * 6. HDCP key 1.4的读写（setHdcp14key, getHdcp14key）
 * 7. HDCP key 2.0的读写（setHdcp20key, getHdcp20key）
 * ********BOX HDCP***********************************
 * 6. HDCP key 的读写（setHdcpkey, getHdcpkey）
 * 7. HDCP key 的设定/验证（transHdcpkey, verHdcpkey）
 * ********OX HDCP***********************************
 * 8. FIRMWARE Ver的读取（getFirmwareVer)
 * 9. Model Name的读取（getModelName)
 * 10. Projector PID 读写 (setPID,getPID)
 * note: 由于java的限制，一次性写入的字符串长度不能大于65534个
 * note: 工厂端对mac, sn, mn等信息的操作必须是从prop或着nvram读写；我们不能直接
 * 从模块中读取它。
 */

package com.fm.middlewareimpl.interf;

import android.content.Context;



public abstract class InfoAccessManagerAbs extends BaseMiddleware {
    public InfoAccessManagerAbs(Context context) {
        super(context);
    }

    /**
     * 1
     * Set Serial Number (SN); write serial number into system at PCBA stage.
     * Note: if just use one API, just use PCBA stage API, and ingore ASSM API
     *
     * @return success or no.
     */
    public abstract boolean setPcbaSerialNumber(String sn);

    /**
     * 2
     * Get Serial Number (SN); Read serial number from system at PCBA stage.
     * Note: if just use one API, just use PCBA stage API, and ingore ASSM API
     *
     * @return the serial number value in String type.
     */
    public abstract String getPcbaSerialNumber();

    /**
     * 3
     * Set Manufacture Number (MN); Write manufacture number into system at PCBA stage.
     * Note: if just use one API, just use PCBA stage API, and ingore ASSM API
     *
     * @return success or no.
     */
    public abstract boolean setPcbaManufactureNumber(String mn);

    /**
     * 4
     * Get Manufacture Number (MN); Read manufacture number into system at PCBA stage.
     * Note: if just use one API, just use PCBA stage API, and ingore ASSM API
     *
     * @return the manufacture number value in String type.
     */
    public abstract String getPcbaManufactureNumber();

    /**
     * 5
     * Set Serial Number (SN); write serial number into system at ASSMBLY stage.
     * Note: if just use one API, just use PCBA stage API, and ingore ASSM API
     *
     * @return success or no.
     */
    public abstract boolean setAssmSerialNumber(String sn);

    /**
     * 6
     * Get Serial Number (SN); Read serial number from system at ASSMBLY stage.
     * Note: if just use one API, just use PCBA stage API, and ingore ASSM API
     *
     * @return the serial number value in String type.
     */
    public abstract String getAssmSerialNumber();

    /**
     * 7
     * Set Manufacture Number (MN); Write manufacture number into system at ASSMBLY stage.
     * Note: if just use one API, just use PCBA stage API, and ingore ASSM API
     *
     * @return success or no.
     */
    public abstract boolean setAssmManufactureNumber(String mn);

    /**
     * 8
     * Get Manufacture Number (MN); Read manufacture number into system at ASSMBLY stage.
     * Note: if just use one API, just use PCBA stage API, and ingore ASSM API
     *
     * @return the manufacture number value in String type.
     */
    public abstract String getAssmManufactureNumber();

    /**
     * 9
     * Set Bluetooth MAC (BT MAC); Write BT MAC into system.
     * Note: if just use one API, just use PCBA stage API, and ingore ASSM API
     *
     * @return success or no.
     */
    public abstract boolean setBluetoothMac(String mac);

    /**
     * 10
     * Get Bluetooth MAC (BT MAC); Read BT MAC into system.
     *
     * @return the Bluetooth MAC in String type.
     */
    public abstract String getBluetoothMac();

    /**
     * 11
     * Set Wifi MAC (WIFI MAC); Write WIFI MAC into system.
     *
     * @return success or no.
     */
    public abstract boolean setWifiMac(String mac);

    /**
     * 12
     * Get Wifi MAC (WIFI MAC); Read WIFI MAC into system.
     *
     * @return the WIFI MAC in String type.
     */
    public abstract String getWifiMac();

    /**
     * 13
     * Set Ethernet MAC (ETH MAC); Write Ethernet MAC into system.
     *
     * @return success or no.
     */
    public abstract boolean setEthernetMac(String mac);

    /**
     * 14
     * Get Ethernet MAC (ETH MAC); Read Ethernet MAC into system.
     *
     * @return the Ethernet MAC in String type.
     */
    public abstract String getEthernetMac();

    /**
     * 15
     * Set HDCP KEY (1.4); Write HDCP Key 1.4  into system.
     * note: here raw data should be sent to, that is to say, what you received from port, what you should be sent to here.
     * now all product follow this rule; I hope it be upgrade.
     *
     * @return success or no.
     */
    public abstract boolean setHdcp14Key(String mac);

    /**
     * 16
     * Get HDCP Key (1.4); Read HDCP Key 1.4 into system.
     *
     * @return the HDCP Key 1.4 in byte array type.
     */
    public abstract byte[] getHdcp14Key();

    /**
     * 17
     * Set HDCP KEY (2.0); Write HDCP Key 2.0  into system.
     * note: here raw data should be sent to, that is to say, what you received from port, what you should be sent to here.
     * now all product follow this rule; I hope it be upgrade.
     *
     * @return success or no.
     */
    public abstract boolean setHdcp20Key(String mac);

    /**
     * 18
     * Get HDCP Key (2.0); Read HDCP Key 2.0 into system.
     *
     * @return the HDCP Key 2.0 in byte array type.
     */
    public abstract byte[] getHdcp20Key();

    /**
     * 19
     * Get HDCP TX Key (1.4); write HDCP Key 1.4 into system.
     *
     * @return success or no.
     */
    public abstract boolean setHdcp14TxKey(String key);

    /**
     * 20
     * Get HDCP TX Key (1.4); Read HDCP Key 1.4 into system.
     *
     * @return the HDCP Key 1.4 in byte array type.
     */
    public abstract byte[] getHdcp14TxKey();

    /**
     * 21
     * Get HDCP TX Key (2.2); write HDCP Key 2.2 into system.
     *
     * @return success or no.
     */
    public abstract boolean setHdcp22TxKey(String key);

    /**
     * 22
     * Get HDCP TX Key (2.2); Read HDCP Key 2.2 into system.
     *
     * @return the HDCP Key 2.2 in byte array type.
     */
    public abstract byte[] getHdcp22TxKey();

    /**
     * 23
     * Get HDCP Key (1.4); Read HDCP first five bytes into system.
     * ksv: key selection vector
     *
     * @return the HDCP Ksv 1.4 in byte array type.
     */
    public abstract byte[] getHdcpKsv();

    /**
     * 24
     * Get Firmware Version; Read Firmware Version.
     *
     * @return the Firmware Version in String type.
     */
    public abstract String getFirmwareVer();

    /**
     * 25
     * Get Model Name; Read Model Name.
     *
     * @return the name in String type.
     */
    public abstract String getModelName();
/********************add for M11 *******************/
    /**
     * 15
     * Set HDCP KEY (1.4 tx / 2.2 rx ); Write HDCP Key into /persist/factory/hdcp.
     * note: here raw data should be sent to, that is to say, what you received from port, what you should be sent to here.
     * now all product follow this rule; I hope it be upgrade.
     *
     * @return success or no.
     */
    public abstract boolean setHdcpKeyM11(String mac);

    /**
     * 16
     * Get HDCP Key info(1.4 tx / 2.2 rx); Read HDCP Key from /persist/factory/hdcp.
     *
     * @return the HDCP Key in byte array type.
     */
    public abstract byte[] getHdcpKeyM11();

    /**
     * 17
     * Trans HDCP Key (1.4 rx/2.2 tx) to MTK Drm.
     *
     * @return success or no.
     */
    public abstract boolean transHdcpKeyM11();

    /**
     * 18
     * Verify HDCP Key (1.4 rx/2.2 tx).
     *
     * @return success or no.
     */
    public abstract boolean verHdcpKeyM11();

    /**
     * 26
     * Set Wifi FREQ OFFSET; Write into system.
     *
     * @return success or no.
     */
    public abstract boolean setWifiFreqOffset(String offset);

    /**
     * 27
     * Get Wifi FREQ OFFSET; Read from system.
     *
     * @return wifi freq in String type.
     */
    public abstract byte getWifiFreqOffset();
/********************add for M11 *******************/

    /******************** add for projector ***********/
    public abstract boolean setPID(String pid);

    public abstract String getPID();
/******************** add for projector end *******/
    /******************** test for factory product Id  *******/
    public abstract boolean setFactoryPID(String fid);

    public abstract String getFactoryPID();
/******************** test for factory product Id end *******/
    /******************** test for look select  *******/
    public abstract boolean setLookSelect(String ls);

    public abstract String getLookSelect();

    /******************** test for look select *******/
    public abstract String readHdcp14Md5();

    public abstract String readHdcp22Md5();

    public abstract boolean writeHdcpMd5();
}
