package com.google.firebase.example.fireeats.java.tuan;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.model.Product;
import com.google.firebase.example.fireeats.java.utils.Tools;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class ShoppingProductAdvDetails extends AppCompatActivity
        implements EventListener<DocumentSnapshot> {

    private static final String TAG = "RestaurantDetail";

    public static final String KEY_RESTAURANT_ID = "key_restaurant_id";

    private View parent_view;
    private TextView tv_qty;
    private FirebaseFirestore mFirestore;
    private DocumentReference mRestaurantRef;
    private ListenerRegistration mRestaurantRegistration;

    private static int[] array_color_fab = {
            R.id.fab_color_blue,
            R.id.fab_color_pink,
            R.id.fab_color_grey,
            R.id.fab_color_green
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_product_adv_details);
        parent_view = findViewById(R.id.parent_view);

        // Get restaurant ID from extras
        String restaurantId = getIntent().getExtras().getString(KEY_RESTAURANT_ID);
        if (restaurantId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_RESTAURANT_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mRestaurantRef = mFirestore.collection("products").document(restaurantId);

        initToolbar();
        initComponent();
    }

    @Override
    public void onStart() {
        super.onStart();

        mRestaurantRegistration = mRestaurantRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mRestaurantRegistration != null) {
            mRestaurantRegistration.remove();
            mRestaurantRegistration = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    /**
     * Listener for the Restaurant document ({@link #mRestaurantRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }

        onRestaurantLoaded(snapshot.toObject(Product.class));
    }


    private void onRestaurantLoaded(Product product) {
        TextView product_name_textview = (TextView) findViewById(R.id.product_name_textview);
        product_name_textview.setText(product.getName());

        TextView categoryTextView = (TextView) findViewById(R.id.categoryTextView);
        categoryTextView.setText(product.getSubcategory() + " - " + product.getCategory());
        TextView priceTextView = (TextView) findViewById(R.id.price);
        priceTextView.setText("VND " + String.valueOf(product.getPrice()));
        TextView productDescription = (TextView) findViewById(R.id.productDescription);
        productDescription.setText(product.getDescription());

        AppCompatRatingBar ratingBar =  (AppCompatRatingBar) findViewById(R.id.ratingBar);
        ratingBar.setRating((float) product.getAvgRating());

        TextView ratingCount = findViewById(R.id.ratingCount);
        ratingCount.setText(String.valueOf(product.getNumRatings()) + " ratings");

        ImageView restaurantImage = findViewById(R.id.restaurantImage);
        ImageView image1 = findViewById(R.id.image_1);
        ImageView image2 = findViewById(R.id.image_2);
        ImageView image3 = findViewById(R.id.image_3);
        ImageView image4 = findViewById(R.id.image_4);

        // Background image
        Glide.with(restaurantImage.getContext())
                .load(product.getPhoto())
                .into(restaurantImage);
        Glide.with(image1.getContext())
                .load(product.getPhoto())
                .into(image1);
        Glide.with(image2.getContext())
                .load(product.getPhoto())
                .into(image2);
        Glide.with(image3.getContext())
                .load(product.getPhoto())
                .into(image3);
        Glide.with(image4.getContext())
                .load(product.getPhoto())
                .into(image4);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Product Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    private void initComponent() {
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image_1), R.drawable.image_shop_9);
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image_2), R.drawable.image_shop_10);
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image_3), R.drawable.image_shop_11);
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image_4), R.drawable.image_shop_12);
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image_5), R.drawable.image_shop_13);
        tv_qty = (TextView) findViewById(R.id.tv_qty);
        ((FloatingActionButton) findViewById(R.id.fab_qty_sub)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(tv_qty.getText().toString());
                if (qty > 1) {
                    qty--;
                    tv_qty.setText(qty + "");
                }
            }
        });

        ((FloatingActionButton) findViewById(R.id.fab_qty_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(tv_qty.getText().toString());
                if (qty < 10) {
                    qty++;
                    tv_qty.setText(qty + "");
                }
            }
        });

        ((AppCompatButton) findViewById(R.id.bt_add_to_cart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(parent_view, "Add to Cart", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void setColor(View v) {
        ((FloatingActionButton) v).setImageResource(R.drawable.ic_done);
        for (int id : array_color_fab) {
            if (v.getId() != id) {
                ((FloatingActionButton) findViewById(id)).setImageResource(android.R.color.transparent);
            }
        }
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
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
