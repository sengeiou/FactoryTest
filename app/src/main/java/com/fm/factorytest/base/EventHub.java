package com.fm.factorytest.base;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

public class EventHub {

	private final static String TAG = "FactoryEventHub";

	private final static int TOUCH_STATUS_MASK = 0xFF000000;
	private final static int TOUCH_STATUS_SHIFT = 24;
	private final static int PROXY_STATUS_MASK = 0x00FF0000;
	private final static int PROXY_STATUS_SHIFT = 16;
	private final static int TOUCH_POSITION_MASK = 0x0000FF00;
	private final static int TOUCH_POSITION_SHIFT = 8;
	private final static int SENSOR_STATUS_MASK = 0x000000FF;
	private final static int SENSOR_STATUS_SHIFT = 0;

	private final static int EVENT_INVALID_ID = -1;
	private final static int EVENT_SHOW_ID = 0;
	private final static int EVENT_HIDE_ID = 1;
	private final static int EVENT_CLICK = 2;
	private final static int EVENT_LEFT = 3;
	private final static int EVENT_RIGHT = 4;
	private final static int EVENT_LEFT_FLING = 5;
	private final static int EVENT_RIGHT_FLING = 6;

	private final static int STEP_SHIFT_INVALID = -1;
	private final static int STEP_SHIFT_FIRST = 2;
	private final static int STEP_SHIFT_SECOND =4;
	private final static int STEP_SHIFT_THIRD = 6;
	
	private final static int VELOCITY_LEVEL_NORMAL = 0;
	private final static int VELOCITY_LEVEL_FAST = 1;
	private final static int VELOCITY_LEVEL_VERY_FAST = 2;
	
	private final static int INITIAL_AUTO_MOVE_WAIT_TIME = 1000;
	private final static int SECOND_AUTO_MOVE_WAIT_TIME = 600;
	private final static int THIRD_AUTO_MOVE_WAIT_TIME = 300;
	private final static int FORTH_AUTO_MOVE_WAIT_TIME = 50;


	private boolean mLastIsClick;
	private int lastMoveLeftStep;
	private int lastMoveRightStep;
	
	private long mMoveStartTime;
	private int mMoveVelocityLevel;
	
	private int mMoveLeftIndex;
	private int mMoveRightIndex;

	private Context mContext;
	private Handler mHandler;

	private long mStartTime = 0;
	private int mClickTimes = 0;
	private long mCurrentTime = 0;
	public boolean switchModeFlag = false;
	private static final long SWITCHMODE_CHECK_TIMEOUT = 3500;
	public EventHub(Context context) {
		mContext = context;
		mHandler = new Handler(mContext.getMainLooper());
	}

	public boolean dispatchRawEvent(final int event, final long deviceTime, final long frameworkTime) {
		Log.d(TAG, "******Event start......dispatchRawEvent event:" + event + ", deviceTime:"
				+ deviceTime
				+ ", frameworkTime:" + frameworkTime);
		String binaryEvent = Integer.toBinaryString(event);
		StringBuilder resultBinaryString = new StringBuilder();
		if (binaryEvent.length() < 32) {
			int lengthDelta = 32 - binaryEvent.length();
			for (int i = 0; i < lengthDelta; ++i) {
				resultBinaryString.append("0");
			}
			resultBinaryString.append(binaryEvent);
		} else {
			resultBinaryString.append(binaryEvent);
		}
		Log.d(TAG, "dispatchRawEvent binary:" + resultBinaryString.toString());
		boolean ieatit = false;
		final int eventId = translateEvent(event, deviceTime, frameworkTime);
		mHandler.post(new Runnable() {

			@Override
			public void run() {

				switch (eventId) {
					case EVENT_SHOW_ID: {

						break;
					}
					case EVENT_HIDE_ID: {
						cancelAutoMove();

						break;
					}
					case EVENT_CLICK: {
						Log.i(TAG, "cuiwei");
						if(mClickTimes == 0){
							Log.i(TAG, "cuiwei Start");
							mStartTime = SystemClock.uptimeMillis();
							mClickTimes++;
						}else{
							mClickTimes++;
							Log.i(TAG, "cuiwei " + mClickTimes);
							if(mClickTimes > 9){
								mCurrentTime = SystemClock.uptimeMillis();
								if((mCurrentTime - mStartTime) < 3500){
									switchModeFlag = true;
								}
								mClickTimes = 0;
							}
						}
						break;
					}
					case EVENT_LEFT: {
						cancelAutoMove();
						mMoveLeftRunnable.run();
						break;
					}
					case EVENT_RIGHT: {
						cancelAutoMove();
						mMoveRightRunnable.run();
						break;
					}
					case EVENT_LEFT_FLING: {
						cancelAutoMove();

						break;
					}
					case EVENT_RIGHT_FLING: {
						cancelAutoMove();

						break;
					}
					default: {
						Log.d(TAG, "handle EVENT_INVALID_ID");
						break;
					}
				}
			}
		});
		if (eventId != EVENT_INVALID_ID) {
			ieatit = true;
		}
		return ieatit;
	}

