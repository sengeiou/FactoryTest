package com.fengmi;
import com.fengmi.MotorStatus;

interface IMotorFocusCallback {
    int notify(in MotorStatus event);
}
