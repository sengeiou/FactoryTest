/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fm.factorytest.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fm.factorytest.R;
import com.fm.factorytest.base.BaseActivity;
import com.fm.factorytest.global.FactorySetting;
import com.fm.middlewareimpl.impl_home.AudioTestManagerImpl;
import com.fm.middlewareimpl.impl_home.MediaTestManagerImpl;
import com.fm.middlewareimpl.impl_home.PicModeManagerImpl;
import com.fm.middlewareimpl.interf.AudioTestManagerAbs;
import com.fm.middlewareimpl.interf.MediaTestManagerAbs;
import com.fm.middlewareimpl.interf.PicModeManagerAbs;


public class InputSourceForTvView extends BaseActivity implements
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    private static final String TAG = "InputSourceForTvView";
    protected MediaTestManagerAbs mMediaImpl;
    protected PicModeManagerAbs mPicModeImpl;
    protected AudioTestManagerAbs mAudioImpl;
    public static final int INPUT_SOURCE_ATV = 1;//come from input source define
    public static final int INPUT_SOURCE_DTV = 28;
    private Menu mMenu;
    private View mView;
    private TextView mOutPutView;
    private TextView mChannelShowView;
    private RelativeLayout mSourceLayout;
    private Handler mHandler;
    private int miCurrSource = INPUT_SOURCE_ATV;

    private int[] mConfigSourceList;
    private int[] mAvailableSourceList;
    private String[] mAvailableSourceListName;
    private TextView[] mSourceList;
    private int mCurrentChannel;
    private int mTotalChannelCount;

    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "[onCreate] set to activity prepare");
        mMediaImpl = new MediaTestManagerImpl(this);
        mPicModeImpl = new PicModeManagerImpl(this);
        mAudioImpl = new AudioTestManagerImpl(this);
        if (!mMediaImpl.initTvview()) {
            Log.i(TAG, "tv view init failed");
            finish();
            return;
        }
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE | android.view.Window.FEATURE_OPTIONS_PANEL);
        setContentView(R.layout.inputsourcefortvview);
        if (!mMediaImpl.tvcontextInit()) {
            Log.i(TAG, "tv context init failed");
            finish();
            return;
        }
        initView();
        mMediaImpl.initSourceIdTable();
