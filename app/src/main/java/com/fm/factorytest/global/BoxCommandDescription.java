/* project description:
 * now the TV projects are:
 * --- gladiator
 *  -- hancock
 *
 *
 * */
package com.fm.factorytest.global;
import android.content.ComponentName;
import android.util.Log;

public class BoxCommandDescription{
	public static final String TAG = "BoxCommandDescription";
	/* ------------------------------------ class property start ---------------------------*/
	static private BoxCommandDescription _BoxCommandDescription = null;
	protected BoxCommandDescription(){
	}
	static public BoxCommandDescription getInstance(){
		if(_BoxCommandDescription == null){
			synchronized(BoxCommandDescription.class){
				if(_BoxCommandDescription == null){
					_BoxCommandDescription = new BoxCommandDescription();
				}
			}
		}
		return _BoxCommandDescription;
	}
	/* ------------------------------------ class property stop ---------------------------*/

	/* ------------------------------------ command id start ---------------------------*/
	/********
	 * command ID Recycling pool
	 * 1135-1136,1138,113a-113f
	 * */
	/***************1. BOX command ID(the prefix is 0x2)**************/
	public static final int CMDID_TF_DECT = 0x2101;
	public static final int CMDID_UDISK_DECT_3_0 = 0x2102;
	public static final int CMDID_UDISK_DECT_2_0 = 0x210b;//-- box valid
	public static final int CMDID_ADB_TO_USB = 0x221e;//-- box valid
	public static final int CMDID_UDISK_UNMOUNT_3_0 = 0x210c;
	public static final int CMDID_TF_UNMOUNT = 0x210d;
	public static final int CMDID_KTV_TEST = 0x2103;
	public static final int CMDID_SOUR_START = 0x2104;
	public static final int CMDID_SOUR_SWITCH = 0x2105;
	public static final int CMDID_SOUR_STOP = 0x2106;
	public static final int CMDID_HDMI_ARC_ON = 0x211f;
	public static final int CMDID_HDMI_ARC_OFF = 0x2107;
	public static final int CMDID_HDMI_1_CEC = 0x2109;
	public static final int CMDID_HDMI_1_CEC_NAME = 0x210e;
	public static final int CMDID_HDMI_2_CEC = 0x2111;
	public static final int CMDID_HDMI_2_CEC_NAME = 0x210f;
	public static final int CMDID_MEDIA_PLAY = 0x2113;//-- box valid
	public static final int CMDID_MEDIA_STOP = 0x211a;//-- box valid
	public static final int CMDID_DISPLAY_SWITCH = 0x221f;//-- box valid
	public static final int CMDID_TOUCHPAD_TEST = 0x211d;
	public static final int CMDID_MHL_CEC_3 = 0x2114;
	public static final int CMDID_MHL_CEC_3_NAME = 0x211e;
	public static final int CMDID_ENABLE_SPEAKER = 0x2115;
	public static final int CMDID_DISABLE_SPEAKER = 0x2116;
	public static final int CMDID_PLAY_SPEAKER_AUDIO = 0x2117;
	public static final int CMDID_ENABLE_SPDIF = 0x2118;
	public static final int CMDID_DISABLE_SPDIF = 0x2119;
	public static final int CMDID_GET_SOUND_VOLUME = 0x211b;
	public static final int CMDID_SET_SOUND_VOLUME = 0x211c;

	public static final int CMDID_PLAY_SPDIF_AUDIO = 0x2120;
	public static final int CMDID_WIFI_QUICKCONNECT = 0x242a;//-- box valid
	public static final int CMDID_WIFI_IPADDR_GET = 0x242c;//-- box valid
	public static final int CMDID_WIFI_RSSI_GET = 0x242b;//-- box valid
	public static final int CMDID_SET_WIFI_STATE = 0x212d;//-- box valid
	public static final int CMDID_SET_BT_STATE = 0x212e;//-- box valid
	public static final int CMDID_SET_BT_STATE_BLE = 0x212f;//-- box valid
	public static final int CMDID_WIFI_PING = 0x2121;//-- box valid
	public static final int CMDID_BT_SCAN = 0x2422;//-- box valid
	public static final int CMDID_ETH_PING = 0x2123;
	public static final int CMDID_IR_START = 0x2124;
	public static final int CMDID_IR_STOP = 0x2125;
	public static final int CMDID_SET_ETH_STATE = 0x2127;
	public static final int CMDID_GET_ETH_STATE = 0x2128;
	public static final int CMDID_SET_PRODUCT_FEATURE = 0x2129;
	public static final int CMDID_CHECK_PRODUCT_FEATURE = 0x2130;

