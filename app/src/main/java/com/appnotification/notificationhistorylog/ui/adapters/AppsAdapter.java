package com.appnotification.notificationhistorylog.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.appnotification.notificationhistorylog.R;
import com.appnotification.notificationhistorylog.ui.items.AppItem;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {
    private final AppItem[] apps;
    private final OnAppClickListener listener;

    // RecyclerView recyclerView;
    public AppsAdapter(AppItem[] apps, OnAppClickListener listener) {
        this.apps = apps;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.app_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AppItem appItem = apps[position];
        holder.appName.setText(appItem.getName());
        holder.appIcon.setImageResource(appItem.getIcon());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onAppClick(appItem.getName(), position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return apps.length;
    }

    public interface OnAppClickListener {
        void onAppClick(String appName, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView appIcon;
        public TextView appName;
        public LinearLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            this.appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
            this.appName = (TextView) itemView.findViewById(R.id.app_name);
            container = (LinearLayout) itemView.findViewById(R.id.app_item_container);
        }
    }
}
