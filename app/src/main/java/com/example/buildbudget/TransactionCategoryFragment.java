package com.example.buildbudget;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionCategoryFragment extends Fragment {
    SearchView searchBar;
    RecyclerView categoryRecyclerView;
    CategoryItemsRecycleViewAdapter categoriesRecyclerViewAdapter;
    CategoryItemsRecycleViewAdapter.CategoryItemsOnClickHandler categoryItemsOnClickHandler;

    public TransactionCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction_category, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        List<Pair<String, Integer>> categoryList;
        categoryList = getCategories();

        categoryRecyclerView = v.findViewById(R.id.categories_view);
        categoriesRecyclerViewAdapter = new CategoryItemsRecycleViewAdapter(categoryList, getActivity(), categoryItemsOnClickHandler);
        categoryRecyclerView.setAdapter(categoriesRecyclerViewAdapter);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        searchBar = v.findViewById(R.id.searchView);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (categoriesRecyclerViewAdapter != null) {
                    categoriesRecyclerViewAdapter.getFilter().filter(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (categoriesRecyclerViewAdapter != null) {
                    categoriesRecyclerViewAdapter.getFilter().filter(s);
                }
                return true;
            }
        });

        return v;
    }

    private List<Pair<String, Integer>> getCategories() {
        List<Pair<String, Integer>> list = new ArrayList<>();
        list.add(new Pair<>("Food & Drinks", R.drawable.dining));
        list.add(new Pair<>("Shopping", R.drawable.shopping_bag));
        list.add(new Pair<>("Housing", R.drawable.home));
        list.add(new Pair<>("Vehicle", R.drawable.car));
        list.add(new Pair<>("Transport", R.drawable.bus));
        list.add(new Pair<>("Healthcare", R.drawable.doctors_bag));
        list.add(new Pair<>("Education & Development", R.drawable.teaching));
        list.add(new Pair<>("Communication", R.drawable.ringing_phone));
        list.add(new Pair<>("Life & Entertainment", R.drawable.movie_projector));
        list.add(new Pair<>("Investments", R.drawable.profit));
        list.add(new Pair<>("Financial Expenses", R.drawable.tax));
        list.add(new Pair<>("Income", R.drawable.cash));
        list.add(new Pair<>("Others", R.drawable.information));
        return list;
    }
}

class CategoryViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    ImageView icon;

    CategoryViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.add_account);
        icon = itemView.findViewById(R.id.imageView);
    }
}

class CategoryItemsRecycleViewAdapter extends RecyclerView.Adapter<CategoryViewHolder> implements Filterable {
    public interface CategoryItemsOnClickHandler {
        void onClick(int index);
    }

    List<Pair<String, Integer>> list;
    List<Pair<String, Integer>> initialList;
    Context context;
    CategoryItemsOnClickHandler clickHandler;

    public CategoryItemsRecycleViewAdapter(List<Pair<String, Integer>> list, Context context, CategoryItemsOnClickHandler clickHandler) {
        this.list = list;
        this.initialList = new ArrayList<>(list);
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        return new CategoryViewHolder(inflater.inflate(R.layout.layout_category_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder viewHolder, int position) {
        final int index = viewHolder.getAdapterPosition();

        Pair<String, Integer> category = list.get(position);
        viewHolder.name.setText(category.first);
        viewHolder.icon.setImageResource(category.second);
        viewHolder.itemView.setOnClickListener(v -> clickHandler.onClick(index));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return categoryFilter;
    }

    private final Filter categoryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Pair<String, Integer>> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(initialList);
            } else {
                String query = constraint.toString().trim().toLowerCase(Locale.ROOT);
                for (Pair<String, Integer> category : initialList) {
                    if (category.first.toLowerCase(Locale.ROOT).contains(query)) {
                        filteredList.add(category);
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
            list.addAll((List<Pair<String, Integer>>) results.values);
            notifyDataSetChanged();
        }
    };

}