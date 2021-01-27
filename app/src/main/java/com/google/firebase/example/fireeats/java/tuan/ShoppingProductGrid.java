package com.google.firebase.example.fireeats.java.tuan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.adapter.RestaurantAdapter;
import com.google.firebase.example.fireeats.java.model.Product;
import com.google.firebase.example.fireeats.java.utils.Tools;
import com.google.firebase.example.fireeats.java.widget.SpacingItemDecoration;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import static com.google.firebase.example.fireeats.java.tuan.ShoppingProductAdvDetails.KEY_RESTAURANT_ID;

public class ShoppingProductGrid extends AppCompatActivity
        implements RestaurantAdapter.OnRestaurantSelectedListener {

    private static final String KEY_CATEGORY_ID = "CATEGORY";
    private View parent_view;

    private RecyclerView recyclerView;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private RestaurantAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_product_grid);
        parent_view = findViewById(R.id.parent_view);

        RecyclerView recyclerRestaurants = findViewById(R.id.recyclerRestaurants);
        LinearLayout viewEmpty = findViewById(R.id.viewEmpty);

        // Get restaurant ID from extras
        String category = getIntent().getExtras().getString(KEY_CATEGORY_ID);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);
        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get ${LIMIT} restaurants
        mQuery = mFirestore.collection("products").whereEqualTo(Product.FIELD_CATEGORY, category);;

        // RecyclerView
        mAdapter = new RestaurantAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    recyclerRestaurants.setVisibility(View.GONE);
                    viewEmpty.setVisibility(View.VISIBLE);
                } else {
                    recyclerRestaurants.setVisibility(View.VISIBLE);
                    viewEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Log.d("Error", "check logs for info.");
            }
        };

        recyclerRestaurants.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerRestaurants.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
        recyclerRestaurants.setHasFixedSize(true);
        recyclerRestaurants.setAdapter(mAdapter);

        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Products");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Intent intent = new Intent(this, ShoppingCartSimple.class);
            intent.putExtra("fuck you", "fuck");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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
    public void onRestaurantSelected(DocumentSnapshot restaurant) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(this, ShoppingProductAdvDetails.class);
//        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra(KEY_RESTAURANT_ID, restaurant.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }


}
