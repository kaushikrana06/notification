package com.appnotification.notificationhistorylog;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appnotification.notificationhistorylog.CommonCl.SharedCommon;
import com.appnotification.notificationhistorylog.ui.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.key1;
import static com.appnotification.notificationhistorylog.CommonCl.SharedCommon.keynotification;

public class NoticeActivity extends AppCompatActivity {

    public String whatnew;
    public String versionfirebase;
    Button btnlimit;

    Thread splashTread;
    String apppackagename = "com.appnotification.notificationhistorylog";
    ProgressBar progressBar;
    LinearLayout linernotification;
    String checkupdates = "";
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;


    //String apppackagename = "com.thetechroot.vision";
    String appid = BuildConfig.APPLICATION_ID;
    FirebaseRemoteConfig firebaseRemoteConfigprice;
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);


        linernotification = findViewById(R.id.linernotification);
        linernotification.setVisibility(View.GONE);


        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF0365A9, android.graphics.PorterDuff.Mode.MULTIPLY);


       /* OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();*/


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");

        mDatabase.keepSynced(true);

        mBlogList = findViewById(R.id.myrecview);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));


        checkConnection();
        doFirstRun();

        firebaseRemoteConfigprice = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
        firebaseRemoteConfigprice.setConfigSettingsAsync(configSettings);

        Map<String, Object> pricedata = new HashMap<>();
        pricedata.put("whatsnew", "whatsnew");
        pricedata.put("version", "111");

        firebaseRemoteConfigprice.setDefaultsAsync(pricedata);


    }

    private void updateremote() {


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


*/
                            whatnew = (firebaseRemoteConfigprice.getString("whatsnew"));
                            versionfirebase = (firebaseRemoteConfigprice.getString("version"));


                            AlertDialog.Builder builder = new AlertDialog.Builder(NoticeActivity.this, R.style.DialogTheme);


                            builder.setTitle("What's New ");

                            builder.setMessage(whatnew + "\n\nLatest Version" + versionfirebase);
                            builder.setIcon(R.drawable.ic_splash_logo);

                            builder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                            Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            positive.setTextColor(getResources().getColor(R.color.colorText));
                            Log.e("TaskError", "firebaseremote" + firebaseRemoteConfigprice.getString("btn_text"));


                                   /* Picasso.get().load(firebaseRemoteConfigprice.getString("image_link"))
                                            .into(img);*/
                        } else {


                            String exp = ("" + task.getException().getMessage());
                            if (exp.equals("null")) {

                                whatnew = ("Server Not Responding ");

                                AlertDialog.Builder builder = new AlertDialog.Builder(NoticeActivity.this, R.style.DialogTheme);


                                builder.setTitle("What's New ");

                                builder.setMessage(whatnew);
                                builder.setIcon(R.drawable.ic_splash_logo);

                                builder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        dialogInterface.dismiss();
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                                Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                positive.setTextColor(getResources().getColor(R.color.colorText));

                                // Toast.makeText(PriceListActivity.this, "Taking Longer Time", Toast.LENGTH_LONG).show();
                               /* txt600.setText("600 Credit (70% Off)");
                                txt1500.setText("1500 Credit(50% Off)");
                                txt3200.setText("3200 Credit(50% Off)");
                                txt5000.setText("5000 Credits(70% Off)");*/



/*

                                 txt600.setText(credit600+"/600 Credits");
                                 txt1500.setText(credit1500+"/1500 Credits");
                                 txt3200.setText(credit3200+"/3200 Credits");
                                 txt5000.setText(credit5000+"/5000 Credits");
*/


                            } else {
                                Log.e("TaskError", "taskexcep :" + task.getException().getMessage() + task.getException() + task);
                                Toast.makeText(NoticeActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        startListening();
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF0365A9, android.graphics.PorterDuff.Mode.MULTIPLY);

    }


    public void startListening() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Notifications")
                .limitToLast(50);


        progressBar.getIndeterminateDrawable().setColorFilter(0x0F82D2, android.graphics.PorterDuff.Mode.MULTIPLY);


        FirebaseRecyclerOptions<Notif> options =
                new FirebaseRecyclerOptions.Builder<Notif>()
                        .setQuery(query, Notif.class)
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Notif, UserViewHolder>(options) {
            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.noti_row, parent, false);


                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(UserViewHolder holder, int position, final Notif model) {
                // Bind the Chat object to the ChatHolder

                checkupdates = model.getImage();

                progressBar.getIndeterminateDrawable().setColorFilter(0x07578f, android.graphics.PorterDuff.Mode.MULTIPLY);


                progressBar.setVisibility(View.GONE);
                linernotification.setVisibility(View.GONE);


                holder.setTitle(model.getTitle());
                Timber.i("@@@ Response setTitle%s", model.getTitle());


                Timber.i("@@@ Response model%s", model);

                Common.HOLDER = model.getTitle();
                holder.setDesc(model.getDesc());

                Timber.i("@@@ Response D%s", model.getDesc());

                holder.setImage(model.getImage(), getApplicationContext());

                Timber.i("@@@ Response setImage%s ", model.getImage());


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                       /* if (model.getImage().contains("Update")){
                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + apppackagename)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + apppackagename)));
                            }

                        }*/

                        if (model.getImage().contains("Mail")) {
                            Intent send = new Intent(Intent.ACTION_SENDTO);
                            String uriText = "mailto:" + Uri.encode("thexenonstudio@gmail.com") +
                                    "?subject=" + Uri.encode("Around Me App - Contact") +
                                    "&body=" + Uri.encode("Hello, Type Your Query/Problem/Bug/Suggestions Here");
                            Uri uri = Uri.parse(uriText);

                            send.setData(uri);
                            startActivity(Intent.createChooser(send, "Send Mail Via : "));

                        }

                       /* if(model.getTitle().contains("Notification")){

                            .setGravity(Gravity.CENTER | Gravity.BOTTOM);
                        }*/


                        if (model.getTitle().contains("Offer")) {

                            WebView webView = new WebView(NoticeActivity.this);
                            webView.loadUrl(model.getImage());

                        }

                        if (model.getImage().contains("Help")) {


                            Intent startIntent = new Intent(NoticeActivity.this, MainActivity.class);
                            startActivity(startIntent);

                        }
                        if (model.getImage().contains("December Value Back")) {


                        }

                        if (model.getImage().contains("Product Search")) {

                            Intent startIntent = new Intent(NoticeActivity.this, MainActivity.class);
                            startActivity(startIntent);
                        }

                        if (model.getImage().contains(versionName)) {


                            Boolean br = model.getImage().contains(versionName);

                            Timber.i("@@@ Response br" + br + versionName);


                            checkupdates = model.getImage();

                            if (br.equals(true)) {
                                Timber.i("@@@ Response br inside" + br + versionName);

                                // Toast.makeText(NoticeActivity.this, ""+versionName+updatecheck, Toast.LENGTH_SHORT).show();

                                Toast.makeText(NoticeActivity.this, "App Is Updated", Toast.LENGTH_LONG).show();

                            } else {
                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + apppackagename)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + apppackagename)));
                                }

                            }


                        } else if (model.getImage().contains("Update")) {
                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + apppackagename)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + apppackagename)));
                            }

                        }


                        if (model.getImage().contains("PRO")) {

                            Intent startIntent = new Intent(NoticeActivity.this, MainActivity.class);
                            startActivity(startIntent);


                        }

                        if (model.getImage().contains("Setting")) {

                            Toast.makeText(NoticeActivity.this, "Change Language", Toast.LENGTH_SHORT).show();
                            Intent startIntent = new Intent(NoticeActivity.this, SettingsActivity.class);
                            startActivity(startIntent);


                        }

                        if (model.getImage().contains("Public Feedback")) {

                            Intent startIntent = new Intent(NoticeActivity.this, MainActivity.class);
                            startActivity(startIntent);


                        }
                        if (model.getImage().contains("Offer")) {


                            Toast.makeText(NoticeActivity.this, "Enter Promo Code", Toast.LENGTH_SHORT).show();


                        }

                        if (model.getTitle().contains("Feedback")) {

                            WebView webView = new WebView(NoticeActivity.this);
                            webView.loadUrl(model.getImage());

                        }

                        if (model.getImage().contains("ReferPage")) {

                            Intent startIntent = new Intent(NoticeActivity.this, MainActivity.class);
                            startActivity(startIntent);

                        }
                        if (model.getTitle().contains("Scratch")) {

                            String srr = model.getImage();


                        }
                        if (model.getTitle().contains("Promo")) {


                            String Webviewurl = model.getImage();
                           /* WebView webView = new WebView(NoticeActivity.this);
                            webView.loadUrl(Webviewurl);*/

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(Webviewurl));
                            startActivity(i);


                        }

                        if (model.getTitle().contains("http")) {


                            String Webviewurl2 = model.getImage();
                           /* WebView webView = new WebView(NoticeActivity.this);
                            webView.loadUrl(Webviewurl);*/

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(Webviewurl2));
                            startActivity(i);


                        }
                        if (model.getImage().contains("Ads")) {

                            final EditText taskEditText = new EditText(NoticeActivity.this);
                            AlertDialog dialog = new AlertDialog.Builder(NoticeActivity.this, R.style.DialogTheme)
                                    .setTitle("Provide Us Your Special Code : ")
                                    .setMessage("This Will Remove Ads From App For Sometime")
                                    .setView(taskEditText)
                                    .setCancelable(false)
                                    .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String task = String.valueOf(taskEditText.getText());

                                        }
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .create();
                            dialog.show();
                            Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            positive.setTextColor(getResources().getColor(R.color.colorText));
                            Button negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            negative.setTextColor(getResources().getColor(R.color.colorText));
                        }

                        int j = SharedCommon.getPreferencesInt(getApplicationContext(), keynotification, 0);


                        final SharedCommon scnotification = new SharedCommon();

                        if (j < 0) {
                            // Toast.makeText(NoticeActivity.this, "Already Added", Toast.LENGTH_SHORT).show();
                        }
                        if (model.getImage().contains("Add 10")) {


                            if (j < 0) {

                                Toast.makeText(NoticeActivity.this, "Already Added", Toast.LENGTH_SHORT).show();

                            } else {

                                int i = SharedCommon.getPreferencesInt(getApplicationContext(), key1, 50);

                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(NoticeActivity.this);
                                SharedPreferences.Editor edit = preferences.edit();
                                edit.putInt(key1, i + 10);
                                edit.apply();


                                j--;
                                SharedCommon.putPreferencesInt(getApplicationContext(), keynotification, j);

                                Toast.makeText(NoticeActivity.this, "Yaay !  Added", Toast.LENGTH_SHORT).show();

                            }


                        } else {

                            Toast.makeText(NoticeActivity.this, "" + model.getImage(), Toast.LENGTH_LONG).show();

                        }

                    }
                });
                // ...

                holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(NoticeActivity.this, R.style.DialogTheme);


                        builder.setTitle("Copy");

                        builder.setCancelable(true);

                        builder.setPositiveButton("COPY TEXT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Toast.makeText(NoticeActivity.this, "Text Copied ", Toast.LENGTH_SHORT).show();

                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("", model.getTitle());
                                clipboard.setPrimaryClip(clip);

                                dialogInterface.dismiss();
                            }
                        }).setNeutralButton("COPY LINK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Toast.makeText(NoticeActivity.this, "Link Copied ", Toast.LENGTH_SHORT).show();

                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("", model.getImage());
                                clipboard.setPrimaryClip(clip);

                                dialogInterface.dismiss();
                            }

                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        positive.setTextColor(getResources().getColor(R.color.colorText));
                        Button negative = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                        negative.setTextColor(getResources().getColor(R.color.colorText));


                        return false;
                    }
                });
            }

        };
        mBlogList.setAdapter(adapter);
        adapter.startListening();
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void checkConnection() {
        if (isOnline()) {
            linernotification.setVisibility(View.GONE);
            //progressBar.setVisibility(View.GONE);
            // btnretry.setVisibility(View.GONE);

            /*splashTread = new Thread() {
                @Override
                public void run() {
                    try {
                        int waited = 0;
                        // Splash screen pause time
                        while (waited < 18000) {
                            sleep(100);
                            waited += 100;
                        }
                        Intent intent = new Intent(NoticeActivity.this,
                                MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        NoticeActivity.this.finish();
                    } catch (InterruptedException e) {
                        // do nothing
                    } finally {
                        NoticeActivity.this.finish();
                    }

                }
            };
            splashTread.start();*/


            /*View parentLayout = findViewById(android.R.id.content);

            Snackbar snackbar = Snackbar
                    .make(parentLayout, "Internet Is Back ! " , Snackbar.LENGTH_LONG);

            snackbar.show();*/
          /*  if(progressBar != null && progressBar.isShowing()){
                progressBar.dismiss();


                View parentLayout = findViewById(android.R.id.content);

                Snackbar snackbar = Snackbar
                        .make(parentLayout, "Internet Is Back ! " , Snackbar.LENGTH_SHORT);

                snackbar.show();
            }*/
        } else {

            linernotification.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.GONE);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.notification_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_update) {


            updateremote();


            //CreateAlertDialogWithRadioButtonGroup();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doFirstRun() {
        SharedPreferences settings = getSharedPreferences("FIRSRRUNMAINNOTIF1", MODE_PRIVATE);
        if (settings.getBoolean("ISFIRSTMANINNOTIF1", true)) {
            /*Toast toast = Toast.makeText(this, "Please Select Any One Plan", Toast.LENGTH_LONG);
            View view = toast.getView();

            view.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

            TextView text = view.findViewById(android.R.id.message);
            text.setTextColor(Color.BLACK);

            toast.show();*/

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("ISFIRSTMANINNOTIF1", false);
            editor.apply();


            AlertDialog.Builder builder = new AlertDialog.Builder(NoticeActivity.this, R.style.DialogTheme);
            builder.setTitle(R.string.notification_updates);
            builder.setIcon(R.drawable.ic_splash_logo);
            builder.setMessage(R.string.message_notification_update);

            builder.setCancelable(false);

            builder.setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
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

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            TextView NameView = mView.findViewById(R.id.noti_title);
            NameView.setText(title);
            Timber.i("@@@ Response title%s", title);
        }

        public void setDesc(String desc) {
            TextView userDesc = mView.findViewById(R.id.noti_dec);
            userDesc.setText(desc);
        }

        public void setImage(String image, Context ctx) {
            ImageView Image = mView.findViewById(R.id.noti_image);
            //Picasso.get().load(image).into(Image);
        }
    }

    /*private void showPrice() {

        FlipAnimator animator = new FlipAnimator(priceBeforeView, priceAfterText, priceContainer.getWidth()/2, priceContainer.getHeight()/2);
        animator.setDuration(800);
        animator.setRotationDirection(FlipAnimator.DIRECTION_Y);
        priceContainer.startAnimation(animator);
    }*/
}

