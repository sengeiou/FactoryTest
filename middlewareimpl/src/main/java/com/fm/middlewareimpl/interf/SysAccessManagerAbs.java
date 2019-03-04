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

    /**
     * 写入 DLP 光机相关指令，并返回执行结果
     * @param param 节点,指令.参数格式：X,Y,Z
     *              X=写入节点前置代号，
     *              Y=写入节点后置代号
     *              Z=写入节点数据
     * @return result 执行结果
     */
    public abstract boolean writeProjectorCMD(String param);

    /**
     * 读取 DLP 光机相关指令，并返回读取的数据
     * @param param 节点,指令。参数格式：X,Y
     *              X=要读取的节点前置代号，
     *              Y=要读取的节点后置代号
     * @return result 读取结果
     */
    public abstract String readProjectorCMD(String param);
    /**
     * 写入 DLP 光机相关指令，并返回执行结果
     * @param param 节点,指令。参数格式：X,Y,Z
     *              X=写入节点前置代号，
     *              Y=写入节点后置代号
     *              Z=写入节点数据
     * @return result 执行结果
     */
    public abstract boolean writeI2CCMD(String param);

    /**
     * 读取 DLP 光机相关指令，并返回读取的数据
     * @param param 节点,指令。参数格式：X,Y
     *              X=要读取的节点前置代号，
     *              Y=要读取的节点后置代号
     * @return result 读取结果
     */
    public abstract String readI2CCMD(String param);
}
