package com.example.buildbudget;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class AuthenticationActivity extends AppCompatActivity {
    ViewPager2 pager;
    TabLayout tabLayout;
    AuthenticationPagerAdapter adapter;
    public boolean first_time = false;

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
        TextView heading = findViewById(R.id.verify_heading);
        if (pager.getCurrentItem() == 0)
            heading.setText("Registration");
        else
            heading.setText("Login");

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new AuthenticationPagerAdapter(fragmentManager, getLifecycle());
        pager.setAdapter(adapter);

        if (!getIntent().hasExtra("origin")) {
            tabLayout.getTabAt(1).select();
            pager.setCurrentItem(1);
            heading.setText("Login");
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null) {
                            String mode = deepLink.getQueryParameter("mode");
                            String oobCode = deepLink.getQueryParameter("oobCode");

                            if ("verifyEmail".equals(mode)) {
                                try {
                                    mAuth.applyActionCode(oobCode).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            first_time = true;
                                            tabLayout.getTabAt(1).select();
                                            pager.setCurrentItem(1);
                                            heading.setText("Login");
                                        } else {
                                            // Failed to apply the action code
                                        }
                                    });
                                } catch (Exception e) {
                                    // Handle any exceptions while applying the action code
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(this, e -> {
                    // Handle any error while retrieving the dynamic link
                });

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

    public void onBackPressed(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
