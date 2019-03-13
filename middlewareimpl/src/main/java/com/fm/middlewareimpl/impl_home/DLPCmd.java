package com.fm.middlewareimpl.impl_home;


import android.os.SystemProperties;
import android.util.Log;

/**
 * DLP 光机命令
 *
 * @author lijie
 * @create 2018-08-12 20:53
 **/
public abstract class DLPCmd {
    protected static final String IMG_CMD_FILE_DEFAULT = "/sys/class/i2c3/slave";

    protected static final String TAG = "DLPCmd";

    private static final String TYPE_CONAN = "conan";
    private static final String TYPE_BATMAN = "batman";
    private static final String TYPE_BATMAN_HW_10 = "fm10_0";

    /**
     * get string [] of DLP screen check cmd
     *
     * @return String[]
     */
    public abstract String[] getImageModes();

    /**
     * get DLP Screen Check Start or Close Cmd
     *
     * @param open boolean
     * @return String
     */
    public abstract String getScreenCheckInitCmd(boolean open);

    /**
     * get XPR check start or close Cmd
     *
     * @param open boolean
     * @return String
     */
    public abstract String getXPRCheckInitCmd(boolean open);
    /**
     * get XPR shake on or off Cmd
     *
     * @param on boolean
     * @return String
     */
    public abstract String getXPRShakeCmd(boolean on);

    /**
     * get image command path
     * @return string
     */
    public abstract String getImageCMDPath();

    /**
     * init DLPCmd by Build.DEVICE
     *
     * @param type Build.DEVICE
     * @return instance of DLPCmd
     */
    public static DLPCmd initDLPCmd(String type) {
        DLPCmd cmd;
        switch (type) {
            case TYPE_CONAN:
                cmd = new ConanDLPCmd();
                break;
            case TYPE_BATMAN:
                Log.d(TAG,"Batman DLP CMD init ! HW version is " + getHardwareVersion());
                if (TYPE_BATMAN_HW_10.equals(getHardwareVersion())){
                    cmd = new Batman_10_DLPCMD();
                }else {
                    cmd = new BatmanDLPCmd();
                }
                break;
            default:
                cmd = new DefaultDLPCmd();
                break;
        }
        return cmd;
    }
    private static String getHardwareVersion(){
        return SystemProperties.get("ro.boot.hardware_version","");
    }

}
class Batman_10_DLPCMD extends DLPCmd{
    private static final String BATMAN_10_IMG_CMD_FILE = "/sys/class/projector/laser-projector/serial_write";

    private static final String CMD_COLOR_INIT = "8 02 14 9b 64 00 00 0d 0a";
    private static final String CMD_COLOR_EXIT = "8 02 14 9c 63 00 09 0d 0a";

    private static final String CMD_COLOR_PREFIX = "8 02 14 9c 63 00 ";
    private static final String CMD_COLOR_SUFFIX = " 0d 0a";

    private static final String CMD_COLOR_RED = CMD_COLOR_PREFIX + "01" + CMD_COLOR_SUFFIX;
    private static final String CMD_COLOR_GREEN =  CMD_COLOR_PREFIX + "02" + CMD_COLOR_SUFFIX;
    private static final String CMD_COLOR_BLUE =  CMD_COLOR_PREFIX + "03" + CMD_COLOR_SUFFIX;
    private static final String CMD_COLOR_WHITE =  CMD_COLOR_PREFIX + "07" + CMD_COLOR_SUFFIX;
    private static final String CMD_COLOR_BLACK =  CMD_COLOR_PREFIX + "00" + CMD_COLOR_SUFFIX;
    private static final String CMD_COLOR_GREY =  CMD_COLOR_PREFIX + "08" + CMD_COLOR_SUFFIX;
    private static final String CMD_COLOR_CYAN =  CMD_COLOR_PREFIX + "04" + CMD_COLOR_SUFFIX;
    private static final String CMD_COLOR_MAGENTA =  CMD_COLOR_PREFIX + "05" + CMD_COLOR_SUFFIX;
    private static final String CMD_COLOR_YELLOW =  CMD_COLOR_PREFIX + "06" + CMD_COLOR_SUFFIX;


    @Override
    public String[] getImageModes() {
        return new String[]{
                CMD_COLOR_RED,CMD_COLOR_GREEN,CMD_COLOR_BLUE,
                CMD_COLOR_WHITE,CMD_COLOR_BLACK,CMD_COLOR_GREY,
                CMD_COLOR_CYAN,CMD_COLOR_MAGENTA,CMD_COLOR_YELLOW
        };
    }

    @Override
    public String getScreenCheckInitCmd(boolean open) {
        if (open){
            return CMD_COLOR_INIT;
        }
        return CMD_COLOR_EXIT;

    }

