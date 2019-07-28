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

import com.letiyaha.android.currency.utilities.CurrencyJsonViewModel;

public class MainActivity extends AppCompatActivity implements CurrencyAdapter.ListItemClickListener {

    private RecyclerView mCurrencyList;
    private TextView mErrorMessageDisplay;

    private CurrencyAdapter mCurrencyAdapter;

    private static final String CURRENCY_DATA = "CURRENCY_DATA";

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

        if (isOnline()) {
            setupViewModel();
        } else {
            showErrorMsg("Check internet connection!");
        }
    }

    private void setupViewModel() {
        CurrencyJsonViewModel viewModel = ViewModelProviders.of(this).get(CurrencyJsonViewModel.class);
        viewModel.getData().observe(this, new Observer<Currency>() {
            @Override
            public void onChanged(Currency currencyData) {
                if (currencyData == null) {
                    showErrorMsg("No reply from the currency source database!");
                } else {
                    mCurrencyAdapter.setCurrencyData(currencyData);
                    showFetchResult();
                }
            }
        });
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intent2StartDetailActivity = new Intent(context, destinationClass);
        startActivity(intent2StartDetailActivity);
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public void showErrorMsg(String errorMsg) {
        /* First, hide the currently visible data */
        mCurrencyList.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setText(errorMsg);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public void showFetchResult() {
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
        if (mCurrencyAdapter.getCurrencyData() == null){
            Toast.makeText(this, "Unable to add currency now. \nPlease check internet connection!", Toast.LENGTH_LONG).show();
            return super.onOptionsItemSelected(item);
        }

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Context context = this;
            Class destinationClass = SettingsActivity.class;
            Intent intent2StartSettomgsActivity = new Intent(context, destinationClass);
            intent2StartSettomgsActivity.putExtra(CURRENCY_DATA, mCurrencyAdapter.getCurrencyData());
            startActivity(intent2StartSettomgsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
