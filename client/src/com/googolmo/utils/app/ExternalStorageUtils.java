package com.googolmo.utils.app;

import android.os.Environment;
import com.googolmo.utils.io.FileUtils;

import java.io.File;


public class ExternalStorageUtils {

    /**
     * 获得外部缓存目录，一般为：/sdcard/Android/data/<package_name>/cache/
     * 
     * @param packageName
     * @return
     */
    public static File getExternalCacheDir(String packageName) {

        File extDir = Environment.getExternalStorageDirectory();
        if (extDir == null)
            return null;
        File dir = new File(extDir.getAbsolutePath() + File.separator + "Android" + File.separator + "data"
                + File.separator + packageName + File.separator + "cache");
        try{
            FileUtils.mkdirs(dir);
        }catch (Exception e){
            e.printStackTrace(); 
        }
        return dir;
    }

    /**
     * 获得外部存储文件目录，一般为：：/sdcard/Android/data/<package_name>/files/<type>/
     * 
     * @param packageName
     * @param type
     * @return
     */
    public static File getExternalFilesDir(String packageName, String type) {
        File extDir = Environment.getExternalStorageDirectory();
        if (extDir == null)
            return null;

        if (type == null) type = "";

        File dir = new File(extDir.getAbsolutePath() + File.separator + "Android" + File.separator + "data"
                + File.separator + packageName + File.separator + "files" + File.separator + type);

        try{
            FileUtils.mkdirs(dir);
        }catch (Exception e){
            e.printStackTrace(); 
        }
        return dir;
    }

}
