/*
 * Copyright (c) 2013. By @GoogolMo
 */

package com.googolmo.fanfou.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.googolmo.fanfou.api.http.Method;
import com.googolmo.fanfou.api.http.MultipartParameter;
import com.googolmo.fanfou.api.http.OAuth;
import com.googolmo.fanfou.api.http.RequestParam;
import com.googolmo.fanfou.api.http.Response;
import com.googolmo.fanfou.api.http.Token;
import com.googolmo.fanfou.api.http.URLClient;
import com.googolmo.fanfou.api.model.Status;
import com.googolmo.fanfou.api.model.User;
import com.googolmo.fanfou.utils.JsonUtils;
import com.googolmo.fanfou.utils.NLog;
import org.apache.http.NameValuePair;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * User: GoogolMo
 * Date: 13-3-6
 * Time: 下午8:44
 */
public class Api {
    private static final String TAG = Api.class.getName();

    private static final String AUTHORIZE_URL = "http://fanfou.com/oauth/authorize";
    private static final String REQUEST_TOKEN_URL = "http://fanfou.com/oauth/request_token";
    private static final String ACCESS_TOKEN_URL = "http://fanfou.com/oauth/access_token";
    private static final String BASE_URL = "http://api.fanfou.com";

    private String mConsumerKey;
    private String mConsumerSecret;

    private OAuth mOAuth;
    private Token oAuthToken;
    private URLClient mClient;
    private Gson mGson;

    public Api(String consumerKey, String consumerSecret, Gson gson) {
        this.mConsumerKey = consumerKey;
        this.mConsumerSecret = consumerSecret;
        this.mGson = gson;
        init();
    }

    public void setGson(Gson gson) {
        this.mGson = gson;
    }

    private void init() {
        this.mClient = new URLClient();
        this.mOAuth = new OAuth(this.mConsumerKey, this.mConsumerSecret);
        if (this.mGson == null) {
            this.mGson = JsonUtils.getGson();
        }


    }

    public void setOAuthToken(Token token) {
        this.oAuthToken = token;
    }

    public Token getOAuthToken() {
        return this.oAuthToken;
    }


    /**
     * get AuthorizeUrl
     * @param oauth_callback
     * @return
     */
    public String getAuthorizeUrl(String oauth_callback) {
        return String.format("%1$s?oauth_token=%2$s&oauth_callback=%3$s", AUTHORIZE_URL, this.oAuthToken.getToken(), oauth_callback);
    }

    /**
     * get AuthorizeUrl
     * @param oauth_callback
     * @return
     */
    public String getAuthorizeUrl(Token token, String oauth_callback) {
        return String.format("%1$s?oauth_token=%2$s&oauth_callback=%3$s", AUTHORIZE_URL, token.getToken(), oauth_callback);
    }

    public String getAuthorizeUrl(String token, String oauth_callback) {
        return String.format("%1$s?oauth_token=%2$s&oauth_callback=%3$s", AUTHORIZE_URL, token, oauth_callback);
    }

    /**
     * 获得 RequestToken
     * @return
     * @throws FanfouException
     */
    public Token getRequestToken() throws FanfouException {
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        String sign = mOAuth.getOAuthSignature(Method.GET.name(), REQUEST_TOKEN_URL, null, null);
        headers.add(new RequestParam("Authorization", sign));
        this.oAuthToken = new Token(get(REQUEST_TOKEN_URL, null, headers));
        return this.oAuthToken;
    }

    /**
     * 获得AccessToken
     * @param requestToken
     * @return
     * @throws FanfouException
     */
    public Token getAccessToken(Token requestToken) throws FanfouException {
        this.oAuthToken = requestToken;
        Token t =new Token(get(ACCESS_TOKEN_URL, null, true));
        this.oAuthToken = t;
        return t;
    }

    /**
     * 验证用户信息
     * @return
     * @throws FanfouException
     */
    public User verify_credentials() throws FanfouException {
        String result = get(getAbsoluteUrl("/account/verify_credentials.json"), null, true);
        return this.mGson.fromJson(result, User.class);
    }

    public List<Status> getHomeTimeline(String userId, String sinceId, String maxId
            , int count, int page, boolean isLite) throws FanfouException {
        String url = getAbsoluteUrl("/statuses/home_timeline.json");
        List<NameValuePair> p = new ArrayList<NameValuePair>();
        if (userId != null) {
            p.add(new RequestParam("id", userId));
        }
        if (sinceId != null) {
            p.add(new RequestParam("since_id", sinceId));
        }
        if (maxId != null) {
            p.add(new RequestParam("max_id", maxId));
        }
        if (count > 0) {
            p.add(new RequestParam("count", String.valueOf(count)));
        }
        if (page > 0) {
            p.add(new RequestParam("page", String.valueOf(page)));
        }
        if (isLite) {
            p.add(new RequestParam("mode", "lite"));
        }
        Type type = new TypeToken<List<Status>>(){}.getType();
        return mGson.fromJson(get(url, p, true), type);
    }

