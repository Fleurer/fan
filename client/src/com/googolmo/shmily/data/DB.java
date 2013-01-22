package com.googolmo.shmily.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.googolmo.fanfouApi.module.Status;
import com.googolmo.fanfouApi.module.User;
import com.googolmo.fanfouApi.utils.NLog;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * User: googolmo
 * Date: 12-9-9
 * Time: 下午12:18
 */
public class DB {
    private static final String TAG = DB.class.getName();

    public static final int STATUS_TYPE_HOMETLINE = 0;
    public static final int STATUS_TYPE_MENTIONS = 1;

    private SQLiteDatabase db;
    private DBHelper helper;
    private Context context;

    public DB(Context context) {
        this.context = context;
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    public void addStatuses(List<Status> statuses, String userId, int type) {
        db.beginTransaction();
        try {
            db.delete("status", "userid like ? and type = ?", new String[]{userId, String.valueOf(type)});
            for (Status status : statuses) {
//                NLog.d(TAG, String.format("insert into status values(%1$s, %2$s, %3$s, %4$s)", status.getId(),
//                        userId, status.getJsonString(), status.getRwaid()));
                db.execSQL("insert into status values (?, ?, ?, ?, ?)", new Object[]{status.getId(),
                        userId, status.getJsonString(), status.getRwaid(), type});
            }
            db.setTransactionSuccessful();
            NLog.i(TAG, "add status user " + userId + " done");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteStatus(String id, int type) {
        db.beginTransaction();
        try {
            db.delete("status", "id like ? and type = ?", new String[]{id, String.valueOf(type)});
            db.setTransactionSuccessful();
            NLog.i(TAG, "delete status id=" + id);
        } finally {
            db.endTransaction();
        }
    }

    public List<Status> getStatuesByUser(String userId, int type) {
        NLog.d(TAG, "get " + userId + "'s status");
        Cursor cursor = null;
        db.beginTransaction();
        try {
            cursor = db.query("status", new String[]{"id", "userid", "rawId", "json"}, "userid like ? and type = ?",
                    new String[]{userId, String.valueOf(type)}, null, null, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }


        List<Status> statuses = new ArrayList<Status>();
        if (cursor != null && cursor.getCount() > 0) {
            NLog.d(TAG, "get cursor size = " + cursor.getCount());
//            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                String json = cursor.getString(cursor.getColumnIndex("json"));
                try {
                    statuses.add(new Status(new JSONObject(json)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        NLog.d(TAG, "get statuses size = " + statuses.size());
        return statuses;
    }

    public void deleteStatusByUser(String userId) {
        db.beginTransaction();
        try{
            db.delete("status", "userid like ?", new String[]{userId});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public Status getStatus(String id, int type) {
        Cursor cursor = db.query("status", new String[]{"id", "userid", "rawId", "json"},
                "id like ? and type = ?", new String[]{id, String.valueOf(type)}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                String s = cursor.getString(cursor.getColumnIndex("json"));
                NLog.i(TAG, s);
                return new Status(new JSONObject(s));
            } catch (JSONException e) {

            }
        }
        return null;
    }


    public void addUser(User user) {
        db.beginTransaction();
        try{
            if (!isExistUser(user.getId())) {
                db.execSQL("insert into user values (?, ?, ?)", new Object[]{user.getId(), user.getName(), user.getJsonString()});
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put("json", user.getJsonString());
                contentValues.put("name", user.getName());
                db.update("user", contentValues, "? like ?", new String[]{"id", user.getId()});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public User getUser(String userId) {
        Cursor cursor = db.query("user", new String[]{"id", "name", "json"}, "id like ?", new String[]{userId},
                null, null, null);
        if (cursor.moveToFirst()) {

            String json = cursor.getString(cursor.getColumnIndex("json"));
            try {
                return new User(new JSONObject(json));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        NLog.d(TAG, "cusor.count" + cursor.getCount());
        return null;
    }

    public void deleteUser(String userId) {
        db.beginTransaction();
        try {
            db.delete("user", "id like ?", new String[]{userId});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public boolean isExistUser(String userId) {
        Cursor cursor = getUserCursor(userId);
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Cursor getUserCursor(String userId) {
        return db.query("user", new String[]{"id", "name", "json"}, "id like ?", new String[]{userId},
                null, null, null);
    }

    public List<User> getUsers() {
        db.beginTransaction();
        try{

            Cursor cursor = db.query("user", new String[]{"id", "name", "json"}, null, null, null, null, null);
            List<User> users = new ArrayList<User>();
            if (cursor.getCount() > 0) {
//                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    try {
                        users.add(new User(new JSONObject(cursor.getString(2))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return users;
        } finally {
            db.endTransaction();
        }
    }
}
