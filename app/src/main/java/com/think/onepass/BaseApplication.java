package com.think.onepass;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.think.onepass.util.AppManager;
import com.think.onepass.view.UnlockActivity;


public class BaseApplication extends Application{
    private static final String TAG = "BaseApplication";
    private int appcount=0;
    private boolean isRunInBackground=true;
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                AppManager.addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                appcount++;
                Log.d(TAG, "onActivityStarted: "+activity.getClass().getSimpleName());
                if(isRunInBackground){
                    isRunInBackground=false;
                    if(!activity.getLocalClassName().equals("view.UnlockActivity")){
                        Intent intent=new Intent(activity, UnlockActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.d(TAG, "onActivityStopped: "+activity.getLocalClassName());
                appcount--;
                if(appcount==0){
                    isRunInBackground=true;
                }

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                AppManager.removeActivity(activity);
            }
        });
    }

}
