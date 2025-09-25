package com.example.quickexam;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.quickexam.activity.MainActivity;
import com.example.quickexam.bean.ConfigBean;
import com.example.quickexam.db.DBLog;
import com.example.quickexam.utils.IflyTts;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.seeta.facedb.DBFace;

import android_serialport_api.SerialPortUtil;

public class MainApplication extends Application {

    private static MainApplication application = null;
    public static ConfigBean configMainBean = null;
    public static int m_iMenuTask = 0;
    public static DBLog dbLog = null;
    public static String FaceID = "";
    public static DBFace instance = null;
    public static IflyTts miflytts;
    public static SerialPortUtil m_serialportutil;

    @Override
    public void onCreate() {
        StringBuffer param = new StringBuffer();
        param.append("appid=" + "5850e8fa");
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(MainApplication.this, param.toString());
        super.onCreate();
        initData();
    }

    public void setMenuTask(int val) {
        m_iMenuTask = val;
    }

    public int getMenuTask() {
        return m_iMenuTask;
    }

    public void setConfigBean(ConfigBean configBean) {
        this.configMainBean = configBean;
    }

    //初始化数据
    private void initData() {
        application = this;
    }

    public static MainApplication getInstance() {
        return application;
    }

    public static class BootBroadcastReceiver extends BroadcastReceiver {
        static final String ACTION = "android.intent.action.BOOT_COMPLETED";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION)) {
                Toast.makeText(MainApplication.getInstance(), "quickexam：" + ACTION, Toast.LENGTH_LONG).show();
                //Intent mintent = new Intent(context, SplashActivity.class);  // 要启动的Activity
                Intent mintent = new Intent(context, MainActivity.class);
                //1.如果自启动APP，参数为需要自动启动的应用包名
                //Intent mintent = context.getPackageManager().getLaunchIntentForPackage("com.example.quickexam");
                //下面这句话必须加上才能开机自动运行app的界面
                mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //2.如果自启动Activity
                context.startActivity(mintent);
                //3.如果自启动服务
                //context.startService(mintent);
            }
        }
    }
}
