package com.fm.middlewareimpl.interf;

import android.content.Context;


public abstract class SysAccessManagerAbs extends BaseMiddleware {
    public SysAccessManagerAbs(Context context) {
        super(context);
    }

    public abstract boolean enableScreenCheck(String param);

    public abstract boolean screenCheck(int mode);

    public abstract boolean syncDlpInfo();

    public abstract boolean saveDlpInfo();

    public abstract boolean setWheelDelay(int delay);

    public abstract int getWheelDelay();

    public abstract boolean enableXPRCheck(String param);

    public abstract boolean enableXPRShake(String param);

    /**
     * get gseneor values
     * return float[] : [xMax,yMax,zMax,xMin,Ymin,zMin]
     */
    public abstract boolean checkGSensorFunc();

    public abstract boolean startGSensorCollect();

    public abstract boolean saveGSensorStandard();

    public abstract String readGSensorStandard();

    public abstract String readGSensorHorizontalData();

    public abstract String readDLPVersion();

    /**
     * 开启关闭8点校正
     *
     * @param param 0 = on,1 = off
     * @return boolean
     */
    public abstract boolean setKeyStoneMode(String param);

    /**
     * 8点校正 调整
     * @param param point(1-8),direct(1-4),step(1,5)
     * @return
     */
    public abstract boolean setKeyStoneDirect(String param);
}
