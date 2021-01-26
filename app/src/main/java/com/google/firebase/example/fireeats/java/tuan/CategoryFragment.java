package com.google.firebase.example.fireeats.java.tuan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.adapter.AdapterGridShopCategory;
import com.google.firebase.example.fireeats.java.data.DataGenerator;
import com.google.firebase.example.fireeats.java.model.ShopCategory;
import com.google.firebase.example.fireeats.java.utils.Tools;
import com.google.firebase.example.fireeats.java.widget.SpacingItemDecoration;

import java.util.List;


public class CategoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdapterGridShopCategory mAdapter;
    private static final String KEY_CATEGORY_ID = "CATEGORY";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        initComponent(view);
        //more code here
        return view;
    }

    private void initComponent(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(v.getContext(), 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(v.getContext(), 8), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        List<ShopCategory> items = DataGenerator.getShoppingCategory(v.getContext());

        //set data and list adapter
        mAdapter = new AdapterGridShopCategory(v.getContext(), items);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterGridShopCategory.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ShopCategory obj, int position) {
                Intent intent = new Intent(v.getContext(), ShoppingProductGrid.class);

                intent.putExtra(KEY_CATEGORY_ID, obj.title);
                startActivity(intent);
            }
        });

    }
}