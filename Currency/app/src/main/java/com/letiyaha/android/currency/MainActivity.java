package com.letiyaha.android.currency;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.letiyaha.android.currency.utilities.CurrencyFetcher;

public class MainActivity extends AppCompatActivity implements CurrencyAdapter.ListItemClickListener {

    private RecyclerView mCurrencyList;
    private CurrencyAdapter mCurrencyAdapter;

    private static final int CURRENCY_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCurrencyList = (RecyclerView) findViewById(R.id.rv_currency);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mCurrencyList.setLayoutManager(layoutManager);

        mCurrencyList.setHasFixedSize(true);

        mCurrencyAdapter = new CurrencyAdapter(this);
        mCurrencyList.setAdapter(mCurrencyAdapter);

        CurrencyFetcher currencyFetcher = new CurrencyFetcher(this, mCurrencyAdapter);
        getSupportLoaderManager().initLoader(CURRENCY_LOADER_ID, null, currencyFetcher);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
    }
}
