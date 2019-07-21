package com.letiyaha.android.currency;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.letiyaha.android.currency.utilities.CurrencyFetcher;

public class MainActivity extends AppCompatActivity implements CurrencyAdapter.ListItemClickListener, CurrencyFetcher.FetchResultDisplay {

    private RecyclerView mCurrencyList;
    private TextView mErrorMessageDisplay;

    private CurrencyAdapter mCurrencyAdapter;

    private static final int CURRENCY_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCurrencyList = (RecyclerView) findViewById(R.id.rv_currency);
        mErrorMessageDisplay = findViewById(R.id.tv_error_msg);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mCurrencyList.setLayoutManager(layoutManager);

        mCurrencyList.setHasFixedSize(true);

        mCurrencyAdapter = new CurrencyAdapter(this);
        mCurrencyList.setAdapter(mCurrencyAdapter);

        if (isOnline()) {
            CurrencyFetcher currencyFetcher = new CurrencyFetcher(this, mCurrencyAdapter, this);
            getSupportLoaderManager().initLoader(CURRENCY_LOADER_ID, null, currencyFetcher);
        } else {
            showErrorMsg();
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intent2StartDestinationClass = new Intent(context, destinationClass);
        startActivity(intent2StartDestinationClass);
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void showErrorMsg() {
        /* First, hide the currently visible data */
        mCurrencyList.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void showFetchResult() {
        mCurrencyList.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }
}
