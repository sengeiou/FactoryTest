package com.fm.factorytest.base;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.fm.factorytest.ICommandService;
import com.fm.factorytest.global.FactorySetting;
import com.fm.factorytest.global.TvCommandDescription;
import com.fm.middlewareimpl.global.SettingManager;
import com.fm.middlewareimpl.impl_home.AudioTestManagerImpl;
import com.fm.middlewareimpl.impl_home.InfoAccessManagerImpl;
import com.fm.middlewareimpl.impl_home.LocalPropertyManagerImpl;
import com.fm.middlewareimpl.impl_home.MediaTestManagerImpl;
import com.fm.middlewareimpl.impl_home.PicModeManagerImpl;
import com.fm.middlewareimpl.impl_home.RfNetManagerImpl;
import com.fm.middlewareimpl.impl_home.StorageManagerImpl;
import com.fm.middlewareimpl.impl_home.SysAccessManagerImpl;
import com.fm.middlewareimpl.impl_home.UtilManagerImpl;
import com.fm.middlewareimpl.interf.AudioTestManagerAbs;
import com.fm.middlewareimpl.interf.InfoAccessManagerAbs;
import com.fm.middlewareimpl.interf.LocalPropertyManagerAbs;
import com.fm.middlewareimpl.interf.MediaTestManagerAbs;
import com.fm.middlewareimpl.interf.PicModeManagerAbs;
import com.fm.middlewareimpl.interf.RfNetManagerAbs;
import com.fm.middlewareimpl.interf.StorageManagerAbs;
import com.fm.middlewareimpl.interf.SysAccessManagerAbs;
import com.fm.middlewareimpl.interf.UtilManagerAbs;

import java.io.PrintWriter;
import java.util.ArrayList;

//import android.support.v4.content.LocalBroadcastManager;


public class BaseCmdService extends Service implements CommandSource.OnCommandListener {
    protected static final String TAG = "FactoryTest";
    /**
     * ------------ onbind ------------
     */

    //TODO, add your message business
    protected static final int CHECK_COMMAND_RUNNING = 10000;
    protected static final int COMMAND_STARTRUN_TIMEOUT = 5000;
    final ArrayList<Command> mActivityRunningCmds = new ArrayList<Command>();
    public TvCommandDescription mTvCd = TvCommandDescription.getInstance();
    protected boolean TouchFlag = false;
    //init for factory middleware
    //init for factory middleware
    protected AudioTestManagerAbs mAudioImpl;
    protected InfoAccessManagerAbs mInfoImpl;
    protected LocalPropertyManagerAbs mLocalPropImpl;
    protected MediaTestManagerAbs mMediaImpl;
    protected PicModeManagerAbs mPicModeImpl;
    protected RfNetManagerAbs mRfNetImpl;
    protected SettingManager mFactorySetting;
    protected UtilManagerAbs mUtilImpl;
    protected StorageManagerAbs mStorageImpl;
    protected SettingManager mSettingManager;
    protected SysAccessManagerAbs mSysAccessImpl;

    CommandSource mCmdSource;
    PrintWriter Cmdout = null;
    Handler myHandler = new Handler() {
        public synchronized void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_COMMAND_RUNNING:
                    //after received this command 5s, system check this activity is working or no.
                    Command cmd = (Command) msg.obj;
                    if (cmd != null) {
                        synchronized (mActivityRunningCmds) {
                            if (cmd.state == Command.COMMAND_STATE_INIT && mActivityRunningCmds.indexOf(cmd) >= 0) {
                                Log.w(TAG, "start run " + cmd + " timeout");
                                try {
                                    mBinder.setResult_bool(cmd.cmdid, false);
                                } catch (RemoteException ex) {
                                    ex.printStackTrace();
                                }
                                mActivityRunningCmds.remove(cmd);
                            }
                        }
                    }
                    return;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private EventHub mEventHub;
    protected final ICommandService.Stub mBinder = new ICommandService.Stub() {

        /********************************************************
         * EVENT HUB (touch pad listener function
         * START
         * ******************************************************/
        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            Log.d(TAG, "onTransact code:" + code);
            if (code == 1000) {
                TouchFlag = mEventHub.dispatchRawEvent(data.readInt(), data.readLong(), data.readLong());
                Log.d(TAG, "onTransact TouchFlag:" + TouchFlag);
                Log.i(TAG, "meet change mode condition");
                if (mEventHub.switchModeFlag) {
                    Log.i(TAG, "change mode");
                    //mUtilImpl.systemSwitchMode();
                }
                return TouchFlag;
            }
            return super.onTransact(code, data, reply, flags);
        }
        /**------------ ontransact ------------*/
        /** EVENT HUB (touch pad listener function) */
        /**------------ ontransact ------------*/

        /* ------------- command return packing START ---------------*/
        public void setResult_string(String cmdid, String resultMsg) throws RemoteException {
            Log.i(TAG, "CommandService setResult_String: " + cmdid + " " + resultMsg);
            byte[] data = null;
            if (resultMsg != null) {
                data = resultMsg.getBytes();
            }
            mCmdSource.sendMsg(cmdid, data);
        }

        public void setResult_bool(String cmdid, boolean result) throws RemoteException {
            byte[] data = new byte[1];
            if (result) {
                data[0] = 0;
            } else {
                data[0] = 1;
            }
            mCmdSource.sendMsg(cmdid, data);
        }


        public void setResult_byte(String cmdid, byte[] resultMsg) throws RemoteException {
            mCmdSource.sendMsg(cmdid, resultMsg);
        }
        /* ------------- command return packing STOP---------------*/

        public void finishCommand(String cmdid, String param) throws RemoteException {
            removeRunningCommand(cmdid, param);
        }
    };
    //if a activity operation have two or more operations, here we should reserve
    //it after first operation for next work.
    private Command ReservedActivityCmd = null;

