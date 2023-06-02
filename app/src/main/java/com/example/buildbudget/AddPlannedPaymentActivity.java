package com.example.buildbudget;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddPlannedPaymentActivity extends AppCompatActivity {

    private EditText editAmount, editDescription;
    private DatePicker datePicker;
    private Button btnAddPlannedPayment;
    private DatabaseReference plannedPaymentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_planned_payment);

        editAmount = findViewById(R.id.editAmount);
        editDescription = findViewById(R.id.editDescription);
        datePicker = findViewById(R.id.datePicker);
        btnAddPlannedPayment = findViewById(R.id.btnAddPlannedPayment);

        // Get the reference to the "planned_payments" node in your Firebase Realtime Database
        plannedPaymentsRef = FirebaseDatabase.getInstance().getReference("planned_payments");

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
        plannedPayment.setDescription(description);

        // Create a Calendar instance to set the payment date
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        plannedPayment.setDate(calendar.getTimeInMillis());

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