	public static final int CMDID_PCBA_SERIAL_WRITE = 0x2400;//-- box valid
	public static final int CMDID_PCBA_SERIAL_READ = 0x2401;//-- box valid
	public static final int CMDID_ASSM_SERIAL_WRITE = 0x240c;//-- box valid
	public static final int CMDID_ASSM_SERIAL_READ = 0x240d;//-- box valid
	public static final int CMDID_BT_MAC_WRITE = 0x2402;//-- box valid
	public static final int CMDID_BT_MAC_READ = 0x2403;//-- box valid
	public static final int CMDID_ETH_MAC_WRITE = 0x2404;
	public static final int CMDID_ETH_MAC_READ = 0x2405;
	public static final int CMDID_WIFI_MAC_WRITE = 0x2406;//-- box valid
	public static final int CMDID_WIFI_MAC_READ = 0x2407;//-- box valid
	public static final int CMDID_WIFI_FREQ_OFFSET_WRITE = 0x2476;//-- box valid
	public static final int CMDID_WIFI_FREQ_OFFSET_READ = 0x2477;//-- box valid

	public static final int CMDID_PCBA_MANUFACTURE_ID_WRITE = 0x2414;//-- box valid
	public static final int CMDID_PCBA_MANUFACTURE_ID_READ = 0x2415;//-- box valid
	public static final int CMDID_ASSM_MANUFACTURE_ID_WRITE = 0x2416;//-- box valid
	public static final int CMDID_ASSM_MANUFACTURE_ID_READ = 0x2417;//-- box valid
	public static final int CMDID_NFC_READ = 0x2139;
	public static final int CMDID_NFC_TAG = 0x2140;

	public static final int CMDID_KTV_ENABLE = 0x2200;
	public static final int CMDID_ATV_CHANNEL_CHANGE = 0x2201;
	public static final int CMDID_ATV_CHANNEL_LOAD_TAB = 0x2202;
	public static final int CMDID_DTV_CHANNEL_CHANGE = 0x2203;
	public static final int CMDID_DTV_CHANNEL_LOAD_TAB = 0x2204;
	public static final int CMDID_SET_3D_MODE = 0x2205;
	public static final int CMDID_GET_3D_MODE = 0x2206;
	public static final int CMDID_WIFI_CONNECT_24AP = 0x2207;
	public static final int CMDID_WIFI_CONNECT_5AP = 0x2208;
	public static final int CMDID_WIFI_SPEED_START = 0x2209;//-- box valid
	public static final int CMDID_WIFI_SPEED_STOP = 0x220b;//-- box valid
	public static final int CMDID_BT_3D_SYNC_FLAG = 0x220c;
	public static final int CMDID_WIFI_WAKEUP = 0x2210;
	public static final int CMDID_BT_PCM_LOOPBACK = 0x2211;
	public static final int CMDID_BT_THROUGH_OUTPUT = 0x2212;
	public static final int CMDID_BT_FORCE_MATCH = 0x2213;
	public static final int CMDID_BT_WAKEUP = 0x2214;
	public static final int CMDID_BACKLIGHT_SET = 0x2220;
	public static final int CMDID_BACKLIGHT_GET = 0x2421;

