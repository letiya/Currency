package com.letiyaha.android.currency.utilities;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import java.util.Date;

/**
 * Created by Belle Lee on 8/2/2019.
 */

public class CurrencyJsonViewModelFactory implements ViewModelProvider.Factory {

    private Date mDate;

    public CurrencyJsonViewModelFactory(Date date) {
        mDate = date;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new CurrencyJsonViewModel(mDate);
    }
}
