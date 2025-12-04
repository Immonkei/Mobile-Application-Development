package com.example.expensetracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView; // ✅ 1. IMPORT TextView
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.expensetracker.R;
import com.example.expensetracker.auth.LoginActivity;
import com.example.expensetracker.utils.LocaleHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class SettingFragment extends Fragment {

    private boolean isInitialSelection = true;
    private TextView currentLanguageText;
    private MaterialButton btnSignOut;

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
        Spinner languageSpinner = view.findViewById(R.id.language_spinner);

        // ✅ 3. FIND the TextView by its ID
        // Make sure your TextView in fragment_setting.xml has this ID
        currentLanguageText = view.findViewById(R.id.current_language_value);

        setupLanguageSpinner(languageSpinner);
    }

    // ✅ 4. ADD the onResume method
    @Override
    public void onResume() {
        super.onResume();
        // onResume is called after recreation is complete. This is the perfect
        // time to make sure the UI displays the correct state.
        updateLanguageDisplay();

        // We also reset the spinner flag here to prevent loops
        isInitialSelection = true;
    }

    // ✅ 5. ADD the UI update method
    private void updateLanguageDisplay() {
        // This method will now use the fully loaded resources.
        if (currentLanguageText != null) {
            currentLanguageText.setText(getString(R.string.current_language_label));
        }
    }

    private void setupLanguageSpinner(Spinner languageSpinner) {
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
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(currentLang)) {
                languageSpinner.setSelection(i);
                break;
            }
        }

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isInitialSelection) {
                    isInitialSelection = false;
                    return;
                }

                String selectedLangCode = languageCodes[position];
                String currentLangCode = LocaleHelper.getSavedLanguage(requireContext());

                if (!selectedLangCode.equals(currentLangCode)) {
                    changeLanguageAndRecreate(selectedLangCode);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void changeLanguageAndRecreate(String languageCode) {
        LocaleHelper.setLocale(requireContext(), languageCode);

        if (getActivity() != null) {
            getActivity().recreate();
        }
    }


    private void setupSignOutButton() {
        View btnSignOut = null;
        btnSignOut.setOnClickListener(v -> {
            signOutUser();
        });
    }

    private void signOutUser() {
        try {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Navigate to LoginActivity
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();

            Toast.makeText(requireContext(),
                    getString(R.string.sign_out),
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(requireContext(),
                    "Error signing out: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

}
