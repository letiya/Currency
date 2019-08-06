package com.letiyaha.android.currency.utilities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.letiyaha.android.currency.Currency;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Belle Lee on 7/27/2019.
 */

public class CurrencyJsonViewModel extends ViewModel {

    private HashMap<Date, CurrencyJsonLiveData> dataList;

    public CurrencyJsonViewModel(Context context, List<Date> dates) {
        dataList = new HashMap<Date, CurrencyJsonLiveData>();
        for (int i = 0; i < dates.size(); i++) {
            Date date = dates.get(i);
            if (!Util.hasDataInDb(context, date)) {
                dataList.put(date, new CurrencyJsonLiveData(date));
            }
        }
    }

    public LiveData<Currency> getData(Date date) {
        return dataList.get(date);
    }

}
