package com.letiyaha.android.currency;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.letiyaha.android.currency.charts.DrawLineChart;
import com.letiyaha.android.currency.database.AppDatabase;
import com.letiyaha.android.currency.database.CurrencyEntry;
import com.letiyaha.android.currency.utilities.AppExecutors;
import com.letiyaha.android.currency.utilities.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Belle Lee on 7/20/2019.
 */

public class DetailActivity extends AppCompatActivity {

    private AppDatabase mDb;

    private LineChart mLineChart;

    private static final String CLICKED_CURRENCY = "clickedCurrency";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDb = AppDatabase.getInstance(getApplicationContext());
        mLineChart = (LineChart) findViewById(R.id.lc_month);

        Intent intentThatStartedThisActivity = getIntent();
        final String clickedCurrency = intentThatStartedThisActivity.getStringExtra(CLICKED_CURRENCY);

        final List<String> xAxisValues = new ArrayList<>();
        final List<Float> dateValueList = new ArrayList<>();

        AppExecutors.getInstance().DiskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<CurrencyEntry> data = mDb.currencyDao().loadAllAfter(clickedCurrency, Util.getNDaysAgo(Util.NUM_OF_HISTORY_DATA));

                for (int i = 0; i < data.size(); i++) {
                    CurrencyEntry currencyEntry = data.get(i);
                    Date date = currencyEntry.getDate();
                    SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
                    String dateString = sdf.format(date);
                    String rate = currencyEntry.getRate();

                    xAxisValues.add(dateString);
                    dateValueList.add(Float.parseFloat(rate));
                }

                DrawLineChart drawLineChart = new DrawLineChart(xAxisValues, mLineChart, dateValueList, clickedCurrency + " trend");

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Context context = this;
            Class destinationClass = DetailSettingsActivity.class;
            Intent intent2StartDetailSettomgsActivity = new Intent(context, destinationClass);
            startActivity(intent2StartDetailSettomgsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
