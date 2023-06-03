package com.example.buildbudget;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddPlannedPaymentActivity extends AppCompatActivity {

    private EditText editAmount, editDescription;
    private DatePicker datePicker;
    private Button btnAddPlannedPayment;
    private ImageButton BackButtonapp;
    private DatabaseReference plannedPaymentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_planned_payment);

        editAmount = findViewById(R.id.editAmount);
        editDescription = findViewById(R.id.editDescription);
        datePicker = findViewById(R.id.datePicker);
        btnAddPlannedPayment = findViewById(R.id.btnAddPlannedPayment);
        BackButtonapp = findViewById(R.id.BackButtonapp);
        BackButtonapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Get the reference to the "planned_payments" node in your Firebase Realtime Database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        plannedPaymentsRef = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference().child("users").child(user.getUid()).child("PlannedPayment");

        btnAddPlannedPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlannedPayment();
            }
        });
    }

    private void addPlannedPayment() {
        // Retrieve the input values
        double amount = Double.parseDouble(editAmount.getText().toString());
        String description = editDescription.getText().toString();
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        // Create a new PlannedPayment object
        PlannedPayment plannedPayment = new PlannedPayment();
        plannedPayment.setAmount(amount);

        plannedPayment.setName(description);
        // Create a Calendar instance to set the payment date
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(calendar.getTime());
        plannedPayment.setDate(dateString);

        // Generate a unique ID for the planned payment
        String paymentId = plannedPaymentsRef.push().getKey();
        plannedPayment.setId(paymentId);

        // Save the planned payment in the Firebase Realtime Database
        plannedPaymentsRef.child(paymentId).setValue(plannedPayment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddPlannedPaymentActivity.this, "Planned payment added successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after adding the planned payment
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddPlannedPaymentActivity.this, "Failed to add planned payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
