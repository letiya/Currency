package com.letiyaha.android.currency;

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
import android.widget.Toast;

import com.letiyaha.android.currency.database.AppDatabase;
import com.letiyaha.android.currency.database.CurrencyEntry;
import com.letiyaha.android.currency.utilities.CurrencyJsonViewModel;
import com.letiyaha.android.currency.utilities.CurrencyJsonViewModelFactory;
import com.letiyaha.android.currency.utilities.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CurrencyAdapter.ListItemClickListener {

    private RecyclerView mCurrencyList;
    private TextView mErrorMessageDisplay;
    private CurrencyAdapter mCurrencyAdapter;
    private AppDatabase mDb;

    private static final String CLICKED_CURRENCY = "clickedCurrency";
    private static final int NUM_OF_HISTORY_DATA = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCurrencyList = (RecyclerView) findViewById(R.id.rv_currency);
        mErrorMessageDisplay = findViewById(R.id.tv_error_msg);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mCurrencyList.setLayoutManager(layoutManager);

        mCurrencyList.setHasFixedSize(true);

        mCurrencyAdapter = new CurrencyAdapter(this, this);
        mCurrencyList.setAdapter(mCurrencyAdapter);

        mDb = AppDatabase.getInstance(getApplicationContext());

    }

    private void setupViewModel(final Date date) {
        CurrencyJsonViewModel viewModel = ViewModelProviders.of(this, new CurrencyJsonViewModelFactory(date)).get(CurrencyJsonViewModel.class);
        viewModel.getData().observe(this, new Observer<Currency>() {
            @Override
            public void onChanged(Currency currencyData) {
                if (currencyData == null) {
                    showErrorMsg("No reply from the currency source database!");
                } else {
                    if (!hasDataInDb(date)) {
                        updateLocalDatabase(currencyData);
                    }
                    if (date.compareTo(Util.getToday()) == 0) {
                        mCurrencyAdapter.setFavoriteCurrencies();
                        showFetchResult();
                    }
                }
            }
        });
    }

    private boolean hasDataInDb(Date date) {
        List<CurrencyEntry> currencyEntries = mDb.currencyDao().loadCurrenciesByDate(date);
        if (currencyEntries != null && currencyEntries.size() > 0) {
            return true;
        }
        return false;
    }

    private void updateLocalDatabase(Currency currencyData) {
        HashMap<String, String> rates = currencyData.getRates();
        for (String currency : rates.keySet()) {
            String rate = rates.get(currency);
            boolean isBase = Double.parseDouble(rate) == 1;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = sdf.parse(currencyData.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            CurrencyEntry currencyEntry = new CurrencyEntry(date, currencyData.getTimestamp(), currency, String.valueOf(isBase), rate, String.valueOf(isBase));
            mDb.currencyDao().insertCurrency(currencyEntry);
        }
    }

    @Override
    public void onListItemClick(String clickedCurrency) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intent2StartDetailActivity = new Intent(context, destinationClass);
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
        if (!hasDataInDb(Util.getToday())) {
            Toast.makeText(this, "Unable to add currency now. \nPlease check internet connection!", Toast.LENGTH_LONG).show();
            return super.onOptionsItemSelected(item);
        }
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
    protected void onStart() {
        super.onStart();
        if (isOnline()) {
            if (!hasDataInDb(Util.getToday())) {
                setupViewModel(Util.getToday()); // Fetch data from the network.
            } else {
                mCurrencyAdapter.setFavoriteCurrencies(); // Refresh
            }
            loadHistoryDataFromDb(NUM_OF_HISTORY_DATA);
        } else {
            showErrorMsg("Check internet connection!");
        }
    }

    private void loadHistoryDataFromDb(int days) {
        List<Date> dates = mDb.currencyDao().loadDataAfter(Util.getNDaysAgo(days));
        HashMap<Date, Boolean> map = new HashMap<Date, Boolean>();
        for (int i = 0; i < dates.size(); i++) {
            map.put(dates.get(i), true);
        }
        for (int i = days; i > 0; i--) {
            Date date = Util.getNDaysAgo(i);
            if (!map.containsKey(date)) {
                setupViewModel(date);
            }
        }
    }

}
