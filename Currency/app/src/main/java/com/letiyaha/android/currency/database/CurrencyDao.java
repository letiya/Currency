package com.letiyaha.android.currency.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by Belle Lee on 7/26/2019.
 */

@Dao
public interface CurrencyDao {

    @Query("SELECT * FROM currency where date = :date")
    List<CurrencyEntry> loadAllCurrenciesByDate(Date date);

    @Insert
    void insertCurrency(CurrencyEntry currencyEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCurrency(CurrencyEntry currencyEntry);

    @Delete
    void deleteCurrency(CurrencyEntry currencyEntry);
}
