package com.appnotification.notificationhistorylog.ui;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.appnotification.notificationhistorylog.BuildConfig;
import com.appnotification.notificationhistorylog.CommonCl.SharedCommon;
import com.appnotification.notificationhistorylog.FAQActivity;
import com.appnotification.notificationhistorylog.FavActivity;
import com.appnotification.notificationhistorylog.R;
import com.appnotification.notificationhistorylog.SettingsActivity;
import com.appnotification.notificationhistorylog.misc.Const;
import com.appnotification.notificationhistorylog.misc.DatabaseHelper;
import com.appnotification.notificationhistorylog.misc.ExportTask;
import com.appnotification.notificationhistorylog.service.NotificationHandler;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1;
import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyfaqs;
import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keylog;
import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyopenrate;
import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyopenrateall;
import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyopenratefav;
import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyopenratetutorial;
import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keyopensetting;

public class MainActivity extends AppCompatActivity {

    private final boolean mlogtext = false;
    private final boolean mlogongoing = false;
    public String whatnew;
    public String showallnotf;
    public String shownotif;
    public String showtuto;
    public String showfav;
    public String livenotice;
    public String versionfirebase;
    public String logno;
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;
    String appid = BuildConfig.APPLICATION_ID;
    FirebaseRemoteConfig firebaseRemoteConfigprice;
    TextView txtcount;
    String PrivacyUrl = "https://xenonstudio.in/helpnotifcation";
    ImageView ivNotification;
    Animation shake;
    View view1, view2, view3, view4, view5;
    RelativeLayout rltip, rlfav, sharerl, tpfav, ncrl, seletcapps;
    TextView txt, txtversion, logtextno;
    String apppackagename = "com.appnotification.notificationhistorylog";
    String onesignalid;
    String adnotshowing;
    Thread splashTread;
    private FirebaseAuth mAuth;
    private InterstitialAd mInterstitialAd;
    private com.facebook.ads.AdView mAdView;
    private DatabaseReference mUserRef, museref, mdatareport, mcredtref, mlinkupdate;
    private GuideView mGuideView;
    private GuideView.Builder buildergf, builder2;

    private AdListener adListener;

    private void dofirstinforun() {

        SharedPreferences settings = getSharedPreferences("FIRSTRUNINFO111", MODE_PRIVATE);
        if (settings.getBoolean("isFirstRunDialogBoxtex111", true)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunDialogBoxtex111", false);
            editor.commit();
            // sendnoifications();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);

            builder.setIcon(R.drawable.notificationlogo);

            builder.setTitle("New Features Available!");
            builder.setMessage("-Multi-Language Support \n -View Notification App Wise \n -Notice For App In Settings \n -Help Section Improved \n -UI Changes \n -Bug Fixed \n");

            builder.setCancelable(false);

            builder.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {


                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Change Language", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent startIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(startIntent);
                }
            });


