package com.think.onepass.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class SecretModelImpl implements SecretModel{
    private MyDatabaseHelper dbHelper;
    private Context mContext;

    SecretModelImpl(Context context){
        mContext=context;
        dbHelper=new MyDatabaseHelper(mContext,"secret.db",null,1);
    }
    @Override
    public void addSecret(Secret secret) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        if(secret!=null) {
            values.put("user",secret.getUser());
            if(!secret.getPassword().equals("")){
                values.put("password",secret.getPassword());
            }
            values.put("label",secret.getLabel());
            String lastTime=secret.getLastTime().toString();
            values.put("lastTime",lastTime);
            db.insert("main",null,values);
        }
    }

    @Override
    public List<Secret> searchSecretByLable(String lable) {
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from main where lable=?",new String[]{lable});
        Secret secret=null;
        List<Secret> secrets=new ArrayList<>();
        while (cursor.moveToNext()){
            int id=cursor.getInt(cursor.getColumnIndex("id"));
            String user=cursor.getString(cursor.getColumnIndex("user"));
            String password=cursor.getString(cursor.getColumnIndex("password"));
            String lastTime=cursor.getString(cursor.getColumnIndex("lastTime"));
            secret=new Secret(id,user,password,lable, Date.valueOf(lastTime));
            secrets.add(secret);
        }
        return secrets;
    }
}
