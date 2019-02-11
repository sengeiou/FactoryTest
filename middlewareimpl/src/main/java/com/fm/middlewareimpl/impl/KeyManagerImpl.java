package com.fm.middlewareimpl.impl;

import android.content.Context;
import android.util.Log;

import com.droidlogic.app.KeyManager;
import com.droidlogic.app.SystemControlManager;
import com.fm.middlewareimpl.interf.KeyManagerAbs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.fm.middlewareimpl.global.DataTypeUtil.bytesToInt;

public class KeyManagerImpl extends KeyManagerAbs {

    private static final String HDCP_14_FILEPATH = "/persist/hdcp14_key.bin";
    private static final String HDCP_20_FILEPATH = "/persist/hdcp22_key.bin";
    private static final String HDCP_14_TX_FILEPATH = "/persist/hdcp14_txkey.bin";
    private static final String HDCP_22_TX_FILEPATH = "/persist/hdcp22_txkey.bin";

    private static final int HDCP_14_RX_LEN = 348;
    private static final int HDCP_14_TX_LEN = 288;
    private static final int HDCP_20_RX_LEN = 3192;
    private static final int HDCP_20_TX_LEN = 32;

    private static final String HDCP_14_NAME = "hdcp14_rx";
    private static final String HDCP_14_TX_NAME = "hdcp";
    private static final String HDCP_22_TX_NAME = "hdcp22_fw_private";

    private static final String ATTACH = "/sys/class/unifykeys/attach";
    private static final String NAME = "/sys/class/unifykeys/name";
    private static final String WRITE = "/sys/class/unifykeys/write";
    private static final String READ = "/sys/class/unifykeys/read";
    private static final String ATTACH_ON = "0";
    private static final String ATTACH_OFF = "1";

    private KeyManager keyManager;
    private SystemControlManager systemControlManager;

    public KeyManagerImpl(Context context) {
        super(context);
        keyManager = new KeyManager(context);
        systemControlManager = new SystemControlManager(context);
    }

    /**
     * 数组拷贝
     */
    private static int arrayCopy(byte[] src, byte[] dst, int curPos) {
        System.arraycopy(src, curPos, dst, 0, dst.length);
        curPos = curPos + dst.length;
        return curPos;
    }

    @Override
    public boolean writeHDCP_RX_14(String datas) {
        return setHdcpKey(datas, HDCP_14_FILEPATH, HDCP_14_RX_LEN);
    }

    @Override
    public boolean writeHDCP_RX_22(String datas) {
        return setHdcpKey(datas, HDCP_20_FILEPATH, HDCP_20_RX_LEN);
    }

    @Override
    public boolean writeHDCP_TX_14(String datas) {
        return setHdcpKey(datas, HDCP_14_TX_FILEPATH, HDCP_14_TX_LEN);
    }

    @Override
    public boolean writeHDCP_TX_22(String datas) {
        return setHdcpKey(datas, HDCP_22_TX_FILEPATH, HDCP_20_TX_LEN);
    }

    @Override
    public byte[] readHDCP_RX_14() {
        return getHdcpKey(HDCP_14_FILEPATH, HDCP_14_RX_LEN);
    }

    @Override
    public byte[] readHDCP_RX_22() {
        return getHdcpKey(HDCP_20_FILEPATH, HDCP_20_RX_LEN);
    }

    @Override
    public byte[] readHDCP_TX_14() {
        return getHdcpKey(HDCP_14_TX_FILEPATH, HDCP_14_TX_LEN);
    }

    @Override
    public byte[] readHDCP_TX_22() {
        return getHdcpKey(HDCP_22_TX_FILEPATH, HDCP_20_TX_LEN);
    }

    @Override
    public boolean writeAttestationKey(byte[] datas) {
        return false;
    }

    @Override
    public byte[] readAttestationKey() {
        return new byte[0];
    }

    @Override
    public boolean writeWidevineKey(byte[] datas) {
        return false;
    }

    @Override
    public byte[] readWidevineKey() {
        return new byte[0];
    }

    @Override
    public boolean enableAllKey() {
        boolean ret = false;
        setAllKeyToDTS();
        ret = true;
        return ret;
    }