	public static final int CMDID_AGING_ON =	0x2222;//-- box valid
	public static final int CMDID_AGING_OFF = 0x2223;//-- box valid
	public static final int CMDID_AGING_RESET_TIMER = 0x2224;//-- box valid
	public static final int CMDID_AGING_GET_TIMER = 0x243a;//-- box valid
	public static final int CMDID_AGING_TIMER_START = 0x2270;//-- box valid
	public static final int CMDID_AGING_TIMER_STOP = 0x2271;//-- box valid
	public static final int CMDID_PATTERN_SET = 0x2225;
	public static final int CMDID_PATTERN_DISABLE = 0x222b;
	public static final int CMDID_SYSTEM_MODE_GET = 0x2232;
	public static final int CMDID_SYSTEM_MODE_SET = 0x2231;
	public static final int CMDID_SYSTEM_REBOOT_RECOVERY = 0x2233;
	public static final int CMDID_FACTORY_RESET = 0x2227;
	public static final int CMDID_SYSTEM_MODE_CHANGE = 0x2228;//-- box valid
	public static final int CMDID_SYSTEM_REBOOT = 0x2229;
	public static final int CMDID_SYSTEM_SHUTDOWN = 0x222f;
	public static final int CMDID_LIGHT_SENSOR = 0x2430;
	public static final int CMDID_SET_PRODUCTREGION = 0x2461;
	public static final int CMDID_GET_PRODUCTREGION = 0x2462;
	/*
	 * cmd buf: 2431 - 2439
	 * 223a - 223f
	 * 222c - 222d
	 * 2240 - 2249
	 * 246*
	 * */
	//Sound Mode
	public static final int CMDID_RESET_SOUND_MODE = 0x2252;
	public static final int CMDID_SET_SOUND_MODE = 0x2253;
	public static final int CMDID_SET_SOUND_BALANCE = 0x2254;
	public static final int CMDID_SET_SOUND_MUTE = 0x2255;

	public static final int CMDID_MODEL_NAME_GET = 0x2456;//-- box valid
	public static final int CMDID_GET_SYSTEM_FW_VER = 0x2457;//-- box valid
	public static final int CMDID_GET_MCU_FW_VER = 0x2258;
	public static final int CMDID_GET_PQ_VER = 0x2259;
	public static final int CMDID_GET_AQ_VER = 0x2260;

	public static final int CMDID_LED_TEST = 0x2261; //-- box valid
	public static final int CMDID_VGA_AUTO_SYNC = 0x2262;
	public static final int CMDID_ASPECT_MODE_SET = 0x2263;
	public static final int CMDID_RC_LOCK = 0x2264;

	public static final int CMDID_HDCP_KSV_GET = 0x2412;
	public static final int CMDID_HDMI_EDID = 0x2413;

	public static final int CMDID_SET_PIC_FULLHD = 0x226d;
	public static final int CMDID_SYSTEM_SLEEP = 0x222a;
	public static final int CMDID_SYSTEM_PARTITION_CHECK = 0x2281;
	public static final int CMDID_BTRC_MAC_SET = 0x2272;//-- box valid
	public static final int CMDID_BTRC_MAC_GET = 0x2463;//-- box valid
	public static final int CMDID_RESET_PANEL_SEL = 0x2273;
	public static final int CMDID_WIFI_DISCONNECT = 0x2126;//-- box valid
	public static final int CMDID_WOOFER_PLUGIN = 0x2112;
	public static final int CMDID_POWERSTANDBY = 0x2122;
	public static final int CMDID_HDMI_HPD_RESET = 0x2110;
	public static final int CMDID_UPDATE_I2C_PORT_TEST = 0x210a;
	public static final int CMDID_CHECK_HDCPKEY14_VALID = 0x212a;
	public static final int CMDID_CHECK_HDCPKEY22_VALID = 0x212b;
	public static final int CMDID_SYSTEM_MASTER_CLEAR = 0x212c;
	public static final int CMDID_SET_AUTORUN_STATUS = 0x2131;//-- box valid
	public static final int CMDID_GET_AUTORUN_STATUS = 0x2132;//-- box valid
	public static final int CMDID_SET_AUTORUN_COMMAND = 0x2133;//-- box valid
	public static final int CMDID_GET_AUTORUN_COMMAND = 0x2134;//-- box valid

	//MTK M11(M7)
	public static final int CMDID_SET_FAN_STAT = 0x2230;//-- box valid
	public static final int CMDID_GET_CPU_TEMP = 0x2234;//-- box valid
	//M11 1.4 tx and 2.2 rx in one file, so just need read/write/trans/verify 4 command
	public static final int CMDID_HDCP_KEY_WRITE  = 0x2408;//-- box valid
	public static final int CMDID_HDCP_KEY_READ   = 0x2409;//-- box valid
	public static final int CMDID_HDCP_KEY_TRANS  = 0x240a;//-- box valid
	public static final int CMDID_HDCP_KEY_VERIFY = 0x240b;//-- box valid
	//BT nonsignal (2150 ~ 216F) BLE
	public static final int CMDID_BT_NONSIG_GETCHIPID = 0x2150;//-- box valid
	public static final int CMDID_BT_NONSIG_INIT = 0x2151;//-- box valid
	public static final int CMDID_BT_NONSIG_UNINIT = 0x2152;//-- box valid
	public static final int CMDID_BT_NONSIG_CHECKBLESUPPORT = 0x2153;//-- box valid
	public static final int CMDID_BT_NONSIG_CLOSENORMAL = 0x2154;//-- box valid
	public static final int CMDID_BT_NONSIG_OPENNORMAL = 0x2155;//-- box valid
	public static final int CMDID_BT_NONSIG_HCICMDRUN = 0x2156;//-- box valid
	//public static final int CMDID_ = 0x;
	//WIFI nonsignal (2170 ~ 219F)
	//public static final int CMDID_ = 0x2170;

