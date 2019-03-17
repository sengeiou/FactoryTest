package com.fm.factorytest.utils;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.fengmi.IMotorFocusCallback;
import com.fengmi.IMotorFocusService;
import com.fengmi.MotorStatus;

public final class MotorUtil{
	private static volatile IMotorFocusService motorService = null;
	private static final String TAG = "MotorUtil";

	public static IMotorFocusService getMotorService(){
        if (motorService == null){
            IBinder binder = ServiceManager.getService("MotorFocus");
            if(binder == null){
                Log.e(TAG,"get MotorFocus failed,please retry");
            }else{
                motorService = IMotorFocusService.Stub.asInterface(binder);
            }
        }
        return motorService;
    }

    private static volatile MyCallback callback;

    /** 马达正转 **/
    private static final int DIR_NORMAL = 0;
    /** 马达反转 **/
    private static final int DIR_REVERSE = 1;

    /** 马达转速微调 **/
    private static final int SPEED_LOW = 0;
    /** 马达转速正常 **/
    private static final int SPEED_NORMAL = 1;
    /** 马达转速快速 **/
    private static final int SPEED_FAST = 2;

    private static final int MOTOR_AUTO_FOCUS_DISABLE = 0;
    private static final int MOTOR_AUTO_FOCUS_ENABLE = 1;

    public static boolean setMotorScale(int scale){
    	boolean res = false;
    	if (getMotorService()!=null) {
    		try{
				if (scale > 0) {
    		    	motorService.setMotorConfig(DIR_NORMAL,SPEED_NORMAL,scale);
    			}else{
    				scale = 0 - scale;
					motorService.setMotorConfig(DIR_REVERSE,SPEED_NORMAL,scale);
    			}
    			motorService.setMotorStart();
    			res = true;
    		} catch (RemoteException e) {
            	e.printStackTrace();
            	Log.d(TAG," RemoteException :: setMotorStart :: " + e.getMessage());
            	res = false;
        	}
    	}

    	return res;
    }

	public static void setEventCallback(AFCallback afc){
        if (getMotorService() != null) {
            callback = new MyCallback(afc);
            try {
                int bc = motorService.setMotorEventCallback(callback);
                Log.d(TAG, "setMotorEventCallback :: " + bc);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.d(TAG, " RemoteException :: setMotorEventCallback :: " + e.getMessage());
            }
        }
    }

    public static void unsetEventCallback() {
        if (getMotorService() != null) {
            try {
                motorService.stopAutoFocus();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (callback != null) {
                try {
                    motorService.unsetMotorEventCallback(callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class MyCallback extends IMotorFocusCallback.Stub {
		AFCallback afc = null;
		public MyCallback(AFCallback afc){
			this.afc = afc;
		}
        @Override
        public int notify(MotorStatus event) throws RemoteException {
            Log.d(TAG,"IMotorFocusCallback :: " + event.toString());
            if (afc != null && MotorStatus.AUTO_FOCUS_START==event.getMotorEventType()) {
            	afc.onAFStart();
            }
            if (afc != null && MotorStatus.AUTO_FOCUS_FINISH==event.getMotorEventType()) {
            	afc.onAFFinish();
            }
            return 0;
        }
    }

    public interface AFCallback{
    	void onAFStart();
        void onAFFinish();
    }
}