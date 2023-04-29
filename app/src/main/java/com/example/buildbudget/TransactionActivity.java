package com.example.buildbudget;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
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
    ViewPager2 pager;
    TransactionPagerAdapter adapter;
    ImageView category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.d_teal));

        category = findViewById(R.id.category);
        pager = findViewById(R.id.transaction_pager);
        TextView heading = findViewById(R.id.transaction_heading);
        if (pager.getCurrentItem() == 0)
            heading.setText("Summary");
        else if (pager.getCurrentItem() == 1)
            heading.setText("Categories");
        else
            heading.setText("Details");

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new TransactionPagerAdapter(fragmentManager, getLifecycle());
        pager.setAdapter(adapter);

        category.setOnClickListener(view -> TransactionActivity.this.runOnUiThread(() -> {
            if (pager.getCurrentItem() == 0)
                pager.setCurrentItem(1);
            else if (pager.getCurrentItem() == 1)
                pager.setCurrentItem(0);
        }));
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

class TransactionPagerAdapter extends FragmentStateAdapter {
    public TransactionPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
            return new TransactionAmountFragment();
        else if (position == 1)
            return new TransactionCategoryFragment();
        else
            return new TransactionDetailsFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