    /**
     * 获得回复列表
     * @param sinceId
     * @param maxId
     * @param count
     * @param page
     * @param isLite
     * @return
     * @throws FanfouException
     */
    public List<Status> getMentions(String sinceId, String maxId, int count, int page
            , boolean isLite) throws FanfouException {
        String url = getAbsoluteUrl("/statuses/mentions.json");
        List<NameValuePair> p = new ArrayList<NameValuePair>();
        if (sinceId != null) {
            p.add(new RequestParam("since_id", sinceId));
        }
        if (maxId != null) {
            p.add(new RequestParam("max_id", maxId));
        }
        if (count > 0 && count <= 60) {
            p.add(new RequestParam("count", String.valueOf(count)));
        }
        if (page > 0) {
            p.add(new RequestParam("page", String.valueOf(page)));
        }
        if (isLite) {
            p.add(new RequestParam("mode", "lite"));
        }
        Type type = new TypeToken<List<Status>>(){}.getType();
        return mGson.fromJson(get(url, p, true), type);
    }

    /**
     * 发布一条纯文字的饭否
     * @param status
     * @param in_reply_to_status_id
     * @param in_reply_to_user_id
     * @param repost_status_id
     * @param location
     * @param isLite
     * @return
     * @throws FanfouException
     */
    public Status publish(String status, String in_reply_to_status_id, String in_reply_to_user_id, String repost_status_id,
                          String location, boolean isLite) throws FanfouException {
        String url = getAbsoluteUrl("/statuses/update.json");
        List<NameValuePair> p = new ArrayList<NameValuePair>();
        p.add(new RequestParam("status", status));
        if (in_reply_to_status_id != null && !in_reply_to_status_id.equals("")) {
            p.add(new RequestParam("in_reply_to_status_id", in_reply_to_status_id));
        }
        if (in_reply_to_user_id != null && !in_reply_to_user_id.equals("")) {
            p.add(new RequestParam("in_reply_to_user_id", in_reply_to_user_id));
        }
        if (repost_status_id != null && !repost_status_id.equals("")) {
            p.add(new RequestParam("repost_status_id", repost_status_id));
        }
        if (location != null) {
            p.add(new RequestParam("location", location));
        }
        if (isLite) {
            p.add(new RequestParam("mode", "lite"));
        }
        return mGson.fromJson(post(url, p, true), Status.class);
    }


    public Status publish(String status, String in_reply_to_status_id, String in_reply_to_user_id, String repost_status_id,
                          String location, InputStream photo, boolean isLite) throws FanfouException {
        String url = getAbsoluteUrl("/photos/upload.json");
        List<NameValuePair> p = new ArrayList<NameValuePair>();
        p.add(new RequestParam("status", status));
        if (in_reply_to_status_id != null && !in_reply_to_status_id.equals("")) {
            p.add(new RequestParam("in_reply_to_status_id", in_reply_to_status_id));
        }
        if (in_reply_to_user_id != null && !in_reply_to_user_id.equals("")) {
            p.add(new RequestParam("in_reply_to_user_id", in_reply_to_user_id));
        }
        if (repost_status_id != null && !repost_status_id.equals("")) {
            p.add(new RequestParam("repost_status_id", repost_status_id));
        }
        if (location != null) {
            p.add(new RequestParam("location", location));
        }
        if (isLite) {
            p.add(new RequestParam("mode", "lite"));
        }
        return mGson.fromJson(post(url, p, true), Status.class);
    }

    public Status publish(String status, String in_reply_to_status_id, String in_reply_to_user_id, String repost_status_id,
                          String location, File photo, boolean isLite) throws FanfouException {
        String url = getAbsoluteUrl("/photos/upload.json");
        List<NameValuePair> p = new ArrayList<NameValuePair>();
        p.add(new RequestParam("status", status));
        if (in_reply_to_status_id != null && !in_reply_to_status_id.equals("")) {
            p.add(new RequestParam("in_reply_to_status_id", in_reply_to_status_id));
        }
        if (in_reply_to_user_id != null && !in_reply_to_user_id.equals("")) {
            p.add(new RequestParam("in_reply_to_user_id", in_reply_to_user_id));
        }
        if (repost_status_id != null && !repost_status_id.equals("")) {
            p.add(new RequestParam("repost_status_id", repost_status_id));
        }
        if (location != null) {
            p.add(new RequestParam("location", location));
        }
        if (isLite) {
            p.add(new RequestParam("mode", "lite"));
        }
        return mGson.fromJson(post(url, p, true), Status.class);
    }

