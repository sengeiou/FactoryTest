package com.fm.factorytest.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class CameraView extends RelativeLayout implements SurfaceHolder.Callback {

    private static String TAG = "CameraView";
    private TextView cameraText;

    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;

    /*Camera2 api*/
    // private CameraManager cameraManager;
    // private CameraCaptureSession mSession;
    // private CaptureRequest mRequest;
    /*Camera2 api*/

    /*Camera api*/
    private Camera mCamera;
    /*Camera api*/

    private Handler cameraHandler = new Handler();

    private Timer timer = null;


    public CameraView(Context context) {
        super(context);
        Log.d(TAG, "constructor param 1");
        initCameraView();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "constructor param 2");
        initCameraView();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(TAG, "constructor param 3");
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "CameraView surfaceCreated");
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        timerOff();
    }

    private void timerOn() {
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                releaseCamera();

                cameraHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateCameraView();
                    }
                });
            }
        }, 10 * 1000, 60 * 1000);
    }

    private void timerOff() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 初始化view
     */
    private void initCameraView() {
        if (Build.DEVICE.equals("conan")) {
            Log.d(TAG, "Factory Build.DEVICE is " + Build.DEVICE + ",now show camera aging view !!!");
            setBackgroundColor(Color.RED);
            timerOn();
        } else {
            Log.d(TAG, "Factory Build.DEVICE is " + Build.DEVICE + ",now skip show camera aging view");
        }
    }

    /**
     * UI thread
     */
    public void updateCameraView() {
        UsbManager usbManager = (UsbManager) getContext().getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> list = usbManager.getDeviceList();
        boolean usbDetected = false;
        for (String key : list.keySet()) {
            UsbDevice usb = list.get(key);
            Log.d(TAG, "detected usb device: name ==>" + usb.getDeviceName()
                    + ", manufacture name is==>" + usb.getManufacturerName());
            String usbMNname = usb.getManufacturerName();
            if (usbMNname.length() > 6 && !usbDetected) {
                usbMNname = usbMNname.substring(0, 6);
                usbDetected = TextUtils.isDigitsOnly(usbMNname);
            }

        }
        //reset
        removeAllViews();

        LayoutParams params;
        mCamera = getCamera();

        if (mCamera == null) {
            cameraText = new TextView(getContext());
            if (!usbDetected) {
                cameraText.setText("未识别到Camera硬件连接");
            } else {
                cameraText.setText("已识别到Camera硬件连接，打开时遇到问题");
            }

            cameraText.setBackgroundColor(Color.WHITE);
            //todo 改为XML属性获取
            cameraText.setTextSize(40f);
            cameraText.setTextColor(Color.RED);
            cameraText.setGravity(Gravity.CENTER);
            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(15, 15, 15, 15);
            addView(cameraText, params);

            Log.d(TAG, " Factory Camera is null,now cancel camera timer");
            timerOff();
        } else {
            surfaceView = new SurfaceView(getContext());
            surfaceView.setZOrderOnTop(true);

            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(15, 15, 15, 15);
            addView(surfaceView, params);

            mHolder = surfaceView.getHolder();
            mHolder.addCallback(this);
            mHolder.setFormat(PixelFormat.TRANSPARENT);
        }

        postInvalidate();
    }

    private Camera getCamera() {
        int cameraNum = Camera.getNumberOfCameras();
        Log.d(TAG, "Factory Camera Test getNumberOfCameras is " + cameraNum);
        if (cameraNum > 0) {
            return Camera.open();
        }
        return null;
    }

    private void startPreview() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

}