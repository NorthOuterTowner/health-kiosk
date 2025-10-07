package com.example.quickexam.http.model.userGroup;

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

    public static class User {
        private String account;
        private String name;
        private int role;
        private String gender;
        private int age;
        private String token;

        public String getAccount() { return account; }
        public String getName() { return name; }
        public int getRole() { return role; }
        public String getGender() { return gender; }
        public int getAge() { return age; }
        public String getToken() { return token; }
    }
}
