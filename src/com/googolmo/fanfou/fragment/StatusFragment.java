package com.googolmo.fanfou.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.googolmo.fanfou.api.module.Status;
import com.googolmo.fanfou.Constants;
import com.googolmo.fanfou.R;
import com.googolmo.fanfou.app.ShareActivity;
import com.googolmo.fanfou.app.ViewImageActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * User: googolmo
 * Date: 12-10-20
 * Time: 下午8:30
 */
public class StatusFragment extends BaseFragment implements ActionBarSherlock.OnOptionsItemSelectedListener {

    private static final String TAG = StatusFragment.class.getName();

    private RelativeLayout mUserInfo;
    private ImageView mAvatar;
    private TextView mUsername;
    private TextView mUserid;
    private TextView mTvStatus;
    private TextView mMore;
    private LinearLayout mReply;
    private TextView mTvReply;
    private ImageView mImage;

    private View mView;

    private Status mStatus;
    private DisplayImageOptions mDisplayOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.view_status, container, false);

        mUserInfo = (RelativeLayout) mView.findViewById(R.id.v_userinfo);
        mAvatar = (ImageView) mView.findViewById(R.id.avatar);
        mUsername = (TextView) mView.findViewById(R.id.username);
        mUserid = (TextView) mView.findViewById(R.id.id);
        mTvStatus = (TextView) mView.findViewById(R.id.tv_status);
        mMore = (TextView) mView.findViewById(R.id.tv_more);
        mReply = (LinearLayout) mView.findViewById(R.id.v_reply);
        mTvReply = (TextView) mView.findViewById(R.id.tv_reply);
        mImage = (ImageView) mView.findViewById(R.id.image);

		mImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getSherlockActivity(), ViewImageActivity.class);
				i.putExtra(Constants.KEY_URL, mStatus.getPhoto().getLargeurl());
				startActivityForResult(i, Constants.REQUEST_CODE_VIEWIMAGE);
			}
		});

        return mView;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String s = getArguments().getString(Constants.KEY_STATUS, "");
        if (s != null && !s.equals("")) {
            try {
//                mStatus = new Status(new JSONObject(s));
            } catch (JSONException e) {
                e.printStackTrace();
                mStatus = null;
            }
        } else {
            mStatus = null;
        }

        setHasOptionsMenu(true);

        mDisplayOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_header_default)
                .build();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mStatus != null) {
            if (mStatus.getUser() != null) {
                String url = mStatus.getUser().getProfile_image_url();
                if (mStatus.getUser().getProfile_image_url_large() != null
                        && !mStatus.getUser().getProfile_image_url_large().equals("")) {
                    url = mStatus.getUser().getProfile_image_url_large();
                }
                ImageLoader.getInstance().displayImage(url, mAvatar
                        , new DisplayImageOptions.Builder()
                        .showStubImage(R.drawable.ic_header_default)
                        .build());
            } else {
                mAvatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_header_default));
            }

            mUsername.setText(mStatus.getUser().getName());
            mUserid.setText(mStatus.getUser().getId());

            try {
                mTvStatus.setText(java.net.URLDecoder.decode(mStatus.getText(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                mTvStatus.setText(mStatus.getText());
            }

            String source = mStatus.getCreated_at() + "via: " + mStatus.getSource();
            mMore.setText(Html.fromHtml(source));
            mMore.setMovementMethod(LinkMovementMethod.getInstance());
//            mMore.setAutoLinkMask(Linkify.WEB_URLS);

            if (mStatus.getIn_reply_to_status_id() != null && !mStatus.getIn_reply_to_status_id().equals("")) {
                mReply.setVisibility(View.VISIBLE);
                mTvReply.setText(mStatus.getIn_reply_to_screen_name());
            } else {
                mReply.setVisibility(View.GONE);
            }

            if (mStatus.getPhoto() != null) {
                mImage.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(mStatus.getPhoto().getImageurl(), mImage);
            } else {
                mImage.setVisibility(View.GONE);
            }


        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem reply = menu.add(Menu.FIRST, Menu.FIRST, Menu.FIRST, R.string.reply);
        reply.setIcon(R.drawable.ic_menu_reply);
        reply.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        MenuItem repost = menu.add(Menu.FIRST, Menu.FIRST + 1, Menu.FIRST, R.string.repost);
        repost.setIcon(R.drawable.ic_menu_retweet);
        repost.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        MenuItem favorite = menu.add(Menu.FIRST, Menu.FIRST + 2, Menu.FIRST, R.string.favorite);
        favorite.setIcon(R.drawable.ic_menu_star);
        favorite.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST:
                reply();
                break;
            case Menu.FIRST + 1:
                repost();
                break;
            case Menu.FIRST + 2:
                favorite();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reply() {

        Intent intent = new Intent(getSherlockActivity(), ShareActivity.class);
        intent.putExtra("mode", ShareActivity.TYPE_REPLY);
        intent.putExtra("id", mStatus.getId());
        intent.putExtra("userid", mStatus.getUser().getId());
        intent.putExtra("username", mStatus.getUser().getName());
        startActivity(intent);
    }

    private void repost() {
        Intent intent = new Intent(getSherlockActivity(), ShareActivity.class);
        intent.putExtra("mode", ShareActivity.TYPE_REPOST);
        intent.putExtra("id", mStatus.getId());
        intent.putExtra("username", mStatus.getUser().getName());
        intent.putExtra("text", mStatus.getText());
        startActivity(intent);
    }

    private void favorite(){
        //TODO
    }
}
