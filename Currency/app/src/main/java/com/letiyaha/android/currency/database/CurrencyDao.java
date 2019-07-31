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

    @Query("SELECT * FROM currency WHERE date = :date ORDER BY currency")
    List<CurrencyEntry> loadCurrenciesByDate(Date date);

    @Insert
    void insertCurrency(CurrencyEntry currencyEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCurrency(CurrencyEntry currencyEntry);

    @Delete
    void deleteCurrency(CurrencyEntry currencyEntry);

    @Query("SELECT currency FROM currency WHERE date = :date AND isBase = 'true'")
    String loadBaseCurrency(Date date);

    @Query("SELECT rate FROM currency WHERE date = :date AND currency = :currency")
    String loadCurrencyRate(Date date, String currency);

    @Query("SELECT * FROM currency WHERE date = :date AND isFavorite = 'true' ORDER BY isBase desc")
    List<CurrencyEntry> loadFavoriteCurrencies(Date date);

    @Query("UPDATE currency SET isFavorite = :isFavorite WHERE currency = :currency")
    void updateFavoriteCurrency(String isFavorite, String currency);

    @Query("UPDATE currency SET isFavorite = 'false'")
    void resetFavoriteSetting();

}
