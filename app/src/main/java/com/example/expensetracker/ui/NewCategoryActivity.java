package com.example.expensetracker.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.dao.DatabaseHelper;
import com.example.expensetracker.model.Category;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewCategoryActivity extends AppCompatActivity {

    private EditText editCategoryName;
    private Button btnAddCategory;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);

        executorService = Executors.newSingleThreadExecutor();

        editCategoryName = findViewById(R.id.editCategoryName);
        btnAddCategory = findViewById(R.id.btnAddCategory);

        setupListeners();
    }

    private void setupListeners() {
        btnAddCategory.setOnClickListener(v -> {
            String categoryName = editCategoryName.getText().toString().trim();

            if (categoryName.isEmpty()) {
                editCategoryName.setError(getString(R.string.error_category_empty));
                return;
            }

            // Check if category already exists
            executorService.execute(() -> {
                Category existing = DatabaseHelper.getInstance(this)
                        .categoryDao()
                        .getCategoryByName(categoryName);

                runOnUiThread(() -> {
                    if (existing != null) {
                        editCategoryName.setError(getString(R.string.error_category_exists));
                    } else {
                        addNewCategory(categoryName);
                    }
                });
            });
        });
    }

    private void addNewCategory(String categoryName) {
        executorService.execute(() -> {
            Category category = new Category(categoryName);
            DatabaseHelper.getInstance(this).categoryDao().insert(category);

            runOnUiThread(() -> {
                Toast.makeText(this,
                        getString(R.string.category_added_success),
                        Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}