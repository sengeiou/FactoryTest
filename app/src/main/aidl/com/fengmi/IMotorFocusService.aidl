package com.fengmi;
import com.fengmi.IMotorFocusCallback;

interface IMotorFocusService {
    int getMotorType();
    int setMotorConfig(int dir,int speed,int period);
    int setMotorStart();
    int setMotorStop();
    int startAutoFocus();
    int stopAutoFocus();
    int setMotorEventCallback(IMotorFocusCallback callback);
    int unsetMotorEventCallback(IMotorFocusCallback callback);
}