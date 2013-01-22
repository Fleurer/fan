package com.googolmo.fanfou.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.googolmo.fanfouApi.http.AccessToken;
import com.googolmo.fanfouApi.http.RequestToken;
import com.googolmo.fanfouApi.module.User;
import com.googolmo.fanfouApi.utils.NLog;
import com.googolmo.fanfou.Constants;
import com.googolmo.fanfou.MainActivity;
import com.googolmo.fanfou.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONObject;

/**
 * User: googolmo
 * Date: 12-12-13
 * Time: 下午8:41
 */
public class OAuthFragment extends BaseFragment {
    private static final String TAG = OAuthFragment.class.getName();

    private WebView mWebView;

    private String mOAuthToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.view_oauth, container, false);
        setupView(view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupView(View view) {
        mWebView = (WebView) view.findViewById(R.id.webview);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                NLog.d(TAG, "shouldOverrideUrlLoading--->" + url);
                mOAuthToken = getOAuthToken(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mOAuthToken = getOAuthToken(url);
                NLog.d(TAG, "onPageStarted----->" + url);
            }
        });
    }

    private String getOAuthToken(String url) {
        if (url.startsWith(Constants.CALLBACK_URL)) {

            int index = url.lastIndexOf(Constants.CALLBACK_URL);
            String oauthToken = url.substring(index + 1);
            oauthToken = oauthToken.split("=")[1];
            RequestToken o = mApi.setRequestToken(oauthToken, mApi.getOAuthToken().getTokenSecret());
            mApi.getAccessToken(o, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String content) {
                    super.onSuccess(content);
                    AccessToken accessToken = mApi.setAccessToken(content);
                    verify_credentials();
                    NLog.d(TAG, content);
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    super.onFailure(error, content);
                    NLog.d(TAG, content);
                }
            });
            return oauthToken;

        }

        return null;
    }

    private void init() {

    }

    private void verify_credentials() {
        mApi.verify_credentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                User user = new User(response);
                getProvider().setToken((AccessToken) mApi.getOAuthToken(), user.getId());
                getProvider().setCurrentUserId(user.getId());
                getProvider().getDB().addUser(user);
                NLog.d(TAG, user.getJsonString());
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getSherlockActivity().setResult(Activity.RESULT_OK);
                getSherlockActivity().finish();
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                super.onFailure(e, errorResponse);
            }
        });
    }


    private void refresh() {
        getApi().getRequestToken(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                NLog.d(TAG, content);
                mApi.getOAuthToken();
                mWebView.loadUrl(mApi.getAuthorizeUrl(Constants.CALLBACK_URL));
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                NLog.d(TAG, content);
            }
        });
    }
}
