package com.googolmo.fanfou.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.googolmo.fanfouApi.http.AccessToken;
import com.googolmo.fanfouApi.module.User;
import com.googolmo.fanfouApi.utils.NLog;
import com.googolmo.fanfou.R;

/**
 * User: googolmo
 * Date: 12-9-8
 * Time: 下午4:12
 */
public class Provider {
    private static final String TAG = Provider.class.getName();

    private Context mContext;
    private DB db;

    public Provider(Context context) {
        this.mContext = context;
        db = new DB(context);
    }

//    public void setToken(Token token) {
//        setToken(token, getCurrentUserId());
//    }

    public void setToken(AccessToken token, String userId) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(userId + "_token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("at", token.getToken());
        editor.putString("ats", token.getTokenSecret());
        editor.commit();
    }

    public AccessToken getToken() {
        return getToken(getCurrentUserId());
    }

    public AccessToken getToken(String userId) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(userId + "_token", Context.MODE_PRIVATE);
        AccessToken token = new AccessToken(sharedPreferences.getString("at", ""),
                sharedPreferences.getString("ats", ""));
        return token;
    }

    public void setNewTLid(String userId, String id) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(userId + "_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ni", id);
        editor.commit();
    }

    public String getNewTLid(String userId) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(userId + "_data", Context.MODE_PRIVATE);
        return sharedPreferences.getString("ni", "");
    }

    public void setOldTLid(String userId, String id) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(userId + "_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("oi", id);
        editor.commit();
    }

    public String getOldTLid(String userId) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(userId + "_data", Context.MODE_PRIVATE);
        return sharedPreferences.getString("oi", "");
    }

    /**
     * 获得当前用户ID
     * @return
     */
    public String getCurrentUserId() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("n", Context.MODE_PRIVATE);
        return sharedPreferences.getString("cuid", "");
    }

    /**
     * 获得当前账户
     * @return
     */
    public User getCurrentUser() {
        String id = getCurrentUserId();
        NLog.d(TAG, "id=" + id);
        if (id != null && !id.equals("")) {
            return db.getUser(id);
        } else {
            return null;
        }

    }

    /**
     * 设置当前账户ID
     * @param id
     */
    public void setCurrentUserId(String id) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("n", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cuid", id);
        editor.commit();
    }

    public DB getDB() {
        return this.db;
    }

    public int getLoadCount() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sp.getInt(mContext.getString(R.string.sp_load_count), 20);
    }

}
