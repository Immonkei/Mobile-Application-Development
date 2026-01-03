//package com.example.expensetracker.ui;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.expensetracker.R;
//import com.example.expensetracker.data.ApiConfig;
//import com.example.expensetracker.data.ExpenseApi;
//import com.example.expensetracker.data.RetrofitClient;
//import com.example.expensetracker.model.Expense;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class ExpenseListFragment extends Fragment {
//
//    private static final String TAG = "ExpenseList";
//    private RecyclerView recycler;
//    private ProgressBar progressBar;
//    private ExpenseAdapter adapter;
//    private final List<Expense> expenses = new ArrayList<>();
//    private boolean firstLoadDone = false;
//    private static long lastClickTime = 0;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Listen for new expense added from AddExpenseFragment
//        getParentFragmentManager().setFragmentResultListener(
//                AddExpenseFragment.KEY_EXPENSE_ADDED,
//                this,
//                (requestKey, result) -> {
//                    // Refresh the list when new expense is added
//                    if (result != null && result.getBoolean(AddExpenseFragment.KEY_EXPENSE_ADDED, false)) {
//                        Log.d(TAG, "Received expense added notification, refreshing list");
//                        loadExpenses();
//                    }
//                }
//        );
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_expense_list, container, false);
//        recycler = v.findViewById(R.id.recycler_expenses);
//        progressBar = v.findViewById(R.id.progress_bar);
//        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        adapter = new ExpenseAdapter(expenses, e -> {
//            long now = System.currentTimeMillis();
//            if (now - lastClickTime < 700) return; // debounce
//            lastClickTime = now;
//
//            // open detail as fragment, add to back stack
//            String id = e.getId() != null ? String.valueOf(e.getId()) : null;
//            if (id == null) return;
//
//            getParentFragmentManager()
//                    .beginTransaction()
//                    .setCustomAnimations(
//                            android.R.anim.slide_in_left,
//                            android.R.anim.slide_out_right,
//                            android.R.anim.slide_in_left,
//                            android.R.anim.slide_out_right
//                    )
//                    .replace(R.id.fragment_container, ExpenseDetailFragment.newInstance(id))
//                    .addToBackStack(null)
//                    .commit();
//        });
//
//        recycler.setAdapter(adapter);
//
//        if (!firstLoadDone) {
//            loadExpenses();
//            firstLoadDone = true;
//        }
//
//        return v;
//    }
//
//    private void loadExpenses() {
//        progressBar.setVisibility(View.VISIBLE);
//        ExpenseApi api = RetrofitClient.getClient().create(ExpenseApi.class);
//        api.getExpenses(ApiConfig.DB_NAME).enqueue(new Callback<List<Expense>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<Expense>> call,
//                                   @NonNull Response<List<Expense>> response) {
//                progressBar.setVisibility(View.GONE);
//                if (response.isSuccessful() && response.body() != null) {
//                    expenses.clear();
//                    expenses.addAll(response.body());
//                    adapter.notifyDataSetChanged();
//                    Log.d(TAG, "Loaded " + expenses.size() + " expenses");
//                } else {
//                    Toast.makeText(getContext(), "Failed to load expenses", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "Response code: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<Expense>> call, @NonNull Throwable t) {
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
//                Log.e(TAG, "Failure", t);
//            }
//        });
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (recycler != null) {
//            recycler.clearFocus();
//            recycler.stopScroll();
//        }
//        Log.d(TAG, "onResume() — stable (no reload)");
//    }
//}
package com.example.expensetracker.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.data.ApiConfig;
import com.example.expensetracker.data.ExpenseApi;
import com.example.expensetracker.data.RetrofitClient;
import com.example.expensetracker.model.Expense;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseListFragment extends Fragment {

    private static final String TAG = "ExpenseList";
    private RecyclerView recycler;
    private ProgressBar progressBar;
    private ExpenseAdapter adapter;
    private final List<Expense> expenses = new ArrayList<>();
    private boolean firstLoadDone = false;
    private static long lastClickTime = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Listen for new expense added from AddExpenseFragment
        getParentFragmentManager().setFragmentResultListener(
                AddExpenseFragment.KEY_EXPENSE_ADDED,
                this,
                (requestKey, result) -> {
                    // Refresh the list when new expense is added
                    if (result != null && result.getBoolean(AddExpenseFragment.KEY_EXPENSE_ADDED, false)) {
                        Log.d(TAG, "Received expense added notification, refreshing list");
                        loadExpenses();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_expense_list, container, false);
        recycler = v.findViewById(R.id.recycler_expenses);
        progressBar = v.findViewById(R.id.progress_bar);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ExpenseAdapter(expenses, e -> {
            long now = System.currentTimeMillis();
            if (now - lastClickTime < 700) return; // debounce
            lastClickTime = now;

            // open detail as fragment, add to back stack
            String id = e.getId() != null ? String.valueOf(e.getId()) : null;
            if (id == null) return;

            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right,
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, ExpenseDetailFragment.newInstance(id))
                    .addToBackStack(null)
                    .commit();
        });

        recycler.setAdapter(adapter);

        if (!firstLoadDone) {
            loadExpenses();
            firstLoadDone = true;
        }

        return v;
    }

    private void loadExpenses() {
        progressBar.setVisibility(View.VISIBLE);
        ExpenseApi api = RetrofitClient.getClient().create(ExpenseApi.class);
        api.getExpenses(ApiConfig.DB_NAME).enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(@NonNull Call<List<Expense>> call,
                                   @NonNull Response<List<Expense>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    expenses.clear();
                    expenses.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Loaded " + expenses.size() + " expenses");
                } else {
                    Toast.makeText(getContext(), "Failed to load expenses", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Expense>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failure", t);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // ✅ force RecyclerView to redraw when returning from Detail
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        Log.d(TAG, "onResume → list refreshed");
    }
}