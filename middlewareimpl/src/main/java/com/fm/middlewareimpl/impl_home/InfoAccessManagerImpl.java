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

import android.os.Build;
import android.os.SystemProperties;
import android.content.Context;
import android.util.Log;
import java.security.MessageDigest;
import java.io.*;

//amlogic TV
import com.droidlogic.app.KeyManager;
import com.droidlogic.app.SystemControlManager;
import com.fm.middlewareimpl.interf.InfoAccessManagerAbs;

public class InfoAccessManagerImpl extends InfoAccessManagerAbs {

    private static final String TAG = "IMPL_InfoAccess";

	private static final String HDCP_14_FILEPATH = "/persist/hdcp14_key.bin";
	private static final String HDCP_20_FILEPATH = "/persist/hdcp22_key.bin";
	private static final String HDCP_14_TX_FILEPATH = "/persist/hdcp14_txkey.bin";
	private static final String HDCP_22_TX_FILEPATH = "/persist/hdcp22_txkey.bin";
	private static final int HDCP_14_RX_LEN = 348;
	private static final int HDCP_14_TX_LEN = 288;
	private static final int HDCP_20_RX_LEN = 3192;
	private static final int HDCP_20_TX_LEN = 32;

	private static final String BUILD_MODE_DEBUG = "userdebug";
	private static final String BUILD_MODE_USER = "user";

	private KeyManager mKeyManager;
	private SystemControlManager mSystenControl;

	public InfoAccessManagerImpl(Context context) {
		super(context);
		mKeyManager = new KeyManager(context);
		//init key manager
		mKeyManager.aml_init_unifykeys();
	        mSystenControl = new SystemControlManager(context);
	}
	public boolean setPcbaSerialNumber(String sn){
		boolean ret = false;
		ret = writePcbaSn(sn);
		return ret;
	}
	public String getPcbaSerialNumber(){
		String serial = null;
		serial = readPcbaSn();
		return serial;
	}
	public boolean setPcbaManufactureNumber(String mn){
		boolean ret = false;
		ret = writePcbaMn(mn);
		return ret;
	}
	public String getPcbaManufactureNumber(){
		String mn = null;
		mn = readPcbaMn();
		return mn;
	}
	public boolean setAssmSerialNumber(String sn){
		boolean ret = false;
		if ("conan".equals(Build.DEVICE)) {
			String res = checkSN(sn);
			if (null==res){
				return ret;
			}
			ret = writeAssmSn(res);
			return ret;
		}
		ret = writeAssmSn(sn);
		return ret;
	}
	public String getAssmSerialNumber(){
		String serial = null;
		serial = readAssmSn();
		return serial;
	}
	public boolean setAssmManufactureNumber(String mn){
		boolean ret = false;
		ret = writeAssmMn(mn);
		return ret;
	}
	public String getAssmManufactureNumber(){
		String mn = null;
		mn = readAssmMn();
		return mn;
	}
	public boolean setBluetoothMac(String mac){
		boolean ret = false;
		if (mac != null) {
			mac = mac.toLowerCase();
			ret = writeBtMac(mac);
		}
		return ret;
	}
	public String getBluetoothMac(){
		String ret = null;
		ret = readBtMac();
		return ret;
	}
	public boolean setWifiMac(String mac){
		boolean ret = false;
		//ret = setMac(NetType[1][0], mac);
		return ret;
	}
	public String getWifiMac(){
		String ret = null;
		//ret = getMac(NetType[1][0]);
		return ret;
	}
	public boolean setEthernetMac(String mac){
		boolean ret = false;
		if (mac != null) {
			mac = mac.toLowerCase();
			ret = writeEthMac(mac);
		}
		return ret;
	}
	public String getEthernetMac(){
		String ret = null;
		ret = readEthMac();
		return ret;
	}

