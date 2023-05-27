package com.example.buildbudget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginTabFragment extends Fragment {
    public static final String TAG = "YOUR-TAG-NAME";
    private FirebaseAuth mAuth;
    EditText email;
    EditText password;
    TextView email_status;
    TextView password_status;
    TextView forgotButton;
    Button continueButton;

    public LoginTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login_tab, container, false);


        email = v.findViewById(R.id.email);
        password = v.findViewById(R.id.password);
        email_status = v.findViewById(R.id.email_invalid);
        password_status = v.findViewById(R.id.password_invalid);
        forgotButton = v.findViewById(R.id.forget_password);
        continueButton = v.findViewById(R.id.continuee);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    continueButton.setEnabled(true);
                    continueButton.setBackgroundColor(getResources().getColor(R.color.Black));
                    continueButton.setOnClickListener(view ->
                    {
                        Login(email.getText().toString(), password.getText().toString());
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return v;
    }

    //    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            reload();
//        }
//    }
    private void Login(String mail, final String pass) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener((Activity) getContext(), task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "signInWithEmail:success");
                AuthenticationActivity xxx = (AuthenticationActivity) getActivity();
                FirebaseUser user = mAuth.getCurrentUser();
                Intent start;
                if (xxx.first_time)
                    start = new Intent((Activity) getContext(), VerificationActivity.class);
                else
                    start = new Intent((Activity) getContext(), DashboardActivity.class);
                startActivity(start);
            } else {
                try {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthInvalidUserException e) {
                    password_status.setVisibility(View.GONE);
                    email_status.setVisibility(View.VISIBLE);
                    email.setBackgroundColor(getResources().getColor(R.color.Input_Invalid));
                    email_status.setText("Email doesn't exist");
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    email_status.setVisibility(View.GONE);
                    password_status.setVisibility(View.VISIBLE);
                    password.setBackgroundColor(getResources().getColor(R.color.Input_Invalid));
                    password_status.setText("Wrong credentials");
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }
}