	private int translateEvent(int event, long deviceTime, long frameworkTime) {
		int resultEventID = EVENT_INVALID_ID;
		Log.d(TAG, "translateEvent event:" + Integer.toBinaryString(event));
		int touchStatus = (event & TOUCH_STATUS_MASK) >>> TOUCH_STATUS_SHIFT;
		Log.d(TAG, "translateEvent touchStatus:" + Integer.toBinaryString(touchStatus));
		int proxyStatus = (event & PROXY_STATUS_MASK) >>> PROXY_STATUS_SHIFT;
		Log.d(TAG, "translateEvent proxyStatus:" + Integer.toBinaryString(proxyStatus));
		int touchPosition = (event & TOUCH_POSITION_MASK) >>> TOUCH_POSITION_SHIFT;
		Log.d(TAG, "translateEvent touchPosition:" + Integer.toBinaryString(touchPosition));
		int sensorStatus = (event & SENSOR_STATUS_MASK) >>> SENSOR_STATUS_SHIFT;
		Log.d(TAG, "translateEvent sensorStatus:" + Integer.toBinaryString(sensorStatus));

		boolean isTouchDown = ((touchStatus & 0x80) == 0x80);
		boolean isClick = ((touchStatus & 0x40) == 0x40) 
				&& ((touchStatus & 0x70) != 0x70); // AND is not long press
		boolean isEnter = ((proxyStatus & 0x80) == 0x80);
		int closeCoe = (proxyStatus & 0x7F);
		boolean moveRight = ((touchStatus & 0x10) == 0x10) 
				&& ((touchStatus & 0x70) != 0x70);// AND is not long press
		boolean moveLeft = ((touchStatus & 0x20) == 0x20) 
				&& ((touchStatus & 0x70) != 0x70);// AND is not long press
		int shiftSteps = (touchStatus & 0x0F);
		if (!isTouchDown) {
			Log.d(TAG, "is NOT TouchDown");
		}
		if (moveLeft) {
			Log.d(TAG, "isMoveLeft shiftSteps:" + shiftSteps 
					+ ", lastMoveLeftStep:" + lastMoveLeftStep);
			if (lastMoveLeftStep == STEP_SHIFT_INVALID) {
				mMoveStartTime = System.currentTimeMillis();
			}
			if (shiftSteps >= 0) {
				if (shiftSteps <= STEP_SHIFT_FIRST) {
					if (lastMoveLeftStep == STEP_SHIFT_INVALID) {
						// a event
						Log.d(TAG, "A moveleft event");
						resultEventID = EVENT_LEFT;
						// regard velocity is 0 when first move
					}
					lastMoveLeftStep = shiftSteps;
				} else if (shiftSteps <= STEP_SHIFT_SECOND) {
					if (lastMoveLeftStep <= STEP_SHIFT_FIRST) {
						// a event
						Log.d(TAG, "A moveleft event");
						resultEventID = EVENT_LEFT;
					}
					lastMoveLeftStep = shiftSteps;
				} else if (shiftSteps <= STEP_SHIFT_THIRD) {
					if (lastMoveLeftStep <= STEP_SHIFT_SECOND) {
						// a event
						Log.d(TAG, "A moveleft event");
						resultEventID = EVENT_LEFT;
					}
					lastMoveLeftStep = shiftSteps;
				} else {
					if (lastMoveLeftStep <= STEP_SHIFT_THIRD) {
						// a event
						Log.d(TAG, "A moveleft event");
						resultEventID = EVENT_LEFT;
					}
					lastMoveLeftStep = shiftSteps;
				}
			} else {
				lastMoveLeftStep = STEP_SHIFT_INVALID;
			}
		} else {
			if (lastMoveLeftStep != STEP_SHIFT_INVALID
					&& !isTouchDown && !moveRight && !isClick) {
				float velocity = ((float)lastMoveLeftStep / (System.currentTimeMillis() - mMoveStartTime));
				mMoveVelocityLevel = judeVelocityLevel(velocity);
				if (mMoveVelocityLevel != VELOCITY_LEVEL_NORMAL) {
					resultEventID = EVENT_LEFT_FLING;
				}
				Log.d(TAG, "A moveleft event end fling v:" + velocity + ", level:" + mMoveVelocityLevel);
			}
			lastMoveLeftStep = STEP_SHIFT_INVALID;
		}
		if (moveLeft) {
			if (resultEventID == EVENT_LEFT){
				mHandler.removeCallbacks(mMoveLeftRunnable);
			}
		} else {
			mMoveLeftIndex = 0;
			mHandler.removeCallbacks(mMoveLeftRunnable);
		}
		if (moveRight) {
			Log.d(TAG, "is moveRight shiftSteps:" + shiftSteps
					+ ",lastMoveRightStep:" + lastMoveRightStep);
			if (lastMoveRightStep == STEP_SHIFT_INVALID) {
				mMoveStartTime = System.currentTimeMillis();
			}
			if (shiftSteps >= 0) {
				if (shiftSteps <= STEP_SHIFT_FIRST) {
					if (lastMoveRightStep == STEP_SHIFT_INVALID) {
						// a event
						Log.d(TAG, "A moveright event");
						resultEventID = EVENT_RIGHT;
					}
					lastMoveRightStep = shiftSteps;
				} else if (shiftSteps <= STEP_SHIFT_SECOND) {
					if (lastMoveRightStep <= STEP_SHIFT_FIRST) {
						// a event
						Log.d(TAG, "A moveright event");
						resultEventID = EVENT_RIGHT;
					}
					lastMoveRightStep = shiftSteps;
				} else if (shiftSteps <= STEP_SHIFT_THIRD) {
					if (lastMoveRightStep <= STEP_SHIFT_SECOND) {
						// a event
						Log.d(TAG, "A moveright event");
						resultEventID = EVENT_RIGHT;
					}
					lastMoveRightStep = shiftSteps;
				} else {
					if (lastMoveRightStep <= STEP_SHIFT_THIRD) {
						// a event
						Log.d(TAG, "A moveright event");
						resultEventID = EVENT_RIGHT;
					}
					lastMoveRightStep = shiftSteps;
				}
			} else {
				lastMoveRightStep = STEP_SHIFT_INVALID;
			}
		} else {
			if (lastMoveRightStep != STEP_SHIFT_INVALID
					&& !isTouchDown && !moveLeft && !isClick) {
				float velocity = ((float)lastMoveRightStep / (System.currentTimeMillis() - mMoveStartTime));
				mMoveVelocityLevel = judeVelocityLevel(velocity);
				if (mMoveVelocityLevel != VELOCITY_LEVEL_NORMAL) {
					resultEventID = EVENT_RIGHT_FLING;
				}
				Log.d(TAG, "A moveright event end fling v:" + velocity + ", level:" + mMoveVelocityLevel);
			}
			lastMoveRightStep = STEP_SHIFT_INVALID;
		}
		if (moveRight) {
			if (resultEventID == EVENT_RIGHT){
				mHandler.removeCallbacks(mMoveRightRunnable);
			}
		} else {
			mMoveRightIndex = 0;
			mHandler.removeCallbacks(mMoveRightRunnable);
		}
		if (resultEventID == EVENT_INVALID_ID) {
			if (isTouchDown) {
				Log.d(TAG, "isTouchDown");
				boolean longPress = ((touchStatus & 0x70) == 0x70);
				if (longPress) {
					// TODO post long press event
					Log.d(TAG, "isLongPress");
					//ScreenSaverManager saverManager = new ScreenSaverManager();
					//saverManager.postSystemShutdownDelayed(0, true);
					return EVENT_HIDE_ID;
				}
				mLastIsClick = false;
				if (resultEventID == EVENT_INVALID_ID
						 ) {
					resultEventID = EVENT_SHOW_ID;
				}
			} else {
				Log.d(TAG, "isClick:" + isClick);
				Log.d(TAG, "isEnter?:" + isEnter + ",closeCoe:" + closeCoe);
				if (isClick) {
					if (!mLastIsClick) {
						Log.d(TAG, "isClick ok");
						resultEventID = EVENT_CLICK;
					}
					mLastIsClick = true;
				} else {
					mLastIsClick = false;
				}
				if (resultEventID == EVENT_INVALID_ID) {
					if (isEnter) {
						Log.d(TAG, "isEnter closeCoe:" + closeCoe);
						if (closeCoe <= 1) {
							//Log.d(TAG, "A leave event *");
							//resultEventID = EVENT_HIDE_ID;
							// ignore closeCoe <= 1 event
						} else if (closeCoe >= 3) {
							Log.d(TAG, "A enter event");
							resultEventID = EVENT_SHOW_ID;
						}
					} else {
						Log.d(TAG, "isLeave closeCoe:" + closeCoe);
						Log.d(TAG, "A leave event");
						resultEventID = EVENT_HIDE_ID;
					}
				}
			}
		}
		return resultEventID;
	}
	
