package com.fm.middlewareimpl.global;
public final class SettingManager{
	/****************
	 * factory property
	 * ****************/
	//the flag for DUT first  bootup; value: boolean
	public static final String FACTPROP_FIRSTBOOT = "Factory.var.FirstBoot";
	//the times of factory apk running. in factory, it shows DUT bootup times; value: int
	public static final String FACTPROP_BOOTCOUNT = "Factory.var.BootCount";
	//show autorun status; value: boolean
	public static final String FACTPROP_AUTORUN_STATUS = "Factory.var.AutoRunStatus";
	//show autorun command; value: string 
	public static final String FACTPROP_AUTORUN_COMMAND = "Factory.var.AutoRunCommand";
	//show aging counterś value; value: int
	public static final String FACTPROP_AGINGTIMERCOUNT = "Factory.var.AgingTimerCount";
	//auto-run after reboot; value: string. "CMDID+para1+para2+...+paran"
	public static final String FACTPROP_AUTORUNCMD = "Factory.var.AutoRunCmd";
	/****************** key just for TV **************************/
	//the setting for backlight. value: int
	public static final String FACTPROP_BACKLIGHT = "Factory.var.Backlight";
	//the setting for brightness; value: int
	public static final String FACTPROP_BRIGHTNESS = "Factory.var.Brightness";
	//the setting for contrast; value: int
	public static final String FACTPROP_CONTRAST = "Factory.var.Contrast";
	//the setting for 3D mode; value: int
	public static final String FACTPROP_3D = "Factory.var.3D";
	//the selcted source for burning; value: int
	public static final String FACTPROP_BURNINGSOUR = "Factory.var.BurnSour";
	/****************** key just for application  team***********************/
	public static final String APPLPROP_RID = "Application.var.Rid";
	/****************** key just for common **********************/
	public static final String FACTPROP_BTRCMAC = "Factory.var.btrcmac";
	/****************** key just for BOX *************************/

	/******************* system property *************************/
	public static final String SYSPROP_PANEL_TYPE = "ro.boot.panel_type";
	public static final String SYSPROP_FACTORYMODE = "ro.product.factorymode";
	public static final String SYSPROP_BOOTSYSTEM = "bootenv.var.bootsystem";
	public static final String SYSPROP_PARTITIONCHECK = "bootenv.var.partitions_err";
	public static final String SYSPROP_FACTORYPOWERMODE = "bootenv.var.factory_power_mode";
	public static final String SYSPROP_PRODUCTREGION = "bootenv.var.product_region";
	public static final String SYSPROP_55TVPANELSELECT = "bootenv.var.db_table";
	public static final String SYSPROP_CONSOLEDISABLE = "bootenv.var.console_disable";
	public static final String SYSPROP_PRODUCTNAME = "ro.product.name";
	public static final String SYSPROP_PRODUCTMODEL = "ro.product.model";
	public static final String SYSPROP_WOOFERPLUGIN = "persist.sys.wooferplugin";
	public static final String SYSPROP_ARC = "persist.sys.arc.enable";
	/*******************system Intent define**********************/
	public static final String INTENT_MASTERCLEARSYS = "android.intent.action.MASTER_CLEAR";
	public static final String INTENT_REBOOTSYS = "android.intent.action.REBOOT";
	public SettingManager(){
	}
}
