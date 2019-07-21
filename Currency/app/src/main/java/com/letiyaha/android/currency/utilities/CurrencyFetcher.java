package com.letiyaha.android.currency.utilities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.letiyaha.android.currency.Currency;
import com.letiyaha.android.currency.CurrencyAdapter;

import java.net.URL;

/**
 * Created by Belle Lee on 7/18/2019.
 */

public class CurrencyFetcher implements LoaderManager.LoaderCallbacks<Currency> {

    private Context mContext;
    private CurrencyAdapter mCurrencyAdapter;
    private FetchResultDisplay mFetchResultDisplay;

    public CurrencyFetcher(Context context, CurrencyAdapter currencyAdapter, FetchResultDisplay fetchResultDisplay) {
        mContext = context;
        mCurrencyAdapter = currencyAdapter;
        mFetchResultDisplay = fetchResultDisplay;
    }

    public interface FetchResultDisplay {
        void showErrorMsg();
        void showFetchResult();
    }

    @Override
    public Loader<Currency> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Currency>(mContext) {

            Currency mLatestCurrency;

            @Override
            protected void onStartLoading() {
                if (mLatestCurrency != null) {
                    deliverResult(mLatestCurrency);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Currency loadInBackground() {
                URL currencyRequestUrl = NetworkUtils.buildLatestUrl();
                String jsonCurrencyResponse = NetworkUtils.getResponseFromHttpUrl(currencyRequestUrl);
                Currency latestCurrency = CurrencyJsonUtils.parseCurrencyJson(jsonCurrencyResponse);
                return latestCurrency;
            }

            @Override
            public void deliverResult(Currency currencyData) {
                mLatestCurrency = currencyData;
                super.deliverResult(currencyData);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Currency> loader, Currency currencyData) {
        if (currencyData == null) {
            mFetchResultDisplay.showErrorMsg();
        } else {
            mCurrencyAdapter.setCurrencyData(currencyData);
            mFetchResultDisplay.showFetchResult();
        }
    }

    @Override
    public void onLoaderReset(Loader<Currency> loader) {

    }
}
