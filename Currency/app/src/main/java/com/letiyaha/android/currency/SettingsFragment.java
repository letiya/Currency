package com.letiyaha.android.currency;

import android.os.Bundle;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Belle Lee on 7/21/2019.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    private MultiSelectListPreference mPrefCurrencies;

    private static final String PREF_KEY_CURRENCIES = "key_currencies";

    private static final String CURRENCY_DATA = "CURRENCY_DATA";

    public static SettingsFragment newInstance(Currency currencyData) {
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(CURRENCY_DATA, currencyData);
        settingsFragment.setArguments(bundle);
        return settingsFragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_settings);

        Bundle bundle = getArguments();
        if (bundle != null){
            Currency currencyData = bundle.getParcelable(CURRENCY_DATA);
            setupCurrenciesPref(currencyData);
        }
    }

    private void setupCurrenciesPref(Currency currencyData) {
        ArrayList<String> avaiableCurrencies = currencyData.getCurrencies();
        HashSet<String> favoriteCurrencies = currencyData.getFavoriteCurrencies();

        mPrefCurrencies = (MultiSelectListPreference) findPreference(PREF_KEY_CURRENCIES);

        CharSequence[] entries = new CharSequence[avaiableCurrencies.size()];
        CharSequence[] entryValues = new CharSequence[avaiableCurrencies.size()];
        for (int i = 0; i < avaiableCurrencies.size(); i++) {
            entries[i] = avaiableCurrencies.get(i);
            entryValues[i] = avaiableCurrencies.get(i);
        }

        mPrefCurrencies.setDefaultValue(entryValues);
        mPrefCurrencies.setEntries(entries);
        mPrefCurrencies.setEntryValues(entryValues);

        Set<String> currentlyAddedCurrencies = mPrefCurrencies.getValues();
        if (currentlyAddedCurrencies != null) {
            for (String key : currentlyAddedCurrencies) {
                if (favoriteCurrencies != null && !favoriteCurrencies.contains(key)) {
                    favoriteCurrencies.add(key);
                }
            }
        }
        mPrefCurrencies.setValues(favoriteCurrencies);
    }
}
