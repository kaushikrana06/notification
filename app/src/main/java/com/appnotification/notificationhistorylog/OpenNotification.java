package com.appnotification.notificationhistorylog;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class OpenNotification extends AppWidgetProvider {

    public static String WIDGET_BUTTON = "MY_PACKAGE_NAME.WIDGET_BUTTON";
    static String CLICK_ACTION = "CLICKED";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Intent intent = new Intent(context, OpenNotification.class);
        intent.setAction(CLICK_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);


        Intent intentbtn = new Intent(WIDGET_BUTTON);
        PendingIntent pendingIntentbtn = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        CharSequence widgetText = context.getString(R.string.opennotifwid);
        // Construct the RemoteViews object


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.open_notification);
        views.setTextViewText(R.id.appwidgettext, widgetText);
        views.setOnClickPendingIntent(R.id.layoutwidtext, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

        // Toast.makeText(context, "Text Recognition Widget Added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(CLICK_ACTION)) {

            Toast.makeText(context, "Opening...", Toast.LENGTH_SHORT).show();

            Intent mailClient = new Intent(Intent.ACTION_VIEW);
            mailClient.putExtra("TITLE", "Hindi");
            mailClient.setClassName("com.appnotification.notificationhistorylog", "com.appnotification.notificationhistorylog.ui.BrowseActivity");
            /*
             *    THE ANSWER!!!   mailClient.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             */
            mailClient.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mailClient);


        }
    }
}



