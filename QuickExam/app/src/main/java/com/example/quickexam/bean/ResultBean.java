package com.example.quickexam.bean;

import java.io.Serializable;

public class ResultBean implements Serializable {

    private int type;
    private String result;

    public ResultBean(int type, String result) {
        this.type = type;
        this.result = result;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
