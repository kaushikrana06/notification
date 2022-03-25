package com.appnotification.notificationhistorylog.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appnotification.notificationhistorylog.CommonCl.SharedCommon;
import com.appnotification.notificationhistorylog.R;
import com.appnotification.notificationhistorylog.ads.InterstitialAdsManager;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdView;

import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1;


public class FavoritesActivity extends AppCompatActivity {

    private FavoriteAdapter adapter;
    private AdView mAdView;
    private AdListener adListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView backButton = toolbar.findViewById(R.id.back_image);
        TextView titleTextView = toolbar.findViewById(R.id.title_text);
        ImageView searchButton = toolbar.findViewById(R.id.search_image);
        ImageView menuButton = toolbar.findViewById(R.id.menu_image);
        searchButton.setVisibility(View.GONE);
        menuButton.setVisibility(View.INVISIBLE);

        titleTextView.setText(getString(R.string.favorites));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        InterstitialAdsManager adsManager = new InterstitialAdsManager(this);
        adsManager.show();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.favorites_list);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FavoriteAdapter(this, true);
        recyclerView.setAdapter(adapter);

//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//                if (direction == ItemTouchHelper.LEFT) {
//                    adapter.deleteFromFavorite(viewHolder.getAdapterPosition());
//                }
//
//            }
//
//            @Override
//            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//                if (viewHolder != null) {
//                    final View foregroundView = ((FavoriteViewHolder) viewHolder).container;
//                    getDefaultUIUtil().onSelected(foregroundView);
//                }
//            }
//
//            @Override
//            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//                final View foregroundView = ((FavoriteViewHolder) viewHolder).container;
//                getDefaultUIUtil().clearView(foregroundView);
//            }
//
//            @Override
//            public void onChildDraw(Canvas c, RecyclerView recyclerView,
//                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
//                                    int actionState, boolean isCurrentlyActive) {
//                final View foregroundView = ((FavoriteViewHolder) viewHolder).container;
//
//                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
//                        actionState, isCurrentlyActive);
//
//            }
//
//            @Override
//            public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
//                                        RecyclerView.ViewHolder viewHolder, float dX, float dY,
//                                        int actionState, boolean isCurrentlyActive) {
//                final View foregroundView = ((FavoriteViewHolder) viewHolder).container;
//                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
//                        actionState, isCurrentlyActive);
//                if (getSwipeDirs(recyclerView, viewHolder) == ItemTouchHelper.RIGHT) {
////                    ((FavoriteViewHolder) viewHolder).viewBackground_delete.setBackgroundColor(getResources().getColor(R.color.green));
////                    ((FavoriteViewHolder) viewHolder).option_text.setText("ADD TO FAVORITES");
//                }
//            }
//
//
//        }).attachToRecyclerView(recyclerView);


        if (adapter.getItemCount() == 0) {
            Toast.makeText(this, getString(R.string.message_no_favorite), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

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
