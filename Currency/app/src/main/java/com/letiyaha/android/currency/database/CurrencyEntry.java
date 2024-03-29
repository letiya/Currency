package com.letiyaha.android.currency.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by Belle Lee on 7/26/2019.
 */

@Entity(tableName = "currency")
public class CurrencyEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private Date date;
    private String timestamp;
    private String currency;
    private String isBase;
    private String rate;
    private String isFavorite;

    @Ignore
    public CurrencyEntry(Date date, String timestamp, String currency, String isBase, String rate, String isFavorite) {
        this.date = date;
        this.timestamp = timestamp;
        this.currency = currency;
        this.isBase = isBase;
        this.rate = rate;
        this.isFavorite = isFavorite;
    }

    public CurrencyEntry(int id, Date date, String timestamp, String currency, String isBase, String rate, String isFavorite) {
        this.id = id;
        this.date = date;
        this.timestamp = timestamp;
        this.currency = currency;
        this.isBase = isBase;
        this.rate = rate;
        this.isFavorite = isFavorite;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getIsBase() {
        return isBase;
    }
    public void setIsBase(String isBase) {
        this.isBase = isBase;
    }
    public String getRate() {
        return rate;
    }
    public void setRate(String rate) {
        this.rate = rate;
    }
    public String getIsFavorite() {
        return isFavorite;
    }
    public void setIsFavorite(String isFavorite) {
        this.isFavorite = isFavorite;
    }

}
