package com.fm.middlewareimpl.interf;

import android.content.Context;

public abstract class KeyManagerAbs extends BaseMiddleware {


    public KeyManagerAbs(Context context) {
        super(context);
    }

    public abstract boolean writeHDCP_RX_14(byte[] datas);

    public abstract boolean writeHDCP_RX_22(byte[] datas);

    public abstract boolean writeHDCP_TX_14(byte[] datas);

    public abstract boolean writeHDCP_TX_22(byte[] datas);

    public abstract byte[] readHDCP_RX_14();

    public abstract byte[] readHDCP_RX_22();

    public abstract byte[] readHDCP_TX_14();

    public abstract byte[] readHDCP_TX_22();

    public abstract boolean writeAttestationKey(byte[] datas);

    public abstract byte[] readAttestationKey();

    public abstract boolean writeWidevineKey(byte[] datas);

    public abstract byte[] readWidevineKey();

    public abstract boolean enableAllKey();

}
