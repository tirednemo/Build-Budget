package com.example.buildbudget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.Manifest;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankAccountTabFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    public static final String TAG = "YOUR-TAG-NAME";
    private static final int PERMISSION_REQUEST_READ_SMS = 1;
    private FirebaseAuth mAuth;
    EditText account_name, account_no;
    Spinner bank_name_spinner, account_type_spinner;
    Button confirm;
    Account account;

    public BankAccountTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account_bank, container, false);

        bank_name_spinner = v.findViewById(R.id.bank_spinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),
                R.array.Banks, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bank_name_spinner.setAdapter(adapter1);
        account_name = v.findViewById(R.id.bank_account_name);
        account_no = v.findViewById(R.id.bank_account_no);
        account_type_spinner = v.findViewById(R.id.bank_account_type);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.Account_types, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        account_type_spinner.setAdapter(adapter2);
        confirm = v.findViewById(R.id.confirm7);

        account_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!bank_name_spinner.getSelectedItem().toString().isEmpty() &&
                        !account_name.getText().toString().isEmpty() &&
                        !account_no.getText().toString().isEmpty() &&
                        !account_type_spinner.getSelectedItem().toString().isEmpty()) {

                    confirm.setEnabled(true);
                    confirm.setBackgroundColor(getResources().getColor(R.color.Black));
                    confirm.setOnClickListener(view ->
                    {
                        account = new Account(account_type_spinner.getSelectedItem().toString(),
                                account_name.getText().toString(),
                                account_no.getText().toString(), "BDT", 0.00);
                        account.Provider = bank_name_spinner.getSelectedItem().toString();
                        CreateAccount();
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
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void CreateAccount() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            readLatestSms();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Permission Required")
                    .setMessage("This app requires access to your SMS messages in order to sync your bank transactions.")
                    .setPositiveButton("Grant Permission", (dialog, which) -> {
                        requestPermissionLauncher.launch(Manifest.permission.READ_SMS);
                    })
                    .setNegativeButton("Deny", (dialog, which) -> {
                        Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    })
                    .show();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_SMS);
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    readLatestSms();
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private void readLatestSms() {
        Uri uri = Uri.parse("content://sms/inbox");
        String[] projection = {"address", "body", "date"};
        String selection = "address = ?";
        String[] selectionArgs;
        switch (account.Provider) {
            case "AB Bank Ltd.": {
                selectionArgs = new String[]{"ABBANK"};
                break;
            }
            case "Eastern Bank Ltd.": {
                selectionArgs = new String[]{"EBL."};
                break;
            }
            default: {
                selectionArgs = new String[]{""};
                break;
            }
        }
        String sortOrder = "date DESC";

        Cursor cursor = getActivity().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder + " LIMIT " + "1");

        if (cursor != null && cursor.moveToFirst()) {
            int bodyIndex = cursor.getColumnIndex("body");
            String body = cursor.getString(bodyIndex);
//            String parsedAccountNumber = parseAccountNumber(body, account.Number);
            double balance = parseBalance(body);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

            account.Balance = Double.valueOf(balance);
            mDatabase.child("users").child(user.getUid()).child("accounts").child(account.Title).setValue(account);

            readAllSms();

        }
        if (cursor != null)
            cursor.close();
    }

    private void readAllSms() {
        Uri uri = Uri.parse("content://sms/inbox");
        String[] projection = {"address", "body", "date"};
        String selection = "address = ?";
        String[] selectionArgs;
        switch (account.Provider) {
            case "AB Bank Ltd.": {
                selectionArgs = new String[]{"ABBANK"};
                break;
            }
            case "Eastern Bank Ltd.": {
                selectionArgs = new String[]{"EBL."};
                break;
            }
            default: {
                selectionArgs = new String[]{""};
                break;
            }
        }
        String sortOrder = "date ASC";

        Cursor cursor = getActivity().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        if (cursor != null) {
            int bodyIndex = cursor.getColumnIndex("body");

            while (cursor.moveToNext()) {
                String body = cursor.getString(bodyIndex);

                String TxID = UUID.randomUUID().toString();
                String date = parseTransactionDate(body);
                String category = parseTransactionType(body);
                double amount = parseTransactionAmount(body);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

                DatabaseReference ref = mDatabase.child("users").child(user.getUid()).child("accounts").child(account.Title);
                Transaction tx = new Transaction(TxID, date, category, body, Double.valueOf(amount));
                ref.child("Transaction").child(TxID).setValue(tx);

            }

            cursor.close();
            getActivity().finish();
        }

    }


    private String parseAccountNumber(String SMSBody, String AccountNumber) {
        return null;
    }

    private double parseBalance(String body) {
        String[] words = body.split(" ");
        for (int i = 0; i < words.length; i++) {
            if (words[i].contains("balance") || words[i].contains("Balance")) {
                String balanceString = words[i + 3];
                return Double.parseDouble(balanceString.substring(0, balanceString.length() - 1));
            }
        }
        return 0.0;
    }

    private String parseTransactionDate(String body) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm:ss", Locale.ENGLISH);
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");

        String[] words = body.split(" ");
        for (int i = 0; i < words.length; i++) {
            if (words[i].contains(":")) {
                String dateString = words[i - 1] + " " + words[i];
                try {
                    Date parsedDate = dateFormat.parse(dateString);
                    String formattedDate = desiredFormat.format(parsedDate);
                    return formattedDate;
                } catch (ParseException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        return null;
    }

    private String parseTransactionType(String body) {
        if (body.toLowerCase().contains("debited") || body.toLowerCase().contains("purchase") || body.toLowerCase().contains("payment")) {
            return "Others";
        } else if (body.toLowerCase().contains("credited") || body.toLowerCase().contains("received")) {
            return "Income";
        }
        return null;
    }

    private double parseTransactionAmount(String body) {
        String[] words = body.split(" ");
        String amountString = "0.0";
        for (int i = 0; i < words.length; i++) {
            if (words[i].equalsIgnoreCase("debited") || words[i].equalsIgnoreCase("credited")) {
                if (words[i + 1].equalsIgnoreCase("with")) {
                    amountString = words[i + 3].replaceAll("[^0-9.]", "");
                    break;
                } else if (words[i + 1].equalsIgnoreCase("from") || words[i + 1].equalsIgnoreCase("to")) {
                    amountString = words[i - 1].replaceAll("[^0-9.]", "");
                    break;
                }
            }
        }
        try {
            return Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return 0.0;
    }
}