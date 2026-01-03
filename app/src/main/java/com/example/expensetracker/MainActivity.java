package com.example.expensetracker;

import android.content.pm.PackageManager;
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

        // ✅ Android 13+ notification permission
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        101
                );
            }
        }

        createNotificationChannel();


        bottomNav = findViewById(R.id.bottom_nav);
        fm = getSupportFragmentManager();

        if (savedInstanceState == null) {
            initializeFragments();
        }

        setupBottomNavigation();
        handleBackButton(); // This method has been corrected

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private void initializeFragments() {
        fm.beginTransaction()
                .add(R.id.fragment_container, new HomeFragment(), "home")
                .add(R.id.fragment_container, new AddExpenseFragment(), "add")
                .add(R.id.fragment_container, new ExpenseListFragment(), "list")
                .add(R.id.fragment_container, SettingFragment.newInstance(), "setting")
                .commitNow();

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
        return "home"; // Default to home
    }

    // ===================================================================
    // THIS IS THE CORRECTED METHOD
    // ===================================================================
    private void handleBackButton() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // STEP 1: Check if the FragmentManager can handle the back press.
                // This is true if a detail screen (or any other fragment) was added to the back stack.
                if (fm.getBackStackEntryCount() > 0) {
                    // If there are fragments on the back stack, let the manager pop them.
                    // This will correctly close the detail screen and show the list screen.
                    fm.popBackStack();
                }
                // STEP 2: If the back stack is empty, THEN execute your custom bottom navigation logic.
                else if (bottomNav.getSelectedItemId() != R.id.nav_home) {
                    // If not on the home tab, navigate to the home tab.
                    bottomNav.setSelectedItemId(R.id.nav_home);
                }
                // STEP 3: If back stack is empty AND you are on the home tab, let the system handle it (exit the app).
                else {
                    // This is the recommended way to call the default back button action
                    // (which is to finish the activity) from within an OnBackPressedCallback.
                    if (isEnabled()) {
                        setEnabled(false);
                        onBackPressed();
                    }
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_ITEM_KEY, bottomNav.getSelectedItemId());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int selectedId = savedInstanceState.getInt(SELECTED_ITEM_KEY, R.id.nav_home);
        bottomNav.setSelectedItemId(selectedId);
        showFragmentByTag(getTagForItemId(selectedId));
    }
//    Notivication-Chhanel
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel =
                    new android.app.NotificationChannel(
                            "budget_channel",
                            "Budget Warning",
                            android.app.NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription("Budget limit warning notifications");

            android.app.NotificationManager manager =
                    getSystemService(android.app.NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

}
