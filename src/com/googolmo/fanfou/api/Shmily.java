package com.googolmo.fanfou.api;

import com.googolmo.fanfou.api.http.*;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * User: googolmo
 * Date: 12-9-7
 * Time: 下午11:21
 */
public class Shmily {
    private static final String TAG = Shmily.class.getName();

    private static final String AUTHORIZE_URL = "http://fanfou.com/oauth/authorize";
    private static final String REQUEST_TOKEN_URL = "http://fanfou.com/oauth/request_token";
    private static final String ACCESS_TOKEN_URL = "http://fanfou.com/oauth/access_token";
    private static final String BASE_URL = "http://api.fanfou.com";

    private String mConsumerKey;
    private String mConsumerSecret;
    private ShmilyClient mClient;
    private OAuth mOAuth;
    private OAuthToken oAuthToken;

    public Shmily(String consumerKey, String consumerSecret) {
        this.mConsumerKey = consumerKey;
        this.mConsumerSecret = consumerSecret;
        init();
    }

    private void init() {
        this.mClient = new ShmilyClient(BASE_URL);
        this.mOAuth = new OAuth(mConsumerKey, mConsumerSecret);
    }



    public OAuthToken getOAuthToken() {
        return this.oAuthToken;
    }

    public void setOAuthToken(OAuthToken token) {
        this.oAuthToken = token;
    }

    public RequestToken setRequestToken(String token, String tokenSecret) {
        this.oAuthToken = new RequestToken(token, tokenSecret);
        return (RequestToken) oAuthToken;
    }

    public AccessToken setAccessToken(String content) {
        this.oAuthToken = new AccessToken(content);
        return (AccessToken) oAuthToken;
    }

    public AccessToken setAccessToken(String token, String tokenSecret) {
        this.oAuthToken = new AccessToken(token, tokenSecret);
        return (AccessToken) oAuthToken;
    }

