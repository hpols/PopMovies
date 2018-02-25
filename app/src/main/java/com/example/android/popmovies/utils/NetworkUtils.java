package com.example.android.popmovies.utils;

import android.content.ContentUris;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.example.android.popmovies.BuildConfig;
import com.example.android.popmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.StringJoiner;

/**
 * {@link NetworkUtils} carries out and supports any internet calls.
 */
public class NetworkUtils {

    private static final String API = "api_key";
    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String POPULAR_SEGMENT = "popular";
    private static final String RATED_SEGMENT = "top_rated";
    private static final String REVIEW = "reviews";
    private static final String TRAILER = "videos";

    //What kind of url is needed for the json call?
    public static final int SORTED_URL = 0;
    public static final int REVIEW_URL = 1;
    public static final int TRAILER_URL = 2;

    /**
     * Build the needed url to make the desired server call.
     *
     * @param ctxt The context within with the server call is made.
     * @return the built url.
     */
    private static URL buildUrl(Context ctxt, int urlType, long mvId) {

        Uri buildUri = null;
        switch (urlType) {
            case SORTED_URL:
                String userSelection;


                //get the user's preference of how to sort the grid.
                if (MovieUtils.isPopularSort(ctxt)) {
                    userSelection = POPULAR_SEGMENT;
                } else {
                    userSelection = RATED_SEGMENT;
                }

                //build the url.
                buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendEncodedPath(userSelection)
                        .appendQueryParameter(API, BuildConfig.API_KEY)
                        .build();
                break;
            case REVIEW_URL:
                buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(String.valueOf(mvId))
                        .appendPath(REVIEW)
                        .appendQueryParameter(API, BuildConfig.API_KEY)
                        .build();
                break;
            case TRAILER_URL:
                buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(String.valueOf(mvId))
                        .appendPath(TRAILER)
                        .appendQueryParameter(API, BuildConfig.API_KEY)
                        .build();
                break;
        }
        try {

            URL url = new URL(buildUri.toString());
            Log.v("NetworkUtils", "url: " + url);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String getResponseFromHttpUrl(Context ctxt, int urlType, long mvId) throws IOException {
        HttpURLConnection urlConnect = (HttpURLConnection) buildUrl(ctxt, urlType, mvId).openConnection();
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

    //If there is no internet display a message prompting the user to check it.
    //based on: https://stackoverflow.com/a/4009133 as pointed out in the project guidelines
    public static boolean checkNetworkToLoad(Context ctxt) {
        ConnectivityManager connectMan = (ConnectivityManager)
                ctxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectMan != null;
        NetworkInfo netInfo = connectMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}