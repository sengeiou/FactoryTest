package com.fm.factorytest.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.fm.factorytest.R;
import com.fm.factorytest.base.BaseActivity;
import com.fm.factorytest.global.FactorySetting;
import com.fm.factorytest.views.CameraView;

import java.io.IOException;

public class LocalMedia extends BaseActivity implements SurfaceHolder.Callback,
        MediaPlayer.OnCompletionListener {
    private static final String TAG = "FactoryLocalMedia";
    private static final String[] MediaSour = {
            "/system/factory/autovideo_4k2k.mov",
            "/system/factory/pink_noise_0db.mov",
            "/system/factory/autovideo.mov",
            "/system/factory/autovideo_1080.mov",
    };
    private int MediaItem = 0;
    //private TextView mTextView;
    private SurfaceView mSurfaceView;
    private SurfaceHolder surfaceHolder;

    //private int source_item = 0;

    private MediaPlayer mMediaPlayer;
    /*
     * Called when the activity is first created.
     */
    private CameraView mCameraView;

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
        try {
            mMediaPlayer.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mMediaPlayer.start();
        mMediaPlayer.seekTo(0);
        mMediaPlayer.setLooping(true);

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

    private static final String DUCK = "duck";
    private static final String VIDEO4K = "4k2k";
    private static final String PINKNOISE = "pink";
    private static final String ICEHOCKEY = "ice";

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
        mCameraView = (CameraView) findViewById(R.id.cv_aging);

        surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(this);

        //surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        //mSurfaceView.setZOrderOnTop(false);
        //mTextView.setText("等待 1 秒");
        mMediaPlayer = new MediaPlayer();

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void handleControlMsg(int cmdtype, String cmdid, String cmdpara) {
        Log.i(TAG, "handle windows control message");
        if (FactorySetting.COMMAND_TASK_STOP == cmdtype) {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            setResult(cmdid, PASS, true);
        }
    }
}
