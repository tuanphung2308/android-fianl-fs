package com.google.firebase.example.fireeats.java.model;

public class User {
    String name = "";
    String email = "";
    String phone = "";
    String address = "";
    String zipcode = "", city = "", state = "";

    public User(String name, String email, String phone, String address, String zipcode, String city, String state) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.zipcode = zipcode;
        this.city = city;
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public User() {
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

    public void setEmail(String gmail) {
        this.email = gmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
