package com.appnotification.notificationhistorylog.ui;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.appnotification.notificationhistorylog.BuildConfig;
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon;
import com.appnotification.notificationhistorylog.R;
import com.appnotification.notificationhistorylog.ads.InterstitialAdsManager;
import com.appnotification.notificationhistorylog.db.DbHelperGmail;
import com.appnotification.notificationhistorylog.misc.Const;
import com.appnotification.notificationhistorylog.misc.DatabaseHelper;
import com.appnotification.notificationhistorylog.misc.Util;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1;

public class DetailsActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_ACTION = "action";
    public static final String ACTION_REFRESH = "refresh";
    private static final boolean SHOW_RELATIVE_DATE_TIME = true;
    public String whatnew;
    public String showallnotf;
    public String shownotif;
    public String showtuto;
    public String showfav;
    public String livenotice;
    public String versionfirebase;
    public com.facebook.ads.AdView fbadview;
    public String texttext;
    //    ImageView ivNotification;
    Animation shake;
    TextView tvName, txtinv;
    String contentText;
    TextView tvText;
    TextView tvDate;
    View view1;
    String txtgmail, txtwhatsapp, txtinstagram;
    //    DbHelperIdeas dbHelper;
    DbHelperGmail dbHelpgmail;
    ArrayAdapter<String> mAdapter, mAdaptergmail;
    ListView lstTask, lstTaskgmail;
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;
    FirebaseRemoteConfig firebaseRemoteConfigprice;
    String appid = BuildConfig.APPLICATION_ID;
    private Button buttonjson;
    private LinearLayout buttonNotifySettings;
    private DatabaseReference mUserRef;
    private String id;
    private final DialogInterface.OnClickListener doDelete = (dialog, which) -> {
        int affectedRows = 0;
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            affectedRows = db.delete(DatabaseHelper.PostedEntry.TABLE_NAME,
                    DatabaseHelper.PostedEntry._ID + " = ?",
                    new String[]{id});
            db.close();
            databaseHelper.close();
            startActivity(new Intent(DetailsActivity.this, NewMainActivity.class));
            finish();
        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
        }

        if (affectedRows > 0) {
            Intent data = new Intent();
            data.putExtra(EXTRA_ACTION, ACTION_REFRESH);
            setResult(RESULT_OK, data);
            finish();
        }
    };
    private com.facebook.ads.AdView mAdView;
    private AdListener adListener;
    private InterstitialAdsManager interstitialAdsManager;
    private String packageName;
    private int appUid;
    private InterstitialAd mInterstitialAd;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAuth mAuth;
    private AlertDialog dialog;
    private TextView textOpenApp;
    private String appJson;

    private void showPopup(View v) {
       /* PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.main, popup.getMenu());
        popup.show();*/
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_share) {
                    shareapplink();
                }
                return false;
            }
        });
        popup.inflate(R.menu.details);
        popup.show();
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        // Action bar button handled
        ImageView backIcon = findViewById(R.id.back_image);
        ImageView deleteIcon = findViewById(R.id.delete_image);
        ImageView favIcon = findViewById(R.id.fav_image);
        ImageView sendIcon = findViewById(R.id.send_image);
        ImageView moreIcon = findViewById(R.id.menu_image);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
            }
        });
        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(shake);
                addToFavorites();
            }
        });
        sendIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailsActivity.this, "Sharing Notification Details..", Toast.LENGTH_SHORT).show();
                ShareCompat.IntentBuilder.from(DetailsActivity.this)
                        .setType("text/plain")
                        .setChooserTitle("")
                        .setText("App :" + tvName.getText() + "\nNotification : " + tvText.getText() + "\nDate & Time :" + tvDate.getText())
                        .startChooser();
            }
        });
        moreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
                // ((NewMainActivity)getActivity()).openactionmenu();
            }
        });

        interstitialAdsManager = new InterstitialAdsManager(this);
        interstitialAdsManager.show();

        buttonNotifySettings = findViewById(R.id.button_notification);
        buttonNotifySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    if (Build.VERSION.SDK_INT > 25) {
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("android.provider.extra.APP_PACKAGE", packageName);
                    } else if (Build.VERSION.SDK_INT >= 21) {
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("app_package", packageName);
                        intent.putExtra("app_uid", appUid);
                    } else {
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + packageName));
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    if (Const.DEBUG) e.printStackTrace();
                }
            }
        });

        textOpenApp = findViewById(R.id.text_openapp);
        LinearLayout buttonOpenApp = findViewById(R.id.button_openapp);
        buttonOpenApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (packageName.contains("systemui")) {
                    //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
                    textOpenApp.setText("System App Cannot Be Opened");
//                    openplay.setVisibility(View.GONE);
                } else if (packageName.equals("android")) {
                    //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
                    textOpenApp.setText("System App Cannot Be Opened");
//                    openplay.setVisibility(View.GONE);
                } else if (packageName.equals("com.motorola.ccc.ota")) {
                    //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
                    textOpenApp.setText("System App Cannot Be Opened");
//                    openplay.setVisibility(View.GONE);
                } else {
                    Toast.makeText(DetailsActivity.this, "Opening..", Toast.LENGTH_SHORT).show();
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                    startActivity(launchIntent);
                }
            }
        });
        TextView tvJSON = findViewById(R.id.json);
        LinearLayout buttonMoreInfo = findViewById(R.id.button_moreinfo);
        buttonMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvJSON.setText(appJson);
                tvJSON.setVisibility(View.VISIBLE);
            }
        });

