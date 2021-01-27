package com.google.firebase.example.fireeats.java.tuan;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.adapter.BottomBarAdapter;
import com.google.firebase.example.fireeats.java.adapter.NoSwipePager;
import com.google.firebase.example.fireeats.java.model.Cart;
import com.google.firebase.example.fireeats.java.model.PaymentDetail;
import com.google.firebase.example.fireeats.java.utils.Tools;
import com.google.firebase.example.fireeats.java.viewmodel.MainActivityViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class BottomNavigationSmall extends AppCompatActivity {
    private ImageView[] menu_nav;
    private View notif_badge;

    private NoSwipePager viewPager;
    private BottomBarAdapter pagerAdapter;
    private int notification_count = -1;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private FirebaseFirestore mFirestore;

    HomeFragment frag1 = new HomeFragment();
    CategoryFragment frag2 = new CategoryFragment();
    OrderFragment frag3 = new OrderFragment();
    UserFragment fragmentProfile = new UserFragment();

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private MainActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_small);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);
        //optimisation
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPagingEnabled(false);

        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());

        pagerAdapter.addFragments(frag1);
        pagerAdapter.addFragments(frag2);
        pagerAdapter.addFragments(frag3);
        pagerAdapter.addFragments(fragmentProfile);

        viewPager.setAdapter(pagerAdapter);

        // View model
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        fragmentProfile.setmViewModel(mViewModel);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        initToolbar();
        initComponent();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        } else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DocumentReference cartRef = mFirestore.collection("carts").document(user.getUid());
            cartRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Map<String, Object> dataInit = new HashMap<>();
                            dataInit.put("total", 0);
                            dataInit.put("cartObjectList", new ArrayList<>());
                            mFirestore.collection("carts").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .set(dataInit);
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            DocumentReference paymentDetailRef = mFirestore.collection("paymentDetails").document(user.getUid());
            paymentDetailRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Map<String, Object> dataInit = new HashMap<>();
                            dataInit.put("total", 0);
                            dataInit.put("cartObjectList", new ArrayList<>());
                            mFirestore
                                    .collection("paymentDetails")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .set(dataInit);
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
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
            }
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    private void initComponent() {
        menu_nav = new ImageView[4];
        menu_nav[0] = findViewById(R.id.menu_nav_1);
        menu_nav[1] = findViewById(R.id.menu_nav_2);
        menu_nav[2] = findViewById(R.id.menu_nav_3);
        menu_nav[3] = findViewById(R.id.menu_nav_5);

        // display image
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image_1), R.drawable.image_18);
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image_2), R.drawable.image_30);
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image_3), R.drawable.image_21);
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image_5), R.drawable.image_19);

        for (ImageView iv : menu_nav) {
            if (iv.getId() == R.id.menu_nav_1) {
                iv.setColorFilter(getResources().getColor(R.color.light_green_500), PorterDuff.Mode.SRC_IN);
                break;
            }
        }
    }

    public void onNavigationClick(View view) {
        for (ImageView iv : menu_nav) {
            iv.setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            if (iv.getId() == view.getId()) {
                switch (iv.getId()) {
                    case R.id.menu_nav_1:
                        System.out.println("First tab");
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.menu_nav_2:
                        System.out.println("2nd tab");
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.menu_nav_3:
                        System.out.println("3rd tab");
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.menu_nav_5:
                        System.out.println("4th tab");
                        viewPager.setCurrentItem(3);
                        break;
                }
                iv.setColorFilter(getResources().getColor(R.color.light_green_500), PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));
        Tools.changeOverflowMenuIconColor(toolbar, getResources().getColor(R.color.grey_60));

        final MenuItem menu_notif = menu.findItem(R.id.action_cart);
        View actionView = MenuItemCompat.getActionView(menu_notif);
        notif_badge = actionView.findViewById(R.id.notif_badge);
        setupBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menu_notif);
            }
        });

        MenuItem action_search = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(action_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(action_search, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                menu_notif.setVisible(true);
                initToolbar();
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setHomeButtonEnabled(false);
                menu_notif.setVisible(false);
                return true;
            }
        });

//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//
//                return false;
//            }
//        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == R.id.action_cart) {
            //go to cart
            Intent intent = new Intent(this, ShoppingCartSimple.class);
            intent.putExtra("fuck you", "fuck");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBadge() {
        if (notif_badge == null) return;
        if (notification_count == 0) {
            notif_badge.setVisibility(View.GONE);
        } else {
            notif_badge.setVisibility(View.VISIBLE);
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
}
