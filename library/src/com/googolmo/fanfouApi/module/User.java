package com.googolmo.fanfouApi.module;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

/**
 * User: googolmo
 * Date: 12-9-7
 * Time: 下午10:48
 */
public class User{

    private String jsonString;

    private String id;
    private String name;
    private String screen_name;
    private String location;
    private String gender;
    private String birthday;
    private String description;
    private String profile_image_url;
    private String profile_image_url_large;
    private String url;
    private boolean isProtected;
    private int followers_count;
    private int friends_count;
    private int favourites_count;
    private int statuses_count;
    private boolean following;
    private boolean notifications;
    private String created_at;
    private int utc_offset;
    private Status status;

    public User() {
    }

    public User(JSONObject user) {

        jsonString = user.toString();

        this.created_at = user.optString("created_at");
        this.name = user.optString("name");
        this.id = user.optString("id");
        this.screen_name = user.optString("screen_name");
        this.location = user.optString("location");
        this.gender = user.optString("gender");
        this.birthday = user.optString("birthday");
        this.description = user.optString("description");
        this.profile_image_url = user.optString("profile_image_url");
        this.profile_image_url_large = user.optString("profile_image_url_large");
        this.url = user.optString("url");
        this.isProtected = user.optBoolean("protected", false);
        this.followers_count = user.optInt("followers_count", 0);
        this.friends_count = user.optInt("friends_count", 0);
        this.favourites_count = user.optInt("favourites_count", 0);
        this.statuses_count = user.optInt("statuses_count", 0);
        this.following = user.optBoolean("following", false);
        this.notifications = user.optBoolean("notifications", false);
        this.utc_offset = user.optInt("utc_offset", 0);
        JSONObject statusJson = user.optJSONObject("status");
        if (statusJson != null && statusJson.length() > 0) {
            this.status = new Status(statusJson);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getProfile_image_url_large() {
        return profile_image_url_large;
    }

    public void setProfile_image_url_large(String profile_image_url_large) {
        this.profile_image_url_large = profile_image_url_large;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public int getFriends_count() {
        return friends_count;
    }

    public void setFriends_count(int friends_count) {
        this.friends_count = friends_count;
    }

    public int getFavourites_count() {
        return favourites_count;
    }

    public void setFavourites_count(int favourites_count) {
        this.favourites_count = favourites_count;
    }

    public int getStatuses_count() {
        return statuses_count;
    }

    public void setStatuses_count(int statuses_count) {
        this.statuses_count = statuses_count;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getUtc_offset() {
        return utc_offset;
    }

    public void setUtc_offset(int utc_offset) {
        this.utc_offset = utc_offset;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!id.equals(user.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", screen_name="
                + screen_name + ", location=" + location + ", desription="
                + description + ", profile_image_url=" + profile_image_url
                + ", url=" + url + ", isProtected=" + isProtected
                + ", friends_count=" + friends_count + ", followers_count="
                + followers_count + ", favourites_count=" + favourites_count
                + ", created_at=" + created_at + ", following=" + following
                + ", notifications=" + notifications + ", utc_offset="
                + utc_offset + ", status=" + status + "]";
    }


    public String getJsonString() {
        return this.jsonString;
    }
}
