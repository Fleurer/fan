package com.googolmo.fanfou.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * User: googolmo
 * Date: 13-1-13
 * Time: 下午12:03
 */
public class MenuModel implements Parcelable{
    private int id;
    private String title;
    private String clsName;
    private Bundle bundle;

    public MenuModel() {
    }

    public MenuModel(int id, String title, String clsName, Bundle bundle) {
        this.id = id;
        this.title = title;
        this.clsName = clsName;
        this.bundle = bundle;
    }

    public MenuModel(Parcel in) {
        String[] s = new String[2];
        in.readStringArray(s);
        this.title = s[0];
        this.clsName = s[1];

        this.id = in.readInt();
        this.bundle = in.readBundle();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag) {
        dest.writeStringArray(new String[]{title, clsName});
        dest.writeInt(id);
        dest.writeBundle(bundle);
    }

    public static final Creator<MenuModel> CREATOR = new Creator<MenuModel>() {
        @Override
        public MenuModel createFromParcel(Parcel parcel) {
            return new MenuModel(parcel);
        }

        @Override
        public MenuModel[] newArray(int i) {
            return new MenuModel[i];
        }
    };


    @Override
    public String toString() {
        return "MenuModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", clsName='" + clsName + '\'' +
                ", bundle=" + bundle +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClsName() {
        return clsName;
    }

    public void setClsName(String clsName) {
        this.clsName = clsName;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }



}
