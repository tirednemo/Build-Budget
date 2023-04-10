package com.example.buildbudget;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

public class AuthenticationActivity extends AppCompatActivity {
    ViewPager2 pager;
    TabLayout tabLayout;
    AuthenticationPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.d_yellow));

        pager = findViewById(R.id.authentication_pager);
        tabLayout = findViewById(R.id.authentication_tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("Sign up"));
        tabLayout.addTab(tabLayout.newTab().setText("Sign in"));
        TextView heading = findViewById(R.id.auth_heading);
        if (pager.getCurrentItem() == 0)
            heading.setText("Registration");
        else
            heading.setText("Login");

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new AuthenticationPagerAdapter(fragmentManager, getLifecycle());
        pager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
                if (pager.getCurrentItem() == 0)
                    heading.setText("Registration");
                else
                    heading.setText("Login");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }
}

class AuthenticationPagerAdapter extends FragmentStateAdapter {
    public AuthenticationPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       if (position == 0)
            return new SignupTabFragment();
        else
            return new LoginTabFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
