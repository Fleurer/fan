package com.googolmo.fanfou.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.googolmo.fanfou.api.module.Status;
import com.googolmo.fanfou.utils.NLog;
import com.googolmo.fanfou.Constants;
import com.googolmo.fanfou.R;
import com.googolmo.fanfou.app.ShareActivity;
import com.googolmo.fanfou.app.StatusActivity;
import com.googolmo.fanfou.data.DB;
import com.googolmo.fanfou.utils.app.DateUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * User: googolmo
 * Date: 12-9-8
 * Time: 下午6:44
 */
public class TimeLineFragment extends BaseFragment {
    private static final String TAG = TimeLineFragment.class.getName();

    private ListView mListView;
    private PullToRefreshListView mPullListView;
    private ProgressBar mPb;
    private TimelineAdapter mAdapter;
    private int mTabMode;
    private List<Status> mStatuses;

//    private String newFirstid;
//    private String oldLastid;
//    private String newLastid;
//    private String oldFirstid;

    private int startIndex = -1;
    private int lastIndex = -1;
    private boolean loadFromDB = false;
    private int page = 1;
    ActionMode mActionMode;

//    public static TimeLineFragment newInstance(Bundle args) {
//        return new TimeLineFragment(args);
//    }
//
//    public TimeLineFragment(Bundle args) {
//        this.setArguments(args);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_timeline, container, false);
        mPullListView = (PullToRefreshListView) v.findViewById(R.id.listview);
        mListView = mPullListView.getRefreshableView();
        mPb = (ProgressBar) v.findViewById(R.id.progressbar);

        mListView.setVisibility(View.GONE);
        mPb.setVisibility(View.VISIBLE);
        mPullListView.setPullToRefreshEnabled(true);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new TimelineAdapter(getSherlockActivity());
        mTabMode = getArguments().getInt(Constants.KEY_HOME_TAB_MODE);
        mStatuses = new ArrayList<Status>();


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStatuses.clear();
        mAdapter.setStatuses(mStatuses);

        mListView.setAdapter(mAdapter);

        mPullListView.setMode(PullToRefreshBase.Mode.BOTH);

        mPullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                startIndex = 0;
                lastIndex = -1;
                refresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                startIndex = -1;
                lastIndex = mStatuses.size() - 1;
                refresh();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    mActionMode.finish();
                    mActionMode = null;
                } else {
                    Intent intent = new Intent(getSherlockActivity(), StatusActivity.class);
                    intent.putExtra(Constants.KEY_STATUS, ((Status) parent.getAdapter().getItem(position)).getJsonString());
                    getSherlockActivity().startActivity(intent);
                }


            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Status status = (Status)(adapterView.getAdapter().getItem(i));
                mActionMode = getSherlockActivity().startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        MenuItem reply = menu.add(0, Menu.FIRST, 0, R.string.reply);
                        reply.setIcon(R.drawable.ic_menu_reply);
                        reply.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

                        MenuItem repost = menu.add(0, Menu.FIRST + 1, 0, R.string.repost);
                        repost.setIcon(R.drawable.ic_menu_retweet);
                        repost.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
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

        mListView.setOnScrollListener(new PauseOnScrollListener(true, true));

        loadData();
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        MenuItem refreshItem = menu.add(Menu.FIRST, Menu.FIRST, Menu.FIRST, R.string.refresh);
        refreshItem.setIcon(R.drawable.ic_menu_refresh);
        refreshItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        String text = "";
        if (mProvider.getCurrentUser() != null && mProvider.getCurrentUser().getName() != null) {
            text = mProvider.getCurrentUser().getName();
        }

        MenuItem infoItem = menu.add(Menu.FIRST, Menu.FIRST + 1, Menu.FIRST, text);
        infoItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        MenuItem updateItem = menu.add(Menu.FIRST, Menu.FIRST + 2, Menu.FIRST, R.string.share);
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
            case Menu.FIRST + 2:
                Intent intent = new Intent(getSherlockActivity(), ShareActivity.class);
                intent.setAction(Intent.ACTION_SEND);
                startActivityForResult(intent, Constants.REQUEST_CODE_SHARE);
                break;
            case Menu.FIRST:
                startIndex = 0;
                lastIndex = -1;
                refresh();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        NLog.d(TAG, "onStop()");
        super.onStop();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case Constants.REQUEST_CODE_LOGIN:
