package com.example.buildbudget;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
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
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout mDrawer;
    ImageView mNotification;
    String account_name;
    FloatingActionButton mAddRecord, mAddOCR, mAddManual;
    TextView addText, OCRText, manualText;
    Boolean isAllFABsVisible;
    RecyclerView background;
    RecyclerView accountCardRecyclerView, recordRecyclerView;
    AccountCardItemsRecycleViewAdapter accountCardsRecyclerViewAdapter;
    AccountCardItemsRecycleViewAdapter.AccountCardItemsOnClickHandler accountCardItemsOnClickHandler;
    RecordItemsRecycleViewAdapter recordsRecyclerViewAdapter;
    RecordItemsRecycleViewAdapter.RecordItemsOnClickHandler recordItemsOnClickHandler;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    FirebaseUser currentUser;
    HashMap<String, Integer> categoryMap = new HashMap<>();
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.d_grey));

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (getIntent().hasExtra("Account")) {
            account_name = getIntent().getExtras().getString("Account");
        } else
            account_name = "All";

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

        background = findViewById(R.id.account_cards_view);
        background.setOnClickListener(view -> {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        });
        getCategories();
        getAccountCards();
        getRecords(account_name);

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
                accountCardsRecyclerViewAdapter = new AccountCardItemsRecycleViewAdapter(list, DashboardActivity.this, accountCardItemsOnClickHandler);
                accountCardRecyclerView.setAdapter(accountCardsRecyclerViewAdapter);
                accountCardRecyclerView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this, LinearLayoutManager.HORIZONTAL, false));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        mDatabase.child("users").child(user.getUid()).child("accounts").addValueEventListener(accountListener);
    }

    private void getRecords(String target) {

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
                            record.Icon = categoryMap.get(record.Category);
                            if (Objects.equals(target, "All")) {
                                list.add(record);
                            } else {
                                if (Objects.equals(record.Account, target)) {
                                    list.add(record);
                                }
                            }
                        }
                    }
                }

                list.sort((t1, t2) -> {
                    if (t1.Date == null || t2.Date == null)
                        return 0;
                    return t2.Date.compareTo(t1.Date);
                });

                Map<String, List<Transaction>> transactionsByDate = new LinkedHashMap<>();
                for (Transaction transaction : list) {
                    String date = transaction.Date.split(" ")[0]; // Extract the date from the full date-time string
                    if (transactionsByDate.containsKey(date)) {
                        transactionsByDate.get(date).add(transaction);
                    } else {
                        List<Transaction> transactions = new ArrayList<>();
                        transactions.add(transaction);
                        transactionsByDate.put(date, transactions);
                    }
                }

                int i = 0;
                List<SectionedRecordsRecyclerViewAdapter.Section> sections =
                        new ArrayList<SectionedRecordsRecyclerViewAdapter.Section>();
                for (Map.Entry<String, List<Transaction>> entry : transactionsByDate.entrySet()) {
                    String date = entry.getKey();
                    List<Transaction> transactions = entry.getValue();
                    int transactionCount = transactions.size();
                    sections.add(new SectionedRecordsRecyclerViewAdapter.Section(i, date));
                    i += transactionCount;
                }

                recordRecyclerView = findViewById(R.id.date_view);
                recordRecyclerView.setHasFixedSize(true);
                recordRecyclerView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this, LinearLayoutManager.VERTICAL, false));

                recordsRecyclerViewAdapter = new RecordItemsRecycleViewAdapter(list, DashboardActivity.this, recordItemsOnClickHandler);

                SectionedRecordsRecyclerViewAdapter.Section[] dummy = new SectionedRecordsRecyclerViewAdapter.Section[sections.size()];
                SectionedRecordsRecyclerViewAdapter mSectionedAdapter = new
                        SectionedRecordsRecyclerViewAdapter(getApplication(), recordsRecyclerViewAdapter);
                mSectionedAdapter.setSections(sections.toArray(dummy));

                recordRecyclerView.setAdapter(mSectionedAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDatabase.child("users").child(user.getUid()).child("accounts").addValueEventListener(recordListener);
    }

    void getCategories() {
        String[] names = getResources().getStringArray(R.array.category_names);
        TypedArray icons = getResources().obtainTypedArray(R.array.category_icons);

        for (int i = 0; i < names.length; i++) {
            categoryMap.put(names[i], icons.getResourceId(i, 0));
        }
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
            case R.id.nav_stats:
                start = new Intent(this, StatisticsActivity.class);
                startActivity(start);
                break;


            case R.id.nav_budget:
                start = new Intent(this, BudgetActivity.class);
                startActivity(start);
                break;
            case R.id.nav_debts:
                start = new Intent(this, DebtActivity.class);
                startActivity(start);
                break;
            case R.id.nav_ppay:
                start = new Intent(this, AddPlannedPaymentActivity.class);
                startActivity(start);
                break;
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

            case R.id.nav_settings:
                start = new Intent(this, SettingsActivity.class);
                startActivity(start);
                break;

            case R.id.nav_help:
                start = new Intent(this, HelpActivity.class);
                startActivity(start);
                break;

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
            Intent inte = new Intent(context, DashboardActivity.class);
            inte.putExtra("Account", account.Title);
            context.startActivity(inte);
        });

        viewHolder.front.setOnLongClickListener(v -> {
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
            return true;
        });

        viewHolder.back.setOnLongClickListener(v -> {
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
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}


class RecordHeaderViewHolder extends RecyclerView.ViewHolder {
    TextView date;

    RecordHeaderViewHolder(View itemView) {
        super(itemView);

        date = itemView.findViewById(R.id.record_date);
    }
}

class RecordItemViewHolder extends RecyclerView.ViewHolder {
    TextView title, account, amount;
    ImageView icon;

    RecordItemViewHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.add_account);
        account = itemView.findViewById(R.id.textView3);
        amount = itemView.findViewById(R.id.textView4);
        icon = itemView.findViewById(R.id.recordIcon);
    }
}

