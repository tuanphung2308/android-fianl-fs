package com.google.firebase.example.fireeats.java.tuan;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.model.User;
import com.google.firebase.example.fireeats.java.utils.FirebaseService;
import com.google.firebase.example.fireeats.java.utils.Tools;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ProfileFormActivity extends AppCompatActivity {

    private String[] array_states;
    private EditText editname, editEmail, editPhone, editAddress1, editAddress2;

    private final FirebaseFirestore db = FirebaseService.getInstance().getDb();
    User wanguser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_profile_data);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
        } else {
            // No user is signed in
        }
        System.out.println(user.getDisplayName());


        DocumentReference docRef = db.collection("users").document(user.getUid());//document("1pU3KTiMOZcFjiWwr6k1ni1zPJt2");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());

                        wanguser = document.toObject(User.class);
                        array_states = getResources().getStringArray(R.array.states);
                        editname = (EditText) findViewById(R.id.editname);
                        editname.setText(wanguser.getName());
                        editEmail=(EditText) findViewById(R.id.editemail);
                        editEmail.setText(wanguser.getEmail());
                        editPhone = (EditText) findViewById(R.id.edit_phone_number);
                        editPhone.setText(wanguser.getPhone());
                        editAddress1 = (EditText) findViewById(R.id.editaddress1);
                        editAddress1.setText(wanguser.getAddress1());
                        editAddress2 = (EditText) findViewById(R.id.editaddress2);
                        editAddress2.setText(wanguser.getAddress1());

                    } else {
                        Log.d("TAG", "No such document");
                        User newUser = new User(user.getDisplayName(), user.getEmail(), "1", "2", "3");
                        array_states = getResources().getStringArray(R.array.states);
                        editname = (EditText) findViewById(R.id.editname);
                        //editname.setText(user.getDisplayName());
                        editEmail=(EditText) findViewById(R.id.editemail);
                        //editEmail.setText(user.getEmail());
                        editPhone = (EditText) findViewById(R.id.edit_phone_number);
                        editAddress1 = (EditText) findViewById(R.id.editaddress1);
                        editAddress2 = (EditText) findViewById(R.id.editaddress2);
                        db.collection("users").document(user.getUid())
                            .set(newUser, SetOptions.merge());
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });


//        Map<String, Object> city = new HashMap<>();
//        city.put("name", "Los Angeles");
//        city.put("state", "CA");
//        city.put("country", "Quangwehr");
//
//        db.collection("users").document("testuser")
//                .set(city, SetOptions.merge());
//
//
//        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
//        mFirestore.collection("users").document(user.getUid())
//                .set(city, SetOptions.merge());

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.blue_200);
    }

    private void initComponent() {

    }

    private void showStateChoiceDialog(final Button bt) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setSingleChoiceItems(array_states, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                bt.setTextColor(Color.BLACK);
                bt.setText(array_states[which]);
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            System.out.println("WANGWANGWANGWANG");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            System.out.println("hello");
            System.out.println(editname.getText().toString()+editEmail.getText().toString());
            System.out.println(db);

            wanguser.setName(editname.getText().toString());
            wanguser.setAddress1(editAddress1.getText().toString());
            wanguser.setAddress2(editAddress2.getText().toString());
            wanguser.setEmail(editEmail.getText().toString());
            wanguser.setPhone(editPhone.getText().toString());

            db.collection("users").document(user.getUid())
                .set(wanguser, SetOptions.merge());



//            CollectionReference usersCollectionRef = db.collection("users");
//            User user = new User(editname.getText().toString(), editEmail.getText().toString(), editPhone.getText().toString(), editAddress.getText().toString(),
//                    editZipcode.getText().toString(), editCity.getText().toString(), editState.getText().toString());
//            System.out.println(user.getName()+user.getEmail());
//            usersCollectionRef.document(docId).set(user);
        }
        return super.onOptionsItemSelected(item);
    }
}
