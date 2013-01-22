package com.googolmo.utils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    public static boolean isFileExists(String dir, String fileName){
        File file = new File(dir + File.separator + fileName);
        return file.exists();
    }

    /**
     * 读取文件内容到字符串, 使用UTF-8编码
     * 
     * @param dir 数据存放的目录
     * @param fileName 数据存放的文件名称
     */
    public static String getStringFromFile(String dir, String fileName){
        return getStringFromFile(dir, fileName, "UTF-8");
    }
    
    /**
     * 读取文件内容到字符串
     * 
     * @param dir 数据存放的目录
     * @param fileName 数据存放的文件名称
     * @param enc 使用的编码
     */
    public static String getStringFromFile(String dir, String fileName, String enc){
        String result = null;
        try{
            InputStream in = openInputStream(dir, fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, enc));
            final StringBuilder str = new StringBuilder();
            String temp;
            do {
                temp = br.readLine();
                str.append(temp);
            } while (temp != null);
            result = str.toString();

            br.close();
            in = null;
            br = null;


        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 将字符串数据写入目录下的文件，使用UTF-8编码，如果文件不存在，会自动创建，如果文件存在，会覆盖。
     * 
     * @param str 要存放的字符串
     * @param dir 数据存放到的目录
     * @param fileName 数据存放到的文件名称
     */
    public static boolean writeStringToFile(String str, String dir, String fileName) {
        return writeStringToFile(str, "UTF-8", dir, fileName);
    }

    /**
     * 将字符串数据写入目录下的文件，如果文件不存在，会自动创建，如果文件存在，会覆盖。
     * 
     * @param str 要存放的字符串
     * @param enc 使用的编码方式
     * @param dir 数据存放到的目录
     * @param fileName 数据存放到的文件名称
     */
    public static boolean writeStringToFile(String str, String enc, String dir, String fileName) {
        if (str == null || dir == null || fileName == null)
            return false;

        File directory = new File(dir);

        if (!mkdirs(directory))
            return false;

        File target = new File(dir + File.separator + fileName);

        try {
            if (!target.exists()) {
                target.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(target);
            byte[] bytes = str.getBytes(enc);
            out.write(bytes);
            out.flush();
            out.close();
            out = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                target.delete();
            } catch (Exception ex) {
            }
            return false;
        }

    }

    /**
     * 将数据写入目录下的文件，如果文件不存在，会自动创建，如果文件存在，会覆盖。
     * 
     * @param in 要存放的数据
     * @param dir 数据存放到的目录
     * @param fileName 数据存放到的文件名称
     */
    public static boolean writeStreamToFile(InputStream in, String dir, String fileName) {
        if (in == null || dir == null || fileName == null)
            return false;

        File directory = new File(dir);

        if (!mkdirs(directory))
            return false;

        File target = new File(dir + File.separator + fileName);

        try {
            if (!target.exists()) {
                target.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(target);

            int b;
            do {
                b = in.read();
                if (b != -1) {
                    out.write(b);
                }
            } while (b != -1);
            in.close();
            out.flush();
            out.close();
            out = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                target.delete();
            } catch (Exception ex) {
            }
            return false;
        }

    }


    /**
     * 如果目录不存在，创建目录
     * 
     * @param dir
     * @return
     */
    public static boolean mkdirs(File dir) {
        if (dir !=null && (!dir.exists() || !dir.isDirectory())) {
            return dir.mkdirs();
        }
        return true;
    }

    /**
     * 使用FileInputStream打开指定的文件
     * 
     * @param dir 文件的目录
     * @param fileName 文件的名称
     * @return
     */

    public static FileInputStream openInputStream(String dir, String fileName) {
        return openInputStream(dir + File.separator + fileName);
    }

    /**
     * 使用FileInputStream打开指定的文件
     * 
     * @param filePath 文件的绝对路径
     * @return
     */
    public static FileInputStream openInputStream(String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            try {
                return new FileInputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获得文件的扩展名
     * 
     * @param fileName
     * @return
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.trim().equals(""))
            return null;
        int pos = fileName.lastIndexOf(".");

        if (pos > -1 && pos < fileName.length()) {
            return fileName.substring(pos + 1);
        } else {
            return "";
        }
    }

    /**
     * 获得url的文件名
     *
     * @param url
     * @return
     */
    public static String getFileName(String url) {
        if (url == null || url.trim().equals(""))
            return null;
        int pos = url.lastIndexOf("/");

        if (pos > -1 && pos < url.length()) {
            return url.substring(pos + 1);
        } else {
            return "";
        }
    }

}
