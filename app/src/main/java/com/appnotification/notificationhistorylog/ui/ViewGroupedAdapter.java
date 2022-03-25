package com.appnotification.notificationhistorylog.ui;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appnotification.notificationhistorylog.R;
import com.appnotification.notificationhistorylog.misc.Util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ViewGroupedAdapter extends RecyclerView.Adapter<BrowseViewHolder> {

    private final HashMap<String, Drawable> iconCache;
    private final Activity context;
    private final DateFormat format = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
    private ArrayList<HelperObject> data;

    public ViewGroupedAdapter(Activity context, ArrayList<HelperObject> data) {
        this.context = context;
        this.data = data;
        iconCache = new HashMap<>();
        for (HelperObject itm : data) {
            if (!iconCache.containsKey(itm.getPackageName())) {
                iconCache.put(itm.getPackageName(), Util.getAppIconFromPackage(context, itm.getPackageName()));
            }
        }
    }

    @NonNull
    @Override
    public BrowseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stats, parent, false);
        return new BrowseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrowseViewHolder vh, int position) {
        HelperObject item = data.get(position);
        if (iconCache.get(item.getPackageName()) != null && iconCache.containsKey(item.getPackageName())) {
            vh.icon.setImageDrawable(iconCache.get(item.getPackageName()));
        } else {
            vh.icon.setImageResource(R.mipmap.ic_launcher);
        }

        vh.name.setText(item.getTitle());
        vh.text.setText(item.getPackageName());
        vh.preview.setText(context.getString(R.string.total_notification)+ " " + item.getNotificationCount());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
