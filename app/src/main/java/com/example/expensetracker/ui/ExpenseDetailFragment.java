package com.example.expensetracker.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.expensetracker.R;
import com.example.expensetracker.data.ApiConfig;
import com.example.expensetracker.data.ExpenseApi;
import com.example.expensetracker.data.RetrofitClient;
import com.example.expensetracker.model.Expense;
import com.google.android.material.appbar.MaterialToolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseDetailFragment extends Fragment {

    private static final String ARG_EXPENSE_ID = "expense_id";
    private static final String TAG = "DETAIL_FRAGMENT";

    public static ExpenseDetailFragment newInstance(String expenseId) {
        ExpenseDetailFragment f = new ExpenseDetailFragment();
        Bundle b = new Bundle();
        b.putString(ARG_EXPENSE_ID, expenseId);
        f.setArguments(b);
        return f;
    }

    private TextView tvAmountCurrency, tvDate, tvCategory, tvRemark;
    private ImageView imgReceipt;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_detail_expense, container, false);

        // ✅ INIT ALL VIEWS (THIS WAS MISSING)
        tvAmountCurrency = root.findViewById(R.id.tv_detail_amount_currency);
        tvDate = root.findViewById(R.id.tv_detail_date);
        tvCategory = root.findViewById(R.id.tv_detail_category);
        tvRemark = root.findViewById(R.id.tv_detail_remark);
        imgReceipt = root.findViewById(R.id.img_detail_receipt);

        MaterialToolbar toolbar = root.findViewById(R.id.toolbar_detail);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(v ->
                    getParentFragmentManager().popBackStack()
            );
        }

        String expenseId = getArguments() != null
                ? getArguments().getString(ARG_EXPENSE_ID)
                : null;

        if (expenseId == null) {
            Toast.makeText(requireContext(), "Expense not found", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
            return root;
        }

        loadExpense(expenseId);
        return root;
    }

    private void loadExpense(String id) {
        Log.d(TAG, "loadExpense id=" + id);

        ExpenseApi api = RetrofitClient.getClient().create(ExpenseApi.class);
        api.getExpense(ApiConfig.DB_NAME, id).enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(
                    @NonNull Call<Expense> call,
                    @NonNull Response<Expense> response) {

                if (response.isSuccessful() && response.body() != null) {
                    populateUi(response.body());
                } else {
                    Toast.makeText(requireContext(), "Failed to load expense", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<Expense> call,
                    @NonNull Throwable t) {

                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void populateUi(@NonNull Expense expense) {
        tvAmountCurrency.setText(
                String.format("%s %.2f",
                        expense.getCurrency() != null ? expense.getCurrency() : "",
                        expense.getAmount())
        );

        tvDate.setText(expense.getCreatedDate());
        tvCategory.setText(expense.getCategory());
        tvRemark.setText(expense.getRemark());

        // ✅ IMAGE SAFE LOAD
        if (expense.getReceiptImageUrl() != null
                && !expense.getReceiptImageUrl().isEmpty()) {

            imgReceipt.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(expense.getReceiptImageUrl())
                    .into(imgReceipt);
        } else {
            imgReceipt.setVisibility(View.GONE);
        }
    }
}
