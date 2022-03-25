package com.appnotification.notificationhistorylog.ads;

import android.app.Activity;

import com.appnotification.notificationhistorylog.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;

public class RewardedAdsManager implements RewardedVideoAdListener {
    private final RewardedVideoAd rewardedVideoAd;

    public RewardedAdsManager(Activity activity) {
        rewardedVideoAd = new RewardedVideoAd(activity, activity.getString(R.string.fb_interstitial_ad));
        rewardedVideoAd.loadAd(rewardedVideoAd.buildLoadAdConfig()
                .withAdListener(this)
                .build());
    }

    public void show() {
        if (rewardedVideoAd.isAdLoaded()) rewardedVideoAd.show();
    }


    @Override
    public void onRewardedVideoCompleted() {

    }

    @Override
    public void onError(Ad ad, AdError adError) {

    }

    @Override
    public void onAdLoaded(Ad ad) {

    }

    @Override
    public void onAdClicked(Ad ad) {

    }

    @Override
    public void onLoggingImpression(Ad ad) {

    }

    @Override
    public void onRewardedVideoClosed() {

    }
}
