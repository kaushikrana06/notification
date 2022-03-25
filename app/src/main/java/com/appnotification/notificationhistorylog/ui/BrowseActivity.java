package com.appnotification.notificationhistorylog.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.appnotification.notificationhistorylog.BuildConfig;
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon;
import com.appnotification.notificationhistorylog.R;
import com.appnotification.notificationhistorylog.ads.InterstitialAdsManager;
import com.appnotification.notificationhistorylog.misc.Const;
import com.appnotification.notificationhistorylog.misc.DatabaseHelper;
import com.appnotification.notificationhistorylog.misc.ExportTask;
import com.appnotification.notificationhistorylog.misc.Util;
import com.appnotification.notificationhistorylog.service.NotificationHandler;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.quinny898.library.persistentsearch.SearchBox;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1;


public class BrowseActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    public String whatnew;
    public String showallnotf;
    public String shownotif;
    public String showtuto;
    public String showfav;
    public String livenotice;
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;
    Boolean isSearch;
    BrowseAdapter mAdapter;
    String appid = BuildConfig.APPLICATION_ID;
    FirebaseRemoteConfig firebaseRemoteConfigprice;
    private SearchBox search;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdView mAdView;
    private FirebaseAuth mAuth;
    private EditText searchEdit;

    private AdListener adListener;
    private ImageView backButton;
    private ImageView searchButton;
    private ImageView menuButton;
    private TextView titleTextView;


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
                                whatnew = (firebaseRemoteConfigprice.getString("showads"));

                                if (whatnew.equals("yes")) {
                                    showads();
                                    //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
                                } else if (whatnew.equals("no")) {
                                    mAdView.setVisibility(View.GONE);
                                    // Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();

                                    Timber.i("AdsStatus Not Showing");

                                }
                                Timber.i("firebaseremote%s", firebaseRemoteConfigprice.getString("btn_text"));
                            } else {
                                String exp = ("" + task.getException().getMessage());
                                if (exp.equals("null")) {
                                    whatnew = ("Server Not Responding ");
                                } else {
                                    Timber.i("taskexcep :" + task.getException().getMessage() + task.getException() + task);
                                    Toast.makeText(BrowseActivity.this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);


        Toolbar toolbar = findViewById(R.id.toolbar);
        backButton = toolbar.findViewById(R.id.back_image);
        titleTextView = toolbar.findViewById(R.id.title_text);
        searchButton = toolbar.findViewById(R.id.search_image);
        menuButton = toolbar.findViewById(R.id.menu_image);

        titleTextView.setText(getString(R.string.nav_browse));

        backButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        menuButton.setOnClickListener(this);


        //Ad
        InterstitialAdsManager adsManager = new InterstitialAdsManager(this);
        adsManager.show();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = findViewById(R.id.swiper);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);
        try {
            doFirstRunsecond();
        } catch (Exception e) {
            e.printStackTrace();
        }

        update();

        //Search

       /* search = (SearchBox) findViewById(R.id.searchbox);
        search.enableVoiceRecognition(this);
        for(int x = 0; x < 10; x++){
            SearchResult option = new SearchResult("Result " + Integer.toString(x), getResources().getDrawable(R.drawable.ic_access_time_black_24dp));
            search.addSearchable(option);
        }
        search.setMenuListener(new SearchBox.MenuListener(){

            @Override
            public void onMenuClick() {
                //Hamburger has been clicked
                Toast.makeText(BrowseActivity.this, "Menu click", Toast.LENGTH_LONG).show();
            }

        });
        search.setSearchListener(new SearchBox.SearchListener(){

            @Override
            public void onSearchOpened() {
                //Use this to tint the screen
            }

            @Override
            public void onSearchClosed() {
                //Use this to un-tint the screen
            }

            @Override
            public void onSearchTermChanged(String term) {
                //React to the search term changing
                //Called after it has updated results
            }

            @Override
            public void onSearch(String searchTerm) {
                Toast.makeText(BrowseActivity.this, searchTerm +" Searched", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResultClick(SearchResult result) {
                //React to a result being clicked
            }

            @Override
            public void onSearchCleared() {
                //Called when the clear button is clicked
            }

        });
        search.setOverflowMenu(R.menu.overflow_menu);
        search.setOverflowMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.test_menu_item:
                        Toast.makeText(BrowseActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });*/

        searchEdit = findViewById(R.id.edit_search);
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                BrowseAdapter mAdapter = new BrowseAdapter(BrowseActivity.this);
                mAdapter.filterList(mAdapter.filter(editable.toString()));
                recyclerView.setAdapter(mAdapter);
            }
        });
        //ADS+Firebase
        mAdView = new com.facebook.ads.AdView(this, getString(R.string.fb_banner_ad), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        mAdView.loadAd(mAdView.buildLoadAdConfig().withAdListener(adListener).build());
        ((LinearLayout) findViewById(R.id.fb_container)).addView(mAdView);
        firebaseRemoteConfigprice = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
        firebaseRemoteConfigprice.setConfigSettingsAsync(configSettings);

        Map<String, Object> pricedata = new HashMap<>();
        pricedata.put("showads", "yn");

        firebaseRemoteConfigprice.setDefaultsAsync(pricedata);
        checkadstatus();
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

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null && DetailsActivity.ACTION_REFRESH.equals(data.getStringExtra(DetailsActivity.EXTRA_ACTION))) {
            update();
        }
      /*  if (requestCode == 1234 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            search.populateEditText(matches.get(0));
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.browse, menu);
       /* MenuItem searchViewItem = menu.findItem(R.id.menu_search);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();

              //  appseachquery(query.toString());
                //Toast.makeText(FAQActivity.this, ""+query, Toast.LENGTH_SHORT).show();



                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //Toast.makeText(FAQActivity.this, ""+newText, Toast.LENGTH_SHORT).show();
                return false;
            }
        });*/
        return super.onCreateOptionsMenu(menu);
    }

