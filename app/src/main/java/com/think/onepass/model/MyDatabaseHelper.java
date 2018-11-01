package com.think.onepass.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.think.onepass.util.EncryptUtils.ARSAUtils;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_MAIN="create table main("
            +"id integer primary key autoincrement, "
            +"title text, "
            +"user text, "
            +"password text,"
            +"label text,"
            +"lastTime text," +
            "deleted integer," +
            "use integer)";
    private Context mContext;
    public MyDatabaseHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mContext=context;
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1:
                Cursor cursor = sqLiteDatabase.rawQuery("select * from main",null);
                while (cursor.moveToNext()){
                    String password = cursor.getString(cursor.getColumnIndex("password"));
                    if(password == null || password.equals(" ") || password.equals("")){

                    }else {
                         password = ARSAUtils.encryptDataByPublicKey(password.getBytes());
                    }
                    sqLiteDatabase.execSQL("update main set password = ? where id = ?",new Object[]{password,cursor.getInt(cursor.getColumnIndex("id"))});
                }
                break;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_MAIN);
    }

}