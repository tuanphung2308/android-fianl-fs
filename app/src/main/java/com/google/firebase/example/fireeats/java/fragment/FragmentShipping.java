package com.google.firebase.example.fireeats.java.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.example.fireeats.R;

//import com.google.firebase.example.fireeats.R;

public class FragmentShipping extends Fragment {

    public FragmentShipping() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shipping, container, false);

        return root;
    }
}