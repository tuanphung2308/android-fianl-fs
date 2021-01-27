package com.google.firebase.example.fireeats.java.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.tuan.adapters.OnPaymentDetailSubmit;

public class FragmentPayment extends Fragment  implements OnPaymentDetailSubmit {

    public FragmentPayment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_payment, container, false);

        return root;
    }

    @Override
    public void submitPaymentDetail() {
        System.out.println("hahahaah");
    }
}