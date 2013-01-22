package com.googolmo.fanfou;

/**
 * User: googolmo
 * Date: 12-9-8
 * Time: 下午2:03
 */
public class Constants {
    public static final String TAG = "FanMo";
    public static final String CONSUMER_KEY = "d0cdaf18beb2156f8b41fdc02be86170";
    public static final String CONSUMER_SECRET = "d9d35304d9863609d9d22a4d693b0c8d";

    public static final String CALLBACK_URL= "mofan://callback_url";

    public static final boolean DEBUG = true;

    public static final String KEY_HOME_TAB_MODE = "com.googolmo.fanfou.homemode";

	public static final String KEY_URL = "url";

    public static final int KEY_HOME_TAB_MODE_HOME_TIMELINE = 1;
    public static final int KEY_HOME_TAB_MODE_METIONS = 2;
    public static final int KEY_HOME_TAB_MODE_DM = 3;
    public static final int KEY_HOME_TAB_MODE_PUBLIC_TIMELINE = 4;


    public static final int REQUEST_CODE_LOGIN = 101;
    public static final int REQUEST_CODE_SHARE = 102;
    public static final int REQUEST_CODE_GALLARY = 103;
    public static final int REQUEST_CODE_CAMERA = 104;
	public static final int REQUEST_CODE_VIEWIMAGE = 105;


    public static final int SHARE_TEXT_MAX_COUNT = 140;

    public static final String KEY_STATUS = "status";

    public static String getTAG(String tag) {
        return String.format("%1$s.%2$s", TAG, tag);
    }
}
