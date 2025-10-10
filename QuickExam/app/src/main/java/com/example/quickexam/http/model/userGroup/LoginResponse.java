package com.example.quickexam.http.model.userGroup;

import com.example.quickexam.bean.User;

/**
 * 登录接口返回数据结构
 */
public class LoginResponse {

    private int code;       // 响应状态码，如 200
    private String msg;     // 消息，例如 "Login successful"
    private User user;      // 用户信息

    // Getter
    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public User getUser() { return user; }

}