            android.app.AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void checkadsstatus() {


        firebaseRemoteConfigprice.fetch(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Log.e("TaskError", "info" + firebaseRemoteConfigprice.getInfo().getLastFetchStatus());


                        Log.e("TaskError", "firebaseremote" + firebaseRemoteConfigprice.getString("btn_text"));

                        if (task.isSuccessful()) {


                            firebaseRemoteConfigprice.fetchAndActivate();
                            /*txt600.setText(firebaseRemoteConfigprice.getString("txt600"));
                            txt1500.setText(firebaseRemoteConfigprice.getString("txt1500"));
                            txt3200.setText(firebaseRemoteConfigprice.getString("txt3200"));
                            txt5000.setText(firebaseRemoteConfigprice.getString("txt5000"));

 pricedata.put("showfavtab", "yn");
        pricedata.put("showtutorial", "yn");
        pricedata.put("showbrowseallnotification", "yn");
        pricedata.put("showbrowsenotification", "yn");

*/
                            whatnew = (firebaseRemoteConfigprice.getString("showads"));
                            showallnotf = (firebaseRemoteConfigprice.getString("showbrowseallnotification"));
                            showfav = (firebaseRemoteConfigprice.getString("showfavtab"));
                            showtuto = (firebaseRemoteConfigprice.getString("showtutorial"));
                            livenotice = (firebaseRemoteConfigprice.getString("livenotice"));
                            shownotif = (firebaseRemoteConfigprice.getString("showbrowsenotification"));


                            if (showtuto.equals("yes")) {
                                rltip.setVisibility(View.VISIBLE);
                            } else {

                                rltip.setVisibility(View.GONE);
                            }

                            if (shownotif.equals("yes")) {
                                seletcapps.setVisibility(View.VISIBLE);
                            } else if (shownotif.equals("shownoif")) {


                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);

                                builder.setMessage(livenotice);

                                builder.setCancelable(false);
                                builder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        dialogInterface.dismiss();
                                    }
                                }).setNegativeButton("Developer Contact", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        Intent send = new Intent(Intent.ACTION_SENDTO);
                                        String uriText = "mailto:" + Uri.encode("thexenonstudio@gmail.com") +
                                                "?subject=" + Uri.encode("Notification Log - Developer Contact") +
                                                "&body=" + Uri.encode("Hello, Type Your Query/Problem/Bug/Suggestions Here" + " \n\n\n ------------ \n\n Version Code : " + versionCode + "\n Version Name : " + versionName + "\n Application ID : " + appid);
                                        Uri uri = Uri.parse(uriText);

                                        send.setData(uri);
                                        startActivity(Intent.createChooser(send, "Send Mail Via : "));
                                    }
                                });

                                android.app.AlertDialog dialog = builder.create();
                                dialog.show();
                            } else if (shownotif.equals("no")) {
                                seletcapps.setVisibility(View.GONE);
                            }


                            if (whatnew.equals("yes")) {
                                showads();
                                showintads();
                                //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
                            } else if (whatnew.equals("no")) {
                                mAdView.setVisibility(View.GONE);

                                // Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();

                                Log.e("AdsStatus", "Not Showing");

                            }

                            Log.e("TaskError", "firebaseremote" + firebaseRemoteConfigprice.getString("btn_text"));


                                   /* Picasso.get().load(firebaseRemoteConfigprice.getString("image_link"))
                                            .into(img);*/
                        } else {


                            String exp = ("" + task.getException().getMessage());
                            if (exp.equals("null")) {

                                whatnew = ("Server Not Responding ");


                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);


                            } else {
                                Log.e("TaskError", "taskexcep :" + task.getException().getMessage() + task.getException() + task);
                                Toast.makeText(MainActivity.this, "Internet Connection Error", Toast.LENGTH_SHORT).show();
                                View parentLayout = findViewById(android.R.id.content);

                                Snackbar snackbar = Snackbar
                                        .make(parentLayout, "Internet Connection Error", Snackbar.LENGTH_INDEFINITE);
                                View snackbarView = snackbar.getView();
                                snackbarView.setBackgroundColor(Color.parseColor("#FF104162"));
                                snackbar.setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        checkConnection();
                                        snackbar.dismiss();
                                    }
                                });

                                snackbar.show();
                            }
                        }
                    }
                });
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void checkConnection() {
        if (isOnline()) {

            Log.d("Hello", "Net");


        } else {

            //cddata.setVisibility(View.GONE);
            View parentLayout = findViewById(android.R.id.content);

            Snackbar snackbar = Snackbar
                    .make(parentLayout, "Internet Connection Not Found! ", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.parseColor("#FF104162"));

            snackbar.show();
        }
    }

    private void showintads() {
        if (mInterstitialAd.isAdLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtversion = findViewById(R.id.versioncode);
        logtextno = findViewById(R.id.logtextno);
        mAuth = FirebaseAuth.getInstance();

        AudienceNetworkAds.initialize(this);
        mInterstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.fb_interstitial_ad));
        mInterstitialAd.loadAd();
        mAdView = new com.facebook.ads.AdView(this, getString(R.string.fb_banner_ad), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        mAdView.loadAd(mAdView.buildLoadAdConfig().withAdListener(adListener).build());
        ((LinearLayout) findViewById(R.id.fb_container)).addView(mAdView);
        //testid-ca-app-pub-3940256099942544/1033173712
        //upid-ca-app-pub-6778147776084460/3563503620
        //my-INT-ID--ca-app-pub-8081344892743036/1424914117
        mAuth = FirebaseAuth.getInstance();


        if (mAuth.getCurrentUser() != null) {


            mdatareport = FirebaseDatabase.getInstance().getReference().child("UsageReports").child(mAuth.getCurrentUser().getUid());
            museref = FirebaseDatabase.getInstance().getReference().child("mainacreport").child(mAuth.getCurrentUser().getUid());

            mUserRef = FirebaseDatabase.getInstance().getReference().child("openreport").child(mAuth.getCurrentUser().getUid());

        }
        //Onesignal
        OneSignal.initWithContext(this);
        OSDeviceState device = OneSignal.getDeviceState();

        String userId = device.getUserId();
        // String userId = OneSignal.getDeviceState().getUserId();

        OneSignal.setAppId(userId);

//        OneSignal.StartInit(this)
//                .InFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                .unsubscribeWhenNotificationsAreDisabled(true)
//                .init();
//        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
//            @Override
//            public void idsAvailable(String userId, String registrationId) {
//                Log.d("debug", "User:" + userId);
//                if (registrationId != null)
//                    onesignalid = userId;
//                Log.d("debug", "registrationId:" + registrationId);
//
//            }
//        });
        int i = SharedCommon.getPreferencesInt(getApplicationContext(), key1, 0);


        final SharedCommon sc = new SharedCommon();

        if (i <= 0) {
            Log.d("", "");
        }

        int or = SharedCommon.getPreferencesInt(getApplicationContext(), keyopenrate, 0);
        int orset = SharedCommon.getPreferencesInt(getApplicationContext(), keyopensetting, 0);
        int ortip = SharedCommon.getPreferencesInt(getApplicationContext(), keyopenratetutorial, 0);
        int orfav = SharedCommon.getPreferencesInt(getApplicationContext(), keyopenratefav, 0);


        final SharedCommon scor = new SharedCommon();

        if (or <= 0) {
            Log.d("", "");
        }
        rlfav = findViewById(R.id.favrl);
        rlfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                export();
            }
        });
        sharerl = findViewById(R.id.sharerl);
        sharerl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareapplink();
            }
        });
        rltip = findViewById(R.id.tiprl);
        rltip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                SharedCommon scor = new SharedCommon();

                int ortop = SharedCommon.getPreferencesInt(getApplicationContext(), keyopenratetutorial, 0);


                ortop++;
                SharedCommon.putPreferencesInt(getApplicationContext(), SharedCommon.keyopenratetutorial, ortop);

                firebaseRemoteConfigprice.fetchAndActivate();

                whatnew = (firebaseRemoteConfigprice.getString("showads"));


                if (whatnew.equals("yes")) {
                    showads();
                    showintads();
                    //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
                } else if (whatnew.equals("no")) {
                    mAdView.setVisibility(View.GONE);

                    //  Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
                }
                showcasetimebaby();
            }
        });

        rltip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, "" + adnotshowing, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        seletcapps = findViewById(R.id.seletcapps);
        seletcapps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedCommon scor = new SharedCommon();

                int or = SharedCommon.getPreferencesInt(getApplicationContext(), keyopenrate, 0);


                or++;
                SharedCommon.putPreferencesInt(getApplicationContext(), SharedCommon.keyopenrate, or);
                showappdailog();
                // dofirstinforun();
                /*Intent startIntent = new Intent(MainActivity.this, SelectedNotifActivity.class);
                startActivity(startIntent);*/
            }
        });
        tpfav = findViewById(R.id.tpfav);
        tpfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedCommon scor = new SharedCommon();

                int orfavc = SharedCommon.getPreferencesInt(getApplicationContext(), keyopenratefav, 0);


                orfavc++;
                SharedCommon.putPreferencesInt(getApplicationContext(), SharedCommon.keyopenratefav, orfavc);
                firebaseRemoteConfigprice.fetchAndActivate();

                whatnew = (firebaseRemoteConfigprice.getString("showads"));


                if (whatnew.equals("yes")) {
                    showads();
                    showintads();
                    //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
                } else if (whatnew.equals("no")) {
                    mAdView.setVisibility(View.GONE);

                    //  Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
                }
                Intent startIntent = new Intent(MainActivity.this, FavActivity.class);
                startActivity(startIntent);
            }
        });


        view1 = findViewById(R.id.favrl);
        view2 = findViewById(R.id.fra);
        view3 = findViewById(R.id.sharerl);
        view4 = findViewById(R.id.tiprl);
        view5 = findViewById(R.id.fra);

        doFirstRun();


        shake = AnimationUtils.loadAnimation(this, R.anim.shake);


        txtversion.setText("v" + versionName);


        firebaseRemoteConfigprice = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
        firebaseRemoteConfigprice.setConfigSettingsAsync(configSettings);

        Map<String, Object> pricedata = new HashMap<>();
        pricedata.put("showads", "yn");
        pricedata.put("showfavtab", "yn");
        pricedata.put("showtutorial", "yn");
        pricedata.put("showbrowseallnotification", "yn");
        pricedata.put("showbrowsenotification", "yn");
        pricedata.put("livenotice", "yn");


        firebaseRemoteConfigprice.setDefaultsAsync(pricedata);

        checkadsstatus();
    }

    private void showads() {
        //testid---ca-app-pub-3940256099942544/6300978111
        //upid--ca-app-pub-6778147776084460/7588656349
        //myid-ca-app-pub-8081344892743036/1017320664
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

    private void showappdailog() {

        final String[] listitems = {"WhatsApp", "Gmail", "Facebook", "Instagram", "Calender", "Calls"};
        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(MainActivity.this, R.style.AlertDialogedit);
        mBuilder.setTitle("Browse Notifications Of  ");
        mBuilder.setSingleChoiceItems(listitems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


               /* String name = "show";
                SharedCommon.putSharedPreferencesString(MainActivity.this,SharedCommon.keysettingdailog,name);*/


                if (i == 0) {

                    //  setlocale("en");
                    Intent startIntent = new Intent(MainActivity.this, WhatsappActivity.class);
                    startActivity(startIntent);
                } else if (i == 1) {

                    // setlocale("hi");
                    Toast.makeText(MainActivity.this, "Gmail Notifications", Toast.LENGTH_SHORT).show();
                    /*recreate();*/
                    Intent startIntent = new Intent(MainActivity.this, GmailActivity.class);
                    startActivity(startIntent);
                } else if (i == 2) {

                    // setlocale("tr");
                    // Toast.makeText(MainActivity.this, "tr", Toast.LENGTH_SHORT).show();

                    Intent startIntent = new Intent(MainActivity.this, FacebookActivity.class);
                    startActivity(startIntent);
                } else if (i == 3) {

                    // setlocale("de");
                    // Toast.makeText(MainActivity.this, "de", Toast.LENGTH_SHORT).show();

                    Intent startIntent = new Intent(MainActivity.this, InstaActivity.class);
                    startActivity(startIntent);
                } else if (i == 4) {

                    //  Toast.makeText(MainActivity.this, "it", Toast.LENGTH_SHORT).show();

                    Intent startIntent = new Intent(MainActivity.this, CalenderActivity.class);
                    startActivity(startIntent);
                } else if (i == 5) {


                    //Toast.makeText(MainActivity.this, "gu", Toast.LENGTH_SHORT).show();

                    Intent startIntent = new Intent(MainActivity.this, CallsActivity.class);
                    startActivity(startIntent);
                }

                dialogInterface.dismiss();
            }
        });

        android.app.AlertDialog mDialog = mBuilder.create();

        mDialog.show();

    }


    private void showratingbar() {


        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.ratinglayout, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                MainActivity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);


        final RatingBar rate;
        rate = promptsView.findViewById(R.id.ratingBardai);

        rate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {


                switch ((int) ratingBar.getRating()) {
                    case 1:

                        Toast.makeText(MainActivity.this, "We Will Improve Our App", Toast.LENGTH_SHORT).show();
                        Intent startIntent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(startIntent);
                        break;
                    case 2:
                        Toast.makeText(MainActivity.this, "We Will Improve Our App", Toast.LENGTH_SHORT).show();
                        Intent startIntent1 = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(startIntent1);
                        break;
                    case 3:
                        Toast.makeText(MainActivity.this, "Thanks For Your Feedback", Toast.LENGTH_SHORT).show();
                        Intent startIntent2 = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(startIntent2);
                        break;
                    case 4:
                        Toast.makeText(MainActivity.this, "Please Rate Us On Play Store", Toast.LENGTH_SHORT).show();
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + apppackagename)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + apppackagename)));
                        }


                        break;
                    case 5:
                        Toast.makeText(MainActivity.this, "Please Rate Us On Play Store", Toast.LENGTH_SHORT).show();
                        final String appPackageName1 = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + apppackagename)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + apppackagename)));
                        }

                        break;
                    default:

                }
            }
        });


        // set dialog message
        alertDialogBuilder

                .setCancelable(true);

        // create alert dialog
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }

    private void showcasetimebaby() {
        new GuideView.Builder(MainActivity.this)
                .setTitle("Export Logs")
                .setContentText("Share/Export Your Logs From Here")
                .setGravity(Gravity.auto)
                .setTargetView(view1)
                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        showsecondshowcase();
                    }
                })
                .build()
                .show();


    }

    private void showsecondshowcase() {


        new GuideView.Builder(MainActivity.this)
                .setTitle("Notifications")
                .setContentText("From Here You Browse Notifications, See No. Of Notification Logged, Change Permission Status")
                .setGravity(Gravity.auto)
                .setTargetView(view2)
                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        showsthirdshowcase();
                    }
                })
                .build()
                .show();
    }

    private void showsthirdshowcase() {


        new GuideView.Builder(MainActivity.this)
                .setTitle("Share App")
                .setContentText("From Here Directly Share Apps With Friends And Family Members")
                .setGravity(Gravity.auto)
                .setTargetView(view3)
                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        showforthshowcase();
                    }
                })
                .build()
                .show();

    }

    private void showforthshowcase() {

        new GuideView.Builder(this)
                .setTitle("More")
                .setContentText("You Can Delete Logs, Change Log Settings From Above Menu \n\n Also While Browsing Notification You Can Open App In PLay Store, Share Particular App, Add To Favorite And Lot More..\n\nLog Text Means - Log the actual content of the notifications. Increases the size of the log file but allows you to read past messages. ")
                .setGravity(Gravity.auto) //optional
                .setDismissType(DismissType.outside) //optional - default DismissType.targetView
                .setTargetView(view4)
                .setContentTextSize(14)//optional
                .setTitleTextSize(16)//optional
                .build()
                .show();

        //  shake();
    }


    private void showtipshowcase() {

        new GuideView.Builder(this)
                .setTitle("Status")
                .setContentText("Click On Status To Give Access To Notification")
                .setGravity(Gravity.auto) //optional
                .setDismissType(DismissType.outside) //optional - default DismissType.targetView
                .setTargetView(view5)
                .setContentTextSize(14)//optional
                .setTitleTextSize(16)//optional
                .build()
                .show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
       /* CheckBox checkBox = (CheckBox) menu.findItem(R.id.menuShowlogtext).getActionView();
        checkBox.setText("Log Text");

        CheckBox checkBox2 = (CheckBox) menu.findItem(R.id.menuShowlogon).getActionView();
        checkBox.setText("Log Ongoing");*/

        return true;
    }

    @Override
    public void onStart() {

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //Toast.makeText(this, ""+currentUser, Toast.LENGTH_SHORT).show();

        if (currentUser == null) {

            sendToStart();

        } else {
            int i = SharedCommon.getPreferencesInt(getApplicationContext(), key1, 0);
            int or = SharedCommon.getPreferencesInt(getApplicationContext(), keyopenrate, 0);
            int orfav = SharedCommon.getPreferencesInt(getApplicationContext(), keyopenratefav, 0);
            int ortip = SharedCommon.getPreferencesInt(getApplicationContext(), keyopenratetutorial, 0);
            int orset = SharedCommon.getPreferencesInt(getApplicationContext(), keyopensetting, 0);
            int orall = SharedCommon.getPreferencesInt(getApplicationContext(), keyopenrateall, 0);
            int orfaqs = SharedCommon.getPreferencesInt(getApplicationContext(), keyfaqs, 0);

            int logno = SharedCommon.getPreferencesInt(getApplicationContext(), keylog, 0);

            SharedPreferences pref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
            String id = pref.getString("facebook_id", "");
            //Toast.makeText(this, ""+id, Toast.LENGTH_SHORT).show();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault());

            String datte = format.format(new Date());
            String idtimedate = datte.substring(5, 10);
            String idtime = datte.substring(11, 19);
            mdatareport = FirebaseDatabase.getInstance().getReference().child("UsageReports").child(mAuth.getCurrentUser().getUid());
            museref = FirebaseDatabase.getInstance().getReference().child("mainacreport").child(mAuth.getCurrentUser().getUid());

            mUserRef = FirebaseDatabase.getInstance().getReference().child("openreport").child(mAuth.getCurrentUser().getUid());


            mUserRef.child("Onesignal-ID").setValue(onesignalid);
            mUserRef.child("Last Seen").setValue(datte);
            mUserRef.child("Notifications Logged").setValue(id);
            mUserRef.child("FAQs").setValue(orfaqs);
            museref.child("D-i").setValue(i);
            mUserRef.child("Browse-Notification-All").setValue(orall);
            mUserRef.child("Browse-Notification").setValue(or);
            mUserRef.child("Settings").setValue(orset);
            mUserRef.child("Tutorial").setValue(ortip);
            mUserRef.child("Favorite").setValue(orfav);
            //username = currentUser.getUid();
            //SAVEDATAREPORT
            //savereport(currentUser);
        }

        super.onStart();
        // Toast.makeText(this, ""+currentUser, Toast.LENGTH_SHORT).show();
        //updateUI(currentUser);
    }

    private void sendToStart() {

        //username = "Not Signed In";
        //Toast.makeText(this, "Not Signed", Toast.LENGTH_SHORT).show();;

    }

    private void signInAnonymously() {
        // showProgressDialog();
        // [START signin_anonymously]
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // [START_EXCLUDE]
                        // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END signin_anonymously]
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                confirm();

                return true;

            /*case R.id.menu_settings:
                opensetting();

                return true;
*/
            case R.id.report:
                helpdialog();
                return true;

            case R.id.menu_sug:
                openhelp();
                return true;

            case R.id.backup:
                Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.restore:
                Toast.makeText(this, "Hello Res", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.faqs:
                SharedCommon scor = new SharedCommon();

                int orfavc = SharedCommon.getPreferencesInt(getApplicationContext(), keyfaqs, 0);


                orfavc++;
                SharedCommon.putPreferencesInt(getApplicationContext(), SharedCommon.keyfaqs, orfavc);
                Intent startIntent = new Intent(MainActivity.this, FAQActivity.class);
                startActivity(startIntent);
                /*String url = "https://xenonstudio.in/notificationlog#24f3eaf5-2efe-4e30-9c58-975c032e08e0";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);*/
                return true;
           /* case R.id.menuShowlogtext:
                if(item.isChecked()){
                    // If item already checked then unchecked it
                    item.setChecked(false);
                    mlogtext = false;
                }else{
                    // If item is unchecked then checked it
                    item.setChecked(true);
                    mlogtext = true;
                }
                showlogtext();
                return true;*/

           /* case R.id.menuShowlogon:
                if(item.isChecked()){
                    // If item already checked then unchecked it
                    item.setChecked(false);
                    mlogtext = false;
                }else{
                    // If item is unchecked then checked it
                    item.setChecked(true);
                    mlogtext = true;
                }
                showlogtext();
                return true;*/

        }
        return super.onOptionsItemSelected(item);
    }

    private void helpdialog() {

        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.helplayout, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                MainActivity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);


        final EditText edtname, edtemail, edtphone, edtpincode, edtid;
        edtname = promptsView.findViewById(R.id.edtfullname);
        edtemail = promptsView.findViewById(R.id.edtemail);
        edtphone = promptsView.findViewById(R.id.edtphonenumber);
        edtpincode = promptsView.findViewById(R.id.edtpincode);
        edtid = promptsView.findViewById(R.id.edtpaypalorpaytm);


        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        final String countryCodeValue = tm.getNetworkCountryIso();

        final Spinner sp = promptsView
                .findViewById(R.id.spinnerpaypalpaytm);


        final Button userInput = promptsView
                .findViewById(R.id.btndiasub);

        userInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = edtname.getText().toString();
                String email = edtemail.getText().toString();
                String phone = edtphone.getText().toString();
                String pincode = edtpincode.getText().toString();
                String id = edtid.getText().toString();


                String Method = ("" + sp.getSelectedItem());
                if (name.equals("") || pincode.equals("") || id.equals("")) {

                    Toast.makeText(MainActivity.this, "Please Enter All The Details", Toast.LENGTH_SHORT).show();
                } else {

                    Toast toast = Toast.makeText(MainActivity.this, "SEND MAIL VIA GMAIL/YAHOO ", Toast.LENGTH_LONG);
                    View view1 = toast.getView();

                    view1.getBackground().setColorFilter((Color.parseColor("#FF104162")), PorterDuff.Mode.SRC_IN);


                    TextView text = view1.findViewById(android.R.id.message);
                    text.setTextColor(Color.WHITE);

                    toast.show();

                    Intent send = new Intent(Intent.ACTION_SENDTO);
                    String uriText = "mailto:" + Uri.encode("notificationapp.xenonstudio@gmail.com") +
                            "?subject=" + Uri.encode(Method + " - Notification Log App") +
                            "&body=" + Uri.encode("" + "Name: " + name + "\n" + "Country: " + pincode + "\n" + "Query Type: " + Method + "\n" + "Query: " + id + " \n\n\n ------------ \n\n Version Code : " + versionCode + "\n Build : " + Build.BRAND + "\n" + Build.MODEL + "\n" + Build.DEVICE);
                    Uri uri = Uri.parse(uriText);

                    send.setData(uri);
                    startActivity(Intent.createChooser(send, "Send Mail Via : "));

                    splashTread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                int waited = 0;
                                // Splash screen pause time
                                while (waited < 10600) {
                                    sleep(100);
                                    waited += 100;
                                }
                                sendFCMPush();
                                MainActivity.this.finish();
                            } catch (InterruptedException e) {
                                // do nothing
                            } finally {
                                MainActivity.this.finish();
                            }

                        }
                    };
                    splashTread.start();

                }
            }
        });
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Go Back",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                            }
                        });

        // create alert dialog
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    private void sendFCMPush() {


        int requestID = (int) System.currentTimeMillis();

        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),
                0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());

        Intent pauseIntent = new Intent(this, MainActivity.class);
        pauseIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pauseIntent.putExtra("pause", true);
        PendingIntent pausePendingIntent = PendingIntent.getActivity(this, requestID, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setAutoCancel(true);

        Intent cancelIntent = new Intent(this, MainActivity.class);
        builder.setAutoCancel(true);

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notificationlogo)
                .setSound(Uri.parse("uri://notification_xperia.mp3"))
                .setContentTitle("Query Received ")
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                /*.addAction(R.drawable.ic_action_boom, "Action!", someOtherPendingIntent)*/
                .setContentText("Contact Us If Don't Get Mail Within 7 Days")
                .setContentIntent(pausePendingIntent);


