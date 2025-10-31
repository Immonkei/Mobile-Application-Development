package com.example.expensetracker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.expensetracker.ui.AddExpenseFragment;
import com.example.expensetracker.ui.ExpenseListFragment;
import com.example.expensetracker.ui.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.expensetracker.ui.HomeFragment;
import com.example.expensetracker.ui.AddExpenseFragment;
import com.example.expensetracker.ui.ExpenseListFragment;

public class MainActivity extends AppCompatActivity {

    private static final String SELECTED_ITEM_KEY = "selected_item";
    private int selectedItemId = R.id.nav_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply existing theme from Lab 3
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setOnItemSelectedListener(item -> {
            selectedItemId = item.getItemId();
            Fragment fragment;
            if (item.getItemId() == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_add) {
                fragment = new AddExpenseFragment();
            } else {
                fragment = new ExpenseListFragment();
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home); // Home loads by default
        } else {
            selectedItemId = savedInstanceState.getInt(SELECTED_ITEM_KEY, R.id.nav_home);
            bottomNav.setSelectedItemId(selectedItemId);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_ITEM_KEY, selectedItemId);
    }
}
