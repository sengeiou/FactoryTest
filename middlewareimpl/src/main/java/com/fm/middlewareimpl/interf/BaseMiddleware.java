package com.fm.middlewareimpl.interf;

import android.content.Context;

public abstract class BaseMiddleware {
    protected Context context;
    public static final String TAG = "FactoryMiddleware";

    public BaseMiddleware(Context context) {
        this.context = context;
    }
}
