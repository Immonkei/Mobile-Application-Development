package com.example.expensetracker;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ExpenseDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        TextView tvAmount = findViewById(R.id.tvAmount);
        TextView tvCurrency = findViewById(R.id.tvCurrency);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvRemark = findViewById(R.id.tvRemark);
        TextView tvDate = findViewById(R.id.tvDate);
        Button btnAddNew = findViewById(R.id.btnAddNew);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        Intent intent = getIntent();
        tvAmount.setText(getString(R.string.detail_amount_label, intent.getStringExtra("amount")));
        tvCurrency.setText(getString(R.string.detail_currency_label, intent.getStringExtra("currency")));
        tvCategory.setText(getString(R.string.detail_category_label, intent.getStringExtra("category")));
        tvRemark.setText(getString(R.string.detail_remark_label, intent.getStringExtra("remark")));
        tvDate.setText(getString(R.string.detail_date_label, intent.getStringExtra("date")));

        btnAddNew.setOnClickListener(v -> {
            startActivity(new Intent(this, AddExpenseActivity.class));
            finish();
        });

        btnBackHome.setOnClickListener(v -> finish());
    }
}