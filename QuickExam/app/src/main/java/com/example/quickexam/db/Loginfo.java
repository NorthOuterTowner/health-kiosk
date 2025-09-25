package com.example.quickexam.db;

public class Loginfo {
    int FaceIdxDB;  //人脸数据库中对应的id
    //float mfaceSimilarity;
    String pid; //服务器中对应id号
    String name;
    String log_time;  //记录时间
    String temper;  //温度
    String alcohol;  //酒精
    String ECG;  //ECG
    String SpO2;  //SPO2
    String PPG;  //PPG
    String blood_sys;
    String blood_dia;
    String blood_hr;
    String tired;  //CO2
    float[] faceFloat;

    public Loginfo(String strid, String strname,
                   String temper, String alcohol, String ECG, String SpO2,
                   String PPG, String blood_sys, String blood_dia, String blood_hr, String tired, String time) {
        pid = strid;
        name = strname;
        log_time = time;
        this.temper = temper;
        this.alcohol = alcohol;
        this.ECG = ECG;
        this.SpO2 = SpO2;
        this.PPG = PPG;
        this.blood_sys = blood_sys;
        this.blood_dia = blood_dia;
        this.blood_hr = blood_hr;
        this.tired = tired;
    }
}
