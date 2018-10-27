package com.think.onepass.util;

import android.content.ClipboardManager;
import android.content.Context;

import com.think.onepass.setting.ClearClipboardService;

public class Utils {
    public static void setClipboard(Context context,String text){
        if(SharePreferenceUtils.getAutoclear()){
            ClearClipboardService.setServiceAlarm(context,true);
        }
        ClipboardManager clipboardManager=(ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(text);
    }
    public static int dp2px(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
