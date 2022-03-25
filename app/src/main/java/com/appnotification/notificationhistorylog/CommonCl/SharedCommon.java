package com.appnotification.notificationhistorylog.CommonCl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedCommon {

    public static final String key1 = "key1";
    public static final String keyopenrate = "keyopenrate";
    public static final String keyopenrateall = "keyopenrateall";
    public static final String keyopenratetutorial = "keyopenratetutorial";
    public static final String keyopenratefav = "keyopenratefav";
    public static final String keyopensetting = "keyopensetting";
    public static final String keylog = "keylog";
    public static final String keyoveruse = "keyoveruse";

    public static final String keyfaqs = "keyfaqs";


    public static final String keylogtext = "keylogtext";

    public static final String keyname = "keyname";

    public static final String keydis = "keydis";
    public static final String keynotification = "keynotification";

    public static final String keytokenid = "keytokenid";


    public static void putPreferencesInt(Context context, String key, int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static int getPreferencesInt(Context context, String key, int _default) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, _default);
    }

    public static void putSharedPreferencesString(Context context, String key, String val) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, val);
        edit.commit();

    }

    public static String getSharedPreferencesString(Context context, String key, String _default) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, _default);
    }

}
