package com.google.firebase.example.fireeats.java.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private Integer total = 0;

    private List<CartObject> cartObjectList = new ArrayList<>();

    public Cart() {
    }

    public Cart(List<CartObject> cartObjectList) {
        this.cartObjectList = cartObjectList;
    }

    public List<CartObject> getCartObjectList() {
        return cartObjectList;
    }

    public void setCartObjectList(List<CartObject> cartObjectList) {
        this.cartObjectList = cartObjectList;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