//                if (resultCode == Activity.RESULT_OK) {
//
//                }
//                break;
//        }
//    }

    private void loadData() {
        LoadFromDBTask task;
        switch (mTabMode) {

            case Constants.KEY_HOME_TAB_MODE_HOME_TIMELINE:
                task = new LoadFromDBTask(DB.STATUS_TYPE_HOMETLINE);
                task.execute();
//                loadHomeTLFromDb();
                break;
            case Constants.KEY_HOME_TAB_MODE_METIONS:
                task = new LoadFromDBTask(DB.STATUS_TYPE_MENTIONS);
                task.execute();
//                loadMentionFromDb();
                break;
            case Constants.KEY_HOME_TAB_MODE_DM:
                break;
            case Constants.KEY_HOME_TAB_MODE_PUBLIC_TIMELINE:
                break;
        }
    }

//    private void loadHomeTLFromDb() {
//        mStatuses.addAll(mProvider.getDB().getStatuesByUser(mProvider.getCurrentUserId(), DB.STATUS_TYPE_HOMETLINE));
//        if (mStatuses.size() == 0) {
//            loadHomeTL(null, null, mProvider.getLoadCount(), 1);
//        } else {
////            oldFirstid = mStatuses.get(0).getId();
////            oldLastid = mStatuses.get(mStatuses.size() - 1).getId();
//            mPb.setVisibility(View.GONE);
//            mListView.setVisibility(View.VISIBLE);
//            mAdapter.notifyDataSetChanged();
//            loadFromDB = true;
//        }
//    }
//
//    private void loadMentionFromDb() {
//        mStatuses.addAll(mProvider.getDB().getStatuesByUser(mProvider.getCurrentUserId(), DB.STATUS_TYPE_MENTIONS));
//        if (mStatuses.size() == 0) {
//            loadMentions(null, null, mProvider.getLoadCount(), 1);
//        } else {
//            mPb.setVisibility(View.GONE);
//            mListView.setVisibility(View.VISIBLE);
//            mAdapter.notifyDataSetChanged();
//            loadFromDB = true;
//        }
//    }

    private class LoadFromDBTask extends AsyncTask<Void, Void, List<Status>> {
        private int mType;

        private LoadFromDBTask(int mType) {
            this.mType = mType;
        }

        @Override
        protected List<com.googolmo.fanfou.api.module.Status> doInBackground(Void... voids) {
            return mProvider.getDB().getStatuesByUser(mProvider.getCurrentUserId(), mType);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<com.googolmo.fanfou.api.module.Status> statuses) {
            super.onPostExecute(statuses);
            mPb.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            loadFromDB = true;
            if (statuses != null) {
                if (mType == DB.STATUS_TYPE_HOMETLINE) {
                    mStatuses.addAll(statuses);
                    if (mStatuses.size() == 0) {
                        loadHomeTL(null, null, mProvider.getLoadCount(), 1);
                    } else {

                        mAdapter.notifyDataSetChanged();

                    }
                } else if (mType == DB.STATUS_TYPE_MENTIONS) {
                    mStatuses.addAll(statuses);
                    if (mStatuses.size() == 0) {
                        loadMentions(null, null, mProvider.getLoadCount(), 1);
                    } else {
                        mAdapter.notifyDataSetChanged();

                    }
                }
            }
        }

    }


    private void refresh() {
        String since_id = null;
        String max_id = null;
        if (startIndex > -1) {
            if (mStatuses.size() > 0) {
                Status s = mStatuses.get(startIndex);
                since_id = s.getId();
            }
        }
        if (lastIndex > -1) {
            if (mStatuses.size() > 0) {
                max_id = mStatuses.get(lastIndex).getId();
            }
        }
        switch (mTabMode) {
            case Constants.KEY_HOME_TAB_MODE_HOME_TIMELINE:
                loadHomeTL(since_id, max_id, mProvider.getLoadCount(), page);
                break;
            case Constants.KEY_HOME_TAB_MODE_METIONS:
                loadMentions(since_id, max_id, mProvider.getLoadCount(), page);
        }

    }

