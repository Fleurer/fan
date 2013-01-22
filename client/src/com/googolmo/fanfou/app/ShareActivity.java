package com.googolmo.fanfou.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.googolmo.fanfouApi.ShmilyException;
import com.googolmo.fanfouApi.utils.NLog;
import com.googolmo.fanfou.BaseActivity;
import com.googolmo.fanfou.Constants;
import com.googolmo.fanfou.R;
import com.googolmo.utils.app.ExternalStorageUtils;
import com.googolmo.utils.app.IntentUtils;
import com.googolmo.utils.graphics.BitmapUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * User: googolmo
 * Date: 12-9-9
 * Time: 下午7:09
 */
public class ShareActivity extends BaseActivity{
    private static final String TAG = ShareActivity.class.getName();

    public static final int TYPE_NONE= 100;
    public static final int TYPE_REPLY = 101;
    public static final int TYPE_REPOST = 102;

    ActionBarSherlock mSherlock;

    private String mShareText;
    private String mReplyId;
    private String mRepostId;
    private String mLocaton;
    private Uri mUri;
    private Bitmap mBitmap;
    private File mTempImage;


    private JsonHttpResponseHandler mHandler;

    private EditText mStatusText;
    private Dialog mDialog;
    private ActionBar mActionBar;
    private TextView mLeft;
    private ImageView mImagePreview;