//Then add the action to your notification


        NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());


    }

    private void openhelp() {
        firebaseRemoteConfigprice.fetchAndActivate();

        whatnew = (firebaseRemoteConfigprice.getString("showads"));


        if (whatnew.equals("yes")) {
            showads();
            showintads();
            //Toast.makeText(MainActivity.this, "Showing", Toast.LENGTH_SHORT).show();
        } else if (whatnew.equals("no")) {
            mAdView.setVisibility(View.GONE);

            //  Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
        }

        final EditText taskEditText = new EditText(this);
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(MainActivity.this, R.style.AlertDialogedit)
                .setTitle("Your Suggestion")
                .setMessage("Provide Details And Send Us Mail ")
                .setView(taskEditText)
                .setCancelable(false)
                .setPositiveButton("Send Mail", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        sendmailintent(task);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();


    }

    private void sendmailintent(String task) {
        Toast toast = Toast.makeText(this, "SEND MAIL VIA GMAIL/YAHOO ", Toast.LENGTH_LONG);
        View view = toast.getView();

        view.getBackground().setColorFilter((Color.parseColor("#FF104162")), PorterDuff.Mode.SRC_IN);


        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);

        toast.show();

        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode("notificationapp.xenonstudio@gmail.com") +
                "?subject=" + Uri.encode("Notification Log App") +
                "&body=" + Uri.encode("" + task + " \n\n\n ------------ \n\n Version Code : " + versionCode + "\n Build : " + Build.BRAND + "\n" + Build.MODEL + "\n" + Build.DEVICE);
        Uri uri = Uri.parse(uriText);

        send.setData(uri);
        startActivity(Intent.createChooser(send, "Send Mail Via : "));
    }

    private void shareapplink() {

        Toast.makeText(MainActivity.this, "Sharing App..", Toast.LENGTH_SHORT).show();


        ShareCompat.IntentBuilder.from(MainActivity.this)
                .setType("text/plain")
                .setChooserTitle("Share URL")
                .setText("Hey, Download This Awesome Notification History Log App - " + "https://play.google.com/store/apps/details?id=com.appnotification.notificationhistorylog")
                .startChooser();
    }

    private void sendnotification() {

        int requestID = (int) System.currentTimeMillis();

        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),
                0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder buildernotif = new NotificationCompat.Builder(getBaseContext());

        Intent pauseIntent = new Intent(this, MainActivity.class);
        pauseIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pauseIntent.putExtra("pause", true);
        PendingIntent pausePendingIntent = PendingIntent.getActivity(this, requestID, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        buildernotif.setAutoCancel(true);

        Intent cancelIntent = new Intent(this, MainActivity.class);
        buildernotif.setAutoCancel(true);

        buildernotif.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notificationlogo)
                .setContentTitle("Deleted Successfully !")
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))

                /*.addAction(R.drawable.ic_action_boom, "Action!", someOtherPendingIntent)*/
                .setContentText("Logs Has Been Deleted")
                .setContentIntent(pausePendingIntent);

