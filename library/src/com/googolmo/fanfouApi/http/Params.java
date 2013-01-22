package com.googolmo.fanfouApi.http;

import com.loopj.android.http.RequestParams;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: googolmo
 * Date: 12-9-8
 * Time: 上午9:12
 */
public class Params extends RequestParams{

    public Params() {
        super();
    }

    public Params(Map<String, String> source) {
        super(source);
    }

    public Params(String key, String value) {
        super(key, value);
    }

    /**
     * 获得Params的set
     * @return
     */
    public Set<ConcurrentHashMap.Entry<String, String>> entrySet() {
        return urlParams.entrySet();
    }
}
