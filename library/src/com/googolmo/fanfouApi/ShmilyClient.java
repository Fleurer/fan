package com.googolmo.fanfouApi;


import com.googolmo.fanfouApi.utils.NLog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ShmilyClient extends AsyncHttpClient{
    private static final String TAG = ShmilyClient.class.getName();


    public static final String GET = "GET";
    public static final String POST = "POST";

    private String base_url = "http://api.fanfou.com/";

    public ShmilyClient(String base_url) {
        init();
        this.base_url = base_url;

    }

    private void init() {
        setUserAgent("shmily.googolmo.fanfou.api/1.0");

    }

    @Override
    public void post(String url, AsyncHttpResponseHandler responseHandler) {
        NLog.d(TAG, "post " + url);
        post(url, null, responseHandler);
    }

    @Override
    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        super.post(url, params, responseHandler);
    }

    @Override
    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        NLog.d(TAG, "get " + url);
        super.get(url, params, responseHandler);
    }

    @Override
    public void get(String url, AsyncHttpResponseHandler responseHandler) {
        get(url, null, responseHandler);
    }

    public String getAbsoluteUrl(String relativeUrl) {
        return base_url + relativeUrl;
    }

    public void setOAuth(String sign) {
        addHeader("Authorization", sign);
    }
}