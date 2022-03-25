package com.appnotification.notificationhistorylog;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appnotification.notificationhistorylog.Interface.IItemClickListner;
import com.appnotification.notificationhistorylog.Model.Item;
import com.appnotification.notificationhistorylog.ui.MainActivity;
import com.appnotification.notificationhistorylog.ui.NewMainActivity;
import com.appnotification.notificationhistorylog.viewholder.ItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FAQActivity extends AppCompatActivity {


//    InterstitialAd mInterstitialAd;

    RecyclerView recyclerView;
    List<Item> items = new ArrayList();
    FirebaseRecyclerAdapter<Item, ItemViewHolder> adapter;
    SparseBooleanArray expandstate = new SparseBooleanArray();

    CharSequence txtitem = "";

    LinearLayout linernotification;
    ProgressBar progressBar;
    String apppackagename = "com.thetechroot.vision";
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;
    String appid = BuildConfig.APPLICATION_ID;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);


        //ShowAds();


        linernotification = findViewById(R.id.linerfaq);
        linernotification.setVisibility(View.GONE);


        progressBar = findViewById(R.id.progressBarfaq);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(0x07578f, android.graphics.PorterDuff.Mode.MULTIPLY);

        recyclerView = findViewById(R.id.lst_item);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        retrivedata();


        setdata();
        checkConnection();


        mUserDatabase = FirebaseDatabase.getInstance().getReference("Items");


        checkclicklink();


    }

    private void setdata() {

        Query query = FirebaseDatabase.getInstance().getReference().child("Items");
        FirebaseRecyclerOptions<Item> options = new FirebaseRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(options) {


            @Override
            public int getItemViewType(int position) {
                Log.e("Faqs", "items" + items);

                if (items.get(position).isExpandable())
                    return 1;

                else {
                    return 0;
                }

            }


            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, final int position, @NonNull final Item model) {

                linernotification.setVisibility(View.GONE);

                progressBar.getIndeterminateDrawable().setColorFilter(0x07578f, android.graphics.PorterDuff.Mode.MULTIPLY);


                progressBar.setVisibility(View.GONE);

                switch (holder.getItemViewType()) {


                    case 0: {

                        ItemViewHolder viewHolder = holder;
                        viewHolder.setIsRecyclable(false);
                        viewHolder.txt_item.setText(model.getText());


                        txtitem = viewHolder.txt_item.getText();

                       /* TextView tv = (TextView) findViewById(R.id.txt_item_text);
                        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/opansans.ttf");
                        tv.setTypeface(face);*/

                        ////
                        ////
                        viewHolder.setiItemClickListner(new IItemClickListner() {
                            @Override
                            public void onClick(View view, int position) {

                                Toast.makeText(FAQActivity.this, "" + items.get(position).getText(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                    case 1: {

                        final ItemViewHolder viewHolder = holder;
                        viewHolder.setIsRecyclable(true);
                        viewHolder.txt_item.setText(model.getText());
                        viewHolder.txt_child.setText(model.getSubText());


                        txtitem = viewHolder.txt_item.getText();

                        Log.e("Faqs", "modeltext" + model.getText());


                        viewHolder.expandableLinearLayout.setInRecyclerView(true);
                        viewHolder.expandableLinearLayout.setExpanded(expandstate.get(position));
                        viewHolder.expandableLinearLayout.setListener(new ExpandableLayoutListenerAdapter() {
                            @Override
                            public void onPreOpen() {

                                changeRoatate(viewHolder.button, 0f, 180f).start();
                                expandstate.put(position, true);

                            }

                            @Override
                            public void onPreClose() {
                                changeRoatate(viewHolder.button, 180f, 0f).start();
                                expandstate.put(position, false);

                            }
                        });

                        viewHolder.txt_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                viewHolder.expandableLinearLayout.toggle();
                            }
                        });
                        viewHolder.button.setRotation(expandstate.get(position) ? 180f : 0f);
                        viewHolder.button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                viewHolder.expandableLinearLayout.toggle();
                            }
                        });

                        viewHolder.txt_child.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                viewHolder.expandableLinearLayout.toggle();
                                if (viewHolder.txt_child.getText().equals("Mail To : thexenonstudio@gmail.com")) {

                                    Toast toast = Toast.makeText(FAQActivity.this, "Mail Us Your Query", Toast.LENGTH_LONG);
                                    View view1 = toast.getView();

                                    view1.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

                                    TextView text = view1.findViewById(android.R.id.message);
                                    text.setTextColor(Color.BLACK);

                                    toast.show();

                                    Intent send = new Intent(Intent.ACTION_SENDTO);
                                    String uriText = "mailto:" + Uri.encode("thexenonstudio@gmail.com") +
                                            "?subject=" + Uri.encode("Around Me - Contact") +
                                            "&body=" + Uri.encode("Hello, Type Your Query/Problem/Bug/Suggestions Here" + " \n\n\n ------------ \n\n Version Code : " + versionCode + "\n Version Name : " + versionName + "\n Application ID : " + appid);
                                    Uri uri = Uri.parse(uriText);

                                    send.setData(uri);
                                    startActivity(Intent.createChooser(send, "Send Mail Via : "));

                                }

                                //Toast.makeText(FAQActivity.this, ""+viewHolder.txt_child.getText(), Toast.LENGTH_SHORT).show();
                            }
                        });


                        viewHolder.setiItemClickListner(new IItemClickListner() {
                            @Override
                            public void onClick(View view, int position) {
                                // Toast.makeText(FAQActivity.this, ""+model.getText(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                    break;
                    default:
                        break;
                }
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                if (viewType == 0) {

                    View itemview = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_with_child, viewGroup, false);
                    return new ItemViewHolder(itemview, viewType == 1);
                } else {
                    View itemview = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_with_newchild, viewGroup, false);
                    return new ItemViewHolder(itemview, viewType == 1);
                }
            }
        };

        expandstate.clear();
        for (int i = 0; i < items.size(); i++)
            expandstate.append(i, false);

        recyclerView.setAdapter(adapter);
    }

    private ObjectAnimator changeRoatate(RelativeLayout button, float from, float to) {

        ObjectAnimator animator = ObjectAnimator.ofFloat(button, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

    private void retrivedata() {

        items.clear();

        DatabaseReference db = FirebaseDatabase.getInstance()
                .getReference()
                .child("Items");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot itemSnapShot : dataSnapshot.getChildren()) {

                    Item item = itemSnapShot.getValue(Item.class);
                    items.add(item);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(FAQActivity.this, "Something went Wrong " + databaseError, Toast.LENGTH_SHORT).show();
            }
        });

    }

