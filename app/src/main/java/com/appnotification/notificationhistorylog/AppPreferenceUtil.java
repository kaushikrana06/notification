package com.appnotification.notificationhistorylog;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreferenceUtil {

//    public static void saveString(ContextString key, String value) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CacheManager.getAppContext());
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString(key, value);
//        editor.apply();
//    }
//
//    public static String getString(String key, String defaultValue) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CacheManager.getAppContext());
//        return settings.getString(key, defaultValue);
//    }

    public static void saveInt(Context context, String key, int value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getInt(key, defaultValue);
    }

//    public static void saveBoolean(String key, boolean value) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CacheManager.getAppContext());
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putBoolean(key, value);
//        editor.apply();
//    }
//
//    public static boolean getBoolean(String key, boolean defaultValue) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CacheManager.getAppContext());
//        return settings.getBoolean(key, defaultValue);
//    }
//
//    public static void saveLong(String key, long value) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CacheManager.getAppContext());
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putLong(key, value);
//        editor.apply();
//    }
//
//    public static long getLong(String key, long defaultValue) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CacheManager.getAppContext());
//        return settings.getLong(key, defaultValue);
//    }

    public static void removeKey(Context context, String key) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        editor.apply();
    }
}
