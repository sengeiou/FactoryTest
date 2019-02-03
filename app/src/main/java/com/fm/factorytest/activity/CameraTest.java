package com.fm.factorytest.activity;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.fm.factorytest.R;
import com.fm.factorytest.base.BaseActivity;
import com.fm.factorytest.global.FactorySetting;
import com.fm.factorytest.global.TvCommandDescription;

import java.io.IOException;
import java.util.List;

/**
 * adb connect 172.15.60.155
 * camera test Activity
 *
 * @author lijie
 */
public class CameraTest extends BaseActivity implements SurfaceHolder.Callback{
    private static final String TAG = "CameraTest";
    private SurfaceView sv;
    private SurfaceHolder holder;
    private Camera mCamera;
    private TextView mTvCamera;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"Factory Camera Test onCreate");
        setContentView(R.layout.camera);
        sv = (SurfaceView)findViewById(R.id.sfv_camera);
        mTvCamera = (TextView) findViewById(R.id.tv_camera);
        mTvCamera.setVisibility(View.INVISIBLE);
        holder = sv.getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG,"Factory Camera Test surfaceCreated");
        mCamera = getCamera();
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG,"Factory Camera Test surfaceChanged");
        if (mCamera != null) {
            refreshCamera();
            Camera.Parameters params = mCamera.getParameters();
            Camera.Size optimalPreviewSize = getOptimalPreviewSize(mCamera.getParameters().getSupportedPreviewSizes(), width, height);
            params.setPictureSize(optimalPreviewSize.width, optimalPreviewSize.height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG,"Factory Camera Test surfaceDestroyed ");

        holder.removeCallback(this);
        releaseCamera();
    }
    public void handleCommand(String cmdid, String param) {
    }
    public void handleControlMsg(int cmdtype, String cmdid, String cmdpara) {
        if(FactorySetting.COMMAND_TASK_STOP == cmdtype){
            finish();
            setResult(cmdid, PASS, true);
        }else if(FactorySetting.COMMAND_TASK_BUSINESS == cmdtype){
            int cmd = Integer.parseInt(cmdid,16);
            if (TvCommandDescription.CMDID_CAMERA_TEST_OPEN == cmd) {
                refreshCamera();
                setResult(cmdid, PASS, false);
            }else if (TvCommandDescription.CMDID_CAMERA_TEST_CAPTURE==cmd) {
                releaseCamera();
                setResult(cmdid, PASS, false);
            }
        }
    }

    //获取最佳的分辨率
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.75;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private void refreshCamera() {
        if (holder.getSurface() == null) {
            //preview surface does not exist
            return;
        }
        mCamera.stopPreview();

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Camera getCamera() {
	int cameraNum = Camera.getNumberOfCameras();
	Log.d(TAG,"Factory Camera Test getNumberOfCameras is "+cameraNum);
	if (cameraNum>0) {
		return Camera.open();
	}
	mHandler.post(new Runnable() {
	@Override
	public void run() {
		Log.d(TAG,"Factory Camera Test show camera error info !");
		mTvCamera.setVisibility(View.VISIBLE);
	}
	});
	return null;
    }

    private void startPreview(){
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
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

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

}
