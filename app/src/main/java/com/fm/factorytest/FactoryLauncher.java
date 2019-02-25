package com.fm.factorytest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.session.MediaSessionLegacyHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.fm.factorytest.helper.TemperatureHelper;
import com.fm.factorytest.service.CommandService;
import com.fm.middlewareimpl.impl.KeyManagerImpl;
import com.fm.middlewareimpl.impl.SysAccessManagerImpl;
import com.fm.middlewareimpl.interf.KeyManagerAbs;
import com.fm.middlewareimpl.interf.SysAccessManagerAbs;

import mitv.powermanagement.ScreenSaverManager;

public class FactoryLauncher extends Activity {
    private final String TAG = "FactoryTestLauncher";
    KeyManagerAbs keyManagerAbs;
    String KEY = "factory_power_mode";
    KeyManagerAbs keyManager = null;
    private TextView mTV_FW_Version;
    private Context mContext;
    private StartPostSale mStartPostSale = null;
    /**
     * Called when the activity is first created.
     */
    private Handler mHandler = null;
    private AudioManager am;
    private Runnable mRefreshRunnable = new Runnable() {

        @Override
        public void run() {
            updateTemperatureView(TemperatureHelper.queryTemperature());
            mHandler.postDelayed(mRefreshRunnable, 3000);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent t = new Intent(this, CommandService.class);
        this.startService(t);
        mContext = this;
        setContentView(R.layout.factorylauncher);
        // disable status bar
        Log.i(TAG, "disable satus bar");
        //StatusBarController.hideStatusBar(this);
        // disable screen saver
        Log.i(TAG, "disable screen saver");
        ScreenSaverManager manager = ScreenSaverManager.getInstance();
        manager.setScreenSaverEnabled(false);
        // disable sleep
        Log.i(TAG, "disable system sleep");
        mTV_FW_Version = (TextView) findViewById(R.id.tv_factory_version);
        //set default input
        android.provider.Settings.Secure.putString(mContext.getContentResolver(), "default_input_method", "com.baidu.input/.ImeService");
        mHandler = new Handler();
        keyManagerAbs = new KeyManagerImpl(this);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUP KeyCode " + keyCode);
        //Log.d(TAG, "event " + event);
        dispatchKeyEvent(keyCode);
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown KeyCode " + keyCode);
        if (mStartPostSale == null) {
            mStartPostSale = new StartPostSale();
        }
        mStartPostSale.tryLanuchPostSale(keyCode);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.post(mRefreshRunnable);
        queryFWVersion();
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRefreshRunnable);
    }

    private void queryFWVersion() {
        String version = SystemProperties.get("ro.build.version.incremental", "XXXX");
        mTV_FW_Version.setText("工厂版本：" + version);
    }

    public void updateTemperatureView(TemperatureHelper.TemperatureData data) {
        ((TextView) findViewById(R.id.temperatur_1)).setText(data.envTemperature);
        ((TextView) findViewById(R.id.temperatur_2)).setText(data.channel1Temperature);
        ((TextView) findViewById(R.id.temperatur_3)).setText(data.channel2Temperature);
        ((TextView) findViewById(R.id.temperatur_4)).setText(data.wheelTemperature);
    }

    public void dispatchKeyEvent(int code) {
        switch (code) {
            case 19:
                SysAccessManagerAbs sysAbs = new SysAccessManagerImpl(this);
                Log.d(TAG, "version = " + sysAbs.readDLPVersion());
                break;
            case 20:
                break;
            case 21:
                break;
            case 22:
                break;
            case 24:
                volUp();
                //String name = keyManagerAbs.aml_key_get_name();
                //Log.i(TAG, "aml_key_get_name   " + name);
                break;
            case 25:
                volDown();
                break;
        }
    }

