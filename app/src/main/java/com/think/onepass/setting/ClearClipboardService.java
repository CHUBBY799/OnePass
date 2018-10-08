package com.think.onepass.setting;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

public class ClearClipboardService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        stopSelf();
        ClipboardManager clipboardManager=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText("");
        return super.onStartCommand(intent, flags, startId);
    }
    public static void setServiceAlarm(Context context){
        Intent i=new Intent(context,ClearClipboardService.class);
        PendingIntent pi=PendingIntent.getService(context,0,i,0);
        AlarmManager alarmManager=(AlarmManager)context.getSystemService(ALARM_SERVICE);
        long time= SystemClock.elapsedRealtime()+60*1000;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,time,pi);
    }
}
