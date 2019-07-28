package com.letiyaha.android.currency.utilities;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.letiyaha.android.currency.Currency;

import java.net.URL;

/**
 * Created by Belle Lee on 7/27/2019.
 */

public class CurrencyJsonLiveData extends LiveData<Currency> {

    public CurrencyJsonLiveData() {
        loadData();
    }

    private void loadData() {
        new AsyncTask<Void, Void, Currency>() {

            @Override
            protected Currency doInBackground(Void... voids) {
                URL currencyRequestUrl = NetworkUtils.buildLatestUrl();
                String jsonCurrencyResponse = NetworkUtils.getResponseFromHttpUrl(currencyRequestUrl);
                Currency latestCurrency = CurrencyJsonUtils.parseCurrencyJson(jsonCurrencyResponse);
                return latestCurrency;
            }

            @Override
            protected void onPostExecute(Currency currency) {
                setValue(currency);
            }
        }.execute();
    }
}
