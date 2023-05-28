package com.example.buildbudget;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class TransactionActivity extends AppCompatActivity {
    FrameLayout frame;
    TextView heading;
    ImageView categoryIcon;
    String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.d_teal));

        categoryIcon = findViewById(R.id.category);
        categoryName = "Food & Drinks";
        categoryIcon.setImageResource(R.drawable.dining);
        frame = findViewById(R.id.transaction_frame);
        heading = findViewById(R.id.transaction_heading);

        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.add(R.id.transaction_frame, new TransactionAmountFragment());
        transaction1.commit();

        categoryIcon.setOnClickListener(view ->
        {
            FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
            TransactionCategoryFragment newFragment = new TransactionCategoryFragment();
            transaction2.replace(R.id.transaction_frame, newFragment);
            transaction2.addToBackStack(null);
            transaction2.commit();
        });
    }

    public void onBackPressed(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

