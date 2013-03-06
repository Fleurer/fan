package com.googolmo.fanfou.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.googolmo.fanfou.api.FanfouException;
import com.googolmo.fanfou.api.http.AccessToken;
import com.googolmo.fanfou.api.http.RequestToken;
import com.googolmo.fanfou.api.http.Token;
import com.googolmo.fanfou.api.module.User;
import com.googolmo.fanfou.utils.NLog;
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
                if (url.startsWith(Constants.CALLBACK_URL)) {
                    mOAuthToken = getOAuthToken(url);
                }

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                NLog.d(TAG, "onPageStarted----->" + url);
                if (url.startsWith(Constants.CALLBACK_URL)) {
                    mOAuthToken = getOAuthToken(url);
                }

            }
        });
    }

    private String getOAuthToken(String url) {

            int index = url.lastIndexOf(Constants.CALLBACK_URL);
            String oauthToken = url.substring(index + 1);
            oauthToken = oauthToken.split("=")[1];
        Token o = new Token(oauthToken, mApi.getOAuthToken().getTokenSecret());
//        mApi.setOAuthToken(o);
        AccessTokenTask task = new AccessTokenTask(o);
        task.execute();
////            RequestToken o = mApi.setRequestToken(oauthToken, mApi.getOAuthToken().getTokenSecret());
//            mApi.getAccessToken(o, new AsyncHttpResponseHandler() {
//                @Override
//                public void onSuccess(String content) {
//                    super.onSuccess(content);
//                    AccessToken accessToken = mApi.setAccessToken(content);
//                    verify_credentials();
//                    NLog.d(TAG, content);
//                }
//
//                @Override
//                public void onFailure(Throwable error, String content) {
//                    super.onFailure(error, content);
//                    NLog.d(TAG, content);
//                }
//            });
            return oauthToken;

    }

    private void init() {

    }
//
//    private void verify_credentials() {
//        mApi.verify_credentials(new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(JSONObject response) {
//                super.onSuccess(response);
//                User user = new User(response);
//                getProvider().setToken((AccessToken) mApi.getOAuthToken(), user.getId());
//                getProvider().setCurrentUserId(user.getId());
//                getProvider().getDB().addUser(user);
//                NLog.d(TAG, user.toString());
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                getSherlockActivity().setResult(Activity.RESULT_OK);
//                getSherlockActivity().finish();
//            }
//
//            @Override
//            public void onFailure(Throwable e, JSONObject errorResponse) {
//                super.onFailure(e, errorResponse);
//            }
//        });
//    }


    private void refresh() {
        TokenTask task = new TokenTask();
        task.execute();
    }

    private class TokenTask extends AsyncTask<Void, Void, Token> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Token token) {
            super.onPostExecute(token);
            if (token != null) {
                mWebView.loadUrl(mApi.getAuthorizeUrl(token, Constants.CALLBACK_URL));
            }
        }

        @Override
        protected Token doInBackground(Void... voids) {
            try {
                return mApi.getRequestToken();
            } catch (FanfouException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class AccessTokenTask extends AsyncTask<Void, Void, AccessToken> {
        private Token token;

        private AccessTokenTask(Token token) {
            this.token = token;
        }

        @Override
        protected AccessToken doInBackground(Void... voids) {
            try {
                Token t = mApi.getAccessToken(token);
                User user = mApi.verify_credentials();

                getProvider().setToken((AccessToken) mApi.getOAuthToken(), user.getId());
                getProvider().setCurrentUserId(user.getId());
                getProvider().getDB().addUser(user);
                NLog.d(TAG, user.toString());
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getSherlockActivity().setResult(Activity.RESULT_OK);
                getSherlockActivity().finish();
            } catch (FanfouException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
