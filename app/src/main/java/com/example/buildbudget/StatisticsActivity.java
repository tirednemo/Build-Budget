package com.example.buildbudget;

import android.icu.text.AlphabeticIndex;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private double expenses = 0.0, income = 0.0;
    private ImageButton BackButtonS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        BackButtonS = findViewById(R.id.BackButtonS);
        BackButtonS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Find the PieChart view
        pieChart = findViewById(R.id.pieChart);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference().child("users").child(user.getUid()).child("accounts");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot transactionsSnapshot = accountSnapshot.child("Transaction");
                    for (DataSnapshot transactionSnapshot : transactionsSnapshot.getChildren()) {
                        String category = transactionSnapshot.child("Category").getValue(String.class);
                        Double amount = transactionSnapshot.child("Amount").getValue(Double.class);
                        Log.d("StatisticsActivity", "Amount: " + amount);
                        if (category != null && category.equals("Income") && amount != null) {
                            income += transactionSnapshot.child("Amount").getValue(Double.class);
                        }
                        else if(category!=null && amount != null) {
                            expenses += transactionSnapshot.child("Amount").getValue(Double.class);
                        }
                    }
                }
                Log.d("StatisticsActivity", "Total Income: " + income);

                List<PieEntry> pieEntryList = new ArrayList<>();
                List<Integer> colorList = new ArrayList<>();

                if (income != 0) {
                    pieEntryList.add(new PieEntry((float) income, "Income"));
                    colorList.add(ContextCompat.getColor(StatisticsActivity.this, R.color.Blue));
                }
                if (expenses != 0) {
                    pieEntryList.add(new PieEntry((float) expenses, "Expenses"));
                    colorList.add(ContextCompat.getColor(StatisticsActivity.this, R.color.d_magenta));
                }

                PieDataSet pieDataSet = new PieDataSet(pieEntryList, String.valueOf(income - expenses));
                pieDataSet.setColors(colorList);
                PieData pieData = new PieData(pieDataSet);

                pieChart.setData(pieData);
                pieChart.invalidate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(StatisticsActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}