package com.fm.factorytest.utils;

import android.os.IBinder;
import android.os.RemoteException;

import mitv.internal.TvUtils;
import mitv.projector.IProjectorService;

public final class ProjectorUtil {
    private static IProjectorService projectorService;
    private static void init(){
        if (projectorService == null){
            IBinder projectorBinder = TvUtils.getAccessoryService(TvUtils.PROJECTOR_SERVICE_NAME);
            projectorService = IProjectorService.Stub.asInterface(projectorBinder);
        }
    }

    public static String readDLPVersion(){
        init();
        String version = "read error";
        if (projectorService != null){
            try {
                version = projectorService.GetProjectorInfo().GetFlashBuildVersion();
                String[] vers = version.split(",");
                String[] vs = new String[3];
                for (String ver : vers) {
                    if (ver.contains("major: ")){
                        vs[0] = (ver.replace("major: ","").trim());
                    }
                    if (ver.contains("minor: ")){
                        vs[1] = (ver.replace("minor: ","").trim());
                    }
                    if (ver.contains("patch: ")){
                        vs[2] = (ver.replace("patch: ","").trim());
                    }
                }
                version =vs[0]+"."+vs[1]+"."+vs[2];
            } catch (RemoteException e) {
                e.printStackTrace();
                version = "read error";
            }

        }

        return version;
    }

    /**
     * 设置投射方向
     * @param mode 投射方向，取值范围为: 1为桌面正投，2为桌面背投，3为吊顶正投，4为吊顶背投
     */
    public static void setDisplayOrientation(int mode){
        init();
        if (projectorService != null){
            try {
                projectorService.SetDisplayImageOrientation(mode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