	public boolean setHdcp14Key(String key){
		boolean ret = false;
		ret = setHdcpKey(key, HDCP_14_FILEPATH, HDCP_14_RX_LEN);
		return ret;
	}
	public byte[] getHdcp14Key(){
		byte[] ret = null;
		ret = getHdcpKey(HDCP_14_FILEPATH, HDCP_14_RX_LEN);
		return ret;
	}
	public boolean setHdcp20Key(String key){
		boolean ret = false;
		ret = setHdcpKey(key, HDCP_20_FILEPATH, HDCP_20_RX_LEN);
		return ret;
	}
	public byte[] getHdcp20Key(){
		byte[] ret = null;
		ret = getHdcpKey(HDCP_20_FILEPATH, HDCP_20_RX_LEN);
		return ret;
	}
	public boolean setHdcp14TxKey(String key){
		boolean ret = false;
		ret = setHdcpKey(key, HDCP_14_TX_FILEPATH, HDCP_14_TX_LEN);
		return ret;
	}
	public byte[] getHdcp14TxKey(){
		byte[] ret = null;
		ret = getHdcpKey(HDCP_14_TX_FILEPATH, HDCP_14_TX_LEN);
		return ret;
	}
	public boolean setHdcp22TxKey(String key){
		boolean ret = false;
		ret = setHdcpKey(key, HDCP_22_TX_FILEPATH, HDCP_20_TX_LEN);
		return ret;
	}
	public byte[] getHdcp22TxKey(){
		byte[] ret = null;
		ret = getHdcpKey(HDCP_22_TX_FILEPATH, HDCP_20_TX_LEN);
		return ret;
	}
	public byte[] getHdcpKsv(){
		byte[] ret = null;
		//ret = readHdcpKsv(HDCP_14_FILEPATH);
		return ret;
	}
	/*********************for M11 start*****************************/
	public boolean setHdcpKeyM11(String key){
		boolean ret = false;
		//ret = writeHdcpKey(key, HDCP_FILEPATH, HDCP_LEN);
		return ret;
	}
	public byte[] getHdcpKeyM11(){
		byte[] ret = null;
		//ret = readHdcpKey(HDCP_FILEPATH, HDCP_LEN);
		return ret;
	}
	public boolean verHdcpKeyM11(){
		boolean ret = false;
		//ret = verifyHdcpKey();
		return ret;
	}
	public boolean transHdcpKeyM11(){
		boolean ret = false;
		//ret = setHdcpKeyToDrm(HDCP_FILEPATH, HDCP_LEN);
		return ret;
	}
	/*********************for M11 end****************************/
	public String getFirmwareVer(){
		String ret = null;
		ret = readFWVer();
		return ret;
	}

	public String getModelName(){
		String ret = null;
		ret = readModelName();
		return ret;
	}
	public boolean setWifiFreqOffset(String offset){
		Log.i(TAG, "offset is " + offset);
		return true;
	}
	public byte getWifiFreqOffset(){
		byte offset_byte = 0x00;
		Log.i(TAG, "getWifiFreqOffset ");
		return offset_byte;
	}

	public String getPID(){
		String pid = null;
		pid = readProductId();
		return pid;
	}

	public boolean setPID(String pid){
		boolean ret = false;
		ret = writeProductId(pid);
		return ret;
	}

	/**
         * set factory product id
         * @param fid
         * @return success
         */
        public boolean setFactoryPID(String fid){
                Log.i(TAG,"setFactoryID:"+fid);
                return writeFactoryProductID(fid);
        }

        /**
         * read factory product id
         * @return factory
         */
        public String getFactoryPID(){
                String fid = null;
		fid = readFactoryProductId();
                Log.i(TAG,"read factory PID:"+fid);
                return fid;
        }
	public boolean setLookSelect(String ls){
		boolean res = false;
		res = writeLookSelect(ls);
		return res;
	}
        public String getLookSelect(){
                String ls = null;
		ls = readLookSelect();
                Log.i(TAG,"read Look Select :"+ls);
                return ls;
        }
	public String readHdcp14Md5(){
		return readKey14Md5();
	}
	public String readHdcp22Md5(){
		return readKey22Md5();
	}
	public boolean writeHdcpMd5(){
		return writeKeyMd5();
	}
	/*===========================================local functions=====================*/
	private String readKey14Md5(){
		String md5 = null;
		md5 = mKeyManager.aml_key_read(HDCP_MD5, 0x0);
		Log.i(TAG,"read hdcp 14 md5 is : "+md5);
		return readKeyMD5(md5,0);
	}
	private String readKey22Md5(){
		String md5 = null;
		md5 = mKeyManager.aml_key_read(HDCP_MD5, 0x0);
		Log.i(TAG,"read hdcp 22 md5 is  : "+md5);
		return readKeyMD5(md5,1);
	}
	/**
     *
     * @param md5 unifykey中的md5字符串
     * @param type 0：hdcp14  1：hdcp22
     * @return md5
     */
    private static String readKeyMD5(String md5,int type){
        if (md5.contains(",")){
            String[] hdcp = md5.split(",");
            if (hdcp.length>type){
                md5 = hdcp[type];
            }
        }
        return md5;
    }
	private boolean writeKeyMd5(){
		byte[] key14 = getHdcpKey(HDCP_14_FILEPATH,HDCP_14_RX_LEN);
		byte[] key22 = getHdcpKey(HDCP_20_FILEPATH,HDCP_20_RX_LEN);
		String md5_14 = string2MD5(key14);
		String md5_22 = string2MD5(key22);
		Log.i(TAG, "hdcp14 md5 is " + md5_14 + "hdcp22 md5 is "+md5_22);
		mKeyManager.aml_key_write(HDCP_MD5, md5_14+","+md5_22, 0x0);
		return true;
	}
	/***
	* MD5加码 生成32位md5码
	*/
	private String string2MD5(byte[] key){
		MessageDigest md5 = null;
		try{
			md5 = MessageDigest.getInstance("MD5");
		}catch (Exception e){
			Log.e(TAG, "Error: "+e.getMessage());
			e.printStackTrace();
			return "";
		}
		byte[] md5Bytes = md5.digest(key);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++){
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		Log.i(TAG, "string2MD5 :"+ hexValue.toString());
		return hexValue.toString();
	}
	private String checkSN(String sn){
		Log.i(TAG,"check ASSM SN old :"+sn);
		if (sn.contains("/")){
			String[] sns = sn.split("/");
			if (sns.length!=2) {
				Log.i(TAG,"unknow  ASSM SN type : "+sn);
				return null;
			}
			sn = sns[1];
		}
		if (sn.length()!=8){
			Log.i(TAG,"check ASSM SN lenght != 8 : "+sn);
			return null;
		}
		Log.i(TAG,"check ASSM SN new :"+sn);
		return sn;
	}
	private static final String PCBA_SERIAL = "pcba_sn";
	private static final String PCBA_MANUFACTURE = "pcba_mn";
	private static final String ASSM_SERIAL = "assm_sn";
	private static final String ASSM_MANUFACTURE = "assm_mn";
	private static final String BT_MAC = "mac_bt";
	private static final String WIFI_MAC = "WIFI_MAC";
	private static final String ETH_MAC = "mac";
	private static final String PRODUCT_ID = "product_id";
	private static final String FACTORY_PID = "product_id_fact";
	private static final String LOOK_SELECT = "look_select";
	private static final String HDCP_MD5 = "md5_hdcp";

