package com.appnotification.notificationhistorylog.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.appnotification.notificationhistorylog.BuildConfig;
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon;
import com.appnotification.notificationhistorylog.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final int NUMBER_OF_ADS = 10;
    private final List<NativeAd> mNativeAds = new ArrayList<>();
    public ImageView imgmore, imgemnu;
    //    private AdView mAdView;
    public String whatnew;
    public String adappid;
    public String nativeadid;
    public String hisormine;
    public String eroorcode;
    public String livenotice;
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;
    String appid = BuildConfig.APPLICATION_ID;
    FirebaseRemoteConfig firebaseRemoteConfigprice;
    private DatabaseReference mUserRef, museref, mdatareport, mcredtref, mlinkupdate;
    private AdLoader adLoader;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView selectedNavigation;
    private EditText searchEdit;
    private AdView mAdView;
    private com.facebook.ads.AdListener adListener;

    private boolean isSearchVisible = false;

    public RecentsFragment() {
        // Required empty public constructor
    }


    private void showads() {
        adListener = new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Timber.e("FacebookAdError: " + adError.getErrorCode() + " : " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {
                SharedCommon sc = new SharedCommon();
                int i = SharedCommon.getPreferencesInt(getContext(), key1, 0);
                i++;
                SharedCommon.putPreferencesInt(getContext(), SharedCommon.key1, i);
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_recents, container, false);
        isSearchVisible = false;
//        getActivity().setTitle("Notifications");

        Bundle bundle = this.getArguments();
//        String fragmentName = bundle.getString("selected_navigation");

        // Initialize the Mobile Ads SDK.
        if (savedInstanceState == null) {
            //loadNativeAds();
            Timber.i("nativeads LOG");

        }
        //selectedNavigation = view.findViewById(R.id.selected_navigation);

        //selectedNavigation.setText(fragmentName);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(layoutManager);
        imgemnu = view.findViewById(R.id.imageView2);
        imgemnu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NewMainActivity) getActivity()).openDrawer();
            }
        });
        imgmore = view.findViewById(R.id.imgmore);
        imgmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NewMainActivity) getActivity()).showPopup(view);
                // ((NewMainActivity)getActivity()).openactionmenu();
            }
        });


        ImageView searchButton = view.findViewById(R.id.imgsearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View editView = getActivity().getCurrentFocus();
                if (editView != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editView.getWindowToken(), 0);
                }
                Timber.i("onClick: SearchButton: isSearchVisible:  %s", isSearchVisible);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isSearchVisible) {
                            isSearchVisible = false;
                            searchEdit.setText("");
                            searchEdit.setVisibility(View.GONE);
                        } else {
                            isSearchVisible = true;
                            searchEdit.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        });

        swipeRefreshLayout = view.findViewById(R.id.swiper);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        update();
        searchEdit = view.findViewById(R.id.edit_search);
        searchEdit.setCursorVisible(false);
        searchEdit.setVisibility(View.GONE);
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchEdit.setCursorVisible(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                BrowseAdapterHome mAdapter = new BrowseAdapterHome(getActivity(), mNativeAds);
                mAdapter.filterList(mAdapter.filter(editable.toString()));
                recyclerView.setAdapter(mAdapter);
                searchEdit.setCursorVisible(true);

                firebasedatabseupdate(editable.toString());
            }
        });
        //ADS+Firebase
        //mAdView = findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        // mAdView.loadAd(adRequest);
        mAdView = new com.facebook.ads.AdView(getContext(), getString(R.string.fb_banner_ad), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        showads();
        mAdView.loadAd(mAdView.buildLoadAdConfig().withAdListener(adListener).build());
        ((LinearLayout) view.findViewById(R.id.fb_container)).addView(mAdView);
        firebaseRemoteConfigprice = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
        firebaseRemoteConfigprice.setConfigSettingsAsync(configSettings);

        Map<String, Object> pricedata = new HashMap<>();
        pricedata.put("shownativeads", "yn");
        pricedata.put("appid", "yn");
        pricedata.put("nativeadid", "yn");
        pricedata.put("hisormine", "yn");

        firebaseRemoteConfigprice.setDefaultsAsync(pricedata);
        checkadstatus();

        return view;
    }

    private void checkadstatus() {
        {


            firebaseRemoteConfigprice.fetch(0)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Timber.i("info%s", firebaseRemoteConfigprice.getInfo().getLastFetchStatus());


                            Timber.i("firebaseremote%s", firebaseRemoteConfigprice.getString("btn_text"));

                            if (task.isSuccessful()) {


                                firebaseRemoteConfigprice.fetchAndActivate();
                                whatnew = (firebaseRemoteConfigprice.getString("shownativeads"));
                                adappid = (firebaseRemoteConfigprice.getString("appid"));
                                nativeadid = (firebaseRemoteConfigprice.getString("nativeadid"));
                                hisormine = (firebaseRemoteConfigprice.getString("hisormine"));

                                Timber.i("adappid : %s", adappid);
                                Timber.i("nativeadid : %s", nativeadid);


                                if (whatnew.equals("yes")) {
                                    loadNativeAds();

                                    //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
                                } else if (whatnew.equals("no")) {
                                    mNativeAds.remove(true);
                                    // Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
                                    Timber.i("AdsStatus Not Showing");

                                }
                               /* if (hisormine.equals("mine")){
                                    loadNativeAds();

                                    //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
                                } else if (hisormine.equals("his")){
                                    adappid = "";
                                    nativeadid = nativeadid;

                                    // Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
                                    Log.e("AdsStatus","his");

                                }
                                else if (hisormine.equals("test")){
                                    adappid = "ca-app-pub-8081344892743036~8262343723";
                                    nativeadid = "ca-app-pub-3940256099942544/2247696110";

                                    // Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
                                    Log.e("AdsStatus","his");

                                }*/


                                Timber.i("firebaseremote%s", firebaseRemoteConfigprice.getString("btn_text"));

                            } else {


                                String exp = ("" + task.getException().getMessage());
                                if (exp.equals("null")) {

                                    whatnew = ("Server Not Responding ");
                                } else {
                                    Timber.i("taskexcep :" + task.getException().getMessage() + task.getException() + task);
                                    Toast.makeText(getActivity(), getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }

    }

    @Override
    public void onDetach() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDetach();
    }

    private void loadNativeAds() {

        AdLoader.Builder builder = new AdLoader.Builder(getActivity(), getString(R.string.ad_native_unit_id));
        builder.forNativeAd(unifiedNativeAd -> {

            mNativeAds.add(unifiedNativeAd);
            if (!adLoader.isLoading()) {
                update();
            }
        });
        builder.withAdListener(
                new AdListener() {
                });
        adLoader = builder.build();

        // Load the Native ads.
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);

//        final NativeAd nativeAd = new NativeAd(getContext(), getString(R.string.fb_native_ad));
//        nativeAd.loadAd();
//
//        if (nativeAd.isAdLoaded()) {
//            inflateAd(getContext(), nativeAd, nativeAdLayout);
//        }
    }

//    public static void inflateAd(Context context, NativeAd nativeAd, NativeAdLayout nativeAdLayout) {
//        nativeAd.unregisterView();
//        try {
//            int i = 0;
//            View view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.native_ad_layout_1, nativeAdLayout, false);
//            nativeAdLayout.addView(view);
//            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ad_choices_container);
//            AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, nativeAdLayout);
//            linearLayout.removeAllViews();
//            linearLayout.addView(adOptionsView, 0);
//            TextView textView = (TextView) view.findViewById(R.id.native_ad_title);
//            MediaView mediaView2 = (MediaView) view.findViewById(R.id.native_ad_media);
//            TextView textView2 = (TextView) view.findViewById(R.id.native_ad_social_context);
//            TextView textView3 = (TextView) view.findViewById(R.id.native_ad_body);
//            TextView textView4 = (TextView) view.findViewById(R.id.native_ad_sponsored_label);
//            Button button = (Button) view.findViewById(R.id.native_ad_call_to_action);
//            textView.setText(nativeAd.getAdvertiserName());
//            textView3.setText(nativeAd.getAdBodyText());
//            textView2.setText(nativeAd.getAdSocialContext());
//            if (!nativeAd.hasCallToAction()) {
//                i = 4;
//            }
//            button.setVisibility(i);
//            button.setText(nativeAd.getAdCallToAction());
//            textView4.setText(nativeAd.getSponsoredTranslation());
//            List arrayList = new ArrayList();
//            arrayList.add(textView);
//            arrayList.add(button);
//            nativeAd.registerViewForInteraction(view, mediaView2, arrayList);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    private void firebasedatabseupdate(String s) {
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            sendToStart();

        } else {


            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault());

            String datte = format.format(new Date());
            String idtimedate = datte.substring(5, 10);
            String idtime = datte.substring(11, 19);
            mdatareport = FirebaseDatabase.getInstance().getReference().child("UsageReports").child(mAuth.getCurrentUser().getUid());
            museref = FirebaseDatabase.getInstance().getReference().child("mainacreport").child(mAuth.getCurrentUser().getUid());

            mUserRef = FirebaseDatabase.getInstance().getReference().child("Search-Query").child(mAuth.getCurrentUser().getUid());
            BrowseAdapterHome adapter = new BrowseAdapterHome(getActivity(), mNativeAds);

            String coint = String.valueOf(adapter.getItemCount());

            mUserRef.child("Last-Query").setValue(s);
            mUserRef.child("Log-Count-While-Searching").setValue(coint);
            mUserRef.child("Last-Query-Time").setValue(datte);
            mUserRef.child("ADERCO").setValue(eroorcode);

            //username = currentUser.getUid();
            //SAVEDATAREPORT
            //savereport(currentUser);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && DetailsActivity.ACTION_REFRESH.equals(data.getStringExtra(DetailsActivity.EXTRA_ACTION))) {
            update();
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void sendToStart() {

        //username = "Not Signed In";
        //Toast.makeText(this, "Not Signed", Toast.LENGTH_SHORT).show();;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                update();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    /*    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }*/
    private void search() {
        if (searchEdit.getVisibility() == View.VISIBLE) {
            searchEdit.setVisibility(View.GONE);
            searchEdit.setText("");
            update();
           /* InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);*/
        } else {
            searchEdit.setVisibility(View.VISIBLE);
           /* searchEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchEdit, InputMethodManager.SHOW_IMPLICIT);*/
        }

    }

    private void update() {
        BrowseAdapterHome adapter = new BrowseAdapterHome(getActivity(), mNativeAds);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            Toast.makeText(getActivity(), getString(R.string.message_no_notify_yet), Toast.LENGTH_SHORT).show();

            /*Toast.makeText(getContext(), R.string.empty_log_file, Toast.LENGTH_LONG).show();
            Intent startIntent = new Intent(getActivity(), IssueActivity.class);
            startActivity(startIntent);*/
        }
    }

    @Override
    public void onRefresh() {
        update();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void refreshAdapter() {
        update();
        swipeRefreshLayout.setRefreshing(false);
    }
}
