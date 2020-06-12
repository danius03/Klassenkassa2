package com.example.klassenkassa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.SwitchPreference;

import android.os.Bundle;
import android.view.View;

import com.example.klassenkassa.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment()).commit();
    }
}