	private int judeVelocityLevel(float v) {
		if (v > 0.045) {
			return VELOCITY_LEVEL_VERY_FAST;
		} else if (v > 0.03) {
			return VELOCITY_LEVEL_FAST;
		} else {
			return VELOCITY_LEVEL_NORMAL;
		}
	}
	
	private KeyEvent getActionDownVelocityKeyEvent(int KeyCode) {
		int repeat = 0;
		if (mMoveVelocityLevel == VELOCITY_LEVEL_NORMAL) {
			repeat = 0;
		} else if (mMoveVelocityLevel == VELOCITY_LEVEL_FAST) {
			repeat = 5;
		} else if (mMoveVelocityLevel == VELOCITY_LEVEL_VERY_FAST) {
			repeat = 10;
		}
		KeyEvent ke = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyCode, repeat);
		return ke;
	}
	
	private Runnable mMoveLeftRunnable = new Runnable() {
		
		@Override
		public void run() {

			++mMoveLeftIndex;
			if (mMoveLeftIndex <= 2) {
				mHandler.postDelayed(this, INITIAL_AUTO_MOVE_WAIT_TIME);
			} else if (mMoveLeftIndex <= 6) {
				mHandler.postDelayed(this, SECOND_AUTO_MOVE_WAIT_TIME);
			} else if(mMoveLeftIndex <= 15){
				mHandler.postDelayed(this, THIRD_AUTO_MOVE_WAIT_TIME);
			} else {
				mHandler.postDelayed(this, FORTH_AUTO_MOVE_WAIT_TIME);
			}
		}
	};
	
	private Runnable mMoveRightRunnable = new Runnable() {
		
		@Override
		public void run() {

			++mMoveRightIndex;
			if (mMoveRightIndex <= 2) {
				mHandler.postDelayed(this, INITIAL_AUTO_MOVE_WAIT_TIME);
			} else if (mMoveRightIndex <= 6) {
				mHandler.postDelayed(this, SECOND_AUTO_MOVE_WAIT_TIME);
			} else if(mMoveRightIndex <= 15){
				mHandler.postDelayed(this, THIRD_AUTO_MOVE_WAIT_TIME);
			} else {
				mHandler.postDelayed(this, FORTH_AUTO_MOVE_WAIT_TIME);
			}
		}
	};
	
	private void cancelAutoMove() {
		mHandler.removeCallbacks(mMoveLeftRunnable);
		mHandler.removeCallbacks(mMoveRightRunnable);
	}

}
