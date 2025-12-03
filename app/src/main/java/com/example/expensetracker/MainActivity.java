package com.example.expensetracker;

import android.os.Bundle;
import android.util.Log;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.expensetracker.ui.AddExpenseFragment;
import com.example.expensetracker.ui.ExpenseListFragment;
import com.example.expensetracker.ui.HomeFragment;
import com.example.expensetracker.ui.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MAIN_DEBUG";
    private static final String SELECTED_ITEM_KEY = "selected_item";

    private BottomNavigationView bottomNav;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);
        fm = getSupportFragmentManager();

        // This block ONLY runs on the very first creation of the activity.
        // After a language change, savedInstanceState is NOT null, so Android's
        // FragmentManager handles re-creating the fragments.
        if (savedInstanceState == null) {
            initializeFragments();
        }

        setupBottomNavigation();
        handleBackButton();

        // The logic to restore the selected tab needs to run on every creation,
        // but we only set the default on the very first run.
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private void initializeFragments() {
        // Use a single transaction to add all fragments and hide the non-active ones.
        // This prevents them from being visible all at once.
        fm.beginTransaction()
                .add(R.id.fragment_container, new HomeFragment(), "home")
                .add(R.id.fragment_container, new AddExpenseFragment(), "add")
                .add(R.id.fragment_container, new ExpenseListFragment(), "list")
                .add(R.id.fragment_container, SettingFragment.newInstance(), "setting")
                .commitNow(); // Using commitNow to ensure fragments are available immediately

        // After adding, explicitly show the home fragment and hide the others.
        showFragmentByTag("home");
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            String selectedTag = getTagForItemId(item.getItemId());
            showFragmentByTag(selectedTag);
            return true;
        });
    }

    private void showFragmentByTag(String tag) {
        Fragment fragmentToShow = fm.findFragmentByTag(tag);
        if (fragmentToShow == null) {
            // This case should ideally not happen if initialization is correct.
            Log.e(TAG, "Fragment with tag " + tag + " not found!");
            return;
        }

        FragmentTransaction transaction = fm.beginTransaction();
        for (Fragment fragment : fm.getFragments()) {
            if (fragment == fragmentToShow) {
                transaction.show(fragment);
            } else {
                transaction.hide(fragment);
            }
        }
        transaction.commit();
    }


    private String getTagForItemId(int itemId) {
        if (itemId == R.id.nav_home) return "home";
        if (itemId == R.id.nav_add) return "add";
        if (itemId == R.id.nav_list) return "list";
        if (itemId == R.id.nav_setting) return "setting";
        return "home";
    }

    private void handleBackButton() {
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the currently selected tab so we can restore it after recreation.
        outState.putInt(SELECTED_ITEM_KEY, bottomNav.getSelectedItemId());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the selected tab. This is called after onCreate.
        int selectedId = savedInstanceState.getInt(SELECTED_ITEM_KEY, R.id.nav_home);
        bottomNav.setSelectedItemId(selectedId);
        // Also ensure the correct fragment is visible.
        showFragmentByTag(getTagForItemId(selectedId));
    }
}