//    private void appseachquery(final String query) {
//
//
//        final View parentLayout = findViewById(android.R.id.content);
//
//        Snackbar snackbar1 = Snackbar
//                .make(parentLayout, "Unable To Find Your Query", Snackbar.LENGTH_LONG)
//                .setAction("Send Mail", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Toast toast = Toast.makeText(BrowseActivity.this, "Mail Us Your Query", Toast.LENGTH_LONG);
//                        View view1 = toast.getView();
//
//                        view1.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
//
//                        TextView text = view1.findViewById(android.R.id.message);
//                        text.setTextColor(Color.BLACK);
//
//                        toast.show();
//
//                        Intent send = new Intent(Intent.ACTION_SENDTO);
//                        String uriText = "mailto:" + Uri.encode("thexenonstudio@gmail.com") +
//                                "?subject=" + Uri.encode("Around Me - FAQs") +
//                                "&body=" + Uri.encode("" + query + " \n\n\n ------------ \n\n Version Code : " + versionCode + "\n Version Name : " + versionName + "\n Application ID : " + appid);
//                        Uri uri = Uri.parse(uriText);
//
//                        send.setData(uri);
//                        startActivity(Intent.createChooser(send, "Send Mail Via : "));
//
//                    }
//                });
//        //Query firebaseSearchQuery = mUserDatabase.orderByChild("Items").startAt(query).endAt(query + "\uf8ff");
//
//        //Toast.makeText(this, ""+query, Toast.LENGTH_SHORT).show();
//
//        /*if(query.equals("Hello")){
//
//            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
//        }
//*/
//
//
//        if (query.isEmpty()) {
//
//            //final View parentLayout = findViewById(android.R.id.content);
//
//
//            Toast.makeText(this, "Please Search Any Query ", Toast.LENGTH_SHORT).show();
//
//            //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                update();
                return true;
            case R.id.menu_help_browse:
                sendingbaby();
                return true;
            case R.id.menu_search:
                search();
                return true;
            case R.id.menu_delete:
                if (Util.isNotificationAccessEnabled(getApplicationContext())) {
                    confirm();
                } else {
                    openDialog();
                }
                return true;
            case R.id.menu_export:
                if (Util.isNotificationAccessEnabled(getApplicationContext())) {
                    export();
                } else {
                    openDialog();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle(R.string.alert)
                .setMessage(R.string.message_allow_permission)
                .setCancelable(true)
                .setPositiveButton(R.string.text_ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positive.setTextColor(getResources().getColor(R.color.colorText));
    }

    private void sendingbaby() {
//        Toast toast = Toast.makeText(this, "Mail Us For More Detail", Toast.LENGTH_LONG);
//        View view = toast.getView();
//        view.getBackground().setColorFilter((Color.parseColor("#FF104162")), PorterDuff.Mode.SRC_IN);
//        TextView text = view.findViewById(android.R.id.message);
//        text.setTextColor(Color.WHITE);
//        toast.show();
        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode(Const.EMAIL) +
                "?subject=" + Uri.encode("Notification Log App") +
                "&body=" + Uri.encode("Hello, Type Your Query/Problem/Bug/Suggestions Here" + " \n\n\n ------------ \n\n Version Code : " + versionCode + "\n Version Name : " + versionName);
        Uri uri = Uri.parse(uriText);
        send.setData(uri);
        startActivity(Intent.createChooser(send, "Send Mail Via : "));
    }

    private void update() {
      /*  BrowseAdapter adapter = new BrowseAdapter(this,"all");
        recyclerView.setAdapter(adapter);

        if(adapter.getItemCount() == 0) {
            Toast.makeText(this, R.string.empty_log_file, Toast.LENGTH_LONG).show();
            finish();
        }*/

        BrowseAdapter adapter = new BrowseAdapter(this);
        recyclerView.setAdapter(adapter);


        if (adapter.getItemCount() == 0) {
            Toast.makeText(this, R.string.empty_log_file, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void confirm() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle(R.string.dialog_delete_header);
        builder.setMessage(R.string.dialog_delete_text);
        builder.setNegativeButton(R.string.dialog_delete_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.dialog_delete_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                truncate();
                sendnotification();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positive.setTextColor(getResources().getColor(R.color.colorText));
        Button negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negative.setTextColor(getResources().getColor(R.color.colorText));
    }

    private void truncate() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL(DatabaseHelper.SQL_DELETE_ENTRIES_POSTED);
            db.execSQL(DatabaseHelper.SQL_CREATE_ENTRIES_POSTED);
            db.execSQL(DatabaseHelper.SQL_DELETE_ENTRIES_REMOVED);
            db.execSQL(DatabaseHelper.SQL_CREATE_ENTRIES_REMOVED);
            Intent local = new Intent();
            local.setAction(NotificationHandler.BROADCAST);
            LocalBroadcastManager.getInstance(this).sendBroadcast(local);

            startActivity(new Intent(BrowseActivity.this, BrowseActivity.class));
            finish();
        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
        }
    }

    private void sendnotification() {

        int requestID = (int) System.currentTimeMillis();

        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),
                0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder buildernotif = new NotificationCompat.Builder(getBaseContext());

        Intent pauseIntent = new Intent(this, NewMainActivity.class);
        pauseIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pauseIntent.putExtra("pause", true);
        PendingIntent pausePendingIntent = PendingIntent.getActivity(this, requestID, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        buildernotif.setAutoCancel(true);

        Intent cancelIntent = new Intent(this, NewMainActivity.class);
        buildernotif.setAutoCancel(true);

        buildernotif.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notificationlogo)
                .setContentTitle(getString(R.string.deleted_success))
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))

                /*.addAction(R.drawable.ic_action_boom, "Action!", someOtherPendingIntent)*/
                .setContentText(getString(R.string.message_deleted_success))
                .setContentIntent(pausePendingIntent);

