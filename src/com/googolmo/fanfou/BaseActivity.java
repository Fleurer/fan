package com.googolmo.fanfou;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.googolmo.fanfou.api.Api;
import com.googolmo.fanfou.api.http.Session;
import com.googolmo.fanfou.app.OAuthActivity;
import com.googolmo.fanfou.data.Provider;

/**
 * User: googolmo
 * Date: 12-9-8
 * Time: 下午2:08
 */
public class BaseActivity extends SherlockFragmentActivity{

    protected Api mApi;
    protected Provider mProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mApi = ((BaseApplication) getApplication()).getApi();
        this.mProvider = ((BaseApplication) getApplication()).getProvider();

		if (this.mProvider.getCurrentUser() != null && !this.mProvider.getCurrentUserId().equals("")) {
			Session session = mProvider.getSession(mProvider.getCurrentUserId());
			if (!session.isAvailed()) {
				startLogin(this);
			}
			this.mApi.setOAuthToken(session);
		} else {
			startLogin(this);
		}
    }

    public Api getApi() {
        return this.mApi;
    }

    public Provider getProvider() {
        return this.mProvider;
    }

    public void startLogin(Context context) {
        Intent intent = new Intent(context, OAuthActivity.class);

        startActivityForResult(intent, Constants.REQUEST_CODE_LOGIN);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
