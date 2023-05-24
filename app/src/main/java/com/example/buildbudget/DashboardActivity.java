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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

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

    DatabaseReference mDatabase;
    User currentUser;
    String uid, name, email, photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.d_grey));


// TODO: Retrieve user info from intent

        mDatabase = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        if (getIntent().hasExtra("com.example.buildbudget.user")) {
            uid = getIntent().getExtras().getString("com.example.buildbudget.user");
            mDatabase.child("users")
                    .child(uid)
                    .get().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            name = String.valueOf(task.getResult().child("name").getValue());
                            email = String.valueOf(task.getResult().child("email").getValue());
                            photo = String.valueOf(task.getResult().child("photo").getValue());
                        }
                    });
        }


// TODO: Navigation Drawer for sidebar

        NavigationView navigationView = findViewById(R.id.nav_view);

        View navHead = navigationView.getHeaderView(0);
        ImageView me = navHead.findViewById(R.id.me);
        Glide.with(getApplicationContext())
                .load(photo)
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

        List<Account> accountList;
        accountList = getAccounts();
        List<Records> recordList;
        recordList = getRecords();

        accountCardRecyclerView = findViewById(R.id.account_cards_view);
        accountCardsRecyclerViewAdapter = new AccountCardItemsRecycleViewAdapter(accountList, getApplication(), accountCardItemsOnClickHandler);
        accountCardRecyclerView.setAdapter(accountCardsRecyclerViewAdapter);
        accountCardRecyclerView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this, LinearLayoutManager.HORIZONTAL, false));

        recordRecyclerView = findViewById(R.id.date_view);
        recordsRecyclerViewAdapter = new RecordItemsRecycleViewAdapter(recordList, getApplication(), recordItemsOnClickHandler);
        recordRecyclerView.setAdapter(recordsRecyclerViewAdapter);
        recordRecyclerView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this, LinearLayoutManager.VERTICAL, false));
    }


    // Sample data for accounts
    private List<Account> getAccounts() {
        List<Account> list = new ArrayList<>();
        list.add(new Account("Debit Card", "card1", 160.0));
        list.add(new Account("Cash", "Cash", 40.0));
        return list;
    }

    // Sample data for records
    private List<Records> getRecords() {
        List<Records> list = new ArrayList<>();
        list.add(new Records("Expense", "bKash", "Chicken chaap & naan", 160.0));
        list.add(new Records("Expense", "Cash", "Bus", 40.0));
        list.add(new Records("Expense", "card1", "Tesla Model 3", 42990.0));
        list.add(new Records("Expense", "card1", "Surface Pro 8", 799.0));
        list.add(new Records("Expense", "bKash", "Cadbury Silk", 150.0));
        list.add(new Records("Expense", "Cash", "Coffee", 20.0));
        return list;
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
                start.putExtra("com.example.buildbudget.uid", uid);
                startActivity(start);
                break;
//            case R.id.nav_banksync:
//                break;
//            case R.id.nav_stats:
//                break;
//
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
//            case R.id.nav_currency_ex:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//                break;
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

            case R.id.nav_settings:
                start = new Intent(this, SettingsActivity.class);
                start.putExtra("com.example.buildbudget.uid", uid);
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
    TextView title_front, title_back, balance, holder, provider_front, provider_back, number, expiry;

    AccountCardViewHolder(View itemView) {
        super(itemView);
        front = itemView.findViewById(R.id.card_layout_front);
        title_front = itemView.findViewById(R.id.card_title_front);
        balance = itemView.findViewById(R.id.card_bal_front);
        holder = itemView.findViewById(R.id.cardholder_front);
        provider_front = itemView.findViewById(R.id.card_provider_front);

        back = itemView.findViewById(R.id.card_layout_back);
        title_back = itemView.findViewById(R.id.card_title_back);
        provider_back = itemView.findViewById(R.id.card_provider_back);
        number = itemView.findViewById(R.id.card_number_back);
        expiry = itemView.findViewById(R.id.card_expiry_back);
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
//        viewHolder.title_front.setText(account.title);
//        viewHolder.title_back.setText(account.title);
//        viewHolder.balance.setText(String.valueOf(account.balance));
//        viewHolder.holder.setText(account.holder);
//        viewHolder.provider_front.setText(account.provider);
//        viewHolder.provider_back.setText(account.provider);
//        viewHolder.number.setText(account.number);
//        if (account.type == "card")
//            viewHolder.expiry.setText(account.year);

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

    List<Records> list;
    Context context;
    RecordItemsOnClickHandler clickHandler;

    public RecordItemsRecycleViewAdapter(List<Records> list, Context context, RecordItemsOnClickHandler clickHandler) {
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

        Records record = list.get(position);
        viewHolder.title.setText(record.title);
        viewHolder.account.setText(record.account);
        viewHolder.amount.setText(String.valueOf(record.amount));

        viewHolder.itemView.setOnClickListener(v -> clickHandler.onClick(index));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}



