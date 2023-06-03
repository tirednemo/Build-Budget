package com.example.buildbudget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PlannedPaymentActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPlannedPayments;
    private Button newr;
    private ImageButton BackButtonpp;
    private PlannedPaymentAdapter plannedPaymentAdapter;
    private List<PlannedPayment> plannedPaymentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planned_payment);

        // Initialize the RecyclerView
        recyclerViewPlannedPayments = findViewById(R.id.recyclerViewPlannedPayments);
        BackButtonpp = findViewById(R.id.BackButtonpp);
        BackButtonpp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        newr=findViewById(R.id.button3);
        newr.setOnClickListener(view -> {
            Intent start = new Intent(this, AddPlannedPaymentActivity.class);
            startActivity(start);
        });
        recyclerViewPlannedPayments.setLayoutManager(new LinearLayoutManager(this));

        // Create an empty list of planned payments
        plannedPaymentsList = new ArrayList<>();

        // Create the adapter and set it to the RecyclerView
        plannedPaymentAdapter = new PlannedPaymentAdapter(plannedPaymentsList);
        recyclerViewPlannedPayments.setAdapter(plannedPaymentAdapter);

        // Retrieve the planned payments data from Firebase Realtime Database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference().child("users").child(user.getUid()).child("PlannedPayment");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot plannedPaymentSnapshot : dataSnapshot.getChildren()) {
                            String paymentName = plannedPaymentSnapshot.child("name").getValue(String.class);
                            Double paymentAmount = plannedPaymentSnapshot.child("amount").getValue(Double.class);
                            String paymentDate = plannedPaymentSnapshot.child("date").getValue(String.class);
                            String paymentId = plannedPaymentSnapshot.child("id").getValue(String.class);

                            if (paymentName != null && paymentAmount != null) {
                                PlannedPayment plannedPayment = new PlannedPayment(paymentName, paymentAmount,paymentDate,paymentId);
                                plannedPaymentsList.add(plannedPayment);
                            }
                        }
                        Log.d("PlannedPaymentActivity", "Data Retrieval done!");

                        // Notify the adapter that the data has changed
                        plannedPaymentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle the error, if any
                    }
                });
    }
}
