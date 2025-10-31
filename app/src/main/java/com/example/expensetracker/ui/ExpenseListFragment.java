package com.example.expensetracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensetracker.R;
import com.example.expensetracker.data.ExpenseData;
import com.example.expensetracker.model.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseListFragment extends Fragment {

    private RecyclerView rv;
    private TextView tvEmpty;
    private ExpenseAdapter adapter;
    private final List<Expense> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_expense_list, container, false);
        rv = root.findViewById(R.id.rv_expenses);
        tvEmpty = root.findViewById(R.id.tv_empty);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ExpenseAdapter(items, expense -> {
            // open detail fragment
            ExpenseDetailFragment detail = ExpenseDetailFragment.newInstance(expense.getId());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detail)
                    .addToBackStack(null)
                    .commit();
        });
        rv.setAdapter(adapter);

        // initial load
        loadData();

        return root;
    }

    private void loadData() {
        items.clear();
        List<Expense> all = ExpenseData.getExpenses();
        if (all != null && !all.isEmpty()) {
            items.addAll(all);
            tvEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