	private boolean writePcbaSn(String sn){
		Log.i(TAG, "write pcba sn: " + sn);
		mKeyManager.aml_key_write(PCBA_SERIAL, sn, 0x0);
		return true;
	}
	private String readPcbaSn(){
		String serial = null;
		serial = mKeyManager.aml_key_read(PCBA_SERIAL, 0x0);
		Log.i(TAG, "read pcba sn: " + serial);
		return serial;
	}
	private boolean writePcbaMn(String mn){
		Log.i(TAG, "write pcba mn: " + mn);
		mKeyManager.aml_key_write(PCBA_MANUFACTURE, mn, 0x0);
		return true;
	}
	private String readPcbaMn(){
		String manu = null;
		manu = mKeyManager.aml_key_read(PCBA_MANUFACTURE, 0x0);
		Log.i(TAG, "read pcba mn: " + manu);
		return manu;
	}

	private boolean writeAssmSn(String sn){
		Log.i(TAG, "write Assm sn: " + sn);
		mKeyManager.aml_key_write(ASSM_SERIAL, sn, 0x0);
		return true;
	}
	private String readAssmSn(){
		String serial = null;
		serial = mKeyManager.aml_key_read(ASSM_SERIAL, 0x0);
		Log.i(TAG, "read Assm sn: " + serial);
		return serial;
	}
	private boolean writeAssmMn(String mn){
		Log.i(TAG, "write Assm mn: " + mn);
		mKeyManager.aml_key_write(ASSM_MANUFACTURE, mn, 0x0);
		return true;
	}
	private String readAssmMn(){
		String manu = null;
		manu = mKeyManager.aml_key_read(ASSM_MANUFACTURE, 0x0);
		Log.i(TAG, "read Assm mn: " + manu);
		return manu;
	}

