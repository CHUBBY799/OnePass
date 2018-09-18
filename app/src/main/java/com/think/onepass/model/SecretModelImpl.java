package com.think.onepass.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SecretModelImpl implements SecretModel{
    private MyDatabaseHelper dbHelper;
    private Context mContext;
    private final String TABLE="main";

    public SecretModelImpl(Context context){
        mContext=context;
        dbHelper=new MyDatabaseHelper(mContext,"secret.db",null,1);
    }
    @Override
    public void addSecret(Secret secret) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        if(secret!=null) {
            values.put("user",secret.getUser());
            values.put("password",secret.getPassword());
            values.put("label",secret.getLabel());
            values.put("lastTime",secret.getLastTime());
            db.insert("main",null,values);
        }
    }

    @Override
    public List<Secret> searchSecretByLable(String lable) {
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from main where lable like ? ",new String[]{"%"+lable+"%"});
        Secret secret=null;
        List<Secret> secrets=new ArrayList<>();
        while (cursor.moveToNext()){
            int id=cursor.getInt(cursor.getColumnIndex("id"));
            String user=cursor.getString(cursor.getColumnIndex("user"));
            String password=cursor.getString(cursor.getColumnIndex("password"));
            String lastTime=cursor.getString(cursor.getColumnIndex("lastTime"));
            secret=new Secret(id,user,password,lable, lastTime);
            secrets.add(secret);
        }
        return secrets;
    }
    private final String sqlOfSecrets="select * from "+TABLE+" order by lastTime desc";
    @Override
    public List<Secret> getSecretsByLasttimeDesc() {
        List<Secret> secrets=new ArrayList<>();
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(sqlOfSecrets,null);
        while (cursor.moveToNext()){
            Secret secret=new Secret();
            secret.setId(cursor.getLong(cursor.getColumnIndex("id")));
            secret.setUser(cursor.getString(cursor.getColumnIndex("user")));
            secret.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            secret.setLabel(cursor.getString(cursor.getColumnIndex("label")));
            secret.setLastTime(cursor.getString(cursor.getColumnIndex("lastTime")));
            secrets.add(secret);
        }
        return secrets;
    }

    /*lastTime 格式 ： yyyy-MM-dd HH:mm:ss*/
    private final String sqlOfUpdate="update main set user=?,password=?,label=?,lastTime=? " +
            "where id = ?";
    @Override
    public void updateSecret(Secret secret) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        long id=secret.getId();
        Date date=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lastTime=format.format(date);
        db.execSQL(sqlOfUpdate,new Object[]{secret.getUser(),secret.getPassword(),secret.getLabel()
        ,lastTime,id});
    }

    private long returnIdOfNew(SQLiteDatabase db, String tableName){
        Cursor cursor=db.rawQuery("select last_insert_rowid() from "+tableName,null);
        int strid=0;
        if(cursor.moveToFirst()){
            strid=cursor.getInt(0);
        }
        return strid;
    }
}
