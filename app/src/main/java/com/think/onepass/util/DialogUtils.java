package com.think.onepass.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtils {


    public static void showDialog(Context context,String title,String message
            ,DialogInterface.OnClickListener positive
            ,DialogInterface.OnClickListener negative){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.setPositiveButton("OK", positive);
        dialog.setNegativeButton("Cancel",negative);
        dialog.show();
    }


}
