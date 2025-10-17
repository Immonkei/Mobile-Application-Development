package com.example.expensetracker;



import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_EXPENSE_REQUEST = 100;

    private TextView tvStatus;
    private Button btnAddExpense, btnViewDetail;

    // Stored last expense
    private String amount, currency, category, remark, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnViewDetail = findViewById(R.id.btnViewDetail);

        // Initial UI
        tvStatus.setText(getString(R.string.last_expense_initial));
        btnViewDetail.setEnabled(false);

        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivityForResult(intent, ADD_EXPENSE_REQUEST); // per Lab 3 requirement
        });

        btnViewDetail.setOnClickListener(v -> {
            Intent detail = new Intent(MainActivity.this, ExpenseDetailActivity.class);
            detail.putExtra("amount", amount);
            detail.putExtra("currency", currency);
            detail.putExtra("category", category);
            detail.putExtra("remark", remark);
            detail.putExtra("date", date);
            startActivity(detail);
        });

        if (savedInstanceState != null) {
            amount   = savedInstanceState.getString("amount");
            currency = savedInstanceState.getString("currency");
            category = savedInstanceState.getString("category");
            remark   = savedInstanceState.getString("remark");
            date     = savedInstanceState.getString("date");
            updateStatus();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_EXPENSE_REQUEST && resultCode == RESULT_OK && data != null) {
            amount   = data.getStringExtra("amount");
            currency = data.getStringExtra("currency");
            category = data.getStringExtra("category");
            remark   = data.getStringExtra("remark");
            date     = data.getStringExtra("date");
            updateStatus();
        }
    }

    private void updateStatus() {
        if (amount != null && currency != null) {
            tvStatus.setText(getString(R.string.last_expense_format, amount, currency));
            btnViewDetail.setEnabled(true);
        } else {
            tvStatus.setText(getString(R.string.last_expense_initial));
            btnViewDetail.setEnabled(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("amount", amount);
        outState.putString("currency", currency);
        outState.putString("category", category);
        outState.putString("remark", remark);
        outState.putString("date", date);
    }
}
