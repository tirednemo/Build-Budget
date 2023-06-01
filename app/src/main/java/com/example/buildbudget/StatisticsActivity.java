package com.example.buildbudget;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private double expenses=0.0, income=0.0;
    private ImageButton BackButtonS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Find the PieChart view
        pieChart = findViewById(R.id.pieChart);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            expenses = extras.getFloat("expenses", 0.0f);
            income = extras.getFloat("income", 0.0f);
        }
        List<PieEntry>pieEntryList=new ArrayList<>();
        List<Integer>colorList=new ArrayList<>();
        if(income!=0) {
            pieEntryList.add(new PieEntry((float) income,"Income"));
            colorList.add(getResources().getColor(R.color.Blue));
        }
        if(expenses!=0) {
            pieEntryList.add(new PieEntry((float) expenses,"Expenses"));
            colorList.add(getResources().getColor(R.color.d_magenta));
        }

        PieDataSet pieDataSet=new PieDataSet(pieEntryList,String.valueOf(income-expenses));
        pieDataSet.setColors(colorList);
        PieData pieData=new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.invalidate();

        BackButtonS= (ImageButton) findViewById(R.id.BackButtonS);
        BackButtonS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }
}


