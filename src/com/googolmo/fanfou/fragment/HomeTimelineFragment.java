/*
 * Copyright (c) 2013. By @GoogolMo
 */

package com.googolmo.fanfou.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.googolmo.fanfou.Constants;
import com.googolmo.fanfou.R;
import com.googolmo.fanfou.adapter.TimelineAdapter;
import com.googolmo.fanfou.api.module.Status;
import com.googolmo.fanfou.app.ShareActivity;
import com.googolmo.fanfou.app.StatusActivity;
import com.googolmo.fanfou.data.DB;
import com.googolmo.fanfou.loader.DBLoader;
import com.googolmo.fanfou.loader.TimelineLoader;
import com.googolmo.fanfou.utils.NLog;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.ArrayList;
import java.util.List;

/**
 * User: GoogolMo
 * Date: 13-3-9
 * Time: 上午11:39
 */
public class HomeTimelineFragment extends BaseListFragment implements LoaderManager.LoaderCallbacks<List<Status>>{
    private static final String TAG = HomeTimelineFragment.class.getName();
//    private ProgressBar mPb;
    private TimelineAdapter mAdapter;
    private List<Status> mStatuses;


    private int mLoadIndex = 0;
    private int page = 1;
    ActionMode mActionMode;
    private int scrolledIndex;
    private int scrolledTop;
    private String mCurrentUserId;