    @Override
    public String getXPRCheckInitCmd(boolean open) {
        Log.d(TAG,"sorry no support for XPR");
        return "";
    }

    @Override
    public String getXPRShakeCmd(boolean on) {
        Log.d(TAG,"sorry no support for XPR");
        return "";
    }

    @Override
    public String getImageCMDPath() {
        return BATMAN_10_IMG_CMD_FILE;
    }
}
/**
 * DLP cmd for batman
 */
class BatmanDLPCmd extends DLPCmd {
    private static final String CMD_COLOR_INIT = "1a 3 0 f0 04 00";
    private static final String CMD_COLOR_EXIT = "1a 3 0 f0 01 00";

    private static final String CMD_XPR_INIT = "1a 3 0 f0 07 01";
    private static final String CMD_XPR_EXIT = "1a 3 0 f0 07 00";

    private static final String CMD_XPR_SHAKE_ON = "1a 7 0 DA 00 00 01 00 00 00";
    private static final String CMD_XPR_SHAKE_OFF = "1a 7 0 DA 00 00 00 00 00 00";

    private static final String CMD_COLOR_RED = "1a 3 0 f0 23 00";
    private static final String CMD_COLOR_GREEN = "1a 3 0 f0 23 01";
    private static final String CMD_COLOR_BLUE = "1a 3 0 f0 23 02";
    private static final String CMD_COLOR_BLACK = "1a 3 0 f0 23 03";
    private static final String CMD_COLOR_WHITE = "1a 3 0 f0 23 04";
    private static final String CMD_COLOR_GRAY_HORIZONTAL = "1a 3 0 f0 23 05";
    private static final String CMD_COLOR_GRAY_VERTICAL = "1a 3 0 f0 23 06";
    // 棋盘
    private static final String CMD_GRADATION_GRID = "1a 3 0 f0 23 07";
    // 网格
    private static final String CMD_GRADATION_RESEAU = "1a 3 0 f0 23 08";
    private static final String CMD_GRADATION_DIAMOND = "1a 3 0 f0 23 09";
    private static final String CMD_GRADATION_LINE_HORIZONTAL = "1a 3 0 f0 23 0a";
    private static final String CMD_GRADATION_LINE_VERTICAL = "1a 3 0 f0 23 0b";

    @Override
    public String[] getImageModes() {
        return new String[]{CMD_COLOR_RED,CMD_COLOR_GREEN,
                CMD_COLOR_BLUE,CMD_COLOR_BLACK,CMD_COLOR_WHITE,
                CMD_COLOR_GRAY_HORIZONTAL,CMD_COLOR_GRAY_VERTICAL,
                CMD_GRADATION_GRID,CMD_GRADATION_RESEAU,CMD_GRADATION_DIAMOND,
                CMD_GRADATION_LINE_HORIZONTAL,CMD_GRADATION_LINE_VERTICAL
        };
    }

    @Override
    public String getScreenCheckInitCmd(boolean open) {
        if (open) {
            return CMD_COLOR_INIT;
        }
        return CMD_COLOR_EXIT;
    }

    @Override
    public String getXPRCheckInitCmd(boolean open) {
        if (open) {
            return CMD_XPR_INIT;
        }
        return CMD_XPR_EXIT;
    }

    @Override
    public String getXPRShakeCmd(boolean on) {
        if (on) {
            return CMD_XPR_SHAKE_ON;
        }else{
            return CMD_XPR_SHAKE_OFF;
        }
    }

    @Override
    public String getImageCMDPath() {
        return IMG_CMD_FILE_DEFAULT;
    }
}

/**
 * DLP cmd for conan
 */
class ConanDLPCmd extends DLPCmd {
    private static final String CMD_COLOR_INIT_CONAN = "1b 2 0 62 00";
    private static final String CMD_COLOR_EXIT_CONAN = "1b 2 0 62 02";

    private static final String CMD_COLOR_WHITE_CONAN = "1b 3 0 67 f0 00";
    private static final String CMD_COLOR_GREEN_CONAN = "1b 3 0 67 c0 00";
    private static final String CMD_COLOR_RED_CONAN = "1b 3 0 67 a0 00";
    private static final String CMD_COLOR_BLUE_CONAN = "1b 3 0 67 90 00";
    private static final String CMD_COLOR_BLACK_CONAN = "1b 3 0 67 00 00";
    private static final String CMD_COLOR_GRAY_HORIZONTAL = "1b 3 0 67 02 ff";
    private static final String CMD_COLOR_GRAY_VERTICAL = "1b 3 0 67 03 ff";
    private static final String CMD_GRADATION_RESEAU = "1b 3 0 67 81 00";
    private static final String CMD_XPR_PIC_CONAN = "1b 3 0 67 08 00";

    private static final String CMD_XPR_INIT = "1b 2 0 fe 01";
    private static final String CMD_XPR_EXIT = "1b 2 0 fe 00";

