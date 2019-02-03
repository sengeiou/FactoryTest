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
}
