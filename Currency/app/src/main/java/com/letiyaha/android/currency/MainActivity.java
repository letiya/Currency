package com.letiyaha.android.currency;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.letiyaha.android.currency.database.AppDatabase;
import com.letiyaha.android.currency.database.CurrencyEntry;
import com.letiyaha.android.currency.utilities.AppExecutors;
import com.letiyaha.android.currency.utilities.CurrencyJsonViewModel;
import com.letiyaha.android.currency.utilities.CurrencyJsonViewModelFactory;
import com.letiyaha.android.currency.utilities.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CurrencyAdapter.ListItemClickListener {

    private Context mContext;
    private RecyclerView mCurrencyList;
    private TextView mErrorMessageDisplay;
    private CurrencyAdapter mCurrencyAdapter;
    private AppDatabase mDb;
    private List<Date> mDates;
    private String mMainCurrency;

    private static final String CLICKED_CURRENCY = "clickedCurrency";
    private static final String MAIN_CURRENCY = "mainCurrency";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mCurrencyList = (RecyclerView) findViewById(R.id.rv_currency);
        mErrorMessageDisplay = findViewById(R.id.tv_error_msg);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mCurrencyList.setLayoutManager(layoutManager);

        mCurrencyList.setHasFixedSize(true);

        mCurrencyAdapter = new CurrencyAdapter(this, this, this);
        mCurrencyList.setAdapter(mCurrencyAdapter);

        mDb = AppDatabase.getInstance(getApplicationContext());

    }

    @Override
    public void onListItemClick(String clickedCurrency) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intent2StartDetailActivity = new Intent(context, destinationClass);
        intent2StartDetailActivity.putExtra(MAIN_CURRENCY, mMainCurrency);
        intent2StartDetailActivity.putExtra(CLICKED_CURRENCY, clickedCurrency);
        startActivity(intent2StartDetailActivity);
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void showErrorMsg(String errorMsg) {
        /* First, hide the currently visible data */
        mCurrencyList.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setText(errorMsg);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showFetchResult() {
        mCurrencyList.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.menu_main, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Context context = this;
            Class destinationClass = SettingsActivity.class;
            Intent intent2StartSettomgsActivity = new Intent(context, destinationClass);
            startActivity(intent2StartSettomgsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isOnline()) {
            getDbMissingHistoryDates(Util.NUM_OF_HISTORY_DATA);
            checkDataInDb(Util.getToday());
        } else {
            showErrorMsg("Check internet connection!");
        }
    }

    private void getDbMissingHistoryDates(final int days) {
        AppExecutors.getInstance().DiskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDates = mDb.currencyDao().loadDataAfter(Util.getNDaysAgo(days));
                HashMap<Date, Boolean> map = new HashMap<Date, Boolean>();
                for (int i = 0; i < mDates.size(); i++) {
                    map.put(mDates.get(i), true);
                }
                mDates = new ArrayList<Date>();
                for (int i = days; i >= 0; i--) {
                    Date date = Util.getNDaysAgo(i);
                    if (!map.containsKey(date)) {
                        mDates.add(date);
                    }
                }
                setupViewModel();
            }
        });
    }

    private void checkDataInDb(final Date date) {
        AppExecutors.getInstance().DiskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<CurrencyEntry> currencyEntries = mDb.currencyDao().loadCurrenciesByDate(date);
                if (currencyEntries != null && currencyEntries.size() > 0) { // Has data
                    mMainCurrency = getMainCurrency(currencyEntries);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCurrencyAdapter.setFavoriteCurrencies(); // Refresh
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showFetchResult();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private String getMainCurrency(List<CurrencyEntry> currencyEntries) {
        for (CurrencyEntry currencyEntry : currencyEntries) {
            if (Boolean.valueOf(currencyEntry.getIsBase())) {
                return currencyEntry.getCurrency();
            }
        }
        return "";
    }

    private void setupViewModel() {
        CurrencyJsonViewModel viewModel = ViewModelProviders.of(this, new CurrencyJsonViewModelFactory(mContext, mDates)).get(CurrencyJsonViewModel.class);
        for (int i = 0; i < mDates.size(); i++) {
            final Date date = mDates.get(i);
            setViewModelObservers(date, viewModel, this);
        }
    }

    private void setViewModelObservers(final Date date, final CurrencyJsonViewModel viewModel, final LifecycleOwner lifecycleOwner) {
        AppExecutors.getInstance().DiskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<CurrencyEntry> currencyEntries = mDb.currencyDao().loadCurrenciesByDate(date);
                if (!(currencyEntries != null && currencyEntries.size() > 0)) { // No data
                    viewModel.getData(date).observe(lifecycleOwner, new Observer<Currency>() {
                        @Override
                        public void onChanged(Currency currencyData) {
                            if (currencyData == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showErrorMsg("No reply from the currency source database!");
                                    }
                                });
                            } else {
                                updateLocalDatabase(currencyData, date);
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateLocalDatabase(Currency currencyData, final Date date) {
        HashMap<String, String> rates = currencyData.getRates();
        for (String currency : rates.keySet()) {
            String rate = rates.get(currency);
            boolean isBase = Double.parseDouble(rate) == 1;

            final CurrencyEntry currencyEntry = new CurrencyEntry(date, currencyData.getTimestamp(), currency, String.valueOf(isBase), rate, String.valueOf(isBase));
            AppExecutors.getInstance().DiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.currencyDao().insertCurrency(currencyEntry);
                    if (date.compareTo(Util.getToday()) == 0) {
                        mCurrencyAdapter.setFavoriteCurrencies(); // Refresh
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showFetchResult();
                            }
                        });
                    }
                }
            });
        }
    }
}
