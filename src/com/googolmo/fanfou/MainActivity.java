package com.googolmo.fanfou;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.googolmo.fanfou.api.http.Session;
import com.googolmo.fanfou.app.OAuthActivity;
import com.googolmo.fanfou.data.Provider;
import com.googolmo.fanfou.fragment.HomeTimelineFragment;
import com.googolmo.fanfou.fragment.MenuFragment;
import com.googolmo.fanfou.module.MenuModule;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends SlidingFragmentActivity{
    private static final String TAG = Constants.getTAG(MainActivity.class.getName());

    private ActionBar mActionBar;
//    private ViewPager mViewPager;
    private Fragment mContent;

    private Map<String, Fragment> mFragments;
    protected Provider mProvider;

//    TabsAdapter mAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActionBar = getSupportActionBar();
        mFragments = new HashMap<String, Fragment>();

        this.mProvider = ((BaseApplication) getApplication()).getProvider();
        if (this.mProvider.getCurrentUser() != null && !this.mProvider.getCurrentUserId().equals("")) {
            Session session = mProvider.getSession(mProvider.getCurrentUserId());
            if (!session.isAvailed()) {
                startLogin(this);
                this.finish();
                return;
            }
            ((BaseApplication) getApplication()).getApi().setOAuthToken(session);
        } else {
            startLogin(this);
            this.finish();
            return;
        }



        if (savedInstanceState != null) {
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        }



        if (mContent == null) {
            mContent = Fragment.instantiate(this, HomeTimelineFragment.class.getName(), null);
            mFragments.put(getString(R.string.home_timeline), mContent);

        }

        setContentView(R.layout.main);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_content, mContent)
                .commit();
        setBehindContentView(R.layout.menu_frame);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_frame, Fragment.instantiate(this, MenuFragment.class.getName()))
                .commit();

        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        mActionBar.setDisplayHomeAsUpEnabled(true);



        setupView();



        init();
    }

    private void setupView() {

//        mViewPager = (ViewPager) findViewById(R.id.viewpager);
//        mAdapter = new TabsAdapter(this, getSupportFragmentManager(), mActionBar, mViewPager);
//        mViewPager.setAdapter(mAdapter);
//        Bundle timelineBudle = new Bundle();
//        timelineBudle.putInt(Constants.KEY_HOME_TAB_MODE, Constants.KEY_HOME_TAB_MODE_HOME_TIMELINE);
//        mAdapter.addTab(TimeLineFragment.class, timelineBudle, getString(R.string.home_timeline), R.drawable.ic_tab_home);
//
//        Bundle mentionBundle = new Bundle();
//        mentionBundle.putInt(Constants.KEY_HOME_TAB_MODE, Constants.KEY_HOME_TAB_MODE_METIONS);
//        mAdapter.addTab(TimeLineFragment.class, mentionBundle, getString(R.string.mentions), R.drawable.ic_tab_mention);

    }

    private void init() {

    }

//    public void setCrouton(CharSequence text, Style style){
//        Crouton.makeText(this, text, style).show();
//    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "mContent", mContent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startLogin(Context context) {
        Intent intent = new Intent(context, OAuthActivity.class);

        startActivityForResult(intent, Constants.REQUEST_CODE_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_LOGIN) {
            if (requestCode == RESULT_OK) {
                //TODO 登录成功
                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_content, mContent)
                        .commit();

            } else {
                //TODO 登录失败
            }
        }
    }

    @Override
    protected void onDestroy() {
//        mFragmentList = null;
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }

    public void switchContent(MenuModule menuModule) {
        Fragment fragment = mFragments.get(menuModule.getTitle());
        if (fragment == null) {
            fragment = Fragment.instantiate(this, menuModule.getClsName(), menuModule.getBundle());
            mFragments.put(menuModule.getTitle(), fragment);

        }
        getSlidingMenu().showContent();
        mContent = fragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, mContent);
        ft.commit();

    }

}
