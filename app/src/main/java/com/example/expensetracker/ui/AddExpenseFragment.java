package com.example.expensetracker.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.expensetracker.R;
import com.example.expensetracker.data.ApiConfig;
import com.example.expensetracker.data.ExpenseApi;
import com.example.expensetracker.data.RetrofitClient;
import com.example.expensetracker.model.Expense;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.time.Instant;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddExpenseFragment extends Fragment {

    private EditText etAmount, etDate, etRemark;
    private AutoCompleteTextView actvCurrency, actvCategory;
    private MaterialButton btnSave;

    public AddExpenseFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_expense, container, false);

        MaterialToolbar toolbar = root.findViewById(R.id.toolbar_add);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        }

        etAmount     = root.findViewById(R.id.et_amount);
        etDate       = root.findViewById(R.id.et_date);
        etRemark     = root.findViewById(R.id.et_remark);
        actvCurrency = root.findViewById(R.id.actv_currency);
        actvCategory = root.findViewById(R.id.actv_category);
        btnSave      = root.findViewById(R.id.btn_save);

        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.list_item_dropdown,
                getResources().getStringArray(R.array.currency_array)
        );
        actvCurrency.setAdapter(currencyAdapter);
        actvCurrency.setText("", false);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.list_item_dropdown,
                getResources().getStringArray(R.array.category_array)
        );
        actvCategory.setAdapter(categoryAdapter);
        actvCategory.setText("", false);

        // Date picker
        TextInputLayout tilDate = root.findViewById(R.id.til_date);
        View.OnClickListener showDatePicker = v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(),
                    (view, year, month, day) -> {
                        String mm = String.format(Locale.getDefault(), "%02d", month + 1);
                        String dd = String.format(Locale.getDefault(), "%02d", day);
                        String formatted = String.format(Locale.getDefault(), "%04d-%s-%s", year, mm, dd);
                        etDate.setText(formatted);
                        validate();
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        };

        if (tilDate != null) tilDate.setEndIconOnClickListener(showDatePicker);
        if (tilDate != null) tilDate.setOnClickListener(showDatePicker);
        etDate.setOnClickListener(showDatePicker);

        View.OnFocusChangeListener focusWatcher = (v, hasFocus) -> validate();
        etAmount.setOnFocusChangeListener(focusWatcher);
        etDate.setOnFocusChangeListener(focusWatcher);
        actvCurrency.setOnFocusChangeListener(focusWatcher);
        actvCategory.setOnFocusChangeListener(focusWatcher);

        btnSave.setOnClickListener(v -> saveExpense());

        validate();
        return root;
    }

    private boolean validate() {
        boolean okAmount   = etAmount.getText() != null && etAmount.getText().toString().trim().length() > 0;
        boolean okDate     = etDate.getText() != null && etDate.getText().toString().trim().length() > 0;
        boolean okCurrency = actvCurrency.getText() != null && actvCurrency.getText().toString().trim().length() > 0;
        boolean okCategory = actvCategory.getText() != null && actvCategory.getText().toString().trim().length() > 0;

        boolean enabled = okAmount && okDate && okCurrency && okCategory;
        btnSave.setEnabled(enabled);
        btnSave.setAlpha(enabled ? 1f : 0.5f);
        return enabled;
    }

    private void saveExpense() {
        String amountStr = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";
        String currency = actvCurrency.getText() != null ? actvCurrency.getText().toString().trim() : "";
        String date = etDate.getText() != null ? etDate.getText().toString().trim() : "";
        String category = actvCategory.getText() != null ? actvCategory.getText().toString().trim() : "";
        String remark = etRemark.getText() != null ? etRemark.getText().toString().trim() : "";

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException ex) {
            etAmount.setError(getString(R.string.error_invalid_number));
            etAmount.requestFocus();
            return;
        }

        // Build createdDate as ISO-8601 (UTC)
        String createdDate = Instant.now().toString(); // requires API 26+

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "anonymous";

        // Create an ID client-side (lab requires using UUID)
        String id = UUID.randomUUID().toString();

        Expense newExpense = new Expense(id, amount, currency, category, remark, uid, createdDate);

        // Call Retrofit to add
        ExpenseApi api = RetrofitClient.getClient().create(ExpenseApi.class);
        Call<Expense> call = api.addExpense(ApiConfig.DB_NAME, newExpense);

        btnSave.setEnabled(false);
        call.enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(Call<Expense> call, Response<Expense> response) {
                btnSave.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), getString(R.string.expense_saved), Toast.LENGTH_SHORT).show();
                    // show list fragment
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ExpenseListFragment())
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(requireContext(), "Failed to save: " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e("AddExpense", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Expense> call, Throwable t) {
                btnSave.setEnabled(true);
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("AddExpense", "onFailure", t);
            }
        });
    }
}
