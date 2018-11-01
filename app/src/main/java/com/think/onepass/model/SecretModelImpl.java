package com.think.onepass.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;


import com.think.onepass.util.EncryptUtils.ARSAUtils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SecretModelImpl implements SecretModel{
    private static final String TAG = "SecretModelImpl";
    private MyDatabaseHelper dbHelper;
    private Context mContext;
    private final String TABLE="main";

    public SecretModelImpl(Context context){
        mContext=context;
        dbHelper=new MyDatabaseHelper(mContext,"secret.db",null,2);
    }
    public String returnLastTimeByDate(Date date){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
    @Override
    public Map<String,Object> addSecret(Secret secret) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        String lastTime=returnLastTimeByDate(new Date());
        if(secret!=null) {
            values.put("user",secret.getUser());
            String password = secret.getPassword();
            if(password == null || password.equals(" ") || password.equals("")){
                values.put("password",password);
            }else {
                String passwordEncrypted = ARSAUtils.encryptDataByPublicKey(password.getBytes());
                values.put("password",passwordEncrypted);
            }
            if (secret.getLabel()==null){
                values.put("label","");
            }else {
                values.put("label",secret.getLabel());
            }
            values.put("lastTime",lastTime);
            values.put("title",secret.getTitle());
            values.put("deleted",0);
            values.put("use",secret.getUse());
            db.insert("main",null,values);
        }
        Map<String,Object> response=new HashMap<>();
        response.put("id",returnIdOfNew(db,"main"));
        response.put("lastTime",lastTime);
        return response;
    }

    @Override
    public List<Secret> searchSecretByKey(String key,int deleted) {
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from main where label like ? or title like ? or user like ? or password like ? and deleted = ? ",new String[]{"%"+key+"%","%"+key+"%","%"+key+"%","%"+key+"%",String.valueOf(deleted)});
        Secret secret=null;
        List<Secret> secrets=new ArrayList<>();
        while (cursor.moveToNext()){
            long id=cursor.getLong(cursor.getColumnIndex("id"));
            String user=cursor.getString(cursor.getColumnIndex("user"));
            String password=cursor.getString(cursor.getColumnIndex("password"));
            if(!(password == null || password.equals(" ")||password.equals(""))){
                password= ARSAUtils.decryptedToStrByPrivate(password);
            }
            String lastTime=cursor.getString(cursor.getColumnIndex("lastTime"));
            String title=cursor.getString(cursor.getColumnIndex("title"));
            String label=cursor.getString(cursor.getColumnIndex("label"));
            secret=new Secret(id,user,title,password,label, lastTime);
            secrets.add(secret);
        }
        return secrets;
    }
    private final String sqlOfSecrets="select * from "+TABLE+" where deleted = 0"+" order by use desc";
    @Override
    public List<Secret> getSecretsByUseDesc() {
        List<Secret> secrets=new ArrayList<>();
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(sqlOfSecrets,null);
        while (cursor.moveToNext()){
            Secret secret=new Secret();
            secret.setId(cursor.getLong(cursor.getColumnIndex("id")));
            secret.setUser(cursor.getString(cursor.getColumnIndex("user")));
            String password=cursor.getString(cursor.getColumnIndex("password"));
            if(!(password == null || password.equals(" ")||password.equals(""))){
                password= ARSAUtils.decryptedToStrByPrivate(password);
            }
            secret.setPassword(password);
            secret.setLabel(cursor.getString(cursor.getColumnIndex("label")));
            secret.setLastTime(cursor.getString(cursor.getColumnIndex("lastTime")));
            secret.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            Log.d(TAG, "getSecretsByUseDesc: "+secret.getId()+" : "+cursor.getInt(cursor.getColumnIndex("use")));
            secrets.add(secret);
        }
        return secrets;
    }

    /*lastTime 格式 ： yyyy-MM-dd HH:mm:ss*/
    private final String sqlOfUpdate="update main set title=?,user=?,password=?,label=?,lastTime=? " +
            "where id = ?";
    @Override
    public String updateSecret(Secret secret) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        long id=secret.getId();
        Date date=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lastTime=format.format(date);
        String label=secret.getLabel();
        if(label==null){
            label="";
        }
        String password = secret.getPassword();
        if(!(password == null || password.equals(" ")||password.equals(""))){
            password = ARSAUtils.encryptDataByPublicKey(password.getBytes());
        }
        db.execSQL(sqlOfUpdate,new Object[]{secret.getTitle(),secret.getUser(),password,label
        ,lastTime,id});
        return lastTime;
    }

    @Override
    public void deleteSecretById(long id) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.execSQL("delete from main where id = ?",new Object[]{id});
    }

    private long returnIdOfNew(SQLiteDatabase db, String tableName){
        Cursor cursor=db.rawQuery("select last_insert_rowid() from "+tableName,null);
        int strid=0;
        if(cursor.moveToFirst()){
            strid=cursor.getInt(0);
        }
        return strid;
    }

    @Override
    public List<String> selectAllLabel() {
        List<String> labels=new ArrayList<>();
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select distinct label from main",null);
        while (cursor.moveToNext()){
            labels.add(cursor.getString(cursor.getColumnIndex("label")));
        }
        return labels;
    }

    @Override
    public List<Secret> selectSecretByLabel(String label) {
        List<Secret> secrets = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        if (label == null) {
            cursor = db.rawQuery("select * from main where label is null", null);
        } else{
            cursor = db.rawQuery("select * from main where label = ?", new String[]{label});
        }
        while (cursor.moveToNext()){
            Secret secret=new Secret();
            secret.setId(cursor.getLong(cursor.getColumnIndex("id")));
            secret.setUser(cursor.getString(cursor.getColumnIndex("user")));
            String password=cursor.getString(cursor.getColumnIndex("password"));
            if(!(password == null || password.equals(" ")||password.equals(""))){
                password= ARSAUtils.decryptedToStrByPrivate(password);
            }
            secret.setPassword(password);
            secret.setLabel(cursor.getString(cursor.getColumnIndex("label")));
            secret.setLastTime(cursor.getString(cursor.getColumnIndex("lastTime")));
            secret.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            secrets.add(secret);
        }
        return secrets;
    }

    @Override
    public boolean isContainById(long id) {
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from main where id = ? ",new String[]{String.valueOf(id)});
        return cursor.moveToFirst();
    }

    @Override
    public void addUse(long id) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.execSQL("update main set use = use + 1 where id = ?",new Object[]{id});
    }
    @Override
    public void deleteAll(){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.execSQL("delete from main");
    }
}
