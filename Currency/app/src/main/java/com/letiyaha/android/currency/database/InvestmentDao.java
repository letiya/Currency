package com.letiyaha.android.currency.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

/**
 * Created by Belle Lee on 8/12/2019.
 */

@Dao
public interface InvestmentDao {

    @Query("SELECT * FROM investment WHERE currency = :currency")
    InvestmentEntry loadInvestmentDetail(String currency);

    @Insert
    void insertInvestment(InvestmentEntry investmentEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateInvestment(InvestmentEntry investmentEntry);

    @Delete
    void deleteInvestment(InvestmentEntry investmentEntry);

    @Query("UPDATE investment SET balance = :balance, cost = :cost WHERE currency = :currency")
    void updateInvestmentDetail(float balance, float cost, String currency);

}
