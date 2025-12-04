package com.example.expensetracker.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.expensetracker.R;
import com.example.expensetracker.dao.DatabaseHelper;
import com.example.expensetracker.data.ApiConfig;
import com.example.expensetracker.data.ExpenseApi;
import com.example.expensetracker.data.RetrofitClient;
import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.Expense;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddExpenseFragment extends Fragment {

    public static final String KEY_EXPENSE_ADDED = "expense_added";

    private EditText etAmount, etDate, etRemark;
    private AutoCompleteTextView actvCurrency, actvCategory;
    private MaterialButton btnSave;
    private Button btnAddCategory;
    private ArrayAdapter<String> categoryAdapter;
    private ExecutorService executorService;

    public AddExpenseFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_expense, container, false);

        MaterialToolbar toolbar = root.findViewById(R.id.toolbar_add);
        etAmount = root.findViewById(R.id.et_amount);
        etDate = root.findViewById(R.id.et_date);
        etRemark = root.findViewById(R.id.et_remark);
        actvCurrency = root.findViewById(R.id.actv_currency);
        actvCategory = root.findViewById(R.id.actv_category);
        btnSave = root.findViewById(R.id.btn_save);
        btnAddCategory = root.findViewById(R.id.btn_add_category);

        executorService = Executors.newSingleThreadExecutor();

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        }

        setupCurrencySpinner();
        setupDatePicker(root);
        setupFocusListeners();

        setupButtonListeners();

        validate();
        return root;
    }

    private void setupButtonListeners() {
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveExpense());
        } else {
            Log.e("AddExpenseFragment", "btnSave is null. Check R.id.btn_save in your layout.");
        }

        if (btnAddCategory != null) {
            btnAddCategory.setOnClickListener(v -> openNewCategoryActivity());
        } else {
            Log.e("AddExpenseFragment", "btnAddCategory is null. Check R.id.btn_add_category in your layout.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategoriesFromDatabase();
    }

    private void setupCurrencySpinner() {
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.list_item_dropdown,
                getResources().getStringArray(R.array.currency_array)
        );
        actvCurrency.setAdapter(currencyAdapter);
        actvCurrency.setText("", false);
    }

    private void setupDatePicker(View root) {
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
    }

    private void setupFocusListeners() {
        View.OnFocusChangeListener focusWatcher = (v, hasFocus) -> {
            if (!hasFocus) {
                validate();
            }
        };
        etAmount.setOnFocusChangeListener(focusWatcher);
        etDate.setOnFocusChangeListener(focusWatcher);
        actvCurrency.setOnFocusChangeListener(focusWatcher);
        actvCategory.setOnFocusChangeListener(focusWatcher);
    }

    private void loadCategoriesFromDatabase() {
        executorService.execute(() -> {
            List<Category> categories = DatabaseHelper.getInstance(requireContext())
                    .categoryDao()
                    .getAllCategoriesSync();

            if (categories.isEmpty()) {
                addDefaultCategories();
                categories = DatabaseHelper.getInstance(requireContext())
                        .categoryDao()
                        .getAllCategoriesSync();
            }

            List<String> newCategoryNames = new ArrayList<>();
            for (Category category : categories) {
                newCategoryNames.add(category.getName());
            }

            runOnUiThread(() -> {
                categoryAdapter = new ArrayAdapter<>(
                        requireContext(),
                        R.layout.list_item_dropdown,
                        newCategoryNames
                );
                if (actvCategory != null) {
                    actvCategory.setAdapter(categoryAdapter);
                }
            });
        });
    }

    private void addDefaultCategories() {
        String[] defaultCategories = getResources().getStringArray(R.array.category_array);

        for (String categoryName : defaultCategories) {
            Category existing = DatabaseHelper.getInstance(requireContext())
                    .categoryDao()
                    .getCategoryByName(categoryName);

            if (existing == null) {
                Category category = new Category(categoryName);
                DatabaseHelper.getInstance(requireContext())
                        .categoryDao()
                        .insert(category);
            }
        }
    }

    private void openNewCategoryActivity() {
        try {
            Intent intent = new Intent(requireActivity(), NewCategoryActivity.class);
            startActivityForResult(intent, 100);
        } catch (Exception e) {
            Log.e("AddExpense", "Error opening NewCategoryActivity: " + e.getMessage());
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            loadCategoriesFromDatabase();
        }
    }

    private boolean validate() {
        boolean okAmount = etAmount.getText() != null && !etAmount.getText().toString().trim().isEmpty();
        boolean okDate = etDate.getText() != null && !etDate.getText().toString().trim().isEmpty();
        boolean okCurrency = actvCurrency.getText() != null && !actvCurrency.getText().toString().trim().isEmpty();
        boolean okCategory = actvCategory.getText() != null && !actvCategory.getText().toString().trim().isEmpty();

        boolean enabled = okAmount && okDate && okCurrency && okCategory;
        if (btnSave != null) {
            btnSave.setEnabled(enabled);
            btnSave.setAlpha(enabled ? 1f : 0.5f);
        }
        return enabled;
    }

    private void saveExpense() {
        try {
            String amountStr = etAmount.getText().toString().trim();
            String currency = actvCurrency.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String category = actvCategory.getText().toString().trim();
            String remark = etRemark.getText().toString().trim();

            if (!validate()) {
                Toast.makeText(requireContext(), getString(R.string.error_fill_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException ex) {
                etAmount.setError(getString(R.string.error_invalid_number));
                etAmount.requestFocus();
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String createdDate = sdf.format(new Date());

            String uid = "anonymous";
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }

            String id = UUID.randomUUID().toString();
            Expense newExpense = new Expense(id, amount, currency, category, remark, uid, createdDate);

            btnSave.setEnabled(false);

            ExpenseApi api = RetrofitClient.getClient().create(ExpenseApi.class);
            Call<Expense> call = api.addExpense(ApiConfig.DB_NAME, newExpense);

            call.enqueue(new Callback<Expense>() {
                @Override
                public void onResponse(@NonNull Call<Expense> call, @NonNull Response<Expense> response) {
                    btnSave.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), getString(R.string.expense_saved), Toast.LENGTH_SHORT).show();
                        clearForm();

                        // Notify ExpenseListFragment that new expense was added
                        Bundle result = new Bundle();
                        result.putBoolean(KEY_EXPENSE_ADDED, true);
                        getParentFragmentManager().setFragmentResult(KEY_EXPENSE_ADDED, result);

                        // DON'T navigate back - stay on add expense screen
                        // requireActivity().onBackPressed(); // REMOVED THIS LINE

                        // Optional: Scroll to top or show confirmation message
                        Toast.makeText(requireContext(), "Expense saved! You can add another.", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMsg = "Failed to save: " + response.code();
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Expense> call, @NonNull Throwable t) {
                    btnSave.setEnabled(true);
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Log.e("SAVE_EXPENSE", "Unexpected error in saveExpense: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            if (btnSave != null) {
                btnSave.setEnabled(true);
            }
        }
    }

    private void clearForm() {
        if (etAmount != null) etAmount.setText("");
        if (etDate != null) etDate.setText("");
        if (etRemark != null) etRemark.setText("");
        if (actvCurrency != null) actvCurrency.setText("", false);
        if (actvCategory != null) actvCategory.setText("", false);
        etAmount.requestFocus();
        validate();
    }

    private void runOnUiThread(Runnable action) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().runOnUiThread(action);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}