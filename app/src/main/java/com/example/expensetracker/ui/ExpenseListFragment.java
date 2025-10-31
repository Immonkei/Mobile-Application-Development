package com.example.expensetracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.data.ExpenseData;
import com.example.expensetracker.model.Expense;

import java.util.List;

public class ExpenseListFragment extends Fragment {

    private RecyclerView rv;
    private LinearLayout emptyContainer;
    private ExpenseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_expense_list, container, false);

        rv = root.findViewById(R.id.rv_expenses);
        emptyContainer = root.findViewById(R.id.empty_container);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setHasFixedSize(true);

        // If RecyclerView is inside any scrolling parent, disabling nested scrolling often helps.
        // Keep false to avoid parent stealing scroll touches.
        rv.setNestedScrollingEnabled(false);

        // Use the live data list from ExpenseData
        List<Expense> data = ExpenseData.getExpenses();

        adapter = new ExpenseAdapter(data, expense -> {
            // open detail fragment
            ExpenseDetailFragment detail = ExpenseDetailFragment.newInstance(expense.getId());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detail)
                    .addToBackStack(null)
                    .commit();
        });

        // Let adapter use stable IDs for smoother updates (getItemId implemented).
        adapter.setHasStableIds(true);

        rv.setAdapter(adapter);

        updateEmptyState();

        return root;
    }

    private void updateEmptyState() {
        boolean isEmpty = ExpenseData.getExpenses() == null || ExpenseData.getExpenses().isEmpty();
        emptyContainer.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rv.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // refresh content if expenses changed
        if (adapter != null) adapter.notifyDataSetChanged();
        updateEmptyState();
    }
}
