package com.think.onepass.suspend;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SuspendController {
    private static final String TAG = "SuspendController";
    private SuspendController(){}
    private static SuspendController mInstance=new SuspendController();
    public static SuspendController getInstance(){
        return mInstance;
    }

    public void startSuspendService(Context context){
        Log.d(TAG, "startSuspendService: ");
        Intent intent=new Intent(context,SuspendService.class);
        context.startService(intent);
    }
}
