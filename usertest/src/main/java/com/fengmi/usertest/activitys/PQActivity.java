package com.fengmi.usertest.activitys;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.droidlogic.app.KeyManager;
import com.fengmi.usertest.PicModeManagerImpl;
import com.fengmi.usertest.R;
import com.fengmi.usertest.utils.Util;

public class PQActivity extends BaseActivity {
    private static final String TAG = "PQActivity";

    private TextView tvInfo;
    private Spinner spinnerPic;
    private ImageView ivPic;

    private PicModeManagerImpl picModeManager;
    private KeyManager keyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pq);
        RadioGroup rgColorTemp = findViewById(R.id.rg_color_temp);
        RadioGroup rgColorAdjust = findViewById(R.id.rg_adjust);
        tvInfo = findViewById(R.id.tv_sn_mn);
        spinnerPic = findViewById(R.id.spinner_pic);
        ivPic = findViewById(R.id.iv_pq);

        picModeManager = new PicModeManagerImpl(this);
        keyManager = new KeyManager(this);

        rgColorTemp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
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
                switch (i) {
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

        spinnerPic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String pic = (String) parent.getAdapter().getItem(position);
                Log.d(TAG, "onItemSelected pic is === " + pic);
                updatePic(pic);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sn = keyManager.aml_key_read("assm_sn", 0);
        String mn = keyManager.aml_key_read("assm_mn", 0);
        tvInfo.setText("SN=" + sn + "\t\tMN=" + mn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Util.clearListener();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //Log.d(TAG,"getKeyCode :: " + event.getKeyCode());
        if ((event.getKeyCode() == KeyEvent.KEYCODE_MENU) && (event.getAction() == KeyEvent.ACTION_UP)) {
            boolean res = picModeManager.picTransPQDataToDB();
            Toast.makeText(this, "数据保存" + (res ? "成功！" : "失败！"), Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void updatePic(String pic) {
        int color = Color.rgb(255, 255, 255);
        switch (pic) {
            case "70IRE":
                color = Color.rgb(178, 178, 178);
                break;
            case "30IRE":
                color = Color.rgb(76, 76, 76);
                break;
            case "红":
                color = Color.rgb(255, 0, 0);
                break;
            case "绿":
                color = Color.rgb(0, 255, 0);
                break;
            case "蓝":
                color = Color.rgb(0, 0, 255);
                break;
            case "白":
                color = Color.rgb(255, 255, 255);
                break;
            case "黑":
                color = Color.rgb(0, 0, 0);
                break;
            case "灰10":
                color = Color.rgb(25, 25, 25);
                break;
        }
        ivPic.setBackgroundColor(color);
        ivPic.invalidate();
    }
}
