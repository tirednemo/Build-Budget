package com.example.buildbudget;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AccountCreationActivity extends AppCompatActivity {

    ViewPager2 pager;
    AccountCreationPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.d_violet));

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        pager = findViewById(R.id.account_creation_pager);

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new AccountCreationPagerAdapter(fragmentManager, getLifecycle());
        pager.setAdapter(adapter);

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

class AccountCreationPagerAdapter extends FragmentStateAdapter {
    public AccountCreationPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
            return new AccountOptionsTabFragment();
        else if (position == 1)
            return new BankAccountTabFragment();
//        else if (position == 2)
//            return new MobileAccountTabFragment();
//        else if (position == 4)
//            return new ImportAccountTabFragment();
        else
            return new ManualAccountTabFragment();
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
