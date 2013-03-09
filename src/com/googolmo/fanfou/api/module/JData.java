/*
 * Copyright (c) 2013. By @GoogolMo
 */

package com.googolmo.fanfou.api.module;

import android.os.Parcel;
import android.os.Parcelable;
import com.googolmo.fanfou.utils.JsonUtils;

/**
 * User: GoogolMo
 * Date: 13-3-9
 * Time: 上午11:26
 */
public class JData implements Parcelable{

    public String getJsonString(){
        return JsonUtils.getGson().toJson(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
