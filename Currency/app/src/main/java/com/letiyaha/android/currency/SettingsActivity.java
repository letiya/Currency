package com.letiyaha.android.currency;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Belle Lee on 7/20/2019.
 */

public class SettingsActivity extends AppCompatActivity {

    private static final String CURRENCY_DATA = "CURRENCY_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intentThatStartedThisActivity = getIntent();
        Currency currencyData = intentThatStartedThisActivity.getParcelableExtra(CURRENCY_DATA);
        SettingsFragment settingsFragment = SettingsFragment.newInstance(currencyData);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.general_settings_fragment, settingsFragment).commit();
    }
}
