package com.google.firebase.example.fireeats.java.tuan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.viewmodel.MainActivityViewModel;

import java.util.Collections;

public class UserFragment extends Fragment {
    private MainActivityViewModel mViewModel;
    private static final int RC_SIGN_IN = 9001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        LinearLayout personal_detail_ll = (LinearLayout) view.findViewById(R.id.personal_detail_ll);
        LinearLayout noti_ll = (LinearLayout) view.findViewById(R.id.noti_ll);
        LinearLayout payment_ll = (LinearLayout) view.findViewById(R.id.payment_ll);
        LinearLayout about_ll = (LinearLayout) view.findViewById(R.id.about_ll);
        LinearLayout logout_ll = (LinearLayout) view.findViewById(R.id.logout_ll);

        payment_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture_intent = new Intent(getActivity(), PaymentProfile.class);
                startActivity(picture_intent);
            }
        });

        personal_detail_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture_intent = new Intent(getActivity(), FormProfileData.class);
                startActivity(picture_intent);
            }
        });

        noti_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture_intent = new Intent(getActivity(), SettingSectioned.class);
                startActivity(picture_intent);
            }
        });

        about_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture_intent = new Intent(getActivity(), AboutAppSimple.class);
                startActivity(picture_intent);
            }
        });

        logout_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(getActivity());
                startSignIn();
            }
        });
        //more code here
        return view;
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

    public void setmViewModel(MainActivityViewModel mViewModel) {
        this.mViewModel = mViewModel;
    }
}
