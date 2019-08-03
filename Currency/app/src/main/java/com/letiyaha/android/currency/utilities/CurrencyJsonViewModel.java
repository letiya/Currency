package com.letiyaha.android.currency.utilities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.letiyaha.android.currency.Currency;

import java.util.Date;

/**
 * Created by Belle Lee on 7/27/2019.
 */

public class CurrencyJsonViewModel extends ViewModel {

    private CurrencyJsonLiveData data;

    public CurrencyJsonViewModel(Date date) {
        data = new CurrencyJsonLiveData(date);
    }

    public LiveData<Currency> getData() {
        return data;
    }

}
