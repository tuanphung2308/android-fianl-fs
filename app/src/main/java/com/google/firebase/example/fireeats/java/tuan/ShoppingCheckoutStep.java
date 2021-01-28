package com.google.firebase.example.fireeats.java.tuan;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.fragment.DialogPaymentSuccessFragment;
import com.google.firebase.example.fireeats.java.fragment.FragmentConfirmation;
import com.google.firebase.example.fireeats.java.fragment.FragmentPayment;
import com.google.firebase.example.fireeats.java.fragment.FragmentShipping;
import com.google.firebase.example.fireeats.java.model.Order;
import com.google.firebase.example.fireeats.java.model.PaymentDetail;
import com.google.firebase.example.fireeats.java.utils.Tools;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ShoppingCheckoutStep extends AppCompatActivity {
    private FirebaseFirestore mFirestore;
    private enum State {
        SHIPPING,
        PAYMENT,
        CONFIRMATION
    }

    State[] array_state = new State[]{State.SHIPPING, State.PAYMENT, State.CONFIRMATION};

    FragmentShipping fragmentShipping = new FragmentShipping();
    FragmentPayment fragmentPayment = new FragmentPayment();
    FragmentConfirmation fragmentConfirmation = new FragmentConfirmation();

    private View line_first, line_second;
    private ImageView image_shipping, image_payment, image_confirm;
    private TextView tv_shipping, tv_payment, tv_confirm;

    private int idx_state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Firestore
        mFirestore = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_shopping_checkout_step);
        initToolbar();
        initComponent();

        displayFragment(State.SHIPPING);
    }

    private void showDialogPaymentSuccess(Order order) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogPaymentSuccessFragment newFragment = new DialogPaymentSuccessFragment(order);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
    }

    private void orderComplete() {
        Order order = new Order();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Order orderData = fragmentConfirmation.getOrder();
        order.setCartObjectList(orderData.getCartObjectList());
        order.setTotal(orderData.getTotal());
        order.setTotalIncShipping(orderData.getTotalIncShipping());
        order.setFirebaseUID(user.getUid());
        order.setPaymentDetail(fragmentShipping.getPaymentDetail());
        order.setLatitude(fragmentShipping.getPaymentDetail().getLatitude());
        order.setLongitude(fragmentShipping.getPaymentDetail().getLongitude());
        order.setPaymentType(fragmentPayment.getPaymentType());
        mFirestore.collection("orders").document(new Date().getTime() + "-" + user.getUid()).set(order);

        Map<String, Object> dataInit = new HashMap<>();
        dataInit.put("total", 0);
        dataInit.put("cartObjectList", new ArrayList<>());
        mFirestore.collection("carts").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(dataInit);
        showDialogPaymentSuccess(order);
//        finish();
    }

    private void initComponent() {
        line_first = (View) findViewById(R.id.line_first);
        line_second = (View) findViewById(R.id.line_second);
        image_shipping = (ImageView) findViewById(R.id.image_shipping);
        image_payment = (ImageView) findViewById(R.id.image_payment);
        image_confirm = (ImageView) findViewById(R.id.image_confirm);

        tv_shipping = (TextView) findViewById(R.id.tv_shipping);
        tv_payment = (TextView) findViewById(R.id.tv_payment);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);

        image_payment.setColorFilter(getResources().getColor(R.color.grey_10), PorterDuff.Mode.SRC_ATOP);
        image_confirm.setColorFilter(getResources().getColor(R.color.grey_10), PorterDuff.Mode.SRC_ATOP);

        (findViewById(R.id.lyt_next)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idx_state == array_state.length - 1) {
                    orderComplete();
                    return;
                }
                idx_state++;
                TextView t = findViewById(R.id.text_step_next);
                if (idx_state == array_state.length - 1){
                    t.setText("FINISH");
                } else {
                    t.setText("NEXT");
                    fragmentShipping.uploadPaymentDetail();
                }
                displayFragment(array_state[idx_state]);

            }
        });

        (findViewById(R.id.lyt_previous)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idx_state < 1) return;
                TextView t = findViewById(R.id.text_step_next);
                t.setText("NEXT");
                idx_state--;
                displayFragment(array_state[idx_state]);
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, android.R.color.white);
        Tools.setSystemBarLight(this);
    }

    private void displayFragment(State state) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        refreshStepTitle();

        if (state.name().equalsIgnoreCase(State.SHIPPING.name())) {
            fragment = fragmentShipping;
            tv_shipping.setTextColor(getResources().getColor(R.color.grey_90));
            image_shipping.clearColorFilter();
        } else if (state.name().equalsIgnoreCase(State.PAYMENT.name())) {
            fragment = fragmentPayment;
            line_first.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            image_shipping.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            image_payment.clearColorFilter();
            tv_payment.setTextColor(getResources().getColor(R.color.grey_90));
        } else if (state.name().equalsIgnoreCase(State.CONFIRMATION.name())) {
            fragment = fragmentConfirmation;
            line_second.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            image_payment.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            image_confirm.clearColorFilter();
            tv_confirm.setTextColor(getResources().getColor(R.color.grey_90));
        }

        if (fragment == null) return;
        fragmentTransaction.replace(R.id.frame_content, fragment);
        fragmentTransaction.commit();
    }

    private void refreshStepTitle() {
        tv_shipping.setTextColor(getResources().getColor(R.color.grey_20));
        tv_payment.setTextColor(getResources().getColor(R.color.grey_20));
        tv_confirm.setTextColor(getResources().getColor(R.color.grey_20));
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
}

