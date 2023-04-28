package com.example.buildbudget;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.d_yellow));
        tick = findViewById(R.id.tick);
        timer = findViewById(R.id.resend_timer);
//        new CountDownTimer(120000, 1000) {
//            public void onTick(long millisUntilFinished) {
//                NumberFormat f = new DecimalFormat("00");
//                long min = (millisUntilFinished / 60000) % 60;
//                long sec = (millisUntilFinished / 1000) % 60;
//                timer.setText(f.format(min) + ":" + f.format(sec));
//            }
//
//            public void onFinish() {
//                timer.setText("00:00");
//                confirm.setEnabled(true);
//                confirm.setBackgroundColor(getResources().getColor(R.color.Black));
//            }
//        }.start();

        sentmail = findViewById(R.id.sentemail);
        tv = findViewById(R.id.tv);
        resend = findViewById(R.id.resend_button);
        confirm = findViewById(R.id.confirm2);

//        if (getIntent().hasExtra("com.example.buildbudget.register")) {
//            uid = getIntent().getExtras().getString("com.example.buildbudget.register");
//            FirebaseUser user = auth.getCurrentUser();
//            if (user != null) {


//        if (getIntent().hasExtra("com.example.buildbudget.register")) {
//            sentmail.setText("We sent a code to " + getIntent().getExtras().getString("com.example.buildbudget.mail"));
            auth = FirebaseAuth.getInstance();

            Intent intent = getIntent();
            if (intent != null && intent.getData() != null) {
                sentmail.setVisibility(View.INVISIBLE);
                tv.setVisibility(View.INVISIBLE);
                timer.setVisibility(View.INVISIBLE);
                resend.setVisibility(View.INVISIBLE);

                String emailLink = intent.getData().toString();


                if (auth.isSignInWithEmailLink(emailLink)) {

                    String name = intent.getExtras().getString("com.example.buildbudget.name");
                    String email = intent.getExtras().getString("com.example.buildbudget.mail");

                    // The client SDK will parse the code from the link for you.
                    auth.signInWithEmailLink(email, emailLink)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Successfully signed in with email link!");
                                        tick.setVisibility(View.VISIBLE);
                                        sentmail.setTextSize(20);
                                        sentmail.setText("Verification successful");
                                        sentmail.setVisibility(View.VISIBLE);
                                        AuthResult result = task.getResult();
                                        // You can access the new user via
                                        // Additional user info profile *not* available via:
                                        // result.getAdditionalUserInfo().getProfile() == null
                                        // You can check if the user is new or existing:
                                        // result.getAdditionalUserInfo().isNewUser()
                                        confirm.setOnClickListener(view ->
                                        {
                                            mDatabase = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
                                            FirebaseUser usr = result.getUser();
                                            if (usr != null) {
                                                User user = new User(name, email);
                                                mDatabase.child("users").child(usr.getUid()).setValue(user);
                                                Intent start = new Intent(getApplicationContext(), DashboardActivity.class);
                                                start.putExtra("com.example.buildbudget.user", usr.getUid());
                                                startActivity(start);
                                            }
                                        });
                                    } else {
                                        Log.e(TAG, "Error signing in with email link", task.getException());
                                    }
                                }
                            });
                }
            }
//        }
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