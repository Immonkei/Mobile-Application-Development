package com.example.expensetracker;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText etAmount, etRemark, etDate;
    private Spinner spCurrency, spCategory;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        etAmount = findViewById(R.id.etAmount);
        etRemark = findViewById(R.id.etRemark);
        etDate = findViewById(R.id.etDate);
        spCurrency = findViewById(R.id.spCurrency);
        spCategory = findViewById(R.id.spCategory);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Date picker via EditText click
        etDate.setKeyListener(null); // disable manual typing
        etDate.setOnClickListener(v -> showDatePicker());

        btnSubmit.setOnClickListener(v -> submitExpense());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, y, m, d) -> etDate.setText(String.format("%02d/%02d/%04d", d, m + 1, y)),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void submitExpense() {
        String amount = etAmount.getText().toString().trim();
        String currency = spCurrency.getSelectedItem().toString();
        String category = spCategory.getSelectedItem().toString();
        String remark = etRemark.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if (TextUtils.isEmpty(amount)) {
            etAmount.setError(getString(R.string.error_amount_required));
            return;
        }
        if (TextUtils.isEmpty(date)) {
            etDate.setError(getString(R.string.error_date_required));
            return;
        }

        Intent result = new Intent();
        result.putExtra("amount", amount);
        result.putExtra("currency", currency);
        result.putExtra("category", category);
        result.putExtra("remark", remark);
        result.putExtra("date", date);
        setResult(RESULT_OK, result);
        finish();
    }
}
