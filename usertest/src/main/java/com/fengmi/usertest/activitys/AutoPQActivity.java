package com.fengmi.usertest.activitys;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fengmi.usertest.R;
import com.fm.fengmicomm.usb.USB;
import com.fm.fengmicomm.usb.USBContext;
import com.fm.fengmicomm.usb.callback.CL200RxDataCallBack;
import com.fm.fengmicomm.usb.task.CL200CommTask;
import com.fm.fengmicomm.usb.task.CL200ProtocolTask;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.fengmi.usertest.utils.PQUtil.B_GAIN;
import static com.fengmi.usertest.utils.PQUtil.B_OFF;
import static com.fengmi.usertest.utils.PQUtil.R_GAIN;
import static com.fengmi.usertest.utils.PQUtil.R_OFF;
import static com.fengmi.usertest.utils.PQUtil.init;
import static com.fengmi.usertest.utils.PQUtil.resetPQ;
import static com.fengmi.usertest.utils.PQUtil.setColorTemp;
import static com.fengmi.usertest.utils.PQUtil.updatePQValue;
import static com.fm.fengmicomm.usb.USBContext.cl200CommTask;
import static com.fm.fengmicomm.usb.USBContext.cl200ProtocolTask;
import static com.fm.fengmicomm.usb.USBContext.cl200Usb;

