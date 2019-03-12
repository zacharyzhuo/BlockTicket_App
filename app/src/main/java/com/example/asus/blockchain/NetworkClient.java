package com.example.asus.blockchain;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class NetworkClient {
    private static final String BASE_URL = "http://120.125.82.2:3000";

    private static Context mContext;

    private static AsyncHttpClient client;
    public static PersistentCookieStore myCookieStore;

    NetworkClient(final Context context) {
        if (client == null && context != null) {
            client = new AsyncHttpClient();
            client.addHeader("mobile", "yes");
            mContext = context;
            myCookieStore = new PersistentCookieStore(context);
            client.setCookieStore(myCookieStore);
        }
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
