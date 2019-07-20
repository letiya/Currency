package com.letiyaha.android.currency.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Belle Lee on 7/18/2019.
 */

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String CURRENCY_BASE_URL = "http://data.fixer.io/api";
    private static final String URL_LATEST = "latest";
    private static final String API_KEY_PARAM = "access_key";

    public static URL buildLatestUrl() {
        Uri uri = Uri.parse(CURRENCY_BASE_URL)
                .buildUpon()
                .appendPath(URL_LATEST)
                .appendQueryParameter(API_KEY_PARAM, CurrencyAPI.API_KEY)
                .build();
        if (uri == null) {
            return null;
        }
        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "Invalid uri: " + uri);
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     */
    public static String getResponseFromHttpUrl(URL url) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A"); // Regular expression - Beginning of string.

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                Log.e(TAG, "No result from HTTP!");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "No internet connection!");
            return null;
        }  finally {
            urlConnection.disconnect();
        }
    }
}
