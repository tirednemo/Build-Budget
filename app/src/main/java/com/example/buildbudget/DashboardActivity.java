package com.example.buildbudget;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout mDrawer;
    ImageView mNotification;

    FloatingActionButton mAddRecord, mAddOCR, mAddManual;
    TextView addText, OCRText, manualText;
    Boolean isAllFABsVisible;

    RecyclerView accountCardRecyclerView, recordRecyclerView;
    AccountCardItemsRecycleViewAdapter accountCardsRecyclerViewAdapter;
    AccountCardItemsRecycleViewAdapter.AccountCardItemsOnClickHandler accountCardItemsOnClickHandler;
    RecordItemsRecycleViewAdapter recordsRecyclerViewAdapter;
    RecordItemsRecycleViewAdapter.RecordItemsOnClickHandler recordItemsOnClickHandler;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.d_grey));

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


// TODO: Navigation Drawer for sidebar

        NavigationView navigationView = findViewById(R.id.nav_view);

        View navHead = navigationView.getHeaderView(0);
        ImageView me = navHead.findViewById(R.id.me);
        Glide.with(getApplicationContext())
                .load(currentUser.getPhotoUrl())
                .into(me);
        me.setOnClickListener(view -> {
            Intent start = new Intent(this, ProfileActivity.class);
            startActivity(start);
        });

        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.open_nav, R.string.close_nav);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//            navigationView.setCheckedItem(R.id.nav_home);
//        }


// TODO:  Floating Action Button for new record

        mAddRecord = findViewById(R.id.add_record);
        mAddOCR = findViewById(R.id.ocr_add);
        mAddManual = findViewById(R.id.manual_add);
        addText = findViewById(R.id.add_record_text);
        OCRText = findViewById(R.id.ocr_add_text);
        manualText = findViewById(R.id.manual_add_text);
        isAllFABsVisible = false;

        mAddRecord.setOnClickListener(view -> {
            if (!isAllFABsVisible) {
                mAddOCR.show();
                mAddManual.show();
                addText.setVisibility(View.VISIBLE);
                OCRText.setVisibility(View.VISIBLE);
                manualText.setVisibility(View.VISIBLE);
                isAllFABsVisible = true;
            } else {
                mAddOCR.hide();
                mAddManual.hide();
                addText.setVisibility(View.GONE);
                OCRText.setVisibility(View.GONE);
                manualText.setVisibility(View.GONE);
                isAllFABsVisible = false;
            }
        });

        mAddOCR.setOnClickListener(view -> {
            //ocr activity
        });
        mAddManual.setOnClickListener(view -> {
            Intent start = new Intent(this, TransactionActivity.class);
            startActivity(start);
        });


// TODO:  Other Buttons

        mNotification = findViewById(R.id.wrap);
        mNotification.setOnClickListener(view -> {
            Intent start = new Intent(this, NotificationsActivity.class);
            startActivity(start);
        });


