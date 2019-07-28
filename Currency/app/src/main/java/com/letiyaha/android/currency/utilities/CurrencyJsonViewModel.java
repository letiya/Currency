package com.letiyaha.android.currency.utilities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.letiyaha.android.currency.Currency;

/**
 * Created by Belle Lee on 7/27/2019.
 */

public class CurrencyJsonViewModel extends ViewModel {

    private CurrencyJsonLiveData data;

    public CurrencyJsonViewModel() {
        data = new CurrencyJsonLiveData();
    }

    public LiveData<Currency> getData() {
        return data;
    }

}
