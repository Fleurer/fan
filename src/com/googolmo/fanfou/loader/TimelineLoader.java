/*
 * Copyright (c) 2013. By @GoogolMo
 */

package com.googolmo.fanfou.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.googolmo.fanfou.api.Api;
import com.googolmo.fanfou.api.FanfouException;
import com.googolmo.fanfou.api.model.Status;
import com.googolmo.fanfou.data.DB;
import com.googolmo.fanfou.data.Provider;
import com.googolmo.fanfou.utils.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * User: GoogolMo
 * Date: 13-3-9
 * Time: 下午2:45
 */
public class TimelineLoader extends AsyncTaskLoader<List<Status>> {
    private Provider mProvider;
    private Api mApi;
    private String mUserId;
    private List<Status> mData;
    private int mType;
    private String mSinceId;
    private String maxId;
    private int mCount;
    private int mPage;

    public TimelineLoader(Context context, Provider provider, Api api, String userId, String sinceId
            , String maxId, int count, int page, List<Status> data, int type) {
        super(context);
        this.mProvider = provider;
        this.mApi = api;
        this.mUserId = userId;
        this.mSinceId = sinceId;
        this.maxId = maxId;
        this.mCount = count;
        this.mPage = page;
//        this.mData = data;
        this.mType = type;
    }

    @Override
    public List<Status> loadInBackground() {
        try {
            if (mUserId == null) {
                mUserId = mProvider.getCurrentUserId();
            }
            mData = new ArrayList<Status>();
            if (mType == DB.STATUS_TYPE_HOMETLINE) {
                mData.addAll(mApi.getHomeTimeline(mUserId, mSinceId, maxId, mCount, mPage, false));
            } else {
                mData.addAll(mApi.getMentions(mSinceId, maxId, mCount, mPage, false));
            }

            return mData;
        } catch (FanfouException e) {
            e.printStackTrace();
            ErrorHandler.handlerError(getContext(), e, ErrorHandler.ShowType.TOAST, null);
        }
        return null;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mData == null) {
//            forceLoad();
        }

    }

    @Override
    public void onCanceled(List<Status> data) {
        super.onCanceled(data);
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }
}
