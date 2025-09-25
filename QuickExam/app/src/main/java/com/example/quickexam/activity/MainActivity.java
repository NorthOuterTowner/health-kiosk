package com.example.quickexam.activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickexam.BuildConfig;
import com.example.quickexam.MainApplication;
import com.example.quickexam.R;
import com.example.quickexam.adapter.ProjectAdapter;

import android_serialport_api.DataUtils;
import android_serialport_api.SerialPortGY;
import android_serialport_api.SerialPortUtil;

import com.example.quickexam.bean.ConfigBean;
import com.example.quickexam.bean.ResultBean;
import com.example.quickexam.databinding.ActivityMainBinding;
import com.example.quickexam.db.DBLog;
import com.example.quickexam.db.Loginfo;
import com.example.quickexam.utils.FFT;
import com.example.quickexam.utils.FileUtils;
import com.example.quickexam.utils.IflyTts;
import com.example.quickexam.utils.NavigationBarUtil;
import com.example.quickexam.utils.ReadAssetsFileUtils;
import com.example.quickexam.utils.SystemUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.seeta.facedb.DBFace;
import com.seeta.mvp.MainFragment;
import com.seeta.mvp.PresenterImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.quickexam.MainApplication.dbLog;
import static com.example.quickexam.MainApplication.instance;
import static com.example.quickexam.MainApplication.m_iMenuTask;
import static com.example.quickexam.MainApplication.m_serialportutil;
import static com.example.quickexam.MainApplication.miflytts;
import static com.example.quickexam.MainApplication.configMainBean;
import static com.example.quickexam.utils.FFT.calculateRMS;
import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private static final int TIME_CODE = 1, FaceClick = 2, ShowToast = 3, Read_GY = 4;
    private int RESULT_BACK = 3000;
    private Bundle bundle = null;
    private TextView m_Datatime;
    private static ScheduledExecutorService scheduledExecutorService;
    private List<Float> allDatas = new ArrayList<>();
    private DecimalFormat dec = new DecimalFormat("0.00");
    private SerialPortGY m_serialGY;
    private List<ConfigBean.SettingBean.ProjectBean> mlistBean;
    private ConfigBean.SettingBean.ProjectBean m_beantemp, m_beanalcohol, m_beanStartEnd, m_beanbloodPress, m_beanspo2;

    private static int maxfftsize = 1024;
    private static double[] fftBuf = new double[maxfftsize];
    private static double[] cesfftBuf = new double[maxfftsize];
    private static int fftpos = 0;
    private static List<Double> ECGList = new ArrayList<>();
    private static List<Double> PPGList = new ArrayList<>();
    private static List<Double> SYSList = new ArrayList<>();
    private static List<Double> DIAList = new ArrayList<>();
    private static List<Double> SISList = new ArrayList<>();
    private static List<Double> FAGList = new ArrayList<>();
    private static List<Double> ARRList = new ArrayList<>();
    private static List<Double> PBFList = new ArrayList<>();
    private static List<Double> SPO2List = new ArrayList<>();
    private static List<Double> alcoholList = new ArrayList<>();
    private static List<Double> gyReadList = new ArrayList<>();
    private static int updateCnt = 0;
    Message gmsg = new Message();
    public boolean m_bFlagStart = false;
    //权限
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static String[] PERMISSIONS_STORAGE = {
            "android.permission.CAMERA",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_PHONE_STATE",
            "android.permission.READ_CONTACTS",
            "android.permission.READ_PHONE_STATE"};
    private HashMap<Integer, ResultBean> map;
    private String maxTemper;
    private String maxAlcohol;
    private String nameFace;
    private String ecg;
    private String sp02;
    private String blood_sys, blood_dia;
    private String fag;
    private long sysTime;
    private String ppg;
    //private ProjectAdapter adapter;

    //更新时间线程
    class TimeThread extends Thread {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = TIME_CODE;  //消息(一个整型值)
            mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
            msg = null;
            mHandler.postDelayed(this, 1000);
        }
    }

    public void showToast(final String msg) {
        final Toast toast = Toast.makeText(getApplicationContext(), msg,
                Toast.LENGTH_SHORT);
        toast.show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 1000);
    }

    //在主线程里面处理消息并更新UI界面
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            bundle = msg.getData();
            String data;
            switch (msg.what) {
                case TIME_CODE:
                    String mMonth, mDay, mWay, mYear, mHours, mMinute;
                    Calendar calendar = Calendar.getInstance();
                    mYear = String.valueOf(calendar.get(Calendar.YEAR));
                    mMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);        //获取日期的月
                    mDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));      //获取日期的天
                    mWay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));      //获取日期的星期
                    if (calendar.get(Calendar.HOUR) < 10) {
                        mHours = "0" + calendar.get(Calendar.HOUR);
                    } else {
                        mHours = String.valueOf(calendar.get(Calendar.HOUR));
                    }
                    if (calendar.get(Calendar.MINUTE) < 10) {
                        mMinute = "0" + calendar.get(Calendar.MINUTE);
                    } else {
                        mMinute = String.valueOf(calendar.get(Calendar.MINUTE));
                    }
                    if ("1".equals(mWay)) {
                        mWay = "日";
                    } else if ("2".equals(mWay)) {
                        mWay = "一";
                    } else if ("3".equals(mWay)) {
                        mWay = "二";
                    } else if ("4".equals(mWay)) {
                        mWay = "三";
                    } else if ("5".equals(mWay)) {
                        mWay = "四";
                    } else if ("6".equals(mWay)) {
                        mWay = "五";
                    } else if ("7".equals(mWay)) {
                        mWay = "六";
                    }
                    //获取系统时间
                    sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("hh:mm:ss", sysTime);//时间显示格式
                    binding.time.setText(mYear + "/" + mMonth + "/" + mDay + "/" + " " + sysTimeStr);
                    break;
                case FaceClick:
                    //m_serialportutil.sendSPStr((String) msg.obj);
                    m_bFlagStart = true;
                    clearView(1);
                    miflytts.playText("开始检测，进行人脸识别");
                    startActivityForResult(new Intent(MainActivity.this, FragmentActivity.class), 1000);
                    break;
                case ShowToast:
                    showToast((String) msg.obj);
                    break;
                case Read_GY:
                    m_serialGY.sendSerialPort("A4030707B5");
                    break;
            }
        }
    };

    public void clearView(int menu) {
        m_beantemp.setValue("--");
        m_beantemp.setColor(0);
        m_beanalcohol.setValue("--");
        m_beanalcohol.setColor(0);
        if (menu == 0) {
            gyReadList.clear();
            alcoholList.clear();
            binding.begin.setText("开始");
            m_serialportutil.sendSPStr("STOP");
            m_iMenuTask = 0;
            binding.mydata.removeData();
            binding.mydata2.removeData();
            binding.mydata3.removeData();
            if (dbLog != null) {
                dbLog.saveLog(new Loginfo("", nameFace, maxTemper, maxAlcohol, ecg,
                        sp02, ppg, blood_sys, blood_dia, ecg, fag, String.valueOf(sysTime)));
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable("result", (Serializable) map);
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, 1000);
        } else if (menu == 1) {
            binding.begin.setText("停止");
            MainApplication.FaceID = "";
            m_iMenuTask = 0;
        }
        m_beanStartEnd.setColor(2);
        m_beanspo2.setValue("--");
        m_beanspo2.setColor(0);
        m_beanbloodPress.setValue("--");
        m_beanbloodPress.setColor(0);
        //adapter.getDataList().set(0, m_beantemp);
        //adapter.getDataList().set(1, m_beanalcohol);
        //adapter.getDataList().set(2, m_beanStartEnd);
        //adapter.getDataList().set(4, m_beanbloodPress);
        //adapter.getDataList().set(3, m_beanspo2);
        //adapter.changeData();
        binding.name.setText("--");//姓名
        binding.result.setText("--");//体温
        binding.result2.setText("--");//酒精
        binding.result3.setText("--");//血压
        binding.result4.setText("--");//疲劳
        binding.ecg.setText("--");
        binding.sp02.setText("--");
        binding.IBP1.setText("--");
        nameFace = "";
        maxTemper = "";
        maxAlcohol = "";
        ecg = "";
        sp02 = "";
        ppg = "";
        blood_sys = "";
        blood_dia = "";
        fag = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        NavigationBarUtil.focusNotAle(getWindow());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavigationBarUtil.hideNavigationBar(getWindow());
        NavigationBarUtil.clearFocusNotAle(getWindow());
        SystemUtils.appThrowable();
        initView();
        verifyStoragePermissions(this);
        initDBFace();
        m_serialportutil = SerialPortUtil.getInstance();
        m_serialportutil.openSerialPort();
        m_serialGY = new SerialPortGY();
        m_serialGY.openSerialPort();
        m_serialGY.sendSerialPort("A4060301AE");

        //ConfigBean.SettingBean.ProjectBean m_probean = new ConfigBean.SettingBean.ProjectBean();
        //m_probean.setIs_open(1);
        //m_probean.setName("123");
        m_beantemp = mlistBean.get(0);
        m_beanalcohol = mlistBean.get(1);
        m_beanStartEnd = mlistBean.get(2);
        m_beanspo2 = mlistBean.get(3);
        m_beanbloodPress = mlistBean.get(4);

        new TimeThread().start(); //启动新的线程
        miflytts = new IflyTts();
        miflytts.initTts(getApplicationContext());


        gmsg.what = FaceClick;  //消息(一个整型值)
        gmsg.obj = "";
        mHandler.sendMessage(gmsg);// 发送一个msg给mHandler
    }

    private void initDBFace() {
        instance = DBFace.getInstance(getApplicationContext());
        instance.dbThreadInit();

        dbLog = DBLog.getInstance(this);

    }

    /**
     * 用EventBus进行线程间通信，也可以使用Handler
     *
     * @param string
     */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEventMainThread(String string) {
        Log.d(TAG, "COM READ:" + string);

        if (m_iMenuTask == 0) return;
        else if (m_iMenuTask == 2) {
            if (map != null) {
                map.clear();
                map = null;
            }
            map = new HashMap<>();
            String[] strGYArray = string.split(",");
            if ((strGYArray[0].equals("GY")) && (strGYArray.length == 2)) {
                byte[] sendData = DataUtils.HexToByteArr(strGYArray[1]);
                double readval = ((sendData[9] & 0xff) * 256 + (sendData[10] & 0xff));
                readval /= 100.0;
                gyReadList.add(readval);
                if (gyReadList.size() > 5) {
                    gyReadList.remove(0);
                    m_iMenuTask = 3;
                    miflytts.playText("请对酒精传感器吹气");
                    m_serialportutil.sendSPStr("EtOH");
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = Read_GY;  //消息(一个整型值)
                            mHandler.sendMessageDelayed(msg, 1000);// 每隔1秒发送一个msg给mHandler
                            msg = null;
                        }
                    });
                }
                double maxtemper = gyReadList.stream().mapToDouble(Double::doubleValue).max().getAsDouble();
                map.put(0, new ResultBean(0, String.valueOf((float) maxtemper)));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        maxTemper = String.valueOf((float) maxtemper);
                        m_beantemp.setValue(maxTemper);
                        binding.result.setText(maxTemper);
                        //adapter.getDataList().set(0, m_beantemp);
                        //adapter.changeData();
                    }
                });
            }
        } else if (m_iMenuTask == 3) {
            String[] strArray = string.split(",");
            if ((strArray[0].equals("EtOH")) && (strArray.length == 3)) {
                double dataalcohol = Double.valueOf(strArray[1]);
                alcoholList.add(dataalcohol);
                if (alcoholList.size() > 5) {
                    alcoholList.remove(0);
                    m_iMenuTask = 4;
                    m_serialportutil.sendSPStr("EPCM");
                } else {
                    m_serialportutil.sendSPStr("EtOH");
                }
                double maxalcohol = alcoholList.stream().mapToDouble(Double::doubleValue).max().getAsDouble();
                map.put(1, new ResultBean(1, String.valueOf((float) maxalcohol)));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        maxAlcohol = String.valueOf((float) maxalcohol);
                        m_beanalcohol.setValue(maxAlcohol);
                        binding.result2.setText(maxAlcohol);
                        //adapter.getDataList().set(1, m_beanalcohol);
                        //adapter.changeData();
                    }
                });
            }
        } else {
            String[] strArray = string.split(",");
            if ((strArray[0].equals("ECG")) && (strArray.length >= 14)) {
                ECGList.add(Double.valueOf(strArray[1])); //ECG
                PPGList.add(Double.valueOf(strArray[2])); //PPG
                if (ECGList.size() > maxfftsize) {
                    PPGList.remove(0);
                    ECGList.remove(0);
                }
                double datahr = Integer.valueOf(strArray[3]); //HR 心率
                double datarr = Integer.valueOf(strArray[4]); //RR  呼吸率
                double datasys = Integer.valueOf(strArray[5]); //SYS 收缩压
                double datadia = Integer.valueOf(strArray[6]); //DIA 舒张压
                double datasis = Integer.valueOf(strArray[7]); //SIS 动脉硬化指数
                double datafag = Integer.valueOf(strArray[8]); //FAG 疲劳等级
                double dataarr = Integer.valueOf(strArray[9]); //ARR 心律不齐
                double datappbf = Integer.valueOf(strArray[10]); //PBF 含水率
                double datatshr = Integer.valueOf(strArray[11]); //hr
                double datatsspo2 = Integer.valueOf(strArray[12]); //spo2
                double datapleth = Integer.valueOf(strArray[13]); //pleth
                if (datapleth > 0) {
                    SPO2List.add(datapleth);
                    if (SPO2List.size() > maxfftsize) {
                        SPO2List.remove(0);
                    }
                }
                if (datatshr > 0) {
                    datahr = datatshr;
                }
                updateCnt++;
                if (updateCnt < 2) {
                    return;
                }
                double dataecg = FFT.RemoveDCComponent(ECGList) * (-0.01);
                double datappg = FFT.RemoveDCComponent(PPGList) * 0.01;
                double dataspo2 = (datapleth - 127) * (-1.5);
                Log.d(TAG, "dataecg:" + dataecg + ",datappg" + datappg);
                double finalDatahr = datahr;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalDatahr > 0) {
                            ecg = String.valueOf((float) finalDatahr);
                            map.put(2, new ResultBean(2, String.valueOf((float) finalDatahr)));
                            binding.ecg.setText(String.valueOf((int) finalDatahr));
                        }
                        if (datarr > 0) {
                            //binding.sp02.setText(String.valueOf((int)datarr));
                        }
                        if (datatsspo2 > 0) {
                            sp02 = String.valueOf((float) datatsspo2);
                            map.put(3, new ResultBean(3, String.valueOf((float) datatsspo2)));
                            binding.sp02.setText(String.valueOf((int) datatsspo2));
                            //m_beanspo2.setValue(String.valueOf((int)datatsspo2));
                            //adapter.getDataList().set(3,m_beanspo2);
                        }
                        if (datatsspo2 > 0) {
//                            map.put(4, new ResultBean(4, String.valueOf((float) datappg)));
                            ppg = String.valueOf((float) datappg);
                            binding.IBP1.setText(String.valueOf((int) datappg));
                        }
                        if ((datasys > 0) && (datadia > 0)) {                //SYS 收缩压 DIA 舒张压
                            SYSList.add(datasys);
                            DIAList.add(datadia);
                            if (SYSList.size() > 10) {
                                SYSList.remove(0);
                                DIAList.remove(0);
                            }
                            blood_sys = (int) datasys + "";
                            blood_dia = "" + (int) datadia;
                            binding.result3.setText(blood_sys + "/" + blood_dia);
                            m_beanbloodPress.setValue(blood_sys + "/" + blood_dia);
                            //adapter.getDataList().set(4, m_beanbloodPress);
                            map.put(4, new ResultBean(5, blood_sys + "/" + blood_dia));
                        }
                        if (datasis > 0) {                //SIS 动脉硬化指数
                            SISList.add(datadia);
                            if (SISList.size() > 10) {
                                SISList.remove(0);
                            }
                        }
                        if (datafag > 0) {                //FAG 疲劳等级
                            FAGList.add(datafag);
                            if (FAGList.size() > 10) {
                                FAGList.remove(0);
                            }
                            fag = String.valueOf((float) datafag);
                            map.put(5, new ResultBean(6, String.valueOf((float) datafag)));
                            binding.result4.setText(String.valueOf(datafag));
                        }
                        if (dataarr > 0) {                //ARR 心律不齐
                            ARRList.add(datadia);
                            if (ARRList.size() > 10) {
                                ARRList.remove(0);
                            }
                        }
                        if (datappbf > 0) {               //PBF 含水率
                            PBFList.add(datadia);
                            if (PBFList.size() > 10) {
                                PBFList.remove(0);
                            }
                        }
                        binding.mydata.addData((float) dataecg);//血氧
                        binding.mydata2.addData((float) dataspo2);//spo2
                        binding.mydata3.addData((float) datappg);//PPG
                        //binding.mydata4.addData((float) dataspo2);//CO2
                        //adapter.changeData();
                        //检测结束
//                        mHandler.sendEmptyMessageDelayed(INTENTSTART, 1000 * 10);
                    }
                });
            }
        }

        //tv.setText(string);
        //byte[] sendData = DataUtils.HexToByteArr(string);
        //Toast.makeText(getApplicationContext(), "未检测到摄像头", Toast.LENGTH_SHORT).show();

    }

    private void initView() {
        binding.setting.setOnClickListener(this);
        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL));
        //adapter = new ProjectAdapter(binding.recyclerView);
        //m_Datatime = (TextView) findViewById(R.id.time);
        //binding.recyclerView.setAdapter(adapter);
        binding.begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!m_bFlagStart) {
                    Message msg = new Message();
                    msg.what = FaceClick;  //消息(一个整型值)
                    msg.obj = "";
                    mHandler.sendMessage(msg);// 发送一个msg给mHandler
                    msg = null;
                } else {
                    m_bFlagStart = false;
                    clearView(0);
                }

            }
        });
