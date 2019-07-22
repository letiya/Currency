package com.letiyaha.android.currency;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Belle Lee on 7/18/2019.
 */

public class Currency implements Parcelable{

    private String mTimestamp;
    private String mBase;
    private String mDate;
    private HashMap<String, String> mRates;
    private ArrayList<String> mCurrencies;

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

    public void setCurrencies(ArrayList<String> currencies) {mCurrencies = currencies;}

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

    public ArrayList<String> getCurrencies() {return mCurrencies; }

    // Parcelling part
    protected Currency(Parcel in) {
        mTimestamp = in.readString();
        mBase = in.readString();
        mDate = in.readString();
        mRates = (HashMap<String, String>) in.readSerializable();
        mCurrencies = (ArrayList<String>) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTimestamp);
        dest.writeString(mBase);
        dest.writeString(mDate);
        dest.writeSerializable(mRates);
        dest.writeSerializable(mCurrencies);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

}
