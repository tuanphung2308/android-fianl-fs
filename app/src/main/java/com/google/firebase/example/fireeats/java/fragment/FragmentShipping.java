package com.google.firebase.example.fireeats.java.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.model.PaymentDetail;
import com.google.firebase.example.fireeats.java.tuan.adapters.OnPaymentDetailSubmit;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.List;


//import com.google.firebase.example.fireeats.R;

public class FragmentShipping extends Fragment implements OnPaymentDetailSubmit {
    private FirebaseFirestore mFirestore;
    private static final String TAG = "FragmentShipping";
    private EditText nameEditText, emailEditText, phoneEditText, address1EditText, address2EditText;
    private final static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private final static int RESULT_OK = 1;
    private LatLng latLng;

    public FragmentShipping() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shipping, container, false);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        nameEditText = root.findViewById(R.id.nameEditText);
        emailEditText = root.findViewById(R.id.emailEditText);
        phoneEditText = root.findViewById(R.id.phoneEditText);
        address1EditText = root.findViewById(R.id.address1EditText);
        address2EditText = root.findViewById(R.id.address2EditText);

        address1EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .setCountry("VN")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .build(getActivity());

                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference paymentDetailRef = mFirestore.collection("paymentDetails").document(user.getUid());
        paymentDetailRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        PaymentDetail pd = document.toObject(PaymentDetail.class);
                        latLng = new LatLng(pd.getLatitude(), pd.getLongitude());
                        nameEditText.setText(pd.getName());
                        emailEditText.setText(pd.getEmail());
                        address1EditText.setText(pd.getAddress1());
                        address2EditText.setText(pd.getAddress2());
                        phoneEditText.setText(pd.getPhoneNumber());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return root;
    }

    public void uploadPaymentDetail() {
        PaymentDetail pd = new PaymentDetail();
        pd.setName(nameEditText.getText().toString());
        pd.setEmail(emailEditText.getText().toString());
        pd.setPhoneNumber(phoneEditText.getText().toString());
        pd.setAddress1(address1EditText.getText().toString());
        pd.setAddress2(address2EditText.getText().toString());
        pd.setLatitude(latLng.latitude);
        pd.setLongitude(latLng.longitude);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mFirestore.collection("paymentDetails").document(user.getUid())
                .set(pd, SetOptions.merge());
    }

    @Override
    public void submitPaymentDetail() {
        uploadPaymentDetail();
    }

    public PaymentDetail getPaymentDetail() {
        PaymentDetail pd = new PaymentDetail();
        pd.setName(nameEditText.getText().toString());
        pd.setEmail(emailEditText.getText().toString());
        pd.setPhoneNumber(phoneEditText.getText().toString());
        pd.setAddress1(address1EditText.getText().toString());
        pd.setAddress2(address2EditText.getText().toString());
        pd.setLatitude(latLng.latitude);
        pd.setLongitude(latLng.longitude);
        return pd;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
                Log.d("PLacesapi", "OOKay bro");
                Place place = Autocomplete.getPlaceFromIntent(data);
                address1EditText.setText(place.getAddress());

//                cleaningSite.setAddress(place.getAddress());
                latLng = place.getLatLng();
//                cleaningSite.setGeoPoint(new GeoPoint(latLng.latitude, latLng.longitude));
//            }
        }
    }

    public LatLng getLatLng() {
        return latLng;
    }
}