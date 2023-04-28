package com.example.buildbudget;

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

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;

    FloatingActionButton mAddRecord, mAddOCR, mAddManual;
    ImageView mNotification;
    TextView addText, OCRText, manualText;
    Boolean isAllFABsVisible;

    RecyclerView accountRecyclerView, recordRecyclerView;
    AccountItemsRecycleViewAdapter accountsRecyclerViewAdapter;
    RecordItemsRecycleViewAdapter recordsRecyclerViewAdapter;

    ClickListener listener;
    DatabaseReference mDatabase;
    User currentUser;
    TextView username;
    String uid, name, email, photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.d_grey));

        NavigationView navigationView = findViewById(R.id.nav_view);
        View navHead = navigationView.getHeaderView(0);
        ImageView me = navHead.findViewById(R.id.me);
        mNotification = findViewById(R.id.notification);

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
                            currentUser = new User(name, email);
                            if (photo.length() > 0)
                                currentUser.setPhoto(photo);
                            Glide.with(getApplicationContext())
                                    .load(photo)
                                    .into(me);
                        }
                    });
        }

// TODO: Navigation Drawer for sidebar
        me.setOnClickListener(view -> {
            Intent start = new Intent(this, ProfileActivity.class);
            startActivity(start);
        });
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open_nav, R.string.close_nav);
        drawer.addDrawerListener(toggle);
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

        mNotification.setOnClickListener(view -> {
            Intent start = new Intent(this, NotificationsActivity.class);
            startActivity(start);
        });

// TODO: Recycler View for accounts and date-ordered records

        List<Account> accountList;
        accountList = getAccounts();
        List<Records> recordList;
        recordList = getRecords();

        accountRecyclerView = findViewById(R.id.accounts_view);
        accountsRecyclerViewAdapter = new AccountItemsRecycleViewAdapter(accountList, getApplication(), listener);
        accountRecyclerView.setAdapter(accountsRecyclerViewAdapter);
        accountRecyclerView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this, LinearLayoutManager.HORIZONTAL, false));

        recordRecyclerView = findViewById(R.id.date_view);
        recordsRecyclerViewAdapter = new RecordItemsRecycleViewAdapter(recordList, getApplication(), listener);
        recordRecyclerView.setAdapter(recordsRecyclerViewAdapter);
        recordRecyclerView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this, LinearLayoutManager.VERTICAL, false));
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.nav_home:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//                break;
            case R.id.nav_acc:
                Intent startt = new Intent(this, AccountsActivity.class);
                startt.putExtra("com.example.buildbudget.uid", uid);
                startActivity(startt);
                break;
//            case R.id.nav_banksync:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//                break;
//            case R.id.nav_stats:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
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
//                break;
//            case R.id.nav_stock_ex:
//                break;
//
//            case R.id.nav_loyal:
//                break;
//            case R.id.nav_warranty:
//                break;

            case R.id.nav_settings:
                Intent start = new Intent(this, SettingsActivity.class);
                start.putExtra("com.example.buildbudget.uid", uid);
                startActivity(start);
                break;
//            case R.id.nav_help:
//                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent starttt = new Intent(this, AuthenticationActivity.class);
                startActivity(starttt);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}


class ClickListener {
    public void click(int index) {
    }
}


class AccountItemsRecycleViewAdapter extends RecyclerView.Adapter<AccountViewHolder> {
    List<Account> list;
    Context context;
    ClickListener listener;

    public AccountItemsRecycleViewAdapter(List<Account> list, Context context, ClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        return new AccountViewHolder(inflater.inflate(R.layout.layout_account_cards, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder viewHolder, int position) {
        final int index = viewHolder.getAdapterPosition();
        viewHolder.name.setText(list.get(position).name);
        viewHolder.balance.setText(String.valueOf(list.get(position).balance));
        viewHolder.view.setOnClickListener(view -> listener.click(index));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}


class AccountViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView balance;
    View view;

    AccountViewHolder(View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.account_name);
        balance = itemView.findViewById(R.id.account_balance);
        view = itemView;
    }
}


class RecordItemsRecycleViewAdapter extends RecyclerView.Adapter<RecordViewHolder> {

    List<Records> list;
    Context context;
    ClickListener listener;

    public RecordItemsRecycleViewAdapter(List<Records> list, Context context, ClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
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
        viewHolder.title.setText(list.get(position).title);
        viewHolder.account.setText(list.get(position).account);
        viewHolder.amount.setText(String.valueOf(list.get(position).amount));
        viewHolder.view.setOnClickListener(view -> listener.click(index));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}


class RecordViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView account;
    TextView amount;
    View view;

    RecordViewHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.textView2);
        account = itemView.findViewById(R.id.textView3);
        amount = itemView.findViewById(R.id.textView4);
        view = itemView;
    }
}