class RecordItemsRecycleViewAdapter extends RecyclerView.Adapter<RecordItemViewHolder> {
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
    public RecordItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        return new RecordItemViewHolder(inflater.inflate(R.layout.layout_record_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordItemViewHolder viewHolder, int position) {
        final int index = viewHolder.getAdapterPosition();

        Transaction record = list.get(position);
        viewHolder.title.setText(record.Note);
        viewHolder.account.setText(record.Account);
        viewHolder.amount.setText(String.valueOf(record.Amount));
        viewHolder.icon.setImageResource(record.Icon);

        viewHolder.itemView.setOnClickListener(v -> clickHandler.onClick(index));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class SectionedRecordsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private static final int SECTION_TYPE = 0;

    private boolean mValid = true;
    private int mSectionResourceId;
    private int mTextResourceId;
    private LayoutInflater mLayoutInflater;
    private RecyclerView.Adapter mBaseAdapter;
    private SparseArray<Section> mSections = new SparseArray<Section>();


    public SectionedRecordsRecyclerViewAdapter(Context context, RecordItemsRecycleViewAdapter baseAdapter) {

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSectionResourceId = R.layout.layout_record_date;
        mTextResourceId = R.id.record_date;
        mBaseAdapter = baseAdapter;
        mContext = context;

        mBaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int typeView) {
        if (typeView == SECTION_TYPE) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            return new RecordHeaderViewHolder(inflater.inflate(R.layout.layout_record_date, parent, false));
        } else {
            return mBaseAdapter.onCreateViewHolder(parent, typeView - 1);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder sectionViewHolder, int position) {
        if (isSectionHeaderPosition(position)) {
            ((RecordHeaderViewHolder) sectionViewHolder).date.setText(mSections.get(position).title);
        } else {
            mBaseAdapter.onBindViewHolder(sectionViewHolder, sectionedPositionToPosition(position));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? SECTION_TYPE
                : mBaseAdapter.getItemViewType(sectionedPositionToPosition(position)) + 1;
    }


    public static class Section {
        int firstPosition;
        int sectionedPosition;
        CharSequence title;

        public Section(int firstPosition, CharSequence title) {
            this.firstPosition = firstPosition;
            this.title = title;
        }

        public CharSequence getTitle() {
            return title;
        }
    }


    public void setSections(Section[] sections) {
        mSections.clear();

        Arrays.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section o, Section o1) {
                return Integer.compare(o.firstPosition, o1.firstPosition);
            }
        });

        int offset = 0; // offset positions for the headers we're adding
        for (Section section : sections) {
            section.sectionedPosition = section.firstPosition + offset;
            mSections.append(section.sectionedPosition, section);
            ++offset;
        }

        notifyDataSetChanged();
    }

    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).firstPosition > position) {
                break;
            }
            ++offset;
        }
        return position + offset;
    }

    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).sectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public boolean isSectionHeaderPosition(int position) {
        return mSections.get(position) != null;
    }


    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - mSections.indexOfKey(position)
                : mBaseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    @Override
    public int getItemCount() {
        return (mValid ? mBaseAdapter.getItemCount() + mSections.size() : 0);
    }

}