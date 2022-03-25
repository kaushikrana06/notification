package com.appnotification.notificationhistorylog;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.facebook.ads.AudienceNetworkAds;

import timber.log.Timber;

public class NotifyLogApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        if (SettingsActivity.isNightModeEnabled(getApplicationContext())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);
    }
}
