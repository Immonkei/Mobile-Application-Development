package com.example.expensetracker.detail;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.data.ExpenseData;
import com.example.expensetracker.model.Expense;

public class DetailExpenseActivity extends AppCompatActivity {

    public static final String EXTRA_EXPENSE_ID = "expense_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Keep theme support from Lab 3
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_expense);

        int id = getIntent().getIntExtra(EXTRA_EXPENSE_ID, -1);
        Expense e = ExpenseData.findById(id);
        if (e == null) {
            Toast.makeText(this, R.string.expense_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvTitle = findViewById(R.id.tv_detail_title);
        TextView tvAmountCurrency = findViewById(R.id.tv_detail_amount_currency);
        TextView tvDate = findViewById(R.id.tv_detail_date);
        TextView tvCategory = findViewById(R.id.tv_detail_category);
        TextView tvRemark = findViewById(R.id.tv_detail_remark);

        tvTitle.setText(getString(R.string.expense_detail_title));
        tvAmountCurrency.setText(getString(R.string.fmt_amount_currency, e.getCurrency(), e.getAmount()));
        // THIS IS THE CORRECTED LINE
        tvDate.setText(getString(R.string.fmt_date, e.getDate()));

        tvCategory.setText(getString(R.string.fmt_category, e.getCategory()));
        tvRemark.setText(getString(R.string.fmt_remark, e.getRemark() == null ? "" : e.getRemark()));
    }
}
