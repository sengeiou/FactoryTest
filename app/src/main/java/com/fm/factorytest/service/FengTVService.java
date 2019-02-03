package com.fm.factorytest.service;

import android.content.Intent;
import android.os.IBinder;

import com.fm.factorytest.base.BaseCmdService;

public class FengTVService extends BaseCmdService {
    public FengTVService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
