package com.example.quickexam.http.model.dataGroup;

public class HealthGeneralRequest {

    private String user;
    private String data;

    public HealthGeneralRequest(String user, String data) {
        this.user = user;
        this.data = data;
    }

    public String getUser() {
        return user;
    }

    public String getData() {
        return data;
    }
}