    private String mText;
    private String mId;
    private String mName;
    private String mReplyUserid;
    private int mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSherlock = ActionBarSherlock.wrap(this);
        mSherlock.setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);

        setContentView(R.layout.act_share);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        mTempImage = new File(ExternalStorageUtils.getExternalFilesDir(getPackageName().toString(), "tmp"), "tem.jpg");

        setupView();
        init();
    }

    private void setupView() {
        mStatusText = (EditText) findViewById(R.id.et_status);
        mImagePreview = (ImageView) findViewById(R.id.image);
        mLeft = (TextView) findViewById(R.id.tv_left);

        mImagePreview.setVisibility(View.GONE);

        mStatusText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setLeftText();
            }
        });

    }

    private void init() {
        mHandler = new UpdateHandler();
        mShareText = "";
//        mReplyId = "";
//        mRepostId = "";
        mMode = getIntent().getIntExtra("mode", TYPE_NONE);


        if (getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_SEND)) {
            String shareTitle = "";
            String shareText = "";
            shareText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            shareTitle = getIntent().getStringExtra(Intent.EXTRA_TITLE);
            if (shareText == null) {
                shareText = "";
            }
            if (shareTitle == null) {
                shareTitle = "";
            }
            mShareText = String.format("%1$s %2$s", shareTitle, shareText).trim();

            mUri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
            if (mUri != null) {
                loadImage(mUri);
            }
        }

        mId = getIntent().getStringExtra("id");
        mText = getIntent().getStringExtra("text");
        mName = getIntent().getStringExtra("username");
        if (mMode == TYPE_REPLY) {
            if (mId != null) {
                mReplyId = mId;
            }
            if (mName != null) {
                mShareText = "@" + mName + " ";
            }
            mReplyUserid = getIntent().getStringExtra("userid");
        } else if (mMode == TYPE_REPOST) {
            if (mId != null) {
                mRepostId = mId;
            }
            if (mText != null && mName != null) {
                mShareText = " 转 @" + mName + " " + mText;
            }
            mStatusText.setSelection(0);

        }

        mStatusText.setText(mShareText);

        if (mMode == TYPE_REPLY) {
            mStatusText.setSelection(mShareText.length());
        } else if (mMode == TYPE_REPOST) {
            mStatusText.setSelection(0);
        }

        setLeftText();


    }

    private void setLeftText() {
        int count = Constants.SHARE_TEXT_MAX_COUNT - mStatusText.getText().length();
        mLeft.setText(String.valueOf(count));
        if (count >= 20) {
            mLeft.setTextColor(Color.BLACK);
        } else {
            mLeft.setTextColor(Color.RED);
        }

    }

    private void loadImage(Uri uri) {
        mBitmap = BitmapUtils.scale(getContentResolver(), uri, 1080, 1080);
        setImage(mBitmap);

    }

    private void setImage(Bitmap bitmap) {
        if (bitmap != null) {
            mImagePreview.setImageBitmap(mBitmap);
            mImagePreview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        NLog.d(TAG, "onActivityResult->" + "requestCode=" + requestCode + ", resultCode=" + resultCode);

        switch (requestCode) {
            case Constants.REQUEST_CODE_LOGIN:
                if (resultCode == RESULT_OK) {
                    update();
                }
                break;
            case Constants.REQUEST_CODE_GALLARY:
                NLog.d(TAG, "REQUEST_GALLARY");
                if (resultCode == RESULT_OK) {
                    mUri = data.getData();
                    if (mUri != null) {
                        loadImage(mUri);
                    }
                }

                break;
            case Constants.REQUEST_CODE_CAMERA:
                NLog.d(TAG, "REQUEST_CAMERA");
                if (resultCode == RESULT_OK) {
                    try {
                        mUri = data.getData();
                        loadImage(mUri);
//                        mBitmap = (Bitmap) data.getParcelableExtra("data");
//                        setImage(mBitmap);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        if (mTempImage == null) {
                            mTempImage = new File(ExternalStorageUtils.getExternalFilesDir(getPackageName().toString(), "tmp"), "tem.jpg");
                        }
                        mUri = Uri.fromFile(mTempImage);
                        loadImage(mUri);
                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem cameraItem = menu.add(Menu.FIRST, Menu.FIRST, Menu.FIRST, R.string.camera);
        cameraItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        cameraItem.setIcon(R.drawable.ic_device_access_camera);

        MenuItem gallaryItem = menu.add(Menu.FIRST, Menu.FIRST + 1, Menu.FIRST, R.string.gallary);
        gallaryItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        gallaryItem.setIcon(R.drawable.ic_content_picture);

        MenuItem updateItem = menu.add(Menu.FIRST, Menu.FIRST + 2, Menu.FIRST, R.string.update);
        updateItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        updateItem.setIcon(R.drawable.ic_menu_send);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST + 2:
                update();
                break;
            case Menu.FIRST + 1:
                startGallary();
                break;
            case Menu.FIRST:
                startCamera();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void update() {
        if (getProvider().getCurrentUser() == null) {
            startLogin(ShareActivity.this);
        } else {
            mShareText = mStatusText.getText().toString();
            if (mShareText.length() <= 0 && mBitmap != null) {
                Toast.makeText(ShareActivity.this, R.string.error_text_empty, Toast.LENGTH_SHORT).show();
            } else if (mShareText.length() > 140) {
                Toast.makeText(ShareActivity.this, R.string.error_text_length, Toast.LENGTH_SHORT).show();
            } else {
                if (mBitmap != null) {
                    try {
                        if (mShareText.trim().length() == 0) {
                            mShareText = getString(R.string.upload_a_new_image);
                        }
                        InputStream input = getContentResolver().openInputStream(mUri);
                        getApi().upload(input, mShareText, mLocaton, true, mHandler);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    getApi().update(mShareText, mReplyId, mReplyUserid, mRepostId, mLocaton, true, mHandler);
                }
            }

        }


    }

    private void startGallary() {
        try {
            final Intent intent = IntentUtils.getPhotoPickIntent();

            startActivityForResult(intent, Constants.REQUEST_CODE_GALLARY);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ShareActivity.this, R.string.error_no_gallary, Toast.LENGTH_SHORT).show();
        }

    }

    private void startCamera() {
        try {
            final Intent i = IntentUtils.getTakePickIntent(mTempImage);
            startActivityForResult(i, Constants.REQUEST_CODE_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ShareActivity.this, R.string.error_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    private void clearDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = null;
    }

    private void showProgress() {
        clearDialog();
        mDialog = new ProgressDialog(ShareActivity.this);
        ((ProgressDialog) mDialog).setMessage(getString(R.string.updating));
        mDialog.setCancelable(false);
        mDialog.show();
    }


    private class UpdateHandler extends JsonHttpResponseHandler {

        @Override
        public void onStart() {
            super.onStart();
            showProgress();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            clearDialog();
        }

        @Override
        public void onSuccess(JSONObject response) {
            super.onSuccess(response);
            NLog.d(TAG, response.toString());
            Toast.makeText(ShareActivity.this, R.string.update_success, Toast.LENGTH_SHORT).show();

            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onFailure(Throwable e, JSONObject errorResponse) {
            super.onFailure(e, errorResponse);
            e.printStackTrace();
            NLog.e(TAG, errorResponse.toString());
            if (errorResponse != null) {
                ShmilyException ex = new ShmilyException(errorResponse);
                Toast.makeText(ShareActivity.this, ex.getError(), Toast.LENGTH_LONG).show();
            }


        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            NLog.e(TAG, content);
            ShmilyException ex = new ShmilyException(error);
            Toast.makeText(ShareActivity.this, ex.getError(), Toast.LENGTH_LONG).show();
        }


    }
}
