package com.googolmo.fanfou.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import com.googolmo.fanfou.api.Api;
import com.googolmo.fanfou.BaseApplication;
import com.googolmo.fanfou.api.FanfouException;
import com.googolmo.fanfou.data.Provider;

/**
 * User: googolmo
 * Date: 12-9-8
 * Time: 下午6:46
 */
public class BaseFragment extends SherlockFragment{
    protected Api mApi;
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

    public Api getApi() {
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
