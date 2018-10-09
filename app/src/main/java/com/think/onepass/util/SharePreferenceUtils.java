package com.think.onepass.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

public class SharePreferenceUtils {
    public static Context mContext;
    public static void setContext(Context context){
        mContext=context;
    }
    public static final String PASSWORD_KEY="password";
    public static final String FILE_NAME="password";
    public static final String FIGERPRINTOPEN_KEY="openfingerprint";
    public static final String SUSPENDPASSTIME_KEY="suspendpasstime";

    public static String getPassword(){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(PASSWORD_KEY,"0000");
    }

    public static void setFigerprintopenKey(boolean open){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(FIGERPRINTOPEN_KEY,open).commit();
    }
    public static boolean getFingerprintopenKey(){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(FIGERPRINTOPEN_KEY,false);
    }
    public static void setSuspendpasstimeKey(long time){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(SUSPENDPASSTIME_KEY,time).commit();
    }
    public static long getSuspendpasstimeKey(){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getLong(SUSPENDPASSTIME_KEY,0);
    }
}
