package com.example.expensetracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.expensetracker.R;
import com.example.expensetracker.data.ExpenseData;
import com.example.expensetracker.model.Expense;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Locale;
import java.util.Objects;

public class ExpenseDetailFragment extends Fragment {

    private static final String ARG_EXPENSE_ID = "expense_id";

    public static ExpenseDetailFragment newInstance(int expenseId) {
        ExpenseDetailFragment fragment = new ExpenseDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EXPENSE_ID, expenseId);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView tvAmountCurrency, tvDate, tvCategory, tvRemark;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_detail_expense, container, false);

        // toolbar back navigation
        MaterialToolbar toolbar = root.findViewById(R.id.toolbar_detail);
        if (toolbar != null) {
            if (toolbar.getNavigationIcon() == null) {
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            }
            toolbar.setNavigationOnClickListener(v -> navigateBack());
        }

        // views
        tvAmountCurrency = root.findViewById(R.id.tv_detail_amount_currency);
        tvDate = root.findViewById(R.id.tv_detail_date);
        tvCategory = root.findViewById(R.id.tv_detail_category);
        tvRemark = root.findViewById(R.id.tv_detail_remark);

        // handle hardware/system back
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navigateBack();
                    }
                }
        );

        // load expense
        int expenseId = getArguments() != null ? getArguments().getInt(ARG_EXPENSE_ID, -1) : -1;
        if (expenseId == -1) {
            Toast.makeText(requireContext(), R.string.expense_not_found, Toast.LENGTH_SHORT).show();
            navigateBack();
            return root;
        }

        Expense expense = null;
        try {
            expense = ExpenseData.findById(expenseId);
        } catch (Exception ignored) {}

        if (expense == null) {
            try {
                for (Expense e : ExpenseData.getExpenses()) {
                    if (e.getId() == expenseId) {
                        expense = e;
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }

        if (expense == null) {
            Toast.makeText(requireContext(), R.string.expense_not_found, Toast.LENGTH_SHORT).show();
            navigateBack();
        } else {
            populateUi(expense);
        }

        return root;
    }

    private void populateUi(@NonNull Expense expense) {
        String amountText = String.format(Locale.getDefault(), "%s %.2f",
                Objects.toString(expense.getCurrency(), ""), expense.getAmount());
        tvAmountCurrency.setText(amountText);
        tvDate.setText(Objects.toString(expense.getDate(), ""));
        tvCategory.setText(Objects.toString(expense.getCategory(), ""));
        tvRemark.setText(Objects.toString(expense.getRemark(), ""));
    }

    private void navigateBack() {
        // Prefer popping the fragment back stack if possible
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
            return;
        }

        // Otherwise replace with the list fragment (fallback)
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ExpenseListFragment())
                .commit();
    }
}