    /**
     * 获取未授权的Request Token
     * @param handler
     */
    public void getRequestToken(final AsyncHttpResponseHandler handler) {

        this.mClient.setOAuth(mOAuth.getOAuthSignature(ShmilyClient.GET,
                REQUEST_TOKEN_URL, null, null));

        this.mClient.get(REQUEST_TOKEN_URL, null, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                oAuthToken = new OAuthToken(content);
                handler.onSuccess(content);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }
        });

    }

    public String getAuthorizeUrl(String oauth_callback) {
        return String.format("%1$s?oauth_token=%2$s&oauth_callback=%3$s", AUTHORIZE_URL, this.oAuthToken.getToken(), oauth_callback);
    }

    public void getAccessToken(RequestToken requestToken, final AsyncHttpResponseHandler handler) {
        this.oAuthToken = requestToken;
        this.mClient.setOAuth(mOAuth.getOAuthSignature(ShmilyClient.GET,
                ACCESS_TOKEN_URL, null, this.oAuthToken));
        this.mClient.get(ACCESS_TOKEN_URL, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                oAuthToken = new AccessToken(content);
                handler.onSuccess(content);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                handler.onFailure(error, content);
            }
        });
    }

    public void verify_credentials(final JsonHttpResponseHandler handler) {
        String url = mClient.getAbsoluteUrl("/account/verify_credentials.json");
        this.mClient.setOAuth(mOAuth.getOAuthSignature(ShmilyClient.GET,
                url, null, this.oAuthToken));
        this.mClient.get(url, handler);
    }

    /**
     * 获得时间线
     * @param userId
     * @param since_id
     * @param max_id
     * @param count
     * @param page
     * @param isLite
     * @param handler
     */
    public void getHomeTimeline(String userId, String since_id, String max_id, int count, int page,
                                boolean isLite, JsonHttpResponseHandler handler) {
        String url = mClient.getAbsoluteUrl("/statuses/home_timeline.json");
        Params p = new Params();
        if (userId != null) {
            p.put("id", userId);
        }
        if (since_id != null) {
            p.put("since_id", since_id);
        }
        if (max_id != null) {
            p.put("max_id", max_id);
        }
        if (count >= 1 && count <= 60) {
            p.put("count", String.valueOf(count));
        }
        if (page < 1) {
            p.put("page", String.valueOf(page));
        }
        if (isLite) {
            p.put("mode", "lite");
        }
        this.mClient.setOAuth(mOAuth.getOAuthSignature(ShmilyClient.GET,
                url, p, this.oAuthToken));
        this.mClient.get(url, p, handler);
    }


    /**
     * 获得制定用户时间线
     * @param userId
     * @param since_id
     * @param max_id
     * @param isLite
     * @param count
     * @param page
     * @param handler
     */
    public void getUserTimeline(String userId, String since_id, String max_id,
                                boolean isLite, int count, int page, JsonHttpResponseHandler handler) {
        String url = mClient.getAbsoluteUrl("/statuses/user_timeline.json");
        Params p = new Params();
        if (userId != null) {
            p.put("id", userId);
        }
        if (since_id != null) {
            p.put("since_id", since_id);
        }
        if (max_id != null) {
            p.put("max_id", max_id);
        }
        if (count >= 1 && count <= 60) {
            p.put("count", String.valueOf(count));
        }
        if (page < 1) {
            p.put("page", String.valueOf(page));
        }
        if (isLite) {
            p.put("mode", "lite");
        }
        this.mClient.setOAuth(mOAuth.getOAuthSignature(ShmilyClient.GET,
                url, p, this.oAuthToken));
        this.mClient.get(url, p, handler);
    }

    /**
     * 更新消息
     * @param status
     * @param in_reply_to_user_id
     * @param repost_status_id
     * @param location
     * @param isLite
     * @param handler
     */
    public void update(String status, String in_reply_to_status_id, String in_reply_to_user_id, String repost_status_id,
                       String location, boolean isLite, JsonHttpResponseHandler handler) {
        String url = mClient.getAbsoluteUrl("/statuses/update.json");
        Params p = new Params();
        p.put("status", status);
        if (in_reply_to_status_id != null && !in_reply_to_status_id.equals("")) {
            p.put("in_reply_to_status_id", in_reply_to_status_id);
        }
        if (in_reply_to_user_id != null && !in_reply_to_user_id.equals("")) {
            p.put("in_reply_to_user_id", in_reply_to_user_id);
        }
        if (repost_status_id != null && !repost_status_id.equals("")) {
            p.put("repost_status_id", repost_status_id);
        }
        if (location != null) {
            p.put("location", location);
        }
        if (isLite) {
            p.put("mode", "lite");
        }
        this.mClient.setOAuth(mOAuth.getOAuthSignature(ShmilyClient.POST,
                url, p, this.oAuthToken));
        this.mClient.post(url, p, handler);

    }

    /**
     * 上传图片
     * @param photo
     * @param status
     * @param location
     * @param isLite
     * @param handler
     */
    public void upload(InputStream photo, String status, String location,
                       boolean isLite, JsonHttpResponseHandler handler) {
        String url = mClient.getAbsoluteUrl("/photos/upload.json");
        Params p = new Params();
        p.put("photo", photo);
        if (status != null) {
            p.put("status", status);
        }
        if (location != null) {
            p.put("location", location);
        }
        if (isLite) {
            p.put("mode", "lite");
        }
        this.mClient.setOAuth(mOAuth.getOAuthSignature(ShmilyClient.POST,
                url, null, this.oAuthToken));
        this.mClient.post(url, p, handler);
    }

    /**
     * 上传图片
     * @param photo
     * @param status
     * @param location
     * @param isLite
     * @param handler
     */
    public void upload(File photo, String status, String location,
                       boolean isLite, JsonHttpResponseHandler handler) {
        try {
            InputStream is = new FileInputStream(photo);
            upload(is, status, location, isLite, handler);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得门神
     * @param since_id
     * @param max_id
     * @param count
     * @param page
     * @param isLite
     * @param handler
     */
    public void getMentions(String since_id, String max_id, int count, int page
            , boolean isLite, JsonHttpResponseHandler handler) {
        String url = mClient.getAbsoluteUrl("/statuses/mentions.json");
        Params p =new Params();
        if (since_id != null) {
            p.put("since_id", since_id);
        }
        if (max_id != null) {
            p.put("max_id", max_id);
        }
        if (count > 0 && count <= 60) {
            p.put("count", String.valueOf(count));
        }
        if (page > 0) {
            p.put("page", String.valueOf(page));
        }
        if (isLite) {
            p.put("mode", "lite");
        }
        this.mClient.setOAuth(mOAuth.getOAuthSignature(ShmilyClient.GET,
                url, p, this.oAuthToken));
        this.mClient.get(url, p, handler);
    }



}
