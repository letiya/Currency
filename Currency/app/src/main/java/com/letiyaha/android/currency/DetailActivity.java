package com.letiyaha.android.currency;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.letiyaha.android.currency.charts.DrawLineChart;
import com.letiyaha.android.currency.database.AppDatabase;
import com.letiyaha.android.currency.database.CurrencyEntry;
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
        String clickedCurrency = intentThatStartedThisActivity.getStringExtra(CLICKED_CURRENCY);

        List<String> xAxisValues = new ArrayList<>(); // 20180101
        List<Float> dateValueList = new ArrayList<>();

        List<CurrencyEntry> data = mDb.currencyDao().loadAllAfter(clickedCurrency, Util.getNDaysAgo(30));
        for (int i = 0; i < data.size(); i++) {
            CurrencyEntry currencyEntry = data.get(i);
            Date date = currencyEntry.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateString = sdf.format(date);
            String rate = currencyEntry.getRate();

            xAxisValues.add(dateString);
            dateValueList.add(Float.parseFloat(rate));
        }

        DrawLineChart drawLineChart = new DrawLineChart(xAxisValues, mLineChart, dateValueList, clickedCurrency + " trend");
    }

}