    /* * create the channel between commandservice and every activity.*/
    public void onCreate() {
        Log.i(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++++");
        Log.i(TAG, "FactoryTest Begin to Work!");
        Log.i(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++++");
        //TODO  some init work
        //init for factory middleware
        initMiddlewareApi();

        mCmdSource = new CommandSource(this, this);

        Context mContext = this;
        mEventHub = new EventHub(mContext);
        //close screen saver and sleep mode
        //mUtilImpl.closeScreenSave2Sleep();
        //other should use myHandler
    }
    /* ------------- communication with activity START---------------*/

    //init for factory middleware
    private void initMiddlewareApi() {
        mAudioImpl = new AudioTestManagerImpl(this);
        mInfoImpl = new InfoAccessManagerImpl(this);
        mLocalPropImpl = new LocalPropertyManagerImpl(this);
        mMediaImpl = new MediaTestManagerImpl(this);
        mPicModeImpl = new PicModeManagerImpl(this);
        mRfNetImpl = new RfNetManagerImpl(this);
        mFactorySetting = new SettingManager();
        mUtilImpl = new UtilManagerImpl(this);
        mStorageImpl = new StorageManagerImpl(this);
        mSysAccessImpl = new SysAccessManagerImpl(this);
    }

    public void onDestroy() {
        mCmdSource.finishCommandSouce();
    }

    /**
     * ------------ onbind ------------
     */
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "CommandService onBind");
        String id = arg0.getStringExtra(FactorySetting.EXTRA_CMDID);
        String para = arg0.getStringExtra(FactorySetting.EXTRA_CMDPARA);
        synchronized (mActivityRunningCmds) {
            Command c = findRunningCommandLocked(id, para);
            if (c != null) {
                c.state = Command.COMMAND_STATE_START;
            } else {
                Log.e(TAG, "onBind can not find the pending command ");
            }
        }
        return mBinder;
    }

    public void TvSetControlMsg(Command cmd, int para0, String para1, String para2) {
        if (cmd != null) {
            String action = TvCommandDescription.getFilterActionForCmd(cmd.cmdid);
            if (action != null) {
                Intent intent = new Intent(action);
                intent.putExtra(FactorySetting.EXTRA_BROADCAST_CMDPARA, cmd.param);
                intent.putExtra(FactorySetting.EXTRA_BROADCAST_CONTROLTYPE, para0);
                intent.putExtra(FactorySetting.EXTRA_BROADCAST_CONTROLID, para1);
                intent.putExtra(FactorySetting.EXTRA_BROADCAST_CONTROLPARA, para2);
                //LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                sendBroadcast(intent);
            }
        }
    }

