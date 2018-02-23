package com.example.android.popmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.android.popmovies.BuildConfig;
import com.example.android.popmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * {@link NetworkUtils} carries out and supports any internet calls.
 */
public class NetworkUtils {

    //create a /values/api.xml resource and store your api as "movieDB_api_v3" string
    private static final String API = "api_key";
    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String POPULAR_SEGMENT = "popular";
    private static final String RATED_SEGMENT = "top_rated";

    /**
     * Build the needed url to make the desired server call.
     *
     * @param ctxt The context within with the server call is made.
     * @return the built url.
     */
    private static URL buildUrl(Context ctxt) {
        String userSelection;

        //get the user's preference of how to sort the grid.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctxt);
        String sortKey = ctxt.getString(R.string.sort_by_key);
        String sortDefault = ctxt.getString(R.string.popularity_sort_label_default);
        String sortPreference = sharedPref.getString(sortKey, sortDefault);
        if (sortDefault.equals(sortPreference)) {
            userSelection = POPULAR_SEGMENT;
        } else {
            userSelection = RATED_SEGMENT;
        }

        //build the url.
        Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendEncodedPath(userSelection)
                .appendQueryParameter(API, BuildConfig.API_KEY)
                .build();
        try {
            URL url = new URL(buildUri.toString());
            Log.v("NetworkUtils", "url: " + url);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponseFromHttpUrl(Context ctxt) throws IOException {
        HttpURLConnection urlConnect = (HttpURLConnection) buildUrl(ctxt).openConnection();
        try {
            InputStream input = urlConnect.getInputStream();

            Scanner scan = new Scanner(input);
            scan.useDelimiter("\\A");

            boolean hasInput = scan.hasNext();
            String response = null;
            if (hasInput) {
                response = scan.next();
            }
            scan.close();
            return response;
        } finally {
            urlConnect.disconnect();
        }
    }
}