/*
 * Copyright (c) 2013. By @GoogolMo
 */

package com.googolmo.fanfou.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * User: GoogolMo
 * Date: 13-3-6
 * Time: 下午10:23
 */
public class JsonUtils {
    private static Gson mGson;

    public static Gson getGson() {
        if (mGson == null) {
            mGson = new GsonBuilder()
                    .serializeNulls()
                    .disableHtmlEscaping()
                    .excludeFieldsWithoutExposeAnnotation()
                    .enableComplexMapKeySerialization()
                    .create();
        }
        return mGson;
    }

    public static void setGson(Gson gson) {
        mGson = gson;
    }
}
