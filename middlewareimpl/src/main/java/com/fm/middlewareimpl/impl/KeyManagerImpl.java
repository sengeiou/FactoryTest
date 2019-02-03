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

public class KeyManagerImpl extends KeyManagerAbs {
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

    @Override
    public boolean writeHDCP_RX_14(byte[] datas) {
        return false;
    }

    @Override
    public boolean writeHDCP_RX_22(byte[] datas) {
        return false;
    }

    @Override
    public boolean writeHDCP_TX_14(byte[] datas) {
        return false;
    }

    @Override
    public boolean writeHDCP_TX_22(byte[] datas) {
        return false;
    }

    @Override
    public byte[] readHDCP_RX_14() {
        return new byte[0];
    }

    @Override
    public byte[] readHDCP_RX_22() {
        return new byte[0];
    }

    @Override
    public byte[] readHDCP_TX_14() {
        return new byte[0];
    }

    @Override
    public byte[] readHDCP_TX_22() {
        return new byte[0];
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
        return false;
    }

    private void writeKey(byte[] datas,String keyName) {
        systemControlManager.writeSysFs(ATTACH,ATTACH_ON);
        systemControlManager.writeSysFs(NAME,keyName);
        systemControlManager.writeSysFs(WRITE,new String(datas));
    }
    private boolean setHdcpKey(String key, String path, int len){
        boolean ret = false;
        byte key_byte[] = new byte[len];
        String keys[] = key.split(",");
        Log.e(TAG, "standard key length : " + len + "param key length : "+keys.length);
        if(keys.length > len){
            for (int i = 0; i < keys.length; i++) {
                Log.e(TAG, "param key ["+i+"] : "+keys[i]);
            }
            Log.e(TAG, key);
            return false;
        }
        for(int i=0;i<keys.length;i++){
            key_byte[i] = (byte) Integer.parseInt(keys[i]);
        }
        File hdcpfile = new File(path);
        if(!hdcpfile.exists()){
            try {
                hdcpfile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Error: "+e.getMessage());
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fstream = new FileOutputStream(hdcpfile);
            fstream.write(key_byte,0,len);
            fstream.getFD().sync();
            fstream.close();
            hdcpfile.setReadable(true,false);
            Log.e(TAG, "Set Hdcp Key: OK");
            ret = true;
        } catch (IOException e) {
            Log.e(TAG, "Error: "+e.getMessage());
            e.printStackTrace();
        }
        return ret;
    }
    private byte[] getHdcpKey(String path, int len){
        byte[] key = new byte[len];
        Log.i(TAG, "Get Hdcp Key");
        File hdcpfile = new File(path);
        if (hdcpfile.exists()) {
            try {
                FileInputStream fstream = new FileInputStream(hdcpfile);
                int ret = fstream.read(key,0,len);
                fstream.close();
                Log.e(TAG, "Get Hdcp Key: get " + ret + "bytes ");
            } catch (IOException e) {
                Log.e(TAG, "Error: "+e.getMessage());
                e.printStackTrace();
            }
        }
        return key;
    }


}
