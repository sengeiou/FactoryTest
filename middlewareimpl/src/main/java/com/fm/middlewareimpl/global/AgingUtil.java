package com.fm.middlewareimpl.global;
import android.content.Context;
import android.content.SharedPreferences;

public final class AgingUtil {
    /* ---- 工厂老化相关参数配置 ---- */
    private static String SP_AGING = "sp_aging";
    private static String KEY_AGING_LINE = "key_aging_line";
    private static String KEY_AGING_VOLUME = "key_aging_volume";
    private static int AGING_LINE_DEFAULT = 4 * 60 * 60 / 3 + 1;
    private static int AGING_VOLUME_DEFAULT = 50;
    /* ---- 工厂老化相关参数配置 ---- */

    /**
     * 设置老化时间标准，当达到此标准时，老化音量会降到最低
     *
     * @param context   context
     * @param agingLine 老化标准时间 > 大于4小时(4*60*60/3),3秒一次
     * @return success
     */
    public static boolean setAgingLine(Context context, int agingLine) {
        SharedPreferences sp = context.getSharedPreferences(SP_AGING, Context.MODE_PRIVATE);
        if (agingLine < AGING_LINE_DEFAULT) {
            return false;
        }
        sp.edit().putInt(KEY_AGING_LINE, agingLine).apply();
        return true;
    }

    /**
     * 获取标准老化时间
     *
     * @param context context
     * @return 老化标准时间 > 4 小时
     */
    public static int getAgingLine(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_AGING, Context.MODE_PRIVATE);
        return sp.getInt(KEY_AGING_LINE, AGING_LINE_DEFAULT);
    }

    /**
     * 设置达到老化时间后的音量
     *
     * @param context context
     * @param vol     音量
     * @return success
     */
    public static boolean setAgingVolume(Context context, int vol) {
        SharedPreferences sp = context.getSharedPreferences(SP_AGING, Context.MODE_PRIVATE);
        if (vol < 0) {
            return false;
        }
        sp.edit().putInt(KEY_AGING_VOLUME, vol).apply();
        return true;
    }

    /**
     * 获取达到老化时间后的音量
     *
     * @param context context
     * @return volume
     */
    public static int getAgingVolume(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_AGING, Context.MODE_PRIVATE);
        return sp.getInt(KEY_AGING_VOLUME, AGING_VOLUME_DEFAULT);
    }
}
