package com.googolmo.fanfou.api;

import com.googolmo.fanfou.api.http.Response;
import org.apache.http.client.HttpResponseException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: googolmo
 * Date: 12-9-7
 * Time: 下午11:20
 */
public class FanfouException extends Exception {


    private JSONObject json;
    private String request;
    private String error;
    private String status;

    public FanfouException(final JSONObject json) {
        this.json = json;
        this.request = this.json.optString("request", "");
        this.error = this.json.optString("error", "");

    }

    public FanfouException(Throwable e) {
        if (e instanceof HttpResponseException) {
            switch (((HttpResponseException)e).getStatusCode()) {
                case 500:
                    this.error = "服务器出错";
                    break;
                case 400:
                    this.error = "无效的请求";
                    break;
                case 401:
                    this.error = "用户需要登录或者认证失败";
                    break;
                case 403:
                    this.error = "用户无访问权限";
                    break;
                case 404:
                    this.error = "请求的资源已经不存在";
                    break;
            }
        }
    }

    public FanfouException(String request, String error) {
        this.request = request;
        this.error = error;
        this.json = null;
    }

    public FanfouException(Response response) {
        this.request = request;
        this.error = error;
        this.json = null;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
        this.error = json.optString("error", "");
        this.request = json.optString("request", "");
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "apiError:<" +
                "request='" + request + '\'' +
                ", error='" + error + '\'' +
                '>';
    }
}