//    private void ShowAds() {
//
//        mInterstitialAd = new InterstitialAd(this);
//
//        // set the ad unit ID
//        mInterstitialAd.setAdUnitId("ca-app-pub-5136021335954278/2863631090");
//
//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        // Load ads into Interstitial Ads
//        mInterstitialAd.loadAd(adRequest);
//
//        mInterstitialAd.setAdListener(new AdListener() {
//            public void onAdLoaded() {
//                showInterstitial();
//            }
//        });
//
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent startIntent = new Intent(FAQActivity.this, NewMainActivity.class);
        startActivity(startIntent);
    }

//    private void showInterstitial() {
//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        }
//    }

    @Override
    protected void onStart() {

        checkConnection();
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF0365A9, android.graphics.PorterDuff.Mode.MULTIPLY);

        if (adapter != null)
            adapter.startListening();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (adapter != null)
            adapter.stopListening();
        super.onStop();
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void checkConnection() {
        if (isOnline()) {


            Log.e("", "");

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
                        Intent intent = new Intent(DemoActivity.this,
                                MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        DemoActivity.this.finish();
                    } catch (InterruptedException e) {
                        // do nothing
                    } finally {
                        DemoActivity.this.finish();
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
    protected void onResume() {
        checkConnection();
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_faq, menu);

       /* MenuItem searchViewItem = menu.findItem(R.id.menu_search);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();

                firebaseUserSearch(query.toString());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_faqs_mail:
                mail();

                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private void mail() {

        final EditText taskEditText = new EditText(this);
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(FAQActivity.this, R.style.DialogTheme)
                .setTitle("Your Query")
                .setMessage("Provide Details And Send Us Mail ")
                .setView(taskEditText)
                .setCancelable(false)
                .setPositiveButton("Send Mail", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        try {
                            sendmailintent(task);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

        Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positive.setTextColor(getResources().getColor(R.color.colorText));
    }

    private void sendmailintent(String task) throws Exception {
        Toast toast = Toast.makeText(this, "SEND MAIL VIA GMAIL/YAHOO ", Toast.LENGTH_LONG);
        View view = toast.getView();

        view.getBackground().setColorFilter((Color.parseColor("#FF104162")), PorterDuff.Mode.SRC_IN);


        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);

        toast.show();

        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode("notificationappgp@gmail.com") +
                "?subject=" + Uri.encode("Notification Log App - FAQs") +
                "&body=" + Uri.encode("" + task + " \n\n\n ------------ \n\n Version Code : " + versionCode + "\n Build : " + Build.BRAND + "\n" + Build.MODEL + "\n" + Build.DEVICE);
        Uri uri = Uri.parse(uriText);

        send.setData(uri);
        startActivity(Intent.createChooser(send, "Send Mail Via : "));
    }


    private void firebaseUserSearch(final String query) {

        final View parentLayout = findViewById(android.R.id.content);

        Snackbar snackbar1 = Snackbar
                .make(parentLayout, "Unable To Find Your Query", Snackbar.LENGTH_LONG)
                .setAction("Send Mail", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast toast = Toast.makeText(FAQActivity.this, "Mail Us Your Query", Toast.LENGTH_LONG);
                        View view1 = toast.getView();

                        view1.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

                        TextView text = view1.findViewById(android.R.id.message);
                        text.setTextColor(Color.BLACK);

                        toast.show();

                        Intent send = new Intent(Intent.ACTION_SENDTO);
                        String uriText = "mailto:" + Uri.encode("notificationapp.xenonstudio@gmail.com") +
                                "?subject=" + Uri.encode("Around Me - FAQs") +
                                "&body=" + Uri.encode("" + query + " \n\n\n ------------ \n\n Version Code : " + versionCode + "\n Version Name : " + versionName + "\n Application ID : " + appid);
                        Uri uri = Uri.parse(uriText);

                        send.setData(uri);
                        startActivity(Intent.createChooser(send, "Send Mail Via : "));

                    }
                });
        Query firebaseSearchQuery = mUserDatabase.orderByChild("Items").startAt(query).endAt(query + "\uf8ff");

        //Toast.makeText(this, ""+query, Toast.LENGTH_SHORT).show();

        /*if(query.equals("Hello")){

            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        }
*/


        if (query.isEmpty()) {

            //final View parentLayout = findViewById(android.R.id.content);


            Toast.makeText(this, "Please Search Any Query ", Toast.LENGTH_SHORT).show();

            //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        } else {

            snackbar1.show();
        }
        ////
        if (query.contains("Credit") || query.contains("credit")) {

            //final View parentLayout = findViewById(android.R.id.content);

            snackbar1.dismiss();
            Toast.makeText(this, "Credit", Toast.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar
                    .make(parentLayout, "Credit System Working ", Snackbar.LENGTH_LONG)
                    .setAction("Know More", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast toast = Toast.makeText(FAQActivity.this, "Read How Credit System Work", Toast.LENGTH_LONG);

                            Intent startIntent = new Intent(FAQActivity.this, MainActivity.class);
                            startActivity(startIntent);


                        }
                    });

            snackbar.show();
            //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        } else {

            snackbar1.show();
        }

        if (query.contains("Offer") || query.contains("offer")) {

            //final View parentLayout = findViewById(android.R.id.content);

            snackbar1.dismiss();
            Snackbar snackbar = Snackbar
                    .make(parentLayout, "Tap To See Offers/Promo Codes", Snackbar.LENGTH_LONG)
                    .setAction("Know", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast toast = Toast.makeText(FAQActivity.this, "Read How Credit System Work", Toast.LENGTH_LONG);


                            AlertDialog.Builder builder = new AlertDialog.Builder(FAQActivity.this, R.style.DialogTheme);
                            builder.setTitle("Offers ");
                            builder.setMessage("You Can Avail Offer And Promo Code \n Go To Notification Section And You Will See Offers There If Any Available");
                            builder.setCancelable(false);

                            builder.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
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
                    });

            snackbar.show();
            //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        } else {


            //final View parentLayout = findViewById(android.R.id.content);


            snackbar1.show();
            /*Toast.makeText(this, "Unable To Find What You Are Searching", Toast.LENGTH_SHORT).show();
             */
        }


        if (query.contains("Credit") || query.contains("credit")) {

            //final View parentLayout = findViewById(android.R.id.content);

            snackbar1.dismiss();
            Toast.makeText(this, "Credit", Toast.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar
                    .make(parentLayout, "Credit System Working ", Snackbar.LENGTH_LONG)
                    .setAction("Know More", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast toast = Toast.makeText(FAQActivity.this, "Read How Credit System Work", Toast.LENGTH_LONG);

                            Intent startIntent = new Intent(FAQActivity.this, MainActivity.class);
                            startActivity(startIntent);


                        }
                    });

            snackbar.show();
            //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        }



        /*FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                Users.class,R.layout.layout_with_child, UsersViewHolder.class, firebaseSearchQuery);*/
    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search

            if(query.contains("Hello")){

                Toast.makeText(this, "Hello !", Toast.LENGTH_SHORT).show();
            }

        }
    }*/

    @Override
    protected void onPause() {
        checkConnection();
        super.onPause();
    }

    private void checkclicklink() {


        Uri uri = getIntent().getData();
        String strUsername = "", strPassword = "";
        if (uri != null) {

            Toast.makeText(this, "FAQs", Toast.LENGTH_SHORT).show();


        } else {
            // Your app will pop up even if http://www.myurl.com/sso is clicked, so better to handle null uri
        }
    }
}