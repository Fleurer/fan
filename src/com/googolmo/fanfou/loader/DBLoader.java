/*
 * Copyright (c) 2013. By @GoogolMo
 */

package com.googolmo.fanfou.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.googolmo.fanfou.api.module.Status;
import com.googolmo.fanfou.data.DB;
import com.googolmo.fanfou.data.Provider;
import com.googolmo.fanfou.utils.NLog;

import java.util.List;

/**
 * User: GoogolMo
 * Date: 13-3-9
 * Time: 下午2:33
 */
public class DBLoader extends AsyncTaskLoader<List<Status>> {
    private static final String TAG = DBLoader.class.getName();
    private Provider mProvider;
    private int mType;
    private String mUserId;
    private List<Status> mData;

    public DBLoader(Context context, Provider provider, int type, String userId) {
        super(context);
        this.mProvider = provider;
        this.mType = type;
        this.mUserId = userId;

    }

    @Override
    public List<Status> loadInBackground() {
        NLog.d(TAG, "loadInBackground");
        if (mUserId == null) {
            this.mUserId = mProvider.getCurrentUserId();
        }
        mData = mProvider.getDB().getStatuesByUser(this.mUserId, mType);
        return mData;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        NLog.d(TAG, "onStartLoading");
        if (mData == null) {
            forceLoad();
        }

    }

    @Override
    public void onCanceled(List<Status> data) {
        super.onCanceled(data);
        NLog.d(TAG, "onCanceled");
//        cancelLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        NLog.d(TAG, "onStopLoading");
//        stopLoading();
        cancelLoad();
    }

    //    @Override
//    public void deliverResult(List<Status> data) {
//        super.deliverResult(data);
//    }
}