	//RF test wifi nonsignal
	public static final int CMDID_ENTER_WIFI_RF  = 0x2470;
	public static final int CMDID_EXIT_WIFI_RF   = 0x2471;
	public static final int CMDID_WIFI_RX_START  = 0x2472;
	public static final int CMDID_WIFI_RX_STOP   = 0x2473;
	public static final int CMDID_WIFI_TX_START  = 0x2474;
	public static final int CMDID_WIFI_TX_STOP   = 0x2475;



	//public static final int CMDID_BT_RX_START    = 0x2476;
	//public static final int CMDID_BT_RX_STOP     = 0x2477;
	public static final int CMDID_BT_TX_START    = 0x2478;
	public static final int CMDID_BT_TX_STOP     = 0x2479;
	public static final int CMDID_BT_RF_FINISH   = 0x2480;
	public static final int CMDID_BT_RF_RESET    = 0x2481;
	/***************2. BOX command ID(the prefix is 0x2)**************/
	/* ------------------------------------ command id stop ---------------------------*/

	/* ------------------------------------ command property start ---------------------------*/
	/**************4. define CommandType***********************/
	public static final String CMD_TYPE_COMMON= "1";
	public static final String CMD_TYPE_ACTIVITY_ON = "2";
	public static final String CMD_TYPE_ACTIVITY_OFF = "3";
	public static final String CMD_TYPE_INNACTIVITY = "4";
	/* ------------------------------------ command property stop ---------------------------*/

