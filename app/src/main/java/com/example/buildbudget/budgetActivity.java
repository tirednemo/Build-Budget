package com.example.buildbudget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buildbudget.databinding.ActivityBudgetBinding;
import com.example.buildbudget.databinding.ActivityTransactionBinding;

public class budgetActivity extends AppCompatActivity {
    private EditText input1;
    private TextView budg;
    private TextView exp;
    private String res;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        input1 = (EditText) findViewById(R.id.budgetentry);
        budg = (TextView) findViewById(R.id.budgetVal);
        exp=(TextView) findViewById(R.id.expenseVal);
      /*  Intent intent = getIntent();
        res=intent.getStringExtra("Expense");*/



    }



    public double getInput(EditText editText)
    {
        if(editText.getText().toString().equals("")) return 0.00;
        else return Double.parseDouble(editText.getText().toString());
    }

    public void budget_set(View view){
        double n1=getInput(input1);
        budg.setText(String.valueOf(n1));
        exp.setText(res);
    }
    /*for expense value:
    add the following lines in transaction
     submitButton = findViewById(R.id.button7);
        Double in1=getInput(input1);
        Double in2=getInput(input2);
        Double res=remainingDebt();
        String diff=String.valueOf(res);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DebtActivity.this, budgetActivity.class);
                intent.putExtra("Expense", diff);
                startActivity(intent);
            }
        });
    }
     */

}