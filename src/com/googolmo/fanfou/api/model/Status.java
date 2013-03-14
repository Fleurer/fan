package com.googolmo.fanfou.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;

/**
 * User: googolmo
 * Date: 12-9-7
 * Time: 下午10:48
 */
public class Status extends JData implements Parcelable{

    @Expose private String created_at;
    @Expose private String id;
    @Expose private String text;
    @Expose private String source;
    @Expose private String location;
    @Expose private String in_reply_to_status_id;
    @Expose private String in_reply_to_user_id;
    @Expose private String in_reply_to_screen_name;
    @Expose private String repost_status_id;
    @Expose private String repost_user_id;
    @Expose private String repost_screen_name;
    @Expose private int rawid;
    @Expose private boolean favorited;
    @Expose private boolean truncated;
    @Expose private User user;
    @Expose private Photo photo;
    @Expose private Status repost_status;

    public Status() {
    }

    private Status(Parcel in) {
        String[] s = new String[11];
        in.readStringArray(s);
        this.created_at = s[0];
        this.id = s[1];
        this.text = s[2];
        this.source = s[3];
        this.location = s[4];
        this.in_reply_to_status_id = s[5];
        this.in_reply_to_user_id = s[6];
        this.in_reply_to_screen_name = s[7];
        this.repost_status_id = s[8];
        this.repost_user_id = s[9];
        this.repost_screen_name = s[10];

        boolean[] b = new boolean[2];
        in.readBooleanArray(b);
        this.favorited = b[0];
        this.truncated = b[1];

        this.rawid = in.readInt();

        this.user = in.readParcelable(User.class.getClassLoader());
        this.photo = in.readParcelable(Photo.class.getClassLoader());
        this.repost_status = in.readParcelable(Status.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeStringArray(new String[]{created_at, id, text, source, location
                , in_reply_to_status_id, in_reply_to_user_id, in_reply_to_screen_name
                , repost_status_id, repost_user_id, repost_screen_name});
        out.writeBooleanArray(new boolean[]{favorited, truncated});

        out.writeInt(rawid);
        out.writeParcelable(user, flags);
        out.writeParcelable(photo, flags);
        out.writeParcelable(repost_status, flags);

    }

    public static final Creator<Status> CREATOR = new Creator<Status>() {
        @Override
        public Status createFromParcel(Parcel source) {
            return new Status(source);
        }

        @Override
        public Status[] newArray(int size) {
            return new Status[size];
        }
    };



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

    public int getRawid() {
        return rawid;
    }

    public void setRawid(int rawid) {
        this.rawid = rawid;
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
    public String toString() {
        return "Status{" +
                "created_at='" + created_at + '\'' +
                ", id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", source='" + source + '\'' +
                ", in_reply_to_status_id='" + in_reply_to_status_id + '\'' +
                ", in_reply_to_user_id='" + in_reply_to_user_id + '\'' +
                ", in_reply_to_screen_name='" + in_reply_to_screen_name + '\'' +
                ", repost_status_id='" + repost_status_id + '\'' +
                ", repost_status=" + repost_status +
                ", repost_user_id='" + repost_user_id + '\'' +
                ", repost_screen_name='" + repost_screen_name + '\'' +
                ", rawid=" + rawid +
                ", favorited=" + favorited +
                ", truncated=" + truncated +
                ", location='" + location + '\'' +
                ", user=" + user +
                ", photo=" + photo +
                '}';
    }
}
