package com.appnotification.notificationhistorylog.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.appnotification.notificationhistorylog.R;
import com.appnotification.notificationhistorylog.misc.Const;
import com.appnotification.notificationhistorylog.misc.DatabaseHelper;
import com.appnotification.notificationhistorylog.misc.Util;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.nativead.NativeAd;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


class BrowseAdapterHome extends RecyclerView.Adapter<BrowseViewHolderHome> {

    private final static int LIMIT = Integer.MAX_VALUE;
    private final static String PAGE_SIZE = "2599";
    private final DateFormat format = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
    private final Activity context;

    private final Handler handler = new Handler();
    private final List<NativeAd> mRecyclerViewItems;
    private final HashMap<String, Drawable> iconCache;
    int ads = 0;
    private ArrayList<BrowseAdapterHome.DataItem> data;
    private String lastDate = "";
    private boolean shouldLoadMore = true;
    private long lastClickTime = 0;

    BrowseAdapterHome(Activity context, List<NativeAd> mNativeAds) {
        this.context = context;
        iconCache = new HashMap<>();
        data = new ArrayList<>();
        loadMore(Integer.MAX_VALUE);
        mRecyclerViewItems = mNativeAds;
    }

    @NonNull
    @Override
    public BrowseViewHolderHome onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_browse_home, parent, false);
        BrowseViewHolderHome vh = new BrowseViewHolderHome(view);
        vh.item.setOnClickListener(v -> {
            // preventing double, using threshold of 1000 ms
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return;
            }

            lastClickTime = SystemClock.elapsedRealtime();
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
    public void onBindViewHolder(@NonNull BrowseViewHolderHome vh, int position) {
        BrowseAdapterHome.DataItem item = data.get(position);
        if (mRecyclerViewItems.size() == ads)
            ads = 0;
        if (mRecyclerViewItems.size() > ads) {
            if (position % 18 == 0) {
                vh.getAdView().setVisibility(View.VISIBLE);
                NativeAd nativeAd = mRecyclerViewItems.get(ads);
                ads++;
                populateNativeAdView(nativeAd, vh.getAdView());
            } else {
                vh.getAdView().setVisibility(View.GONE);
            }
        } else {
            vh.getAdView().setVisibility(View.GONE);
        }
        if (iconCache.size() > 0 && iconCache.get(item.getPackageName()) != null && iconCache.containsKey(item.getPackageName())) {
            Drawable drawable = iconCache.get(item.getPackageName());
            if (drawable != null) {
                vh.icon.setImageDrawable(drawable);
            } else {
                vh.icon.setImageDrawable(Util.getAppIconFromPackage(context, item.getPackageName()));
            }
        } else {
            vh.icon.setImageResource(R.drawable.ic_splash_logo);
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

        if (item.shouldShowDate()) {
            vh.date.setVisibility(View.VISIBLE);
            vh.date.setText(item.getDate());
        } else {
            vh.date.setVisibility(View.GONE);
        }

        if (position == getItemCount() - 1) {
            loadMore(item.getId());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void filterList(ArrayList<BrowseAdapterHome.DataItem> filteredList) {
        data = filteredList;
        notifyDataSetChanged();
    }

    private void loadMore(long afterId) {
        if (!shouldLoadMore) {
            if (Const.DEBUG) System.out.println("not loading more items");
            return;
        }
        if (Const.DEBUG) System.out.println("loading more items");
        int before = getItemCount();
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.query(DatabaseHelper.PostedEntry.TABLE_NAME,
                    new String[]{
                            DatabaseHelper.PostedEntry._ID,
                            DatabaseHelper.PostedEntry.COLUMN_NAME_CONTENT
                    },
                    DatabaseHelper.PostedEntry._ID + " < ?",
                    new String[]{"" + afterId},
                    null,
                    null,
                    DatabaseHelper.PostedEntry._ID + " DESC",
                    PAGE_SIZE);

            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    BrowseAdapterHome.DataItem dataItem = new BrowseAdapterHome.DataItem(context, cursor.getLong(0), cursor.getString(1));
                    String thisDate = dataItem.getDate();
                    if (lastDate.equals(thisDate)) {
                        dataItem.setShowDate(false);
                    }
                    lastDate = thisDate;
                    data.add(dataItem);
                    cursor.moveToNext();
                }
                cursor.close();
            }

            db.close();
            databaseHelper.close();
        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
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
        ArrayList<BrowseAdapterHome.DataItem> filteredList = new ArrayList<>();
        {
            for (BrowseAdapterHome.DataItem item : data) {
                if (item.getAppName() != null && !item.getAppName().equals("") && (
                        item.getAppName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getText().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDate().toLowerCase().contains(text.toLowerCase()))) {
                    filteredList.add(item);
                } else if (item.getText() != null && item.getText().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                } else if (item.getPackageName() != null && item.getPackageName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }

        return filteredList;

    }

    private void populateNativeAdView(NativeAd nativeAd,
                                      NativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }

    private class DataItem {

        private final long id;
        String time;
        private String packageName;
        private String appName;
        private String text;
        private String preview;
        private String date;
        private boolean showDate;

        DataItem(Context context, long id, String str) {
            this.id = id;
            try {
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
                DateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
                time = df.format(json.optLong("systemTime"));
                showDate = true;
            } catch (JSONException e) {
                if (Const.DEBUG) e.printStackTrace();
            }
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