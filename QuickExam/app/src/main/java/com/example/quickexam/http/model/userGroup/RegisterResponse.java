package com.example.quickexam.http.model.userGroup;

public class RegisterResponse {
    private int code;
    private String msg;
    private LoginResponse.User user;

    // Getter
    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public LoginResponse.User getUser() { return user; }

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
