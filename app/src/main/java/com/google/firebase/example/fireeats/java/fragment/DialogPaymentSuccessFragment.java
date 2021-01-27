package com.google.firebase.example.fireeats.java.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.model.Order;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DialogPaymentSuccessFragment extends DialogFragment {

    private View root_view;
    private Order order;

    public DialogPaymentSuccessFragment(Order order) {
        this.order = order;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.dialog_payment_success, container, false);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatter2 = new SimpleDateFormat("hh:mm");
        String strDate = formatter.format(date);
        String strTime = formatter2.format(date);

        TextView dateCreated = root_view.findViewById(R.id.dateCreated);
        TextView timeCreated = root_view.findViewById(R.id.timeCreated);
        TextView shippingName = root_view.findViewById(R.id.shippingName);
        TextView shippingEmail = root_view.findViewById(R.id.shippingEmail);
        TextView totalAmountInclShip = root_view.findViewById(R.id.totalAmountInclShip);
        TextView paymentStatus = root_view.findViewById(R.id.paymentStatus);
        TextView paymentType = root_view.findViewById(R.id.paymentType);

        dateCreated.setText(strDate);
        timeCreated.setText(strTime);
        shippingEmail.setText(order.getPaymentDetail().getEmail());
        shippingName.setText(order.getPaymentDetail().getName());
        totalAmountInclShip.setText("VND " + order.getTotalIncShipping());
        paymentStatus.setText(order.getOrderStatus());
        paymentType.setText(order.getPaymentType());


        ((FloatingActionButton) root_view.findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                dismiss();
            }
        });

        return root_view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}