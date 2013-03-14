package com.googolmo.fanfou;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.googolmo.fanfou.api.http.Session;
import com.googolmo.fanfou.app.OAuthActivity;
import com.googolmo.fanfou.data.Provider;
import com.googolmo.fanfou.fragment.HomeTimelineFragment;
import com.googolmo.fanfou.fragment.MentionFragment;
import com.googolmo.fanfou.fragment.MenuFragment;
import com.googolmo.fanfou.model.MenuModel;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import de.keyboardsurfer.android.widget.crouton.Crouton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends SlidingFragmentActivity{
    private static final String TAG = Constants.getTAG(MainActivity.class.getName());

    private static final String KEY_VIEWPAGER_POSITION = "viewpager_position";

    private ActionBar mActionBar;
    private ViewPager mViewPager;
    private int mViewPagerPostion;

    private Map<String, Fragment> mFragments;
    protected Provider mProvider;

//    TabsAdapter mAdapter;
    private MainAdapter mAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        mActionBar = getSupportActionBar();

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

        this.mViewPager = (ViewPager) findViewById(R.id.viewpager);


        List<MenuModel> fregments = new ArrayList<MenuModel>();
        fregments.add(new MenuModel(0, getString(R.string.home_timeline), HomeTimelineFragment.class.getName(), null));
        fregments.add(new MenuModel(1, getString(R.string.mentions), MentionFragment.class.getName(), null));


        this.mAdapter = new MainAdapter(getSupportFragmentManager(), this, fregments);

        this.mViewPager.setAdapter(this.mAdapter);

        this.mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                        break;
                    default:
                        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        setBehindContentView(R.layout.menu_frame);
        Bundle bundle  = new Bundle();
        bundle.putParcelableArrayList(Constants.KEY_MENULIST, (ArrayList<? extends Parcelable>) fregments);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_frame, Fragment.instantiate(this, MenuFragment.class.getName(), bundle))
                .commit();

        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        mActionBar.setDisplayHomeAsUpEnabled(true);


        if (savedInstanceState != null) {

                mViewPagerPostion = savedInstanceState.getInt(KEY_VIEWPAGER_POSITION, 0);

        }

        mViewPager.setCurrentItem(mViewPagerPostion);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            outState.putInt(KEY_VIEWPAGER_POSITION, mViewPagerPostion);
        }
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

    public void switchContent(MenuModel menuModel) {
        getSlidingMenu().showContent();
        mViewPager.setCurrentItem(menuModel.getId());

    }

    private class MainAdapter extends FragmentStatePagerAdapter {
        private List<MenuModel> menuModels;
        private Context context;

        public MainAdapter(FragmentManager fm, Context context, List<MenuModel> models) {
            super(fm);
            this.menuModels = models;
            this.context = context;
        }

        @Override
        public Fragment getItem(int i) {
            MenuModel model = menuModels.get(i);

            return Fragment.instantiate(context, model.getClsName(), model.getBundle());
        }

        @Override
        public int getCount() {
            return menuModels.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
    }

}
