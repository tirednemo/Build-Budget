package com.example.buildbudget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DebtActivity extends AppCompatActivity {
    private EditText input1;
    private EditText input2;
    private TextView totaldebt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt);
        input1=(EditText) findViewById(R.id.input1);
        input2=(EditText) findViewById(R.id.input2);
        totaldebt=(TextView) findViewById(R.id.totaldebt);


    }

    public double getInput(EditText editText)
    {
        if(editText.getText().toString().equals("")) return 0.00;
        else return Double.parseDouble(editText.getText().toString());
    }
    public void remainingDebt(View view){
        double n1=getInput(input1);
        double n2=getInput(input2);
        String value = totaldebt.getText().toString();
        Double val =Double.valueOf(value);
        if(n1>=n2)
        {
            double diff=val+n1-n2;
            totaldebt.setText(String.valueOf(diff));

        }
        else
        {
            totaldebt.setText(String.valueOf(0.00));
        }
        input1.getText().clear();
        input2.getText().clear();


    }
    public void reset(View view) {
        input1.getText().clear();
        input2.getText().clear();
        totaldebt.setText(String.valueOf(0.00));
    }




}