    protected void TvhandleCommandForActivity(Command c) {
        ComponentName component = TvCommandDescription.getComponentNameForCmd(c.cmdid);
        if (component == null) {
            Log.e(TAG, "can not run activity for " + c.cmdid);
            return;
        }
        // add task running check
        myHandler.sendMessageDelayed(myHandler.obtainMessage(CHECK_COMMAND_RUNNING, c), COMMAND_STARTRUN_TIMEOUT);
        //launch task
        Intent intent = new Intent();
        intent.setComponent(component);
        intent.putExtra(FactorySetting.EXTRA_CMDID, c.cmdid);
        intent.putExtra(FactorySetting.EXTRA_CMDPARA, c.param);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    protected Command addRunningCommand(String cmdid, String param) {
        synchronized (mActivityRunningCmds) {
            Command c = findRunningCommandLocked(cmdid, param);
            if (c != null && mActivityRunningCmds.remove(c)) {
                Log.e(TAG, "there is pending command not finish, something wrong? " + c);
            }
            c = new Command(cmdid, param);
            mActivityRunningCmds.add(c);
            return c;
        }
    }

    private Command findRunningCommandLocked(String id, String para) {
        for (int i = 0; i < mActivityRunningCmds.size(); i++) {
            Command c = mActivityRunningCmds.get(i);
            if (c.match(id, para)) {
                return c;
            }
        }
        return null;
    }

    //will be called after
    protected Command removeRunningCommand(String cmdid, String param) {
        synchronized (mActivityRunningCmds) {
            Command c = findRunningCommandLocked(cmdid, param);
            if (!mActivityRunningCmds.remove(c)) {
                Log.e(TAG, "there is not pending command, something wrong? " + c);
            }
            return c;
        }
    }

    //return the matched first command
    private Command findRunningCommandById(String cmdid) {
        for (int i = 0; i < mActivityRunningCmds.size(); i++) {
            Command c = mActivityRunningCmds.get(i);
            if (c.cmdid.equals(cmdid)) {
                return c;
            }
        }
        return null;
    }

    public void setResultOuter(PrintWriter writer) {
        Cmdout = writer;
    }
    /* ------------- communication with activity STOP---------------*/

    //TODO finish your business
    public void handleCommand(String cmdid, String param) {
    }

    /* ------------- Tv Check CP210xCommand Status Start---------------*/
    public boolean checkTvWindowStatus() {
        boolean ret = false;
        synchronized (mActivityRunningCmds) {
            for (int i = 0; i < mActivityRunningCmds.size(); i++) {
                Command c = mActivityRunningCmds.get(i);
                if (mTvCd.getCmdTypeByID(c.cmdid).equals(TvCommandDescription.CMD_TYPE_ACTIVITY_ON)) {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }

    public boolean checkTvWindowInOperating(String cmdid) {
        boolean ret = false;
        if (checkTvWindowStatus() && mTvCd.getCmdTypeByID(cmdid).equals(TvCommandDescription.CMD_TYPE_ACTIVITY_ON)) {
            Log.i(TAG, "[Window]checkTvWindowStatus(): " + checkTvWindowStatus());
            Log.i(TAG, "[Window]getCmdTypeByID(" + cmdid + "): " + mTvCd.getCmdTypeByID(cmdid));
            try {
                mBinder.setResult_bool(cmdid, true);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
            ret = true;
        } else if (!checkTvWindowStatus() && mTvCd.getCmdTypeByID(cmdid).equals(TvCommandDescription.CMD_TYPE_ACTIVITY_OFF)) {
            Log.i(TAG, "[Window]checkTvWindowStatus(): " + checkTvWindowStatus());
            Log.i(TAG, "[Window]getCmdTypeByID(" + cmdid + "): " + mTvCd.getCmdTypeByID(cmdid));
            try {
                mBinder.setResult_bool(cmdid, true);
            } catch (RemoteException ex) {
                ex.printStackTrace();
                return false;
            }
            ret = true;
        }
        return ret;
    }

    public Command getTvRunningWindCmd() {
        for (int i = 0; i < mActivityRunningCmds.size(); i++) {
            Command c = mActivityRunningCmds.get(i);
            if (mTvCd.getCmdTypeByID(c.cmdid).equals(TvCommandDescription.CMD_TYPE_ACTIVITY_ON)) {
                return c;
            }
        }
        return null;
    }

    // should use command state for timeout(bind service)
    protected class Command {
        public static final int COMMAND_STATE_INIT = 1;
        public static final int COMMAND_STATE_START = 2;
        public String cmdid;
        public String param;
        int state;

        Command(String _cmdid, String _param) {
            cmdid = _cmdid;
            param = _param;
            state = COMMAND_STATE_INIT;
        }

        //should we need command running state
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Command)) return false;
            Command other = (Command) obj;
            return match(other.cmdid, other.param);
        }

        public boolean match(String _cmdid, String _param) {
            boolean sameCmdid = cmdid == null ?
                    _cmdid == null : cmdid.equals(_cmdid);
            boolean sameParam = param == null ?
                    _param == null : param.equals(_param);
            return sameCmdid && sameParam;
        }

        public String toString() {
            return "cmdid=" + cmdid + " param=" + param;
        }
    }

}
