/*
 * Copyright (c) 2013. By @GoogolMo
 */

package com.googolmo.fanfou.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * User: GoogolMo
 * Date: 13-3-6
 * Time: 下午10:23
 */
public class JsonUtils {
    private static Gson mGson;
    private static JsonParser mJsonParser;

    public static Gson getGson() {
        if (mGson == null) {
            mGson = new GsonBuilder()
                    .serializeNulls()
                    .disableHtmlEscaping()
                    .enableComplexMapKeySerialization()
                    .excludeFieldsWithoutExposeAnnotation()
                    .enableComplexMapKeySerialization()
                    .create();
        }
        return mGson;
    }

    public static void setGson(Gson gson) {
        mGson = gson;
    }

    public static JsonParser getJsonParser() {
        if (mJsonParser == null) {
            mJsonParser = new JsonParser();
        }
        return mJsonParser;
    }

    public static JsonObject parser(String json) {
        return getJsonParser().parse(json).getAsJsonObject();
    }
}
