package com.letiyaha.android.currency.utilities;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;

import java.util.Date;
import java.util.List;

/**
 * Created by Belle Lee on 8/2/2019.
 */

public class CurrencyJsonViewModelFactory implements ViewModelProvider.Factory {

    private Context mContext;
    private List<Date> mDates;

    public CurrencyJsonViewModelFactory(Context context, List<Date> dates) {
        mContext = context;
        mDates = dates;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new CurrencyJsonViewModel(mContext, mDates);
    }
}
