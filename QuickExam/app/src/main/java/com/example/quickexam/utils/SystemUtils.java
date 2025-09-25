package com.example.quickexam.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;

public class SystemUtils {

    public static int NET_ETHERNET = 1;
    public static int NET_WIFI = 2;
    public static int NET_NOCONNECT = 0;
    private static SimpleDateFormat format;
    private static BroadcastReceiver mUsbPermissionActionReceiver;
    private static final String ACTION_USB_PERMISSION = "com.template.USB_PERMISSION";//可自定义
    private static UsbManager mUsbManager;


    public static void appThrowable() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //主线程异常拦截
                while (true) {
                    try {
                        Looper.loop();//主线程的异常会从这里抛出
                    } catch (Throwable e) {
                        Log.d("sss 主线程拦截异常", e.getMessage());
                    }
                }
            }
        });

        Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        //主线程异常catch，拦截子线程的异常
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.d("sss  子线程", e.getMessage());
            }
        });
    }

    public static String getSN(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) { // android O_MR1(安卓8.1)、安卓9
            return Build.getSerial();
        }else
            return Build.SERIAL;
    }

    /**
     * Android 7.0之后获取Mac地址
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET"></uses-permission>
     *
     * @return
     */
    public static String getMacFromHardware() {
        /*获取mac地址有一点需要注意的就是android 6.0版本后，以下注释方法不再适用，不管任何手机都会返回"02:00:00:00:00:00"这个默认的mac地址，这是googel官方为了加强权限管理而禁用了getSYstemService(Context.WIFI_SERVICE)方法来获得mac地址。*/
        String macAddress = null;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth0");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "02:00:00:00:00:02";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "02:00:00:00:00:02";
        }
        return macAddress;
    }

    //获取IP地址
    public static String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }

    //获取系统时间戳
    public static long getCurTimeLong() {
        long time = System.currentTimeMillis();
        TimeZone timeZone = TimeZone.getDefault();
        String id = timeZone.getID(); //获取时区id，如“Asia/Shanghai”
        String shotName = timeZone.getDisplayName(false, TimeZone.SHORT); //获取名字，如“GMT+08:00”
        int times = timeZone.getRawOffset(); //获取时差，返回值毫秒
        //Log.d("sss", "时区id:" + id + " 时区名字：" + shotName + " 时差：" + times + " 时间戳：" + time + " 时差后时间：" + (time - times));
        return time;
    }

    //时间戳转字符串
    public static String getDateToString(long milSecond, int type, String pattern) {
        try {
            Date date = new Date(milSecond);
            format = new SimpleDateFormat(pattern);
            if (type == 0)
                format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            String time;
            if (!pattern.contains("HH")) {
                time = format.format(date) + " 00:00:00";
            } else {
                time = format.format(date);
            }
            //Log.d("sss time ymd", time + " " + milSecond + " " + type + " " + pattern);
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //获取日期
    public static long dateTimeStamp(long time, int type) {
        String dateToString = getDateToString(getCurTimeLong(), type, "yyyy-MM-dd");
        long timeStamp = date2TimeStamp(dateToString, "yyyy-MM-dd HH:mm:ss");
        //Log.d("sss times", timeStamp + " " + dateToString);
        long times = (timeStamp + time) * 1000;
        return timeStamp;
    }

    //字符串转时间戳
    public static long date2TimeStamp(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            return sdf.parse(date).getTime() / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //获取年月日时分秒
    public static int[] dataSpring(String data){
        String[] s = data.split(" ");
        int[] num = null;
        if (s.length == 2){
            num = new int[6];
            String[] ymd = s[0].split("-");
            for (int i = 0; i < ymd.length; i++) {
                num[i] = Integer.parseInt(ymd[i]);
            }
            String[] hms = s[1].split(":");
            for (int i = 0; i < hms.length; i++) {
                num[i+3] = Integer.parseInt(hms[i]);
            }
        }
        return num;
    }

    /*x`
     * 将时分秒转为秒数
     * */
    public static long formatTurnSecond(String time) {
        String s = time;
        int index1 = s.indexOf(":");
        int index2 = s.indexOf(":", index1 + 1);
        int hh = Integer.parseInt(s.substring(0, index1));
        int mi = Integer.parseInt(s.substring(index1 + 1, index2));
        int ss = Integer.parseInt(s.substring(index2 + 1));
        return hh * 60 * 60 + mi * 60 + ss;
    }

    //判断是否在一个时间范围内
    public static boolean isCurrentInTimeScope(int deadlineHour, int deadlineMin) {
        boolean result;
        // 1000 * 60 * 60 * 24
        final long aDayInMillis = 86400000;
        final long currentTimeMillis = System.currentTimeMillis();
        //截止时间
        Time deadlineTime = new Time();
        deadlineTime.set(currentTimeMillis);
        deadlineTime.hour = deadlineHour;
        deadlineTime.minute = deadlineMin;
        //当前时间
        Time startTime = new Time();
        startTime.set(currentTimeMillis);
        //当前时间推后20分钟
        Date d = new Date(currentTimeMillis);
        long myTime = (d.getTime() / 1000) + 20 * 60;
        d.setTime(myTime * 1000);
        Time endTime = new Time();
        endTime.set(myTime);
        if (!startTime.before(endTime)) {
            // 跨天的特殊情况（比如22:00-8:00）
            startTime.set(startTime.toMillis(true) - aDayInMillis);
            result = !deadlineTime.before(startTime) && !deadlineTime.after(endTime);
            // startTime <= deadlineTime <=endTime
            Time startTimeInThisDay = new Time();
            startTimeInThisDay.set(startTime.toMillis(true) + aDayInMillis);
            if (!deadlineTime.before(startTimeInThisDay)) {
                result = true;
            }
        } else {
            // 普通情况(比如 8:00 - 14:00)
            result = !deadlineTime.before(startTime) && !deadlineTime.after(endTime);
            // startTime <= deadlineTime <=endTime
        }
        return result;
    }

    //时间戳获取周
    public static String getWeek(long time) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(new Date(time));
        int year = cd.get(Calendar.YEAR); //获取年份
        int month = cd.get(Calendar.MONTH); //获取月份
        int day = cd.get(Calendar.DAY_OF_MONTH); //获取日期
        int week = cd.get(Calendar.DAY_OF_WEEK); //获取星期
        String weekString;
        switch (week) {
            case Calendar.SUNDAY:
                weekString = "周日";
                break;
            case Calendar.MONDAY:
                weekString = "周一";
                break;
            case Calendar.TUESDAY:
                weekString = "周二";
                break;
            case Calendar.WEDNESDAY:
                weekString = "周三";
                break;
            case Calendar.THURSDAY:
                weekString = "周四";
                break;
            case Calendar.FRIDAY:
                weekString = "周五";
                break;
            default:
                weekString = "周六";
                break;
        }
        return weekString;
    }

    /**
     * 根据当前日期获得是星期几
     * time=yyyy-MM-dd
     *
     * @return
     */
    public static String getWeek2(long time) {
        String Week = "";
        if (format == null)
            format = new SimpleDateFormat("yyyy-MM-dd");
        String dateToString = getDateToString((time) * 1000, 1, "yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(dateToString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int wek = c.get(Calendar.DAY_OF_WEEK);

        if (wek == 1) {
            Week += "周日";
        }
        if (wek == 2) {
            Week += "周一";
        }
        if (wek == 3) {
            Week += "周二";
        }
        if (wek == 4) {
            Week += "周三";
        }
        if (wek == 5) {
            Week += "周四";
        }
        if (wek == 6) {
            Week += "周五";
        }
        if (wek == 7) {
            Week += "周六";
        }
        return Week;
    }

    /**
     * 隐藏标题栏与导航栏
     *
     * @param activity
     */
    public static void setFullScreen(Activity activity) {
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        activity.getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    /**
     * 隐藏标题栏与导航栏
     *
     * @param context
     */
    public static void hideNavKey(Context context) {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = ((Activity) context).getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = ((Activity) context).getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        //获得连接的管理者
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            //获得网络工作信息
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                //当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 判断网络连接（有线|无线|无网络）
     **/
    public static int isNetworkAvailableLinear(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ethNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (ethNetInfo != null && ethNetInfo.isConnected()) {
            return NET_ETHERNET;
        } else if (wifiNetInfo != null && wifiNetInfo.isConnected()) {
            return NET_WIFI;
        } else {
            return NET_NOCONNECT;
        }
    }

    /**
     * 判断网络连接状态
     *
     * @param ipString
     * @return
     */
    public static int isNetWorkStatus(String ipString) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("ping -c 1 -w 1 " + ipString);
            // 读取ping的内容，可不加
//            InputStream input = p.getInputStream();
//            BufferedReader in = new BufferedReader(new InputStreamReader(input));
//            StringBuffer stringBuffer = new StringBuffer();
//            String content = "";
//            while ((content = in.readLine()) != null) {
//                stringBuffer.append(content);
//            }
            // PING的状态 0连接成功 1连接成功但网络不可用 2网络未连接
            return p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 获得屏幕宽高度（不包含底部导航栏）
     */
    public static int[] getScreen(Context context) {
        int[] screen = new int[2];
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }
        screen[0] = outMetrics.widthPixels;//手机屏幕真实宽度
        screen[1] = outMetrics.heightPixels;//手机屏幕真实高度
        return screen;
    }

    /**
     * 获得屏幕真实宽高高度（包含底部导航栏）
     */
    public static int[] getScreenReal(Context context) {
        int[] screen = new int[2];
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        if (Build.VERSION.SDK_INT >= 19) {
            // 可能有虚拟按键的情况
            display.getRealSize(outPoint);
        } else {
            // 不可能有虚拟按键
            display.getSize(outPoint);
        }
        screen[0] = outPoint.x;//手机屏幕真实宽度
        screen[1] = outPoint.y;//手机屏幕真实高度
        return screen;
    }

    //获取USB权限
    public static void tryGetUsbPermission(Context context) {
        mUsbPermissionActionReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ACTION_USB_PERMISSION.equals(action)) {
                    context.unregisterReceiver(this);//解注册
                    synchronized (this) {
                        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (null != usbDevice) {
                                //Log.e("sss", usbDevice.getDeviceName() + "已获取USB权限");
                            }
                        } else {
                            //user choose NO for your previously popup window asking for grant perssion for this usb device
                            //Log.e("sss", String.valueOf("USB权限已被拒绝，Permission denied for device" + usbDevice));
                        }
                    }

                }
            }
        };


        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);

        if (mUsbPermissionActionReceiver != null) {
            context.registerReceiver(mUsbPermissionActionReceiver, filter);
        }

        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);

        boolean has_idcard_usb = false;
        for (final UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {

            if (usbDevice.getVendorId() == 20307 && usbDevice.getProductId() == 18775)//身份证设备USB
            {
                has_idcard_usb = true;
                //Log.e("sss", usbDevice.getDeviceName() + "已找到身份证USB");
                if (mUsbManager.hasPermission(usbDevice)) {
                    //Log.e("sss", usbDevice.getDeviceName() + "已获取过USB权限");
                } else {
                    //Log.e("sss", usbDevice.getDeviceName() + "请求获取USB权限");
                    mUsbManager.requestPermission(usbDevice, mPermissionIntent);
                }
            }
        }

        if (!has_idcard_usb) {
            //Log.e("sss", "未找到身份证USB");
        }

    }

}