// TODO: Recycler View for accounts and date-ordered records

        getAccountCards();
        getRecords();

    }


    private void getAccountCards() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        ValueEventListener accountListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Account> list = new ArrayList<>();
                for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
                    Account account = accountSnapshot.getValue(Account.class);
                    list.add(account);
                }
                accountCardRecyclerView = findViewById(R.id.account_cards_view);
                accountCardsRecyclerViewAdapter = new AccountCardItemsRecycleViewAdapter(list, getApplication(), accountCardItemsOnClickHandler);
                accountCardRecyclerView.setAdapter(accountCardsRecyclerViewAdapter);
                accountCardRecyclerView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this, LinearLayoutManager.HORIZONTAL, false));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        mDatabase.child("users").child(user.getUid()).child("accounts").addValueEventListener(accountListener);
    }

    private void getRecords() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        ValueEventListener recordListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Transaction> list = new ArrayList<>();
                for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
                    if (accountSnapshot.hasChild("Transaction")) {
                        DataSnapshot transactionSnapshot = accountSnapshot.child("Transaction");
                        for (DataSnapshot recordSnapshot : transactionSnapshot.getChildren()) {
                            Transaction record = recordSnapshot.getValue(Transaction.class);
                            record.Account = accountSnapshot.getKey();
                            list.add(record);
                        }
                    }
                }

                list.sort((t1, t2) -> {
                    if (t1.Date == null || t2.Date == null)
                        return 0;
                    return t2.Date.compareTo(t1.Date);
                });

                recordRecyclerView = findViewById(R.id.date_view);
                recordsRecyclerViewAdapter = new RecordItemsRecycleViewAdapter(list, getApplication(), recordItemsOnClickHandler);
                recordRecyclerView.setAdapter(recordsRecyclerViewAdapter);
                recordRecyclerView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this, LinearLayoutManager.VERTICAL, false));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDatabase.child("users").child(user.getUid()).child("accounts").addValueEventListener(recordListener);
    }


    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent start;
        int option = item.getItemId();

        switch (option) {

//            case R.id.nav_home:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//                break;
            case R.id.nav_acc:
                start = new Intent(this, AccountsActivity.class);
                startActivity(start);
                break;
//            case R.id.nav_banksync:
//                break;
//            case R.id.nav_stats:
//                break;
//

            case R.id.nav_budget:
                start = new Intent(this, BudgetActivity.class);
                startActivity(start);
                break;
            case R.id.nav_debts:
                start = new Intent(this, DebtActivity.class);
                startActivity(start);
                break;

//            case R.id.nav_budget:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//                break;
//            case R.id.nav_debts:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//                break;
//            case R.id.nav_ppay:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//                break;
//            case R.id.nav_goals:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//                break;
//            case R.id.nav_invests:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//                break;
//
            case R.id.nav_currency_ex:
                start = new Intent(this, CurrencyExchangeActivity.class);
                startActivity(start);
                break;
//            case R.id.nav_stock_ex:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//                break;
//
//            case R.id.nav_loyal:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//                break;
//            case R.id.nav_warranty:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//                break;
            case R.id.nav_stats:
                start = new Intent(this, StatisticsActivity.class);
                startActivity(start);
                break;
            case R.id.nav_settings:
                start = new Intent(this, SettingsActivity.class);
                startActivity(start);
                break;

            case R.id.nav_help:
                start = new Intent(this, HelpActivity.class);
                startActivity(start);
                break;
//            case R.id.nav_help:
//                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                start = new Intent(this, AuthenticationActivity.class);
                startActivity(start);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + option);
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}


class AccountCardViewHolder extends RecyclerView.ViewHolder {
    LinearLayout front, back;
    TextView title_front, title_back, balance, holder, provider_front, provider_back, number, expiry, title_heading, expireson, cvv_heading, cvv;

    AccountCardViewHolder(View itemView) {
        super(itemView);
        front = itemView.findViewById(R.id.card_layout_front);
        title_front = itemView.findViewById(R.id.card_title_front);
        balance = itemView.findViewById(R.id.card_bal_front);
        holder = itemView.findViewById(R.id.cardholder_front);
        provider_front = itemView.findViewById(R.id.card_provider_front);

        back = itemView.findViewById(R.id.card_layout_back);
        title_heading = itemView.findViewById(R.id.card_or_account);
        title_back = itemView.findViewById(R.id.card_title_back);
        provider_back = itemView.findViewById(R.id.card_provider_back);
        number = itemView.findViewById(R.id.card_number_back);
        expireson = itemView.findViewById(R.id.expires_on);
        expiry = itemView.findViewById(R.id.card_expiry_back);
        cvv_heading = itemView.findViewById(R.id.cvv);
        cvv = itemView.findViewById(R.id.card_cvv_back);
    }
}

class AccountCardItemsRecycleViewAdapter extends RecyclerView.Adapter<AccountCardViewHolder> {
    public interface AccountCardItemsOnClickHandler {
        void onClick(int index);
    }

    List<Account> list;
    Context context;
    AccountCardItemsOnClickHandler clickHandler;