    public Status publish(String status, String in_reply_to_status_id, String in_reply_to_user_id, String repost_status_id,
                          String location, byte[] photo, boolean isLite) throws FanfouException {
        String url = getAbsoluteUrl("/photos/upload.json");
        List<NameValuePair> p = new ArrayList<NameValuePair>();
        p.add(new RequestParam("status", status));
        if (in_reply_to_status_id != null && !in_reply_to_status_id.equals("")) {
            p.add(new RequestParam("in_reply_to_status_id", in_reply_to_status_id));
        }
        if (in_reply_to_user_id != null && !in_reply_to_user_id.equals("")) {
            p.add(new RequestParam("in_reply_to_user_id", in_reply_to_user_id));
        }
        if (repost_status_id != null && !repost_status_id.equals("")) {
            p.add(new RequestParam("repost_status_id", repost_status_id));
        }
        if (location != null) {
            p.add(new RequestParam("location", location));
        }
        if (isLite) {
            p.add(new RequestParam("mode", "lite"));
        }
        MultipartParameter parameter = new MultipartParameter("photo", "PNG", photo);
        return mGson.fromJson(post(url, p, true, parameter), Status.class);
    }

    /**
     *
     * @param url
     * @param params
     * @return
     * @throws FanfouException
     */
    private String get(String url, List<NameValuePair> params) throws FanfouException {
        return get(url, params, false);
    }

    /**
     * get方法
     *
     * @param url
     * @param params
     * @param isAuth
     * @return
     * @throws FanfouException
     */
    private String get(String url, List<NameValuePair> params, boolean isAuth) throws FanfouException {
        List<NameValuePair> headers = null;
        if (isAuth) {
            headers = new ArrayList<NameValuePair>();
            String sign = mOAuth.getOAuthSignature(Method.GET.name(), url, params, this.oAuthToken);
            headers.add(new RequestParam("Authorization", sign));
        }
        return get(url, params, headers);
    }

    public String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private String get(String url, List<NameValuePair> params, List<NameValuePair> headers) throws FanfouException {
        if (params != null) {
            url = getQuery(url, params);
        }

        NLog.d(TAG, "request:" + url);

        Response response = mClient.fetch(url, Method.GET, null, headers);
        if (response.getResponseCode() > 300) {
            //Error
            NLog.d(TAG, "error code=" + response.getResponseCode());
            NLog.d(TAG, "respose=[" + response.getResponseContent() + "]");
            throw new FanfouException(response);
        } else {
            NLog.d(TAG, "respose=[" + response.getResponseContent() + "]");
            return response.getResponseContent();
        }
    }

    private String post(String url, List<NameValuePair> params, boolean isAuth) throws FanfouException {
        List<NameValuePair> headers = null;
        if (isAuth) {
            headers = new ArrayList<NameValuePair>();
            String sign = mOAuth.getOAuthSignature(Method.POST.name(), url, params, this.oAuthToken);
            headers.add(new RequestParam("Authorization", sign));

        }
        return post(url, params, headers);
    }

    private String post(String url, List<NameValuePair> params, List<NameValuePair> headers) throws FanfouException {

        Response response = mClient.fetch(url, Method.POST, params, headers);
        if (response.getResponseCode() > 300) {
            //Error
            NLog.d(TAG, "error code=" + response.getResponseCode());
            NLog.d(TAG, "respose=[" + response.getResponseContent() + "]");
            throw new FanfouException(response);
        } else {
            NLog.d(TAG, "respose=[" + response.getResponseContent() + "]");
            return response.getResponseContent();
        }
    }

    private String post(String url, List<NameValuePair> params, MultipartParameter parameter) throws FanfouException {
        return post(url, params, true, parameter);
    }

    private String post(String url, List<NameValuePair> params, boolean isAuth, MultipartParameter parameter) throws FanfouException {
        List<NameValuePair> headers = null;
        if (isAuth) {
            headers = new ArrayList<NameValuePair>();
            String sign = mOAuth.getOAuthSignature(Method.POST.name(), url, null, this.oAuthToken);
            headers.add(new RequestParam("Authorization", sign));
        }
        return post(url, params, headers, parameter);
    }

    private String post(String url, List<NameValuePair> params, List<NameValuePair> headers, MultipartParameter parameter) throws FanfouException {

        Response response = mClient.fetch(url, Method.POST, params, headers, parameter);
        if (response.getResponseCode() > 300) {
            //Error
            NLog.d(TAG, "error code=" + response.getResponseCode());
            NLog.d(TAG, "respose=[" + response.getResponseContent() + "]");
            throw new FanfouException(response);
        } else {
            NLog.d(TAG, "respose=[" + response.getResponseContent() + "]");
            return response.getResponseContent();
        }
    }


    private String getQuery(String url, List<NameValuePair> params) {
        StringBuilder result = new StringBuilder(url);
        boolean first = true;
        if (url.contains("?")) {
            first = false;
        }

        try {
            for (NameValuePair pair : params){
                if (first) {
                    first = false;
                    result.append("?");
                } else {
                    result.append("&");
                }


                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }
            return result.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return url;
        }
    }
}
