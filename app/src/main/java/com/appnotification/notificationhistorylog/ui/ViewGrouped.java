package com.appnotification.notificationhistorylog.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.appnotification.notificationhistorylog.BuildConfig;
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon;
import com.appnotification.notificationhistorylog.R;
import com.appnotification.notificationhistorylog.ads.InterstitialAdsManager;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1;

public class ViewGrouped extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<GroupedDataItem> newData;
    private ArrayList<HelperObject> holderObjs;
    private RecyclerView recyclerView;
    private ArrayList<BrowseAppAdapter.DataItem> data;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdView mAdView;
    private AdListener adListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_grouped);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView backButton = toolbar.findViewById(R.id.back_image);
        TextView titleTextView = toolbar.findViewById(R.id.title_text);
        ImageView searchButton = toolbar.findViewById(R.id.search_image);
        ImageView menuButton = toolbar.findViewById(R.id.menu_image);
        searchButton.setVisibility(View.GONE);
        menuButton.setVisibility(View.INVISIBLE);

        titleTextView.setText(getString(R.string.nav_stats));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        InterstitialAdsManager adsManager = new InterstitialAdsManager(this);
        adsManager.show();

        swipeRefreshLayout = findViewById(R.id.swiper);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = findViewById(R.id.list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        try {
            update();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //ADS+Firebase
        mAdView = new com.facebook.ads.AdView(this, getString(R.string.fb_banner_ad), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        showads();
        mAdView.loadAd(mAdView.buildLoadAdConfig().withAdListener(adListener).build());
        ((LinearLayout) findViewById(R.id.fb_container)).addView(mAdView);
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menustats, menu);
        return true;
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


//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//
//            case R.id.count:
//                graphinfo();
//                    /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                            new SettingsFragment()).commit();*/
//
//
//                return true;
//
//
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void graphinfo() {
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ViewGrouped.this);
//
//        builder.setIcon(R.drawable.notificationlogo);
//
//        builder.setTitle("Coming Soon!");
//
//        builder.setMessage("Get Insights Of Notifications, View Data Of Notifications Into Graph And Manage Easily");
//        builder.setCancelable(false);
//
//        builder.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//                Intent startIntent = new Intent(ViewGrouped.this, NewMainActivity.class);
//                startActivity(startIntent);
//
//            }
//        });
//        builder.setNegativeButton("Notify Me", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(ViewGrouped.this, "We Will Notify You ", Toast.LENGTH_SHORT).show();
//                dialogInterface.dismiss();
//            }
//        });
//
//
//        android.app.AlertDialog dialog = builder.create();
//        dialog.show();
//    }

    private void update() throws Exception {
        //start filling recycler view
        holderObjs = new ArrayList<>();
        newData = new ArrayList<>();
        data = new ArrayList<>();
        BrowseAppAdapter adapter = new BrowseAppAdapter(this);
        data = adapter.getData();
        for (BrowseAppAdapter.DataItem itm : data) {
            try {
                GroupedDataItem dataItem = new GroupedDataItem(this, itm.getStr());
                if (dataItem != null)
                    newData.add(dataItem);
            } catch (Exception e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
            }
        }

        @SuppressLint({"NewApi", "LocalSuppress"}) Map<String, List<GroupedDataItem>> groupedByPackageName = newData.stream().collect(Collectors.groupingBy(w -> w.getPackageName()));

        for (String str : groupedByPackageName.keySet()) {
            HelperObject obj = new HelperObject();
            obj.setPackageName(str);
            obj.setTitle(groupedByPackageName.get(str).get(0).getAppName());
            obj.setNotificationCount(groupedByPackageName.get(str).size());
            holderObjs.add(obj);
        }

        Collections.sort(holderObjs, new Comparator<HelperObject>() {
            public int compare(HelperObject obj1, HelperObject obj2) {
                // ## Ascending order
//                return obj1.firstName.compareToIgnoreCase(obj2.firstName); // To compare string values
//                return Integer.valueOf(obj1.getNotificationCount()).compareTo(Integer.valueOf(obj2.getNotificationCount())); // To compare integer values

                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                return Integer.compare(obj2.getNotificationCount(), obj1.getNotificationCount()); // To compare integer values
            }
        });
        ViewGroupedAdapter groupedAdapter = new ViewGroupedAdapter(this, holderObjs);
        recyclerView.setAdapter(groupedAdapter);
//        Log.e("Map size","Size: "+groupedByPackageName.size());
//        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            Toast.makeText(this, R.string.empty_log_file, Toast.LENGTH_LONG).show();
            finish();
        }
    }


    @Override
    public void onRefresh() {
        try {
            update();
        } catch (Exception e) {
            e.printStackTrace();
        }
        swipeRefreshLayout.setRefreshing(false);
    }
}
