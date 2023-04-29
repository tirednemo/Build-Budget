package com.example.buildbudget;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SettingsActivity_Account extends AppCompatActivity {
    DatabaseReference mDatabase;
    TextView username;
    ImageView photo;
    EditText name;
    EditText email;
    User currentUser;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_account);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.d_orange));

        mDatabase = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        username = findViewById(R.id.textView6);
        photo = findViewById(R.id.category);
        name = findViewById(R.id.namee);
        email = findViewById(R.id.emaill);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            mDatabase.child("users")
                    .child(user.getUid())
                    .get().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            String namee = String.valueOf(task.getResult().child("name").getValue());
                            String eemail = String.valueOf(task.getResult().child("email").getValue());
                            String photoo = String.valueOf(task.getResult().child("photo").getValue());
                            username.setText(namee);
                            name.setText(namee);
                            email.setText(eemail);
                            Glide.with(getApplicationContext())
                                    .load(photoo)
                                    .into(photo);
                        }
                    });
        }

    }
    public void onBackPressed(View v)
    {
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
