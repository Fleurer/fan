package com.googolmo.fanfou.api.module;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

/**
 * User: googolmo
 * Date: 12-9-7
 * Time: 下午10:48
 */
public class Status implements Parcelable{
    private String jsonString;
    private String created_at;
    private String id;
    private int rwaid;
    private String text;
    private String source;
    private boolean truncated;
    private String in_reply_to_status_id;
    private String in_reply_to_user_id;
    private String in_reply_to_screen_name;
    private String repost_status_id;
    private Status repost_status;
    private String repost_user_id;
    private String repost_screen_name;
    private boolean favorited;
    private String location;
    private User user;
    private Photo photo;

    public Status() {
    }

    public Status(JSONObject status) {
        this.jsonString = status.toString();
        this.created_at = status.optString("created_at");
        this.id = status.optString("id");
        this.rwaid = status.optInt("rawid", 0);
        this.text = status.optString("text");
        this.source = status.optString("source");
        this.truncated = status.optBoolean("truncated", false);
        this.in_reply_to_screen_name = status.optString("in_reply_to_screen_name");
        this.in_reply_to_status_id = status.optString("in_reply_to_status_id");
        this.in_reply_to_user_id = status.optString("in_reply_to_user_id");
        this.favorited = status.optBoolean("favorited");
        this.location = status.optString("location");
        JSONObject userJson = status.optJSONObject("user");
        if (userJson != null && userJson.length() > 0) {
            this.user = new User(userJson);
        }
        JSONObject photoJson = status.optJSONObject("photo");
        if (photoJson != null && photoJson.length() > 0) {
            this.photo = new Photo(photoJson);
        }
        JSONObject repostJson = status.optJSONObject("repost_status");
        if (repostJson != null && repostJson.length() > 0) {
            this.repost_status = new Status(repostJson);
        }
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRwaid() {
        return rwaid;
    }

    public void setRwaid(int rwaid) {
        this.rwaid = rwaid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    public String getIn_reply_to_status_id() {
        return in_reply_to_status_id;
    }

    public void setIn_reply_to_status_id(String in_reply_to_status_id) {
        this.in_reply_to_status_id = in_reply_to_status_id;
    }

    public String getIn_reply_to_user_id() {
        return in_reply_to_user_id;
    }

    public void setIn_reply_to_user_id(String in_reply_to_user_id) {
        this.in_reply_to_user_id = in_reply_to_user_id;
    }

    public String getIn_reply_to_screen_name() {
        return in_reply_to_screen_name;
    }

    public void setIn_reply_to_screen_name(String in_reply_to_screen_name) {
        this.in_reply_to_screen_name = in_reply_to_screen_name;
    }

    public String getRepost_status_id() {
        return repost_status_id;
    }

    public void setRepost_status_id(String repost_status_id) {
        this.repost_status_id = repost_status_id;
    }

    public Status getRepost_status() {
        return repost_status;
    }

    public void setRepost_status(Status repost_status) {
        this.repost_status = repost_status;
    }

    public String getRepost_user_id() {
        return repost_user_id;
    }

    public void setRepost_user_id(String repost_user_id) {
        this.repost_user_id = repost_user_id;
    }

    public String getRepost_screen_name() {
        return repost_screen_name;
    }

    public void setRepost_screen_name(String repost_screen_name) {
        this.repost_screen_name = repost_screen_name;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Status status = (Status) o;

        if (!id.equals(status.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Status [created_at=" + created_at + ", id=" + id + ", text="
                + text + ", source=" + source + ", truncated=" + truncated
                + ", in_reply_to_status_id=" + in_reply_to_status_id
                + ", in_reply_to_user_id=" + in_reply_to_user_id
                + ", favorited=" + favorited + ", in_reply_to_screen_name="
                + in_reply_to_screen_name + ", location=" + location
                + ", photo_url=" + photo + ", user=" + user + "]";
    }

    public String getJsonString() {
        return this.jsonString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(created_at);
        out.writeString(id);
        out.writeString(text);
        out.writeString(source);
    }

    public static final Creator<Status> CREATOR
            = new Creator<Status>() {
        @Override
        public Status createFromParcel(Parcel source) {
            return new Status(source);
        }

        @Override
        public Status[] newArray(int size) {
            return new Status[0];
        }
    };

    private Status(Parcel in) {


    }
}
