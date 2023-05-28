package com.example.buildbudget;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class TransactionDetailsFragment extends Fragment {
    FirebaseUser user;
    DatabaseReference mDatabase;
    StorageReference mStorage;
    EditText date, time, payee, note;
    Spinner status_spinner;
    ImageView attach_button;
    EditText attachment_name;
    Button confirm;
    private Uri filePath;
    String TxID;
    String category, account_to, account_from, key;
    Double amount;
    private final int PICK_IMAGE_REQUEST = 22;

    public TransactionDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction_details, container, false);
        TransactionActivity parent = (TransactionActivity) getActivity();
        parent.heading.setText("Details");
        TxID = UUID.randomUUID().toString();
        category = parent.categoryName;


        Bundle bundle = getArguments();
        if (bundle.containsKey("income")) {
            key = "income";
            account_from = bundle.getString("account");
            amount = bundle.getDouble("income");
        } else if (bundle.containsKey("expense")) {
            key = "expense";
            account_from = bundle.getString("account");
            amount = bundle.getDouble("expense");
        } else if (bundle.containsKey("transfer")) {
            category = null;
            key = "transfer";
            account_from = bundle.getString("account_from");
            account_to = bundle.getString("account_to");
            amount = bundle.getDouble("transfer");
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        date = v.findViewById(R.id.date);
        time = v.findViewById(R.id.time);
        payee = v.findViewById(R.id.payee);
        note = v.findViewById(R.id.note);
        status_spinner = v.findViewById(R.id.status);
        attach_button = v.findViewById(R.id.attach_button);
        attachment_name = v.findViewById(R.id.attachment);
        confirm = v.findViewById(R.id.continuee);


        date.setOnClickListener(v1 -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1);
                    }, year, month, day);

            datePickerDialog.show();
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.Black));
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.Black));
        });
        time.setOnClickListener(v1 -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (view, hourOfDay, minute1) -> time.setText(hourOfDay + ":" + minute1), hour, minute, false);

            timePickerDialog.show();
            timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.Black));
            timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.Black));
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.Status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status_spinner.setAdapter(adapter);

        attach_button.setOnClickListener(v2 ->
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(
                    Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
        });

        confirm.setOnClickListener(v3 -> {

            String dattime = date.getText().toString() + " " + time.getText().toString();
            String t1, t2, finalDate = "";
            Calendar newDate = Calendar.getInstance();
            if (date.getText().toString().isEmpty()) {
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                t1 = format.format(newDate.getTime());
                dattime = t1 + dattime;
            }
            if (time.getText().toString().isEmpty()) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                t2 = format.format(newDate.getTime());
                dattime = dattime + t2;
            }

            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
            try {
                Date date = format1.parse(dattime);
                finalDate = format2.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            CreateTransaction(finalDate, payee.getText().toString(), note.getText().toString(), status_spinner.getSelectedItem().toString(), "images/" + TxID);

        });
        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            filePath = data.getData();
        }
        if (filePath != null) {
            attachment_name.setText(filePath.toString());
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            mStorage
                    .child("images/" + TxID).putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> progressDialog.dismiss())
                    .addOnFailureListener(e -> progressDialog.dismiss())
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }
    }

    private void CreateTransaction(String date, String payee, String note, String status, String path) {

        Transaction tx = new Transaction(TxID, date, category, payee, note, status, path, Double.valueOf(amount));
        DatabaseReference ref = mDatabase.child("users").child(user.getUid()).child("accounts").child(account_from);

        ref.child("Balance").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Double balance = Double.valueOf(String.valueOf(task.getResult().getValue()));
                if (Objects.equals(key, "income"))
                    ref.child("Balance").setValue(balance + amount);
                else if (Objects.equals(key, "expense"))
                    ref.child("Balance").setValue(balance - amount);
                else {
                    ref.child("Balance").setValue(balance - amount);
                    Log.d("f", account_to);
                    mDatabase.child("users").child(user.getUid()).child("accounts").child(account_to).child("Balance").setValue(balance + amount);
                    mDatabase.child("users").child(user.getUid()).child("accounts").child(account_to).child("Transaction").child(TxID).setValue(tx);
                }
                ref.child("Transaction").child(TxID).setValue(tx);
            }
        });

        getActivity().finish();
    }
}
