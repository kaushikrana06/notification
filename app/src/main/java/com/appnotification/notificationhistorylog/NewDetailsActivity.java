package com.appnotification.notificationhistorylog;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.appnotification.notificationhistorylog.misc.Const;
import com.appnotification.notificationhistorylog.misc.DatabaseHelper;
import com.appnotification.notificationhistorylog.misc.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Locale;

public class NewDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_ACTION = "action";
    public static final String ACTION_REFRESH = "refresh";

    private static final boolean SHOW_RELATIVE_DATE_TIME = true;
    JSONObject json = null;
    String titleText, contentText, date;
    Menu menu1 = null;
    private String id;
    private final DialogInterface.OnClickListener doDelete = (dialog, which) -> {
        int affectedRows = 0;
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            affectedRows = db.delete(DatabaseHelper.PostedEntry.TABLE_NAME,
                    DatabaseHelper.PostedEntry._ID + " = ?",
                    new String[]{id});
            db.close();
            databaseHelper.close();
        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
        }

        if (affectedRows > 0) {
            Intent data = new Intent();
            data.putExtra(EXTRA_ACTION, ACTION_REFRESH);
            setResult(RESULT_OK, data);
            finish();
        }
    };
    private String packageName;
    private int appUid;
    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_details);

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra(EXTRA_ID);
            if (id != null) {
                loadDetails(id);
            } else {
                finishWithToast();
            }
        } else {
            finishWithToast();
        }
    }

    @Override
    protected void onPause() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);

        if (getIntent().getStringExtra("EXTRA_INFO").equals("hide")) {
            menu.findItem(R.id.menu_favorite_remove).setVisible(false);
            menu.findItem(R.id.menu_favorite).setVisible(false);
        } else {

            menu.findItem(R.id.menu_favorite_remove).setVisible(false);
            menu.findItem(R.id.menu_favorite).setVisible(true);

        }
        menu1 = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete: {
                confirmDelete();
                break;
            }
            case R.id.menu_favorite: {
                item.setVisible(false);
                menu1.findItem(R.id.menu_favorite_remove).setVisible(true);
                addToFavorites();
                Log.d("MENU OPT", "favorite");
                break;
            }
            case R.id.menu_favorite_remove: {
                item.setVisible(false);
                menu1.findItem(R.id.menu_favorite).setVisible(true);
                removeFavorites();
                Log.d("MENU OPT", "favorite remove");
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeFavorites() {

        Log.v("METHOD", "favorite remove");
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.PostedEntry.COLUMN_NAME_FAVORITE, 0);
        db.update(DatabaseHelper.PostedEntry.TABLE_NAME, contentValues, "_ID=" + id, null);


        Toast.makeText(NewDetailsActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();

    }

    private void addToFavorites() {

        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.PostedEntry.COLUMN_NAME_FAVORITE, 1);
        db.update(DatabaseHelper.PostedEntry.TABLE_NAME, contentValues, "_ID=" + id, null);
        db.close();
        helper.close();
        Toast.makeText(NewDetailsActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();

    }

    private void loadDetails(String id) {

        String str = "error";
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            SQLiteDatabase db = databaseHelper.getReadableDatabase();

            Cursor cursor = db.query(DatabaseHelper.PostedEntry.TABLE_NAME,
                    new String[]{
                            DatabaseHelper.PostedEntry.COLUMN_NAME_CONTENT,
                    },
                    DatabaseHelper.PostedEntry._ID + " = ?",
                    new String[]{
                            id
                    },
                    null,
                    null,
                    null,
                    "1");

            if (cursor != null && cursor.getCount() == 1 && cursor.moveToFirst()) {
                try {
                    json = new JSONObject(cursor.getString(0));
                    str = json.toString(2);
                } catch (JSONException e) {
                    if (Const.DEBUG) e.printStackTrace();
                }
                cursor.close();
            }

            db.close();
            databaseHelper.close();
        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
        }
        TextView tvJSON = findViewById(R.id.json);
        tvJSON.setText(str);

        CardView card = findViewById(R.id.card);
        CardView buttons = findViewById(R.id.buttons);
        if (json != null) {
            packageName = json.optString("packageName", "???");
            titleText = json.optString("title");
            contentText = json.optString("text");
            String text = (titleText + "\n" + contentText).trim();
            if (!"".equals(text)) {
                card.setVisibility(View.VISIBLE);
                ImageView icon = findViewById(R.id.icon);
                icon.setImageDrawable(Util.getAppIconFromPackage(this, packageName));
                TextView tvName = findViewById(R.id.name);
                tvName.setText(Util.getAppNameFromPackage(this, packageName, false));
                TextView tvText = findViewById(R.id.text);
                tvText.setText(text);
                TextView tvDate = findViewById(R.id.date);
                if (SHOW_RELATIVE_DATE_TIME) {
                    tvDate.setText(DateUtils.getRelativeDateTimeString(
                            this,
                            json.optLong("systemTime"),
                            DateUtils.MINUTE_IN_MILLIS,
                            DateUtils.WEEK_IN_MILLIS,
                            0));
                } else {
                    DateFormat format = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, Locale.getDefault());
                    tvDate.setText(format.format(json.optLong("systemTime")));
                }

                try {
                    ApplicationInfo app = this.getPackageManager().getApplicationInfo(packageName, 0);
                    buttons.setVisibility(View.VISIBLE);
                    appUid = app.uid;
                } catch (PackageManager.NameNotFoundException e) {
                    if (Const.DEBUG) e.printStackTrace();
                    buttons.setVisibility(View.GONE);
                }
            } else {
                card.setVisibility(View.GONE);

            }
        } else {
            card.setVisibility(View.GONE);
            buttons.setVisibility(View.GONE);
        }
    }

    private void finishWithToast() {
        Toast.makeText(this, R.string.details_error, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void confirmDelete() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        dialog = new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle(R.string.delete_dialog_title)
                .setMessage(R.string.delete_dialog_text)
                .setPositiveButton(R.string.delete_dialog_yes, doDelete)
                .setNegativeButton(R.string.delete_dialog_no, null)
                .show();
        Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positive.setTextColor(getResources().getColor(R.color.colorText));
        Button neutral = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        neutral.setTextColor(getResources().getColor(R.color.colorText));
    }

    public void openNotificationSettings(View v) {
        try {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT > 25) {
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("android.provider.extra.APP_PACKAGE", packageName);
            } else if (Build.VERSION.SDK_INT >= 21) {
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", packageName);
                intent.putExtra("app_uid", appUid);
            } else {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + packageName));
            }
            startActivity(intent);
        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
        }
    }

}