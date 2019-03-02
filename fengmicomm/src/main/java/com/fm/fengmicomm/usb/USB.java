package com.fm.fengmicomm.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.usbserial.driver.UsbSerialDriver;
import com.usbserial.driver.UsbSerialPort;
import com.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * USB 管理
 * 包括 usb device 打开
 * @author lijie
 */
public class USB {
    private static final String TAG = "USB";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    /**
     * 通讯端口
     */
    private volatile UsbSerialPort port;
    /**
     * USB Connection
     */
    private volatile UsbDeviceConnection usbConn = null;
    /**
     * 上下文对象
     */
    private WeakReference<Context> ctx;
    /**
     * USB Manager
     */
    private UsbManager usbManager;
    /**
     * USB 状态监听对象
     */
    private OnUsbChangeListener onUsbChangeListener;
    /**
     * 波特率
     */
    private int BAUD_RATE;
    /**
     * 数据位
     */
    private int DATA_BITS;
    /**
     * 停止位
     */
    private int STOP_BITS;
    /**
     * 校验位
     */
    private int PARITY;
    /**
     * 缓存空间
     */
    private int MAX_READ_BYTES;
    /**
     * 是否支持 DTR
     */
    private boolean DTR = false;
    /**
     * 是否支持 RTS
     */
    private boolean RTS = false;
    /**
     * USB 拔插、授权事件监听
     */
    private final BroadcastReceiver mUsbPermissionActionReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                afterGetUsbPermission(getTargetDevice());
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                disConnectDevice();
            } else if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        //user choose YES for your previously popup window asking for grant perssion for this usb device
                        if (onUsbChangeListener != null) {
                            onUsbChangeListener.onPermissionGranted();
                        }
                        if (null != usbDevice) {
                            afterGetUsbPermission(usbDevice);
                        }
                    } else {
                        //user choose NO for your previously popup window asking for grant perssion for this usb device
                        if (onUsbChangeListener != null) {
                            onUsbChangeListener.onPermissionRefused();
                        }
                    }
                }
            }
        }
    };

    private USB(Context ctx, USBBuilder builder) {
        BAUD_RATE = builder.BAUD_RATE;
        DATA_BITS = builder.DATA_BITS;
        STOP_BITS = builder.STOP_BITS;
        PARITY = builder.PARITY;
        MAX_READ_BYTES = builder.MAX_READ_BYTES;
        DTR = builder.DTR;
        RTS = builder.RTS;

        this.ctx = new WeakReference<>(ctx);
        usbManager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
    }

    public void setOnUsbChangeListener(OnUsbChangeListener onUsbChangeListener) {
        this.onUsbChangeListener = onUsbChangeListener;
        register();
    }

    /**
     * 发送数据
     *
     * @param data         数据
     * @param timeoutMills 超时时间(ms)
     */
    public void writeData(byte[] data, int timeoutMills) {
        try {
            if (port != null && usbConn != null) {
                Log.e(TAG, "write start");
                int num = port.write(data, timeoutMills);
                Log.e(TAG, "write end");
                if (onUsbChangeListener != null) {
                    onUsbChangeListener.onWriteSuccess(num);
                }
            } else {
                if (onUsbChangeListener != null) {
                    onUsbChangeListener.onWriteDataFailed(" port == null");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (onUsbChangeListener != null) {
                onUsbChangeListener.onWriteDataFailed(e.toString());
            }
        }

    }

    /**
     * 读取端口数据
     * @param recvBuffer 数据接收
     * @param timeoutMills 超时时间
     * @return real length of read ,-1 is read error
     */
    public int readData(byte[] recvBuffer, int timeoutMills) {
        int size = -1;
        if (port != null && usbConn != null) {
            try {
                size = port.read(recvBuffer, timeoutMills);
            } catch (IOException e) {
                e.printStackTrace();
                size = -1;
            }
        }
        return size;
    }

    /**
     * 注册监听
     */
    private void register() {
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        usbFilter.addAction(ACTION_USB_PERMISSION);
        Context context = ctx.get();
        if (context != null) {
            context.registerReceiver(mUsbPermissionActionReceiver, usbFilter);
        }
    }

    /**
     * 取消注册
     */
    public void destroy() {
        Context context = ctx.get();
        if (context != null) {
            context.unregisterReceiver(mUsbPermissionActionReceiver);
        }
        disConnectDevice();
        onUsbChangeListener = null;
        ctx.clear();
        ctx = null;
    }

    /**
     * 断开连接
     */
    private void disConnectDevice() {
        if (port != null) {
            try {
                port.close();
                port = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (usbConn != null){
            usbConn.close();
            usbConn = null;
        }
        if (onUsbChangeListener != null) {
            onUsbChangeListener.onUsbDisconnect();
        }
    }

    /**
     * 请求USB设备权限
     */
    private void requestPermission() {
        Context context = ctx.get();
        if (context != null) {
            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
            //here do emulation to ask all connected usb device for permission

            for (final UsbDevice usbDevice : usbManager.getDeviceList().values()) {
                if (usbManager.hasPermission(usbDevice)) {
                    afterGetUsbPermission(usbDevice);
                } else {
                    usbManager.requestPermission(usbDevice, mPermissionIntent);
                }
            }
        }
    }

    /**
     * 打开 USB端口
     *
     * @param usbDevice usb 设备
     */
    public void afterGetUsbPermission(UsbDevice usbDevice) {
        // Find all available drivers from attached devices.
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
        if (availableDrivers.isEmpty()) {
            if (onUsbChangeListener != null) {
                onUsbChangeListener.onDriverNotSupport();
            }
            return;
        }

        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        try {
            usbConn = usbManager.openDevice(driver.getDevice());
            if (usbConn == null) {
                if (usbDevice == null) {
                    requestPermission();
                }
                return;
            }
            // Read some data! Most have just one port (port 0).
            port = driver.getPorts().get(0);
            try {
                port.open(usbConn);
                port.setParameters(BAUD_RATE, DATA_BITS, STOP_BITS, PARITY);
                port.setDTR(DTR);
                port.setRTS(RTS);
                if (onUsbChangeListener != null) {
                    onUsbChangeListener.onUsbConnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (onUsbChangeListener != null) {
                    onUsbChangeListener.onUsbConnectFailed();
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取目标端口
     *
     * @return USB device
     */
    public UsbDevice getTargetDevice() {
        UsbDevice res = null;
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            String name = device.getProductName();
            if (name != null && name.contains("CP2102")) {
                res = device;
            }
        }
        Log.d(TAG, "target device = " + res);
        return res;
    }

    public interface OnUsbChangeListener {

        void onUsbConnect();

        void onUsbDisconnect();

        void onUsbConnectFailed();

        void onPermissionGranted();

        void onPermissionRefused();

        void onDriverNotSupport();

        void onWriteDataFailed(String error);

        void onWriteSuccess(int num);

    }

    public static class USBBuilder {
        Context act;

        private int BAUD_RATE = 115200;
        private int DATA_BITS = 8;
        private int STOP_BITS = UsbSerialPort.STOPBITS_1;
        private int PARITY = UsbSerialPort.PARITY_NONE;
        private int MAX_READ_BYTES = 100;

        private boolean DTR = false;
        private boolean RTS = false;


        public USBBuilder(Context act) {
            this.act = act;
        }

        public USBBuilder setBaudRate(int baudRate) {
            this.BAUD_RATE = baudRate;
            return this;
        }

        public USBBuilder setDataBits(int dataBits) {
            this.DATA_BITS = dataBits;
            return this;
        }

        public USBBuilder setStopBits(int stopBits) {
            this.STOP_BITS = stopBits;
            return this;
        }

        public USBBuilder setParity(int parity) {
            this.PARITY = parity;
            return this;
        }

        public USBBuilder setMaxReadBytes(int maxReadBytes) {
            this.MAX_READ_BYTES = maxReadBytes;
            return this;
        }

        public USBBuilder setDTR(boolean dtr) {
            this.DTR = dtr;
            return this;
        }

        public USBBuilder setRTS(boolean rts) {
            this.RTS = rts;
            return this;
        }

        public USB build() {
            return new USB(act, this);
        }

    }
}
