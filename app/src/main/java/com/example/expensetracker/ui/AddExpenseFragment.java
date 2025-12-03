package com.example.expensetracker.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
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

    private EditText etAmount, etDate, etRemark;
    private AutoCompleteTextView actvCurrency, actvCategory;
    private MaterialButton btnSave;
    private Button btnAddCategory;
    private ArrayAdapter<String> categoryAdapter;
    private List<String> categoryNames = new ArrayList<>();
    private ExecutorService executorService;

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

        etAmount = root.findViewById(R.id.et_amount);
        etDate = root.findViewById(R.id.et_date);
        etRemark = root.findViewById(R.id.et_remark);
        actvCurrency = root.findViewById(R.id.actv_currency);
        actvCategory = root.findViewById(R.id.actv_category);
        btnSave = root.findViewById(R.id.btn_save);
        btnAddCategory = root.findViewById(R.id.btn_add_category);

        executorService = Executors.newSingleThreadExecutor();

        setupCurrencySpinner();
        setupCategorySpinner();
        setupDatePicker(root);
        setupFocusListeners();
        setupButtonListeners();

        validate();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadCategoriesFromDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload categories when fragment resumes (e.g., after language change)
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

    private void setupCategorySpinner() {
        categoryAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.list_item_dropdown,
                categoryNames
        );
        actvCategory.setAdapter(categoryAdapter);
        actvCategory.setText("", false);
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
        View.OnFocusChangeListener focusWatcher = (v, hasFocus) -> validate();
        etAmount.setOnFocusChangeListener(focusWatcher);
        etDate.setOnFocusChangeListener(focusWatcher);
        actvCurrency.setOnFocusChangeListener(focusWatcher);
        actvCategory.setOnFocusChangeListener(focusWatcher);
    }

    private void setupButtonListeners() {
        btnSave.setOnClickListener(v -> saveExpense());
        btnAddCategory.setOnClickListener(v -> openNewCategoryActivity());
    }

    private void loadCategoriesFromDatabase() {
        executorService.execute(() -> {
            List<Category> categories = DatabaseHelper.getInstance(requireContext())
                    .categoryDao()
                    .getAllCategoriesSync();

            // Add default categories if database is empty
            if (categories.isEmpty()) {
                addDefaultCategories();
                categories = DatabaseHelper.getInstance(requireContext())
                        .categoryDao()
                        .getAllCategoriesSync();
            }

            List<Category> finalCategories = categories;
            runOnUiThread(() -> {
                categoryNames.clear();
                for (Category category : finalCategories) {
                    categoryNames.add(category.getName());
                }
                categoryAdapter.notifyDataSetChanged();
            });
        });
    }

    private void addDefaultCategories() {
        String[] defaultCategories = getResources().getStringArray(R.array.category_array);

        for (String categoryName : defaultCategories) {
            // Check if category already exists
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
            // Reload categories when returning from NewCategoryActivity
            loadCategoriesFromDatabase();
        }
    }

    private boolean validate() {
        boolean okAmount = etAmount.getText() != null && etAmount.getText().toString().trim().length() > 0;
        boolean okDate = etDate.getText() != null && etDate.getText().toString().trim().length() > 0;
        boolean okCurrency = actvCurrency.getText() != null && actvCurrency.getText().toString().trim().length() > 0;
        boolean okCategory = actvCategory.getText() != null && actvCategory.getText().toString().trim().length() > 0;

        boolean enabled = okAmount && okDate && okCurrency && okCategory;
        btnSave.setEnabled(enabled);
        btnSave.setAlpha(enabled ? 1f : 0.5f);
        return enabled;
    }

    private void saveExpense() {
        try {
            Log.d("SAVE_EXPENSE", "=== Starting saveExpense ===");

            // Get values
            String amountStr = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";
            String currency = actvCurrency.getText() != null ? actvCurrency.getText().toString().trim() : "";
            String date = etDate.getText() != null ? etDate.getText().toString().trim() : "";
            String category = actvCategory.getText() != null ? actvCategory.getText().toString().trim() : "";
            String remark = etRemark.getText() != null ? etRemark.getText().toString().trim() : "";

            Log.d("SAVE_EXPENSE", "Amount: " + amountStr + ", Currency: " + currency +
                    ", Date: " + date + ", Category: " + category);

            // Validation
            if (amountStr.isEmpty()) {
                etAmount.setError(getString(R.string.error_required));
                etAmount.requestFocus();
                return;
            }

            if (currency.isEmpty()) {
                Toast.makeText(requireContext(), "Please select currency", Toast.LENGTH_SHORT).show();
                return;
            }

            if (date.isEmpty()) {
                etDate.setError(getString(R.string.error_required));
                etDate.requestFocus();
                return;
            }

            if (category.isEmpty()) {
                Toast.makeText(requireContext(), "Please select category", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parse amount
            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                Log.d("SAVE_EXPENSE", "Parsed amount: " + amount);
            } catch (NumberFormatException ex) {
                etAmount.setError(getString(R.string.error_invalid_number));
                etAmount.requestFocus();
                return;
            }

            // Create ISO 8601 date - Works on all Android versions
            String createdDate;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                createdDate = sdf.format(new Date());
                Log.d("SAVE_EXPENSE", "Created date: " + createdDate);
            } catch (Exception e) {
                Log.e("SAVE_EXPENSE", "Date error: " + e.getMessage());
                createdDate = new Date().toString();
            }

            // Get user ID
            String uid = "anonymous";
            try {
                if (FirebaseAuth.getInstance() != null &&
                        FirebaseAuth.getInstance().getCurrentUser() != null) {
                    uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Log.d("SAVE_EXPENSE", "User ID: " + uid);
                } else {
                    Log.d("SAVE_EXPENSE", "No Firebase user, using anonymous");
                }
            } catch (Exception e) {
                Log.e("SAVE_EXPENSE", "Firebase error: " + e.getMessage());
            }

            // Create expense object
            String id = UUID.randomUUID().toString();
            Log.d("SAVE_EXPENSE", "Generated ID: " + id);

            Expense newExpense = new Expense(id, amount, currency, category, remark, uid, createdDate);
            Log.d("SAVE_EXPENSE", "Expense object created");

            // Disable save button
            btnSave.setEnabled(false);

            // Make API call
            try {
                Log.d("SAVE_EXPENSE", "Creating Retrofit client...");
                ExpenseApi api = RetrofitClient.getClient().create(ExpenseApi.class);
                Call<Expense> call = api.addExpense(ApiConfig.DB_NAME, newExpense);
                Log.d("SAVE_EXPENSE", "API call created, enqueuing...");

                call.enqueue(new Callback<Expense>() {
                    @Override
                    public void onResponse(Call<Expense> call, Response<Expense> response) {
                        btnSave.setEnabled(true);
                        Log.d("SAVE_EXPENSE", "API Response code: " + response.code());

                        if (response.isSuccessful()) {
                            Log.d("SAVE_EXPENSE", "Expense saved successfully!");
                            Toast.makeText(requireContext(),
                                    getString(R.string.expense_saved),
                                    Toast.LENGTH_SHORT).show();

                            // Clear form instead of navigating (prevents crashes)
                            clearForm();

                        } else {
                            String errorMsg = "Failed to save: " + response.code();
                            Log.e("SAVE_EXPENSE", errorMsg);
                            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Expense> call, Throwable t) {
                        btnSave.setEnabled(true);
                        Log.e("SAVE_EXPENSE", "API Failure: " + t.getMessage(), t);
                        Toast.makeText(requireContext(),
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                btnSave.setEnabled(true);
                Log.e("SAVE_EXPENSE", "Retrofit setup error: " + e.getMessage(), e);
                Toast.makeText(requireContext(),
                        "API error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e("SAVE_EXPENSE", "Unexpected error in saveExpense: " + e.getMessage(), e);
            Toast.makeText(requireContext(),
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            if (btnSave != null) {
                btnSave.setEnabled(true);
            }
        }
    }

    private void clearForm() {
        try {
            if (etAmount != null) etAmount.setText("");
            if (etDate != null) etDate.setText("");
            if (etRemark != null) etRemark.setText("");
            if (actvCurrency != null) actvCurrency.setText("");
            if (actvCategory != null) actvCategory.setText("");

            validate();

            Toast.makeText(requireContext(),
                    "Form cleared. Ready for next expense.",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("CLEAR_FORM", "Error clearing form: " + e.getMessage());
        }
    }

    private void runOnUiThread(Runnable action) {
        if (getActivity() != null) {
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