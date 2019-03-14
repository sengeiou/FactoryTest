package com.fengmi.usertest.activitys;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.fengmi.usertest.R;
import com.fengmi.usertest.utils.QRCodeUtil;
import com.fengmi.usertest.utils.SPUtils;

public class ResultActivity extends BaseActivity {
    private ImageView imageView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        imageView = findViewById(R.id.iv_code_te);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Bitmap bit = QRCodeUtil.createQRcodeImage(queryPQResult(), 500, 500);
                imageView.setImageBitmap(bit);
            }
        }, 1000);
    }

    private String queryPQResult() {
        StringBuilder result = new StringBuilder();
        result.append("C3:").append(SPUtils.getParam(this, SPUtils.PQ_COLD_30, "null")).append("\n")
                .append("C7:").append(SPUtils.getParam(this, SPUtils.PQ_COLD_70, "null")).append("\n")
                .append("N3:").append(SPUtils.getParam(this, SPUtils.PQ_NORMAL_30, "null")).append("\n")
                .append("N7:").append(SPUtils.getParam(this, SPUtils.PQ_NORMAL_70, "null")).append("\n")
                .append("W3:").append(SPUtils.getParam(this, SPUtils.PQ_WARM_30, "null")).append("\n")
                .append("W7:").append(SPUtils.getParam(this, SPUtils.PQ_WARM_70, "null"));

        return result.toString();
    }

}
