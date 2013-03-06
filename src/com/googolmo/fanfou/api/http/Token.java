/*
 * Copyright (c) 2013. By @GoogolMo
 */

package com.googolmo.fanfou.api.http;

import javax.crypto.spec.SecretKeySpec;

/**
 * User: GoogolMo
 * Date: 13-3-6
 * Time: 下午9:41
 */
public class Token {

    private String token;
    private String tokenSecret;

    private transient SecretKeySpec secretKeySpec;
    String[] responseStr = null;

    public Token(String token, String tokenSecret) {
        this.token = token;
        this.tokenSecret = tokenSecret;
    }

    public Token(String string) {
        responseStr = string.split("&");
        tokenSecret = getParameter("oauth_token_secret");
        token = getParameter("oauth_token");
    }

    public String getToken() {
        return token;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    /*package*/ void setSecretKeySpec(SecretKeySpec secretKeySpec) {
        this.secretKeySpec = secretKeySpec;
    }

    /*package*/ SecretKeySpec getSecretKeySpec() {
        return secretKeySpec;
    }

    public String getParameter(String parameter) {
        String value = null;
        for (String str : responseStr) {
            if (str.startsWith(parameter+'=')) {
                value = str.split("=")[1].trim();
                break;
            }
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;

        Token that = (Token) o;

        return !(secretKeySpec != null ? !secretKeySpec.equals(that.secretKeySpec) : that.secretKeySpec != null) && token.equals(that.token) && tokenSecret.equals(that.tokenSecret);

    }

    @Override
    public int hashCode() {
        int result = token.hashCode();
        result = 31 * result + tokenSecret.hashCode();
        result = 31 * result + (secretKeySpec != null ? secretKeySpec.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OAuthToken{" +
                "token='" + token + '\'' +
                ", tokenSecret='" + tokenSecret + '\'' +
                ", secretKeySpec=" + secretKeySpec +
                '}';
    }
}
