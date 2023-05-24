package com.example.buildbudget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class TutorialActivity extends AppCompatActivity {
    List<TutorialItemsClass> featureItems;
    ViewPager pager;
    TabLayout tabLayout;
    Button nextButton;
    TextView skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.d_violet));

        featureItems = new ArrayList<>();
        featureItems.add(new TutorialItemsClass(R.drawable.billing, "Track"));
        featureItems.add(new TutorialItemsClass(R.drawable.bank, "Sync"));
        featureItems.add(new TutorialItemsClass(R.drawable.positive_dynamic, "Analyse"));
        featureItems.add(new TutorialItemsClass(R.drawable.goal, "Plan"));
        featureItems.add(new TutorialItemsClass(R.drawable.pyramid_toy, "Play"));
        featureItems.add(new TutorialItemsClass(R.drawable.money_box, "Budget"));

        pager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tutorial_tab_layout);
        TutorialItemsPagerAdapter itemsPager_adapter = new TutorialItemsPagerAdapter(this, featureItems);
        pager.setAdapter(itemsPager_adapter);
        tabLayout.setupWithViewPager(pager, true);

        nextButton = findViewById(R.id.next);
        nextButton.setOnClickListener(view -> TutorialActivity.this.runOnUiThread(() -> {
            if (pager.getCurrentItem() < featureItems.size() - 1) {
                pager.setCurrentItem(pager.getCurrentItem() + 1);
                if (pager.getCurrentItem() == 5)
                    nextButton.setText(R.string.start);
            } else {
                System.out.println("here");
                Intent start = new Intent(getApplicationContext(), AuthenticationActivity.class);
                start.putExtra("origin","Tutorial");
                startActivity(start);
            }
        }));


        skipButton = findViewById(R.id.skip);
        skipButton.setOnClickListener(view -> TutorialActivity.this.runOnUiThread(() -> {
            pager.setCurrentItem(featureItems.size() - 1);
            nextButton.setText(R.string.start);
        }));

        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new The_slide_timer(), 3000, 5000);
    }

    public class The_slide_timer extends TimerTask {
        @Override
        public void run() {

            TutorialActivity.this.runOnUiThread(() -> {
                if (pager.getCurrentItem() < featureItems.size() - 1) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1);
                    if (pager.getCurrentItem() == 5)
                        nextButton.setText(R.string.start);
                }
            });
        }
    }
}

class TutorialItemsClass {
    private final int feature;
    private final String caption;

    public TutorialItemsClass(int image, String caption) {
        this.feature = image;
        this.caption = caption;
    }

    public int getFeatureImage() {
        return feature;
    }

    public String getFeatureCaption() {
        return caption;
    }
}

class TutorialItemsPagerAdapter extends PagerAdapter {
    private final Context context;
    private final List<TutorialItemsClass> tutorialItems;

    public TutorialItemsPagerAdapter(Context context, List<TutorialItemsClass> featureItems) {
        this.context = context;
        this.tutorialItems = featureItems;
    }

    @Override
    public int getCount() {
        return tutorialItems.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View sliderLayout = View.inflate(context, R.layout.layout_tutorial_items, null);

        ImageView feature_image = sliderLayout.findViewById(R.id.feature_image);
        TextView caption = sliderLayout.findViewById(R.id.caption);

        feature_image.setImageResource(tutorialItems.get(position).getFeatureImage());
        caption.setText(tutorialItems.get(position).getFeatureCaption());
        container.addView(sliderLayout);
        return sliderLayout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}