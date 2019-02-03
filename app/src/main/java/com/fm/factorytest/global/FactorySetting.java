package com.fm.factorytest.global;


public class FactorySetting {
    /* start --- command service launch activity intent parameters definitions */
    // type String, content: the command id from pc
    public static final String EXTRA_CMDID = "commandid";
    // type String, content: the command para from pc, some bytes
    public static final String EXTRA_CMDPARA = "commandpara";
    // type String, content: the command para from pc, some bytes
    public static final String EXTRA_CMDPARAMDBG = "cmdparamdbg";
    /* end --- command service launch activity intent parameters definitions */


    /* start --- command service broadcast command to activity parameters definitions */
    public static final String EXTRA_BROADCAST_CMDPARA = "commandparas";
    // type int, content: the command type from command service
    public static final String EXTRA_BROADCAST_CONTROLTYPE = "controltype";
    // type String, content: the command id from command service
    public static final String EXTRA_BROADCAST_CONTROLID = "controlid";
    // type String, content: the command para from command service
    public static final String EXTRA_BROADCAST_CONTROLPARA = "controlparas";
    /* end --- command service broadcast command to activity parameters definitions */

    /* start --- command id definition which command service send broadcast to activity */
    // business command, check the parameters
    public static final int COMMAND_TASK_BUSINESS = 1;
    // pause the task running
    public static final int COMMAND_TASK_PAUSE = 2;
    // resume the task running
    public static final int COMMAND_TASK_RESUME = 3;
    // finish the task running
    public static final int COMMAND_TASK_STOP = 4;
    /*
    ** override the command id temporarily
    * usage case: one activity run command 1, the follow command 2 need the activity run some tasks
    * you must use below commands in pairs and order
    */
    public static final int COMMAND_SET_OVERRIDE_CMDID = 5;
    public static final int COMMAND_UNSET_OVERRIDE_CMDID = 6;
    /* end  --- command id definition which command service send broadcast to activity */

	//Product type Prefix
	public static final String COMMAND_PRODUCT_TYPE_TV = "1";
	public static final String COMMAND_PRODUCT_TYPE_BOX = "2";
	public static final String NAME_PRODUCT_TYPE_TV = "TV";
	public static final String NAME_PRODUCT_TYPE_BOX = "BOX";
	public static final String NAME_PRODUCT_TYPE_PROJECTOR = "Proj";
    //TODO, you should add your command dependency
    //{"cmdA","cmdB"} mean cmdA depend cmdB
    //here we think one command just depend on one command because only one foreground activity
    public static final String Cmd_Dependency[][] = {
        {"011a","0113"},
        {"0105","0104"},
		{"0125","0124"},
		{"0201","0104"},
		{"0203","0104"},
		{"0223","0222"},//aging
		{"0224","0222"},
		{"043a","0222"},
    };
    /* end --- <command id,activity name> definition */

    public static String getDependantCmdId(String cmdid) {
        if (cmdid == null) return null;
        for (int i=0; i< Cmd_Dependency.length; i++) {
            if (cmdid.equals(Cmd_Dependency[i][0])) {
                return Cmd_Dependency[i][1];
            }
        }
        return null;
    }
}
