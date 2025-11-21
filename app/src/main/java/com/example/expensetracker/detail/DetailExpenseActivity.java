package com.example.expensetracker.detail;

import android.content.SharedPreferences;
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
    private static final String TAG = "DETAILS";

    private static final String PREFS_NAME = "prefs_expense";
    private static final String KEY_LAST_CLOSED_ID = "last_closed_id";
    private static final String KEY_LAST_CLOSED_AT = "last_closed_at";
    private static final String KEY_CURRENTLY_VIEWING = "currently_viewing_id";

    private TextView tvDate, tvCategory, tvAmountCurrency, tvRemark;
    private String expenseId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_expense);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.expense_detail_title);
        }

        // click back -> uniform handling
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvDate = findViewById(R.id.tv_detail_date);
        tvCategory = findViewById(R.id.tv_detail_category);
        tvAmountCurrency = findViewById(R.id.tv_detail_amount_currency);
        tvRemark = findViewById(R.id.tv_detail_remark);

        expenseId = getIntent() != null ? getIntent().getStringExtra(EXTRA_EXPENSE_ID) : null;
        if (expenseId == null) {
            Toast.makeText(this, R.string.expense_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // mark as currently viewing so list can hide it
        try {
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .putString(KEY_CURRENTLY_VIEWING, expenseId)
                    .apply();
            Log.d(TAG, "Marked currently viewing: " + expenseId);
        } catch (Exception ignored) {}

        loadExpense(expenseId);
    }

    @Override
    public void onBackPressed() {
        // record last closed id (so quick reopen is prevented)
        recordLastClosedId();
        // clear currently viewing so the list can show it again
        clearCurrentlyViewingId();
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ensure currently viewing cleared in all cases
        clearCurrentlyViewingId();
    }

    private void recordLastClosedId() {
        try {
            if (expenseId == null) return;
            long now = System.currentTimeMillis();
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .putString(KEY_LAST_CLOSED_ID, expenseId)
                    .putLong(KEY_LAST_CLOSED_AT, now)
                    .apply();
            Log.d(TAG, "Recorded lastClosedId=" + expenseId + " at=" + now);
        } catch (Exception ex) {
            Log.w(TAG, "Failed to record last closed id", ex);
        }
    }

    private void clearCurrentlyViewingId() {
        try {
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .remove(KEY_CURRENTLY_VIEWING)
                    .apply();
            Log.d(TAG, "Cleared currently viewing id");
        } catch (Exception ignored) {}
    }

    private void loadExpense(String id) {
        ExpenseApi api = RetrofitClient.getClient().create(ExpenseApi.class);
        api.getExpense(ApiConfig.DB_NAME, id).enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(Call<Expense> call, Response<Expense> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateUi(response.body());
                } else {
                    Log.e(TAG, "Failed to load expense. code=" + response.code());
                    Toast.makeText(DetailExpenseActivity.this, R.string.failed_to_load_expense, Toast.LENGTH_SHORT).show();
                    // record guard and clear currently viewing before finishing
                    recordLastClosedId();
                    clearCurrentlyViewingId();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Expense> call, Throwable t) {
                Log.e(TAG, "Network failure", t);
                Toast.makeText(DetailExpenseActivity.this, getString(R.string.network_error_colon) + " " + t.getMessage(), Toast.LENGTH_LONG).show();
                recordLastClosedId();
                clearCurrentlyViewingId();
                finish();
            }
        });
    }

    private void populateUi(Expense e) {
        tvDate.setText(e.getCreatedDate() != null ? e.getCreatedDate() : "");
        tvAmountCurrency.setText(String.format("%s %.2f",
                e.getCurrency() != null ? e.getCurrency() : "", e.getAmount()));
        tvCategory.setText(e.getCategory() != null ? e.getCategory() : "");
        tvRemark.setText(e.getRemark() != null ? e.getRemark() : "");
    }
}
