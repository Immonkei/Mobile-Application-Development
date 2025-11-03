package com.example.expensetracker.detail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.R;
import com.example.expensetracker.data.ApiConfig;
import com.example.expensetracker.data.ExpenseApi;
import com.example.expensetracker.data.RetrofitClient;
import com.example.expensetracker.model.Expense;
import com.google.android.material.appbar.MaterialToolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailExpenseActivity extends AppCompatActivity {

    public static final String EXTRA_EXPENSE_ID = "expense_id";

    private TextView tvDate, tvCategory, tvAmountCurrency, tvRemark;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_expense);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_detail);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvDate = findViewById(R.id.tv_detail_date);
        tvCategory = findViewById(R.id.tv_detail_category);
        tvAmountCurrency = findViewById(R.id.tv_detail_amount_currency);
        tvRemark = findViewById(R.id.tv_detail_remark);

        Intent intent = getIntent();
        String expenseId = intent != null ? intent.getStringExtra(EXTRA_EXPENSE_ID) : null;
        if (expenseId == null) {
            Toast.makeText(this, R.string.expense_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadExpense(expenseId);
    }

    private void loadExpense(String id) {
        ExpenseApi api = RetrofitClient.getClient().create(ExpenseApi.class);
        api.getExpense(ApiConfig.DB_NAME, id).enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(Call<Expense> call, Response<Expense> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateUi(response.body());
                } else {
                    Toast.makeText(DetailExpenseActivity.this, "Failed to load expense", Toast.LENGTH_SHORT).show();
                    Log.e("DetailExpense", "Response code: " + response.code());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Expense> call, Throwable t) {
                Toast.makeText(DetailExpenseActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("DetailExpense", "Failure", t);
                finish();
            }
        });
    }

    private void populateUi(Expense e) {
        // use getters from the Retrofit-friendly model
        String createdDate = e.getCreatedDate() != null ? e.getCreatedDate() : "";
        tvDate.setText(getString(R.string.fmt_date, createdDate)); // fmt_date can be "%s"
        String amountCurrency = String.format("%s %.2f", e.getCurrency() != null ? e.getCurrency() : "", e.getAmount());
        tvAmountCurrency.setText(amountCurrency);
        tvCategory.setText(e.getCategory() != null ? e.getCategory() : "");
        tvRemark.setText(e.getRemark() != null ? e.getRemark() : "");
    }
}