	private boolean writeEthMac(String mac){
		Log.i(TAG, "write Ethernet Mac: " + mac);
		mKeyManager.aml_key_write(ETH_MAC, mac, 0x0);
		return true;
	}
	private String readEthMac(){
		String mac = null;
		mac = mKeyManager.aml_key_read(ETH_MAC, 0x0);
		Log.i(TAG, "read Ethernet Mac: " + mac);
		return mac;
	}
	private boolean writeBtMac(String mac){
		Log.i(TAG, "write Bluetooth Mac: " + mac);
		mKeyManager.aml_key_write(BT_MAC, mac, 0x0);
		return true;
	}
	private String readBtMac(){
		String mac = null;
		mac = mKeyManager.aml_key_read(BT_MAC, 0x0);
		Log.i(TAG, "read Bluetooth Mac: " + mac);
		return mac;
	}
	private final static String VERSION_INCREMENTAL = "ro.build.version.incremental";
	private final static String BUILD_TYPE = "ro.build.type";
	private static final String BUILD_MODELNAME = "ro.build.product";
	private static final String ERROR_RESULT = "xxxx";
	private String readFWVer(){
		String ver = null;
		String mode = null;
		String userMode = "U";
		String debugMode = "D";
		String otherMode = "O";
		ver = SystemProperties.get(VERSION_INCREMENTAL, ERROR_RESULT);
		mode = SystemProperties.get(BUILD_TYPE, ERROR_RESULT);
		if(mode.equals(BUILD_MODE_USER)){
			ver = ver + userMode;
		}else if(mode.equals(BUILD_MODE_DEBUG)){
			ver = ver + debugMode;
		}else{
			ver = ver + otherMode;
		}
		Log.i(TAG, "get SW version: " + "[" + ver + "]");
		return ver;
	}
	private String readModelName(){
		String name = null;
		name = SystemProperties.get(BUILD_MODELNAME, ERROR_RESULT);
		Log.i(TAG, "get model name: " + "[" + name + "]");
		return name;
	}
	private boolean setHdcpKey(String key, String path, int len){
		boolean ret = false;
		byte key_byte[] = new byte[len];
		String keys[] = key.split(",");
		Log.e(TAG, "standard key length : " + len + "param key length : "+keys.length);
		if(keys.length > len){
		    for (int i = 0; i < keys.length; i++) {
		        Log.e(TAG, "param key ["+i+"] : "+keys[i]);
		    }
		    Log.e(TAG, key);
		    return false;
		}
		for(int i=0;i<keys.length;i++){
			key_byte[i] = (byte) Integer.parseInt(keys[i]);
		}
		File hdcpfile = new File(path);
		if(!hdcpfile.exists()){
			try {
				hdcpfile.createNewFile();
			} catch (IOException e) {
				Log.e(TAG, "Error: "+e.getMessage());
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fstream = new FileOutputStream(hdcpfile);
			fstream.write(key_byte,0,len);
			fstream.getFD().sync();
			fstream.close();
			hdcpfile.setReadable(true,false);
			Log.e(TAG, "Set Hdcp Key: OK");
			ret = true;
		} catch (IOException e) {
			Log.e(TAG, "Error: "+e.getMessage());
			e.printStackTrace();
		}
		return ret;
	}
	private byte[] getHdcpKey(String path, int len){
		byte[] key = new byte[len];
		Log.i(TAG, "Get Hdcp Key");
		File hdcpfile = new File(path);
		if (hdcpfile.exists()) {
			try {
				FileInputStream fstream = new FileInputStream(hdcpfile);
				int ret = fstream.read(key,0,len);
				fstream.close();
				Log.e(TAG, "Get Hdcp Key: get " + ret + "bytes ");
			} catch (IOException e) {
				Log.e(TAG, "Error: "+e.getMessage());
				e.printStackTrace();
			}
		}
		return key;
	}
	/*===========================================local functions=====================*/
	/*===========================================tool functions=====================*/
	private String delSpace(String src){
		String dest = "";
		String srcs[] = src.split(",");
		for(int i = 0; i < srcs.length ; i++){
			dest += String.format("%02x",Integer.parseInt(srcs[i]));
		}
		return dest;
	}
	/*===========================================tool functions=====================*/
	private boolean writeProductId(String pid){
		Log.i(TAG,"write PID:"+pid);
		mKeyManager.aml_key_write(PRODUCT_ID, pid, 0x0);
		return true;
	}

	private String readProductId(){
		String pid = null;
		pid = mKeyManager.aml_key_read(PRODUCT_ID, 0x0);
		Log.i(TAG,"read PID:"+pid);
		return pid;
	}

	/**
	 * factory product id 写入
	 * @param fid String
	 * @return success
	 */
	private boolean writeFactoryProductID(String fid){
		Log.i(TAG,"write factory product pid" + fid);
		mKeyManager.aml_key_write(FACTORY_PID,fid,0x0);
		return true;
	}

	/**
	 * factory product id 读取
	 * @return fid  String
	 */
	private String readFactoryProductId(){
		String fid = null;
		fid = mKeyManager.aml_key_read(FACTORY_PID,0x0);
		Log.i(TAG,"read factory product pid:"+fid);
		return fid;
	}
	private boolean writeLookSelect(String ls){
		Log.i(TAG,"set Look_Select : "+ls);
		mKeyManager.aml_key_write(LOOK_SELECT, ls, 0x0);
		return true;
	}

	private String readLookSelect(){
		String ls = null;
		ls = mKeyManager.aml_key_read(LOOK_SELECT, 0x0);
		Log.i(TAG,"read Loock Select : "+ls);
		return ls;
	}
}
