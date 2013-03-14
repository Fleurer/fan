package com.googolmo.fanfou.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;

/**
 * User: googolmo
 * Date: 12-9-7
 * Time: 下午10:48
 */
public class User extends JData implements Parcelable{

    @Expose private String id;
    @Expose private String name;
    @Expose private String screen_name;
    @Expose private String location;
    @Expose private String gender;
    @Expose private String birthday;
    @Expose private String description;
    @Expose private String profile_image_url;
    @Expose private String profile_image_url_large;
    @Expose private String url;
    @Expose private String created_at;
    @Expose private int followers_count;
    @Expose private int friends_count;
    @Expose private int favourites_count;
    @Expose private int statuses_count;
    @Expose private int utc_offset;
    @Expose private boolean isProtected;
    @Expose private boolean following;
    @Expose private boolean notifications;
    @Expose private Status status;

    public User() {
    }

    public User(Parcel in) {
        String[] strings = new String[11];
        in.readStringArray(strings);
        this.id = strings[0];
        this.name = strings[1];
        this.screen_name = strings[2];
        this.location = strings[3];
        this.gender = strings[4];
        this.birthday = strings[5];
        this.description = strings[6];
        this.profile_image_url = strings[7];
        this.profile_image_url_large = strings[8];
        this.url = strings[9];
        this.created_at = strings[10];

        int[] ints = new int[5];
        in.readIntArray(ints);
        this.followers_count = ints[0];
        this.friends_count = ints[1];
        this.favourites_count = ints[2];
        this.statuses_count = ints[3];
        this.utc_offset = ints[4];

        boolean[] booleans = new boolean[3];
        in.readBooleanArray(booleans);
        this.isProtected = booleans[0];
        this.following = booleans[1];
        this.notifications = booleans[2];

        this.status = in.readParcelable(Status.class.getClassLoader());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {

        out.writeStringArray(new String[]{id, name, screen_name, location, gender, birthday
                , description, profile_image_url, profile_image_url_large, url, created_at});
        out.writeIntArray(new int[]{followers_count, friends_count, favourites_count
                , statuses_count, utc_offset});
        out.writeBooleanArray(new boolean[]{isProtected, following, notifications});
        out.writeParcelable(status, i);

    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel parcel) {
            return new User(parcel);
        }

        @Override
        public User[] newArray(int i) {
            return new User[i];
        }
    };

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
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", screen_name='" + screen_name + '\'' +
                ", location='" + location + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", description='" + description + '\'' +
                ", profile_image_url='" + profile_image_url + '\'' +
                ", profile_image_url_large='" + profile_image_url_large + '\'' +
                ", url='" + url + '\'' +
                ", created_at='" + created_at + '\'' +
                ", followers_count=" + followers_count +
                ", friends_count=" + friends_count +
                ", favourites_count=" + favourites_count +
                ", statuses_count=" + statuses_count +
                ", utc_offset=" + utc_offset +
                ", isProtected=" + isProtected +
                ", following=" + following +
                ", notifications=" + notifications +
                ", status=" + status +
                '}';
    }
}