//        adapter.setOnItemClickListener(new ProjectAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                Log.e("Main", "" + position + "被点击了！！");
//                if (position == 2) {
//                }
//            }
//        });
    }

    /**
     * 检测是否有写的权限
     *
     * @param activity
     */
    private void verifyStoragePermissions(Activity activity) {
        try {
            SystemUtils.tryGetUsbPermission(this);
            if (Build.VERSION.SDK_INT >= 23) {
                int permission = ActivityCompat.checkSelfPermission(activity,
                        "android.permission.WRITE_EXTERNAL_STORAGE");
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    //去申请权限
                    ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                } else {
                    //存在权限
                    getConfig();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //通过requestCode来识别是否同一个请求
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //用户同意，执行操作
                getConfig();
            } else {
                //未开启
                Toast.makeText(this, "请开启存储功能", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getConfig() {
        String json = "";
        if (FileUtils.isExists(FileUtils.pathHead + FileUtils.settingFileName)) {
            json = FileUtils.readString(FileUtils.pathHead, FileUtils.settingFileName);
        } else {
            json = ReadAssetsFileUtils.readAssetsTxt(MainActivity.this, "setting");
        }
        ConfigBean configBean = new Gson().fromJson(json, ConfigBean.class);
        configMainBean = configBean;
        mlistBean = configMainBean.getSetting().getProject();
        //adapter.setData(configBean.getSetting().getProject());
        //changeData(ReadAssetsFileUtils.readAssetsTxt(this, "StarCareData"));
        //times();
        //sensorRroTxt();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册EventBus
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        if (m_iMenuTask == 1) {
            miflytts.playText("请测量额温");
            m_serialGY.sendSerialPort("A4030707B5");
            m_iMenuTask = 2;
        }
    }

    private void initData() {
        allDatas.clear();
    }

    //打印
    @org.jetbrains.annotations.NotNull
    private String arrayToString(float[] data) {
        StringBuilder stringBuilder = new StringBuilder();
        if (null == data) {
            return "";
        }
        for (int i = 0; i < data.length; i++) {
            if (i == 0) {
                stringBuilder.append(data[i]);
            } else {
                stringBuilder.append("," + data[i]);
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting:
                startActivityForResult(new Intent(MainActivity.this, SettingActivity.class), 1000);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//            adapter.setData(MainApplication.configMainBean.getSetting().getProject());
        if (requestCode == 1000 && resultCode == 2000) {
            nameFace = data.getStringExtra("name");
            if (nameFace != null)
                binding.name.setText(nameFace);
        } else if (requestCode == 1000 && resultCode == RESULT_BACK) {
            Message msg = new Message();
            msg.what = FaceClick;  //消息(一个整型值)
            msg.obj = "";
            mHandler.sendMessage(msg);// 发送一个msg给mHandler
            msg = null;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注册EventBus
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        instance.closeDb();
        dbLog.closeDb();
        Process.killProcess(Process.myPid());
        System.exit(0);
    }

    // 自定义 Thread 实现，用于执行线程任务
    static class FFTTask extends Thread {
        private double[] x = new double[maxfftsize];
        private double[] cesx = new double[maxfftsize];
        private double maxval = 0, minval;
        private int maxindex = 0, minindex = 0;
        private FFT fft = new FFT(maxfftsize);

        public FFTTask() {
            //this.x = msg;
        }

        @Override
        public void run() {

            boolean cesMutation = fft.calculateListValue(ECGList);
            System.out.println("CES: " + cesMutation);

            boolean phMutation = fft.calculateListValue(PPGList);
            System.out.println("PH: " + phMutation);
            if (cesMutation && !phMutation) {
                System.out.println("Respiratory arrest！！！ ");
            }
            /*
            System.arraycopy(fftBuf, 0, this.x, 0, maxfftsize);
            System.arraycopy(cesfftBuf, 0, this.cesx, 0, maxfftsize);
            maxval = minval = 0.0;
            // 在线程执行期间打印消息
            //System.out.println("Message from thread: " + message);
            int n = x.length;
            //double[] x = {1, 2, 3, 4, 5, 6, 7, 8};
            double[] y = new double[n];
            String str1 = Arrays.toString(x);
            fft.fft(x, y);
            for (int i = 0; i < n/2; i++) {
                //System.out.println("x[" + i + "] = " + x[i] + ", y[" + i + "] = " + y[i]);
                if(y[i]>maxval) {
                    maxval = y[i];
                    maxindex = i;
                }
                if(y[i] < minval) {
                    minval = y[i];
                    minindex = i;
                }
            }
            String str2 = Arrays.toString(y);
            System.out.println("Max[" + maxindex + "] = " + maxval + ", Min[" + minindex + "] = " + minval);
            System.out.println("PHRMS:" + calculateRMS(this.x) + ", CESRMS:" + calculateRMS(this.cesx));
             */

        }
    }

    private static class FragmentFactory {
        public static Fragment create(Context context, String buildType) {

            MainFragment fragment = new MainFragment();
            new PresenterImpl(context, fragment);
            return fragment;

        }
    }
}