package com.example.buildbudget;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    AuthenticationPagerAdapter adapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        new Handler().postDelayed(() -> {
            Intent start;
            if (currentUser != null && currentUser.isEmailVerified()) {
                start = new Intent(this, DashboardActivity.class);
                start.putExtra("com.example.buildbudget.user", currentUser.getUid());
            } else {
                start = new Intent(getApplicationContext(), TutorialActivity.class);
            }
            startActivity(start);
            finish();
        }, 2000);
    }
}
