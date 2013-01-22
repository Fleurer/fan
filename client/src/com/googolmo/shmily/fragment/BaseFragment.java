package com.googolmo.shmily.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import com.googolmo.fanfouApi.Shmily;
import com.googolmo.shmily.BaseApplication;
import com.googolmo.shmily.data.Provider;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * User: googolmo
 * Date: 12-9-8
 * Time: 下午6:46
 */
public class BaseFragment extends SherlockFragment{
    protected Shmily mApi;
    protected Provider mProvider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mApi = ((BaseApplication) getSherlockActivity().getApplication()).getApi();
        this.mProvider = ((BaseApplication) getSherlockActivity().getApplication()).getProvider();
    }

    public Shmily getApi() {
        return this.mApi;
    }

    public Provider getProvider() {
        return this.mProvider;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
