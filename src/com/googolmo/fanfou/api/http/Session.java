package com.googolmo.fanfou.api.http;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User: googolmo
 * Date: 12-9-8
 * Time: 下午1:43
 */
public class Session extends Token implements Parcelable {

//    private String screenName;
    private String userId;

//    public Session(String token, String tokenSecret) {
//        super(token, tokenSecret);
//    }

    public Session(String userId, Token token) {
        super(token.getToken(), token.getTokenSecret());
//        this.screenName = screenName;
        this.userId = userId;
    }

    public Session(String userId, String token, String tokenSecret) {
        super(token, tokenSecret);
        this.userId = userId;
    }


    public String getUserId() {
        return userId;
    }

    /**
     * 判断Session是否有效
     * @return
     */
    public boolean isAvailed(){
        return this.getToken() != null && !this.getToken().equals("");
    }

    @Override
    public String toString() {
        return "Session{" +
                "userId='" + userId + '\'' +
                "token='" + getToken() + '\'' +
                ", tokenSecret='" + getTokenSecret() + '\''+
                "} ";
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(this.userId);
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel parcel) {
            return new Session(parcel);
        }

        @Override
        public Session[] newArray(int i) {
            return new Session[0];
        }
    };

    public Session(Parcel in) {
        super(in);
        this.userId = in.readString();
    }


}
