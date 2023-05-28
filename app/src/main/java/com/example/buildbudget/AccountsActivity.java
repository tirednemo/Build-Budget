package com.example.buildbudget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AccountsActivity extends AppCompatActivity {
    SearchView searchBar;
    RecyclerView accountRecyclerView;
    AccountItemsRecycleViewAdapter accountsRecyclerViewAdapter;
    AccountItemsRecycleViewAdapter.AccountItemsOnClickHandler accountItemsOnClickHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.d_violet));

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getAccounts();


        searchBar = findViewById(R.id.searchView2);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (accountsRecyclerViewAdapter != null) {
                    accountsRecyclerViewAdapter.getFilter().filter(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (accountsRecyclerViewAdapter != null) {
                    accountsRecyclerViewAdapter.getFilter().filter(s);
                }
                return true;
            }
        });

        TextView add_account = findViewById(R.id.add_account);
        add_account.setOnClickListener(view ->
        {
            Intent startt = new Intent(AccountsActivity.this, AccountCreationActivity.class);
            startActivity(startt);
        });
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private void getAccounts() {
        List<Account> list = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://build-budget-71a7f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        mDatabase.child("users").child(user.getUid()).child("accounts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
                            Account account = accountSnapshot.getValue(Account.class);
                            list.add(account);
                        }
                        accountRecyclerView = findViewById(R.id.accounts_view);
                        accountsRecyclerViewAdapter = new AccountItemsRecycleViewAdapter(list, AccountsActivity.this, accountItemsOnClickHandler);
                        accountRecyclerView.setAdapter(accountsRecyclerViewAdapter);
                        accountRecyclerView.setLayoutManager(new LinearLayoutManager(AccountsActivity.this, LinearLayoutManager.VERTICAL, false));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
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

class AccountViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView type;
    ImageView icon;

    AccountViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.add_account);
        type = itemView.findViewById(R.id.textView9);
        icon = itemView.findViewById(R.id.imageView3);
    }
}

class AccountItemsRecycleViewAdapter extends RecyclerView.Adapter<AccountViewHolder> implements Filterable {
    public interface AccountItemsOnClickHandler {
        void onClick(int index);
    }

    List<Account> list;
    List<Account> initialList;
    Context context;
    AccountItemsOnClickHandler clickHandler;

    public AccountItemsRecycleViewAdapter(List<Account> list, Context context, AccountItemsOnClickHandler clickHandler) {
        this.list = list;
        this.initialList = new ArrayList<>(list);
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        return new AccountViewHolder(inflater.inflate(R.layout.layout_account_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder viewHolder, int position) {
        final int index = viewHolder.getAdapterPosition();

        Account account = list.get(position);
        viewHolder.name.setText(account.Title);
        viewHolder.type.setText(account.Type);
        switch (account.Type) {
            case "Cash":
                viewHolder.icon.setImageResource(R.drawable.wallet);
                break;
            case "General":
                viewHolder.icon.setImageResource(R.drawable.mobile_wallet);
                break;
            case "Credit Card":
            case "Debit Card":
                viewHolder.icon.setImageResource(R.drawable.bank_cards);
                break;
            case "Savings":
            case "Current":
                viewHolder.icon.setImageResource(R.drawable.bank);
                break;
        }

        viewHolder.itemView.setOnClickListener(v -> {
            clickHandler.onClick(index);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return accountFilter;
    }

    private final Filter accountFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Account> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(initialList);
            } else {
                String query = constraint.toString().trim().toLowerCase(Locale.ROOT);
                for (Account account : initialList) {
                    if (account.Title.toLowerCase(Locale.ROOT).contains(query)) {
                        filteredList.add(account);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List<Account>) results.values);
            notifyDataSetChanged();
        }
    };

}