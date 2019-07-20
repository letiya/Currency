package com.letiyaha.android.currency;

import java.util.HashMap;

/**
 * Created by Belle Lee on 7/18/2019.
 */

public class Currency {

    private String mTimestamp;
    private String mBase;
    private String mDate;
    private HashMap<String, String> mRates;

    public Currency() {

    }

    public void setTimestamp(String timestamp) {
        mTimestamp = timestamp;
    }

    public void setBaseCurrency(String baseCurrency) {
        mBase = baseCurrency;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public void setRates(HashMap<String, String> rates) {
        mRates = rates;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public String getBaseCurrency() {
        return mBase;
    }

    public String getDate() {
        return mDate;
    }

    public HashMap<String, String> getRates() {
        return mRates;
    }

}
