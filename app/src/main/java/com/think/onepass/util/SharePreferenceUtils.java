package com.think.onepass.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

public class SharePreferenceUtils {
    private static Context mContext;
    public static void setContext(Context context){
        mContext=context;
    }
    private static final String PASSWORD_KEY="password";
    private static final String FILE_NAME="password";
    private static final String FIGERPRINTOPEN_KEY="openfingerprint";
    private static final String SUSPENDPASSTIME_KEY="suspendpasstime";
    private static final String SUSPENDPEMISSION_KEY="suspendpermission";
    private static final String AUTOCLEAR_KEY="autoclear";
    private static final String GUIDE_FILE_NAME = "guide";
    private static final String GUIDE_COPY_KEY = "guidecopykey";
    private static final String GUIDE_FLOAT_KEY = "guidefloatkey";
    private static final String GUIDE_MAIN_KEY = "guidemainkey";

    public static void setGuideMain(boolean show){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(GUIDE_FILE_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(GUIDE_MAIN_KEY,show).apply();
    }
    public static boolean getGuideMain(){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(GUIDE_FILE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(GUIDE_MAIN_KEY,false);
    }
    public static void setGuideCopy(boolean show){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(GUIDE_FILE_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(GUIDE_COPY_KEY,show).apply();
    }
    public static boolean getGuideCopy(){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(GUIDE_FILE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(GUIDE_COPY_KEY,false);
    }
    public static void setGuideFloat(boolean show){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(GUIDE_FILE_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(GUIDE_FLOAT_KEY,show).apply();
    }
    public static boolean getGuideFloat(){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(GUIDE_FILE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(GUIDE_FLOAT_KEY,false);
    }

    public static String getPassword(){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(PASSWORD_KEY,"0000");
    }

    public static void setFigerprintopenKey(boolean open){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(FIGERPRINTOPEN_KEY,open).apply();
    }
    public static boolean getFingerprintopenKey(){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(FIGERPRINTOPEN_KEY,false);
    }
    public static void setSuspendpasstimeKey(long time){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(SUSPENDPASSTIME_KEY,time).apply();
    }
    public static long getSuspendpasstimeKey(){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getLong(SUSPENDPASSTIME_KEY,0);
    }
    public static boolean getSuspendPemission(){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SUSPENDPEMISSION_KEY,false);
    }
    public static void setSuspendPemission(boolean pemission){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SUSPENDPEMISSION_KEY,pemission).apply();
    }
    public static boolean getAutoclear(){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(AUTOCLEAR_KEY,false);
    }
    public static void setAutoclearKey(boolean autoclearKey){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(AUTOCLEAR_KEY,autoclearKey).apply();
    }
}
