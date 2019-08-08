package com.letiyaha.android.currency.utilities;

import android.content.Context;

import com.letiyaha.android.currency.database.AppDatabase;
import com.letiyaha.android.currency.database.CurrencyEntry;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Belle Lee on 7/30/2019.
 */

public class Util {

    public static final int NUM_OF_HISTORY_DATA = 30;

    public static Date getToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getNDaysAgo(int n) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -n);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static boolean hasDataInDb(Context context, Date date) {
        AppDatabase mDb = AppDatabase.getInstance(context);
        List<CurrencyEntry> currencyEntries = mDb.currencyDao().loadCurrenciesByDate(date);
        if (currencyEntries != null && currencyEntries.size() > 0) {
            return true;
        }
        return false;
    }
}
