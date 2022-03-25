package com.appnotification.notificationhistorylog.ads;

import android.app.Activity;

import com.appnotification.notificationhistorylog.AppPreferenceUtil;
import com.appnotification.notificationhistorylog.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import timber.log.Timber;

public class InterstitialAdsManager implements InterstitialAdListener {
    private final InterstitialAd interstitialAd;
    private final Activity activity;
    private OnAdResponseFinish onAdResponseFinish;

    public InterstitialAdsManager(Activity activity) {
        this.activity = activity;
        interstitialAd = new InterstitialAd(activity, activity.getString(R.string.fb_interstitial_ad));
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(this)
                        .build());
    }

    public void setOnAdFinishListener(OnAdResponseFinish onAdResponseFinish) {
        this.onAdResponseFinish = onAdResponseFinish;
    }

    public void show() {
        int totalBlockCount = AppPreferenceUtil.getInt(activity, "total_block_count", 0);
        Timber.i("TotalAdBlockCount : %s", totalBlockCount);
        if (totalBlockCount == 0 && interstitialAd.isAdLoaded()) {
            interstitialAd.show();
        } else {
            int currentBlockCount = AppPreferenceUtil.getInt(activity, "current_block_count", 0);
            Timber.i("currentBlockCount : %s", currentBlockCount);
            if (currentBlockCount > totalBlockCount) {
                AppPreferenceUtil.removeKey(activity, "total_block_count");
                AppPreferenceUtil.removeKey(activity, "current_block_count");
            } else {
                currentBlockCount++;
                AppPreferenceUtil.saveInt(activity, "current_block_count", currentBlockCount);
            }
        }
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {
        Timber.i("onInterstitialDisplayed.................");
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        Timber.i("onInterstitialDismissed.................");
        if (onAdResponseFinish != null) onAdResponseFinish.onAdFinish();
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        Timber.i("onError.................");
        if (onAdResponseFinish != null) onAdResponseFinish.onAdFinish();
    }

    @Override
    public void onAdLoaded(Ad ad) {
        Timber.i("onAdLoaded.................");
        show();
    }

    @Override
    public void onAdClicked(Ad ad) {
        Timber.i("onAdClicked.................");
        int DEFAULT_BLOCK_COUNT = 3;
        AppPreferenceUtil.saveInt(activity, "total_block_count", DEFAULT_BLOCK_COUNT);
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        Timber.i("onLoggingImpression.................");
    }

    public interface OnAdResponseFinish {
        void onAdFinish();
    }
}