package com.example.buildbudget;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buildbudget.databinding.ActivityBudgetBinding;
import com.example.buildbudget.databinding.ActivityTransactionBinding;

public class budgetActivity extends AppCompatActivity {
    ActivityBudgetBinding binding;
    ActivityTransactionBinding binding2;    //for the expense value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityBudgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String budgetval=binding.editTextNumber.getText().toString()+"Tk";
                binding.textView13.setText(budgetval);
                String expenseval=binding.editTextNumber.getText().toString()+"Tk";
                binding.textView15.setText(expenseval);
            }
        });
    }


}