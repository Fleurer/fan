package com.googolmo.fanfou.api.module;

import org.json.JSONObject;

/**
 * User: googolmo
 * Date: 12-9-7
 * Time: 下午10:48
 */
public class Photo{

    private String jsonString;

    private String thumburl;
    private String imageurl;
    private String largeurl;

    public Photo() {
    }

    public Photo(JSONObject photo) {
        jsonString = photo.toString();
        this.thumburl = photo.optString("thumburl");
        this.largeurl = photo.optString("largeurl");
        this.imageurl = photo.optString("imageurl");

    }

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
        return "Photo [thumburl=" + thumburl + ", imageurl=" + imageurl
                + ", largeurl=" + largeurl + "]";
    }

    public String getJsonString() {
        return this.jsonString;
    }

}
