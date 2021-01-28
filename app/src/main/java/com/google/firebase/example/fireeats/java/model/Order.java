package com.google.firebase.example.fireeats.java.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private Integer total = 0;
    private Integer totalIncShipping = 0;
    private String firebaseUID = "";
    private String paymentType = "";
    private Double latitude = new Double(69.696969696969);
    private Double longitude = new Double(69.696969696969);
    private String orderStatus = "PROCESSING"; //DONE
    private PaymentDetail paymentDetail = new PaymentDetail();
    private Date createdDate = new Date();
    private String driverID = "";
    private Double driverLatitude = new Double(69.696969696969);
    private Double driverLongitude = new Double(69.696969696969);

    public Double getDriverLatitude() {
        return driverLatitude;
    }

    public void setDriverLatitude(Double driverLatitude) {
        this.driverLatitude = driverLatitude;
    }

    public Double getDriverLongitude() {
        return driverLongitude;
    }

    public void setDriverLongitude(Double driverLongitude) {
        this.driverLongitude = driverLongitude;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    private List<CartObject> cartObjectList = new ArrayList<>();

    public Order() {
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public PaymentDetail getPaymentDetail() {
        return paymentDetail;
    }

    public void setPaymentDetail(PaymentDetail paymentDetail) {
        this.paymentDetail = paymentDetail;
    }

    public Integer getTotalIncShipping() {
        return totalIncShipping;
    }

    public void setTotalIncShipping(Integer totalIncShipping) {
        this.totalIncShipping = totalIncShipping;
    }

    public String getFirebaseUID() {
        return firebaseUID;
    }

    public void setFirebaseUID(String firebaseUID) {
        this.firebaseUID = firebaseUID;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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
