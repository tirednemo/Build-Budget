package com.example.buildbudget;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SettingsActivity_Account extends AppCompatActivity {
    DatabaseReference mDatabase;
    TextView username;
    ImageView photo;
    FirebaseAuth auth;
    User user;
    EditText name, email, password;
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_account);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.d_orange));

        mDatabase = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        username = findViewById(R.id.textView6);
        photo = findViewById(R.id.prro_pic);
        name = findViewById(R.id.namaye);
        email = findViewById(R.id.emaill);
        password = findViewById(R.id.passwordd);
        confirm = findViewById(R.id.confirm3);

        auth = FirebaseAuth.getInstance();
        FirebaseUser usr = auth.getCurrentUser();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                name.setText(user.name);
                email.setText(user.email);
                username.setText(user.name);
                Glide.with(getApplicationContext())
                        .load(user.photo)
                        .into(photo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG",  databaseError.toException());
            }
        };
        mDatabase.child("users").child(usr.getUid()).addValueEventListener(postListener);

        confirm.setOnClickListener(view -> {
            Log.d("TAG", "tapped");
            if (!name.getText().toString().isEmpty()) {
                Log.d("TAG", "User profile to be updated.");
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name.getText().toString())
                        .build();

                usr.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "User profile updated.");
                        mDatabase.child("users").child(usr.getUid()).child("name").setValue(name.getText().toString());
                    }
                });
            }
//            if (!password.getText().toString().isEmpty()) {
//                Log.d("TAG", "User pass to be  updated.");
//                usr.updatePassword(password.toString())
//                        .addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                Log.d("TAG", "User password updated.");
//                            }
//                        });
//            }
        });
    }

    public void onBackPressed(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
