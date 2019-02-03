package com.fm.factorytest.helper;

import android.os.Build;
import android.os.SystemClock;
import android.os.SystemProperties;

import com.fm.factorytest.utils.ShellUtil;

public class TemperatureHelper {
    private static final String CONAN = "conan";
    private static final String FM10_HEAD = "fm10";

    private static String[] getTemperatureTitles() {
        String[] temperatureTitles = null;
        String hwVersion = SystemProperties.get("ro.boot.hardware_version", " ");
        if (Build.DEVICE.equals(CONAN)) {
            temperatureTitles = new String[]{"红光温度\t\t", "绿光温度\t\t", "环境温度\t\t"};
        } else {
            if (hwVersion.contains(FM10_HEAD)) {
                temperatureTitles = new String[]{"环境温度\t\t", "色轮温度\t\t", "光源温度\t\t"};
            } else {
                temperatureTitles = new String[]{"环境温度\t\t", "色轮温度\t\t", "激光1路温度\t\t", "激光2路温度\t\t"};
            }
        }

        return temperatureTitles;
    }

    public static TemperatureData queryTemperature() {
        String hwVersion = SystemProperties.get("ro.boot.hardware_version", " ");
        if (Build.DEVICE.equals(CONAN)) {
            return queryConanTemperature();
        } else {
            if (hwVersion.contains(FM10_HEAD)) {
                return queryFM10Temperature();
            } else {
                return queryFM15Temperature();
            }
        }
    }

    private static TemperatureData queryFM15Temperature() {
        TemperatureData data = new TemperatureData();
        data.time = SystemClock.elapsedRealtime();
        try {
            if (!ShellUtil.isI2CReady()) {
                return data;
            }
            String s = ShellUtil.execCommand("cat /sys/class/projector/laser-projector/projector-0-temp/temp");
            String s1 = s.replaceAll("\n", " ");
            data.envTemperature = getTemperatureTitles()[0] + s1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String s = ShellUtil.execCommand("cat /sys/class/projector/laser-projector/projector-1-temp/temp");
            String s1 = s.replaceAll("\n", " ");
            data.wheelTemperature = getTemperatureTitles()[1] + s1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String s = ShellUtil.execCommand("cat /sys/class/projector/laser-projector/projector-2-temp/temp");
            String s1 = s.replaceAll("\n", " ");
            data.channel1Temperature = getTemperatureTitles()[2] + s1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String s = ShellUtil.execCommand("cat /sys/class/projector/laser-projector/projector-3-temp/temp");
            String s1 = s.replaceAll("\n", " ");
            data.channel2Temperature = getTemperatureTitles()[3] + s1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private static TemperatureData queryConanTemperature() {
        TemperatureData data = new TemperatureData();
        data.time = SystemClock.elapsedRealtime();
        try {
            String s = ShellUtil.execCommand("cat /sys/class/projector/led-projector/projector-0-temp/temp");
            String s1 = s.replaceAll("\n", " ");
            data.channel1Temperature = getTemperatureTitles()[0] + s1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String s = ShellUtil.execCommand("cat /sys/class/projector/led-projector/projector-1-temp/temp");
            String s1 = s.replaceAll("\n", " ");
            data.channel2Temperature = getTemperatureTitles()[1] + s1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String s = ShellUtil.execCommand("cat /sys/class/projector/led-projector/projector-2-temp/temp");
            String s1 = s.replaceAll("\n", " ");
            data.envTemperature = getTemperatureTitles()[2] + s1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    private static TemperatureData queryFM10Temperature() {
        TemperatureData data = new TemperatureData();
        data.time = SystemClock.elapsedRealtime();
        try {
            if (!ShellUtil.isI2CReady()) {
                return data;
            }
            String s = ShellUtil.execCommand("cat /sys/class/projector/laser-projector/fm10_temp");
            String[] ss = s.replaceAll("\n", " ").split(" ");
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < ss.length; i++) {
                if (ss[i].trim().equals("")) {
                    continue;
                }
                temp.append(ss[i]).append(",");
            }
            ss = temp.toString().split(",");
            data.envTemperature = getTemperatureTitles()[0] + ss[0];
            data.wheelTemperature = getTemperatureTitles()[1] + ss[1];
            data.channel1Temperature = getTemperatureTitles()[2] + ss[2];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static class TemperatureData {
        public long time = -1;
        public String envTemperature = "";
        public String wheelTemperature = "";
        public String channel1Temperature = "";
        public String channel2Temperature = "";
    }
}
