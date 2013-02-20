package com.googolmo.fanfou.utils.app;

import android.content.Context;

/**
 * User: googolmo
 * Date: 12-9-23
 * Time: 下午12:46
 */
public class Utils {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }

    public static int dip2px(Context context, int res) {
        float dpValue = context.getResources().getDimensionPixelSize(res);
        return dip2px(context, dpValue);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