//    private void refresh(String since_id, String max_id, int count, int page) {
//        loadHomeTL(since_id, max_id, count, page);
//    }

    private void loadHomeTL(String since_id, String max_id, int count, int page) {
        mApi.getHomeTimeline(null, since_id, max_id, count, page, false, mJsonHandler);
    }

    private void loadMentions(String since_id, String max_id, int count, int page) {
        mApi.getMentions(since_id, max_id, count, page, false, mJsonHandler);
    }

    private JsonHttpResponseHandler mJsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(JSONArray response) {
            NLog.d(TAG, response.toString());
            super.onSuccess(response);

            if (loadFromDB && lastIndex < 0) {
                if (response.length() == mProvider.getLoadCount()) {
                    mStatuses.clear();
                }

                loadFromDB = false;
            }
//            List<Status> ss = new ArrayList<Status>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jb = response.optJSONObject(i);
//                    NLog.d(TAG, jb.toString());
                Status s = new Status(jb);
                if (startIndex > -1) {
                    mStatuses.add(startIndex + i, s);

                } else if (lastIndex > -1) {
                    mStatuses.add(s);
                }

            }
            mPb.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            if (mStatuses.size() > 200) {
                if (lastIndex > -1) {
                    mStatuses = mStatuses.subList(lastIndex - 199, lastIndex);
                } else {
                    mStatuses = mStatuses.subList(0, 199);
                }

            }
            mPullListView.onRefreshComplete();
            mAdapter.notifyDataSetChanged();
            if (lastIndex == -1) {
                mListView.setSelection(response.length());
            }

            if (mStatuses != null && mStatuses.size() > 0) {
                switch (mTabMode) {
                    case Constants.KEY_HOME_TAB_MODE_HOME_TIMELINE:
                        mProvider.getDB().addStatuses(mStatuses, mProvider.getCurrentUserId(),
                                DB.STATUS_TYPE_HOMETLINE);
                        break;
                    case Constants.KEY_HOME_TAB_MODE_METIONS:
                        mProvider.getDB().addStatuses(mStatuses, mProvider.getCurrentUserId(),
                                DB.STATUS_TYPE_MENTIONS);
                }


            }

//            lastIndex = -1;
//            startIndex = -1;
        }

        @Override
        public void onFailure(Throwable e, JSONObject errorResponse) {
            super.onFailure(e, errorResponse);
            Toast.makeText(getActivity(), errorResponse.toString(), Toast.LENGTH_LONG).show();
            mPullListView.onRefreshComplete();
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            Toast.makeText(getActivity(), content, Toast.LENGTH_LONG).show();
            mPullListView.onRefreshComplete();
        }
    };


    private class TimelineAdapter extends BaseAdapter {
        private Context context;
        private List<Status> statuses;
        DisplayImageOptions headerOptions;

        public TimelineAdapter(Context context) {
            this.context = context;
            headerOptions = new DisplayImageOptions.Builder()
                    .resetViewBeforeLoading(true)
                    .showStubImage(R.drawable.ic_header_default)
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .bitmapConfig(Bitmap.Config.ARGB_8888)
                    .build();
        }

        public void setStatuses(List<Status> statuses) {
            this.statuses = statuses;
        }

        @Override
        public int getCount() {
            return statuses.size();
        }

        @Override
        public Object getItem(int position) {
            return statuses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return statuses.get(position).getRwaid();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_status, null);
                holder.header = (ImageView) convertView.findViewById(R.id.avatar);
                holder.username = (TextView) convertView.findViewById(R.id.username);
                holder.status = (TextView) convertView.findViewById(R.id.tv_status);
                holder.source = (TextView) convertView.findViewById(R.id.tv_source);
                holder.hasLocation = (ImageView) convertView.findViewById(R.id.iv_has_location);
                holder.hasMedia = (ImageView) convertView.findViewById(R.id.iv_has_photo);
                holder.isProtected = (ImageView) convertView.findViewById(R.id.iv_protected);
                holder.fav = (ImageView) convertView.findViewById(R.id.iv_fav);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //TODO
            Status status = (Status) getItem(position);

            ImageLoader.getInstance().displayImage(status.getUser().getProfile_image_url(), holder.header, headerOptions);
            holder.username.setText(status.getUser().getName());
            try {
                holder.status.setText(java.net.URLDecoder.decode(status.getText(), "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
                holder.status.setText(status.getText());
            }
            String source = "";
            if (!status.getCreated_at().equals("")) {
                source = DateUtils.utc2Local(status.getCreated_at(), DateUtils.LOCAL_TIME_PATTER);
            }

            holder.source.setText(source);
            if (status.getUser().isProtected()) {
                holder.isProtected.setVisibility(View.VISIBLE);
            } else {
                holder.isProtected.setVisibility(View.GONE);
            }
            if (status.getPhoto() != null) {
                holder.hasMedia.setVisibility(View.VISIBLE);
            } else {
                holder.hasMedia.setVisibility(View.GONE);
            }

            if (status.getLocation() != null && !status.getLocation().equals("")
                    && !status.getLocation().equalsIgnoreCase(status.getUser().getLocation())) {
                holder.hasLocation.setVisibility(View.VISIBLE);
            } else {
                holder.hasLocation.setVisibility(View.GONE);
            }
            if (status.isFavorited()) {
                holder.fav.setVisibility(View.VISIBLE);
            } else {
                holder.fav.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView header;
        ImageView hasLocation;
        ImageView hasMedia;
        ImageView isProtected;
        ImageView fav;
        TextView username;
        TextView status;
        TextView source;
    }
}
