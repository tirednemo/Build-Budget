package com.example.buildbudget;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    DatabaseReference mDatabase;
    TextView username;
    ImageView photo;
    FirebaseAuth auth;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.d_green));

        mDatabase = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        username = findViewById(R.id.textView14);
        photo = findViewById(R.id.pro_pic);

        auth = FirebaseAuth.getInstance();
        FirebaseUser usr = auth.getCurrentUser();
        mDatabase.child("users").child(usr.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    user = new User();
                    user.name = usr.getDisplayName();
                    user.photo = String.valueOf(task.getResult().child("photo").getValue());
                    username.setText(user.name);
                    Glide.with(getApplicationContext())
                            .load(user.photo)
                            .into(photo);
                }
            }
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