package com.googolmo.shmily.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * User: googolmo
 * Date: 12-9-8
 * Time: 下午8:42
 */
public class DBHelper extends SQLiteOpenHelper{
    private static final String TAG = DBHelper.class.getName();
    private static final String DATABASE_NAME = "shmily_fan";
    private static final int DATABASE_VERION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS user" +
                "(id varchar NOT NULL, name varchar NOT NULL, json text NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS status" +
                "(id varchar NOT NULL," +
                "userid varchar NOT NULL," +
                "json text NOT NULL," +
                "rawid integer NOT NULL," +
                "type integer NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


}
