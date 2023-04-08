package com.example.buildbudget;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignupTabFragment extends Fragment {
    public static final String TAG = "YOUR-TAG-NAME";
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    EditText name;
    EditText email;
    EditText password;
    TextView email_status;
    TextView password_status;
    Button confirmButton;

    public SignupTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup_tab, container, false);

        name = v.findViewById(R.id.name);
        email = v.findViewById(R.id.email);
        password = v.findViewById(R.id.password);
        email_status = v.findViewById(R.id.email_invalid);
        password_status = v.findViewById(R.id.password_invalid);
        confirmButton = v.findViewById(R.id.confirm);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!name.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    confirmButton.setEnabled(true);
                    confirmButton.setBackgroundColor(getResources().getColor(R.color.Black));
                    confirmButton.setOnClickListener(view ->
                    {
                        Signin(name.getText().toString(), email.getText().toString(), password.getText().toString());
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

    private void Signin(String name, String mail, final String pass) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                } else {
                    try {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthUserCollisionException e) {
                        password_status.setVisibility(View.GONE);
                        email_status.setVisibility(View.VISIBLE);
                        email.setBackgroundColor(getResources().getColor(R.color.Input_Invalid));
                        email_status.setText("Email already exists");
                    } catch (FirebaseAuthWeakPasswordException e) {
                        email_status.setVisibility(View.GONE);
                        password_status.setVisibility(View.VISIBLE);
                        password.setBackgroundColor(getResources().getColor(R.color.Input_Invalid));
                        password_status.setText("Use a stronger password");
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        });
    }
}
