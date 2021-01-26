package com.google.firebase.example.fireeats.java.model;

public class CartObject {
    private Product product;
    private int quantity;

    public CartObject(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public CartObject() {
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
