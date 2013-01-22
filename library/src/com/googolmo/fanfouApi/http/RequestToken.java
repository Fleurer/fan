package com.googolmo.fanfouApi.http;

/**
 * User: googolmo
 * Date: 12-9-8
 * Time: 下午4:24
 */
public class RequestToken extends OAuthToken {
    public RequestToken(String token, String tokenSecret) {
        super(token, tokenSecret);
    }

    public RequestToken(String string) {
        super(string);
    }


}
