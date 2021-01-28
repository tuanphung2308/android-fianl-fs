package com.google.firebase.example.fireeats.java.fragment.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.activities.AboutActivity;
import com.google.firebase.example.fireeats.java.activities.ProfileFormActivity;
import com.google.firebase.example.fireeats.java.viewmodel.MainActivityViewModel;

import java.util.Collections;

public class UserFragment extends Fragment {
    private MainActivityViewModel mViewModel;
    private static final int RC_SIGN_IN = 9001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        LinearLayout personal_detail_ll = (LinearLayout) view.findViewById(R.id.personal_detail_ll);
        LinearLayout about_ll = (LinearLayout) view.findViewById(R.id.about_ll);
        LinearLayout logout_ll = (LinearLayout) view.findViewById(R.id.logout_ll);

        personal_detail_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture_intent = new Intent(getActivity(), ProfileFormActivity.class);
                startActivity(picture_intent);
            }
        });

        about_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture_intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(picture_intent);
            }
        });

        logout_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
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

    public void logout() {
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Confirmation")
                .setMessage("Do you want to logout")
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AuthUI.getInstance().signOut(getActivity());
                        startSignIn();
                    }
                })
                .setPositiveButton("No", null)
                .show();
    }
}