    private void volUp() {
        // am.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);

        MediaSessionLegacyHelper.getHelper(this).sendAdjustVolumeBy(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);

        // KeyEvent event = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_VOLUME_UP);
        //
        // MediaSessionLegacyHelper.getHelper(this).sendVolumeKeyEvent(event,false);
    }

    private void volDown() {
        MediaSessionLegacyHelper.getHelper(this).sendAdjustVolumeBy(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);

        //am.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);

        // KeyEvent event = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_VOLUME_DOWN);
        // MediaSessionLegacyHelper.getHelper(this).sendVolumeKeyEvent(event,false);
    }

    /**
     * \\\\\\\\\\\\\\\\\\\\\\\\\\
     * * Modified for launch FactoryTest APP
     * *
     * * The following key sequence use to launch FactoryTest main UI
     * *     KEYCODE_DPAD_RIGHT       1
     * *     KEYCODE_MENU       1
     * *     KEYCODE_BACK       1
     * *     KEYCODE_MENU       1
     * *     KEYCODE_BACK       1
     * *     KEYCODE_MENU       1
     * *     KEYCODE_BACK       1
     * *
     * * The following key use to clean cached key sequence
     * *     KEYCODE_ENTER
     **/
    private class StartPostSale {
        private int[] mKeySequence = null;
        private int[] mKeyPreset = null;
        private int mKeyCount = 0;
        private int mKeyIdx = 0;

        public StartPostSale() {
            mKeyPreset = new int[32];

            mKeyPreset[mKeyCount++] = KeyEvent.KEYCODE_DPAD_RIGHT;

            for (int i = 0; i < 3; i++) {
                //mKeyPreset[mKeyCount++] = KeyEvent.KEYCODE_DPAD_UP;
                //mKeyPreset[mKeyCount++] = KeyEvent.KEYCODE_DPAD_DOWN;
                mKeyPreset[mKeyCount++] = KeyEvent.KEYCODE_MENU;
                mKeyPreset[mKeyCount++] = KeyEvent.KEYCODE_BACK;
            }
            mKeyIdx = 0;
            mKeySequence = new int[mKeyCount];
        }

        /**
         * * Factory test launched return true, otherwise return false.
         **/
        public boolean tryLanuchPostSale(int keyCode) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                Log.v(TAG, "######## Clear factory key sequence ########");
                mKeyIdx = 0;
                return false;
            }

            mKeySequence[mKeyIdx++] = keyCode;
            if (mKeyIdx >= mKeyCount) {
                String preset = "";
                String seq = "";
                int i = 0;

                mKeyIdx = 0;
                for (i = 0; i < mKeyCount; i++) {
                    preset += (String.valueOf(mKeyPreset[i]) + "\t");
                    seq += (String.valueOf(mKeySequence[i]) + "\t");
                    if (mKeyPreset[i] != mKeySequence[i])
                        break;
                }
                Log.v(TAG, "################## preset and sequence keys #####################");
                Log.v(TAG, "  Preset: " + preset);
                Log.v(TAG, "Sequence: " + seq);
                Log.v(TAG, "#################################################################");
                if (i >= mKeyCount) {
					/*
					Intent postsale = new Intent(FactoryLauncher.this, PostSale.class);
					mContext.startActivity(postsale);
					*/
					/*
					PackageManager packageManager = getPackageManager();
					Intent intent=new Intent();
					intent =packageManager.getLaunchIntentForPackage("PostSale");
					*/
                    Log.v(TAG, "**************************prepare start postsale*******");
                    Intent mIntent = new Intent("android.intent.action.MAIN");
                    ComponentName comp = new ComponentName(
                            "com.duokan.postsale",
                            "com.duokan.postsale.MainActivity");
                    mIntent.setComponent(comp);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mIntent.addCategory("android.intent.category.LAUNCHER");
                    if (mIntent == null) {
                        Log.i(TAG, "APP not found!   ");
                        return false;
                    }
                    startActivity(mIntent);
                    return true;

                }
            }
            return false;
        }
    }

}
