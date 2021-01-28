package com.google.firebase.example.fireeats.java.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.activities.MapActivity;
import com.google.firebase.example.fireeats.java.adapter.OrderCartObjectAdapter;
import com.google.firebase.example.fireeats.java.model.Order;
import com.google.firebase.example.fireeats.java.utils.Tools;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class OrderViewActivity extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    private static final String TAG = "ShoppingCheckoutTL";
    private Order order;
    private String orderId;
    private OrderCartObjectAdapter cartObjectAdapter;
    private RecyclerView recyclerView;
    private Button cancelOrderButton;

    private EditText shippingName, shippingEmail, shippingPhone, shippingAddress1, shippingAddress2, paymentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_checkout_timeline);
        initToolbar();

        recyclerView = findViewById(R.id.recyclerCartObjects);
        shippingName = findViewById(R.id.shippingName);
        shippingEmail = findViewById(R.id.shippingEmail);
        shippingPhone = findViewById(R.id.shippingPhone);
        shippingAddress1 = findViewById(R.id.shippingAddress1);
        shippingAddress2 = findViewById(R.id.shippingAddress2);
        paymentType = findViewById(R.id.paymentType);
        cancelOrderButton = findViewById(R.id.cancelOrderButton);

        shippingName.setEnabled(false);
        shippingEmail.setEnabled(false);
        shippingPhone.setEnabled(false);
        shippingAddress1.setEnabled(false);
        shippingAddress2.setEnabled(false);
        paymentType.setEnabled(false);


        Intent intent = getIntent();
        orderId = intent.getStringExtra("ORDERID");

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        DocumentReference docRef = mFirestore.collection("orders").document(orderId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    order = snapshot.toObject(Order.class);
                    cartObjectAdapter = new OrderCartObjectAdapter(order.getCartObjectList());
                    recyclerView.setAdapter(cartObjectAdapter);
                    cartObjectAdapter.notifyDataSetChanged();
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                    shippingName.setText(order.getPaymentDetail().getName());
                    shippingEmail.setText(order.getPaymentDetail().getEmail());
                    shippingPhone.setText(order.getPaymentDetail().getPhoneNumber());
                    shippingAddress1.setText(order.getPaymentDetail().getAddress1());
                    shippingAddress2.setText(order.getPaymentDetail().getAddress2());
                    paymentType.setText(order.getPaymentType());

                    if (order.getOrderStatus().equals("DONE") || order.getOrderStatus().equals("CANCELLED")) {
                        cancelOrderButton.setEnabled(false);
                        cancelOrderButton.setBackgroundTintList(getResources().getColorStateList(R.color.greyDisabled));
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        ((AppCompatButton) findViewById(R.id.cancelOrderButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCancelOrder();
            }
        });

        ((AppCompatButton) findViewById(R.id.openMapButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapProgressView();
            }
        });
    }

    private void openMapProgressView() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("fuck you", "fuck");
        startActivity(intent);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_setting, menu);
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

    private void confirmCancelOrder() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmation")
                .setMessage("Do you want to cancel this order")
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        order.setOrderStatus("CANCELLED");
                        mFirestore.collection("orders").document(orderId).set(order);
                        cancelOrderButton.setEnabled(false);
                    }
                })
                .setPositiveButton("No", null)
                .show();

    }
}
