package com.google.firebase.example.fireeats.java.model;

public class User {
    String name = "";
    String email = "";
    String phone = "";
    String address1 = "";
    String address2 = "";

    public User() {
    }

    public User(String name, String email, String phone, String address1, String address2) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address1 = address1;
        this.address2 = address2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }
}