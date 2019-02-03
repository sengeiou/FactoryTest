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
 * 1. set wifi state。启动/停止wifi。（wifiSetStatus)
 * 2. wifi connect。连接wifi到指定路由器。（wifiConnectAp)
 * 3. wifi ping。通过wifi，ping指定的路由器。（wifiPingAp)
 * 4. wifi throughput Start。对wifi做吞吐量测试。（wifiThroughputStart）
 * 4.1 wifi throughput Stop。对wifi做吞吐量测试。（wifiThroughputStop）
 * 5. get wifi ip address。报告AP分配给wifi设备的IP地址。（wifiGetIpAddr)
 * 6. get wifi Rssi。报告与DUT连接的AP的RSSI。（wifiGetRssi）
 * 7. wifi disconnect。断开wifi与指定AP的连接。(wifiDisconnect)
 * 15. scan ap for user mode tes. 扫描周围ap（wifiStartScan）
 * 16. get scan list user mode tes. 获取扫描ap list（wifiGetScanList）
 * -------------- BT -----------------
 * 8. set Bluetooth state。启动/停止蓝牙。（btSetStatus,开始/停止蓝牙搜索)
 * 15. set Bluetooth state。启动/停止蓝牙。（btSetStatusBLE，开始/停止BLE蓝牙搜索)
 * 9. bt scan。bt搜索指定的SSID。（btStartScan）
 * 10. get bt Rssi。汇报于DUT连接的dongle的RSSI。（btGetRssi）
 * 14. get scaned device list(btGetList)
 * -------------- ETH -----------------
 * 11. set Ethernet state。启动/停止以太网。（ethernetSetStatus)
 * 12. ethernet ping。通过ethernet，ping指定的路由器或PC。（ethernetPingAp）
 * -------------- NFC(预留） -----------------
 * 13. set Nfc state。启动/停止NFC。（setNfcStatus)
 * note:
 * 1. 测试路由器的命名中，5G路由器的SSID中必须包含“5G”, 建议命名为“MITEST_5GAP”
 * 2. 测试路由器的命名中，2.4G的路由器的SSID中必须包括“24G”, 建议命名为“MITEST_24GAP”
 * 3. wifi enable到能够connect to AP之间的时间至少为2.3s，建议至少为4s;
 * 4. connect to AP到能够获取到IP的时间至少是2s，但是建议不小于3s。
 */

package com.fm.middlewareimpl.interf;

import android.content.Context;
import android.net.wifi.ScanResult;



import java.util.Dictionary;
import java.util.List;

public abstract class RfNetManagerAbs extends BaseMiddleware {
    public RfNetManagerAbs(Context context) {
        super(context);
    }

    /**
     * enable/disable wifi function.
     *
     * @return success or no.
     */
    public abstract boolean wifiSetStatus(boolean stat);

    /**
     * get wifi function working status.
     *
     * @return success or no.
     */
    public abstract boolean wifiGetStatus();

    /**
     * connect to AP by wifi.
     *
     * @return success or no.
     */
    public abstract boolean wifiConnectAp(String ssid);

    /**
     * start throughput.
     *
     * @return success or no.
     */
    public abstract boolean wifiThroughputStart();

    /**
     * start throughput with parameter.
     *
     * @return success or no.
     */
    public abstract boolean wifiThroughputWithParameter(String para);

    /**
     * stop throughput.
     *
     * @return success or no.
     */
    public abstract boolean wifiThroughputStop();

    /**
     * after connect to AP, get the allocated IP address.
     *
     * @return IP address with String type.
     */
    public abstract String wifiGetIpAddr();

    /**
     * after connect to AP, get the AP's RSSI.
     *
     * @return RSSI value with integer type.
     */
    public abstract int wifiGetRssi();

    /**
     * after connect to AP, get the AP's RSSI.
     *
     * @return RSSI value with integer type.
     */
    public abstract byte[] wifiGetRssiBox();

    /**
     * disconnect to AP by wifi.
     *
     * @return success or no.
     */
    public abstract boolean wifiDisconnect();

    /**
     * wifi start to scan and fill a temp list to save all of the scanned device.
     *
     * @return success or no.
     */
    public abstract boolean wifiStartScan();
    /**
     * wifi get scan list of the scanned device.
     * @return List<ScanResult>.
     */
    //public abstract List<ScanResult> wifiGetScanList();

    /**
     * enable/disable Bluetooth Function.
     *
     * @return success or no.
     */
    public abstract boolean btSetStatus(boolean stat);

    /**
     * enable/disable Bluetooth BLE scan Function.
     *
     * @return success or no.
     */
    public abstract boolean btSetStatusBLE(boolean stat);

    /**
     * get Bluetooth Function working status.
     *
     * @return success or no.
     */
    public abstract boolean btGetStatus();

    /**
     * bt start to scan and fill a temp list to save all of the scanned device.
     *
     * @return success or no.
     */
    public abstract boolean btStartScan();

    /**
     * after find pointed ssid, read it's RSSI.
     *
     * @return in integer type.
     */
    public abstract int btGetRssi(String mac);
    /**
     * after scan,read devices list.
     * @return List<BTDevice> type.
     */
    //public abstract List<BTDevice> btGetList();

    /**
     * enable/disable ethernet Function.
     *
     * @return success or no.
     */
    public abstract boolean ethernetSetStatus(boolean stat);

    /**
     * get ethernet Function working status.
     *
     * @return success or no.
     */
    public abstract boolean ethernetGetStatus();

    /**
     * ping pointed IP by ethernet.
     *
     * @return success or no.
     */
    public abstract boolean ethernetPingAp(String ipaddr);

    /**
     * ping pointed IP by Wifi.
     *
     * @return success or no.
     */
    public abstract boolean wifiPingAp(String ipaddr);

    //empty function for compatible box
    public abstract byte[] nonSigHciCmdRun(String param);

    public abstract boolean nonSigCloseNormalBt();

    public abstract boolean nonSigOpenNormalBt();

    public abstract int nonSigIsBleSupport();

    public abstract int nonSigInitBt();

    public abstract int nonSigUninitBt();

    public abstract int nonSigBleGetChipId();

    public abstract boolean enterWifi();

    public abstract boolean exitWifi();

    public abstract boolean startWifiRx(String param);

    public abstract String stopWifiRx();

    public abstract boolean startWifiTx(String param);

    public abstract boolean stopWifiTx();

    public abstract List<ScanResult> wifiGetScanList();

    public abstract boolean setBtRcMac(String param);

    // public abstract List<BT> btGetList();
}
