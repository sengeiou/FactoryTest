package com.fengmi.usertest.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengmi.usertest.IRadioCheckListener;
import com.fengmi.usertest.PQUtil;
import com.fengmi.usertest.PicModeManagerImpl;
import com.fengmi.usertest.R;

public class PQRegulatorView extends RelativeLayout implements IRadioCheckListener {
    private static final String TAG = "PQRegulatorView";
    private PicModeManagerImpl picModeManager;

    private TextView tvPQValue;
    private TextView tvPQType;
    private Button btnPlus;
    private Button btnMinus;
    /**
     * pq type 对应 RGB gain 和 offset
     */
    private int pqType;

    public PQRegulatorView(Context context) {
        this(context, null, 0);
    }

    public PQRegulatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PQRegulatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        PQUtil.addCheckListener(this);

        picModeManager = new PicModeManagerImpl(getContext());
        initView();
        initAttr(attrs);
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PQRegulatorView);
        pqType = Integer.parseInt(typedArray.getString(R.styleable.PQRegulatorView_pq_type));
        typedArray.recycle();
        tvPQType.setText(PQ_TYPE.valueof(pqType));
        updatePQView();
    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.pq_regulator, null);
        tvPQValue = view.findViewById(R.id.tv_pq_value);
        tvPQType = view.findViewById(R.id.tv_pq_type);
        btnPlus = view.findViewById(R.id.btn_pq_plus);
        btnMinus = view.findViewById(R.id.btn_pq_minus);

        btnPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePQValue(true);
                updatePQView();
            }
        });

        btnMinus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePQValue(false);
                updatePQView();
            }
        });

        addView(view);
    }

    public void updatePQValue(boolean add) {
        if (add) {
            pqIncrease();
        } else {
            pqDecrease();
        }
    }
    public void updatePQView(){
        int val = queryPQValue(pqType);
        tvPQValue.setText(""+val);
    }

    private void pqIncrease() {
        int val = queryPQValue(pqType);
        val+= PQUtil.PQ_ADJUST_STEP;
        Log.d(TAG,"pqIncrease :: "+val);
        setPQValue(pqType, val);
    }

    private void pqDecrease() {
        int val = queryPQValue(pqType);
        val-=PQUtil.PQ_ADJUST_STEP;
        Log.d(TAG,"pqDecrease :: "+val);
        setPQValue(pqType, val);
    }

    private int queryPQValue(int type) {
        int val = -1;
        int colorTemp = picModeManager.picGetColorTemp();
        Log.d(TAG,"picGetColorTemp :: "+colorTemp);
        switch (type) {
            case 0:
                val = picModeManager.picGetPostRedGain(colorTemp);
                break;
            case 1:
                val = picModeManager.picGetPostGreenGain(colorTemp);
                break;
            case 2:
                val = picModeManager.picGetPostBlueGain(colorTemp);
                break;
            case 10:
                val = picModeManager.picGetPostRedOffset(colorTemp);
                break;
            case 11:
                val = picModeManager.picGetPostGreenOffset(colorTemp);
                break;
            case 12:
                val = picModeManager.picGetPostBlueOffset(colorTemp);
                break;
        }
        return val;
    }

    private boolean setPQValue(int type, int data) {
        int colorTemp = picModeManager.picGetColorTemp();
        Log.d(TAG,"picGetColorTemp :: "+colorTemp);
        boolean val = false;
        switch (type) {
            case 0:
                val = picModeManager.setRedGain(colorTemp, data);
                break;
            case 1:
                val = picModeManager.setGreenGain(colorTemp, data);
                break;
            case 2:
                val = picModeManager.setBlueGain(colorTemp, data);
                break;
            case 10:
                val = picModeManager.setRedOffs(colorTemp, data - 1024);
                break;
            case 11:
                val = picModeManager.setGreenOffs(colorTemp, data - 1024);
                break;
            case 12:
                val = picModeManager.setBlueOffs(colorTemp, data - 1024);
                break;
        }
        return val;
    }

    @Override
    public void onRadioChecked() {
        updatePQView();
    }

    private enum PQ_TYPE {
        RED_GAIN(0, "red gain"), GREEN_GAIN(1, "green gain"), BLUE_GAIN(2, "blue gain"),
        RED_OFF(10, "red offset"), GREEN_OFF(11, "green offset"), BLUE_OFF(12, "blue offset");
        int value;
        String desc;

        PQ_TYPE(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public static String valueof(int value) {
            String res = "unknow";
            for (PQ_TYPE pq_type : PQ_TYPE.values()) {
                if (value == pq_type.value) {
                    res = pq_type.desc;
                }
            }

            return res;
        }
    }
}
