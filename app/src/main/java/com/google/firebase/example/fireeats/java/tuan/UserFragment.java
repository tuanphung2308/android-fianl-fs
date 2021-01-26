package com.google.firebase.example.fireeats.java.tuan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.google.firebase.example.fireeats.R;

public class UserFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        LinearLayout personal_detail_ll = (LinearLayout) view.findViewById(R.id.personal_detail_ll);
        LinearLayout noti_ll = (LinearLayout) view.findViewById(R.id.noti_ll);
        LinearLayout payment_ll = (LinearLayout) view.findViewById(R.id.payment_ll);
        LinearLayout about_ll = (LinearLayout) view.findViewById(R.id.about_ll);

        payment_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture_intent = new Intent(getActivity(), PaymentProfile.class);
                startActivity(picture_intent);
            }
        });

        personal_detail_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture_intent = new Intent(getActivity(), FormProfileData.class);
                startActivity(picture_intent);
            }
        });

        noti_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture_intent = new Intent(getActivity(), SettingSectioned.class);
                startActivity(picture_intent);
            }
        });

        about_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture_intent = new Intent(getActivity(), AboutAppSimple.class);
                startActivity(picture_intent);
            }
        });
        //more code here
        return view;
    }
}
