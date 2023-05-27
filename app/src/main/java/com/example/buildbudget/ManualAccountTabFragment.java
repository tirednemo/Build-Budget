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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
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

public class ManualAccountTabFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    public static final String TAG = "YOUR-TAG-NAME";
    private FirebaseAuth mAuth;
    EditText account_name, account_no, account_balance;
    Spinner account_type_spinner, account_currency_spinner;
    Button confirm;

    public ManualAccountTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account_manual, container, false);

        account_name = v.findViewById(R.id.account_name);
        account_no = v.findViewById(R.id.account_no);
        account_type_spinner = v.findViewById(R.id.account_type);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),
                R.array.Account_types, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        account_type_spinner.setAdapter(adapter1);

        account_currency_spinner = v.findViewById(R.id.account_currency);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.Currencies, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        account_currency_spinner.setAdapter(adapter2);

        account_balance = v.findViewById(R.id.account_balance);
        confirm = v.findViewById(R.id.confirm5);


        account_balance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!account_name.getText().toString().isEmpty() &&
                        !account_no.getText().toString().isEmpty() &&
                        !account_type_spinner.getSelectedItem().toString().isEmpty() &&
                        !account_currency_spinner.getSelectedItem().toString().isEmpty() &&
                        !account_balance.getText().toString().isEmpty()) {

                    confirm.setEnabled(true);
                    confirm.setBackgroundColor(getResources().getColor(R.color.Black));
                    confirm.setOnClickListener(view ->
                    {
                        CreateAccount(account_name.getText().toString(), account_no.getText().toString(), account_type_spinner.getSelectedItem().toString(), account_currency_spinner.getSelectedItem().toString(), account_balance.getText().toString());
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void CreateAccount(String name, String number, String type, String currency, String balance) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        Account account = new Account(type, name, number, currency, Double.valueOf(balance));
        mDatabase.child("users").child(user.getUid()).child("accounts").child(name).setValue(account);
        getActivity().finish();
    }
}
