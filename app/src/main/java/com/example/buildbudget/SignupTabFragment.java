package com.example.buildbudget;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignupTabFragment extends Fragment {
    public static final String TAG = "YOUR-TAG-NAME";
    private static final int REQ_ONE_TAP = 2;
    private boolean showOneTapUI = true;
    SignInButton btSignIn;
    GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
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

//        oneTapClient = Identity.getSignInClient((Activity) getContext());
//        signUpRequest = BeginSignInRequest.builder()
//                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true).
//                        .setServerClientId(getString(R.string.your_web_client_id))
//                        .setFilterByAuthorizedAccounts(false)
//                        .build())
//                .build();

//        btSignIn = btSignIn.findViewById(R.id.googlesignup);
//
//        // Initialize sign in options the client-id is copied form google-services.json file
//        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("438431947620-ecpi41uk3dhhf4mv8g8q993k3vs49ltm.apps.googleusercontent.com")
//                .requestEmail()
//                .build();
//
//        // Initialize sign in client
//        googleSignInClient = GoogleSignIn.getClient((Activity) getContext(), googleSignInOptions);
//
//        btSignIn.setOnClickListener((View.OnClickListener) view -> {
//            // Initialize sign in intent
//            Intent intent = googleSignInClient.getSignInIntent();
//            // Start activity for result
//            startActivityForResult(intent, 100);
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup_tab, container, false);

        name = v.findViewById(R.id.name);
        email = v.findViewById(R.id.date);
        password = v.findViewById(R.id.password);
        email_status = v.findViewById(R.id.email_invalid);
        password_status = v.findViewById(R.id.password_invalid);
        confirmButton = v.findViewById(R.id.confirm);

//        oneTapClient.beginSignIn(signUpRequest)
//                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
//                    @Override
//                    public void onSuccess(BeginSignInResult result) {
//                        try {
//                            startIntentSenderForResult(result.getPendingIntent().getIntentSender(), REQ_ONE_TAP, null, 0, 0, 0);
//                        } catch (IntentSender.SendIntentException e) {
//                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
//                        }
//                    }
//                })
//                .addOnFailureListener((Activity) getContext(), new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // No Google Accounts found. Just continue presenting the signed-out UI.
//                        Log.d(TAG, e.getLocalizedMessage());
//                    }
//                });

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
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 100) {
//            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
//            if (signInAccountTask.isSuccessful()) {
//                try {
//                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
//                    if (googleSignInAccount != null) {
//                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
//                        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                // Check condition
//                                if (task.isSuccessful()) {
//                                    // When task is successful redirect to profile activity display Toast
//                                    startActivity(new Intent(MainActivity.this, ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                                } else {
//                                    //
//                                }
//                            }
//                        });
//                    }
//                } catch (ApiException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode) {
//            case REQ_ONE_TAP:
//                try {
//                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
//                    String idToken = credential.getGoogleIdToken();
//                    if (idToken != null) {
//                        // Got an ID token from Google. Use it to authenticate
//                        // with your backend.
//                        Log.d(TAG, "Got ID token.");
//                    }
//                } catch (ApiException e) {
//                    switch (e.getStatusCode()) {
//                        case CommonStatusCodes.CANCELED:
//                            Log.d(TAG, "One-tap dialog was closed.");
//                            // Don't re-prompt the user.
//                            showOneTapUI = false;
//                            break;
//                        case CommonStatusCodes.NETWORK_ERROR:
//                            Log.d(TAG, "One-tap encountered a network error.");
//                            // Try again or just ignore.
//                            break;
//                        default:
//                            Log.d(TAG, "Couldn't get credential from result."
//                                    + e.getLocalizedMessage());
//                            break;
//                    }
//                }
//                break;
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

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/build-budget-71a7f.appspot.com/o/test%20account.png?alt=media&token=b9cd2260-4aa7-462c-a058-cf30e644f8fb"))
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                }
                            });

                    ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                            .setUrl("https://buildbudget.page.link/bjYi")
                            .setHandleCodeInApp(true)
                            .setAndroidPackageName(
                                    "com.example.buildbudget",
                                    true,
                                    "1")
                            .setIOSBundleId("com.example.buildbudget")
                            .build();

                    user.sendEmailVerification(actionCodeSettings)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");

                            User usr = new User(name, mail);

                            mDatabase = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
                            mDatabase.child("users").child(user.getUid()).setValue(usr);

                            FirebaseAuth.getInstance().signOut();
                            Intent start = new Intent(getActivity(), VerificationActivity.class);
                            start.putExtra("com.example.buildbudget.mail", mail);
                            startActivity(start);
                            getActivity().finish();
                        }
                    });
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
