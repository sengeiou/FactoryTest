package com.fengmi.usertest.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fengmi.usertest.Util;
import com.fengmi.usertest.PicModeManagerImpl;
import com.fengmi.usertest.R;

public class PQActivity extends Activity {
    private static final String TAG = "PQActivity";

    private RadioGroup rgColorTemp;
    private RadioGroup rgColorAdjust;

    private PicModeManagerImpl picModeManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pq);
        rgColorTemp = findViewById(R.id.rg_color_temp);
        rgColorAdjust = findViewById(R.id.rg_adjust);

        picModeManager = new PicModeManagerImpl(this);

        rgColorTemp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_color_cold:
                        picModeManager.picSetColorTemp(0);
                        Util.notifyListeners();
                        break;
                    case R.id.rb_color_normal:
                        picModeManager.picSetColorTemp(1);
                        Util.notifyListeners();
                        break;
                    case R.id.rb_color_warm:
                        picModeManager.picSetColorTemp(2);
                        Util.notifyListeners();
                        break;
                }
            }
        });
        rgColorAdjust.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_adjust_max:
                        Util.PQ_ADJUST_STEP = 20;
                        break;
                    case R.id.rb_adjust_min:
                        Util.PQ_ADJUST_STEP = 1;
                        break;
                }
            }
        });
        rgColorTemp.check(R.id.rb_color_normal);
        rgColorAdjust.check(R.id.rb_adjust_min);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Util.clearListener();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG,"getKeyCode :: " + event.getKeyCode());
        if ((event.getKeyCode() == KeyEvent.KEYCODE_MENU) && (event.getAction()==KeyEvent.ACTION_UP)){
            boolean res = picModeManager.picTransPQDataToDB();
            Toast.makeText(this,"数据保存" + (res?"成功！":"失败！"),Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