//Then add the action to your notification

        NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, buildernotif.build());

    }

    private void export() {
        if (!ExportTask.exporting) {
            ExportTask exportTask = new ExportTask(this, findViewById(android.R.id.content));
            exportTask.execute();
        }
    }

    @Override
    public void onRefresh() {
        update();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void doFirstRunsecond() {
        SharedPreferences settings = getSharedPreferences("FIRSTRUNTEXT", MODE_PRIVATE);
        if (settings.getBoolean("isFirstRunDialogBoxtext", true)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunDialogBoxtext", false);
            editor.apply();

            AlertDialog.Builder builder = new AlertDialog.Builder(BrowseActivity.this, R.style.DialogTheme);
            builder.setIcon(R.drawable.ic_splash_logo);
            builder.setTitle(R.string.alert_title_tip);
            builder.setMessage(R.string.alert_message_tip);

            builder.setCancelable(false);

            builder.setPositiveButton(R.string.button_got_it, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positive.setTextColor(getResources().getColor(R.color.colorText));
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_image:
                finish();
                break;
            case R.id.search_image:
                search();
                break;
            case R.id.menu_image:
                PopupMenu popup = new PopupMenu(this, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_refresh:
                                update();
                                return true;
                            case R.id.menu_help_browse:
                                sendingbaby();
                                return true;
                            case R.id.menu_search:
                                search();
                                return true;
                            case R.id.menu_delete:
                                if (Util.isNotificationAccessEnabled(getApplicationContext())) {
                                    confirm();
                                } else {
                                    openDialog();
                                }
                                return true;
                            case R.id.menu_export:
                                if (Util.isNotificationAccessEnabled(getApplicationContext())) {
                                    export();
                                } else {
                                    openDialog();
                                }
                                return true;

                        }
                        return false;
                    }
                });
                popup.inflate(R.menu.browse);
                popup.show();
                break;
        }
    }
}
