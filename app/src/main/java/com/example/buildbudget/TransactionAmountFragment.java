package com.example.buildbudget;

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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TransactionAmountFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    MaterialCardView account_to_cntnr;
    Spinner account_from_spinner, account_to_spinner;
    ImageButton income, expense, transfer;
    Button confirm;
    TextView account_from_balance, invalid;
    EditText amount;

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
        TransactionActivity parent = (TransactionActivity) getActivity();
        parent.heading.setText("Summary");

        getAccounts();
        account_to_cntnr = v.findViewById(R.id.account_to_cntnr);
        account_from_spinner = v.findViewById(R.id.account_from_spinner);
        account_to_spinner = v.findViewById(R.id.account_to_spinner);
        income = v.findViewById(R.id.incomeButton);
        expense = v.findViewById(R.id.expenseButton);
        transfer = v.findViewById(R.id.transferButton);
        account_from_balance = v.findViewById(R.id.account_from_balance);
        amount = v.findViewById(R.id.amount);
        invalid = v.findViewById(R.id.amount_invalid);
        confirm = v.findViewById(R.id.confirm_button);

        amount.setEnabled(false);

        income.setOnClickListener(v1 -> {
            amount.setEnabled(true);
            confirm.setEnabled(false);
            invalid.setVisibility(View.INVISIBLE);
            parent.categoryIcon.setImageResource(R.drawable.cash);
            parent.categoryName = "Income";
            confirm.setBackgroundColor(getResources().getColor(R.color.Disabled));
            income.setBackgroundTintList(getResources().getColorStateList(R.color.d_teal));
            expense.setBackgroundTintList(getResources().getColorStateList(R.color.Orange));
            transfer.setBackgroundTintList(getResources().getColorStateList(R.color.Yellow));
            account_to_cntnr.setVisibility(View.GONE);

            amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!amount.getText().toString().isEmpty()) {
                        confirm.setEnabled(true);
                        confirm.setBackgroundColor(getResources().getColor(R.color.Black));
                        confirm.setOnClickListener(view ->
                        {
                            TransactionDetailsFragment newFragment = new TransactionDetailsFragment();

                            Bundle bundle = new Bundle();
                            bundle.putString("account", account_from_spinner.getSelectedItem().toString());
                            bundle.putDouble("income", Double.parseDouble(amount.getText().toString()));
                            newFragment.setArguments(bundle);

                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                            transaction.replace(R.id.transaction_frame, newFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        });
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        });

        expense.setOnClickListener(v1 -> {
            amount.setEnabled(true);
            confirm.setEnabled(false);
            invalid.setVisibility(View.INVISIBLE);
            confirm.setBackgroundColor(getResources().getColor(R.color.Disabled));
            income.setBackgroundTintList(getResources().getColorStateList(R.color.Teal));
            expense.setBackgroundTintList(getResources().getColorStateList(R.color.d_orange));
            transfer.setBackgroundTintList(getResources().getColorStateList(R.color.Yellow));
            account_to_cntnr.setVisibility(View.GONE);

            amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!amount.getText().toString().isEmpty()) {
                        confirm.setEnabled(true);
                        confirm.setBackgroundColor(getResources().getColor(R.color.Black));
                        confirm.setOnClickListener(view ->
                        {
                            if (Double.parseDouble(amount.getText().toString()) <= Double.parseDouble(account_from_balance.getText().toString())) {
                                TransactionDetailsFragment newFragment = new TransactionDetailsFragment();

                                Bundle bundle = new Bundle();
                                bundle.putString("account", account_from_spinner.getSelectedItem().toString());
                                bundle.putDouble("expense", Double.parseDouble(amount.getText().toString()));
                                newFragment.setArguments(bundle);

                                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                transaction.replace(R.id.transaction_frame, newFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            } else {
                                invalid.setVisibility(View.VISIBLE);
                                amount.setText(null);
                            }
                        });
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        });

        transfer.setOnClickListener(v1 -> {
            amount.setEnabled(true);
            confirm.setEnabled(false);
            invalid.setVisibility(View.INVISIBLE);
            parent.categoryIcon.setImageResource(R.drawable.transfer);
            parent.categoryName = "Transfer";
            confirm.setBackgroundColor(getResources().getColor(R.color.Disabled));
            income.setBackgroundTintList(getResources().getColorStateList(R.color.Teal));
            expense.setBackgroundTintList(getResources().getColorStateList(R.color.Orange));
            transfer.setBackgroundTintList(getResources().getColorStateList(R.color.d_yellow));
            account_to_cntnr.setVisibility(View.VISIBLE);

            amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!amount.getText().toString().isEmpty() && account_from_spinner.getSelectedItem() != account_to_spinner.getSelectedItem()) {
                        confirm.setEnabled(true);
                        confirm.setBackgroundColor(getResources().getColor(R.color.Black));
                        confirm.setOnClickListener(view ->
                        {
                            if (Double.parseDouble(amount.getText().toString()) <= Double.parseDouble(account_from_balance.getText().toString())) {
                                TransactionDetailsFragment newFragment = new TransactionDetailsFragment();

                                Bundle bundle = new Bundle();
                                bundle.putString("account_from", account_from_spinner.getSelectedItem().toString());
                                bundle.putString("account_to", account_to_spinner.getSelectedItem().toString());
                                bundle.putDouble("transfer", Double.parseDouble(amount.getText().toString()));
                                newFragment.setArguments(bundle);

                                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                transaction.replace(R.id.transaction_frame, newFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            } else {
                                invalid.setVisibility(View.VISIBLE);
                                amount.setText(null);
                            }
                        });
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        });


        return v;
    }

    private void getAccounts() {
        List<String> list = new ArrayList<>();


        mDatabase.child("users").child(user.getUid()).child("accounts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
                            Account account = accountSnapshot.getValue(Account.class);
                            list.add(account.Title);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        account_from_spinner.setAdapter(adapter);
                        account_to_spinner.setAdapter(adapter);

                        account_from_spinner.setOnItemSelectedListener(TransactionAmountFragment.this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        mDatabase.child("users")
                .child(user.getUid())
                .child("accounts")
                .child(item)
                .child("Balance").get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        account_from_balance.setText(String.valueOf(task.getResult().getValue()));
                    }
                });
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
