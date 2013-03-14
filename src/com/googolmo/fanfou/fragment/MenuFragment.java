package com.googolmo.fanfou.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.googolmo.fanfou.Constants;
import com.googolmo.fanfou.model.MenuModel;
import com.googolmo.fanfou.utils.NLog;
import com.googolmo.fanfou.MainActivity;
import com.googolmo.fanfou.R;

import java.util.ArrayList;
import java.util.List;

/**
 * User: googolmo
 * Date: 13-1-13
 * Time: 上午11:50
 */
public class MenuFragment extends BaseFragment{
    private static final String TAG = MenuFragment.class.getName();

    private View mView;

    private ListView mListView;

    private List<MenuModel> mMenuList;
    private MenuListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.view_left_menu, container, false);
        mListView = (ListView) mView.findViewById(R.id.listview);
        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMenuList = getArguments().getParcelableArrayList(Constants.KEY_MENULIST);


        mAdapter = new MenuListAdapter();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MenuModel module = (MenuModel)(adapterView.getAdapter().getItem(i));
                NLog.d(TAG, "model=" + module.getTitle());
                switchFragment(module);
            }
        });

    }

    private void switchFragment(MenuModel menuModel) {
        if (getActivity() == null) {
            return;
        }
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity)getActivity();
            mainActivity.switchContent(menuModel);
        }

    }


    private class MenuListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mMenuList.size();
        }

        @Override
        public Object getItem(int i) {
            return mMenuList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_menu, null);
                viewHolder.content = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            MenuModel item = (MenuModel)getItem(position);
            viewHolder.content.setText(item.getTitle());
            return convertView;
        }

        private class ViewHolder {
            TextView content;
        }

    }


}
