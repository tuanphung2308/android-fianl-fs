package com.google.firebase.example.fireeats.java.tuan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.utils.Tools;


public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_phone);

        Tools.setSystemBarColor(this, android.R.color.white);
        Tools.setSystemBarLight(this);

        Button button = findViewById(R.id.phone_continue_btn);

        button.setOnClickListener(v -> {
            System.out.println("Test");
            // Do something in response to button click
//            Intent intent = new Intent(this, BottomNavigationSmall.class);
            Intent intent = new Intent(this, VerificationCode.class);
            intent.putExtra("fuck you", "fuck");
            startActivity(intent);
        });
    }
}
