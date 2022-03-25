package com.appnotification.notificationhistorylog.ui;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.appnotification.notificationhistorylog.CommonCl.SharedCommon;
import com.appnotification.notificationhistorylog.R;
import com.appnotification.notificationhistorylog.misc.Const;
import com.appnotification.notificationhistorylog.misc.DatabaseHelper;
import com.appnotification.notificationhistorylog.misc.Util;
import com.appnotification.notificationhistorylog.service.NotificationHandler;

import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyopenrateall;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String TAG = SettingsFragment.class.getName();
    TextView tx;
    private DatabaseHelper dbHelper;
    private BroadcastReceiver updateReceiver;
    private Preference prefStatus;
    private Preference prefText;
    private Preference prefOngoing;
    private Preference prefEntries;
    private Object SettingsFragment;
    private Preference prefFavorites;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);

        int orall = SharedCommon.getPreferencesInt(getActivity(), keyopenrateall, 0);


        PreferenceManager pm = getPreferenceManager();
        /*getListView().setBackgroundColor(Color.TRANSPARENT);
        getListView().setBackgroundColor(Color.rgb(4, 26, 55));*/
        prefStatus = pm.findPreference(Const.PREF_STATUS);

        prefStatus.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                return true;
            }
        });

        pm.findPreference(Const.PREF_BROWSE).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedCommon scor = new SharedCommon();

                int orfavc = SharedCommon.getPreferencesInt(getActivity(), keyopenrateall, 0);


                orfavc++;
                SharedCommon.putPreferencesInt(getActivity(), SharedCommon.keyopenrateall, orfavc);
                startActivity(new Intent(getActivity(), BrowseActivity.class));
                return true;
            }
        });


        // prefText = pm.findPreference(Const.PREF_TEXT);
        //  prefOngoing = pm.findPreference(Const.PREF_ONGOING);
        prefEntries = pm.findPreference(Const.PREF_ENTRIES);


        // pm.findPreference(Const.PREF_VERSION).setSummary(BuildConfig.VERSION_NAME + (Const.DEBUG ? " dev" : ""));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            dbHelper = new DatabaseHelper(getActivity());
        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
        }

        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                update();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Util.isNotificationAccessEnabled(getActivity())) {
            prefStatus.setSummary(R.string.settings_notification_access_enabled);

            //prefText.setEnabled(true);
            // prefOngoing.setEnabled(true);
        } else {
            prefStatus.setSummary(R.string.settings_notification_access_disabled);
            //prefText.setEnabled(false);
            //prefOngoing.setEnabled(false);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(NotificationHandler.BROADCAST);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateReceiver, filter);

        update();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateReceiver);
        super.onPause();
    }

    private void update() {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            long numRowsPosted = DatabaseUtils.queryNumEntries(db, DatabaseHelper.PostedEntry.TABLE_NAME);
            prefEntries.setSummary("" + numRowsPosted);

            String nnn = String.valueOf(numRowsPosted);
            SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edt = pref.edit();
            edt.putString("facebook_id", nnn);
            edt.commit();

            if (numRowsPosted >= 19000) {
                int n = (int) numRowsPosted;
                String nn = ("" + n);
                overnotificationmessage(n, nn);

            }
            long numRowsFavorite = DatabaseUtils.queryNumEntries(db, DatabaseHelper.PostedEntry.TABLE_NAME,
                    DatabaseHelper.PostedEntry.COLUMN_NAME_FAVORITE + "=?", new String[]{String.valueOf(1)});
            int stringResource1 = numRowsFavorite == 1 ? R.string.settings_browse_summary_singular : R.string.settings_browse_summary_plural;
            prefFavorites.setSummary(getString(stringResource1, numRowsFavorite));


        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
        }
    }

    private void overnotificationmessage(int n, String nn) {


        // Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        builder.setIcon(R.drawable.ic_splash_logo);

        builder.setTitle("Note ");
        builder.setMessage("You Have More Than 10,000 Notifications Logged, We Request You To Clear Notifications So That App Can Run Smoothly ");

        builder.setCancelable(true);

        builder.setNeutralButton("DISMISS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("CLEAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                try {
                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.execSQL(DatabaseHelper.SQL_DELETE_ENTRIES_POSTED);
                    db.execSQL(DatabaseHelper.SQL_CREATE_ENTRIES_POSTED);
                    db.execSQL(DatabaseHelper.SQL_DELETE_ENTRIES_REMOVED);
                    db.execSQL(DatabaseHelper.SQL_CREATE_ENTRIES_REMOVED);
                    Intent local = new Intent();
                    local.setAction(NotificationHandler.BROADCAST);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(local);
                    Intent startIntentr = new Intent(getActivity(), NewMainActivity.class);
                    startActivity(startIntentr);
                } catch (Exception e) {
                    if (Const.DEBUG) e.printStackTrace();
                }


                Toast.makeText(getActivity(), "Cleared !", Toast.LENGTH_LONG).show();

                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positive.setTextColor(getResources().getColor(R.color.colorText));
        Button negative = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        negative.setTextColor(getResources().getColor(R.color.colorText));
    }

}