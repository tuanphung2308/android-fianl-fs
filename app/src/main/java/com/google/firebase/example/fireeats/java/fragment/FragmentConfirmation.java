package com.google.firebase.example.fireeats.java.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.adapter.CartObjectAdapter;
import com.google.firebase.example.fireeats.java.adapter.ConfirmationCartObjectAdapter;
import com.google.firebase.example.fireeats.java.model.Cart;
import com.google.firebase.example.fireeats.java.tuan.adapters.OnPaymentDetailSubmit;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class FragmentConfirmation extends Fragment  implements OnPaymentDetailSubmit {

    private DocumentReference cartRef;
    private ConfirmationCartObjectAdapter cartObjectAdapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore mFirestore;
    private static final String TAG = "FragmentConfirmation";

    public FragmentConfirmation() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_confirmation, container, false);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final int[] totalCurrent = {0};
        // Firestore
        mFirestore = FirebaseFirestore.getInstance();
        RadioGroup radioGroup = root.findViewById(R.id.radioGroup);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                TextView totalProductTextView = root.findViewById(R.id.totalConfirmationPrice);
                TextView totalOrderTextView = root.findViewById(R.id.orderTotalTextView);
                int totalCurrent = 0;
                try {
                    totalCurrent = Integer.parseInt(totalProductTextView
                            .getText()
                            .toString()
                            .replaceAll("VND ", ""));
                } catch (Exception e) {

                }
                switch (checkedId) {
                    case R.id.standard_delivery:
                        //Implement logic
                        totalCurrent += 20000;
                        totalOrderTextView.setText("VND " + String.valueOf(totalCurrent));
                        break;
                    case R.id.express_delivery:
                        //Implement logic
                        totalCurrent += 70000;
                        totalOrderTextView.setText("VND " + String.valueOf(totalCurrent));
                        break;
                }
            }
        });
        radioGroup.setSelected(true);

        cartRef = mFirestore.collection("carts").document(user.getUid());
        recyclerView = root.findViewById(R.id.recyclerConfirmationCartObjects);
        cartRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Cart currentCart = snapshot.toObject(Cart.class);
                    Log.d(TAG, "Current total: " + currentCart.getTotal());
                    TextView totalTextView = root.findViewById(R.id.totalConfirmationPrice);
                    TextView totalOrderTextView = root.findViewById(R.id.orderTotalTextView);
                    totalCurrent[0] = currentCart.getTotal() + 20000;

                    totalTextView.setText("VND " + String.valueOf(currentCart.getTotal()));
                    totalOrderTextView.setText("VND " + String.valueOf(totalCurrent[0]));
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
        cartRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Current data: " + document.getData());
                        Cart cart = document.toObject(Cart.class);
                        Log.d(TAG, "Current data: " + cart.getCartObjectList().size());

                        cartObjectAdapter = new ConfirmationCartObjectAdapter(cart.getCartObjectList());
                        recyclerView.setAdapter(cartObjectAdapter);
                        cartObjectAdapter.notifyDataSetChanged();
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
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

    @Override
    public void submitPaymentDetail() {
        System.out.println("haha");
    }
}