//        dbHelper = new DbHelperIdeas(this);
        lstTask = findViewById(R.id.lstTaskidea);
        txtinv = findViewById(R.id.txtinv);


        dbHelpgmail = new DbHelperGmail(this);
        lstTaskgmail = findViewById(R.id.lsttakgmail);


        txtinv = findViewById(R.id.txtinv);


//        firstrun();

//        btnselevct = findViewById(R.id.btnselevct);
//        btnselevct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                if (packageName.contains("gm")) {
//
//                    String task = txtgmail + "\n" + contentText;
//                    dbHelpgmail.insertNewTask(task);
//                    //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(DetailsActivity.this, "Add To :" + tvName.getText() + " Section", Toast.LENGTH_SHORT).show();
//
//                } else if (packageName.equals("com.whatsapp")) {
//                    //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
//
//                    Toast.makeText(DetailsActivity.this, "Add To :" + tvName.getText() + " Section", Toast.LENGTH_SHORT).show();
//
//
//                } else {
//
//                    btnselevct.setText("Click To Add This To Favorite Section");
//                    Toast.makeText(DetailsActivity.this, "We Dont Have Section For This App, You Can Add This To Your Favorite Section", Toast.LENGTH_SHORT).show();
//                    btnselevct.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            String task = String.valueOf(txtinv.getText());
//                            addToFavorites();
////                            dbHelper.insertNewTask(task);
////
////                            Toast.makeText(DetailsActivity.this, "Added To Favorite ", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//        });


        shake = AnimationUtils.loadAnimation(this, R.anim.shake);

//        openplay = findViewById(R.id.buttonopen);
//        openplay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (packageName.contains("systemui")) {
//                    //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
//                    textOpenApp.setText("System App Cannot Be Opened");
////                    openplay.setVisibility(View.GONE);
//
//                } else if (packageName.equals("android")) {
//                    //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
//                    textOpenApp.setText("System App Cannot Be Opened");
////                    openplay.setVisibility(View.GONE);
//                } else {
//                    Toast.makeText(DetailsActivity.this, "Opening PlayStore..", Toast.LENGTH_SHORT).show();
//                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
//                    try {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
//                    } catch (android.content.ActivityNotFoundException anfe) {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
//                    }
//                }
//            }
//        });

        firebaseRemoteConfigprice = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
        firebaseRemoteConfigprice.setConfigSettingsAsync(configSettings);

        Map<String, Object> pricedata = new HashMap<>();
        pricedata.put("showads", "yn");
        pricedata.put("showfacebookads", "yn");


        firebaseRemoteConfigprice.setDefaultsAsync(pricedata);

