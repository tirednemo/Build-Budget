package com.example.buildbudget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

public class AccountOptionsTabFragment extends Fragment {
    public static final String TAG = "YOUR-TAG-NAME";
    private FirebaseAuth mAuth;
    ImageView add_bank, add_bkash, add_manual, add_import;


    public AccountOptionsTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account_options, container, false);

        add_bank = v.findViewById(R.id.add_bank);
        add_bkash = v.findViewById(R.id.add_bkash);
        add_manual = v.findViewById(R.id.add_manual);
        add_import = v.findViewById(R.id.add_import);


        add_bank.setOnClickListener(view ->
        {
            ViewPager2 pager = getActivity().findViewById(R.id.account_creation_pager);
            pager.setCurrentItem(1, false);
        });
        add_manual.setOnClickListener(view ->
        {
            ViewPager2 pager = getActivity().findViewById(R.id.account_creation_pager);
            pager.setCurrentItem(4, false);
        });

        return v;
    }
}