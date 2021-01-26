package com.google.firebase.example.fireeats.java.tuan;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.adapter.BottomBarAdapter;
import com.google.firebase.example.fireeats.java.adapter.NoSwipePager;
import com.google.firebase.example.fireeats.java.model.Cart;
import com.google.firebase.example.fireeats.java.model.Product;
import com.google.firebase.example.fireeats.java.utils.Tools;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class BottomNavigationSmall extends AppCompatActivity implements EventListener<DocumentSnapshot>  {
    private ImageView[] menu_nav;
    private View notif_badge;
    private FirebaseFirestore mFirestore;
    private DocumentReference mCartRef;
    private NoSwipePager viewPager;
    private BottomBarAdapter pagerAdapter;
    private int notification_count = -1;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private ListenerRegistration mCartRegistration;

    HomeFragment frag1 = new HomeFragment();
    CategoryFragment frag2 = new CategoryFragment();
    OrderFragment frag3 = new OrderFragment();
    UserFragment fragmentProfile = new UserFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_small);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.d("Signed in", user.getUid());
            // Get reference to the restaurant
            mCartRef = mFirestore.collection("carts").document(user.getUid());
        }

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

        initToolbar();
        initComponent();
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

    @Override
    public void onStart() {
        super.onStart();

        mCartRegistration = mCartRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mCartRegistration != null) {
            mCartRegistration.remove();
            mCartRegistration = null;
        }
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

    /**
     * Listener for the Restaurant document ({@link #mCartRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w("XD", "cart:onEvent", e);
            return;
        }

        onCartLoaded(snapshot.toObject(Cart.class));
    }


    private void onCartLoaded(Cart cart) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (cart == null) {
            Map<String, Object> cartMap = new HashMap<>();
            cartMap.put("Fuck", "off");
            mFirestore.collection("carts").document(user.getUid())
                    .set(cartMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("xd", "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("xd", "Error writing document", e);
                        }
                    });
            // No user is signed in
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put("capital", true);

            mFirestore.collection("carts").document(user.getUid())
                    .set(data, SetOptions.merge());
            Log.d("XD", "cart is not null");
        }
    }
}
