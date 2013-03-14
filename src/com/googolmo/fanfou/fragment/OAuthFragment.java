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
import com.googolmo.fanfou.MainActivity;
import com.googolmo.fanfou.api.FanfouException;
import com.googolmo.fanfou.api.http.Session;
import com.googolmo.fanfou.api.http.Token;
import com.googolmo.fanfou.api.model.User;
import com.googolmo.fanfou.utils.ErrorHandler;
import com.googolmo.fanfou.utils.NLog;
import com.googolmo.fanfou.Constants;
import com.googolmo.fanfou.R;

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
        AccessTokenTask task = new AccessTokenTask(o);
        task.execute();
            return oauthToken;

    }

    private void init() {

    }



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

    private class AccessTokenTask extends AsyncTask<Void, Void, Session> {
        private Token token;

        private AccessTokenTask(Token token) {
            this.token = token;
        }

        @Override
        protected Session doInBackground(Void... voids) {
            try {
                Token t = mApi.getAccessToken(token);
                User user = mApi.verify_credentials();
                Session s = new Session(user.getId(), t);
                getProvider().addSession(s);
                getProvider().getDB().addUser(user);
                getProvider().setCurrentUserId(user.getId());
                NLog.d(TAG, user.toString());
                return s;
            } catch (FanfouException e) {
                e.printStackTrace();
                ErrorHandler.handlerError(getActivity(), e, ErrorHandler.ShowType.DIALOG, new ErrorHandler.CallBack() {
                    @Override
                    public void beforeShow() {
                    }

                    @Override
                    public void afterShow() {
                        getActivity().finish();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Session session) {
            super.onPostExecute(session);
            if (session != null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();

            }
        }
    }
}