//        checkadsstatus();

        //Facebook ADS
        //ADS+Firebase
        mAdView = new com.facebook.ads.AdView(this, getString(R.string.fb_banner_ad), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        showads();
        mAdView.loadAd(mAdView.buildLoadAdConfig().withAdListener(adListener).build());
        ((LinearLayout) findViewById(R.id.fb_container)).addView(mAdView);

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra(EXTRA_ID);
            if (id != null) {
                loadDetails(id);
            } else {
                finishWithToast();
            }
        } else {
            finishWithToast();
        }


        view1 = findViewById(R.id.icon);
//        view3 = findViewById(R.id.buttonjson);
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    //    private void checkadsstatus() {
//
//
//        firebaseRemoteConfigprice.fetch(0)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//
//                        Log.e("TaskError", "info" + firebaseRemoteConfigprice.getInfo().getLastFetchStatus());
//
//
//                        Log.e("TaskError", "firebaseremote" + firebaseRemoteConfigprice.getString("btn_text"));
//
//                        if (task.isSuccessful()) {
//
//
//                            firebaseRemoteConfigprice.fetchAndActivate();
//                            /*txt600.setText(firebaseRemoteConfigprice.getString("txt600"));
//                            txt1500.setText(firebaseRemoteConfigprice.getString("txt1500"));
//                            txt3200.setText(firebaseRemoteConfigprice.getString("txt3200"));
//                            txt5000.setText(firebaseRemoteConfigprice.getString("txt5000"));
//
// pricedata.put("showfavtab", "yn");
//        pricedata.put("showtutorial", "yn");
//        pricedata.put("showbrowseallnotification", "yn");
//        pricedata.put("showbrowsenotification", "yn");
//
//*/
//                            whatnew = (firebaseRemoteConfigprice.getString("showads"));
//                            showallnotf = (firebaseRemoteConfigprice.getString("showfacebookads"));
//
//
//                            if (showallnotf.equals("yes")) {
////                                showfacebookads();
//                                //Toast.makeText(NewMainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
//                            } else if (showallnotf.equals("no")) {
//                                mAdView.setVisibility(View.GONE);
//
//                                com.facebook.ads.AdView adView2 = new com.facebook.ads.AdView(DetailsActivity.this, "913703148991807_913704952324960", com.facebook.ads.AdSize.BANNER_HEIGHT_50);
//
//                                adView2.setVisibility(View.GONE);
//                                // Toast.makeText(NewMainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
//
//                                Log.e("AdsStatus", "Not Showing");
//
//                            }
//
//                            Log.e("TaskError", "firebaseremote" + firebaseRemoteConfigprice.getString("btn_text"));
//
//
//                                   /* Picasso.get().load(firebaseRemoteConfigprice.getString("image_link"))
//                                            .into(img);*/
//                        } else {
//
//
//                            String exp = ("" + task.getException().getMessage());
//                            if (exp.equals("null")) {
//
//                                whatnew = ("Server Not Responding ");
//
//
//                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DetailsActivity.this);
//
//
//                            } else {
//                                Log.e("TaskError", "taskexcep :" + task.getException().getMessage() + task.getException() + task);
//                                Toast.makeText(DetailsActivity.this, "Internet Connection Error", Toast.LENGTH_SHORT).show();
//                                View parentLayout = findViewById(android.R.id.content);
//
//                                Snackbar snackbar = Snackbar
//                                        .make(parentLayout, "Internet Connection Error", Snackbar.LENGTH_INDEFINITE);
//                                View snackbarView = snackbar.getView();
//                                snackbarView.setBackgroundColor(Color.parseColor("#FF104162"));
//                                snackbar.setAction("Retry", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        //  checkConnection();
//                                        snackbar.dismiss();
//                                    }
//                                });
//
//                                snackbar.show();
//                            }
//                        }
//                    }
//                });
//    }

