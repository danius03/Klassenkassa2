package com.example.klassenkassa.activities;


import android.os.Bundle;
import android.view.View;

import androidx.preference.PreferenceFragmentCompat;

import com.example.klassenkassa.R;

public class PreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences,rootKey);
    }
}
