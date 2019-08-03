package com.letiyaha.android.currency.utilities;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.letiyaha.android.currency.Currency;

import java.net.URL;
import java.util.Date;

/**
 * Created by Belle Lee on 7/27/2019.
 */

public class CurrencyJsonLiveData extends LiveData<Currency> {

    public CurrencyJsonLiveData(Date date) {
        loadData(date);
    }

    private void loadData(final Date date) {
        new AsyncTask<Void, Void, Currency>() {

            @Override
            protected Currency doInBackground(Void... voids) {
                URL currencyRequestUrl = NetworkUtils.buildHistoricalUrl(date);
                String jsonCurrencyResponse = NetworkUtils.getResponseFromHttpUrl(currencyRequestUrl);
                Currency currencyOnDate = CurrencyJsonUtils.parseHistoryCurrencyJson(jsonCurrencyResponse);
                return currencyOnDate;
            }

            @Override
            protected void onPostExecute(Currency currency) {
                setValue(currency);
            }
        }.execute();
    }
}
