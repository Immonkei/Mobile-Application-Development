package com.example.expensetracker;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import com.example.expensetracker.utils.LocaleHelper;

/**
 * A base class for all Activities in this app.
 * Its single responsibility is to manage the app's locale (language)
 * in a consistent and lifecycle-aware manner.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        // This method wraps the base context with our locale settings.
        // It's called before onCreate() and ensures the Activity has the correct resources.
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
