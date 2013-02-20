package com.googolmo.fanfou.module;

import android.os.Bundle;

/**
 * User: googolmo
 * Date: 13-1-13
 * Time: 下午12:03
 */
public class MenuModule {
    private String title;
    private String clsName;
    private Bundle bundle;

    public MenuModule() {
    }

    public MenuModule(String title, String clsName, Bundle bundle) {

        this.title = title;
        this.clsName = clsName;
        this.bundle = bundle;
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
