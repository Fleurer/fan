package com.googolmo.fanfou.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.view.MenuItem;
import com.googolmo.fanfou.BaseActivity;
import com.googolmo.fanfou.R;
import com.googolmo.fanfou.fragment.OAuthFragment;

/**
 * User: googolmo
 * Date: 12-9-8
 * Time: 下午2:08
 */
public class OAuthActivity extends BaseActivity{
    private static final String TAG = OAuthActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_oauth);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment fragment = Fragment.instantiate(this, OAuthFragment.class.getName(), getIntent().getExtras());
		ft.replace(android.R.id.content, fragment);
		ft.commitAllowingStateLoss();
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
