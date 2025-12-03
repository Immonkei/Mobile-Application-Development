package com.example.expensetracker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import java.util.Locale;

public class LocaleHelper {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_LANG = "app_language";

    // Save language preference
    public static void saveLanguage(Context context, String languageCode) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANG, languageCode).apply();
    }

    // Get saved language
    public static String getSavedLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LANG, "en");
    }

    // Apply locale to app
    public static void applySavedLocale(Context context) {
        String language = getSavedLanguage(context);
        setAppLocale(context, language);
    }

    // Set and save new language
    public static void setLocale(Context context, String languageCode) {
        saveLanguage(context, languageCode);
        setAppLocale(context, languageCode);
    }

    // Apply locale to app configuration
    private static void setAppLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            config.setLayoutDirection(locale);
        } else {
            config.locale = locale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLayoutDirection(locale);
            }
        }

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    // For attachBaseContext
    public static Context onAttach(Context context) {
        String language = getSavedLanguage(context);
        return updateResources(context, language);
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.createConfigurationContext(config);
        } else {
            resources.updateConfiguration(config, resources.getDisplayMetrics());
            return context;
        }
    }
}