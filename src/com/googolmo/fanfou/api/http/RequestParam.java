/*
 * Copyright (c) 2013. By @GoogolMo
 */

package com.googolmo.fanfou.api.http;

import org.apache.http.NameValuePair;

/**
 * User: GoogolMo
 * Date: 13-3-6
 * Time: 下午9:04
 */
public class RequestParam implements NameValuePair{
    private String key;
    private String value;

    public RequestParam(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getName() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }
}