//    private void showfacebookads() {
//        AudienceNetworkAds.initialize(DetailsActivity.this);
//
//        String adid = "913703148991807_913704952324960";
//        // fbadview = new AdView(DetailsActivity.this, "913703148991807_913704952324960", AdSize.BANNER_HEIGHT_50);
//        com.facebook.ads.AdView adView2 = new com.facebook.ads.AdView(this, "913703148991807_913704952324960", com.facebook.ads.AdSize.BANNER_HEIGHT_50);
//        // Find the Ad Container
//        LinearLayout adContainer = findViewById(R.id.banner_container);
//
//        // Add the ad view to your activity layout
//        adContainer.addView(adView2);
//        adView2.loadAd();
//    }

    private void firstrun() {

        SharedPreferences settings = getSharedPreferences("FIRSTRUNINFOdet11", MODE_PRIVATE);
        if (settings.getBoolean("isFirstRunDialogBoxtexdet1", true)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunDialogBoxtexdet1", false);
            editor.commit();
            // sendnoifications();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DetailsActivity.this);

            builder.setIcon(R.drawable.ic_splash_logo);

            builder.setTitle("How To Use!");

            builder.setMessage("Want To Know How To Use This Feature ?");
            builder.setCancelable(false);

            builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {


                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showcasetimebaby();
                }
            });


            android.app.AlertDialog dialog = builder.create();
            dialog.show();
            Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positive.setTextColor(getResources().getColor(R.color.colorText));
            Button neutral = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            neutral.setTextColor(getResources().getColor(R.color.colorText));
        }
    }

//    private void showsecondshowcase() {
//        new GuideView.Builder(DetailsActivity.this)
//                .setTitle("Open App")
//                .setContentText("From Here You Can Open Apps Directly In Your Phone")
//                .setGravity(smartdevelop.ir.eram.showcaseviewlib.config.Gravity.auto)
//                .setTargetView(view2)
//                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
//                .setGuideListener(new GuideListener() {
//                    @Override
//                    public void onDismiss(View view) {
//                        showsthirdshowcase();
//                    }
//                })
//                .build()
//                .show();
//    }

//    private void showsthirdshowcase() {
//
//
//        new GuideView.Builder(DetailsActivity.this)
//                .setTitle("More Info")
//                .setContentText("From Here You Can View Details About Notifications ")
//                .setGravity(smartdevelop.ir.eram.showcaseviewlib.config.Gravity.auto)
//                .setTargetView(view3)
//                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
//                .setGuideListener(new GuideListener() {
//                    @Override
//                    public void onDismiss(View view) {
//                        showforthshowcase();
//                    }
//                })
//                .build()
//                .show();
//
//    }

