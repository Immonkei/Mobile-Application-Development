package com.example.expensetracker.ui;

import android.content.Intent;
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

    private RecyclerView recycler;
    private ProgressBar progressBar;
    private ExpenseAdapter adapter;
    private List<Expense> expenses = new ArrayList<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_list, container, false);
        recycler = view.findViewById(R.id.recycler_expenses);
        progressBar = view.findViewById(R.id.progress_bar);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseAdapter(expenses, e -> {
            Intent i = new Intent(getActivity(), com.example.expensetracker.detail.DetailExpenseActivity.class);
            i.putExtra(com.example.expensetracker.detail.DetailExpenseActivity.EXTRA_EXPENSE_ID, e.getId());
            startActivity(i);
        });
        recycler.setAdapter(adapter);

        loadExpenses();
        return view;
    }

    public void reloadExpenses() { loadExpenses(); }

    private void loadExpenses() {
        progressBar.setVisibility(View.VISIBLE);

        ExpenseApi api = RetrofitClient.getClient().create(ExpenseApi.class);
        api.getExpenses(ApiConfig.DB_NAME).enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    expenses.clear();
                    expenses.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load expenses", Toast.LENGTH_SHORT).show();
                    Log.e("ExpenseList", "Response error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ExpenseList", "Failure", t);
            }
        });
    }
}
