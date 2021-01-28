package com.google.firebase.example.fireeats.java.utils;

import com.google.firebase.firestore.FirebaseFirestore;


import java.io.IOException;

//Todo:This is just for testing and development, final product will have many service and implement this class
//Todo: Custom queries
//Todo: Optimize query and request time
@SuppressWarnings("Duplicates")
public class FirebaseService {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseService instance;

    private FirebaseService() throws IOException {
        System.out.println("Good Stuff");
    }

    public static FirebaseService getInstance() {
        if (instance == null) {
            try {
                instance = new FirebaseService();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
    public FirebaseFirestore getDb(){
        return db;
    }
}