    private void setAllKeyToDTS() {
        byte[] hdcp14rx = getHdcpKey(HDCP_14_FILEPATH, HDCP_14_RX_LEN);

        //key attach on
        systemControlManager.writeSysFs(ATTACH, ATTACH_ON);
        //hdcp 1.4 RX write
        setDataToDTS(hdcp14rx, HDCP_14_NAME);
        //hdcp 2.2 RX write
        try {
            setImgPath(HDCP_20_FILEPATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        systemControlManager.writeSysFs("/sys/class/hdmirx/hdmirx0/debug", "load22key");
    }

    private void setDataToDTS(byte[] datas, String keyName) {
        systemControlManager.writeSysFs(NAME, keyName);
        systemControlManager.writeSysFs(WRITE, new String(datas));
    }

    private boolean setHdcpKey(String key, String path, int len) {
        boolean ret = false;
        byte key_byte[] = new byte[len];
        String keys[] = key.split(",");
        Log.e(TAG, "standard key length : " + len + "param key length : " + keys.length);
        if (keys.length > len) {
            for (int i = 0; i < keys.length; i++) {
                Log.e(TAG, "param key [" + i + "] : " + keys[i]);
            }
            Log.e(TAG, key);
            return false;
        }
        for (int i = 0; i < keys.length; i++) {
            key_byte[i] = (byte) Integer.parseInt(keys[i]);
        }
        File hdcpfile = new File(path);
        if (!hdcpfile.exists()) {
            try {
                hdcpfile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fstream = new FileOutputStream(hdcpfile);
            fstream.write(key_byte, 0, len);
            fstream.getFD().sync();
            fstream.close();
            hdcpfile.setReadable(true, false);
            Log.e(TAG, "Set Hdcp Key: OK");
            ret = true;
        } catch (IOException e) {
            Log.e(TAG, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return ret;
    }

    private byte[] getHdcpKey(String path, int len) {
        byte[] key = new byte[len];
        Log.i(TAG, "Get Hdcp Key");
        File hdcpfile = new File(path);
        if (hdcpfile.exists()) {
            try {
                FileInputStream fstream = new FileInputStream(hdcpfile);
                int ret = fstream.read(key, 0, len);
                fstream.close();
                Log.e(TAG, "Get Hdcp Key: get " + ret + "bytes ");
            } catch (IOException e) {
                Log.e(TAG, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return key;
    }

    /**
     * hdcp 2.2 RX 写入
     * @param hdcp22Path hdcp 2.2 file path
     * @throws IOException
     */
    private void setImgPath(String hdcp22Path) throws IOException {
        byte[] readBuf;
        File file = new File(hdcp22Path);
        AmlResImgHead imgHead;

        if (!file.exists()) {
            Log.e(TAG, "setImgPath :: no file exist");
            return;
        }
        InputStream is = new FileInputStream(file);
        int len = is.available();
        if (len > 0) {
            readBuf = new byte[len];
            is.read(readBuf);

            imgHead = new AmlResImgHead(readBuf);
            //imgHead.showInfo();
            for (int i = 0; i < imgHead.itemHeadList.size(); i++) {
                AmlResItemHead item = imgHead.itemHeadList.get(i);
                item.itemSplit(readBuf);
            }
        } else {
            Log.d(TAG, "file read error");
        }
    }

    /**
     * key 文件item描述类（byte 转 int 低位在前）
     */
    private class AmlResItemHead {
        /**
         * key item 文件大小
         */
        byte[] totalSz = new byte[4];
        /**
         * key item 数据长度
         */
        byte[] dataSz = new byte[4];
        /**
         * key item 在 key 文件中的偏移位置
         */
        byte[] dataOffset = new byte[4];
        /**
         * key item 类型
         */
        byte[] type = new byte[1];
        /**
         * key item compression type
         */
        byte[] comp = new byte[1];
        /**
         * reserve
         */
        byte[] reserv = new byte[2];
        /**
         * key item name
         */
        byte[] name = new byte[32];

        public AmlResItemHead(byte[] datas, int startPos) {
            initItem(datas, startPos);
        }

        /**
         * 初始化
         *
         * @param datas    完整的 key 文件字节数据
         * @param startPos key item 起始位置
         */
        private void initItem(byte[] datas, int startPos) {
            int curPos = startPos;
            curPos = arrayCopy(datas, totalSz, curPos);
            curPos = arrayCopy(datas, dataSz, curPos);
            curPos = arrayCopy(datas, dataOffset, curPos);
            curPos = arrayCopy(datas, type, curPos);
            curPos = arrayCopy(datas, comp, curPos);
            curPos = arrayCopy(datas, reserv, curPos);
            curPos = arrayCopy(datas, name, curPos);
        }

        void show() {
            Log.d(TAG, "total Size == " + Arrays.toString(totalSz));
            Log.d(TAG, "data size == " + Arrays.toString(dataSz));
            Log.d(TAG, "data offset == " + Arrays.toString(dataOffset));
            Log.d(TAG, "type == " + Arrays.toString(type));
            Log.d(TAG, "comp == " + Arrays.toString(comp));
            Log.d(TAG, "reserv == " + Arrays.toString(reserv));
            Log.d(TAG, "name == " + Arrays.toString(name) + new String(name));
        }

        String getName() {
            return new String(name);
        }

        int getDataSize() {
            return bytesToInt(dataSz, 0);
        }

        int getTotalSize() {
            return bytesToInt(totalSz, 0);
        }

        int getDataOffset() {
            return bytesToInt(dataOffset, 0);
        }

        /**
         * key item 分类写入
         *
         * @param datas 完整的 key 文件字节数据
         */
        void itemSplit(byte[] datas) {
            byte[] buffer = new byte[getDataSize() + 4];
            String keyName = null;
            int off = getDataOffset();
            int size = getDataSize();
            if ((off + size) > datas.length) {
                Log.d(TAG, "item split error, off + size > data len");
            }

            String temp = getName();
            System.arraycopy(datas, off, buffer, 0, size);

            if (temp.contains("hdcp22_rx_private")) {
                buffer = hdcp2DataEncryption(getDataSize(), buffer);
                keyName = "hdcp22_rx_private";
            } else if (temp.contains("hdcp2_rx")) {
                keyName = "hdcp2_rx";
            } else if (temp.contains("extractedKey")) {
                keyName = "hdcp22_rx_fw";
            }

            Log.d(TAG, keyName + " | " + Arrays.toString(buffer));
            // TODO: 2019-02-11 write key here
        }

        private byte generateDataChange(byte input) {
            byte result = 0;
            for (int i = 0; i < 8; i++) {
                if ((input & (1 << i)) != 0) {
                    result |= (1 << (7 - i));
                } else {
                    result &= ~(1 << (7 - i));
                }
            }
            return result;
        }

        private byte[] hdcp2DataEncryption(int len, byte[] in) {
            for (int i = 0; i < len; i++) {
                in[i] = generateDataChange(in[i]);
            }
            return in;
        }
    }

    /**
     * hdcp 22 key 文件头描述类 （byte 转 int 低位在前）
     */
    private class AmlResImgHead {
        /**
         * CRC 校验值
         */
        byte[] crc = new byte[4];
        /**
         * KEY 文件版本
         */
        byte[] version = new byte[4];
        /**
         * 魔数
         */
        byte[] magic = new byte[8];
        /**
         * key 文件大小
         */
        byte[] imgSz = new byte[4];
        /**
         * key item 数量
         */
        byte[] imgItemNum = new byte[4];
        /**
         * key item 对象集合
         */
        List<AmlResItemHead> itemHeadList = new ArrayList<>();

        public AmlResImgHead(byte[] datas) {
            if (datas.length >= 24) {
                initImg(datas);
                initItem(datas);
            } else {
                Log.d(TAG, "data len < 24 :: " + datas.length);
            }
        }

        /**
         * 初始化 key 文件头
         *
         * @param datas
         */
        private void initImg(byte[] datas) {
            int curPos = 0;
            curPos = arrayCopy(datas, crc, curPos);
            curPos = arrayCopy(datas, version, curPos);
            curPos = arrayCopy(datas, magic, curPos);
            curPos = arrayCopy(datas, imgSz, curPos);
            curPos = arrayCopy(datas, imgItemNum, curPos);
        }

        /**
         * 初始化 key item
         *
         * @param datas key文件字节数组
         */
        private void initItem(byte[] datas) {
            int itemNum = bytesToInt(imgItemNum, 0);
            int startPos = 24;
            AmlResItemHead amlItem;
            for (int i = 0; i < itemNum; i++) {
                amlItem = new AmlResItemHead(datas, startPos);
                //amlItem.show();
                itemHeadList.add(amlItem);
                // 48 指的是 key item 的字节长度
                startPos += 48;
            }
        }

        /**
         * 打印 key 文件头信息
         */
        void showInfo() {
            Log.d(TAG, "CRC " + Arrays.toString(crc) + bytesToInt(crc, 0));
            Log.d(TAG, "Version " + Arrays.toString(version) + bytesToInt(version, 0));
            Log.d(TAG, "magic " + Arrays.toString(magic) + new String(magic));
            Log.d(TAG, "imgSize " + Arrays.toString(imgSz) + bytesToInt(imgSz, 0));
            Log.d(TAG, "imgItemNum " + Arrays.toString(imgItemNum) + bytesToInt(imgItemNum, 0));
        }
    }

}
