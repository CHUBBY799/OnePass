package com.think.onepass.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtils {
    public static Context mContext;
    public static void setContext(Context context){
        mContext=context;
    }
    public static final String PASSWORD_KEY="password";
    public static final String FILE_NAME="password";

    public static String getPassword(){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(PASSWORD_KEY,"0000");
    }
}
