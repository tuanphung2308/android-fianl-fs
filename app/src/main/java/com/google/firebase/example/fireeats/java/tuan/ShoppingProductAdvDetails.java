package com.google.firebase.example.fireeats.java.tuan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.model.Cart;
import com.google.firebase.example.fireeats.java.model.CartObject;
import com.google.firebase.example.fireeats.java.model.Product;
import com.google.firebase.example.fireeats.java.utils.Tools;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingProductAdvDetails extends AppCompatActivity
        implements EventListener<DocumentSnapshot> {

    private static final String TAG = "RestaurantDetail";

    public static final String KEY_RESTAURANT_ID = "key_restaurant_id";
    private TextView quantityText;
    Product product = new Product();
    private FirebaseFirestore mFirestore;
    private DocumentReference mRestaurantRef;
    private ListenerRegistration mRestaurantRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_product_adv_details);

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
        this.product = product;
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

        FloatingActionButton subBtn = findViewById(R.id.fab_qty_sub);
        FloatingActionButton addBtn = findViewById(R.id.fab_qty_add);
        quantityText = findViewById(R.id.tv_qty);
        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int quantity = Integer.parseInt((String) quantityText.getText());
                    if (quantity > 1)
                        quantityText.setText(String.valueOf(quantity - 1));
                    return;
                } catch (NumberFormatException e) {
                    return;
                }

                // do whatever you want to do on click (to launch any fragment or activity you need to put intent here.)
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int quantity = Integer.parseInt((String) quantityText.getText());
                    quantityText.setText(String.valueOf(quantity + 1));
                    return;
                } catch (NumberFormatException e) {
                    return;
                }

                // do whatever you want to do on click (to launch any fragment or activity you need to put intent here.)
            }
        });

        Button addToCartButton = findViewById(R.id.bt_add_to_cart);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartObject cartObject = new CartObject();
                cartObject.setProduct(product);
                List<CartObject> cartObjectList = new ArrayList<>();
                int quantity = 1;
                try {
                    quantity = Integer.parseInt((String) quantityText.getText());
                } catch (NumberFormatException e) {
                }
                cartObject.setQuantity(quantity);
                cartObjectList.add(cartObject);
                Cart cart = new Cart();
                cart.setCartObjectList(cartObjectList);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Map<String, Object> data = new HashMap<>();
                data.put("items", cartObjectList);

                mFirestore.collection("carts").document(user.getUid())
                        .set(cart);
//                        .set(data, SetOptions.merge());
                // do whatever you want to do on click (to launch any fragment or activity you need to put intent here.)
            }
        });

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Product Details");
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
