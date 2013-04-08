/*
 * Copyright (c) 2013. By @GoogolMo
 */

package com.googolmo.fanfou.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.googolmo.fanfou.R;
import com.googolmo.fanfou.api.model.Status;
import com.googolmo.fanfou.utils.app.DateUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;

/**
 * User: GoogolMo
 * Date: 13-3-9
 * Time: 上午11:42
 */
public class TimelineAdapter extends BaseAdapter{

    private Context context;
    private List<Status> statuses;
    private DisplayImageOptions headerOptions;

    public TimelineAdapter(Context context, List<Status> statuses) {
        this.context = context;
        this.statuses = statuses;
        headerOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading()
                .showStubImage(R.drawable.ic_header_default)
                .showImageForEmptyUri(R.drawable.ic_header_default)
                .showImageOnFail(R.drawable.ic_header_default)
                .cacheInMemory()
                .cacheOnDisc()
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
    }

    @Override
    public int getCount() {
        return statuses.size();
    }

    @Override
    public Object getItem(int i) {
        return statuses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return statuses.get(i).getRawid();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_status, null);
            holder.vStatus = (RelativeLayout) convertView.findViewById(R.id.list_status);
            holder.vLoadmore = (RelativeLayout) convertView.findViewById(R.id.list_loadmore);
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
        if (status.getRawid() == 0) {
            holder.vLoadmore.setVisibility(View.VISIBLE);
            holder.vStatus.setVisibility(View.GONE);
        } else {
            holder.vLoadmore.setVisibility(View.GONE);
            holder.vStatus.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(status.getUser().getProfile_image_url(), holder.header, headerOptions);
            holder.username.setText(status.getUser().getName());
            try {
                holder.status.setText(StringEscapeUtils.unescapeHtml4(status.getText()));
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
        }


        return convertView;
    }

    private static class ViewHolder {
        RelativeLayout vStatus;
        RelativeLayout vLoadmore;
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
