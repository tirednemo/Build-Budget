package com.example.buildbudget;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    FloatingActionButton mAddRecord, mAddOCR, mAddManual;
    TextView addText, OCRText, manualText;
    Boolean isAllFabsVisible;
    RecyclerView accountRecyclerView, recordRecyclerView;
    AccountItemsRecycleViewAdapter accountsRecyclerViewAdapter;
    RecordItemsRecycleViewAdapter recordsRecyclerViewAdapter;
    ClickListener listener;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseReference mDatabase;
    public User currentUser;
    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.d_grey));

        mDatabase = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        username = findViewById(R.id.username);

        if (getIntent().hasExtra("com.example.buildbudget.user")) {
            mDatabase.child("users").child(getIntent().getExtras().getString("com.example.buildbudget.user")).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        String name = String.valueOf(task.getResult().child("name").getValue());
                        String email = String.valueOf(task.getResult().child("email").getValue());
                        currentUser = new User(name, email);
                        username.setText(currentUser.name);
                    }
                }
            });
        }

//        drawerLayout = findViewById(R.id.my_drawer_layout);
//        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();


        mAddRecord = findViewById(R.id.add_record);
        mAddOCR = findViewById(R.id.ocr_add);
        mAddManual = findViewById(R.id.manual_add);
        addText = findViewById(R.id.add_record_text);
        OCRText = findViewById(R.id.ocr_add_text);
        manualText = findViewById(R.id.manual_add_text);
        isAllFabsVisible = false;

        mAddRecord.setOnClickListener(view -> {
            if (!isAllFabsVisible) {
                mAddOCR.show();
                mAddManual.show();
                addText.setVisibility(View.VISIBLE);
                OCRText.setVisibility(View.VISIBLE);
                manualText.setVisibility(View.VISIBLE);
                isAllFabsVisible = true;
            } else {
                mAddOCR.hide();
                mAddManual.hide();
                addText.setVisibility(View.GONE);
                OCRText.setVisibility(View.GONE);
                manualText.setVisibility(View.GONE);
                isAllFabsVisible = false;
            }
        });

        mAddOCR.setOnClickListener(view -> {
            //ocr activity
        });
        mAddManual.setOnClickListener(view -> {
            //manual activity
        });


        List<Records> recordList = new ArrayList<>();
        recordList = getRecords();
        List<Account> accountList = new ArrayList<>();
        accountList = getAccounts();

        recordRecyclerView = (RecyclerView) findViewById(R.id.date_view);
        accountRecyclerView = (RecyclerView) findViewById(R.id.accounts_view);
        listener = new ClickListener() {
//            @Override
//            public void click(int index) {
//                Toast.makeText(DashboardActivity.this, "clicked item index is " + index, Toast.LENGTH_LONG).show();
//            }
        };

        recordsRecyclerViewAdapter = new RecordItemsRecycleViewAdapter(recordList, getApplication(), listener);
        recordRecyclerView.setAdapter(recordsRecyclerViewAdapter);
        recordRecyclerView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this));

        accountsRecyclerViewAdapter = new AccountItemsRecycleViewAdapter(accountList, getApplication(), listener);
        accountRecyclerView.setAdapter(accountsRecyclerViewAdapter);
        accountRecyclerView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this, LinearLayoutManager.HORIZONTAL, false));
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    // Sample data for accounts
    private List<Account> getAccounts() {
        List<Account> list = new ArrayList<>();
        list.add(new Account("Debit Card", "card1", 160.0));
        list.add(new Account("Cash", "Cash", 40.0));
        return list;
    }
}

class RecordItemsRecycleViewAdapter extends RecyclerView.Adapter<RecordViewHolder> {

    List<Records> list = Collections.emptyList();
    Context context;
    ClickListener listener;

    public RecordItemsRecycleViewAdapter(List<Records> list, Context context, ClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecordViewHolder viewHolder = new RecordViewHolder(inflater.inflate(R.layout.record_items_layout, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder viewHolder, int position) {
        final int index = viewHolder.getAdapterPosition();
        viewHolder.title.setText(list.get(position).title);
        viewHolder.account.setText(list.get(position).account);
        viewHolder.amount.setText(list.get(position).amount + "");
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.click(index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

class ClickListener {
    public void click(int index) {
    }
}

class RecordViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView account;
    TextView amount;
    View view;

    RecordViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.textView2);
        account = (TextView) itemView.findViewById(R.id.textView3);
        amount = (TextView) itemView.findViewById(R.id.textView4);
        view = itemView;
    }
}


class AccountItemsRecycleViewAdapter extends RecyclerView.Adapter<AccountViewHolder> {
    List<Account> list = Collections.emptyList();
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
        AccountViewHolder viewHolder = new AccountViewHolder(inflater.inflate(R.layout.account_cards_layout, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder viewHolder, int position) {
        final int index = viewHolder.getAdapterPosition();
        viewHolder.name.setText(list.get(position).name);
        viewHolder.balance.setText(list.get(position).balance + "");
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.click(index);
            }
        });

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

        name = (TextView) itemView.findViewById(R.id.account_name);
        balance = (TextView) itemView.findViewById(R.id.account_balance);
        view = itemView;
    }
}