    private Crouton crouton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStatuses = new ArrayList<Status>();
        mAdapter = new TimelineAdapter(getSherlockActivity(), mStatuses);
        mCurrentUserId = mProvider.getCurrentUserId();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this).startLoading();
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NLog.d(TAG, "onViewCreated");
        mStatuses.clear();



        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Status status = (Status) (adapterView.getAdapter().getItem(i));
                mActionMode = getSherlockActivity().startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        MenuItem reply = menu.add(0, Menu.FIRST, 0, R.string.reply);
                        reply.setIcon(R.drawable.ic_menu_reply);
                        reply.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

                        MenuItem repost = menu.add(0, Menu.FIRST + 1, 0, R.string.repost);
                        repost.setIcon(R.drawable.ic_menu_retweet);
                        repost.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case Menu.FIRST:
                                reply(status);
                                break;
                            case Menu.FIRST + 1:
                                repost(status);
                                break;
                        }
                        mode.finish();
                        mActionMode = null;
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                    }
                });
                return true;
            }
        });

        getListView().setOnScrollListener(new PauseOnScrollListener(true, true, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    scrolledIndex = getListView().getFirstVisiblePosition();
                    View v = getListView().getChildAt(0);
                    scrolledTop = (v == null) ? 0 : v.getTop();
                    NLog.d(TAG, "scrollTo:" + scrolledIndex + "," + scrolledTop);

                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            }
        }));

        setListShown(false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        } else {
            Status status = (Status)l.getAdapter().getItem(position);
            if (status.getRawid() == 0) {
                refresh(position);
            } else {
                Intent intent = new Intent(getSherlockActivity(), StatusActivity.class);
//                NLog.d(TAG, status.getJsonString());
                intent.putExtra(Constants.KEY_STATUS, status);
                getSherlockActivity().startActivity(intent);
            }

        }
    }


    private void reply(Status status) {

        Intent intent = new Intent(getSherlockActivity(), ShareActivity.class);
        intent.putExtra("mode", ShareActivity.TYPE_REPLY);
        intent.putExtra("id", status.getId());
        intent.putExtra("userid", status.getUser().getId());
        intent.putExtra("username", status.getUser().getName());
        startActivity(intent);
    }

    private void repost(Status status) {
        Intent intent = new Intent(getSherlockActivity(), ShareActivity.class);
        intent.putExtra("mode", ShareActivity.TYPE_REPOST);
        intent.putExtra("id", status.getId());
        intent.putExtra("username", status.getUser().getName());
        intent.putExtra("text", status.getText());
        startActivity(intent);
    }

    private static final int MENU_SHARE = 0;
    private static final int MENU_REFRESH = 2;
    private static final int MENU_INFO = 1;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        MenuItem refreshItem = menu.add(0, MENU_REFRESH, MENU_REFRESH, R.string.refresh);
        refreshItem.setIcon(R.drawable.ic_menu_refresh);
        refreshItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        String text = "";
        if (mProvider.getCurrentUser() != null && mProvider.getCurrentUser().getName() != null) {
            text = mProvider.getCurrentUser().getName();
        }

        MenuItem infoItem = menu.add(0, MENU_INFO, MENU_INFO, text);
        infoItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        MenuItem updateItem = menu.add(0, MENU_SHARE, MENU_SHARE, R.string.share);
        updateItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        updateItem.setIcon(R.drawable.ic_menu_edit);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SHARE:
                Intent intent = new Intent(getSherlockActivity(), ShareActivity.class);
                intent.setAction(Intent.ACTION_SEND);
                startActivityForResult(intent, Constants.REQUEST_CODE_SHARE);
                break;
            case MENU_REFRESH:
                refresh();
                break;
            case MENU_INFO:
                getListView().smoothScrollToPosition(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putInt("hl_si", this.scrolledIndex);
            outState.putInt("hl_st", this.scrolledTop);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        NLog.d(TAG, "onResume()");
    }

    @Override
    public void onStart() {
        super.onStart();
        NLog.d(TAG, "onStart()");
    }

    @Override
    public void onStop() {
        NLog.d(TAG, "onStop()");
        super.onStop();
        getProvider().setHomeTLScrollPostion(scrolledIndex, scrolledTop);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    private void loadData() {
    }



    @Override
    public Loader<List<Status>> onCreateLoader(int i, Bundle bundle) {
        NLog.d(TAG, "onCreateLoader");
        if (i == 0) {
            return new DBLoader(getActivity(), getProvider(), DB.STATUS_TYPE_HOMETLINE, null);
        }
//        else if (i == 1) {
//            return new TimelineLoader(getActivity(), getProvider(), getApi(), null);
//        }
        else{
            crouton = Crouton.makeText(getActivity(), R.string.now_loading, Style.INFO);
            crouton.show();
            String sinceId = null;
            String maxId = null;
            if (mLoadIndex == 0) {
                if (mStatuses.size() > mLoadIndex) {
                    sinceId = mStatuses.get(mLoadIndex).getId();
                }

            } else if (mLoadIndex == mStatuses.size() - 1) {
                maxId = mStatuses.get(mLoadIndex).getId();
            } else {
                maxId = mStatuses.get(mLoadIndex - 1).getId();
                sinceId = mStatuses.get(mLoadIndex + 1).getId();
            }
            return new TimelineLoader(getActivity(), getProvider(), getApi(), null, sinceId
                    , maxId, mProvider.getLoadCount(), 0, mStatuses, DB.STATUS_TYPE_HOMETLINE);
        }


    }

    @Override
    public void onLoadFinished(Loader<List<Status>> listLoader, List<Status> statuses) {
        NLog.d(TAG, "onLoadFinished");
        boolean isShow = true;
        boolean isSetPostion = false;
        int newPosition = 0;

        if (listLoader.getId() == 0) {
            if (statuses.size() < 1) {
                isShow = false;
                getLoaderManager().restartLoader(1, null, this);
            } else {
                this.mStatuses.clear();

                this.mStatuses.addAll(statuses);
                int p = getProvider().getHomeTLLoadmoreMark(mCurrentUserId);
                if (p > 0) {
                    Status s = new Status();
                    s.setRawid(0);
                    this.mStatuses.add(p, s);
//                    getProvider().addHomeTLLoadmoreMark(mCurrentUserId, p);
                }
                isSetPostion = true;
            }

        } else if (listLoader.getId() == 1) {
            if (statuses.size() > 0) {
                crouton = Crouton.makeText(getActivity(), getActivity().getString(R.string.load_result, statuses.size()), Style.INFO);
            } else {
                crouton = Crouton.makeText(getActivity(), R.string.result_no_new_statuses, Style.INFO);
            }

            crouton.show();
            if (mLoadIndex == 0) {
                newPosition = statuses.size() ;
                if (statuses.size() < getProvider().getLoadCount()) {
                    this.mStatuses.addAll(0, statuses);
                } else {
                    int p =getProvider().getHomeTLLoadmoreMark(mCurrentUserId);

                    Status s = new Status();
                    s.setRawid(0);
                    this.mStatuses.add(0, s);
                    this.mStatuses.addAll(0, statuses);
                    getProvider().addHomeTLLoadmoreMark(mCurrentUserId, statuses.size());

                    if (p != 0) {
                        for (int i = statuses.size() + 1; i < this.mStatuses.size(); i ++) {
                            this.mStatuses.remove(i);
                        }

                    }

                }

                if (this.mStatuses.size() > getProvider().getMaxCount()) {
                    for (int i = getProvider().getMaxCount(); i < this.mStatuses.size(); i ++) {
                        this.mStatuses.remove(i);
                    }
                }
            } else if(mLoadIndex == mStatuses.size() - 1) {
                this.mStatuses.addAll(statuses);
            } else {
                //TODO
//                this.mStatuses.remove(mLoadIndex);

                if (this.mStatuses.size() + statuses.size() >= getProvider().getMaxCount()) {
                    this.mStatuses.clear();
                    getProvider().addHomeTLLoadmoreMark(mCurrentUserId, 0);
                    this.mStatuses.addAll(this.mStatuses.subList(0, mLoadIndex));
                    this.mStatuses.addAll(statuses);
                } else {
                    if (mLoadIndex < this.mStatuses.size()) {
                        this.mStatuses.remove(mLoadIndex);
                        getProvider().addHomeTLLoadmoreMark(mCurrentUserId, 0);
                    }

                    this.mStatuses.addAll(mLoadIndex, statuses);
                    if (statuses.size() == getProvider().getLoadCount()) {
                        Status s = new Status();
                        s.setRawid(0);
                        this.mStatuses.add(mLoadIndex + statuses.size(), s);
                        getProvider().addHomeTLLoadmoreMark(mCurrentUserId, mLoadIndex + statuses.size());
                    }
                }


            }
            this.mStatuses.addAll(statuses);
            //TODO 保存到数据库中
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    getProvider().getDB().addStatuses(mStatuses, mProvider.getCurrentUserId(), DB.STATUS_TYPE_HOMETLINE);
                }
            }.start();
        }




        mAdapter.notifyDataSetChanged();

        if (newPosition > 0) {
            getListView().setSelection(newPosition);
//            getListView().smoothScrollToPosition(newPosition);
        }

        if (isShow) {
            if (isResumed()) {
                setListShown(true);
            } else {
                setListShownNoAnimation(true);
            }
        }
        if (isSetPostion) {
            this.scrolledIndex = getProvider().getHomeTLScrolledIndex();
            this.scrolledTop = getProvider().getHomeTLScrolledTop();
            getListView().setSelectionFromTop(this.scrolledIndex, this.scrolledTop);
        }
//        listLoader

    }

    @Override
    public void onLoaderReset(Loader loader) {
        NLog.d(TAG, "onLoaderReset");
//        this.mStatuses.clear();
//        mAdapter.notifyDataSetChanged();
    }


    private void refresh(int index) {
        mLoadIndex = index;
        getLoaderManager().restartLoader(1, null, this);
    }

    private void refresh() {


        NLog.d(TAG, "firstPosition=" + getListView().getFirstVisiblePosition());

        if (getListView().getLastVisiblePosition() == mStatuses.size() - 1) {
            mLoadIndex = getListView().getLastVisiblePosition();

        }
        else {
            mLoadIndex = 0;
        }
        getLoaderManager().restartLoader(1, null, this);
    }


}
