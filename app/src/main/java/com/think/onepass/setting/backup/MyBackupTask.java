package com.think.onepass.setting.backup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.think.onepass.R;
import com.think.onepass.model.Secret;
import com.think.onepass.model.SecretModelImpl;
import com.think.onepass.util.EncryptUtils.ARSAUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MyBackupTask extends AsyncTask<String, Void, Integer> {
    private static final String TAG = "MyBackupTask";
    private Context mContext;
    public static final String BACKUP = "backup";
    public static final String RESTORE = "RESTORE";

    public static final int BACKUP_SUCCESS = 1;
    public static final int BACKUP_FAILED = 0;
    public static final int RESTORE_SUCCESS = 3;
    public static final int RESTORE_FAILED = 2;
    public MyBackupTask(Context context){
        mContext = context;
    }
    @Override
    protected Integer doInBackground(String... strings) {
        String command = strings[0];
        if(command.equals(BACKUP)){
            try{
                return doSerializable();
            }catch (IOException e){
                e.printStackTrace();
            }
        }else if(command.equals(RESTORE)){
            return doAntiSerialiable();
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        if(integer == BACKUP_SUCCESS){
            Toast.makeText(mContext,R.string.data_backup_success,Toast.LENGTH_SHORT).show();
        }else if(integer == BACKUP_FAILED){
            Toast.makeText(mContext,R.string.data_backup_failed,Toast.LENGTH_SHORT).show();
        }else if(integer == RESTORE_SUCCESS){
            Toast.makeText(mContext,R.string.data_store_success,Toast.LENGTH_SHORT).show();
        }else if(integer == RESTORE_FAILED){
            Toast.makeText(mContext,R.string.data_backup_failed,Toast.LENGTH_SHORT).show();
        }
    }

    private int doSerializable() throws IOException{
        MyBackUp myBackUp = new MyBackUp();
        SharedPreferences sp = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String password = sp.getString("password","1111");
        sp = mContext.getSharedPreferences(ARSAUtils.SP_NAME,Context.MODE_PRIVATE);
        String publicKey = sp.getString(ARSAUtils.PUBLIC_KEY,"none");
        String privateKey = sp.getString(ARSAUtils.PRIVATE_KEY,"none");
        SecretModelImpl model = new SecretModelImpl(mContext);
        ArrayList<Secret> secrets = (ArrayList<Secret>)model.getSecretsByUseDesc();
        myBackUp.setPassword(password);
        myBackUp.setPublicKey(publicKey);
        myBackUp.setPrivateKey(privateKey);
        myBackUp.setSecrets(secrets);
        File extDir = Environment.getExternalStorageDirectory();
        String filename = "tempFile.txt";
        File fullFilename = new File(extDir, filename);
        try{
            fullFilename.createNewFile();
            fullFilename.setWritable(Boolean.TRUE);
            fullFilename.setReadable(Boolean.TRUE);
        }catch (IOException e){
            e.printStackTrace();
            return BACKUP_FAILED;
        }
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(fullFilename.getAbsoluteFile()));
            oos.writeObject(myBackUp);
        }catch (IOException e){
            e.printStackTrace();
            return BACKUP_FAILED;
        }finally {
//            oos.close();
        }
        return BACKUP_SUCCESS;
    }

    private int doAntiSerialiable(){
        File extDir = Environment.getExternalStorageDirectory();
        String filename = "tempFile.txt";
        File fullFilename = new File(extDir, filename);
        fullFilename.setWritable(Boolean.TRUE);
        fullFilename.setReadable(Boolean.TRUE);
        ObjectInputStream ois = null;
        try{
            ois = new ObjectInputStream(new FileInputStream(fullFilename.getAbsoluteFile()));
            MyBackUp myBackUp = (MyBackUp)ois.readObject();
            Log.d(TAG, "doAntiSerialiable: "+myBackUp.getPassword()+" "+myBackUp.getSecrets().get(0).getUser());
            SharedPreferences sp = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
            String password = sp.getString("password","1111");
            sp.edit().putString("password",myBackUp.getPassword()).commit();
            sp = mContext.getSharedPreferences(ARSAUtils.SP_NAME,Context.MODE_PRIVATE);
            sp.edit().putString(ARSAUtils.PUBLIC_KEY,myBackUp.getPublicKey())
                    .putString(ARSAUtils.PRIVATE_KEY,myBackUp.getPrivateKey())
                    .commit();
            SecretModelImpl model = new SecretModelImpl(mContext);
            model.deleteAll();
            List<Secret> secrets = myBackUp.getSecrets();
            for(Secret secret : secrets){
                model.addSecret(secret);
            }

        }catch (IOException e){
            e.printStackTrace();
            return RESTORE_FAILED;
        }catch (ClassNotFoundException e){
            e.printStackTrace();
            return RESTORE_FAILED;
        }finally {

        }
        return RESTORE_SUCCESS;
    }
}
