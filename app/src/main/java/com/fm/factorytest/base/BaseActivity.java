package com.fm.factorytest.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import com.fm.factorytest.ICommandService;
import com.fm.factorytest.global.BoxCommandDescription;
import com.fm.factorytest.global.FactorySetting;
import com.fm.factorytest.global.TvCommandDescription;
import com.fm.factorytest.service.CommandService;

public class BaseActivity extends Activity {
    static final String TAG = "FactoryBaseActivity";
    public static final boolean PASS = true;
    public static final boolean FAIL = false;

    private String mCaseName = null;
    private String mCaseId = null;
    private String mCasePara = null;
    public static ICommandService sService = null;
    public static final String NON_INNACTIVITYCMD = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //pic distortion
        getWindow().setFormat(PixelFormat.RGBA_8888);

        String action = null;
        Log.d(TAG, "FactoryActivity onCreate");
        Intent intent = getIntent();
        mCaseId = intent.getStringExtra(FactorySetting.EXTRA_CMDID);
        mCasePara = intent.getStringExtra(FactorySetting.EXTRA_CMDPARA);
        if (mCaseId == null) {
            Log.e(TAG, "not support cmd name:" + mCaseId + " param:" + mCasePara);
            finish();
            return;
        }
        mCaseName = getLocalClassName();
        Log.i(TAG, "caseId:" + mCaseId + " para:" + mCasePara + " activity:" + mCaseName);

        if (mCaseId.substring(0, 1).equals(FactorySetting.COMMAND_PRODUCT_TYPE_TV)) {
            Log.i(TAG, "product: TV Type");
            action = TvCommandDescription.getFilterActionForCmd(mCaseId);
        } else if (mCaseId.substring(0, 1).equals(FactorySetting.COMMAND_PRODUCT_TYPE_BOX)) {
            Log.i(TAG, "product: Box Type");
            action = BoxCommandDescription.getFilterActionForCmd(mCaseId);
        } else {
            Log.e(TAG, "no this product type");
        }

        Intent serviceIntent = new Intent(this, CommandService.class);
        serviceIntent.setAction(action);
        //type just tell service: they are not the same one
        // but not impact our getFilterActionForCmd
        // fix bug: CommandService can not receive the second same activity instance onBind
        serviceIntent.setType(String.valueOf(this.hashCode()));
        serviceIntent.putExtra(FactorySetting.EXTRA_CMDID, mCaseId);
        serviceIntent.putExtra(FactorySetting.EXTRA_CMDPARA, mCasePara);

        if (!bindService(serviceIntent, mConnection,
                Context.BIND_AUTO_CREATE)) {
            Log.e(TAG, "bind to Service failed");
            finish();
            return;
        }
        if (action != null) {
            IntentFilter filter = new IntentFilter(action);
            //LocalBroadcastManager.getInstance(this).registerReceiver(commandReceiver, filter);
            registerReceiver(commandReceiver, filter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        Log.i(TAG, "555555555555");
        if (sService != null) {
            unbindService(mConnection);
        }
        Log.i(TAG, "4444444444444");
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(commandReceiver);
        unregisterReceiver(commandReceiver);
        Log.i(TAG, "aaaaaaaa4444444444444");
    }

    public void setGreenText(TextView view, String text) {
        view.setTextColor(Color.GREEN);
        view.setText(text);
    }

    public void setRedText(TextView view, String text) {
        view.setTextColor(Color.RED);
        view.setText(text);
    }

    private static final int RUN_TASK = 20000;

    Handler mHandler = new Handler() {
        @Override
        public synchronized void handleMessage(Message msg) {
            if (msg.what == RUN_TASK) {
                handleCommand(mCaseId, mCasePara);
                return;
            }
            super.handleMessage(msg);
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use
            // to call on the service
            sService = ICommandService.Stub.asInterface(service);
            Log.i(TAG, "Service connected Activity:" + mCaseName);
            mHandler.sendEmptyMessage(RUN_TASK);
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "Service has unexpectedly disconnected");
            sService = null;
        }
    };

    private BroadcastReceiver commandReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            //still use original case id when communicate with command service
            String action = intent.getAction();
            String filter = null;
            if (mCaseId.substring(0, 1).equals(FactorySetting.COMMAND_PRODUCT_TYPE_TV)) {
                Log.i(TAG, "product: TV Type for filter");
                filter = TvCommandDescription.getFilterActionForCmd(mCaseId);
            } else if (mCaseId.substring(0, 1).equals(FactorySetting.COMMAND_PRODUCT_TYPE_BOX)) {
                Log.i(TAG, "product: Box Type for filter");
                filter = BoxCommandDescription.getFilterActionForCmd(mCaseId);
            } else {
                Log.e(TAG, "no this product type");
            }
            Log.i(TAG, "11111");
            if (action != null && action.equals(filter)) {
                Log.i(TAG, "222222222");
                String param = intent.getStringExtra(FactorySetting.EXTRA_BROADCAST_CMDPARA);
                boolean sameParam = param == null ?
                        mCasePara == null : param.equals(mCasePara);
                if (!sameParam) return;
                Log.i(TAG, "33333333");
                int cmdtype = intent.getIntExtra(FactorySetting.EXTRA_BROADCAST_CONTROLTYPE, FactorySetting.COMMAND_TASK_BUSINESS);
                String cmdid = intent.getStringExtra(FactorySetting.EXTRA_BROADCAST_CONTROLID);
                String cmdpara = intent.getStringExtra(FactorySetting.EXTRA_BROADCAST_CONTROLPARA);
                Log.i(TAG, "call handleControlMsg para0:" + cmdtype + " para1:" + cmdid + " para2:" + cmdpara);
                handleControlMsg(cmdtype, cmdid, cmdpara);
            }
        }
    };

    /* ------------------- set result with id start------------------*/
    public void setResult(String id, String msg) {
        setResult(id, msg, true);
    }

    public void setResult(String id, byte[] msg) {
        setResult(id, msg, true);
    }

    public void setResult(String id, boolean result) {
        setResult(id, result, true);
    }

    public void setResult(String id, String msg, boolean finish) {
        try {
            if (sService != null) {
                if (!msg.equals("noresponse"))
                    sService.setResult_string((id == null ? mCaseId : id), msg);
                if (finish) {
                    sService.finishCommand(mCaseId, mCasePara);
                }
            }
        } catch (RemoteException re) {
            re.printStackTrace();
        }
        if (finish) {
            finish();
        }
    }

    public void setResult(String id, byte[] msg, boolean finish) {
        try {
            if (sService != null) {
                sService.setResult_byte((id == null ? mCaseId : id), msg);
                if (finish) {
                    sService.finishCommand(mCaseId, mCasePara);
                }
            }
        } catch (RemoteException re) {
            re.printStackTrace();
        }
        if (finish) {
            finish();
        }
    }

    public void setResult(String id, boolean result, boolean finish) {
        Log.i(TAG, "88888888887777777776666666555555555555");
        try {
            if (sService != null) {
                sService.setResult_bool((id == null ? mCaseId : id), result);
                if (finish) {
                    sService.finishCommand(mCaseId, mCasePara);
                }
            }
        } catch (RemoteException re) {
            re.printStackTrace();
        }
        Log.i(TAG, "99999999988888888887777777776666666555555555555");
        if (finish) {
            finish();
        }
    }
    /* ------------------- set result with id stop------------------*/

    //all subclass should inherit them,mean original task command
    public void handleCommand(String cmdid, String param) {
        Log.d(TAG, "handle command " + cmdid + " para=" + param);
    }

    public void handleControlMsg(int cmdtype, String cmdid, String cmdpara) {
    }
}
