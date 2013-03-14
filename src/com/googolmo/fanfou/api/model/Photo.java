package com.googolmo.fanfou.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;

/**
 * User: googolmo
 * Date: 12-9-7
 * Time: 下午10:48
 */
public class Photo extends JData implements Parcelable{

    @Expose private String thumburl;
    @Expose private String imageurl;
    @Expose private String largeurl;

    public Photo(String json) {
    }

    public Photo(Parcel in) {
        String[] strings = new String[3];
        in.readStringArray(strings);
        this.thumburl = strings[0];
        this.imageurl = strings[1];
        this.largeurl = strings[2];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeStringArray(new String[]{thumburl, imageurl, largeurl});
    }

    public static final Creator<Photo> CREATOR
            = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel parcel) {
            return new Photo(parcel);
        }

        @Override
        public Photo[] newArray(int i) {
            return new Photo[i];
        }
    };

    public String getThumburl() {
        return thumburl;
    }

    public void setThumburl(String thumburl) {
        this.thumburl = thumburl;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getLargeurl() {
        return largeurl;
    }

    public void setLargeurl(String largeurl) {
        this.largeurl = largeurl;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "thumburl='" + thumburl + '\'' +
                ", imageurl='" + imageurl + '\'' +
                ", largeurl='" + largeurl + '\'' +
                '}';
    }


}
