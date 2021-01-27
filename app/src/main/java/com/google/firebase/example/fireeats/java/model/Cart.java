package com.google.firebase.example.fireeats.java.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartObject> cartObjectList;

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
}
