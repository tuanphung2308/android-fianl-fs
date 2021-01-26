package com.google.firebase.example.fireeats.java.tuan;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.adapter.AdapterSnapGeneric;
import com.google.firebase.example.fireeats.java.data.DataGenerator;
import com.google.firebase.example.fireeats.java.helper.StartSnapHelper;
import com.google.firebase.example.fireeats.java.model.Image;

import java.util.List;

public class HomeFragment extends Fragment {
    private static final String KEY_CATEGORY_ID = "CATEGORY";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initComponent(view);
        CardView desktopCard = (CardView) view.findViewById(R.id.desktopCard); // creating a CardView and assigning a value.

        desktopCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShoppingProductGrid.class);

                intent.putExtra(KEY_CATEGORY_ID, "Gaming Desktop");
                startActivity(intent);
                // do whatever you want to do on click (to launch any fragment or activity you need to put intent here.)
            }
        });

        CardView laptopCard = (CardView) view.findViewById(R.id.laptopCard); // creating a CardView and assigning a value.

        laptopCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShoppingProductGrid.class);

                intent.putExtra(KEY_CATEGORY_ID, "Gaming Laptop");
                startActivity(intent);
                // do whatever you want to do on click (to launch any fragment or activity you need to put intent here.)
            }
        });

        CardView kbCard = (CardView) view.findViewById(R.id.kbCard); // creating a CardView and assigning a value.

        kbCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShoppingProductGrid.class);

                intent.putExtra(KEY_CATEGORY_ID, "Mouse and Keyboard");
                startActivity(intent);
                // do whatever you want to do on click (to launch any fragment or activity you need to put intent here.)
            }
        });

        CardView headphoneCard = (CardView) view.findViewById(R.id.headphoneCard); // creating a CardView and assigning a value.

        headphoneCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShoppingProductGrid.class);

                intent.putExtra(KEY_CATEGORY_ID, "Headphones");
                startActivity(intent);
                // do whatever you want to do on click (to launch any fragment or activity you need to put intent here.)
            }
        });

        CardView monitorCard = (CardView) view.findViewById(R.id.monitorCard); // creating a CardView and assigning a value.

        monitorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShoppingProductGrid.class);

                intent.putExtra(KEY_CATEGORY_ID, "Monitors");
                startActivity(intent);
                // do whatever you want to do on click (to launch any fragment or activity you need to put intent here.)
            }
        });

        CardView storageCard = (CardView) view.findViewById(R.id.storageCard); // creating a CardView and assigning a value.

        storageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShoppingProductGrid.class);

                intent.putExtra(KEY_CATEGORY_ID, "Storage");
                startActivity(intent);
                // do whatever you want to do on click (to launch any fragment or activity you need to put intent here.)
            }
        });

        CardView chairCard = (CardView) view.findViewById(R.id.chairCard); // creating a CardView and assigning a value.

        chairCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShoppingProductGrid.class);

                intent.putExtra(KEY_CATEGORY_ID, "Gaming Chair");
                startActivity(intent);
                // do whatever you want to do on click (to launch any fragment or activity you need to put intent here.)
            }
        });

        CardView tableCard = (CardView) view.findViewById(R.id.tableCard); // creating a CardView and assigning a value.

        tableCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShoppingProductGrid.class);

                intent.putExtra(KEY_CATEGORY_ID, "Gaming Table");
                startActivity(intent);
                // do whatever you want to do on click (to launch any fragment or activity you need to put intent here.)
            }
        });

        //more code here
        return view;
    }

    private void initComponent(View v) {
        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progress);
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        // generate data
        List<Image> items = DataGenerator.getImageDate(getActivity()).subList(0, 5);

        recyclerView.setAdapter(new AdapterSnapGeneric(getActivity(), items, R.layout.item_snap_full));
        recyclerView.setOnFlingListener(null);

        progressBar.setMax(items.size());
        progressBar.setProgress(1);
        StartSnapHelper startSnapHelper = new StartSnapHelper();
        startSnapHelper.attachToRecyclerView(recyclerView);
        startSnapHelper.setSnapPositionListener(new StartSnapHelper.SnapPositionListener() {
            @Override
            public void position(View view, int position) {
                progressBar.setProgress(position + 1);
            }
        });
    }
}
