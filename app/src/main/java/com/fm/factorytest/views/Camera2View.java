package com.fm.factorytest.views;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fm.middlewareimpl.impl_home.UtilManagerImpl;
import com.fm.middlewareimpl.interf.UtilManagerAbs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Camera2View extends LinearLayout implements SurfaceHolder.Callback {
    private static String TAG = "CameraView";

    private TextView cameraText;
    private SurfaceView surfaceView;

    private CameraCaptureSession mSession;
    private CaptureRequest mRequest;
    private SurfaceHolder holder;
    private CameraManager cameraManager = null;
    private CameraDevice mCamera;
    private CameraStateCallback stateCallback;

    private Handler cameraHandler = new Handler();
    private Timer timer = null;
    private UtilManagerAbs mUtilImpl;

    private static volatile boolean usbRework = false;

    public Camera2View(Context context) {
        super(context);
        Log.d(TAG, "constructor param 1");
        initCameraView();
    }

    public Camera2View(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "constructor param 2");
        initCameraView();
    }

    public Camera2View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(TAG, "constructor param 3");
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "CameraView surfaceCreated");
        timerOn();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        timerOff();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void timerOn() {
        cameraHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                closeCamera();

                openCamera();
            }
        }, 2000);
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    cameraHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (usbDetected()) {
                                showTextInfo("检测到 Camera 连接");
                                if (usbRework) {
                                    closeCamera();
                                    openCamera();
                                    usbRework = false;
                                }
                            } else {
                                showTextInfo("未检测到 Camera 硬件连接");
                                cameraReTect();
                            }
                        }
                    });
                }
            }, 2000, 10 * 1000);
        }
    }

    private void timerOff() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void openCamera() {
        startOpenCameraListener();
    }

    public void closeCamera() {
        sessionClose();
        mCamera = null;
    }

    /**
     * 读取当前 Camera2 集合
     */
    private String[] getCameraIdList() {
        String[] cameras = null;
        try {
            cameras = cameraManager.getCameraIdList();
            Log.d(TAG, "getCameraIdList id length: " + cameras.length);
            for (String camera : cameras) {
                Log.d(TAG, "Factory getCameraIdList id : " + camera);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            cameraReTect();
            Log.d(TAG, "Factory getCameraIdList error : " + e.toString());
        }
        return cameras;
    }

    /**
     * 打开 Camera2
     */
    private void startOpenCameraListener() {
        String[] ids = getCameraIdList();

        if (ids.length == 0) {
            Log.d(TAG, "get Factory Camera Number is 0");
            showTextInfo("可用相机数为0");
        } else {
            Log.d(TAG, "get Factory Camera" + Arrays.toString(ids));
            open(ids[0]);
        }
    }

    private void open(String id) {
        try {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (getContext() instanceof Activity) {
                    ((Activity) getContext()).requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                }
                Log.d(TAG, "Factory Camera no permission , request P");
                return;
            }
            if (usbDetected()) {
                cameraManager.openCamera(id, stateCallback, null);
            } else {
                showTextInfo("未检测到 Camera 设备");
            }

        } catch (Exception e) {
            e.printStackTrace();
            cameraReTect();
            Log.d(TAG, "openCamera error : " + e.toString());
        }
    }

    private void sessionClose() {
        //surface will keep last frame when session is closed
        if (mSession != null) {
            try {
                mSession.stopRepeating();
                mSession.close();
                mSession = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化view
     */
    private void initCameraView() {
        mUtilImpl = new UtilManagerImpl(getContext());
        setOrientation(VERTICAL);

        if (Build.DEVICE.equals("conan")) {
            Log.d(TAG, "Factory Build.DEVICE is " + Build.DEVICE + ",now show camera aging view !!!");
            stateCallback = new CameraStateCallback();
            if (cameraManager == null) {
                cameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
            }
            setBackgroundColor(Color.RED);
            if (surfaceView == null) {
                surfaceView = new SurfaceView(getContext());
                surfaceView.setZOrderOnTop(true);
                LayoutParams params;
                params = new LayoutParams(500, 400);
                params.setMargins(15, 15, 15, 15);
                addView(surfaceView, params);

                holder = surfaceView.getHolder();
                holder.addCallback(this);
                holder.setFormat(PixelFormat.TRANSPARENT);
            }
            if (cameraText == null) {
                cameraText = new TextView(getContext());
                cameraText.setBackgroundColor(Color.WHITE);
                //todo 改为XML属性获取
                cameraText.setTextSize(20f);
                cameraText.setTextColor(Color.RED);
                LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
                params.bottomMargin = 0;
                addView(cameraText, params);
            }
        } else {
            Log.d(TAG, "Factory Build.DEVICE is " + Build.DEVICE + ",now skip show camera aging view");
        }
    }

    private void showTextInfo(String info) {
        if (cameraText != null)
            cameraText.setText(info);
    }

    private boolean usbDetected() {
        UsbManager usbManager = (UsbManager) getContext().getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> list = usbManager.getDeviceList();
        boolean usbDetected = false;
        for (String key : list.keySet()) {
            UsbDevice usb = list.get(key);
            Log.d(TAG, "detected usb device: name ==>" + usb.getDeviceName()
                    + ", manufacture name is==>" + usb.getManufacturerName());
            String usbMNname = usb.getManufacturerName();
            if (usbMNname != null) {
                if (usbMNname.length() > 6 && !usbDetected) {
                    usbMNname = usbMNname.substring(0, 6);
                    usbDetected = TextUtils.isDigitsOnly(usbMNname);
                }
            }
        }
        return usbDetected;
    }

    /**
     * 复位 Camera 将 IO 拉低后再拉高
     */
    public void cameraReTect() {
        mUtilImpl.setGpioOut("LGPIOH_2");
        SystemClock.sleep(1000 * 5);
        mUtilImpl.setGpioOut("HGPIOH_2");
        usbRework = true;
    }

    /**
     * open Camera callback
     */
    class CameraStateCallback extends CameraDevice.StateCallback {
        CaptureRequest.Builder requestBuild;

        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d(TAG, "Factory Camera onOpened");
            showTextInfo("相机打开");
            mCamera = camera;
            try {
                requestBuild = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                requestBuild.addTarget(holder.getSurface());
                camera.createCaptureSession(Arrays.asList(holder.getSurface()), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        mSession = session;
                        requestBuild.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        mRequest = requestBuild.build();
                        //发送请求
                        try {
                            mSession.setRepeatingRequest(mRequest, new CameraCaptureSession.CaptureCallback() {
                                @Override
                                public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
                                    super.onCaptureFailed(session, request, failure);
                                    showTextInfo("Camera 画面捕捉失败");
                                }
                            }, surfaceView.getHandler());
                        } catch (Exception e) {
                            mSession = null;
                            mCamera = null;
                            Log.e(TAG, e.toString());
                            cameraReTect();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        Log.d(TAG, "Factory camera onConfigureFailed");
                        session.close();
                        mCamera = null;
                        cameraReTect();
                    }
                }, null);
            } catch (Exception e) {
                mCamera = null;
                e.printStackTrace();
                cameraReTect();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d(TAG, "Factory camera onDisconnected");
            showTextInfo("Camera 断开连接");
            cameraReTect();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.d(TAG, "Factory camera open onError : " + error);
            showTextInfo("Camera 异常：" + error);
            cameraReTect();
        }
    }

}