//    private void showforthshowcase() {
//
//        new GuideView.Builder(this)
//                .setTitle("More")
//                .setContentText("From Above Menu You Can Delete,Favorite,Send any Notification")
//                .setGravity(Gravity.auto) //optional
//                .setDismissType(DismissType.outside) //optional - default DismissType.targetView
//                .setTargetView(view3)
//                .setContentTextSize(14)//optional
//                .setTitleTextSize(16)//optional
//                .build()
//                .show();
//        //  shake();
//    }

    private void showcasetimebaby() {
        new GuideView.Builder(DetailsActivity.this)
                .setTitle("Play Store")
                .setContentText("From Here You Will Be Able to Open Apps In Play Store")
                .setGravity(smartdevelop.ir.eram.showcaseviewlib.config.Gravity.auto)
                .setTargetView(view1)
                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
//                        showsecondshowcase();
                    }
                })
                .build()
                .show();


    }

    @Override
    protected void onPause() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        try {
//            ivNotification = menu.findItem(R.id.fav).getActionView().findViewById(R.id.ivNotification);
//            txtinv.setText("App :" + tvName.getText() + "\nNotification : " + tvText.getText() + "\nDate & Time :" + tvDate.getText());
//            txtgmail = ("\nNotification : " + tvText.getText() + "\nDate & Time :" + tvDate.getText());
//            String tt = "Hello";
//            String tet = ("App :" + tvName.getText() + "\nNotification : " + tvText.getText() + "\nDate & Time :" + tvDate.getText());
//            ivNotification.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            shake();
//                            addToFavorites();
////                            String task = String.valueOf(txtinv.getText());
////                            dbHelper.insertNewTask(task);
////                            Toast.makeText(DetailsActivity.this, "Added To Favorite ", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                }
//            });
//
//            ivNotification.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            final Vibrator vibe1 = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
////                            Intent startIntent = new Intent(DetailsActivity.this, FavActivity.class);
////                            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                            startActivity(startIntent);
//                            addToFavorites();
//                        }
//                    });
//
//
//                    return false;
//                }
//            });
//        } catch (Exception e) {
//            Log.e(TAG, "onPrepareOptionsMenu :  Exception: " + e.toString());
//            e.printStackTrace();
//        }
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.menu_delete) {
//            confirmDelete();
//        }
//        if (item.getItemId() == R.id.menu_share) {
//            shareapplink();
//        }
//        if (item.getItemId() == R.id.add) {
//            Toast.makeText(DetailsActivity.this, "Sharing Notification Details..", Toast.LENGTH_SHORT).show();
//
//
//            ShareCompat.IntentBuilder.from(DetailsActivity.this)
//                    .setType("text/plain")
//                    .setChooserTitle("")
//                    .setText("App :" + tvName.getText() + "\nNotification : " + tvText.getText() + "\nDate & Time :" + tvDate.getText())
//                    .startChooser();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void shake() {
//        ivNotification.startAnimation(shake);
//    }

    private void addToFavorites() {
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.PostedEntry.COLUMN_NAME_FAVORITE, 1);
        db.update(DatabaseHelper.PostedEntry.TABLE_NAME, contentValues, "_ID=" + id, null);
        db.close();
        helper.close();
        Toast.makeText(this, getString(R.string.message_add_favorite), Toast.LENGTH_SHORT).show();
    }

    private void shareapplink() {
        Toast.makeText(DetailsActivity.this, "Sharing App..", Toast.LENGTH_SHORT).show();
        ShareCompat.IntentBuilder.from(DetailsActivity.this)
                .setType("text/plain")
                .setChooserTitle("Share URL")
                .setText("Hey, Download This App - " + "https://play.google.com/store/apps/details?id=" + packageName)
                .startChooser();
    }

    private void loadDetails(String id) {


        JSONObject json = null;
        appJson = "error";
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            SQLiteDatabase db = databaseHelper.getReadableDatabase();

            Cursor cursor = db.query(DatabaseHelper.PostedEntry.TABLE_NAME,
                    new String[]{
                            DatabaseHelper.PostedEntry.COLUMN_NAME_CONTENT,
                    },
                    DatabaseHelper.PostedEntry._ID + " = ?",
                    new String[]{
                            id
                    },
                    null,
                    null,
                    null,
                    "1");

            if (cursor != null && cursor.getCount() == 1 && cursor.moveToFirst()) {
                try {
                    json = new JSONObject(cursor.getString(0));
                    appJson = json.toString(2);
                } catch (JSONException e) {
                    if (Const.DEBUG) e.printStackTrace();
                }
                cursor.close();
            }

            db.close();
            databaseHelper.close();
        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
        }


//        buttonjson = findViewById(R.id.buttonjson);
//        buttonjson.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
//
//                tvJSON.setVisibility(View.VISIBLE);
//                buttonjson.setVisibility(View.GONE
//                );
//
//
//            }
//        });

        ImageView icon = findViewById(R.id.icon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (packageName.contains("systemui")) {
                    //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
                    textOpenApp.setText("System App Cannot Be Opened");
//                    openplay.setVisibility(View.GONE);

                } else if (packageName.equals("android")) {
                    //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
                    textOpenApp.setText("System App Cannot Be Opened");
//                    openplay.setVisibility(View.GONE);
                } else {
                    Toast.makeText(DetailsActivity.this, "Opening PlayStore..", Toast.LENGTH_SHORT).show();
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                    }
                }
            }
        });
        LinearLayout card = findViewById(R.id.card);