//Then add the action to your notification

        NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, buildernotif.build());

    }

    private void showlogtext() {

        int status = 0;

        SharedCommon.putSharedPreferencesString(MainActivity.this, SharedCommon.keylogtext, String.valueOf(status));


    }

    private void opensetting() {

        SharedCommon scor = new SharedCommon();

        int orfavc = SharedCommon.getPreferencesInt(getApplicationContext(), keyopensetting, 0);


        orfavc++;
        SharedCommon.putPreferencesInt(getApplicationContext(), SharedCommon.keyopensetting, orfavc);
        Intent startIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(startIntent);

    }

    private void confirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setTitle(R.string.dialog_delete_header);
        builder.setMessage(R.string.dialog_delete_text);
        builder.setNegativeButton(R.string.dialog_delete_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setPositiveButton(R.string.dialog_delete_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                truncate();
                sendnotification();
            }
        });
        builder.show();
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
        } catch (Exception e) {
            if (Const.DEBUG) e.printStackTrace();
        }
    }


    private void doFirstRun2() {

        SharedPreferences settings = getSharedPreferences("FIRSTRUNTEXTrateitbaby", MODE_PRIVATE);
        if (settings.getBoolean("isFirstRunDialogBoxtextrb", true)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunDialogBoxtextrb", false);
            editor.commit();

            showratingbar();


        }
    }

    private void doFirstRun() {
        SharedPreferences settings = getSharedPreferences("FIRSTRUNTEXT111", MODE_PRIVATE);
        if (settings.getBoolean("isFirstRunDialogBoxtext111", true)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunDialogBoxtext111", false);
            editor.commit();
            sendnoifications();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);

            builder.setIcon(R.drawable.notificationlogo);

            builder.setTitle("Welcome !");
            builder.setMessage("Please Make Sure That You Provide Necessary Permissions ");

            builder.setCancelable(false);

            builder.setPositiveButton("Where", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showtipshowcase();

                    dialogInterface.dismiss();
                }
            });
            builder.setNeutralButton("GOT IT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                }
            });
            builder.setNeutralButton("Whats New !", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);

                    builder.setIcon(R.drawable.notificationlogo);

                    builder.setTitle("New Update  :-" + versionName);
                    builder.setMessage("Multi-Language Support \n View Notification App Wise \nUI Changes \nBug Fixed \n");

                    builder.setCancelable(false);

                    builder.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showtipshowcase();

                            dialogInterface.dismiss();
                        }
                    });
                    android.app.AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            android.app.AlertDialog dialog = builder.create();
            dialog.show();


        }
    }

    private void sendnoifications() {

        int requestID = (int) System.currentTimeMillis();

        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),
                0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder buildernotif = new NotificationCompat.Builder(getBaseContext());

        Intent pauseIntent = new Intent(this, MainActivity.class);
        pauseIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pauseIntent.putExtra("pause", true);
        PendingIntent pausePendingIntent = PendingIntent.getActivity(this, requestID, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        buildernotif.setAutoCancel(true);

        Intent cancelIntent = new Intent(this, MainActivity.class);
        buildernotif.setAutoCancel(true);

        buildernotif.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notificationlogo)
                .setContentTitle("You Have Latest Version Of The App ")
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))

                /*.addAction(R.drawable.ic_action_boom, "Action!", someOtherPendingIntent)*/
                .setContentText("Now Save Battery With The New Dark Mode !")
                .setContentIntent(pausePendingIntent);

//Then add the action to your notification

        NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, buildernotif.build());


    }


    private void export() {
        if (!ExportTask.exporting) {
            ExportTask exportTask = new ExportTask(this, findViewById(android.R.id.content));
            exportTask.execute();
            doFirstRun2();
        }
    }


}
