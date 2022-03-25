package com.appnotification.notificationhistorylog.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.appnotification.notificationhistorylog.R;
import com.appnotification.notificationhistorylog.misc.Const;
import com.appnotification.notificationhistorylog.misc.DatabaseHelper;
import com.appnotification.notificationhistorylog.misc.Util;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import timber.log.Timber;

class GmailAdapter extends RecyclerView.Adapter<BrowseViewHolder> {
    private final static int LIMIT = Integer.MAX_VALUE;
    private final static String PAGE_SIZE = "9999";

    private final DateFormat format = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());

    private final Activity context;
    private final HashMap<String, Drawable> iconCache;
    private final Handler handler = new Handler();
    private final String packageName;
    private ArrayList<DataItem> data;
    private String lastDate = "";
    private boolean shouldLoadMore = true;

    GmailAdapter(Activity context, String packageName) {
        this.context = context;
        this.packageName = packageName;
        data = new ArrayList<>();
        iconCache = new HashMap<>();
        loadMore(packageName, Integer.MAX_VALUE);

    }


    public void deleteItem(int position) {
        long id = data.get(position).id;
        Timber.i("delete favorite");
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DatabaseHelper.PostedEntry.TABLE_NAME, "_ID=" + id, null);
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
    }

    @NonNull
    @Override
    public BrowseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_browse, parent, false);
        BrowseViewHolder vh = new BrowseViewHolder(view);
        vh.item.setOnClickListener(v -> {
            String id = (String) v.getTag();
            if (id != null) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra(DetailsActivity.EXTRA_ID, id);
                if (Build.VERSION.SDK_INT >= 21) {
                    Pair<View, String> p1 = Pair.create(vh.icon, "icon");
                    @SuppressWarnings("unchecked") ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, p1);
                    context.startActivityForResult(intent, 1, options.toBundle());
                } else {
                    context.startActivityForResult(intent, 1);
                }
            }
        });
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull BrowseViewHolder vh, int position) {
        DataItem item = data.get(position);
        if (iconCache.get(item.getPackageName()) != null && iconCache.containsKey(item.getPackageName())) {
            vh.icon.setImageDrawable(iconCache.get(item.getPackageName()));
        } else {
            Drawable appIcon = Util.getAppIconFromPackage(context, item.getPackageName());
            if (appIcon != null) vh.icon.setImageDrawable(appIcon);
            else vh.icon.setImageResource(R.mipmap.ic_launcher);
        }

        vh.item.setTag("" + item.getId());
        vh.name.setText(item.getAppName());
        vh.time.setText(item.getTime());

        if (item.getPreview().length() == 0) {
            vh.preview.setVisibility(View.GONE);
            vh.text.setVisibility(View.VISIBLE);
            vh.text.setText(item.getText());
        } else {
            vh.text.setVisibility(View.GONE);
            vh.preview.setVisibility(View.VISIBLE);
            vh.preview.setText(item.getPreview());
        }
        Timber.i("onBindViewHolder: " + item.shouldShowDate() + "  " + item.getDate());
        if (item.shouldShowDate()) {
            vh.date.setVisibility(View.VISIBLE);
            vh.date.setText(item.getDate());
        } else {
            vh.date.setVisibility(View.GONE);
        }
        if (position == getItemCount() - 1) {
            loadMore(packageName, item.getId());
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void filterList(ArrayList<DataItem> filteredList) {
        data = filteredList;
        notifyDataSetChanged();
    }

    private void loadMore(String packageName, long afterId) {
        if (!shouldLoadMore) {
            if (Const.DEBUG) System.out.println("not loading more items");
            return;
        }

        if (Const.DEBUG) System.out.println("loading more items");
        int before = getItemCount();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query(DatabaseHelper.PostedEntry.TABLE_NAME,
                    new String[]{DatabaseHelper.PostedEntry._ID, DatabaseHelper.PostedEntry.COLUMN_NAME_CONTENT},
                    DatabaseHelper.PostedEntry._ID + " < ?",
                    new String[]{"" + afterId},
                    null,
                    null,
                    DatabaseHelper.PostedEntry._ID + " DESC",
                    PAGE_SIZE);

            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    try {
                        Timber.i("Column1: %s", cursor.getLong(0));
                        Timber.i("Column2: %s", cursor.getString(1));
                        DataItem dataItem = new DataItem(context, cursor.getLong(0), cursor.getString(1));
                        Timber.e(dataItem.packageName + " == " + packageName);
                        if (dataItem.packageName.equals(packageName)) {
                            String thisDate = dataItem.getDate();
                            dataItem.setShowDate(!lastDate.equals(thisDate));
                            lastDate = thisDate;
                            data.add(dataItem);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
        } finally {
            db.close();
            databaseHelper.close();
        }
        int after = getItemCount();

        if (before == after) {
            if (Const.DEBUG) System.out.println("no new items loaded: " + getItemCount());
            shouldLoadMore = false;
        }

        if (getItemCount() > LIMIT) {
            if (Const.DEBUG)
                System.out.println("reached the limit, not loading more items: " + getItemCount());
            shouldLoadMore = false;
        }

        handler.post(() -> notifyDataSetChanged());
    }

    public ArrayList filter(String text) {
        ArrayList<DataItem> filteredList = new ArrayList<>();

        for (DataItem item : data) {
            if (item.getAppName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        return filteredList;

    }

    private class DataItem {
        private final long id;
        private final String packageName;
        private final String appName;
        private final String text;
        private final String preview;
        private final String date;
        private final String time;
        private boolean showDate;

        DataItem(Context context, long id, String str) throws Exception {
            this.id = id;
            JSONObject json = new JSONObject(str);
            packageName = json.getString("packageName");
            appName = Util.getAppNameFromPackage(context, packageName, false);
            text = str;

            String title = json.optString("title");
            String text = json.optString("text");
            preview = (title + "\n" + text).trim();

            if (!iconCache.containsKey(packageName)) {
                iconCache.put(packageName, Util.getAppIconFromPackage(context, packageName));
            }

            date = format.format(json.optLong("systemTime"));
            SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
            time = df.format(json.optLong("systemTime"));
            showDate = false;
        }

        public long getId() {
            return id;
        }

        public String getPackageName() {
            return packageName;
        }

        public String getAppName() {
            return appName;
        }

        public String getText() {
            return text;
        }

        public String getPreview() {
            return preview;
        }

        public String getDate() {
            return date;
        }

        public boolean shouldShowDate() {
            return showDate;
        }

        public void setShowDate(boolean showDate) {
            this.showDate = showDate;
        }

        public String getTime() {
            return time;
        }
    }
}


