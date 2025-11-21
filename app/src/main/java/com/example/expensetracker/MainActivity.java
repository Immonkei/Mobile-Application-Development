package com.example.expensetracker;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.expensetracker.ui.AddExpenseFragment;
import com.example.expensetracker.ui.ExpenseListFragment;
import com.example.expensetracker.ui.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_DEBUG";
    private static final String SELECTED_ITEM_KEY = "selected_item";
    private int selectedItemId = R.id.nav_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // must contain fragment_container

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
            Log.d(TAG, "Switching fragment to " + fragment.getClass().getSimpleName());
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        } else {
            selectedItemId = savedInstanceState.getInt(SELECTED_ITEM_KEY, R.id.nav_home);
            bottomNav.setSelectedItemId(selectedItemId);
        }
    }

    @Override
    public void onBackPressed() {
        // If there are fragment entries on back stack, pop one (Detail -> List works)
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_ITEM_KEY, selectedItemId);
    }
}
