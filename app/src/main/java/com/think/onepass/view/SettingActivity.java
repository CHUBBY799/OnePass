package com.think.onepass.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import com.think.onepass.BaseApplication;
import com.think.onepass.R;
import com.think.onepass.excel.ExcelUtils;
import com.think.onepass.guide.CommonWindow;
import com.think.onepass.guide.GuideWindow;
import com.think.onepass.model.Secret;
import com.think.onepass.model.SecretModelImpl;
import com.think.onepass.setting.BackupTask;
import com.think.onepass.setting.backup.MyBackupTask;
import com.think.onepass.suspend.SuspendController;
import com.think.onepass.suspend.permission.FloatPermissionManager;
import com.think.onepass.util.DialogUtils;
import com.think.onepass.util.ServiceUtils;
import com.think.onepass.util.SharePreferenceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SettingActivity extends Activity implements View.OnClickListener,ScreenLock.OnTimeOutListener,CompoundButton.OnCheckedChangeListener{
    private static final String TAG = "SettingActivity";
    private RelativeLayout rlUpdatePassword,rlExcel,rlBackup,rlRestore;
    private SharedPreferences msharedPreferences;
    private Boolean isLock,isOpenFinger,isOpenSuspend,isopenAutoClearClip;
    private Switch mswitchLock,openFinger,openSuspend,openAutoClearClip;
    private ImageView imageViewBackToHeadActivity;

    private static final int MSG_SHOW = 3001;
    private GuideWindow window;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        imageViewBackToHeadActivity = findViewById(R.id.backimagview);
        imageViewBackToHeadActivity.setOnClickListener(this);
        //创建一个SharedPreferences实例
        msharedPreferences = this.getSharedPreferences("settings", MODE_PRIVATE);

        //20秒无动作锁定
        mswitchLock = findViewById(R.id.secure_lock_switch);
        BaseApplication.mScreenLock = new ScreenLock(20000); //定时20秒
        BaseApplication.mScreenLock.setOnTimeOutListener(this); //监听


        //修改密码
        rlUpdatePassword = findViewById(R.id.secure_passwordupdate);
        rlUpdatePassword.setOnClickListener(this);

        //指纹
        openFinger=findViewById(R.id.secure_fingerprintsatrt_switch);

        //悬浮窗
        openSuspend=findViewById(R.id.secure_floatwindowstart_switch);
        openAutoClearClip=findViewById(R.id.secure_clear_switch);


        isLock = msharedPreferences.getBoolean("lock",false);
        isOpenSuspend = msharedPreferences.getBoolean("suspend",false);
        isOpenFinger = msharedPreferences.getBoolean("finger",false);
        isopenAutoClearClip = msharedPreferences.getBoolean("clear",false);

        mswitchLock.setChecked(isLock);
        openFinger.setChecked(isOpenFinger);
        openSuspend.setChecked(isOpenSuspend);
        openAutoClearClip.setChecked(isopenAutoClearClip);
        mswitchLock.setOnCheckedChangeListener(this);
        openSuspend.setOnCheckedChangeListener(this);
        openFinger.setOnCheckedChangeListener(this);
        //清除剪切板
        openAutoClearClip.setOnCheckedChangeListener(this);

        //输出Excel
        rlExcel = findViewById(R.id.data_excel);
        rlExcel.setOnClickListener(this);

        //备份
        rlBackup=findViewById(R.id.data_backup);
        rlBackup.setOnClickListener(this);

        //还原
        rlRestore = findViewById(R.id.data_retore);
        rlRestore.setOnClickListener(this);
    }
    @Override
    protected void onResume(){
        super.onResume();
        if (isLock==true && BaseApplication.isUnlockActivity==false){
            BaseApplication.mScreenLock.start(); //开始计时
        }
        SharedPreferences.Editor meditor_suspend = msharedPreferences.edit();
        if(FloatPermissionManager.getInstance().checkPermission(this) && msharedPreferences.getBoolean("suspend",false)){
            Log.d(TAG, "onResume: 1");
            openSuspend.setChecked(true);
            SuspendController.getInstance().stopSuspendService(this);
            SuspendController.getInstance().startSuspendService(this);
        }else {
            Log.d(TAG, "onResume: 2");
            meditor_suspend.putBoolean("suspend",false);
            openSuspend.setChecked(false);
            SuspendController.getInstance().stopSuspendService(this);
        }
        showGuide();
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_SHOW :
                    showFloatGuide();
                    break;
            }
        }
    };
    private void showGuide(){
            mHandler.removeMessages(MSG_SHOW);
            Message message = mHandler.obtainMessage(MSG_SHOW);
            mHandler.sendMessageDelayed(message,300);

    }

    private void showFloatGuide(){
        if(SharePreferenceUtils.getGuideFloat()){
            return;
        }
        Log.d(BaseApplication.TAG, "showFloatGuide: ");
        window = new GuideWindow(this, R.layout.guide_layout, R.mipmap.floating_window_guide_img,
                R.string.guide_float, new GuideWindow.Callback() {
            @Override
            public void onClickOk() {
                SharePreferenceUtils.setGuideFloat(true);
            }
        });
        window.showAtLocation(mswitchLock, Gravity.NO_GRAVITY,0,0);
    }
    @Override
    protected void onPause(){
        super.onPause();
        BaseApplication.mScreenLock.stop();
        Log.d(BaseApplication.TAG, "onPause: ");
        mHandler.removeMessages(MSG_SHOW);
        if(window != null){
            window.dismiss();
        }
    }
    /** * 当触摸就会执行此方法 */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        isLock = msharedPreferences.getBoolean("lock",false);
        if (isLock==true){
            BaseApplication.mScreenLock.resetTime(); //重置时间
        }
        else {
            BaseApplication.mScreenLock.stop();
        }
        return super.dispatchTouchEvent(ev);
    } /** * 当使用键盘就会执行此方法 */

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        isLock = msharedPreferences.getBoolean("lock",false);
        if (isLock==true){
            BaseApplication.mScreenLock.resetTime(); //重置时间
        }
        else {
            BaseApplication.mScreenLock.stop();
        }
        return super.dispatchKeyEvent(event);
    } /** * 时间到就会执行此方法 */

    @Override
    public void onTimeOut(ScreenLock screensaver) {
        Intent intent = new Intent(this, UnlockActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(BaseApplication.TAG, "onStop: ");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backimagview:{
//                try{
//                    Runtime runtime=Runtime.getRuntime();
//                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
//                }catch(IOException e){
//                }
                finish();
                break;
            }
            //修该密码
            case R.id.secure_passwordupdate:
                final AlertDialog.Builder builderupdatepassword= new AlertDialog.Builder(this);
                final LinearLayout linearlayoutUpdatePassword = (LinearLayout)getLayoutInflater().inflate
                        (R.layout.set_update_password,null);
                builderupdatepassword.setTitle("Confirm the original password");
                builderupdatepassword.setView(linearlayoutUpdatePassword);
                builderupdatepassword.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText metoldPassWord = linearlayoutUpdatePassword.findViewById(R.id.oldpasswordedittext);
                        String mOldPassWordfromshared =  msharedPreferences.getString("password","");
                        String mOldPassWord = metoldPassWord.getText().toString();
                        if(mOldPassWordfromshared.equals(mOldPassWord)){
                            Intent intent = new Intent(SettingActivity.this,SetNewPassWordActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(SettingActivity.this,"The password is wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builderupdatepassword.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builderupdatepassword.create().show();
                break;
            case R.id.data_excel:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    exportExcel();
                }
                break;
            case R.id.data_backup:
//                new BackupTask(this).execute("backupDatabase");
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
                }else {
                    new MyBackupTask(this.getApplicationContext()).execute(MyBackupTask.BACKUP);
                }
                break;
            case R.id.data_retore:
                DialogUtils.showDialog(this, getString(R.string.data_retore_remind_title)
                        , getString(R.string.data_restore_remind_message), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                                } else {
                                    new MyBackupTask(SettingActivity.this.getApplicationContext()).execute(MyBackupTask.RESTORE);
                                }
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
//                new BackupTask(this).execute("restoreDatabase");
                break;
            default:break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    exportExcel();
                }else{
                    Toast.makeText(this,R.string.data_excel_unsuccess,Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    new MyBackupTask(this.getApplicationContext()).execute(MyBackupTask.BACKUP);
                }else{
                    Toast.makeText(this,R.string.data_excel_unsuccess,Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    new MyBackupTask(this.getApplicationContext()).execute(MyBackupTask.RESTORE);
                }else{
                    Toast.makeText(this,R.string.data_excel_unsuccess,Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private String[] title = new String[]{"id","title","username","password","label","number of use"};
    public void exportExcel() {
        File file = new File(getSDPath() + "/Record");
        makeDir(file);
        ExcelUtils.initExcel(file.toString() + "/passport.xls", title);
        String fileName = getSDPath() + "/Record/passport.xls";
        Log.d(TAG, "exportExcel: ");
        ExcelUtils.writeObjListToExcel(getRecordData(), fileName, this);
//        File file1 = new File(file,"passport.xls");
//         Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//         Uri excelURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider",file1);
//         intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//         intent.setDataAndType(excelURI, getContentResolver().getType(excelURI));
//         intent.addCategory(Intent.CATEGORY_OPENABLE);
//         startActivity(intent);
    }

    /**
     * 将数据集合 转化成ArrayList<ArrayList<String>>
     * @return
     */
    private  ArrayList<ArrayList<String>> getRecordData() {
        ArrayList<ArrayList<String>> recordList = new ArrayList<>();
        SecretModelImpl model = new SecretModelImpl(this);
        List<Secret> secrets = model.getSecretsByUseDesc();
        if(secrets.size() == 0){
            Toast.makeText(this,R.string.data_excel_none,Toast.LENGTH_LONG).show();
            return null;
        }
        for (int i = 0; i <secrets.size(); i++) {
            Secret secret = secrets.get(i);
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(String.valueOf(secret.getId()));
            beanList.add(secret.getTitle());
            beanList.add(secret.getUser());
            beanList.add(secret.getPassword());
            beanList.add(secret.getLabel());
            beanList.add(String.valueOf(secret.getUse()));
            recordList.add(beanList);
        }
        return recordList;
    }

    private  String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        String dir = sdDir.toString();
        return dir;
    }

    public void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.secure_lock_switch:{
                SharedPreferences.Editor meditor_lock = msharedPreferences.edit();
                meditor_lock.putBoolean("lock",isChecked);
                meditor_lock.commit();
                isLock = msharedPreferences.getBoolean("lock",false);
                if (isLock==true && BaseApplication.isUnlockActivity==false){
                    BaseApplication.mScreenLock.resetTime(); //重新开始计时
                }
                else {
                    BaseApplication.mScreenLock.stop();
                }
                break;
            }
            case R.id.secure_fingerprintsatrt_switch:
                SharedPreferences.Editor meditor_finger = msharedPreferences.edit();
                meditor_finger.putBoolean("finger",isChecked);
                meditor_finger.commit();
                if(isChecked){
                    SharePreferenceUtils.setFigerprintopenKey(true);
                }else {
                    SharePreferenceUtils.setFigerprintopenKey(false);
                }
                break;
            case R.id.secure_floatwindowstart_switch:
                SharedPreferences.Editor meditor_suspend = msharedPreferences.edit();
                if(isChecked){
                    boolean isPermission = FloatPermissionManager.getInstance().applyFloatWindow(this);
                    if(isPermission){
                        meditor_suspend.putBoolean("suspend",true);
                        meditor_suspend.commit();
                        ((Switch)buttonView).setChecked(true);
                    }else {
                        Log.d(TAG, "onCheckedChanged: false");
                        meditor_suspend.putBoolean("suspend",true);
                        meditor_suspend.commit();
                        ((Switch)buttonView).setChecked(false);
                    }
                    //有对应权限或者系统版本小于7.0
                    if (isPermission || Build.VERSION.SDK_INT < 24){
                        SuspendController.getInstance().startSuspendService(this);
                    }
                }else {
                    Log.d(TAG, "onCheckedChanged: 3");
                    meditor_suspend.putBoolean("suspend",false).commit();
                    SuspendController.getInstance().stopSuspendService(this);
                }
                break;
            case R.id.secure_clear_switch:
                SharedPreferences.Editor meditor_clear = msharedPreferences.edit();
                meditor_clear.putBoolean("clear",isChecked);
                meditor_clear.commit();
                if(isChecked){
                    SharePreferenceUtils.setAutoclearKey(true);
                }else {
                    SharePreferenceUtils.setAutoclearKey(false);
                    //判断自动清除服务是否正在开启 如果是马上停止
                    if(ServiceUtils.isServiceWork(this.getApplicationContext(),
                            "com.think.onepass.setting.ClearClipboardService")){
                        sendBroadcast(new Intent("com.think.onepass.clearclipboardservice"));
                    }
                }

                break;
        }
    }

}