public class AutoPQActivity extends BaseActivity implements CL200RxDataCallBack {
    //L246 标准
    // private static final String[][] PQ_STANDARD = new String[][]{
    //         //||    30IRE       ||      70IRE      ||
    //         //  x     |    y    ||    x   |    y
    //         {"0.2700", "0.2700", "0.2700", "0.2700"}, //coll
    //         {"0.2820", "0.2950", "0.2820", "0.2950"}, //normal
    //         {"0.3100", "0.3250", "0.3100", "0.3250"}, //warm
    // };
    //FM05 标准
    private static final String[][] PQ_STANDARD = new String[][]{
            //||    30IRE       ||      70IRE      ||
            //  x     |    y    ||    x   |    y
            {"0.2750", "0.2750", "0.2750", "0.2750"}, //coll
            {"0.2900", "0.3100", "0.2900", "0.3100"}, //normal
            {"0.3150", "0.3250", "0.3150", "0.3250"}, //warm
    };
    private static final BigDecimal adjust = new BigDecimal("0.003").setScale(4, RoundingMode.HALF_UP);
    private static final BigDecimal verify = new BigDecimal("0.006").setScale(4, RoundingMode.HALF_UP);
    private static volatile CL200Info cl200Info;
    private static volatile boolean newData = false;
    private static volatile boolean pqRunning = false;
    private int color;
    private ImageView ivPic;
    private PQVerifyTask pqVerifyTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_pq);
        ivPic = findViewById(R.id.iv_pq_pattern);

        cl200Info = new CL200Info();
        pqVerifyTask = new PQVerifyTask();
        USBContext.cl200RxDataCallBack = this;

        init(this);
        initCL200A(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cl200Usb != null) {
            cl200Usb.destroy(this);
        }
    }

    @Override
    public void onDataReceived(boolean valid, String Ev, String x, String y) {
        cl200Info.valid = valid;
        cl200Info.Ev = Ev;
        cl200Info.x = x;
        cl200Info.y = y;

        newData = true;
    }

    public void updatePic(String pic) {
        color = Color.rgb(255, 255, 255);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivPic.setBackgroundColor(color);
                ivPic.invalidate();
            }
        });
    }

    public void runPQ(View view) {
        if (!pqRunning) {
            if (USBContext.cl200ProtocolTask != null && USBContext.cl200CommTask != null) {
                pqVerifyTask.start();
            } else {
                Toast.makeText(this, "未检测到通讯，请确认 CL200 是否连接", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "PQ 测试进行中，请勿重复开启", Toast.LENGTH_SHORT).show();
        }

    }

    private void initCL200A(Context context) {
        cl200Usb = new USB.USBBuilder(context)
                .setBaudRate(9600)
                .setDataBits(7)
                .setParity(2)//EVEN
                .setStopBits(1)
                .setMaxReadBytes(80)
                .setVID(1027)
                .setPID(24577)
                .build();
        cl200Usb.setDevName("FT232");
        cl200Usb.setOnUsbChangeListener(new USB.OnUsbChangeListener() {
            @Override
            public void onUsbConnect() {
                if (cl200ProtocolTask == null) {
                    cl200ProtocolTask = new CL200ProtocolTask();
                    cl200ProtocolTask.initTask();
                    cl200ProtocolTask.start();
                }
                if (cl200CommTask == null) {
                    cl200CommTask = new CL200CommTask();
                    cl200CommTask.initTask();
                    cl200CommTask.start();
                }
            }

            @Override
            public void onUsbDisconnect() {
                if (cl200CommTask != null) {
                    cl200CommTask.killComm();
                    cl200CommTask = null;
                }
                if (cl200ProtocolTask != null) {
                    cl200ProtocolTask.killProtocol();
                    cl200ProtocolTask = null;
                }
            }

            @Override
            public void onUsbConnectFailed() {
                Log.d(TAG, "onUsbConnectFailed");
            }

            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "onPermissionGranted");
            }

            @Override
            public void onPermissionRefused() {
                Log.d(TAG, "onPermissionRefused");
            }

            @Override
            public void onDriverNotSupport() {
                Log.d(TAG, "onDriverNotSupport");
            }

            @Override
            public void onWriteDataFailed(String s) {
                Log.d(TAG, "onWriteDataFailed == " + s);
            }

            @Override
            public void onWriteSuccess(int i) {
                //Log.d(TAG, "onWriteSuccess");
            }
        });
        if (cl200Usb.getTargetDevice() != null) {
            cl200Usb.afterGetUsbPermission(cl200Usb.getTargetDevice());
        }
    }

    class PQVerifyTask extends Thread {
        CL200Info temp;
        int check70IRETimes = 0;
        int check30IRETimes = 0;
        boolean pqPass = false;

        @Override
        public void run() {
            pqRunning = true;
            //色温代号 0:cool,1:normal,2:warm
            for (int colorTemp = 0; colorTemp < 3; colorTemp++) {
                //set color temp
                setColorTemp(colorTemp);
                Log.d(TAG, "切换色温 ：" + colorTemp);
                SystemClock.sleep(200);
                //最多调整3次
                for (int i = 0; i < 3; i++) {
                    //切换到 70 IRE
                    switchTo70IRE();
                    //调整70IRE
                    if (adjust70IRE(colorTemp, 0)) {
                        //切换到 30 IRE
                        switchTo30IRE();
                        //校验 30 IRE
                        if (verify30IRE(colorTemp, 0)) {
                            pqPass = true;
                            break;
                        } else {
                            Log.d(TAG, "30 IRE 校验失败,次数 ：" + i);
                        }
                    } else {
                        Log.d(TAG, "70 IRE 校验失败，次数 ：" + i);
                        break;
                    }
                }
            }

            check70IRETimes = 0;
            check30IRETimes = 0;
            pqRunning = false;
            if (pqPass) {
                Log.d(TAG, "PQ 调节完成");
            } else {
                resetPQ();
            }
        }

        private void switchTo70IRE() {
            updatePic("70IRE");
        }

        private void switchTo30IRE() {
            updatePic("30IRE");
        }

        /**
         * 70IRE 调整
         *
         * @param colorTemp 色温 0:cool   1:normal    2:warm
         * @param status    case 状态
         * @return pass
         */
        private boolean adjust70IRE(int colorTemp, int status) {
            Log.d(TAG, "adjust70IRE");
            BigDecimal X_STANDARD = new BigDecimal(PQ_STANDARD[colorTemp][2]).setScale(4, RoundingMode.HALF_UP);
            BigDecimal Y_STANDARD = new BigDecimal(PQ_STANDARD[colorTemp][3]).setScale(4, RoundingMode.HALF_UP);
            switch (status) {
                case 0:
                    //调整 70IRE 下的 y 坐标
                    while (true) {
                        temp = readCl200();
                        Log.d(TAG, "Y : 70 IRE readCl200 " + temp);
                        if (temp == null) {
                            return false;
                        }
                        BigDecimal off = temp.yData.subtract(Y_STANDARD);
                        //小于标准
                        if (temp.yData.compareTo(Y_STANDARD.subtract(adjust)) < 0) {
                            Log.d(TAG, "Y : 小于标准  " + temp.yData + " 偏移量：" + off);
                            // B gain 减少
                            updatePQValue(B_GAIN, false, off.abs());
                        } else if (temp.yData.compareTo(Y_STANDARD.add(adjust)) > 0) {
                            Log.d(TAG, "Y : 大于标准  " + temp.yData + " 偏移量：" + off);
                            // B gain 增加
                            updatePQValue(B_GAIN, true, off.abs());
                        } else {
                            break;
                        }
                    }
                case 1:
                    boolean changed = false;
                    //调整 70IRE 下的 x 坐标
                    while (true) {
                        temp = readCl200();
                        Log.d(TAG, "X : 70 IRE readCl200 " + temp);
                        if (temp == null) {
                            return false;
                        }
                        BigDecimal off = temp.xData.subtract(X_STANDARD);
                        //小于标准
                        if (temp.xData.compareTo(X_STANDARD.subtract(adjust)) < 0) {
                            Log.d(TAG, " X : 小于标准  " + temp.xData + " 偏移量：" + off);
                            // R gain 增加
                            updatePQValue(R_GAIN, true, off.abs());
                            changed = true;
                        } else if (temp.xData.compareTo(X_STANDARD.add(adjust)) > 0) {
                            Log.d(TAG, "X ：大于标准  " + temp.xData + " 偏移量：" + off);
                            // R gain 减少
                            updatePQValue(R_GAIN, false, off.abs());
                            changed = true;
                        } else {
                            break;
                        }
                    }
                    if (!changed) {
                        return true;
                    } else if (check70IRETimes < 3) {
                        check70IRETimes++;
                        return adjust70IRE(colorTemp, 0);
                    } else {
                        return false;
                    }
            }
            return false;
        }

        /**
         * 校验 30IRE
         *
         * @param colorTemp 色温 0:cool   1:normal    2:warm
         * @param status    case 状态
         * @return pass
         */
        private boolean verify30IRE(int colorTemp, int status) {
            BigDecimal X_STANDARD = new BigDecimal(PQ_STANDARD[colorTemp][0]).setScale(4, RoundingMode.HALF_UP);
            BigDecimal Y_STANDARD = new BigDecimal(PQ_STANDARD[colorTemp][1]).setScale(4, RoundingMode.HALF_UP);
            switch (status) {
                case 0:
                    //校验 30IRE 下的 y 坐标
                    while (true) {
                        temp = readCl200();
                        Log.d(TAG, "Y : 30 IRE readCl200 " + temp);
                        if (temp == null) {
                            return false;
                        }
                        BigDecimal off = temp.yData.subtract(Y_STANDARD);
                        //小于标准
                        if (temp.yData.compareTo(Y_STANDARD.subtract(verify)) < 0) {
                            Log.d(TAG, "Y : 30 IRE 小于标准 " + temp.yData + " 偏移量：" + off);
                            //B off 减少
                            updatePQValue(B_OFF, false, off.abs());
                        } else if (temp.yData.compareTo(Y_STANDARD.add(verify)) > 0) {
                            Log.d(TAG, "Y : 30 IRE 大于标准 " + temp.yData + " 偏移量：" + off);
                            //B off 增加
                            updatePQValue(B_OFF, true, off.abs());
                        } else {
                            break;
                        }
                    }
                case 1:
                    boolean changed = false;
                    //校验 30IRE 下的 x 坐标
                    while (true) {
                        temp = readCl200();
                        Log.d(TAG, "X : 30 IRE readCl200 " + temp);
                        if (temp == null) {
                            return false;
                        }
                        BigDecimal off = temp.xData.subtract(X_STANDARD);
                        //小于标准
                        if (temp.xData.compareTo(X_STANDARD.subtract(verify)) < 0) {
                            Log.d(TAG, "X : 30 IRE  小于标准 " + temp.xData + " 偏移量：" + off);
                            //R off 增加
                            updatePQValue(R_OFF, true, off.abs());
                            changed = true;
                        } else if (temp.xData.compareTo(X_STANDARD.add(verify)) > 0) {
                            Log.d(TAG, "X : 30 IRE  大于标准 " + temp.xData + " 偏移量：" + off);
                            //R off 减少
                            updatePQValue(R_OFF, false, off.abs());
                            changed = true;
                        } else {
                            break;
                        }
                    }
                    if (!changed) {
                        return true;
                    } else if (check30IRETimes < 3) {
                        check30IRETimes++;
                        return verify30IRE(colorTemp, 0);
                    } else {
                        return false;
                    }
            }

            return true;
        }

        /**
         * 读取 CL200 数据
         *
         * @return CL200 Info
         */
        private CL200Info readCl200() {
            if (USBContext.cl200CommTask == null || USBContext.cl200ProtocolTask == null) {
                return null;
            }
            newData = false;
            while (!newData) {
                SystemClock.sleep(10);
            }
            if (!cl200Info.valid) {
                return readCl200();
            } else {
                String ev = cl200Info.Ev;
                String x = cl200Info.x;
                String y = cl200Info.y;
                cl200Info.evData = new BigDecimal(ev.substring(1)).setScale(2, BigDecimal.ROUND_HALF_UP);
                cl200Info.xData = new BigDecimal(x.substring(1)).setScale(4, BigDecimal.ROUND_HALF_UP);
                cl200Info.yData = new BigDecimal(y.substring(1)).setScale(4, BigDecimal.ROUND_HALF_UP);
                Log.d(TAG, cl200Info.toString());
                return cl200Info;
            }
        }
    }

    class CL200Info {
        volatile boolean valid;
        volatile String Ev;
        volatile String x;
        volatile String y;

        volatile BigDecimal evData;
        volatile BigDecimal xData;
        volatile BigDecimal yData;

        @Override
        public String toString() {
            return "CL200Info{" +
                    "valid=" + valid +
                    ", Ev='" + Ev + '\'' +
                    ", x='" + x + '\'' +
                    ", y='" + y + '\'' +
                    ", evData=" + evData +
                    ", xData=" + xData +
                    ", yData=" + yData +
                    '}';
        }
    }

}
