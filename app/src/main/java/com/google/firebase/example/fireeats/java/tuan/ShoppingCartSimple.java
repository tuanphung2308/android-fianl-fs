package com.google.firebase.example.fireeats.java.tuan;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.adapter.CartObjectAdapter;
import com.google.firebase.example.fireeats.java.model.Cart;
import com.google.firebase.example.fireeats.java.utils.Tools;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class ShoppingCartSimple extends AppCompatActivity {

    private DocumentReference cartRef;
    private CartObjectAdapter cartObjectAdapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore mFirestore;
    private static final String TAG = "ShoppingCartSimple";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart_simple);
        initToolbar();

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        cartRef = mFirestore.collection("carts").document(user.getUid());
        recyclerView = findViewById(R.id.recyclerCartObjects);
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
                    TextView totalTextView = findViewById(R.id.totalTextView);
                    totalTextView.setText("VND " + String.valueOf(currentCart.getTotal()));
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

                        cartObjectAdapter = new CartObjectAdapter(cart.getCartObjectList());
                        recyclerView.setAdapter(cartObjectAdapter);
                        cartObjectAdapter.notifyDataSetChanged();
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));
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

    public void onCheckOut(View view) {
        if (cartObjectAdapter.getItemCount() > 0) {
            finish();
            Intent intent = new Intent(this, ShoppingCheckoutStep.class);
            intent.putExtra("fuck you", "fuck");
            startActivity(intent);
        }
    }
}
