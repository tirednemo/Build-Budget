package com.example.buildbudget;

import android.content.Context;
import android.content.res.TypedArray;
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
        categoryItemsOnClickHandler = index -> {
            getActivity().getSupportFragmentManager().popBackStack();
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction_category, container, false);
        TransactionActivity parent = (TransactionActivity) getActivity();
        parent.heading.setText("Categories");

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
        String[] names = getResources().getStringArray(R.array.category_names);
        TypedArray icons = getResources().obtainTypedArray(R.array.category_icons);

        for (int i = 0; i < names.length; i++) {
            list.add(new Pair<String, Integer>(names[i], icons.getResourceId(i, 0)));
        }
        return list;
    }
}

class CategoryViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    ImageView icon;

    CategoryViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.add_account);
        icon = itemView.findViewById(R.id.recordIcon);
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
    TransactionActivity parent;

    public CategoryItemsRecycleViewAdapter(List<Pair<String, Integer>> list, Context context, CategoryItemsOnClickHandler clickHandler) {
        this.list = list;
        this.initialList = new ArrayList<>(list);
        this.context = context;
        this.clickHandler = clickHandler;
        this.parent = (TransactionActivity) context;
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

        viewHolder.itemView.setOnClickListener(v ->
                {
                    clickHandler.onClick(index);
                    Pair<String, Integer> selected = list.get(index);
                    parent.categoryName = selected.first;
                    parent.categoryIcon.setImageResource(selected.second);
                }
        );
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