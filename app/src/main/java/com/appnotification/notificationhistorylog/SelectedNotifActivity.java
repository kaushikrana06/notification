package com.appnotification.notificationhistorylog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.appnotification.notificationhistorylog.db.DbHelperGmail;
import com.appnotification.notificationhistorylog.ui.BrowseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;

public class SelectedNotifActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DbHelperGmail dbHelper;
    ArrayAdapter<String> mAdapter;
    ListView lstTask;
    View view1, view2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_notif);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        dbHelper = new DbHelperGmail(this);


        lstTask = findViewById(R.id.lsttakgmail);

        loadTaskList();
        doFirstRun();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.selected_notif, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void loadTaskList() {
        ArrayList<String> taskList = dbHelper.getTaskList();
        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<String>(this, R.layout.row, R.id.task_title, taskList);
            lstTask.setAdapter(mAdapter);

        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = parent.findViewById(R.id.task_title);
        Log.e("String", (String) taskTextView.getText());
        String task = String.valueOf(taskTextView.getText());
        dbHelper.deleteTask(task);
        loadTaskList();
    }

    private void doFirstRun() {
        SharedPreferences settings = getSharedPreferences("FIRSTRUNTEXT2", MODE_PRIVATE);
        if (settings.getBoolean("isFirstRunDialogBoxtext2", true)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunDialogBoxtext2", false);
            editor.apply();

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SelectedNotifActivity.this, R.style.DialogTheme);

            builder.setIcon(R.drawable.ic_splash_logo);

            builder.setTitle("Add Notifications To Favorite  !");
            builder.setMessage("You Can Later Read Them  ");

            builder.setCancelable(false);


            builder.setPositiveButton("Start Adding ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent startIntent = new Intent(SelectedNotifActivity.this, BrowseActivity.class);
                    startActivity(startIntent);

                }
            });
            builder.setNeutralButton("Got It ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            });

            android.app.AlertDialog dialog = builder.create();
            dialog.show();
            Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positive.setTextColor(getResources().getColor(R.color.colorText));
            Button neutral = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
            neutral.setTextColor(getResources().getColor(R.color.colorText));

        }
    }

    private void showtipshowcase() {

        new GuideView.Builder(this)
                .setTitle("Add Tasks")
                .setContentText("Click Here To Add Your Ideas")
                .setGravity(Gravity.auto) //optional
                .setDismissType(DismissType.outside) //optional - default DismissType.targetView
                .setTargetView(view1)
                .setContentTextSize(14)//optional
                .setTitleTextSize(16)//optional
                .build()
                .show();


    }
}

