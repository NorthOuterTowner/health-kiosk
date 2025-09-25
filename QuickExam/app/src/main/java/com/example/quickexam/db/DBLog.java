package com.example.quickexam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBLog extends SQLiteOpenHelper {

    public final static String DB_NAME = "userLog.db";
    public final static int VERSION = 1;
    private static DBLog instance = null;
    private SQLiteDatabase db;

    public static boolean b_FlagReady = false;
    public static Map<String, float[]> g_Name2Feats = new HashMap<>();
    public static ArrayList<HashMap<String, Object>> faceList = new ArrayList<>();
    private Thread thread_InitDB;


    public void faceFeatsAdd(String key, String val) {
        Gson gson = new Gson();
        float[] faceFeats = gson.fromJson(val, float[].class);
        g_Name2Feats.put(key, faceFeats);
    }

    public static DBLog getInstance(Context context) {
        if (instance == null) {
            instance = new DBLog(context);
        }
        return instance;
    }

    private void openDatabase() {
        if (db == null) {
            db = getWritableDatabase();
        }
    }

    public DBLog(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /**
     * 第一次安装程序后创建数据库
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table userinfo (_id integer primary key autoincrement,pid text, name text,temper text,alcohol text,ECG text,SpO2 text,PPG text,blood_sys text,blood_dia text,blood_hr text,tired text,log_time text )");
    }

    /**
     * 版本升级时，先删除原有的数据库，再重新创建数据库
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists userinfo");
        onCreate(db);
    }

    public String getOneFaceToStr(float[] faceFloat) {

        //float[] faceFloat = mipsGetFeature(bitmap);

        Gson gson = new Gson();
        String str = gson.toJson(faceFloat);
        return str;
    }

    /**
     * 添加一条数据
     */
    public long saveLog(Loginfo pro) {
        openDatabase();
        ContentValues value = new ContentValues();
        value.put("pid", String.valueOf(pro.pid));
        value.put("name", String.valueOf(pro.name));
        value.put("temper", String.valueOf(pro.temper));
        value.put("alcohol", String.valueOf(pro.alcohol));
        value.put("ECG", String.valueOf(pro.ECG));
        value.put("SpO2", String.valueOf(pro.SpO2));
        value.put("PPG",String.valueOf( pro.PPG));
        value.put("blood_sys", String.valueOf(pro.blood_sys));
        value.put("blood_dia", String.valueOf(pro.blood_dia));
        value.put("blood_hr", String.valueOf(pro.blood_hr));
        value.put("tired", String.valueOf(pro.tired));
        value.put("log_time", String.valueOf(pro.log_time));

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("pid", pro.pid);
        map.put("name", pro.name);
        map.put("temper", pro.temper);
        map.put("alcohol", pro.alcohol);
        map.put("ECG", pro.ECG);
        map.put("SpO2", pro.SpO2);
        map.put("PPG", pro.PPG);
        map.put("blood_sys", pro.blood_sys);
        map.put("blood_dia", pro.blood_dia);
        map.put("blood_hr", pro.blood_hr);
        map.put("tired", pro.tired);
        map.put("log_time", pro.log_time);

        faceList.add(map);
        g_Name2Feats.put(pro.name, pro.faceFloat);
        return db.insert("userinfo", null, value);
    }

    /**
     * 根据id删除数据
     */
    public int deleteLog(int id) {
        return db.delete("userinfo", "_id=?", new String[]{String.valueOf(id)});
    }

    /**
     * 根据id更新数据
     */
    public int updateLog(Loginfo pro, int id) {
        ContentValues value = new ContentValues();
        value.put("pid", String.valueOf(pro.pid));
        value.put("name", String.valueOf(pro.name));
        value.put("temper", String.valueOf(pro.temper));
        value.put("alcohol", String.valueOf(pro.alcohol));
        value.put("ECG", String.valueOf(pro.ECG));
        value.put("SpO2", String.valueOf(pro.SpO2));
        value.put("PPG", String.valueOf(pro.PPG));
        value.put("blood_sys", String.valueOf(pro.blood_sys));
        value.put("blood_dia", String.valueOf(pro.blood_dia));
        value.put("blood_hr", String.valueOf(pro.blood_hr));
        value.put("tired", String.valueOf(pro.tired));
        value.put("log_time", String.valueOf(pro.log_time));
        return db.update("userinfo", value, "_id=?", new String[]{String.valueOf(id)});
    }

    /**
     * 查询所有数据
     */
    public ArrayList<HashMap<String, Object>> getLogList() {
        openDatabase();
        Cursor cursor = db.query("userinfo", null, null, null, null, null, null);
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        while (cursor.moveToNext()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("pid", cursor.getInt(cursor.getColumnIndex("pid")));
            map.put("name", cursor.getInt(cursor.getColumnIndex("name")));
            map.put("temper", cursor.getString(cursor.getColumnIndex("temper")));
            map.put("alcohol", cursor.getString(cursor.getColumnIndex("alcohol")));
            map.put("ECG", cursor.getString(cursor.getColumnIndex("ECG")));
            map.put("SpO2", cursor.getString(cursor.getColumnIndex("SpO2")));
            map.put("PPG", cursor.getString(cursor.getColumnIndex("PPG")));
            map.put("blood_sys", cursor.getString(cursor.getColumnIndex("blood_sys")));
            map.put("blood_dia", cursor.getString(cursor.getColumnIndex("blood_dia")));
            map.put("blood_hr", cursor.getString(cursor.getColumnIndex("blood_hr")));
            map.put("tired", cursor.getString(cursor.getColumnIndex("tired")));
            map.put("log_time", cursor.getString(cursor.getColumnIndex("log_time")));
            list.add(map);
        }
        return list;
    }

    /**
     * 根据id查询数据
     */
    public Loginfo getALog(int id) {
        openDatabase();
        Cursor cursor = db.query("userinfo", null, "_id=?", new String[]{String.valueOf(id)}, null, null, null);

        Loginfo pro = new Loginfo("", "", "", "", "", "", "", "", "", "", "", "");
        while (cursor.moveToNext()) {
            pro.pid = cursor.getString(cursor.getColumnIndex("pid"));
            pro.name = cursor.getString(cursor.getColumnIndex("name"));
            pro.temper = cursor.getString(cursor.getColumnIndex("temper"));
            pro.alcohol = cursor.getString(cursor.getColumnIndex("alcohol"));
            pro.ECG = cursor.getString(cursor.getColumnIndex("ECG"));
            pro.SpO2 = cursor.getString(cursor.getColumnIndex("SpO2"));
            pro.PPG = cursor.getString(cursor.getColumnIndex("PPG"));
            pro.blood_sys = cursor.getString(cursor.getColumnIndex("blood_sys"));
            pro.blood_dia = cursor.getString(cursor.getColumnIndex("blood_dia"));
            pro.blood_hr = cursor.getString(cursor.getColumnIndex("blood_hr"));
            pro.tired = cursor.getString(cursor.getColumnIndex("tired"));
            pro.log_time = cursor.getString(cursor.getColumnIndex("log_time"));
        }
        return pro;
    }

    /**
     * 根据id查询数据
     */
    public Loginfo getALogByIdxDB(int name) {
        openDatabase();
        Cursor cursor = db.query("userinfo", null, "name=?", new String[]{String.valueOf(name)}, null, null, null);

        Loginfo pro = new Loginfo("", "", "", "", "", "", "", "", "","","", "");
        while (cursor.moveToNext()) {
            pro.pid = cursor.getString(cursor.getColumnIndex("pid"));
            pro.name = cursor.getString(cursor.getColumnIndex("name"));
            pro.temper = cursor.getString(cursor.getColumnIndex("temper"));
            pro.alcohol = cursor.getString(cursor.getColumnIndex("alcohol"));
            pro.ECG = cursor.getString(cursor.getColumnIndex("ECG"));
            pro.SpO2 = cursor.getString(cursor.getColumnIndex("SpO2"));
            pro.PPG = cursor.getString(cursor.getColumnIndex("PPG"));
            pro.blood_sys = cursor.getString(cursor.getColumnIndex("blood_sys"));
            pro.blood_dia = cursor.getString(cursor.getColumnIndex("blood_dia"));
            pro.blood_hr = cursor.getString(cursor.getColumnIndex("blood_hr"));
            pro.tired = cursor.getString(cursor.getColumnIndex("tired"));
            pro.log_time = cursor.getString(cursor.getColumnIndex("log_time"));
        }
        return pro;
    }

    /**
     * 根据编码查询数据
     */
    public Loginfo getALog(String code) {
        openDatabase();
        Cursor cursor = db.query("userinfo", null, "code=?", new String[]{code}, null, null, null);

        Loginfo pro = new Loginfo("", "", "", "", "", "", "", "", "", "", "", "");
        while (cursor.moveToNext()) {
            //pro.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            //pro.setCode(cursor.getString(cursor.getColumnIndex("code")));
            //pro.setName(cursor.getString(cursor.getColumnIndex("name")));
            //pro.setQty(cursor.getInt(cursor.getColumnIndex("qty")));
        }

        return pro;
    }

    /**
     * 查询有多少条记录
     */
    public int getLogCount() {
        openDatabase();
        Cursor cursor = db.query("userinfo", null, null, null, null, null, null);
        return cursor.getCount();
    }

    public void del(int id) {
        openDatabase();
        db.delete("userinfo", "_id=?", new String[]{String.valueOf(id)});
    }

    public int delAll() {
        openDatabase();
        int ret = db.delete("userinfo", null, null);

        db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name ='userinfo'");
        return ret;
    }

    public void closeDb() {
        if (db != null) {
            db.close();
        }
    }
}