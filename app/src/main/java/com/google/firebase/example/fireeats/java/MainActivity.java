package com.google.firebase.example.fireeats.java;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.databinding.ActivityMainBinding;
import com.google.firebase.example.fireeats.java.adapter.OrderAdapter;
import com.google.firebase.example.fireeats.java.adapter.RestaurantAdapter;
import com.google.firebase.example.fireeats.java.model.Cart;
import com.google.firebase.example.fireeats.java.model.PaymentDetail;
import com.google.firebase.example.fireeats.java.model.Product;
import com.google.firebase.example.fireeats.java.model.Rating;
import com.google.firebase.example.fireeats.java.model.User;
import com.google.firebase.example.fireeats.java.tuan.ShoppingProductAdvDetails;
import com.google.firebase.example.fireeats.java.util.RatingUtil;
import com.google.firebase.example.fireeats.java.util.RestaurantUtil;
import com.google.firebase.example.fireeats.java.utils.Tools;
import com.google.firebase.example.fireeats.java.viewmodel.MainActivityViewModel;
import com.google.firebase.example.fireeats.java.widget.SpacingItemDecoration;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OrderAdapter.OnOrderSelectedListener{

    private static final String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 9001;

    private static final int LIMIT = 50;

    private ActivityMainBinding mBinding;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private OrderAdapter mAdapter;

    private MainActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setSupportActionBar(mBinding.toolbar);

        // View model
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get ${LIMIT} restaurants
        mQuery = mFirestore.collection("orders")
                .limit(LIMIT);

        // RecyclerView
        mAdapter = new OrderAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.recyclerRestaurants.setVisibility(View.GONE);
                    mBinding.viewEmpty.setVisibility(View.VISIBLE);
                } else {
                    mBinding.recyclerRestaurants.setVisibility(View.VISIBLE);
                    mBinding.viewEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(mBinding.getRoot(),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };
        mBinding.recyclerRestaurants.setLayoutManager(new LinearLayoutManager(this));
//        mBinding.recyclerRestaurants.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
//        mBinding.recyclerRestaurants.setHasFixedSize(true);
        mBinding.recyclerRestaurants.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            mViewModel.setIsSigningIn(false);

            if (resultCode != RESULT_OK) {
                if (response == null) {
                    // User pressed the back button.
                    finish();
                } else if (response.getError() != null
                        && response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSignInErrorDialog(R.string.message_no_network);
                } else {
                    showSignInErrorDialog(R.string.message_unknown);
                }
            } else {
                Map<String, Object> dataInit = new HashMap<>();
                dataInit.put("total", 0);
                dataInit.put("cartObjectList", new ArrayList<>());

                mFirestore.collection("carts").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .set(dataInit, SetOptions.merge());

                mFirestore.collection("paymentDetails").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .set(new PaymentDetail(), SetOptions.merge());
            }
        }
    }

    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);

    }

    private void showSignInErrorDialog(@StringRes int message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.title_sign_in_error)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.option_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startSignIn();
                    }
                })
                .setNegativeButton(R.string.option_exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).create();

        dialog.show();
    }

    @Override
    public void onOrderSelected(DocumentSnapshot restaurant) {
//        Intent intent = new Intent(this, ShoppingProductAdvDetails.class);
////        Intent intent = new Intent(this, RestaurantDetailActivity.class);
//        intent.putExtra(ShoppingProductAdvDetails.KEY_RESTAURANT_ID, restaurant.getId());
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }
}
