package com.letiyaha.android.currency.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Belle Lee on 7/27/2019.
 */

public class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
