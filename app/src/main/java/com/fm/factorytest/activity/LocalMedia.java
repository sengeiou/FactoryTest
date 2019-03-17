package com.fm.factorytest.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.fengmi.IMotorFocusService;
import com.fm.factorytest.R;
import com.fm.factorytest.base.BaseActivity;
import com.fm.factorytest.global.FactorySetting;
import com.fm.factorytest.utils.MotorUtil;
import com.fm.factorytest.utils.SPUtils;
import com.fm.factorytest.views.Camera3View;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class LocalMedia extends BaseActivity implements SurfaceHolder.Callback,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MotorUtil.AFCallback {
    private static final String TAG = "FactoryLocalMedia";
    private static final String[] MediaSour = {
            "/system/factory/autovideo_4k2k.mov",
            "/system/factory/pink_noise_0db.mov",
            "/system/factory/autovideo.mov",
            "/system/factory/autovideo_1080.mov",
    };
    private static final String DUCK = "duck";
    private static final String VIDEO4K = "4k2k";
    private static final String PINKNOISE = "pink";

    //private int source_item = 0;
    private static final String ICEHOCKEY = "ice";

    private static final String AF_COUNT = "af_count";
    private int MediaItem = 0;
    //private TextView mTextView;
    private SurfaceView mSurfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mMediaPlayer;

    private static int count = 0;
    private IMotorFocusService motorService;
    /*
     * Called when the activity is first created.
     */
    private Camera3View mCameraView;
    private Timer autoFocusTimer = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "LocalMedia Started");
    }

    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp KeyCode " + keyCode);
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown KeyCode " + keyCode);
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "on TouchEvent");
        return true;
    }

    private void localMediaPlay(int item) {

        mMediaPlayer.setDisplay(surfaceHolder);
        try {
            //mMediaPlayer.setDataSource("/data/factory/autovideo.mov");
            Log.i(TAG, "source is " + MediaSour[item]);
            mMediaPlayer.setDataSource(MediaSour[item]);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //mMediaPlayer.setLooping(true);
        //设置 prepared 监听
        mMediaPlayer.setOnPreparedListener(this);
        try {
            //异步加载资源，在监听器中 start()
            mMediaPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        Log.v(TAG, "surfaceChanged called");
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        Log.v(TAG, "surfaceDestroyed called");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(TAG, "surfaceCreated called");
        //localMediaPlay();
        localMediaPlay(MediaItem);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCompletion(MediaPlayer Player) {
        // TODO Auto-generated method stub
        setResult(NON_INNACTIVITYCMD, FAIL);
    }

    public void handleCommand(String cmdid, String param) {
        int i = 0;
        super.handleCommand(cmdid, param);
        //setContentView(mSurfaceView);
        Log.v(TAG, "command para: " + param);
        if (DUCK.equals(param)) {
            MediaItem = 0;
        } else if (PINKNOISE.equals(param)) {
            MediaItem = 1;
        } else if (VIDEO4K.equals(param)) {
            MediaItem = 2;
        } else if (ICEHOCKEY.equals(param)) {
            MediaItem = 3;
        } else {
            Log.v(TAG, "no para, set as default source ");
            MediaItem = 0;
        }
        setContentView(R.layout.localmedia);
        //mTextView = (TextView) findViewById(R.id.localmediaview);
        mSurfaceView = (SurfaceView) findViewById(R.id.localmediaview);
        mCameraView = (Camera3View) findViewById(R.id.cv_aging);

        if (Build.DEVICE.equals("conan")) {
            count = (int) SPUtils.getParam(this, AF_COUNT, 0);
            MotorUtil.setEventCallback(this);
            if (autoFocusTimer == null) {
                autoFocusTimer = new Timer();
                autoFocusTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        motorService = (IMotorFocusService) MotorUtil.getMotorService();
                        if (motorService != null) {
                            try {
                                count++;
                                motorService.startAutoFocus();
                                mCameraView.updateCount("自动对焦运行次数：" + count);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 15 * 1000, 60 * 1000);
            }
        }

        surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(this);

        //surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        //mSurfaceView.setZOrderOnTop(false);
        //mTextView.setText("等待 1 秒");
        mMediaPlayer = new MediaPlayer();

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setOnErrorListener(this);
    }

    public void handleControlMsg(int cmdtype, String cmdid, String cmdpara) {
        Log.i(TAG, "handle windows control message");
        if (FactorySetting.COMMAND_TASK_STOP == cmdtype) {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            if (autoFocusTimer != null) {
                autoFocusTimer.cancel();
            }
            MotorUtil.unsetEventCallback();
            setResult(cmdid, PASS, true);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mp != null) {
            if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                Log.e(TAG, "media error, server died,we need new media player");
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();

                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.setOnErrorListener(this);
                    localMediaPlay(MediaItem);
                }
            }
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            mMediaPlayer.seekTo(0);
            mMediaPlayer.setLooping(true);
        }
    }

    @Override
    public void onAFStart() {
        SPUtils.setParam(this, AF_COUNT, count);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCameraView.updateAF("----自动对焦中----");
            }
        });
    }

    @Override
    public void onAFFinish() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCameraView.updateAF("----自动对焦完成----");
            }
        });
    }
}
