package com.example.buildbudget;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class VerificationActivity extends AppCompatActivity {
    public static final String TAG = "YOUR-TAG-NAME";
    FirebaseAuth auth;
    DatabaseReference mDatabase;
    ImageView tick;
    TextView sentmail;
    TextView tv;
    TextView timer;
    TextView resend;
    Button confirm;
    EditText startAmount;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.d_yellow));
        tick = findViewById(R.id.tick);
        timer = findViewById(R.id.resend_timer);
        sentmail = findViewById(R.id.sentemail);
        tv = findViewById(R.id.tv);
        resend = findViewById(R.id.resend_button);
        startAmount = findViewById(R.id.startAmount);
        confirm = findViewById(R.id.confirm2);

        String email = "";
        if (getIntent().hasExtra("com.example.buildbudget.mail")) {
            email = getIntent().getExtras().getString("com.example.buildbudget.mail");
            sentmail.setText("We sent a code to " + email);

            new CountDownTimer(120000, 1000) {
                public void onTick(long millisUntilFinished) {
                    NumberFormat f = new DecimalFormat("00");
                    long min = (millisUntilFinished / 60000) % 60;
                    long sec = (millisUntilFinished / 1000) % 60;
                    timer.setText(f.format(min) + ":" + f.format(sec));
                }

                public void onFinish() {
                    timer.setText("00:00");
                    confirm.setEnabled(true);
                    confirm.setBackgroundColor(getResources().getColor(R.color.Black));
                }
            }.start();
        } else {
            auth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

            auth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user.isEmailVerified()) {
                        sentmail.setText("Thank you for verifying!");
                        timer.setVisibility(View.GONE);
                        tv.setVisibility(View.GONE);
                        resend.setVisibility(View.GONE);
                        tick.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(() -> {
                            tick.setVisibility(View.GONE);

                            startAmount.setVisibility(View.VISIBLE);
                            sentmail.setText("How much money do you have in your wallet right now?");

                            startAmount.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (!startAmount.getText().toString().isEmpty()) {
                                        confirm.setEnabled(true);
                                        startAmount.setTextColor(getResources().getColor(R.color.Black));
                                        confirm.setBackgroundColor(getResources().getColor(R.color.Black));
                                        confirm.setOnClickListener(view ->
                                        {
                                            Account account = new Account("Cash", "Cash",Double.valueOf(startAmount.getText().toString()));
                                            mDatabase.child("users").child(user.getUid()).child("accounts").child("Cash").setValue(account);

                                            Intent startt = new Intent(VerificationActivity.this, DashboardActivity.class);
                                            startt.putExtra("com.example.buildbudget.user", user.getUid());
                                            startActivity(startt);
                                        });
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                }
                            });
                        }, 2000);
                    } else {
                mDatabase.child("users").child(user.getUid()).removeValue();
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "WTH User account deleted.");
                                }
                            }
                        });
                auth.signOut();
                    }
                }
            });

        }

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