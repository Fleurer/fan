package com.googolmo.fanfou.utils;

/**
 * User: googolmo
 * Date: 12-10-21
 * Time: 下午12:14
 */
public class BooleanUtils {

    public static final int toInt(boolean value) {
        return value ? 0 : 1;
    }

    public static final boolean toBoolean(int value) {
        return value == 0;
    }
}
