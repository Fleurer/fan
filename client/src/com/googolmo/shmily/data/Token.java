package com.googolmo.shmily.data;

/**
 * User: googolmo
 * Date: 12-9-8
 * Time: 下午4:01
 */
public class Token {
    private String request_token;
    private String request_tokensecret;
    private String access_token;
    private String access_tokensecret;

    public Token() {
    }

    public String getRequest_token() {
        return request_token;
    }

    public void setRequest_token(String request_token) {
        this.request_token = request_token;
    }

    public String getRequest_tokensecret() {
        return request_tokensecret;
    }

    public void setRequest_tokensecret(String request_tokensecret) {
        this.request_tokensecret = request_tokensecret;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getAccess_tokensecret() {
        return access_tokensecret;
    }

    public void setAccess_tokensecret(String access_tokensecret) {
        this.access_tokensecret = access_tokensecret;
    }
}
