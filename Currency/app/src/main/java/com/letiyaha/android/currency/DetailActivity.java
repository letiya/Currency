package com.letiyaha.android.currency;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.letiyaha.android.currency.charts.DrawLineChart;
import com.letiyaha.android.currency.database.AppDatabase;
import com.letiyaha.android.currency.database.CurrencyEntry;
import com.letiyaha.android.currency.database.InvestmentEntry;
import com.letiyaha.android.currency.utilities.AppExecutors;
import com.letiyaha.android.currency.utilities.Util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Belle Lee on 7/20/2019.
 */

public class DetailActivity extends AppCompatActivity {

    private AppDatabase mDb;
    private Context mContext;

    private LineChart mLineChart;
    private TextView mBalance;
    private TextView mBalanceUnit;
    private TextView mCost;
    private TextView mCostUnit;
    private TextView mSuggestedBuyAmount;
    private TextView mSuggestedBuyAmountUnit;

    private EditText mAmount;
    private Button mButtonBuy;
    private Button mButtonSell;

    private static final String CLICKED_CURRENCY = "clickedCurrency";
    private static final String MAIN_CURRENCY = "mainCurrency";
    private final double[] INVEST_RATIO = {0.06, 0.1, 0.2, 0.28, 0.36}; // Assume 5 currencies investment for now

    private float mRateToday;

    private String mClickedCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mContext = this;
        mDb = AppDatabase.getInstance(getApplicationContext());
        mLineChart = (LineChart) findViewById(R.id.lc_month);

        Intent intentThatStartedThisActivity = getIntent();
        mClickedCurrency = intentThatStartedThisActivity.getStringExtra(CLICKED_CURRENCY);
        String mainCurrency = intentThatStartedThisActivity.getStringExtra(MAIN_CURRENCY);

        final List<String> xAxisValues = new ArrayList<>();
        final List<Float> dateValueList = new ArrayList<>();

        AppExecutors.getInstance().DiskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<CurrencyEntry> data = mDb.currencyDao().loadAllAfter(mClickedCurrency, Util.getNDaysAgo(Util.NUM_OF_HISTORY_DATA));

                for (int i = 0; i < data.size(); i++) {
                    CurrencyEntry currencyEntry = data.get(i);
                    Date date = currencyEntry.getDate();
                    SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
                    String dateString = sdf.format(date);
                    String rate = currencyEntry.getRate();

                    xAxisValues.add(dateString);
                    dateValueList.add(Float.parseFloat(rate));
                }

