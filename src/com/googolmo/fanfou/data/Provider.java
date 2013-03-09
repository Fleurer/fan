package com.googolmo.fanfou.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.googolmo.fanfou.api.http.Session;
import com.googolmo.fanfou.api.module.User;
import com.googolmo.fanfou.utils.NLog;
import com.googolmo.fanfou.R;

import java.util.HashSet;
import java.util.Set;

/**
 * User: googolmo
 * Date: 12-9-8
 * Time: 下午4:12
 */
public class Provider {
    private static final String TAG = Provider.class.getName();

    private Context mContext;
    private DB db;
    private Gson gson;

    public Provider(Context context, Gson gson) {
        this.mContext = context;
        this.gson = gson;
        db = new DB(context, gson);
    }

    public void addSession(Session s) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sessions", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getUserString(s.getUserId(), "at"), s.getToken());
        editor.putString(getUserString(s.getUserId(), "ats"), s.getTokenSecret());
        addUserId(s.getUserId());
        editor.commit();
//        setCurrentUserId(s.getUserId());
    }

    public Session getToken() {
        return getSession(getCurrentUserId());
    }

    private String getUserString(String userId, String originString) {
        return userId + "_" + originString;
    }

    public Session getSession(String userId) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sessions", Context.MODE_PRIVATE);
        Session s = new Session(userId, sharedPreferences.getString(getUserString(userId ,"at"), null),
                sharedPreferences.getString(getUserString(userId, "ats"), ""));
        return s;
    }

    public void setNewTLid(String userId, String id) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sessions", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getUserString(userId, "ni"), id);
        editor.commit();
    }

    public String getNewTLid(String userId) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sessions", Context.MODE_PRIVATE);
        return sharedPreferences.getString(getUserString(userId, "ni"), "");
    }

    public void setOldTLid(String userId, String id) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sessions", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getUserString(userId, "oi"), id);
        editor.commit();
    }

    public String getOldTLid(String userId) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sessions", Context.MODE_PRIVATE);
        return sharedPreferences.getString(getUserString(userId, "oi"), "");
    }

    /**
     * 获得当前用户ID
     * @return
     */
    public String getCurrentUserId() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sessions", Context.MODE_PRIVATE);
        return sharedPreferences.getString("cuid", null);
    }

    /**
     * 从持久化存储中获得保存的用户ID集合
     * @return
     */
    public Set<String> getUserIds() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sessions", Context.MODE_PRIVATE);
        String users = sharedPreferences.getString("uss", null);
        Set<String> userIdSet = new HashSet<String>();
        if (users != null) {
            String[] s = users.split("&");
            for (String id : s) {
                userIdSet.add(id);
            }
        }
        return userIdSet;
    }

    public void addUserId(String userId) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sessions", Context.MODE_PRIVATE);
        String users = sharedPreferences.getString("uss", "");
        Set<String> userIds = getUserIds();

        if (!userIds.contains(userId)) {
            StringBuilder builder = new StringBuilder();
            for (String id: userIds) {
                builder.append(id);
                builder.append("&");
            }
            builder.append(userId);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("uss", builder.toString());
        }
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
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sessions", Context.MODE_PRIVATE);
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

    public int getMaxCount() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sp.getInt(mContext.getString(R.string.sp_load_count), 400);
    }

    public void setHomeTLScrollPostion(int scolledIndex, int scolledTop) {
        SharedPreferences sp = mContext.getSharedPreferences("appinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("hl_si", scolledIndex);
        editor.putInt("hl_st", scolledTop);
        editor.commit();
    }

    public int getHomeTLScrolledIndex() {
        SharedPreferences sp = mContext.getSharedPreferences("appinfo", Context.MODE_PRIVATE);
        return sp.getInt("hl_si", 0);
    }

    public int getHomeTLScrolledTop() {
        SharedPreferences sp = mContext.getSharedPreferences("appinfo", Context.MODE_PRIVATE);
        return sp.getInt("hl_st", 0);
    }

    public void addHomeTLLoadmoreMark(String userId, int index) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sessions", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getUserString(userId, "ldmark"), index);
        editor.commit();
    }

    public int getHomeTLLoadmoreMark(String userId) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sessions", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(getUserString(userId, "ldmark"), 0);
    }

    public void setHomeMTScrollPostion(int scolledIndex, int scolledTop) {
        SharedPreferences sp = mContext.getSharedPreferences("appinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("mt_si", scolledIndex);
        editor.putInt("mt_st", scolledTop);
        editor.commit();
    }

    public int getHomeMTScrolledIndex() {
        SharedPreferences sp = mContext.getSharedPreferences("appinfo", Context.MODE_PRIVATE);
        return sp.getInt("mt_si", 0);
    }

    public int getHomeMTScrolledTop() {
        SharedPreferences sp = mContext.getSharedPreferences("appinfo", Context.MODE_PRIVATE);
        return sp.getInt("mt_st", 0);
    }

    public void addHomeMTLoadmoreMark(String userId, int index) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sessions", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getUserString(userId, "mldmark"), index);
        editor.commit();
    }

    public int getHomeMTLoadmoreMark(String userId) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("sessions", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(getUserString(userId, "mldmark"), 0);
    }




}
