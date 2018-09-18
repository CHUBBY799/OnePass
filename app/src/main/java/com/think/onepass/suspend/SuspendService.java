package com.think.onepass.suspend;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SuspendService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        initWindowData();

    }
    private void initWindowData(){
        SuspendManager.createSuspendWindow(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return null;
    }
}
