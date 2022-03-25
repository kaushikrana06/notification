package com.appnotification.notificationhistorylog.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appnotification.notificationhistorylog.CommonCl.SharedCommon;
import com.appnotification.notificationhistorylog.R;
import com.appnotification.notificationhistorylog.ads.InterstitialAdsManager;
import com.appnotification.notificationhistorylog.ui.CalenderActivity;
import com.appnotification.notificationhistorylog.ui.CallsActivity;
import com.appnotification.notificationhistorylog.ui.FacebookActivity;
import com.appnotification.notificationhistorylog.ui.GmailActivity;
import com.appnotification.notificationhistorylog.ui.InstaActivity;
import com.appnotification.notificationhistorylog.ui.TelegramActivity;
import com.appnotification.notificationhistorylog.ui.WhatsappActivity;
import com.appnotification.notificationhistorylog.ui.adapters.AppsAdapter;
import com.appnotification.notificationhistorylog.ui.items.AppItem;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdView;

import timber.log.Timber;

import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1;

public class AppsActivity extends AppCompatActivity implements AppsAdapter.OnAppClickListener {

    private AdView mAdView;
    private AdListener adListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView backButton = toolbar.findViewById(R.id.back_image);
        TextView titleTextView = toolbar.findViewById(R.id.title_text);
        ImageView searchButton = toolbar.findViewById(R.id.search_image);
        ImageView menuButton = toolbar.findViewById(R.id.menu_image);
        searchButton.setVisibility(View.GONE);
        menuButton.setVisibility(View.INVISIBLE);

        titleTextView.setText(getString(R.string.nav_apps));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        InterstitialAdsManager adsManager = new InterstitialAdsManager(this);
        adsManager.show();

        RecyclerView appsRecycler = findViewById(R.id.app_recycler);

        String[] apps = getResources().getStringArray(R.array.apps);
        AppItem[] items = new AppItem[apps.length];
        for (int i = 0; i < apps.length; i++) {
            Timber.i("Apps: %s", apps[i]);
            AppItem app = new AppItem();
            app.setName(apps[i]);
            switch (i) {
                case 0: {
                    // Whatsapp
                    app.setIcon(R.drawable.ic_whatsapp);
                }
                break;
                case 1: {
                    // Gmail
                    app.setIcon(R.drawable.ic_gmail);
                }
                break;
                case 2: {
                    // Facebook
                    app.setIcon(R.drawable.ic_facebook);
                }
                break;
                case 3: {
                    // Instagram
                    app.setIcon(R.drawable.ic_instagram);
                }
                break;
                case 4: {
                    // Calendar
                    app.setIcon(R.drawable.ic_calendar);
                }
                break;
                case 5: {
                    // Telegram
                    app.setIcon(R.drawable.ic_telegram);
                }
                break;
                case 6: {
                    if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                        app.setIcon(R.drawable.ic_phone_white);
                    } else {
                        app.setIcon(R.drawable.ic_phone);
                    }
                }
                break;
            }
            items[i] = app;
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        AppsAdapter appsAdapter = new AppsAdapter(items, this);
        appsRecycler.setLayoutManager(layoutManager);
        appsRecycler.setAdapter(appsAdapter);


        //ADS+Firebase
        mAdView = new com.facebook.ads.AdView(this, getString(R.string.fb_banner_ad), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        showads();
        mAdView.loadAd(mAdView.buildLoadAdConfig().withAdListener(adListener).build());
        ((LinearLayout) findViewById(R.id.fb_container)).addView(mAdView);
    }

    @Override
    public void onAppClick(String appName, int position) {
        Intent startIntent = null;
        switch (position) {
            case 0:
                startIntent = new Intent(this, WhatsappActivity.class);
                break;
            case 1:
                startIntent = new Intent(this, GmailActivity.class);
                break;
            case 2:
                startIntent = new Intent(this, FacebookActivity.class);
                break;
            case 3:
                startIntent = new Intent(this, InstaActivity.class);
                break;
            case 4:
                startIntent = new Intent(this, CalenderActivity.class);
                break;
            case 5:
                startIntent = new Intent(this, TelegramActivity.class);
                break;
            case 6:
                startIntent = new Intent(this, CallsActivity.class);
                break;
        }
        if (startIntent != null) {
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }
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