/*
		if(!mMediaImpl.switchCurrSour(INPUT_SOURCE_ATV)){
			Log.e(TAG,"switch to ATV source error");
			finish();
			return;
		}
*/
    }

    void initView() {
        mView = findViewById(R.id.tranplentview);
        mView.setVisibility(View.VISIBLE);
        mMediaImpl.registerTvview(mView);
        mChannelShowView = (TextView) findViewById(R.id.atv_channel);
        mOutPutView = (TextView) findViewById(R.id.event_output);
        mSourceLayout = (RelativeLayout) findViewById(R.id.linear_layout_root);
        mConfigSourceList = mMediaImpl.getAllInputSour();
        mSourceList = new TextView[mConfigSourceList.length];
        for (int i = 0; i < mConfigSourceList.length; i++) {
            mSourceList[i] = new TextView(this);
            mSourceList[i].setGravity(Gravity.LEFT);
            mSourceList[i].setVisibility(View.GONE);
        }
    }

    void setOutPutView(TextView view) {
    }

    void output(String msg) {
        if (mOutPutView != null) {
            //mOutPutView.setText(msg);
        }
    }

    private int SourceWaitCounts = 0;
    private String CurrentCmdId = null;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "=======" + SourceWaitCounts + "==========");
            int source = mMediaImpl.getCurrSour();
            if (SourceWaitCounts < 8) {
                SourceWaitCounts++;
                // TODO Auto-generated method stub
                //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
                Log.i(TAG, "miCurrentSource is " + miCurrSource);
                Log.i(TAG, "source is " + source);
                if (source != miCurrSource) {
                    mHandler.postDelayed(this, 500);
                } else {
                    updateAvailableSource();
                    setResult(CurrentCmdId, true, false);
                    mHandler.removeCallbacks(runnable);
                }
            } else {
                SourceWaitCounts = 0;
                //timeout (4s), kill current activity
                Log.i(TAG, "timeout, kill current activity");
                setResult(CurrentCmdId, false, true);
                mHandler.removeCallbacks(runnable);
            }
        }
    };

    /******** begin onError callback **************/
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onError enter TODO what=" + what);
        return false;
    }
    /******** end onError callback **************/
    /******** begin OnPreparedListener callback **************/
    public void onPrepared(MediaPlayer mp) {
        Log.i(TAG, "onPrepared enter, TODO");
    }

    /******** end OnPreparedListener callback **************/
    protected void onPause() {
        super.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        mMediaImpl.unregisterTvview(mView);

    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        if (action != KeyEvent.ACTION_DOWN) {
            return super.dispatchKeyEvent(event);
        }
        int keyCode = event.getKeyCode();
        if ((keyCode == KeyEvent.KEYCODE_BACK) || (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) || (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
                || (keyCode == KeyEvent.KEYCODE_DPAD_UP) || (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
            mAudioImpl.closeDap();
            return true;
        }
        //channel change
        if (miCurrSource == INPUT_SOURCE_ATV || miCurrSource == INPUT_SOURCE_DTV) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (mTotalChannelCount > 1) {
                    if (miCurrSource == INPUT_SOURCE_ATV) {
                        mMediaImpl.switchAtvChannel((mCurrentChannel + 1) % mTotalChannelCount);
                    } else {
                        if ((mCurrentChannel + 1) == mTotalChannelCount) {
                            mCurrentChannel = 0;
                        }
                        mMediaImpl.switchDtvChannel((mCurrentChannel + 1) % mTotalChannelCount);
                    }
                    updateChannel();
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                Log.i(TAG, "down channel " + (mCurrentChannel));
                if (mTotalChannelCount > 1) {
                    if (miCurrSource == INPUT_SOURCE_ATV) {
                        if (mCurrentChannel > 0) {
                            mMediaImpl.switchAtvChannel((mCurrentChannel - 1) % mTotalChannelCount);
                        } else {
                            mMediaImpl.switchAtvChannel(mTotalChannelCount - 1);
                        }
                    } else {
                        if (mCurrentChannel > 1) {
                            mMediaImpl.switchDtvChannel((mCurrentChannel - 1) % mTotalChannelCount);
                        } else {
                            mMediaImpl.switchDtvChannel(mTotalChannelCount);
                        }
                    }
                    updateChannel();
                }
            }
        }
        //input source change//TODO
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            int source = getNextInputSource(false);
            if (source != -1) {
                mMediaImpl.switchCurrSour(source);
                output("switch to input source:" + mMediaImpl.getSourName(source));
                updateChannelSourceShowing();
                miCurrSource = source;
                mMediaImpl.surfacePlayerStart();
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            int source = getNextInputSource(true);
            if (source != -1) {
                mMediaImpl.switchCurrSour(source);
                output("switch to input source:" + mMediaImpl.getSourName(source));
                updateChannelSourceShowing();
                miCurrSource = source;
                mMediaImpl.surfacePlayerStart();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    void updateChannel() {
        Log.i(TAG, "======update channel=========");
        if (miCurrSource == INPUT_SOURCE_ATV) {
            mCurrentChannel = mMediaImpl.getAtvCurrChan();
            mTotalChannelCount = mMediaImpl.getAtvChanCount();
            Log.i(TAG, "ATV channel Count is:" + mTotalChannelCount);
        } else if (miCurrSource == INPUT_SOURCE_DTV) {
            mCurrentChannel = mMediaImpl.getDtvCurrChan();
            mTotalChannelCount = mMediaImpl.getDtvChanCount();
            Log.i(TAG, "DTV channel Count is:" + mTotalChannelCount);
        }
        //updateChannelSourceShowing();
    }

    int getNextInputSource(boolean up) {
        int index = -1;
        int size = -1;
        if (mAvailableSourceList != null) {
            size = mAvailableSourceList.length;
            for (int i = 0; i < size; i++) {
                if (miCurrSource == mAvailableSourceList[i]) {
                    index = i;
                    break;
                }
            }
        }
        if (index == -1) return -1;
        if (index == 0) {
            if (up) {
                return size > 1 ? mAvailableSourceList[index + 1] : mAvailableSourceList[index];
            } else {
                return mAvailableSourceList[size - 1];
            }
        } else if (index == (size - 1)) {
            if (up) {
                return mAvailableSourceList[0];
            } else {
                return size > 1 ? mAvailableSourceList[index - 1] : mAvailableSourceList[index];
            }
        } else {
            return up ? mAvailableSourceList[index + 1] : mAvailableSourceList[index - 1];
        }
    }

    void updateChannelSourceShowing() {
		/*
		   mHandler.removeCallbacks(updateUIRunnable);
		   mHandler.postDelayed(updateUIRunnable,50);
		   */
    }

    TextView getTextView(int source) {
        for (int i = 0; i < mConfigSourceList.length; i++) {
            if (mConfigSourceList[i] == source) {
                return mSourceList[i];
            }
        }
        return null;
    }

    /*
    Runnable updateUIRunnable = new Runnable() {
        public void run() {
            // channel view
            if (miCurrSource == INPUT_SOURCE_ATV) {
                mChannelShowView.setVisibility(View.VISIBLE);
                mChannelShowView.setText("current channel/total: "+mCurrentChannel+"/"+mTotalChannelCount);
            } else {
                mChannelShowView.setVisibility(View.INVISIBLE);
            }
            mSourceLayout.removeAllViews();
            if (mAvailableSourceList != null) {
                for (int i=0; i<mAvailableSourceList.length; i++) {
                    TextView view = getTextView(mAvailableSourceList[i]);
                    if (view != null) {
                        view.setVisibility(View.VISIBLE);
                        if (miCurrSource == mAvailableSourceList[i]) {
                            view.setTextColor(0xd5f619ff);
                        } else {
                            view.setTextColor(0xffffffff);
                        }
                        view.setText(mAvailableSourceListName[i]);
                        view.setPadding(3, 40*(i+1), 3, 3);
                        mSourceLayout.addView(view,new LinearLayout.LayoutParams(
                                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    }
                }
            }
        }
    };
    */
    private void updateAvailableSource() {
        mAvailableSourceList = mMediaImpl.getAllInputSour();
        mAvailableSourceListName = new String[mAvailableSourceList.length];
        if (mAvailableSourceList != null) {
            for (int i = 0; i < mAvailableSourceList.length; i++) {
                mAvailableSourceListName[i] = mMediaImpl.getSourName(mAvailableSourceList[i]);
            }
        }
        miCurrSource = mMediaImpl.getCurrSour();
        updateChannelSourceShowing();
    }


    class AtvChannel {
        AtvChannel(long _freq, int _index) {
            freq = _freq;
            index = _index;
        }

        long freq;
        int index;
    }

    public void handleCommand(String cmdid, String param) {
        int setTime;
        super.handleCommand(cmdid, param);
		/*
		Log.i(TAG, "[handleCommand] set to ATV Source");
		//mSourceManager.setCurrentSource(SourceManager.TYPE_INPUT_SOURCE_ATV);
		for(setTime = 0; setTime < 20; setTime++){
			if (mMediaImpl.getCurrSour() != INPUT_SOURCE_ATV) {
				Log.e(TAG,"wait switch to ATV source : " + setTime);
				sleep(200);
			}else{
				break;
			}
		}
		if(setTime == 10){
			Log.e(TAG,"switch to ATV source error");
			finish();
			return;
		}
		miCurrSource = INPUT_SOURCE_ATV;
		SourceWaitCounts = 0;
		CurrentCmdId = cmdid;
		mHandler.postDelayed(runnable, 200);
		updateChannelSourceShowing();
		*/
        mHandler = new Handler();
        mPicModeImpl.picSwitchPicUserMode();
        setResult(cmdid, true, false);

        mAudioImpl.closeDap();

        Log.i(TAG, "handleCommand param is :[" + param + "]");

        if (!param.equals("")) {
            mMediaImpl.switchCurrSour(2);
            output("switch to input source:" + mMediaImpl.getSourName(2));
            updateChannelSourceShowing();
            miCurrSource = 2;
            mMediaImpl.surfacePlayerStart();

            miCurrSource = 2;
            SourceWaitCounts = 0;
            CurrentCmdId = cmdid;
            mHandler.postDelayed(runnable, 200);
        }
    }

    public void handleControlMsg(int cmdtype, String cmdid, String cmdpara) {
        int setTime;
        Log.e(TAG, "input id: " + cmdid);
        Log.e(TAG, "input information: " + cmdpara);
        if (FactorySetting.COMMAND_TASK_STOP == cmdtype) {
            setResult(cmdid, "noresponse", true);
        } else if (FactorySetting.COMMAND_TASK_BUSINESS == cmdtype) {
            int currSource = mMediaImpl.getCurrSour();
            String[] paraInput;
            if (cmdpara == null) {
                setResult(cmdid, false, false);
                Log.e(TAG, "no input information");
                return;
            }
            paraInput = cmdpara.split(":");
            //paraInput[0]: source  or channel flag
            //paraInput[1]: source name or channel number
            if (paraInput.length < 2) {
                setResult(cmdid, false, false);
                Log.i(TAG, cmdpara + " --- cmd para isn't enough");
                return;
            }
            Log.i(TAG, "[0]:" + paraInput[0] + "---[1]" + paraInput[1]);
            if (paraInput[0].equals("SOURCE")) {
                Integer id = mMediaImpl.transSourNameToId(paraInput[1]);
                Log.i(TAG, "the source id is: " + id);
                if (id == null) {
                    setResult(cmdid, false, false);
                    Log.i(TAG, "set source Id error: " + cmdpara);
                }
                if (currSource != id) {
                    mMediaImpl.switchCurrSour(id);
                    output("switch to input source:" + mMediaImpl.getSourName(id));
                    updateChannelSourceShowing();
                    miCurrSource = id;
                    mMediaImpl.surfacePlayerStart();

                    miCurrSource = id;
                    SourceWaitCounts = 0;
                    CurrentCmdId = cmdid;
                    mHandler.postDelayed(runnable, 200);
					/*
					for(setTime = 0; setTime < 25; setTime++){
						if (mMediaImpl.getCurrSour() != id) {
							Log.e(TAG,"wait switch to :" + id + " source :" + setTime);
							sleep(200);
						}else{
							break;
						}
					}
					if(setTime == 25){
						Log.e(TAG,"switch to "+id+" source error");
						finish();
						setResult(cmdid, false, false);
						return;
					}
					setResult(cmdid, true, false);
					*/
                } else {
                    Log.i(TAG, "switch into current source");
                    setResult(cmdid, true, false);
                }
            } else if (paraInput[0].equals("CHANNEL")) {
                String[] paraSub = paraInput[1].split(",");
                if (currSource == INPUT_SOURCE_ATV) {
                    updateChannel();
                    Log.i(TAG, "change atv channel paraSub is:" + paraSub[0] + "mTotalChannelCount is :" + mTotalChannelCount);
                    if (mTotalChannelCount > 1 && Integer.parseInt(paraSub[0]) < mTotalChannelCount) {
                        mMediaImpl.switchAtvChannel(Integer.parseInt(paraSub[0]));
                        updateChannel();
                    }
                    setResult(cmdid, true, false);
                } else if (currSource == INPUT_SOURCE_DTV) {
                    int channelnum = 0;
                    if (paraSub.length > 1) {
                        Log.i(TAG, "paraSubi:" + paraSub[0] + paraSub[1] + "--value");
                        channelnum = Integer.parseInt(paraSub[0]) * 0x100 +
                                Integer.parseInt(paraSub[1]);
                    } else {
                        Log.i(TAG, "paraSubi:" + paraSub[0] + "--value");
                        channelnum = Integer.parseInt(paraSub[0]);
                    }
                    Log.i(TAG, "mTotalChannelCount:" + mTotalChannelCount);
                    Log.i(TAG, "channelnum:" + channelnum);
                    updateChannel();
                    mMediaImpl.switchDtvChannel(channelnum);
                    updateChannel();
                    setResult(cmdid, true, false);
                } else {
                    Log.e(TAG, "can't set channel at current source: " + paraInput[1]);
                    setResult(cmdid, false, false);
                }
            } else {
                Log.e(TAG, "tag error: " + paraInput[0]);
                setResult(cmdid, false, false);
            }
        }
    }
}
