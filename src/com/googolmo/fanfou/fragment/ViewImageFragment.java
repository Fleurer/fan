package com.googolmo.fanfou.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.googolmo.fanfou.utils.NLog;
import com.googolmo.fanfou.Constants;
import com.googolmo.fanfou.R;
import com.googolmo.fanfou.utils.app.DateUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.polites.android.GestureImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;

/**
 * User: googolmo
 * Date: 12-12-9
 * Time: 下午8:35
 */
public class ViewImageFragment extends BaseFragment {
    private static final String TAG = ViewImageFragment.class.getName();

    private GestureImageView mImageView;
    private ProgressBar mProgressBar;
    private View mView;

    private String mUrl;
    private Bitmap mBitmap;
    private ActionBar mActionBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.view_viewimage, container, false);
        mImageView = (GestureImageView) mView.findViewById(android.R.id.content);
        mProgressBar = (ProgressBar) mView.findViewById(android.R.id.progress);

        mProgressBar.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.GONE);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActionBar.isShowing()) {
                    mActionBar.hide();
                } else {
                    mActionBar.show();
                }
            }
        });

        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString(Constants.KEY_URL, "");
        mActionBar = getSherlockActivity().getSupportActionBar();
        mActionBar.setBackgroundDrawable(getSherlockActivity().getResources().getDrawable(R.drawable.abs__ab_bottom_solid_dark_holo));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .delayBeforeLoading(0)
                .build();

        ImageLoader.getInstance().displayImage(mUrl, mImageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted() {
            }

            @Override
            public void onLoadingFailed(FailReason failReason) {
                NLog.d(TAG, failReason.toString());
            }

            @Override
            public void onLoadingComplete(Bitmap bitmap) {
                mProgressBar.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
                mBitmap = bitmap;
            }

            @Override
            public void onLoadingCancelled() {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem saveItem = menu.add(0, Menu.FIRST, 0, R.string.save_image_to_disc);
        saveItem.setIcon(R.drawable.ic_action_download);
        saveItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST:
                saveBitmapToMedia(mUrl, mBitmap, DateUtils.getDateTimeString(DateUtils.IMAGE_FILE_NAME_PATTER));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveBitmapToMedia(final String uri, final Bitmap bitmap, final String title) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = ImageLoader.getInstance().getImageFile(getSherlockActivity(), uri, bitmap);
                String result = "failed";
                File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator + "Shmily");
                result = saveFileToMedia(getSherlockActivity(), file, dir, title, title);
                if (result == null) {
                    result = "failed";
                }
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            }
        }).run();


    }

    private String saveFileToMedia(Context context, File source, File dir, String title, String description) {

        Bitmap bitmap = BitmapFactory.decodeFile(source.getAbsoluteFile().toString());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File saveFile = new File(dir, title + ".jpg");
        if (saveFile.exists()) {
            File[] files = saveFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    String n = s.replace(s.substring(s.lastIndexOf(".") - 1, s.length()), "");
                    if (n.equalsIgnoreCase("title")) {
                        return true;
                    }
                    return false;
                }
            });
            if (files.length > 0) {
                title = title + " - " + files.length;
            }
            saveFile = new File(dir, title + ".jpg");
        }
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(saveFile), 8 * 1024);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)) {
                MediaScannerConnection.scanFile(context, new String[]{saveFile.getAbsolutePath()}, new String[]{"image/jpg"}, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String s, Uri uri) {

                    }
                });
                return saveFile.getAbsolutePath();
            }
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }
}
