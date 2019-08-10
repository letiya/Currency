package com.letiyaha.android.currency;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.letiyaha.android.currency.database.AppDatabase;
import com.letiyaha.android.currency.database.CurrencyEntry;
import com.letiyaha.android.currency.utilities.AppExecutors;
import com.letiyaha.android.currency.utilities.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Belle Lee on 7/21/2019.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static AppDatabase mDb;
    private static SettingsFragment instance;
    private static final String PREF_KEY_CURRENCIES = "key_currencies";

    private MultiSelectListPreference mPrefCurrencies;

    public static SettingsFragment getInstance(Context context) {
        if (instance == null) {
            instance = new SettingsFragment();
        }
        mDb = AppDatabase.getInstance(context);
        return instance;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_settings);

        getDbCurrencies();

    }

    private ArrayList<String> avaiableCurrencies;

    private void getDbCurrencies() {
        AppExecutors.getInstance().DiskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<CurrencyEntry> currencyEntries = mDb.currencyDao().loadCurrenciesByDate(Util.getToday());
                avaiableCurrencies = new ArrayList<>();
                for (int i = 0; i < currencyEntries.size(); i++) {
                    CurrencyEntry currencyEntry = currencyEntries.get(i);
                    avaiableCurrencies.add(currencyEntry.getCurrency());
                }
                getDbFavoriteCurrencies();
            }
        });
    }

    private HashSet<String> favoriteCurrencies;

    private void getDbFavoriteCurrencies() {
        AppExecutors.getInstance().DiskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<CurrencyEntry> currencyEntries = mDb.currencyDao().loadFavoriteCurrencies(Util.getToday());
                favoriteCurrencies = new HashSet<String>();
                for (int i = 0; i < currencyEntries.size(); i++) {
                    CurrencyEntry currencyEntry = currencyEntries.get(i);
                    favoriteCurrencies.add(currencyEntry.getCurrency());
                }
                setupCurrenciesPref();
            }
        });
    }

    private void setupCurrenciesPref() {
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
        mPrefCurrencies.setValues(favoriteCurrencies);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference != null) {
            final Set<String> currentFavoriteCurrencies = sharedPreferences.getStringSet(key, null);
            AppExecutors.getInstance().DiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.currencyDao().resetFavoriteSetting();
                    for (final String currency : currentFavoriteCurrencies) {
                        AppExecutors.getInstance().DiskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                mDb.currencyDao().updateFavoriteCurrency("true", currency);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        /* Unregister the preference change listener */
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        /* Register the preference change listener */
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
}