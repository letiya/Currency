package com.letiyaha.android.currency.utilities;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Belle Lee on 7/30/2019.
 */

public class Util {

    public static Date getToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
