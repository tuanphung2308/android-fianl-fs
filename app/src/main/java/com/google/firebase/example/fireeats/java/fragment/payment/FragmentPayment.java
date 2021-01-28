package com.google.firebase.example.fireeats.java.fragment.payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.tuan.adapters.OnPaymentDetailSubmit;

public class FragmentPayment extends Fragment implements OnPaymentDetailSubmit, AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private static final String[] paths = {"Cash On Delivery", "Credit Card"};
    TextView detailTitle;
    EditText cardId;
    EditText cardName;
    LinearLayout expireLayout;
    LinearLayout saveCardInfo;
    private String paymentType = "Cash On Delivery";
    public FragmentPayment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_payment, container, false);
        spinner = (Spinner) root.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,paths);
        detailTitle = root.findViewById(R.id.detailTitle);
        cardId = root.findViewById(R.id.cardId);
        cardName = root.findViewById(R.id.cardName);
        saveCardInfo = root.findViewById(R.id.saveCardInfo);
        expireLayout = root.findViewById(R.id.expireLayout);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelected(true);
        spinner.setOnItemSelectedListener(this);

        return root;
    }

    @Override
    public void submitPaymentDetail() {
        System.out.println("hahahaah");
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                paymentType = "Cash On Delivery";
                detailTitle.setVisibility(View.GONE);
                cardId.setVisibility(View.GONE);
                cardName.setVisibility(View.GONE);
                expireLayout.setVisibility(View.GONE);
                saveCardInfo.setVisibility(View.GONE);
                // Whatever you want to happen when the first item gets selected
                break;
            case 1:
                paymentType = "Credit Card";
                detailTitle.setVisibility(View.VISIBLE);
                cardId.setVisibility(View.VISIBLE);
                cardName.setVisibility(View.VISIBLE);
                expireLayout.setVisibility(View.VISIBLE);
                saveCardInfo.setVisibility(View.VISIBLE);
                // Whatever you want to happen when the second item gets selected
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    public String getPaymentType() {
        return paymentType;
    }
}