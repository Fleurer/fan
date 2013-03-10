package com.googolmo.fanfou.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.googolmo.fanfou.api.http.Response;
import com.googolmo.fanfou.utils.JsonUtils;
import com.googolmo.fanfou.utils.NLog;

/**
 * User: googolmo
 * Date: 12-9-7
 * Time: 下午11:20
 */
public class FanfouException extends Exception {
    private static final String TAG = FanfouException.class.getName();


//    private JSONObject json;
    private String request;
    private String error;
    private String status;

//    public FanfouException(final JSONObject json) {
//        this.json = json;
//        this.request = this.json.optString("request", "");
//        this.error = this.json.optString("error", "");
//
//    }

//    public FanfouException(Throwable e) {
//        if (e instanceof HttpResponseException) {
//            switch (((HttpResponseException)e).getStatusCode()) {
//                case 500:
//                    this.error = "服务器出错";
//                    break;
//                case 400:
//                    this.error = "无效的请求";
//                    break;
//                case 401:
//                    this.error = "用户需要登录或者认证失败";
//                    break;
//                case 403:
//                    this.error = "用户无访问权限";
//                    break;
//                case 404:
//                    this.error = "请求的资源已经不存在";
//                    break;
//            }
//        }
//    }

    public FanfouException(String request, String error) {
        this.request = request;
        this.error = error;
        this.status = null;
    }

    public FanfouException(Response response) {
        this.status = response.getMessage();
        String res = response.getResponseContent();
        if (res != null && !res.equals("")) {
            try {
                JsonObject object = JsonUtils.parser(res);
                this.request = object.get("request").getAsString();
                this.error = object.get("error").getAsString();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                this.request = null;
                this.error = null;
            }

        } else {
            this.request = null;
            this.error = null;
        }

    }

    public String getRequest() {
        return request;
    }

//    public void setRequest(String request) {
//        this.request = request;
//    }

    public String getError() {
        return error;
    }

//    public void setError(String error) {
//        this.error = error;
//    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "FanfouException{" +
                "request='" + request + '\'' +
                ", error='" + error + '\'' +
                ", status='" + status + '\'' + "} ";
    }
}