	/* ------------------------------------ command table start ---------------------------*/
	/**************5. define cmdDesc***********************/
	public static final String[][] cmdDesc = {
		/**************5.1 TV Segment***********************/
		{Integer.toHexString(CMDID_TF_DECT).toUpperCase(), "TF Test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_UDISK_DECT_3_0).toUpperCase(), "USB3.0 Test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_UDISK_DECT_2_0).toUpperCase(), "USB2.0 Test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_UDISK_UNMOUNT_3_0).toUpperCase(), "USB3.0 Umount", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_TF_UNMOUNT).toUpperCase(), "TF Umount", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_KTV_TEST).toUpperCase(), "KTV Test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SOUR_START).toUpperCase(), "Source Start", CMD_TYPE_ACTIVITY_ON},
		{Integer.toHexString(CMDID_SOUR_SWITCH).toUpperCase(), "Source Switch", CMD_TYPE_INNACTIVITY},
		{Integer.toHexString(CMDID_SOUR_STOP).toUpperCase(), "Source Close", CMD_TYPE_ACTIVITY_OFF},
		{Integer.toHexString(CMDID_HDMI_ARC_ON).toUpperCase(), "HDMI ARC Test ON", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_HDMI_ARC_OFF).toUpperCase(), "HDMI ARC Test OFF", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_HDMI_1_CEC).toUpperCase(), "HDMI1 CEC Test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_HDMI_1_CEC_NAME).toUpperCase(), "HDMI1 CEC Name Test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_HDMI_2_CEC).toUpperCase(), "HDMI2 CEC Test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_HDMI_2_CEC_NAME).toUpperCase(), "HDMI2 CEC name Test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_MEDIA_PLAY).toUpperCase(), "LocalMediaPlay Start", CMD_TYPE_ACTIVITY_ON},
		{Integer.toHexString(CMDID_MEDIA_STOP).toUpperCase(), "LocalMediaPlay Stop", CMD_TYPE_ACTIVITY_OFF},
		{Integer.toHexString(CMDID_TOUCHPAD_TEST).toUpperCase(), "Touchpad Test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_MHL_CEC_3).toUpperCase(), "HDMI3 CEC Test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_MHL_CEC_3_NAME).toUpperCase(), "HDMI3 CEC Name Test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_ENABLE_SPEAKER).toUpperCase(), "Speaker Enable", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_DISABLE_SPEAKER).toUpperCase(), "Speaker Disable", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_PLAY_SPEAKER_AUDIO).toUpperCase(), "Audio Play to Speaker", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_ENABLE_SPDIF).toUpperCase(), "Spdif Enable", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_DISABLE_SPDIF).toUpperCase(), "Spdif Disable", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_GET_SOUND_VOLUME).toUpperCase(), "Volume Get", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SET_SOUND_VOLUME).toUpperCase(), "Volume Set", CMD_TYPE_COMMON},

		{Integer.toHexString(CMDID_PLAY_SPDIF_AUDIO).toUpperCase(), "Audio Play to SPDIF", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WIFI_QUICKCONNECT).toUpperCase(), "WIFI QuickConnect", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WIFI_IPADDR_GET).toUpperCase(), "WIFI Get IP", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WIFI_RSSI_GET).toUpperCase(), "WIFI Get RSSI", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SET_WIFI_STATE).toUpperCase(), "WIFI Set State", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SET_BT_STATE).toUpperCase(), "BT Set State", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SET_BT_STATE_BLE).toUpperCase(), "BT Set State for BLE state", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WIFI_PING).toUpperCase(), "WIFI Ping", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_SCAN).toUpperCase(), "BT Scan", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_ETH_PING).toUpperCase(), "ETH Ping", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SET_ETH_STATE).toUpperCase(), "ETH set state", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_GET_ETH_STATE).toUpperCase(), "ETH get state", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SET_PRODUCT_FEATURE).toUpperCase(), "set product feature", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_CHECK_PRODUCT_FEATURE).toUpperCase(), "get product feature", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_IR_START).toUpperCase(), "IR Test Start", CMD_TYPE_ACTIVITY_ON},
		{Integer.toHexString(CMDID_IR_STOP).toUpperCase(), "IR Test Stop", CMD_TYPE_ACTIVITY_OFF},

		{Integer.toHexString(CMDID_PCBA_SERIAL_WRITE).toUpperCase(), "PCBA SN Write", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_PCBA_SERIAL_READ).toUpperCase(), "PCBA SN Read", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_ASSM_SERIAL_WRITE).toUpperCase(), "ASSM SN Write", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_ASSM_SERIAL_READ).toUpperCase(), "ASSM SN Read", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_MAC_WRITE).toUpperCase(), "BTMAC Write", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_MAC_READ).toUpperCase(), "BTMAC Read", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_ETH_MAC_WRITE).toUpperCase(), "ETHMAC Write", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_ETH_MAC_READ).toUpperCase(), "ETHMAC Read", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WIFI_MAC_WRITE).toUpperCase(), "WIFIMAC Write", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WIFI_MAC_READ).toUpperCase(), "WIFIMAC Read", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_HDCP_KEY_WRITE).toUpperCase(), "HDCPKEY Write", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_HDCP_KEY_READ).toUpperCase(), "HDCPKEY Read", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_HDCP_KEY_TRANS).toUpperCase(), "MIRACAST Write", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_HDCP_KEY_VERIFY).toUpperCase(), "MIRACAST Trans", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_PCBA_MANUFACTURE_ID_WRITE).toUpperCase(), "PCBA MN Write", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_PCBA_MANUFACTURE_ID_READ).toUpperCase(), "PCBA MN Read", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_ASSM_MANUFACTURE_ID_WRITE).toUpperCase(), "ASSM MN Write", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_ASSM_MANUFACTURE_ID_READ).toUpperCase(), "ASSM MN Read", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_NFC_READ).toUpperCase(), "NFC Test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_NFC_TAG).toUpperCase(), "NFC TAG", CMD_TYPE_COMMON},

		{Integer.toHexString(CMDID_KTV_ENABLE).toUpperCase(), "KTV Enable", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_ATV_CHANNEL_CHANGE).toUpperCase(), "ATV Switch Chan", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_ATV_CHANNEL_LOAD_TAB).toUpperCase(), "ATV Load Chan Tab", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_DTV_CHANNEL_CHANGE).toUpperCase(), "DTV Switch Chan", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_DTV_CHANNEL_LOAD_TAB).toUpperCase(), "DTV Load Chan Tab", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SET_3D_MODE).toUpperCase(), "3DMode Set", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_GET_3D_MODE).toUpperCase(), "3DMode Get", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WIFI_CONNECT_24AP).toUpperCase(), "WIFI Conn to 24G", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WIFI_CONNECT_5AP).toUpperCase(), "WIFI Conn to 5G", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WIFI_SPEED_START).toUpperCase(), "WIFISPEED Start", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WIFI_SPEED_STOP).toUpperCase(), "WIFISPEED Stop", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_3D_SYNC_FLAG).toUpperCase(), "3DSYNC Flag", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WIFI_WAKEUP).toUpperCase(), "WIFI WakeUp", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_PCM_LOOPBACK).toUpperCase(), "BTPCM Loopback", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_THROUGH_OUTPUT).toUpperCase(), "BT Throughput", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_FORCE_MATCH).toUpperCase(), "BT Force Match", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_WAKEUP).toUpperCase(), "BT WakeUp", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BACKLIGHT_SET).toUpperCase(), "Backlight Set", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BACKLIGHT_GET).toUpperCase(), "Backlight Get", CMD_TYPE_COMMON},

		{Integer.toHexString(CMDID_AGING_ON).toUpperCase(), 	"Aging On", CMD_TYPE_ACTIVITY_ON},
		{Integer.toHexString(CMDID_AGING_OFF).toUpperCase(), "Aging Off", CMD_TYPE_ACTIVITY_OFF},
		{Integer.toHexString(CMDID_AGING_RESET_TIMER).toUpperCase(), "Aging Rest Timer", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_AGING_GET_TIMER).toUpperCase(), "Aging Get Timer", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_AGING_TIMER_START).toUpperCase(), "Aging Start Timer", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_AGING_TIMER_STOP).toUpperCase(), "Aging Stop Timer", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_PATTERN_SET).toUpperCase(), "Pattern Set", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_PATTERN_DISABLE).toUpperCase(), "Pattern Disable", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SYSTEM_MODE_GET).toUpperCase(), "SystemMode Get", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_FACTORY_RESET).toUpperCase(), "Factory Reset", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SYSTEM_MODE_CHANGE).toUpperCase(), "SystemMode Switch", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SYSTEM_REBOOT).toUpperCase(), "System Reboot", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SYSTEM_REBOOT_RECOVERY).toUpperCase(), "System Reboot & enter recovery", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SYSTEM_SHUTDOWN).toUpperCase(), "System Shutdown", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_LIGHT_SENSOR).toUpperCase(), "LightSensor Get", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SET_PRODUCTREGION).toUpperCase(), "ProductRegion Set", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_GET_PRODUCTREGION).toUpperCase(), "ProductRegion Get", CMD_TYPE_COMMON},
		//Sound Mode
		{Integer.toHexString(CMDID_RESET_SOUND_MODE).toUpperCase(), "Sound Mode Reset", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SET_SOUND_MODE).toUpperCase(), "Sound Mode Set", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SET_SOUND_BALANCE).toUpperCase(), "Sound Balance Set", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SET_SOUND_MUTE).toUpperCase(), "Sound Mute Set", CMD_TYPE_COMMON},

		{Integer.toHexString(CMDID_MODEL_NAME_GET).toUpperCase(), "ModelName Get", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_GET_SYSTEM_FW_VER).toUpperCase(), "SystemFW Ver Get", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_GET_MCU_FW_VER).toUpperCase(), "MCU FW Get", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_GET_PQ_VER).toUpperCase(), "PQ Ver Get", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_GET_AQ_VER).toUpperCase(), "AQ Ver Get", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_LED_TEST).toUpperCase(), "LED Test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_VGA_AUTO_SYNC).toUpperCase(), "VGA Auto-Sync", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_ASPECT_MODE_SET).toUpperCase(), "AspectMode Set", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_RC_LOCK).toUpperCase(), "RC Lock", CMD_TYPE_COMMON},

		{Integer.toHexString(CMDID_HDCP_KSV_GET).toUpperCase(), "HDCP KSV Get", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_HDMI_EDID).toUpperCase(), "HDMI EDID Get", CMD_TYPE_COMMON},

		{Integer.toHexString(CMDID_SET_PIC_FULLHD).toUpperCase(), "PIC FullHD Set", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SYSTEM_SLEEP).toUpperCase(), "System Sleep Mode Set", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SYSTEM_PARTITION_CHECK).toUpperCase(), "Partition Check", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BTRC_MAC_SET).toUpperCase(), "BTRC MAC Set", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BTRC_MAC_GET).toUpperCase(), "BTRC MAC Get", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_RESET_PANEL_SEL).toUpperCase(), "PanelSel Reset", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WIFI_DISCONNECT).toUpperCase(), "WIFI Disconnect", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WOOFER_PLUGIN).toUpperCase(), "subwoofer detect", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_POWERSTANDBY).toUpperCase(), "set power standby output", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_HDMI_HPD_RESET).toUpperCase(), "reset HDMI Hpd (4030 chip)", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_UPDATE_I2C_PORT_TEST).toUpperCase(), "test I2C port used for update 6M60", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_CHECK_HDCPKEY14_VALID).toUpperCase(), "check HDCP key 1.4 valid or no", CMD_TYPE_COMMON}, 
		{Integer.toHexString(CMDID_CHECK_HDCPKEY22_VALID).toUpperCase(), "check HDCP key 2.2 valid or no", CMD_TYPE_COMMON}, 
		{Integer.toHexString(CMDID_SYSTEM_MASTER_CLEAR).toUpperCase(), "do system master clear directly", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SET_AUTORUN_STATUS).toUpperCase(), "set the flag to do auto-run", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_GET_AUTORUN_STATUS).toUpperCase(), "get the flag of auto-run", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_SET_AUTORUN_COMMAND).toUpperCase(), "set auto-run command", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_GET_AUTORUN_COMMAND).toUpperCase(), "set auto-run command", CMD_TYPE_COMMON},
		//M11
		{Integer.toHexString(CMDID_SET_FAN_STAT).toUpperCase(), "open/close fan and set speed", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_GET_CPU_TEMP).toUpperCase(), "get cpu temperature", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_DISPLAY_SWITCH).toUpperCase(), "switch display", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_ADB_TO_USB).toUpperCase(), "switch adb to usb", CMD_TYPE_COMMON},
		//RF TEST M11 - bt nonsignal test
		{Integer.toHexString(CMDID_BT_NONSIG_GETCHIPID).toUpperCase(), "get bt chip id", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_NONSIG_INIT).toUpperCase(), "init nonsignal bt test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_NONSIG_UNINIT).toUpperCase(), "exit bt nonsignal init", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_NONSIG_CHECKBLESUPPORT).toUpperCase(), "check ble is supported", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_NONSIG_CLOSENORMAL).toUpperCase(), "close bt normal mode for nonsignal", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_NONSIG_OPENNORMAL).toUpperCase(), "open bt normal mode for exit nonsig", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_NONSIG_HCICMDRUN).toUpperCase(), "run bt hci command", CMD_TYPE_COMMON},
		//RF TEST M11
		{Integer.toHexString(CMDID_ENTER_WIFI_RF).toUpperCase(), "enter WIFI Rf test mode", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_EXIT_WIFI_RF).toUpperCase(), "exit WIFI Rf test mode", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WIFI_RX_START).toUpperCase(), "start wifi rx test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WIFI_RX_STOP).toUpperCase(), "stop wifi rx test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WIFI_TX_START).toUpperCase(), "start wifi tx test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_WIFI_TX_STOP).toUpperCase(), "stop wifi tx test", CMD_TYPE_COMMON}, 
		{Integer.toHexString(CMDID_WIFI_FREQ_OFFSET_WRITE).toUpperCase(), "write wifi freq offset", CMD_TYPE_COMMON}, 
		{Integer.toHexString(CMDID_WIFI_FREQ_OFFSET_READ).toUpperCase(), "raed wifi freq offset", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_TX_START).toUpperCase(), "start bt tx test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_TX_STOP).toUpperCase(), "stop bt tx test", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_RF_FINISH).toUpperCase(), "exit bt test mode", CMD_TYPE_COMMON},
		{Integer.toHexString(CMDID_BT_RF_RESET).toUpperCase(), "reset to bt test mode", CMD_TYPE_COMMON},
		/**************5.2  Segment***********************/
	};
	/* ------------------------------------ command table stop ---------------------------*/
	/* -------------------- command table operations managements start -------------------*/
	public String getCmdExplanationByID(String id){
		String ret = null;
		if(id == null) return ret;
		for(int i = 0; i < cmdDesc.length; i++){
			if(id.equals(cmdDesc[i][0])){
				ret = cmdDesc[i][1];
				break;
			}
		}
		return ret;
	}
	public String getCmdTypeByID(String id){
		String ret = null;
		if(id == null) return ret;
		for(int i = 0; i < cmdDesc.length; i++){
			if(id.equals(cmdDesc[i][0])){
				ret = cmdDesc[i][2];
				break;
			}
		}
		return ret;
	}
	public String[] getCmdDescByID(String id){
		String ret[] = null;
		if(id == null) return ret;
		for(int i = 0; i < cmdDesc.length; i++){
			if(id.equals(cmdDesc[i][0])){
				ret = cmdDesc[i];
				break;
			}
		}
		return ret;
	}
	public int getCmdIndexByID(String id){
		int ret = 0;
		if(id == null) return ret;
		for(int i = 0; i < cmdDesc.length; i++){
			if(id.equals(cmdDesc[i][0])){
				ret = i;
				break;
			}
		}
		return ret;
	}
	public String[] getCmdInfoByIndex(int index){
		String ret[] = null;
		if(index < 0 || index > cmdDesc.length) return ret;
		ret = cmdDesc[index];
		return ret;

	}
	public String getCmdExplanationByIndex(int index){
		String ret = null;
		if(index < 0 || index > cmdDesc.length) return ret;
		ret = cmdDesc[index][1];
		return ret;
	}

	public String getCmdTypeByIndex(int index){
		String ret = null;
		if(index < 0 || index > cmdDesc.length) return ret;
		ret = cmdDesc[index][2];
		return ret;
	}

	/* -------------------- command table operations managements stop ---------------*/
	/* -------------------- activity operations managements start -------------------*/
    /* start --- <command id,activity name> definition */
    public static final String APPLICATION_PACKAGENAME = "com.duokan.factorytest";
    public static final String ACTION_FILTER_PREFIX = "com.duokan.action.";
    //TODO, you should add your test item for UI interaction
    public static final String Cmd_Activity[][] = {
        {Integer.toHexString(CMDID_IR_START).toUpperCase(),"KeyTest"},
        {Integer.toHexString(CMDID_MEDIA_PLAY).toUpperCase(),"LocalMedia"},
        {Integer.toHexString(CMDID_AGING_ON).toUpperCase(),"BoxBurning"},
    };
    public static String getCmdNameForCmdid(String cmdid) {
        if (cmdid == null) return null;
        for (int i=0; i< Cmd_Activity.length; i++) {
            if (cmdid.equals(Cmd_Activity[i][0])) {
                return Cmd_Activity[i][1];
            }
        }
        return null;
    }

    public static ComponentName getComponentNameForCmd(String cmdid) {
        if (cmdid == null) return null;
        for (int i=0; i< Cmd_Activity.length; i++) {
            if (cmdid.equals(Cmd_Activity[i][0])) {
                return new ComponentName(APPLICATION_PACKAGENAME, APPLICATION_PACKAGENAME+"."+Cmd_Activity[i][1]);
            }
        }
        return null;
    }
    public static String getFilterActionForCmd(String cmdid) {
        if (cmdid == null) return null;
        for (int i=0; i< Cmd_Activity.length; i++) {
            if (cmdid.equals(Cmd_Activity[i][0])) {
                return ACTION_FILTER_PREFIX + Cmd_Activity[i][1];
            }
        }
        return null;
    }
	/* -------------------- activity operations managements stop -------------------*/
	/* -------------------- activity operations - window status start -------------------*/
	private boolean WindowCmdWorkingStatus = false;
	public void setWindCmdWorkInactive(){
		Log.i(TAG, "set Window idle");
		WindowCmdWorkingStatus = false;
	}
	public void setWindCmdWorkActive(){
		Log.i(TAG, "set Window active");
		WindowCmdWorkingStatus = true;
	}
	public boolean CheckWindWorkFlag(){
		Log.i(TAG, "WindowCmdWorkingStatus = " + WindowCmdWorkingStatus);
		return WindowCmdWorkingStatus;
	}
	/* -------------------- activity operations - window status start -------------------*/
}
