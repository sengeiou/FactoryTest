package com.fm.factorytest.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fm.middlewareimpl.impl_home.UtilManagerImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Camera3View extends LinearLayout implements SurfaceHolder.Callback {
    private static String TAG = "CameraView";

    private TextView cameraText;
    private TextView afText;
    private TextView afCount;
    private SurfaceView surfaceView;
    private SurfaceHolder holder;

    /*Camera api*/
    private Camera mCamera;
    private UtilManagerImpl mUtilImpl;

    private Handler cameraHandler = new Handler();
    private Timer timer = null;

    private Context context;

    public Camera3View(Context context) {
        this(context, null);
    }

    public Camera3View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initCameraView();
    }

    public void updateAF(final String info) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (afText != null) {
                        afText.setText(info);
                    }
                }
            });
        }
    }

    public void updateCount(final String info) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (afCount != null) {
                        afCount.setText(info);
                    }
                }
            });
        }
    }

    /**
     * 初始化view
     */
    private void initCameraView() {
        mUtilImpl = new UtilManagerImpl(getContext());
        setOrientation(VERTICAL);
        setGravity(Gravity.BOTTOM);

        if (Build.DEVICE.equals("conan")) {
            Log.d(TAG, "Factory Build.DEVICE is " + Build.DEVICE + ",now show camera aging view !!!");
            setBackgroundColor(Color.RED);
            // if (surfaceView == null) {
            //     surfaceView = new SurfaceView(getContext());
            //     surfaceView.setZOrderOnTop(true);
            //     LayoutParams params;
            //     params = new LayoutParams(500, 400);
            //     params.setMargins(15, 15, 15, 15);
            //     addView(surfaceView, params);
            //
            //     holder = surfaceView.getHolder();
            //     holder.addCallback(this);
            //     holder.setFormat(PixelFormat.TRANSPARENT);
            // }
            if (cameraText == null) {
                cameraText = new TextView(getContext());
                cameraText.setBackgroundColor(Color.WHITE);
                //todo 改为XML属性获取
                cameraText.setTextSize(20f);
                cameraText.setTextColor(Color.RED);
                LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
                params.bottomMargin = 10;
                addView(cameraText, params);
            }
            if (afText == null) {
                afText = new TextView(getContext());
                afText.setBackgroundColor(Color.WHITE);
                //todo 改为XML属性获取
                afText.setTextSize(20f);
                afText.setTextColor(Color.RED);
                LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
                params.bottomMargin = 10;
                addView(afText, params);
            }
            if (afCount == null) {
                afCount = new TextView(getContext());
                afCount.setBackgroundColor(Color.WHITE);
                //todo 改为XML属性获取
                afCount.setTextSize(20f);
                afCount.setTextColor(Color.RED);
                LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
                params.bottomMargin = 10;
                addView(afCount, params);
            }
            timerOn();
        } else {
            Log.d(TAG, "Factory Build.DEVICE is " + Build.DEVICE + ",now skip show camera aging view");
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        timerOff();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //timerOn();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //timerOff();
    }

    private void startPreview() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                    }
                });
                mCamera.setErrorCallback(new Camera.ErrorCallback() {
                    @Override
                    public void onError(int error, Camera camera) {
                        if (error == 100) {
                            //camera server died
                            Log.e(TAG, "camera server died,we re tect camera ");
                            showTextInfo("相机 server 异常 ：" + error);
                            cameraReTect();
                        }
                        if (error == Camera.CAMERA_ERROR_UNKNOWN) {
                            showTextInfo("相机未知异常 ：" + error);
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void timerOn() {
        //showTextInfo("相机开启中，请稍候,首次启动需等待20s");
        //开启 Camera
        // cameraHandler.postDelayed(new Runnable() {
        //     @Override
        //     public void run() {
        //         mCamera = getCamera();
        //         if (mCamera != null) {
        //             startPreview();
        //             showTextInfo("打开 Camera");
        //         } else {
        //             showTextInfo("未找到可用 Camera");
        //         }
        //     }
        // }, 18 * 1000);
        //开启 usb 检测
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (usbDetected()) {
                        showTextInfo("检测到 Camera 连接");
                    } else {
                        showTextInfo("未检测到 Camera 硬件连接");
                        timer.cancel();
                        timer = null;
                    }
                }
            }, 2000, 10 * 1000);
        }
    }

    private void timerOff() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        // if (mCamera != null) {
        //     mCamera.setPreviewCallback(null);
        //     mCamera.stopPreview();
        //     mCamera.release();
        //     mCamera = null;
        // }
    }

    private void showTextInfo(final String info) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (cameraText != null)
                        cameraText.setText(info);
                }
            });
        }
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
                if (usbMNname.contains("Alcor")) {
                    usbDetected = true;
                }
                if (usbMNname.length() > 6 && !usbDetected) {
                    usbMNname = usbMNname.substring(0, 6);
                    usbDetected = TextUtils.isDigitsOnly(usbMNname);
                }
            }
        }
        return usbDetected;
    }

    private Camera getCamera() {
        int cameraNum = Camera.getNumberOfCameras();
        Log.d(TAG, "Factory Camera Test getNumberOfCameras is " + cameraNum);
        if (cameraNum > 0) {
            return Camera.open();
        }
        return null;
    }

    /**
     * 复位 Camera 将 IO 拉低后再拉高
     */
    private void cameraReTect() {
        mUtilImpl.setGpioOut("LGPIOH_2");
        SystemClock.sleep(1000 * 2);
        mUtilImpl.setGpioOut("HGPIOH_2");
    }
}