    // echo "1b 1 1 73" > /sys/class/i2c3/slave    //读gain
    // echo "1b 2 0 72 0" > /sys/class/i2c3/slave   //关xpr
    // echo "1b 2 0 72 2d" > /sys/class/i2c3/slave   //开xpr

    private static final String CMD_XPR_SHAKE_ON = "1b 2 0 7e 00";
    private static final String CMD_XPR_SHAKE_OFF = "1b 2 0 7e 01";

    @Override
    public String[] getImageModes() {
        return new String[]{CMD_COLOR_WHITE_CONAN,
                CMD_COLOR_GREEN_CONAN, CMD_COLOR_RED_CONAN, CMD_COLOR_BLUE_CONAN,
                CMD_GRADATION_RESEAU,CMD_XPR_PIC_CONAN,CMD_COLOR_BLACK_CONAN,
                CMD_COLOR_GRAY_HORIZONTAL,CMD_COLOR_GRAY_VERTICAL
        };
    }

    @Override
    public String getScreenCheckInitCmd(boolean open) {
        if (open) {
            return CMD_COLOR_INIT_CONAN;
        }
        return CMD_COLOR_EXIT_CONAN;
    }

    @Override
    public String getXPRCheckInitCmd(boolean open) {
        if (open) {
            return CMD_XPR_INIT;
        }
        return CMD_XPR_EXIT;
    }

    @Override
    public String getXPRShakeCmd(boolean on) {
        if (on) {
            return CMD_XPR_SHAKE_ON;
        }else{
            return CMD_XPR_SHAKE_OFF;
        }
    }

    @Override
    public String getImageCMDPath() {
        return IMG_CMD_FILE_DEFAULT;
    }
}

/**
 * Default DLP Cmd for rainman
 */
class DefaultDLPCmd extends DLPCmd {
    private static final String CMD_COLOR_INIT = "1b 2 0 05 01";
    private static final String CMD_COLOR_EXIT = "1b 2 0 05 00";

    private static final String CMD_COLOR_WHITE = "1b 3 0 0b 00 70";
    private static final String CMD_COLOR_BLACK = "1b 3 0 0b 00 00";
    private static final String CMD_COLOR_GREEN = "1b 3 0 0b 00 20";
    private static final String CMD_COLOR_RED = "1b 3 0 0b 00 10";
    private static final String CMD_COLOR_BLUE = "1b 3 0 0b 00 30";
    private static final String CMD_COLOR_YELLOW = "1b 3 0 0b 00 60";
    private static final String CMD_COLOR_SOLFERINO = "1b 3 0 0b 00 50";// 品红
    private static final String CMD_COLOR_CYAN = "1b 3 0 0b 00 40";

    private static final String CMD_GRADATION_WHITE = "1b 5 0 0b 01 70 00 ff";
    private static final String CMD_GRADATION_RED = "1b 5 0 0b 01 10 00 ff";
    private static final String CMD_GRADATION_BLUE = "1b 5 0 0b 01 30 00 ff";
    private static final String CMD_GRADATION_GREEN = "1b 5 0 0b 01 20 00 ff";

    private static final String CMD_COLOR_GRAY10 = "1b 5 0 0b 01 70 0A 0A";
    private static final String CMD_COLOR_GRAY20 = "1b 5 0 0b 01 70 14 14";
    // 棋盘
    private static final String CMD_GRADATION_GRID = "1b 7 0 0b 07 70 04 00 04 00";
    // 网格
    private static final String CMD_GRADATION_RESEAU = "1b 7 0 0b 86 70 00 43 00 78";

    @Override
    public String[] getImageModes() {
        return new String[]{CMD_COLOR_WHITE,
                CMD_COLOR_BLACK, CMD_COLOR_GREEN, CMD_COLOR_RED, CMD_COLOR_BLUE, CMD_COLOR_YELLOW,
                CMD_COLOR_SOLFERINO, CMD_COLOR_CYAN, CMD_GRADATION_WHITE, CMD_GRADATION_RED,
                CMD_GRADATION_BLUE, CMD_GRADATION_GREEN, CMD_GRADATION_GRID, CMD_GRADATION_RESEAU,
                CMD_COLOR_GRAY10, CMD_COLOR_GRAY20
        };
    }

    @Override
    public String getScreenCheckInitCmd(boolean open) {
        if (open) {
            return CMD_COLOR_INIT;
        }
        return CMD_COLOR_EXIT;
    }

    @Override
    public String getXPRCheckInitCmd(boolean open) {
        return null;
    }

    @Override
    public String getXPRShakeCmd(boolean on) {
        return null;
    }

    @Override
    public String getImageCMDPath() {
        return IMG_CMD_FILE_DEFAULT;
    }
}

