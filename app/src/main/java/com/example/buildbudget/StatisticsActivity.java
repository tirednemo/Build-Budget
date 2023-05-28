package com.example.buildbudget;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Find the PieChart view
        pieChart = findViewById(R.id.pieChart);
        // Retrieve expense and income data from the intent
        //float expenses = getIntent().getFloatExtra("expenses",0.0f);
        //float income = getIntent().getFloatExtra("income",0.0f);
        float income=5;
        float expenses=10;


        List<PieEntry>pieEntryList=new ArrayList<>();
        List<Integer>colorList=new ArrayList<>();
        if(income!=0) {
            pieEntryList.add(new PieEntry(income,"Income"));
            colorList.add(getResources().getColor(R.color.Blue));
        }
        if(expenses!=0) {
            pieEntryList.add(new PieEntry(expenses,"Expenses"));
            colorList.add(getResources().getColor(R.color.d_magenta));
        }

        PieDataSet pieDataSet=new PieDataSet(pieEntryList,String.valueOf(income-expenses));
        pieDataSet.setColors(colorList);
        PieData pieData=new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.invalidate();






    }
}

/*    // Method to navigate to the StatisticsActivity : TO BE PUT IN TRANSACTIONS ACTTIVITY
    private void navigateToStatistics() {
        Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
        intent.putExtra("expenses", expenses);
        intent.putExtra("income", income);
        startActivity(intent);
    }*/

