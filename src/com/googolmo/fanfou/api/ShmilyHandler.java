package com.googolmo.fanfou.api;

import android.os.Message;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * User: googolmo
 * Date: 12-9-15
 * Time: 上午10:11
 */
public class ShmilyHandler extends AsyncHttpResponseHandler {
    protected static final int SUCCESS_JSON_MESSAGE = 100;

    //
    // Callbacks to be overridden, typically anonymously
    //

    /**
     * Fired when a request returns successfully and contains a json object
     * at the base of the response string. Override to handle in your
     * own code.
     * @param response the parsed json object found in the server response (if any)
     */
    public void onSuccess(JSONObject response) {}


    /**
     * Fired when a request returns successfully and contains a json array
     * at the base of the response string. Override to handle in your
     * own code.
     * @param response the parsed json array found in the server response (if any)
     */
    public void onSuccess(JSONArray response) {}

    public void onFailure(Throwable e, JSONObject errorResponse) {}
    public void onFailure(Throwable e, JSONArray errorResponse) {}


    //
    // Pre-processing of messages (executes in background threadpool thread)
    //

    @Override
    protected void sendSuccessMessage(String responseBody) {
        try {
            Object jsonResponse = parseResponse(responseBody);
            sendMessage(obtainMessage(SUCCESS_JSON_MESSAGE, jsonResponse));
        } catch(JSONException e) {
            sendFailureMessage(e, responseBody);
        }
    }


    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    @Override
    protected void handleMessage(Message msg) {
        switch(msg.what){
            case SUCCESS_JSON_MESSAGE:
                handleSuccessJsonMessage(msg.obj);
                break;
            default:
                super.handleMessage(msg);
        }
    }

    protected void handleSuccessJsonMessage(Object jsonResponse) {
        if(jsonResponse instanceof JSONObject) {
            onSuccess((JSONObject)jsonResponse);
        } else if(jsonResponse instanceof JSONArray) {
            onSuccess((JSONArray)jsonResponse);
        } else {
            onFailure(new JSONException("Unexpected type " + jsonResponse.getClass().getName()));
        }
    }

    protected Object parseResponse(String responseBody) throws JSONException {
        Object result = null;
        //trim the string to prevent start with blank, and test if the string is valid JSON, because the parser don't do this :(. If Json is not valid this will return null
        responseBody = responseBody.trim();
        if(responseBody.startsWith("{") || responseBody.startsWith("[")) {
            result = new JSONTokener(responseBody).nextValue();
        }
        if (result == null) {
            result = responseBody;
        }
        return result;
    }

    @Override
    protected void handleFailureMessage(Throwable e, String responseBody) {
        try {
            if (responseBody != null) {
                Object jsonResponse = parseResponse(responseBody);
                if(jsonResponse instanceof JSONObject) {
                    onFailure(e, (JSONObject)jsonResponse);
                } else if(jsonResponse instanceof JSONArray) {
                    onFailure(e, (JSONArray)jsonResponse);
                } else {
                    onFailure(e, responseBody);
                }
            }else {
                onFailure(e, "");
            }
        }catch(JSONException ex) {
            onFailure(e, responseBody);
        }
    }

}
