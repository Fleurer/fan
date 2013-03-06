package com.googolmo.fanfou.api.http;

/**
 * User: googolmo
 * Date: 12-9-8
 * Time: 下午1:43
 */
public class AccessToken extends Token{

    private String screenName;
    private String userId;

    public AccessToken(String token, String tokenSecret) {
        super(token, tokenSecret);
    }

    public AccessToken(String string) {
        super(string);
        screenName = getParameter("screen_name");
        userId = getParameter("user_id");
    }

    public String getScreenName() {
        return screenName;
    }

    public String getUserId() {
        return userId;
    }
}
