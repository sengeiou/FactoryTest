package com.fm.factorytest.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.fm.factorytest.base.BaseActivity;
import com.fm.factorytest.global.FactorySetting;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import mitv.graphics.ImagePlayerFor4KManager;

public class TestPattern extends BaseActivity {
    private final String TAG = "FactoryTestPattern";
    private ImagePlayerFor4KManager mMMediaPlayer = null;
    private SurfaceView surfaveview;
    SurfaceHolder myholder;
    MySurfaceView cocossurface;

    private String picfile = "";

    //add key test method, 0x1 is combine method, it also is default
    private int keytestmethod = 0x1;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(NON_INNACTIVITYCMD, PASS, false);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "catch key event");

        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {

        return super.onKeyUp(keyCode, event);
    }


    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            myholder = holder;
            mMMediaPlayer.setDisplay(myholder);
            test();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }

    private void test() {
        String path = "/system/factory/";
        String realpath = path + picfile;
        Log.i(TAG, "TestPattern realpath:[" + realpath + "]");
        mMMediaPlayer.setDataSource(realpath);
        Log.i(TAG, "TestPattern prepare");
        int aa = mMMediaPlayer.prepare(0);
        Log.i(TAG, "TestPattern show");
        mMMediaPlayer.show();
        Log.i(TAG, "TestPattern after show");
        echoEntry("sys/class/gpio/gpio199/value", "1");
    }

    private static final String sdcuhd_21 = "FlickerPattern_T21_SDC_4K.bmp";
    private static final String sdcfhd = "sdcfhd.bmp";
    private static final String lgfhd = "FlickerPattern_T19_LG_FHD.bmp";
    private static final String csotfhd = "FlickerPattern_T25_CSOT_FHD.bmp";
    private static final String lguhd4k = "FlickerPattern_T20_T21_LG_4K.bmp";
    private static final String sdcuhd_20 = "FlickerPattern_T20_SDC_4K.bmp";

    public void handleCommand(String cmdid, String param) {
        super.handleCommand(cmdid, param);
        Log.v(TAG, "command para: " + param);
        if (param.equals("1")) {
            picfile = sdcuhd_21;
        } else if (param.equals("2")) {
            picfile = sdcfhd;
        } else if (param.equals("3")) {
            picfile = lgfhd;
        } else if (param.equals("4")) {
            picfile = csotfhd;
        } else if (param.equals("5")) {
            picfile = lguhd4k;
        } else if (param.equals("6")) {
            picfile = sdcuhd_20;
        } else {
            Log.v(TAG, "no para, set as default source ");
            picfile = sdcfhd;
        }

        surfaveview = new SurfaceView(this);
        surfaveview.getHolder().addCallback(new SurfaceCallback());
        surfaveview.getHolder().setKeepScreenOn(true);
        surfaveview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaveview.getHolder().setFormat(257);
        surfaveview.getHolder().setFormat(258);
        surfaveview.setFocusable(true);
        surfaveview.setFocusableInTouchMode(true);
        surfaveview.requestFocus();

        cocossurface = new MySurfaceView(this);
        cocossurface.setLayoutParams(new FrameLayout.LayoutParams(1500, 1000));

        FrameLayout framelayout = new FrameLayout(this);
        framelayout.addView(cocossurface);
        setContentView(framelayout);

        FrameLayout lay = (FrameLayout) findViewById(android.R.id.content);
        lay.addView(surfaveview);

        mMMediaPlayer = ImagePlayerFor4KManager.getInstance(this);

    }

    public void handleControlMsg(int cmdtype, String cmdid, String cmdpara) {
        if (FactorySetting.COMMAND_TASK_BUSINESS == cmdtype) {
            Log.i(TAG, "TestPattern finish");
            echoEntry("sys/class/gpio/gpio199/value", "0");
            setResult(cmdid, PASS, true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean echoEntry(final String entry, String rts) {
        boolean ret = false;
        OutputStreamWriter osw = null;
        Log.i(TAG, "echo [" + rts + "] to path [" + entry + "]");
        try {
            osw = new OutputStreamWriter(new FileOutputStream(entry));
            osw.write(rts, 0, rts.length());
            osw.flush();
            ret = true;
        } catch (Exception e) {
            Log.e(TAG, "final write data to file " + entry + " execetpion=" + e);
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                }
            }
        }
        return ret;
    }
}

class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private DrawThread mThread = null;

    public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySurfaceView(Context context) {
        super(context);
        init();
    }

    private void init() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        mThread = new DrawThread(holder);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        mThread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    /**
     * 绘制线程类
     */
    public class DrawThread extends Thread {
        private SurfaceHolder mHolder = null;
        private boolean isRun = false;

        public DrawThread(SurfaceHolder holder) {
            mHolder = holder;

        }

        @Override
        public void run() {
            Canvas canvas = null;
            synchronized (mHolder) {
				/*try {
					canvas = mHolder.lockCanvas();
					canvas.drawColor(Color.TRANSPARENT);
					Paint p = new Paint();
					p.setColor(Color.BLUE);

					Rect r = new Rect(100, 50, 300, 250);
					canvas.drawRect(r, p);
					canvas.drawText("这是上边surface", 100, 310, p);

				} catch (Exception e) {
					e.printStackTrace();

				} finally {
					if (null != canvas) {
						mHolder.unlockCanvasAndPost(canvas);
					}
				}*/

            }
        }

    }

}
