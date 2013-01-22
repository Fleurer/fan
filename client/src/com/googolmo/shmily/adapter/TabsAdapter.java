package com.googolmo.shmily.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;

import java.util.ArrayList;

/**
 * User: googolmo
 * Date: 12-10-21
 * Time: 下午2:23
 */
public class TabsAdapter extends FragmentStatePagerAdapter implements ActionBar.TabListener, ViewPager.OnPageChangeListener{

    private final Context mContext;
    private final ActionBar mActionBar;
    private ViewPager mViewPager;

    private ArrayList<TabInfo> mTabsInfo;


    public TabsAdapter(Context context, FragmentManager fm, ActionBar actionBar, ViewPager viewPager) {
        super(fm);
        this.mContext = context;
        this.mActionBar = actionBar;
        this.mViewPager = viewPager;
        this.mViewPager.setOnPageChangeListener(this);
        mTabsInfo = new ArrayList<TabInfo>();
        mTabsInfo.clear();
    }

    public void addTab(Class<? extends Fragment> cls, Bundle args, String name, Integer icon) {

        if (cls == null) throw new IllegalArgumentException("Fragment cannot be null!");
        if (name == null && icon == null)
            throw new IllegalArgumentException("You must specify a name or icon for this tab!");
        mTabsInfo.add(new TabInfo(name, icon, cls, args));

        ActionBar.Tab tab = mActionBar.newTab().setText(name).setIcon(icon).setTabListener(this);
        mActionBar.addTab(tab);

        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo tabInfo = mTabsInfo.get(position);
        final Fragment fragment = Fragment.instantiate(this.mContext, tabInfo.cls.getName(), tabInfo.args);
        return fragment;
    }

    @Override
    public int getCount() {
        return mTabsInfo.size();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        this.mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
//        Toast.makeText(mContext, "select at " + position, Toast.LENGTH_LONG).show();
        mActionBar.getTabAt(position).select();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }




    private class TabInfo {

        private final String name;
        private final Integer icon;
        private final Class<? extends Fragment> cls;
        private final Bundle args;

        public TabInfo(String name, Integer icon, Class<? extends Fragment> cls, Bundle args) {
            if (name == null && icon == null)
                throw new IllegalArgumentException("You must specify a name or icon for this tab!");
            this.name = name;
            this.icon = icon;
            this.cls = cls;
            this.args = args;

        }
    }
}
