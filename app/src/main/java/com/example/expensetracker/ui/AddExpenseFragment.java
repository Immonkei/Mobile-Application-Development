package com.example.expensetracker.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
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
import com.example.expensetracker.data.ExpenseData;
import com.example.expensetracker.ui.ExpenseListFragment;
import com.example.expensetracker.model.Expense;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddExpenseFragment extends Fragment {

    private EditText etAmount, etDate, etRemark;
    private AutoCompleteTextView actvCurrency, actvCategory;
    private MaterialButton btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_expense, container, false);

        // Optional toolbar back behaviour (if desired)
        MaterialToolbar toolbar = root.findViewById(R.id.toolbar_add);
        if (toolbar != null) {
            // Provide a back icon if you have one; otherwise skip
            // toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        }

        etAmount     = root.findViewById(R.id.et_amount);
        etDate       = root.findViewById(R.id.et_date);
        etRemark     = root.findViewById(R.id.et_remark);
        actvCurrency = root.findViewById(R.id.actv_currency);
        actvCategory = root.findViewById(R.id.actv_category);
        btnSave      = root.findViewById(R.id.btn_save);

        // Dropdown adapters (use our small list item layout)
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

        // Date picker: make icon, field and whole TextInputLayout trigger it
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
        // allow tapping the whole layout (ripple) to open too
        if (tilDate != null) tilDate.setOnClickListener(showDatePicker);
        etDate.setOnClickListener(showDatePicker);

        // simple validation watcher via focus changes and text change (lightweight)
        View.OnFocusChangeListener focusWatcher = (v, hasFocus) -> validate();
        etAmount.setOnFocusChangeListener(focusWatcher);
        etDate.setOnFocusChangeListener(focusWatcher);
        actvCurrency.setOnFocusChangeListener(focusWatcher);
        actvCategory.setOnFocusChangeListener(focusWatcher);

        // Save action: validate, create Expense, add to ExpenseData, navigate to list
        btnSave.setOnClickListener(v -> {
            if (!validate()) return;

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

            // compute next id (simple in-memory)
            // THIS IS THE CORRECTED LINE
            List<Expense> all = ExpenseData.getExpenses();
            int nextId = 1;
            for (Expense e : all) {
                if (e.getId() >= nextId) nextId = e.getId() + 1;
            }

            Expense newExp = new Expense(nextId, amount, currency, date, category, remark);
            ExpenseData.addExpense(newExp);

            Toast.makeText(requireContext(), getString(R.string.expense_saved), Toast.LENGTH_SHORT).show();

            // clear fields
            etAmount.setText("");
            actvCurrency.setText("", false);
            etDate.setText("");
            actvCategory.setText("", false);
            etRemark.setText("");

            // show list fragment so user sees the new item
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ExpenseListFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // initial validate
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
}
