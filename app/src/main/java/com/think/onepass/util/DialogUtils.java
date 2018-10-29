package com.think.onepass.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.think.onepass.R;

public class DialogUtils {


    public static void showDialog(Context context,String title,String message
            ,DialogInterface.OnClickListener positive
            ,DialogInterface.OnClickListener negative){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.setPositiveButton(context.getString(R.string.confirm), positive);
        dialog.setNegativeButton(context.getString(R.string.cancel),negative);
        dialog.show();
    }


}
