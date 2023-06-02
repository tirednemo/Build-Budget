package com.example.buildbudget;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buildbudget.databinding.ActivityBudgetBinding;
import com.example.buildbudget.databinding.ActivityTransactionBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BudgetActivity extends AppCompatActivity {
    private EditText input1;
    private TextView budg;
    private TextView exp;
    private ImageButton Backbuttonb;
    private double expenses;
    private float res;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        input1 = (EditText) findViewById(R.id.budgetentry);
        budg = (TextView) findViewById(R.id.budgetVal);
        exp=(TextView) findViewById(R.id.expenseVal);
      /*  Intent intent = getIntent();
        res=intent.getStringExtra("Expense");*/
        Intent intent2 = getIntent();
        res = intent2.getFloatExtra("expenses", 0.0f);
        Backbuttonb= (ImageButton) findViewById(R.id.Backbuttonb);
        Backbuttonb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


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
                    if (category != null && amount != null && !category.equals("Income")) {
                        expenses += transactionSnapshot.child("Amount").getValue(Double.class);
                    }
                }
            }
            exp.setText(String.valueOf(expenses));
        }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BudgetActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        }
    public double getInput(EditText editText)
    {
        if(editText.getText().toString().equals("")) return 0.00;
        else return Double.parseDouble(editText.getText().toString());
    }

    public void budget_set(View view){
        double n1=getInput(input1);
        budg.setText(String.valueOf(n1));
        input1.getText().clear();
    }
}