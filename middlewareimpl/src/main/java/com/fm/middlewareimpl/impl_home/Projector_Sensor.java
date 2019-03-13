package com.fm.middlewareimpl.impl_home;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.droidlogic.app.KeyManager;
import com.droidlogic.app.SystemControlManager;

public class Projector_Sensor implements SensorEventListener {
    private static final String TAG = "Projector_Sensor";
    private Map<String, ArrayList<Integer>> datas;
    private static boolean collecting = false;
    private static boolean finish = false;
    private boolean result;
    private KeyManager mKeyManager;
    private static final String G_SENSOR_KEY = "g_sensor_standard";
    public static final String G_SENSOR_X = "x";
    public static final String G_SENSOR_Y = "y";
    public static final String G_SENSOR_Z = "z";

    private SystemControlManager sysCtlManager;

    public Projector_Sensor(Context context) {
        Sensor gsensor;
        SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mKeyManager = new KeyManager(context);
        mKeyManager.aml_init_unifykeys();

        datas = new HashMap<>();
        datas.put(G_SENSOR_X, new ArrayList<Integer>());
        datas.put(G_SENSOR_Y, new ArrayList<Integer>());
        datas.put(G_SENSOR_Z, new ArrayList<Integer>());
        //List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        //sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        //Log.e(TAG, "in Projector_Sensor constructor Factory mode accelerometer sensors size is " + sensors.size());
        //for (Sensor sensor : sensors) {
        //    Log.i(TAG,"sensor name : " + sensor.getName());
        //}
        //for (int n = 0; n < sensors.size(); n++) {
        //    Sensor avail_sensor = sensors.get(n);
        //    Log.e(TAG, "Name:" + avail_sensor.getName() + " Vendor:" + avail_sensor.getVendor());
        //    if (avail_sensor.getName().equals("Xiaomi Projector Internal Accelerometer")) {
        //        gsensor = avail_sensor;
        //        Log.e(TAG, "Name:" + gsensor.getName() + " Vendor:" + gsensor.getVendor());
        //        mSensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_GAME);
        //        break;
        //    }
        //}

        sysCtlManager = new SystemControlManager(context);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged : sensor " + sensor.getName());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (collecting) {
            //datas.get(G_SENSOR_X).add(event.values[0]);
            //datas.get(G_SENSOR_Y).add(event.values[1]);
            //datas.get(G_SENSOR_Z).add(event.values[2]);
            //Log.d(TAG, "start listener onSensorChanged : event values : " + Arrays.toString(event.values));
        }
    }

    public void startCollect() {
        new Thread() {
            @Override
            public void run() {
                resetGSensorData();

                collecting = true;
                finish = false;
                result = false;
                String[] values;

                int count = 0;
                while (count <= 30) {
                    String v = sysCtlManager.readSysFs("/sys/devices/virtual/bst/ACC/value");
                    values = v.split(" ");
                    Log.d(TAG, "v " + v + " values : " + Arrays.toString(values));
                    datas.get(G_SENSOR_X).add(Integer.parseInt(values[0]));
                    datas.get(G_SENSOR_Y).add(Integer.parseInt(values[1]));
                    datas.get(G_SENSOR_Z).add(Integer.parseInt(values[2]));

                    count++;
                    SystemClock.sleep(100);
                }

                collecting = false;
                ArrayList<Integer> xValues = datas.get(G_SENSOR_X);
                ArrayList<Integer> yValues = datas.get(G_SENSOR_Y);
                ArrayList<Integer> zValues = datas.get(G_SENSOR_Z);
                Log.d(TAG, "xValues" + xValues.size() + " yValues" + yValues.size() + " zValues" + zValues.size());

                int xMax = Collections.max(xValues);
                int yMax = Collections.max(yValues);
                int zMax = Collections.max(zValues);
                Log.d(TAG, "xMax" + xMax + " yMax" + yMax + " zMax" + zMax);

                int xMin = Collections.min(xValues);
                int yMin = Collections.min(yValues);
                int zMin = Collections.min(zValues);
                Log.d(TAG, "xMin" + xMin + " yMin" + yMin + " zMin" + zMin);

                result = isMoved(xValues.get(0), xMax, 10) || isMoved(yValues.get(0), yMax, 10) || isMoved(zValues.get(0), zMax, 10);

                finish = true;
            }
        }.start();
    }

    public boolean isCompleted() {
        return !collecting & finish;
    }

    public boolean getSensorResult() {
        //if (result == null) {
        //    result = new float[]{0, 0, 0, 0, 0, 0};
        //}
        finish = false;
        return result;
    }

    /**
     * 水平位置时保存G Sensor X、Y、Z标准数值(共6个数值，包含xMax,yMax,zMax,xMin,yMin,zMin)
     *
     * @return success
     */
    public boolean saveStandardData() {
        boolean echoFine = false;
        echoFine = sysCtlManager.writeSysFs("/sys/devices/virtual/bst/ACC/fast_calibration_x", "0");
        if (!echoFine) {
            Log.d(TAG, "echo 0 > /sys/devices/virtual/bst/ACC/fast_calibration_x failed");
            return false;
        }
        echoFine = sysCtlManager.writeSysFs("/sys/devices/virtual/bst/ACC/fast_calibration_y", "0");
        if (!echoFine) {
            Log.d(TAG, "echo 0 > /sys/devices/virtual/bst/ACC/fast_calibration_y failed");
            return false;
        }
        echoFine = sysCtlManager.writeSysFs("/sys/devices/virtual/bst/ACC/fast_calibration_z", "2");
        if (!echoFine) {
            Log.d(TAG, "echo 2 > /sys/devices/virtual/bst/ACC/fast_calibration_z failed");
            return false;
        }

        StringBuilder sb = new StringBuilder();
        String offset = sysCtlManager.readSysFs("/sys/devices/virtual/bst/ACC/offset_x");
        sb.append(offset).append(",");
        offset = sysCtlManager.readSysFs("/sys/devices/virtual/bst/ACC/offset_y");
        sb.append(offset).append(",");
        offset = sysCtlManager.readSysFs("/sys/devices/virtual/bst/ACC/offset_z");
        sb.append(offset);

        String standard = sb.toString();
        mKeyManager.aml_key_write(G_SENSOR_KEY, standard, 0x0);
        Log.d(TAG, "read offset (x,y,z)" + standard);

        return true;
    }

    public String readGsensorData() {
        String sensorData = null;
        sensorData = mKeyManager.aml_key_read(G_SENSOR_KEY, 0x0);
        Log.d(TAG, "readGsensorData  sensorData : " + sensorData);
        return sensorData == null ? "null" : sensorData;
    }

    public String readHorizontal() {
        String value = null;
        value = sysCtlManager.readSysFs("/sys/devices/virtual/bst/ACC/value").replace(" ", ",");
        return value;
    }

    private void resetGSensorData() {
        datas.get(G_SENSOR_X).clear();
        datas.get(G_SENSOR_Y).clear();
        datas.get(G_SENSOR_Z).clear();
        result = false;
    }

    private boolean isMoved(int preValue, int aftValue, int threshold) {
        int diff;
        if (preValue > 0) {
            if (aftValue > 0) {
                diff = aftValue - preValue;
            } else {
                diff = preValue - aftValue;
            }
        } else {
            if (aftValue > 0) {
                diff = aftValue - preValue;
            } else {
                diff = Math.abs(aftValue - preValue);
            }
        }
        return diff >= threshold;
    }
}