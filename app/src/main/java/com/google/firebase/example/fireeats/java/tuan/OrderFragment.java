package com.google.firebase.example.fireeats.java.tuan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.adapter.AdapterListBasic;
import com.google.firebase.example.fireeats.java.adapter.OrderAdapter;
import com.google.firebase.example.fireeats.java.data.DataGenerator;
import com.google.firebase.example.fireeats.java.model.Order;
import com.google.firebase.example.fireeats.java.model.People;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.List;

public class OrderFragment extends Fragment implements OrderAdapter.OnOrderSelectedListener {

    private RecyclerView recyclerView;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private OrderAdapter mAdapter;
    private RelativeLayout viewEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_four, container, false);
        //more code here

        initComponent(view);
        return view;
    }

    private void initComponent(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        viewEmpty = v.findViewById(R.id.viewEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setHasFixedSize(true);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get ${LIMIT} restaurants
        if (user != null) {
            mQuery = mFirestore.collection("orders")
                    .whereEqualTo("firebaseUID", user.getUid())
                    .limit(20);

// RecyclerView
            mAdapter = new OrderAdapter(mQuery, this) {
                @Override
                protected void onDataChanged() {
                    // Show/hide content if the query returns empty.
                    if (getItemCount() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        viewEmpty.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        viewEmpty.setVisibility(View.GONE);
                    }
                }

                @Override
                protected void onError(FirebaseFirestoreException e) {
                    // Show a snackbar on errors
                    Snackbar.make(v,
                            "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
                }
            };
            recyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }


    @Override
    public void onOrderSelected(DocumentSnapshot snapshot) {
        Order order = snapshot.toObject(Order.class);
        Intent intent = new Intent(getActivity(), ShoppingCheckoutTimeline.class);
        Log.d("WANGISNOOB", snapshot.getReference().getId());
        intent.putExtra("ORDERID", snapshot.getReference().getId());
        startActivity(intent);
    }
}
