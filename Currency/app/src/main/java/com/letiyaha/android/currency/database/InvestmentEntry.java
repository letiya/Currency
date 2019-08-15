package com.letiyaha.android.currency.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Belle Lee on 8/12/2019.
 */

@Entity(tableName = "investment")
public class InvestmentEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String currency;
    private float balance;
    private float cost;

    @Ignore
    public InvestmentEntry(String currency, float balance, float cost) {
        this.currency = currency;
        this.balance = balance;
        this.cost = cost;
    }

    public InvestmentEntry(int id, String currency, float balance, float cost) {
        this.id = id;
        this.currency = currency;
        this.balance = balance;
        this.cost = cost;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public float getBalance() {
        return balance;
    }
    public void setBalance(float balance) {
        this.balance = balance;
    }
    public float getCost() {
        return cost;
    }
    public void setCost(float cost) {
        this.cost = cost;
    }

}
