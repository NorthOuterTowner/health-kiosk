package com.example.quickexam.bean;
public class User {
    private String account;
    private String name;
    private int role;
    private String gender;
    private int age;
    private String token;
    private String height;
    private String weight;

    public User(String account, String name, String gender, int age) {
        this.account = account;
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getAccount() {
        return account;
    }

    public String getName() {
        return name;
    }

    public int getRole() {
        return role;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getToken() {
        return token;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void setToken(String token) {
        this.token = token;
    }
}