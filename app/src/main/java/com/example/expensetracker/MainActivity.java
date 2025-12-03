package com.example.expensetracker;

import android.os.Bundle;
import android.util.Log;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.expensetracker.ui.AddExpenseFragment;
import com.example.expensetracker.ui.ExpenseListFragment;
import com.example.expensetracker.ui.HomeFragment;
import com.example.expensetracker.ui.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// ✅ Make sure it extends your BaseActivity
public class MainActivity extends BaseActivity {

    private static final String TAG = "MAIN_DEBUG";
    private static final String SELECTED_ITEM_KEY = "selected_item";

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);
        FragmentManager fm = getSupportFragmentManager();

        // ✅ THE CORRECTED INITIALIZATION LOGIC
        if (savedInstanceState == null) {
            // Instantiate all fragments first.
            Fragment homeFragment = new HomeFragment();
            Fragment addExpenseFragment = new AddExpenseFragment();
            Fragment expenseListFragment = new ExpenseListFragment();
            Fragment settingFragment = SettingFragment.newInstance();

            // Use a single, atomic transaction.
            fm.beginTransaction()
                    .add(R.id.fragment_container, settingFragment, "setting")
                    .add(R.id.fragment_container, expenseListFragment, "list")
                    .add(R.id.fragment_container, addExpenseFragment, "add")
                    .add(R.id.fragment_container, homeFragment, "home") // Add the home fragment last
                    // Now hide all fragments EXCEPT the home fragment.
                    .hide(settingFragment)
                    .hide(expenseListFragment)
                    .hide(addExpenseFragment)
                    // The home fragment, being the last one added and not hidden, will be visible.
                    .commitNow(); // Use commitNow() to ensure it happens synchronously before the next line.
        }

        bottomNav.setOnItemSelectedListener(item -> {
            String selectedTag = getTagForItemId(item.getItemId());
            Fragment activeFragment = getActiveFragment(fm);
            Fragment fragmentToShow = fm.findFragmentByTag(selectedTag);

            if (fragmentToShow != null && fragmentToShow != activeFragment) {
                Log.d(TAG, "Switching to fragment: " + selectedTag);
                fm.beginTransaction()
                        .hide(activeFragment)
                        .show(fragmentToShow)
                        .commit();
            }
            return true;
        });

        // Restore selection after a configuration change
        if (savedInstanceState != null) {
            int selectedId = savedInstanceState.getInt(SELECTED_ITEM_KEY, R.id.nav_home);
            bottomNav.setSelectedItemId(selectedId);
        } else {
            // For the first run, explicitly set the home item.
            bottomNav.setSelectedItemId(R.id.nav_home);
        }

        // Handle back press using the modern API
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (bottomNav.getSelectedItemId() != R.id.nav_home) {
                    bottomNav.setSelectedItemId(R.id.nav_home);
                } else {
                    finish();
                }
            }
        });
    }

    private String getTagForItemId(int itemId) {
        if (itemId == R.id.nav_home) return "home";
        if (itemId == R.id.nav_add) return "add";
        if (itemId == R.id.nav_list) return "list";
        if (itemId == R.id.nav_setting) return "setting";
        return "home"; // Default case
    }

    private Fragment getActiveFragment(FragmentManager fm) {
        for (Fragment fragment : fm.getFragments()) {
            if (fragment != null && fragment.isVisible()) {
                return fragment;
            }
        }
        // Fallback in case no fragment is visible yet during initialization
        return fm.findFragmentByTag("home");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_ITEM_KEY, bottomNav.getSelectedItemId());
    }
}
