package com.ckt.d22400.sensortest_lp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PSensorTestService extends Service {
    public PSensorTestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