//        CardView buttons = findViewById(R.id.buttons);
        if (json != null) {
            packageName = json.optString("packageName", "???");
            String titleText = json.optString("title");
            contentText = json.optString("text");

            texttext = contentText;
            String text = (titleText + "\n" + contentText).trim();
            if (!"".equals(text)) {
                card.setVisibility(View.VISIBLE);
                //ImageView icon = findViewById(R.id.icon);
                icon.setImageDrawable(Util.getAppIconFromPackage(this, packageName));

                //ImageView image = (ImageView) findViewById(R.id.icon);
                // ColorFilter test = image.getColorFilter();
                geticoncolor();
                //Color tagColor = Color.rgb(Color.red(color),Color.green(color), Color.blue(color));
                // imageview.setTag(tagColor);
                //tagColor = (Color) imageview.getTag();
                //Toast.makeText(this, ""+test, Toast.LENGTH_SHORT).show();

                tvName = findViewById(R.id.name);
                tvName.setText(Util.getAppNameFromPackage(this, packageName, false));
                tvText = findViewById(R.id.text);

                tvText.setText(text);
                tvDate = findViewById(R.id.date);
                if (SHOW_RELATIVE_DATE_TIME) {
                    tvDate.setText(DateUtils.getRelativeDateTimeString(
                            this,
                            json.optLong("systemTime"),
                            DateUtils.MINUTE_IN_MILLIS,
                            DateUtils.WEEK_IN_MILLIS,
                            0));
                } else {
                    DateFormat format = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, Locale.getDefault());
                    tvDate.setText(format.format(json.optLong("systemTime")));
                }

                try {
                    ApplicationInfo app = this.getPackageManager().getApplicationInfo(packageName, 0);
                    buttonNotifySettings.setVisibility(View.VISIBLE);
                    appUid = app.uid;
                } catch (PackageManager.NameNotFoundException e) {
                    if (Const.DEBUG) e.printStackTrace();
                    buttonNotifySettings.setVisibility(View.GONE);
                }
            } else {
                card.setVisibility(View.GONE);

            }
        } else {
            card.setVisibility(View.GONE);
            buttonNotifySettings.setVisibility(View.GONE);
        }


    }

    private void geticoncolor() {
        try {
            if (packageName.contains("systemui")) {
                //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
                textOpenApp.setText("System App Cannot Be Opened");
//                openplay.setVisibility(View.GONE);

            } else if (packageName.equals("android")) {
                //Toast.makeText(DetailsActivity.this, "", Toast.LENGTH_SHORT).show();
                textOpenApp.setText("System App Cannot Be Opened");
//                openplay.setVisibility(View.GONE);

            }
       /* else if(packageName.equals("com.whatsapp")){
            ImageView icon = findViewById(R.id.icon);
            icon.setImageDrawable(Util.getAppIconFromPackage(this, packageName));

            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), geticoncolor());

            Bitmap bitmap3 = ((BitmapDrawable) icon.getDrawable()).getBitmap();

            BitmapDrawable drawable = (BitmapDrawable) icon.getDrawable();
            Bitmap bitmap2 = drawable.getBitmap();

            Palette.from(bitmap3).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    //work with the palette here
                    int defaultValue = 0x000000;
                    int vibrant = palette.getVibrantColor(defaultValue);
                    int vibrantLight = palette.getLightVibrantColor(defaultValue);
                    int vibrantDark = palette.getDarkVibrantColor(defaultValue);
                    int muted = palette.getMutedColor(defaultValue);
                    int mutedLight = palette.getLightMutedColor(defaultValue);
                    int mutedDark = palette.getDarkMutedColor(defaultValue);
                    openapp.setTextColor(vibrant);
                    openplay.setTextColor(vibrant);

                    notificationsettingbtn.setTextColor(muted);
                    *//*ActionBar actionBar = getSupportActionBar();
                    actionBar.setBackgroundDrawable(new ColorDrawable(vibrant));
                    //actionBar.setTitle(Html.fromHtml("<vibrantLight>Hello World</font>"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(vibrantDark);
                    }*//*
                    // Toast.makeText(DetailsActivity.this, ""+vibrant+vibrantDark+mutedLight+mutedDark, Toast.LENGTH_SHORT).show();
                *//*vibrantView.setBackgroundColor(vibrant);
                vibrantLightView.setBackgroundColor(vibrantLight);
                vibrantDarkView.setBackgroundColor(vibrantDark);
                mutedView.setBackgroundColor(muted);
                mutedLightView.setBackgroundColor(mutedLight);
                mutedDarkView.setBackgroundColor(mutedDark);*//*


                }
            });
        }*/
            else {
                ImageView icon = findViewById(R.id.icon);
                icon.setImageDrawable(Util.getAppIconFromPackage(this, packageName));

                //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), geticoncolor());

                Bitmap bitmap3 = ((BitmapDrawable) icon.getDrawable()).getBitmap();

                //BitmapDrawable drawable = (BitmapDrawable) icon.getDrawable();
                //Bitmap bitmap2 = drawable.getBitmap();

                Palette.from(bitmap3).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
//                        //work with the palette here
//                        int defaultValue = 0x000000;
//                        int vibrant = palette.getVibrantColor(defaultValue);
//                        int vibrantLight = palette.getLightVibrantColor(defaultValue);
//                        int vibrantDark = palette.getDarkVibrantColor(defaultValue);
//                        int muted = palette.getMutedColor(defaultValue);
//                        int mutedLight = palette.getLightMutedColor(defaultValue);
//                        int mutedDark = palette.getDarkMutedColor(defaultValue);
//
//                        Log.d("Color Codes", "vibrant" + vibrant + "vibrantDark" + vibrantDark);
//                        if (vibrantDark == 0) {
//
//                            textOpenApp.setTextColor(vibrant);
//                            //openplay.setTextColor(vibrant);
//                        } else {
//                            openapp.setTextColor(vibrantDark);
//                        }
//                        // Toast.makeText(DetailsActivity.this, ""+vibrantDark+vibrant, Toast.LENGTH_LONG).show();
//                        if (vibrant == 0 && vibrantDark == 0) {
//
//                            openapp.setTextColor(mutedDark);
//
////                            ActionBar actionBar = getSupportActionBar();
////                            actionBar.setBackgroundDrawable(new ColorDrawable(mutedDark));
//
//                            //actionBar.setTitle(Html.fromHtml("<vibrantLight>Hello World</font>"));
////                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                                Window window = getWindow();
////                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
////                                window.setStatusBarColor(mutedDark);
////                            }
//                            //openplay.setTextColor(vibrant);
//                        } else {
////                            ActionBar actionBar = getSupportActionBar();
////                            actionBar.setBackgroundDrawable(new ColorDrawable(vibrant));
////
////                            //actionBar.setTitle(Html.fromHtml("<vibrantLight>Hello World</font>"));
////                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                                Window window = getWindow();
////                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
////                                window.setStatusBarColor(vibrant);
////                            }
//                        }
//                        // Toast.makeText(DetailsActivity.this, ""+vibrant+vibrantDark+mutedLight+mutedDark, Toast.LENGTH_SHORT).show();
//                /*vibrantView.setBackgroundColor(vibrant);
//                vibrantLightView.setBackgroundColor(vibrantLight);
//                vibrantDarkView.setBackgroundColor(vibrantDark);
//                mutedView.setBackgroundColor(muted);
//                mutedLightView.setBackgroundColor(mutedLight);
//                mutedDarkView.setBackgroundColor(mutedDark);*/
                    }
                });
            }
        } catch (Exception e) {
            // This will catch any exception, because they are all descended from Exception
            //Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            mAuth = FirebaseAuth.getInstance();

            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser == null) {

                Log.e("user", "null");


            } else {


                mUserRef = FirebaseDatabase.getInstance().getReference().child("Detail-Activty").child(mAuth.getCurrentUser().getUid());

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault());

                String datte = format.format(new Date());
                String idtimedate = datte.substring(5, 10);
                String idtime = datte.substring(11, 19);

                mUserRef.child("packageName").setValue(packageName);
                mUserRef.child("Date").setValue(datte);
                mUserRef.child("Exception").setValue(e);

                //username = currentUser.getUid();
                //SAVEDATAREPORT
                //savereport(currentUser);
            }

        }

    }

    private void finishWithToast() {
        Toast.makeText(this, R.string.details_error, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void confirmDelete() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.delete_dialog_title)
                .setMessage(R.string.delete_dialog_text)
                .setPositiveButton(R.string.delete_dialog_yes, doDelete)
                .setNegativeButton(R.string.delete_dialog_no, null)
                .show();
        Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positive.setTextColor(getResources().getColor(R.color.colorText));
        Button neutral = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        neutral.setTextColor(getResources().getColor(R.color.colorText));
    }

    public void openNotificationSettings(View v) {
        try {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT > 25) {
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("android.provider.extra.APP_PACKAGE", packageName);
            } else if (Build.VERSION.SDK_INT >= 21) {
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", packageName);
                intent.putExtra("app_uid", appUid);
            } else {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + packageName));
            }
            startActivity(intent);
        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
        }
    }

}