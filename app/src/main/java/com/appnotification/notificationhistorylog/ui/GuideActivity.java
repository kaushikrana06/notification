package com.appnotification.notificationhistorylog.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.appnotification.notificationhistorylog.CommonCl.SharedCommon;
import com.appnotification.notificationhistorylog.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdView;

import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1;

public class GuideActivity extends AppCompatActivity {

    private AdView mAdView;
    private AdListener adListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);


        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView backButton = toolbar.findViewById(R.id.back_image);
        TextView titleTextView = toolbar.findViewById(R.id.title_text);
        ImageView searchButton = toolbar.findViewById(R.id.search_image);
        ImageView menuButton = toolbar.findViewById(R.id.menu_image);
        searchButton.setVisibility(View.GONE);
        menuButton.setVisibility(View.INVISIBLE);

        titleTextView.setText("");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //ADS+Firebase
        mAdView = new com.facebook.ads.AdView(this, getString(R.string.fb_banner_ad), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        showads();
        mAdView.loadAd(mAdView.buildLoadAdConfig().withAdListener(adListener).build());
        ((LinearLayout) findViewById(R.id.fb_container)).addView(mAdView);
    }

    private void showads() {
        adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {
                SharedCommon sc = new SharedCommon();
                int i = SharedCommon.getPreferencesInt(getApplicationContext(), key1, 0);
                i++;
                SharedCommon.putPreferencesInt(getApplicationContext(), SharedCommon.key1, i);
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
