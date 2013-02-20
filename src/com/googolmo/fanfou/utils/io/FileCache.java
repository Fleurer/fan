package com.googolmo.fanfou.utils.io;

import android.content.Context;
import android.content.SharedPreferences;
import com.googolmo.fanfou.utils.app.ExternalStorageUtils;
import com.googolmo.fanfou.utils.codec.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class FileCache {

    public static void clear(Context context){
        SharedPreferences pref = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.clear().commit();
    }

    public static String getString(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
        String k = getFileName(key);
        long expire = pref.getLong(k, -1);
        if (expire > System.currentTimeMillis()/1000 || expire == 0){
            String dir = getFileDir(context);
            try{
                return FileUtils.getStringFromFile(dir, k);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(expire != -1) {
            delete(context ,key);
        }

        return null;
    }

    public static JSONObject get(Context context, String key){
        return getJSONObject(context, key);
    }

    public static JSONObject getJSONObject(Context context, String key){
        try{
            String str = getString(context, key);
            return new JSONObject(str);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getJSONArray(Context context, String key){
        try{
            String str = getString(context, key);
            return new JSONArray(str);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void set(Context context, String key, JSONArray value){
        set(context, key, value, 0);
    }

    public static void set(Context context, String key, JSONArray value, long expire){
        set(context, key, value.toString(), expire);
    }

    public static void set(Context context, String key, JSONObject value){
        set(context, key, value, 0);
    }

    public static void set(Context context, String key, JSONObject value, long expire){
        set(context, key, value.toString(), expire);
    }

    public static void set(Context context, String key, String value){
        set(context, key, value, 0);
    }

    /**
     * 将数据写入cache
     * 
     * @param key key, 比如api的url
     * @param value 数据
     * @param expire 过期时间，秒数，如果传0是没有过期
     */
    public static void set(Context context, String key, String value, long expire){
        SharedPreferences pref = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        String k = getFileName(key);
        if (expire > 0){
            edit.putLong(k, System.currentTimeMillis()/1000 + expire).commit();
        }else if (expire == 0){
            edit.putLong(k, 0).commit();
        }else if (pref.contains(k)){
            edit.putLong(k, -1).commit();
        }

        String dir = getFileDir(context);
        try {
            FileUtils.writeStringToFile(value, dir, k);
        }catch (Exception e){
        }
    }

    public static void delete(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        String k = getFileName(key);
        if (pref.contains(k)){
            edit.remove(k).commit();
        }

        String dir = getFileDir(context);
        if (FileUtils.isFileExists(getFileDir(context), k)){
            File f = new File(dir + File.separator + k);
            try{
                f.delete();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 得到cache的文件目录
     * 
     */
    public static String getFileDir(Context context) {
        String type = "Caches";
        return ExternalStorageUtils.getExternalFilesDir(context.getPackageName(), type).getAbsolutePath();
    }

    /**
     * 得到对应key的cache的文件名，为key的md5值
     * 
     */
    public static String getFileName(String key) {
        String md5 = DigestUtils.md5Hex(key);
        return md5;
    }

}
