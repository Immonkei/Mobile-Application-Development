package com.example.expensetracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.expensetracker.R;
import com.example.expensetracker.utils.LocaleHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingFragment extends Fragment {

    private Spinner languageSpinner;
    private TextView currentLanguageText;
    private boolean isFirstTime = true;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentLanguageText = view.findViewById(R.id.current_language_text);
        languageSpinner = view.findViewById(R.id.language_spinner);

        setupLanguageSpinner();
        updateCurrentLanguageDisplay();
    }

    private void setupLanguageSpinner() {
        String[] languageNames = getResources().getStringArray(R.array.language_entries);
        String[] languageCodes = getResources().getStringArray(R.array.language_values);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                languageNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        String currentLang = LocaleHelper.getSavedLanguage(requireContext());
        int position = currentLang.equals("km") ? 1 : 0;
        languageSpinner.setSelection(position);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstTime) {
                    isFirstTime = false;
                    return;
                }

                String selectedLang = languageCodes[position];
                String currentLang = LocaleHelper.getSavedLanguage(requireContext());

                if (!selectedLang.equals(currentLang)) {
                    changeLanguage(selectedLang);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void changeLanguage(String languageCode) {
        // Save the new language
        LocaleHelper.setLocale(requireContext(), languageCode);

        // Show toast message
        String message = languageCode.equals("km")
                ? "ភាសាត្រូវបានកំណត់ទៅខ្មែរ"
                : "Language set to English";
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

        // Update the display IMMEDIATELY in this fragment
        updateCurrentLanguageDisplay();

        // Update the spinner text to show current selection
        updateSpinnerDisplay(languageCode);

        // Apply locale to the CURRENT activity (MainActivity)
        if (getActivity() != null) {
            LocaleHelper.applySavedLocale(getActivity());

            // Update the Bottom Navigation labels
            updateBottomNavigationLabels();

            // Update the fragment title
            updateFragmentTitle();
        }

        // Language will persist for next app launch
        // User stays in SettingsFragment
    }

    private void updateCurrentLanguageDisplay() {
        String currentLang = LocaleHelper.getSavedLanguage(requireContext());
        String displayText = currentLang.equals("km")
                ? "ភាសាបច្ចុប្បន្ន៖ ខ្មែរ"
                : "Current Language: English";
        if (currentLanguageText != null) {
            currentLanguageText.setText(displayText);
        }
    }

    private void updateSpinnerDisplay(String languageCode) {
        if (languageSpinner != null) {
            // Force update spinner selection
            int position = languageCode.equals("km") ? 1 : 0;
            languageSpinner.setSelection(position);
        }
    }

    private void updateBottomNavigationLabels() {
        if (getActivity() != null) {
            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_nav);
            if (bottomNav != null) {
                // Get menu items
                bottomNav.getMenu().findItem(R.id.nav_home).setTitle(R.string.nav_home);
                bottomNav.getMenu().findItem(R.id.nav_add).setTitle(R.string.nav_add_expense);
                bottomNav.getMenu().findItem(R.id.nav_list).setTitle(R.string.nav_expense_list);
                bottomNav.getMenu().findItem(R.id.nav_setting).setTitle(R.string.nav_setting);
            }
        }
    }

    private void updateFragmentTitle() {
        if (getActivity() != null) {
            // Update the toolbar title if you have one
            // getActivity().setTitle(R.string.settings_title);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh display when coming back to fragment
        updateCurrentLanguageDisplay();
    }
}