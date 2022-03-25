package com.appnotification.notificationhistorylog.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.appnotification.notificationhistorylog.R;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

class BrowseViewHolderHome extends RecyclerView.ViewHolder {

    private final NativeAdView adView;
    public ImageView icon;
    public TextView name;
    public TextView preview;
    public TextView text;
    public TextView date;
    public TextView time;
    LinearLayout item;

    BrowseViewHolderHome(View view) {
        super(view);
        item = view.findViewById(R.id.item);
        icon = view.findViewById(R.id.icon);
        name = view.findViewById(R.id.name);
        preview = view.findViewById(R.id.preview);
        text = view.findViewById(R.id.text);
        date = view.findViewById(R.id.date);
        time = view.findViewById(R.id.time);
        adView = view.findViewById(R.id.ad_view);

        // The MediaView will display a video asset if one is present in the ad, and the
        // first image asset otherwise.
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Register the view used for each individual asset.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
    }

    public NativeAdView getAdView() {
        return adView;
    }

}
