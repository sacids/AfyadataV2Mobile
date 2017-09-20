package org.sacids.afyadataV2.android.web;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.sacids.afyadataV2.android.app.AppConfig;
import org.sacids.afyadataV2.android.preferences.PreferenceKeys;

/**
 * Created by administrator on 12/09/2017.
 */

public class RestClient {
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {
        //client.addHeader(Preferences.API_KEY_NAME, Preferences.API_KEY);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        //client.addHeader(Preferences.API_KEY_NAME, Preferences.API_KEY);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        Log.d("Request url ", AppConfig.BASE_URL + relativeUrl);
        return AppConfig.BASE_URL + relativeUrl;
    }
}
