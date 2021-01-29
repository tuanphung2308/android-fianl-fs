package com.google.firebase.example.fireeats.java.activities;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.activities.ShoppingCartActivity;
import com.google.firebase.example.fireeats.java.model.Cart;
import com.google.firebase.example.fireeats.java.model.CartObject;
import com.google.firebase.example.fireeats.java.model.Product;
import com.google.firebase.example.fireeats.java.utils.Tools;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Transaction;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity
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

        AppCompatRatingBar ratingBar = (AppCompatRatingBar) findViewById(R.id.ratingBar);
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
                .load(product.getPhoto1())
                .into(image1);
        Glide.with(image2.getContext())
                .load(product.getPhoto2())
                .into(image2);
        Glide.with(image3.getContext())
                .load(product.getPhoto3())
                .into(image3);
        Glide.with(image4.getContext())
                .load(product.getPhoto4())
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
            }
        });

        Button addToCartButton = findViewById(R.id.openMapButton);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CartObject cartObject = new CartObject();
                cartObject.setProduct(product);
                int quantity = 1;
                try {
                    quantity = Integer.parseInt((String) quantityText.getText());
                } catch (NumberFormatException e) {
                }
                cartObject.setQuantity(quantity);

                // init new cart
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                //from here
                final DocumentReference sfDocRef = mFirestore.collection("carts").document(user.getUid());
                mFirestore.runTransaction(new Transaction.Function<Cart>() {
                    @Override
                    public Cart apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(sfDocRef);
                        Cart currentCart = snapshot.toObject(Cart.class);
                        List<CartObject> cartObjectsCurrent = currentCart.getCartObjectList();
                        Boolean foundProduct = false;
                        for (CartObject co : cartObjectsCurrent) {
                            if (co.getProduct().getName().equals(cartObject.getProduct().getName())) {
                                foundProduct = true;
                                co.setQuantity(co.getQuantity() + cartObject.getQuantity());
                            }
                        }
                        if (!foundProduct) {
                            cartObjectsCurrent.add(cartObject);
                        }
                        int total = 0;
                        for (CartObject co : currentCart.getCartObjectList()) {
                            total += co.getQuantity() * co.getProduct().getPrice();
                        }
                        transaction.update(sfDocRef, "total", total);
                        transaction.update(sfDocRef, "cartObjectList", cartObjectsCurrent);
                        return currentCart;
                    }
                })
                        .addOnSuccessListener(new OnSuccessListener<Cart>() {
                            @Override
                            public void onSuccess(Cart result) {
                                Log.d(TAG, "Transaction success: " + result.getCartObjectList().size());
                                showSuccessfulSnack();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Transaction failure.", e);
                            }
                        });
            }
        });

    }

    private void showSuccessfulSnack() {
        Snackbar
                .make(findViewById(R.id.parent_view), "Added to cart", Snackbar.LENGTH_LONG).show();
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
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            intent.putExtra("fuck you", "fuck");
            startActivity(intent);
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
