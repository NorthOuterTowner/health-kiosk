package com.seeta.facedb;

public class faceinfo {
    int FaceIdxDB;  //人脸数据库中对应的id
    //float mfaceSimilarity;
    String pid; //服务器中对应id号
    String _id; //
    String name;
    String sex;
    String age;
    String height;
    String weight;
    String time;
    String blood_sys;
    String blood_dia;
    String blood_hr;
    String face;  //人脸特征值
    float[] faceFloat;

    public faceinfo(String strid, String strname, String strsex, String strage, String strheight, String strweight,
                    int IdxDb, float[] faces, String blood_sys, String blood_dia, String blood_hr, String time) {
        pid = strid;
        name = strname;
        sex = strsex;
        age = strage;
        height = strheight;
        weight = strweight;
        FaceIdxDB = IdxDb;
        faceFloat = faces;
        this.blood_sys = blood_sys;
        this.blood_dia = blood_dia;
        this.blood_hr = blood_hr;
        this.time = time;
    }

    public faceinfo(String _id) {
        this._id = _id;
    }

    public String get_id() {
        return _id;
    }
}