    public AccountCardItemsRecycleViewAdapter(List<Account> list, Context context, AccountCardItemsOnClickHandler clickHandler) {
        this.list = list;
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @NonNull
    @Override
    public AccountCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        return new AccountCardViewHolder(inflater.inflate(R.layout.layout_account_cards, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccountCardViewHolder viewHolder, int position) {
        final int index = viewHolder.getAdapterPosition();

        Account account = list.get(position);
        viewHolder.title_front.setText(account.Title);
        viewHolder.title_back.setText(account.Title);
        viewHolder.balance.setText(String.valueOf(account.Balance));
        viewHolder.number.setText(account.Number);
        if (Objects.equals(account.Type, "Debit Card") || Objects.equals(account.Type, "Credit Card")) {
            viewHolder.holder.setText(account.Holder);
            viewHolder.provider_front.setText(account.Provider);
            viewHolder.provider_back.setText(account.Provider);
            viewHolder.title_heading.setText("CARD NUMBER");
            viewHolder.expireson.setText("EXPIRES ON");
            viewHolder.expiry.setText(account.Validity);
            viewHolder.cvv_heading.setText("CVV");
            viewHolder.cvv.setText("***");
        } else {
            viewHolder.holder.setText("");
            viewHolder.provider_front.setText("");
            viewHolder.provider_back.setText("");
            viewHolder.title_heading.setText("ACCOUNT NUMBER");
            viewHolder.expireson.setText("");
            viewHolder.expiry.setText("");
            viewHolder.cvv_heading.setText("");
            viewHolder.cvv.setText("");
        }

        AnimatorSet frontAnim = (AnimatorSet) AnimatorInflater.loadAnimator(viewHolder.itemView.getContext(), R.animator.front_animator);
        AnimatorSet backAnim = (AnimatorSet) AnimatorInflater.loadAnimator(viewHolder.itemView.getContext(), R.animator.back_animator);
        float scale = viewHolder.itemView.getContext().getResources().getDisplayMetrics().density;
        viewHolder.front.setCameraDistance(8000 * scale);
        viewHolder.back.setCameraDistance(8000 * scale);

        viewHolder.front.setOnClickListener(v -> {
            frontAnim.setTarget(viewHolder.front);
            frontAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    viewHolder.front.setVisibility(View.GONE);
                    backAnim.setTarget(viewHolder.front);
                    viewHolder.back.setVisibility(View.VISIBLE);
                }
            });
            frontAnim.start();
            backAnim.start();
        });
        viewHolder.back.setOnClickListener(v -> {
            frontAnim.setTarget(viewHolder.back);
            frontAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    viewHolder.back.setVisibility(View.GONE);
                    backAnim.setTarget(viewHolder.back);
                    viewHolder.front.setVisibility(View.VISIBLE);
                }
            });
            frontAnim.start();
            backAnim.start();
        });

        viewHolder.front.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
//                Intent start = new Intent(v.getContext(), RecordsActivity.class);
//                v.getContext().startActivity(start);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}


class RecordViewHolder extends RecyclerView.ViewHolder {
    TextView title, account, amount;

    RecordViewHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.add_account);
        account = itemView.findViewById(R.id.textView3);
        amount = itemView.findViewById(R.id.textView4);
    }
}

class RecordItemsRecycleViewAdapter extends RecyclerView.Adapter<RecordViewHolder> {
    public interface RecordItemsOnClickHandler {
        void onClick(int index);
    }

    List<Transaction> list;
    Context context;
    RecordItemsOnClickHandler clickHandler;

    public RecordItemsRecycleViewAdapter(List<Transaction> list, Context context, RecordItemsOnClickHandler clickHandler) {
        this.list = list;
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        return new RecordViewHolder(inflater.inflate(R.layout.layout_record_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder viewHolder, int position) {
        final int index = viewHolder.getAdapterPosition();

        Transaction record = list.get(position);
        viewHolder.title.setText(record.Note);
        viewHolder.account.setText(record.Account);
        viewHolder.amount.setText(String.valueOf(record.Amount));

        viewHolder.itemView.setOnClickListener(v -> clickHandler.onClick(index));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}