                DrawLineChart drawLineChart = new DrawLineChart(xAxisValues, mLineChart, dateValueList, mClickedCurrency + " trend");

            }
        });

        mBalance = findViewById(R.id.tv_balance_amount);
        mBalanceUnit = findViewById(R.id.tv_balance_amount_unit);
        mBalanceUnit.setText("(" + mClickedCurrency + ")");

        mCost = findViewById(R.id.tv_avg_cost_amount);
        mCostUnit = findViewById(R.id.tv_avg_cost_amount_unit);
        mCostUnit.setText("(" + mainCurrency + ")");

        AppExecutors.getInstance().DiskIO().execute(new Runnable() {
            @Override
            public void run() {
                InvestmentEntry investmentEntry = mDb.investmentDao().loadInvestmentDetail(mClickedCurrency);
                float balance = 0;
                float cost = 0;
                if (investmentEntry != null) {
                    balance = investmentEntry.getBalance();
                    cost = investmentEntry.getCost();
                }
                mBalance.setText(balance + "");
                mCost.setText(cost + "");
            }
        });

        mAmount = findViewById(R.id.et_amount);

        mButtonBuy = findViewById(R.id.bt_buy);
        mButtonSell = findViewById(R.id.bt_sell);

        mButtonBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputString = mAmount.getText().toString();
                if (Util.isNumber(inputString)) {
                    makeTransaction(mClickedCurrency, Float.valueOf(inputString));
                } else {
                    Toast.makeText(mContext, "Please enter a valid number!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mButtonSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputString = mAmount.getText().toString();
                if (Util.isNumber(inputString)) {
                    makeTransaction(mClickedCurrency, (-1) * Float.valueOf(inputString));
                } else {
                    Toast.makeText(mContext, "Please enter a valid number!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSuggestedBuyAmount = findViewById(R.id.tv_suggest_amount);
        mSuggestedBuyAmountUnit = findViewById(R.id.tv_suggest_amount_unit);
        mSuggestedBuyAmountUnit.setText("(" + mainCurrency + ")");

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppExecutors.getInstance().DiskIO().execute(new Runnable() {
            @Override
            public void run() {
                String rateTodayString = mDb.currencyDao().loadCurrencyRate(Util.getToday(), mClickedCurrency);
                mRateToday = Float.valueOf(rateTodayString);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSuggestedInvAmount();
                    }
                });
            }
        });
    }

    private void makeTransaction(final String clickedCurrency, final float inputAmount) {
        AppExecutors.getInstance().DiskIO().execute(new Runnable() {
            @Override
            public void run() {
                InvestmentEntry investmentEntry = mDb.investmentDao().loadInvestmentDetail(clickedCurrency);
                float oldBalance = 0;
                float oldCost = 0;
                if (investmentEntry != null) {
                    oldBalance = investmentEntry.getBalance();
                    oldCost = investmentEntry.getCost();
                } else {
                    addCurrency2Investment(clickedCurrency);
                }

                float currentBalance = (oldBalance + (inputAmount / mRateToday)) > 0 ? (oldBalance + (inputAmount / mRateToday)) : 0;
                float currentCost = currentBalance > 0 ? ((oldBalance * oldCost + inputAmount) / currentBalance) : 0;

                updateTransactionHistory(currentBalance, currentCost, clickedCurrency);
            }
        });
    }

    private void addCurrency2Investment(final String clickedCurrency) {
        AppExecutors.getInstance().DiskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.investmentDao().insertInvestment(new InvestmentEntry(clickedCurrency, 0, 0));
            }
        });
    }

    private void updateTransactionHistory(final float currentBalance, final float currentCost, final String clickedCurrency) {
        AppExecutors.getInstance().DiskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.investmentDao().updateInvestmentDetail(currentBalance, currentCost, clickedCurrency);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBalance.setText(currentBalance + "");
                        mCost.setText(currentCost + "");
                    }
                });
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

    private void setSuggestedInvAmount() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String monInvKey = mContext.getString(R.string.pref_monthly_inv_key);
        String monInvDefault = mContext.getString(R.string.pref_monthly_inv_default);
        String monInvValue = sharedPreferences.getString(monInvKey, monInvDefault);

        String minKey = mContext.getString(R.string.pref_history_min_key);
        String minDefault = mContext.getString(R.string.pref_history_min_default);
        String minValue = sharedPreferences.getString(minKey, minDefault);

        String maxKey = mContext.getString(R.string.pref_history_max_key);
        String maxDefault = mContext.getString(R.string.pref_history_max_default);
        String maxValue = sharedPreferences.getString(maxKey, maxDefault);

        if (Util.isNumber(monInvValue) && Util.isNumber(minValue) && Util.isNumber(maxValue) && Float.valueOf(maxValue) != 0) {
            double monInv = Double.valueOf(monInvValue);
            double min = Double.valueOf(minValue);
            double max = Double.valueOf(maxValue);
            int interval = getInterval(min, max);

            if (interval <  INVEST_RATIO.length) {
                double amount = INVEST_RATIO[interval] * monInv;
                DecimalFormat df = new DecimalFormat("#0.0000");
                df.format(amount);
                mSuggestedBuyAmount.setText(amount + "");
            }
        } else {
            Toast.makeText(mContext, "Please udpate preference settings with valid numbers!", Toast.LENGTH_LONG);
        }
    }

    private int getInterval(double min, double max) {
        double intervalToday = (mRateToday / max - 1) * 100;
        double ratio = ((min / max - 1) * 100) / 5; // Ratio = (min/max - 1) * 100 / # of investment currencies
        return (int) (intervalToday / ratio);
    }

}
