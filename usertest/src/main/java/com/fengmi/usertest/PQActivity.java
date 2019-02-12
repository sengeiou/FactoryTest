package com.fengmi.usertest;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;

public class PQActivity extends Activity {
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
                        PQUtil.notifyListeners();
                        break;
                    case R.id.rb_color_normal:
                        picModeManager.picSetColorTemp(1);
                        PQUtil.notifyListeners();
                        break;
                    case R.id.rb_color_warm:
                        picModeManager.picSetColorTemp(2);
                        PQUtil.notifyListeners();
                        break;
                }
            }
        });
        rgColorAdjust.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_adjust_max:
                        PQUtil.PQ_ADJUST_STEP = 20;
                        break;
                    case R.id.rb_adjust_min:
                        PQUtil.PQ_ADJUST_STEP = 1;
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
        PQUtil.clearListener();
    }
}
