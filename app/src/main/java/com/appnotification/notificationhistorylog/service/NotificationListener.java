package com.appnotification.notificationhistorylog.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.appnotification.notificationhistorylog.misc.Const;
import com.appnotification.notificationhistorylog.misc.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class NotificationListener extends NotificationListenerService {

    private static NotificationListener instance = null;

    private ActivityRecognitionClient activityRecognitionClient = null;
    private PendingIntent activityRecognitionPendingIntent = null;

    private FusedLocationProviderClient fusedLocationClient = null;
    private PendingIntent fusedLocationPendingIntent = null;

    public static StatusBarNotification[] getAllActiveNotifications() {
        if (instance != null) {
            try {
                return instance.getActiveNotifications();
            } catch (Exception e) {
                if (Const.DEBUG) e.printStackTrace();
            }
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public static StatusBarNotification[] getAllSnoozedNotifications() {
        if (instance != null) {
            try {
                return instance.getSnoozedNotifications();
            } catch (Exception e) {
                if (Const.DEBUG) e.printStackTrace();
            }
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int getInterruptionFilter() {
        if (instance != null) {
            try {
                return instance.getCurrentInterruptionFilter();
            } catch (Exception e) {
                if (Const.DEBUG) e.printStackTrace();
            }
        }
        return -1;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int getListenerHints() {
        if (instance != null) {
            try {
                return instance.getCurrentListenerHints();
            } catch (Exception e) {
                if (Const.DEBUG) e.printStackTrace();
            }
        }
        return -1;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static NotificationListenerService.RankingMap getRanking() {
        if (instance != null) {
            try {
                return instance.getCurrentRanking();
            } catch (Exception e) {
                if (Const.DEBUG) e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT < 21) {
            instance = this;
            startActivityRecognition();
            startFusedLocationIntentService();
        }
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT < 24) {
            instance = null;
            stopActivityRecognition();
            stopFusedLocationIntentService();
        }
        super.onDestroy();
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        if (Build.VERSION.SDK_INT >= 21) {
            instance = this;
            startActivityRecognition();
            startFusedLocationIntentService();
        }
    }

    @Override
    public void onListenerDisconnected() {
        if (Build.VERSION.SDK_INT >= 24) {
            instance = null;
            stopActivityRecognition();
            stopFusedLocationIntentService();
        }
        super.onListenerDisconnected();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            NotificationHandler notificationHandler = new NotificationHandler(this);
            notificationHandler.handlePosted(sbn);
        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        try {
            NotificationHandler notificationHandler = new NotificationHandler(this);
            notificationHandler.handleRemoved(sbn, -1);
        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap, int reason) {
        try {
            NotificationHandler notificationHandler = new NotificationHandler(this);
            notificationHandler.handleRemoved(sbn, reason);
        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
        }
    }

    private void startActivityRecognition() {
        if (!Const.ENABLE_ACTIVITY_RECOGNITION) {
            return;
        }
        try {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    activityRecognitionPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, ActivityRecognitionIntentService.class), PendingIntent.FLAG_IMMUTABLE);
                    activityRecognitionClient = ActivityRecognition.getClient(this);
                    activityRecognitionClient.requestActivityUpdates(120_000L, activityRecognitionPendingIntent);
                }else {
                    activityRecognitionPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, ActivityRecognitionIntentService.class), PendingIntent.FLAG_ONE_SHOT);
                    activityRecognitionClient = ActivityRecognition.getClient(this);
                    activityRecognitionClient.requestActivityUpdates(120_000L, activityRecognitionPendingIntent);
                }
            }
        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
        }
    }


    private void stopActivityRecognition() {
        if (!Const.ENABLE_ACTIVITY_RECOGNITION) {
            return;
        }
        try {
            if (activityRecognitionClient != null && activityRecognitionPendingIntent != null) {
                activityRecognitionClient.removeActivityUpdates(activityRecognitionPendingIntent);
                activityRecognitionClient = null;
                activityRecognitionPendingIntent = null;
            }
        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    private void startFusedLocationIntentService() {
        if (!Const.ENABLE_LOCATION_SERVICE) {
            return;
        }
        if (Util.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) && Util.hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {

                fusedLocationPendingIntent = PendingIntent.getService(this, 0, new Intent(this, FusedLocationIntentService.class), PendingIntent.FLAG_IMMUTABLE);
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationClient.requestLocationUpdates(locationRequest, fusedLocationPendingIntent);
            }else{
                fusedLocationPendingIntent = PendingIntent.getService(this, 0, new Intent(this, FusedLocationIntentService.class), PendingIntent.FLAG_ONE_SHOT);
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationClient.requestLocationUpdates(locationRequest, fusedLocationPendingIntent);
            }
        }
    }

    private void stopFusedLocationIntentService() {
        if (!Const.ENABLE_LOCATION_SERVICE) {
            return;
        }
        if (fusedLocationClient != null && fusedLocationPendingIntent != null) {
            fusedLocationClient.removeLocationUpdates(fusedLocationPendingIntent);
        }
    }
}
