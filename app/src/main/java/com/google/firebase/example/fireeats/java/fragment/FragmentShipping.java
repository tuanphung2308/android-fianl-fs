package com.google.firebase.example.fireeats.java.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.model.PaymentDetail;
import com.google.firebase.example.fireeats.java.tuan.adapters.OnPaymentDetailSubmit;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

//import com.google.firebase.example.fireeats.R;

public class FragmentShipping extends Fragment implements OnPaymentDetailSubmit {
    private FirebaseFirestore mFirestore;
    private static final String TAG = "FragmentShipping";
    private EditText nameEditText, emailEditText, phoneEditText, address1EditText, address2EditText;

    public FragmentShipping() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shipping, container, false);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        nameEditText = root.findViewById(R.id.nameEditText);
        emailEditText = root.findViewById(R.id.emailEditText);
        phoneEditText = root.findViewById(R.id.phoneEditText);
        address1EditText = root.findViewById(R.id.address1EditText);
        address2EditText = root.findViewById(R.id.address2EditText);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference paymentDetailRef = mFirestore.collection("paymentDetails").document(user.getUid());
        paymentDetailRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        PaymentDetail pd = document.toObject(PaymentDetail.class);
                        nameEditText.setText(pd.getName());
                        emailEditText.setText(pd.getEmail());
                        address1EditText.setText(pd.getAddress1());
                        address2EditText.setText(pd.getAddress2());
                        phoneEditText.setText(pd.getPhoneNumber());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return root;
    }

    public void uploadPaymentDetail() {
        PaymentDetail pd = new PaymentDetail();
        pd.setName(nameEditText.getText().toString());
        pd.setEmail(emailEditText.getText().toString());
        pd.setPhoneNumber(phoneEditText.getText().toString());
        pd.setAddress1(address1EditText.getText().toString());
        pd.setAddress2(address2EditText.getText().toString());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mFirestore.collection("paymentDetails").document(user.getUid())
                .set(pd, SetOptions.merge());

    }

    @Override
    public void submitPaymentDetail() {
        uploadPaymentDetail();
    }
}