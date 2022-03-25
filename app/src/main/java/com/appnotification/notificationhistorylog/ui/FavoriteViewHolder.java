package com.appnotification.notificationhistorylog.ui;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.appnotification.notificationhistorylog.R;


public class FavoriteViewHolder extends RecyclerView.ViewHolder {

    ConstraintLayout container;
    ImageView appIcon;
    TextView appName;
    TextView title;
    TextView preview;
    TextView date;
    Button remove;


    FavoriteViewHolder(View view) {
        super(view);
        container = view.findViewById(R.id.container);
        appIcon = view.findViewById(R.id.app_icon);
        appName = view.findViewById(R.id.app_name);
        title = view.findViewById(R.id.message_title);
        preview = view.findViewById(R.id.message_preview);
        date = view.findViewById(R.id.msg_date);
        remove = view.findViewById(R.id.button_remove);
    }

}
