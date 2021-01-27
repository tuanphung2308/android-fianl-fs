package com.google.firebase.example.fireeats.java.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Restaurant POJO.
 */
@IgnoreExtraProperties
public class Order {
    private String uuid;
    private String name;
    private String category;
    private String photo;
    private String photo1;
    private String photo2;
    private String photo3;
    private String photo4;
    private String description;
    private int price;
    private int numRatings;
    private double avgRating;
}
