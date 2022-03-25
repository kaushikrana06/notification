package com.appnotification.notificationhistorylog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.appnotification.notificationhistorylog.ui.BrowseActivity;

import java.util.ArrayList;

import timber.log.Timber;

public class FavActivity extends AppCompatActivity {
    DbHelperIdeas dbHelper;
    ArrayAdapter<String> mAdapter;
    ListView lstTask;
    View view1, view2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

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

        dbHelper = new DbHelperIdeas(this);


        lstTask = findViewById(R.id.lstTaskidea);

        loadTaskList();
        doFirstRun();

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
        Timber.e((String) taskTextView.getText());
        String task = String.valueOf(taskTextView.getText());
        dbHelper.deleteTask(task);
        loadTaskList();
    }

    private void doFirstRun() {
        SharedPreferences settings = getSharedPreferences("FIRSTRUNTEXT2", MODE_PRIVATE);
        if (settings.getBoolean("isFirstRunDialogBoxtext2", true)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunDialogBoxtext2", false);
            editor.commit();

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(FavActivity.this, R.style.DialogTheme);

            builder.setIcon(R.drawable.notificationlogo);

            builder.setTitle("Add Notifications To Favorite  !");
            builder.setMessage("You Can Later Read Them  ");

            builder.setCancelable(false);


            builder.setPositiveButton("Start Adding ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent startIntent = new Intent(FavActivity.this, BrowseActivity.class);
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

}
