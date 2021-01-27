package com.google.firebase.example.fireeats.java.tuan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.adapter.AdapterListBasic;
import com.google.firebase.example.fireeats.java.data.DataGenerator;
import com.google.firebase.example.fireeats.java.model.People;
import com.google.firebase.example.fireeats.java.utils.Tools;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.example.fireeats.java.model.Orders;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterListBasic mAdapter;
    private static final String TAG = "Main";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_four, container, false);
        //more code here



        initComponent(view);
        return view;
    }

    private void initComponent(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setHasFixedSize(true);
        System.out.println("Fuck offf wirh reallly?????s");


        List<Orders> orders = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("orders").document("2fMSCeYLor3HYgTPNTaS");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);

                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    orders.add(snapshot.toObject(Orders.class));
                    List<People> items = DataGenerator.getPeopleData(v.getContext());
                    items.addAll(DataGenerator.getPeopleData(v.getContext()));
                    items.addAll(DataGenerator.getPeopleData(v.getContext()));
                    People obj = new People();
                    obj.name = "Wang";
                    obj.email = "Wang";
                    items.add(obj);
                    mAdapter = new AdapterListBasic(v.getContext(), items);
                    recyclerView.setAdapter(mAdapter);

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });




//        orders.get(0).getAddress();




        //set data and list adapter

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterListBasic.OnItemClickListener() {
            @Override
            public void onItemClick(View view, People obj, int position) {
                viewOrder(obj);
                System.out.println("Fuck off this shit im done");
            }
        });
    }

    private void viewOrder(People obj) {
        Intent intent = new Intent(getActivity(), ShoppingCheckoutTimeline.class);
        intent.putExtra("fuck you", "fuck");
        startActivity(intent);
    }
}
