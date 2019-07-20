package com.letiyaha.android.currency.utilities;

import com.letiyaha.android.currency.Currency;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Belle Lee on 7/18/2019.
 */

public class CurrencyJsonUtils {

    private static final String JSON_SUCCESS = "success";
    private static final String JSON_TIMESTAMP = "timestamp";
    private static final String JSON_BASE = "base";
    private static final String JSON_DATE = "date";
    private static final String JSON_RATES = "rates";

    public static Currency parseCurrencyJson(String json) {
        if (json == null) {
            return null;
        }
        Currency currency = new Currency();
        try {
            JSONObject jsonObject = new JSONObject(json);
            String success = jsonObject.getString(JSON_SUCCESS);
            String timestamp = jsonObject.getString(JSON_TIMESTAMP);
            String base = jsonObject.getString(JSON_BASE);
            String date = jsonObject.getString(JSON_DATE);

            HashMap<String, String> ratesHashMap = new HashMap<String, String>();
            JSONObject rates = jsonObject.getJSONObject(JSON_RATES);
            Iterator<String> keys = rates.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                ratesHashMap.put(key, rates.getString(key));
            }
            if (success.equalsIgnoreCase("true")) {
                currency.setTimestamp(timestamp);
                currency.setBaseCurrency(base);
                currency.setDate(date);
                currency.setRates(ratesHashMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currency;
    }
}
