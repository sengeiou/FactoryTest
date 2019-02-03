package com.fengmi;

import android.os.Parcel;
import android.os.Parcelable;

public final class MotorStatus implements Parcelable {
    private int type;
    private int status;

    public final static int MOTOR_STOP = 1;
    public final static int MOTOR_MIN = 5;
    public final static int MOTOR_MAX = 6;

    public final static int AUTO_FOCUS_START = 10;
    public final static int AUTO_FOCUS_FINISH = 11;

    private MotorStatus(Parcel in) {
        type = in.readInt();
        status = in.readInt();
    }

    public int getMotorEventType(){
        return type;
    }

    public int getMotorEventStatus(){
        return status;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(type);
        out.writeInt(status);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Type:" + type + ",");
        builder.append("Status:" + status);

        return builder.toString();
    }

    public static final Creator<MotorStatus> CREATOR = new Creator<MotorStatus>() {
            @Override
            public MotorStatus createFromParcel(Parcel source) {
                return new MotorStatus(source);
            }

            @Override
            public MotorStatus[] newArray(int size) {
                return new MotorStatus[size];
            }
        };

}
