package com.example.buildbudget;

import android.app.Activity;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

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
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

public class TransactionAmountFragment extends Fragment {
    Button confirm;
        public TransactionAmountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction_amount, container, false);

        Bundle bundle = new Bundle();
        bundle.putString("date", "5/12");
        bundle.putInt("payee", 123);

        confirm = v.findViewById(R.id.confirm_button);
        confirm.setOnClickListener(v1 -> {
            ViewPager2 pager = getActivity().findViewById(R.id.transaction_pager);
            pager.setCurrentItem(2, true);

            TransactionDetailsFragment fragment2 = new TransactionDetailsFragment();
            fragment2.setArguments(bundle);

//            getActivity().getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.content, fragment2)
//                    .commit();
        });
        return v;
    }
}
