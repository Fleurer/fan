/*
 * Copyright (c) 2013. By @GoogolMo
 */

package com.googolmo.fanfou.api.http;

import android.os.Parcel;
import android.os.Parcelable;

import javax.crypto.spec.SecretKeySpec;

/**
 * User: GoogolMo
 * Date: 13-3-6
 * Time: 下午9:41
 */
public class Token implements Parcelable{

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
        return "Token{" +
                "token='" + token + '\'' +
                ", tokenSecret='" + tokenSecret + '\'' +
                ", secretKeySpec=" + secretKeySpec +
                '}';
    }

    public Token(Parcel in) {
        String[] s = new String[2];
        in.readStringArray(s);
        this.token = s[0];
        this.tokenSecret = s[1];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{token, tokenSecret});
    }

    public static final Creator<Token> CREATOR = new Creator<Token>() {
        @Override
        public Token createFromParcel(Parcel parcel) {
            return new Token(parcel);
        }

        @Override
        public Token[] newArray(int i) {
            return new Token[i];
        }
    };
}
