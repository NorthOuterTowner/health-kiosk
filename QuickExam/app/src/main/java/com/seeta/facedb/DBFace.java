package com.seeta.facedb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

public class DBFace extends SQLiteOpenHelper {

    public final static String DB_NAME = "userFace.db";
    public final static int VERSION = 1;
    private static DBFace instance = null;
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

    public void dbThreadInit() {
        thread_InitDB = new Thread() {
            @Override
            public void run() {
                openDatabase();
                //初始化人脸数据
                g_Name2Feats.clear();
                faceList.clear();
                faceList = getLampList();
                for (HashMap<String, Object> map : faceList) {
                    faceFeatsAdd(map.get("idxdb").toString(), map.get("face").toString());
                }
                b_FlagReady = true;
            }
        };
        thread_InitDB.start();
    }

    public static DBFace getInstance(Context context) {
        if (instance == null) {
            instance = new DBFace(context);
        }
        return instance;
    }

    private void openDatabase() {
        if (db == null) {
            db = getWritableDatabase();
        }
    }

    public DBFace(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /**
     * 第一次安装程序后创建数据库
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table userinfo (_id integer primary key autoincrement,pid text, name text,sex text,age text,height text,weight text,blood_sys text,blood_dia text,blood_hr text,idxdb integer,face text,time text )");
    }

    /**
     * 版本升级时，先删除原有的数据库，再重新创建数据库
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exist userinfo");
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
    public long saveLamp(faceinfo pro) {
        openDatabase();
        ContentValues value = new ContentValues();
        value.put("pid", pro.pid);
        value.put("name", pro.name);
        value.put("sex", pro.sex);
        value.put("age", pro.age);
        value.put("height", pro.height);
        value.put("weight", pro.weight);
        value.put("blood_sys", pro.blood_sys);
        value.put("blood_dia", pro.blood_dia);
        value.put("blood_hr", pro.blood_hr);
        value.put("time", pro.time);
        value.put("idxdb", pro.FaceIdxDB);
        value.put("face", getOneFaceToStr(pro.faceFloat));

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("pid", pro.pid);
        map.put("name", pro.name);
        map.put("sex", pro.sex);
        map.put("age", pro.age);
        map.put("height", pro.height);
        map.put("weight", pro.weight);
        map.put("blood_sys", pro.blood_sys);
        map.put("blood_dia", pro.blood_dia);
        map.put("blood_hr", pro.blood_hr);
        map.put("time", pro.time);
        map.put("idxdb", pro.FaceIdxDB);
        map.put("face", pro.faceFloat);

        faceList.add(map);
        g_Name2Feats.put(pro.name, pro.faceFloat);
        return db.insert("userinfo", null, value);
    }

    /**
     * 根据id删除数据
     */
    public int deleteLamp(int id) {
        return db.delete("userinfo", "_id=?", new String[]{String.valueOf(id)});
    }

    /**
     * 根据id更新数据
     */
    public int updateLamp(faceinfo pro, String id) {
        ContentValues value = new ContentValues();
        value.put("pid", pro.pid);
        value.put("name", pro.name);
        value.put("sex", pro.sex);
        value.put("age", pro.age);
        value.put("height", pro.height);
        value.put("weight", pro.weight);
        value.put("blood_sys", pro.blood_sys);
        value.put("blood_dia", pro.blood_dia);
        value.put("blood_hr", pro.blood_hr);
        value.put("time", pro.time);
        value.put("idxdb", pro.FaceIdxDB);
        return db.update("userinfo", value, "_id=?", new String[]{String.valueOf(id)});
    }

    /**
     * 查询所有数据
     */
    public ArrayList<HashMap<String, Object>> getLampList() {
        openDatabase();
        Cursor cursor = db.query("userinfo", null, null, null, null, null, null);
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        while (cursor.moveToNext()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("pid", cursor.getInt(cursor.getColumnIndex("pid")));
            map.put("name", cursor.getInt(cursor.getColumnIndex("name")));
            map.put("sex", cursor.getString(cursor.getColumnIndex("sex")));
            map.put("age", cursor.getString(cursor.getColumnIndex("age")));
            map.put("height", cursor.getString(cursor.getColumnIndex("height")));
            map.put("weight", cursor.getString(cursor.getColumnIndex("weight")));
            map.put("blood_sys", cursor.getString(cursor.getColumnIndex("blood_sys")));
            map.put("blood_dia", cursor.getString(cursor.getColumnIndex("blood_dia")));
            map.put("blood_hr", cursor.getString(cursor.getColumnIndex("blood_hr")));
            map.put("time", cursor.getString(cursor.getColumnIndex("time")));
            map.put("idxdb", cursor.getString(cursor.getColumnIndex("idxdb")));
            map.put("face", cursor.getString(cursor.getColumnIndex("face")));
            list.add(map);
        }
        return list;
    }

    /**
     * 根据id查询数据
     */
    public faceinfo getALamp(int id,String str) {
        openDatabase();
        Cursor cursor = db.query("userinfo", null, str+"=?", new String[]{String.valueOf(id)}, null, null, null);

        faceinfo pro = new faceinfo("", "", "", "", "", "", -1, null, "", "", "", "");
        while (cursor.moveToNext()) {
            pro._id = cursor.getString(cursor.getColumnIndex("_id"));
            pro.pid = cursor.getString(cursor.getColumnIndex("pid"));
            pro.name = cursor.getString(cursor.getColumnIndex("name"));
            pro.sex = cursor.getString(cursor.getColumnIndex("sex"));
            pro.age = cursor.getString(cursor.getColumnIndex("age"));
            pro.height = cursor.getString(cursor.getColumnIndex("height"));
            pro.weight = cursor.getString(cursor.getColumnIndex("weight"));
            pro.blood_sys = cursor.getString(cursor.getColumnIndex("blood_sys"));
            pro.blood_dia = cursor.getString(cursor.getColumnIndex("blood_dia"));
            pro.blood_hr = cursor.getString(cursor.getColumnIndex("blood_hr"));
            pro.time = cursor.getString(cursor.getColumnIndex("time"));
        }
        return pro;
    }

    /**
     * 根据id查询数据
     */
    public faceinfo getALampByIdxDB(int idxDB) {
        openDatabase();
        Cursor cursor = db.query("userinfo", null, "idxdb=?", new String[]{String.valueOf(idxDB)}, null, null, null);

        faceinfo pro = new faceinfo("", "", "", "", "", "", -1, null, "", "", "", "");
        while (cursor.moveToNext()) {
            pro.pid = cursor.getString(cursor.getColumnIndex("pid"));
            pro.name = cursor.getString(cursor.getColumnIndex("name"));
            pro.sex = cursor.getString(cursor.getColumnIndex("sex"));
            pro.age = cursor.getString(cursor.getColumnIndex("age"));
            pro.height = cursor.getString(cursor.getColumnIndex("height"));
            pro.weight = cursor.getString(cursor.getColumnIndex("weight"));
            pro.blood_sys = cursor.getString(cursor.getColumnIndex("blood_sys"));
            pro.blood_dia = cursor.getString(cursor.getColumnIndex("blood_dia"));
            pro.blood_hr = cursor.getString(cursor.getColumnIndex("blood_hr"));
            pro.time = cursor.getString(cursor.getColumnIndex("time"));
        }
        return pro;
    }

    /**
     * 根据编码查询数据
     */
    public faceinfo getALamp(String code) {
        openDatabase();
        Cursor cursor = db.query("userinfo", null, "code=?", new String[]{code}, null, null, null);

        faceinfo pro = new faceinfo("", "", "", "", "", "", -1, null, "", "", "", "");
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
    public int getLampCount() {
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