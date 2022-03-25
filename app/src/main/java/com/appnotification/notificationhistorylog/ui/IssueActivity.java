package com.appnotification.notificationhistorylog.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.appnotification.notificationhistorylog.FAQActivity;
import com.appnotification.notificationhistorylog.R;

public class IssueActivity extends AppCompatActivity {

    public int logs;
    Button buttonAllow, buttonfaqs, buttonhelp, button_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);


        buttonAllow = findViewById(R.id.button_allow);
        buttonAllow.setOnClickListener(view -> {
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        });

        buttonfaqs = findViewById(R.id.button_faqs);
        buttonfaqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(IssueActivity.this, FAQActivity.class);
                startActivity(startIntent);
            }
        });
        button_check = findViewById(R.id.button_check);
        button_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checklogs();
            }
        });
        buttonhelp = findViewById(R.id.button_help);
        buttonhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(IssueActivity.this, "SEND MAIL VIA GMAIL/YAHOO ", Toast.LENGTH_LONG);
                View view1 = toast.getView();

                view1.getBackground().setColorFilter((Color.parseColor("#FF104162")), PorterDuff.Mode.SRC_IN);


                TextView text = view1.findViewById(android.R.id.message);
                text.setTextColor(Color.WHITE);

                toast.show();

                Intent send = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode("notificationapp.xenonstudio@gmail.com") +
                        "?subject=" + Uri.encode("Notification Log App ") +
                        "&body=" + Uri.encode("Write Your Query Here");
                Uri uri = Uri.parse(uriText);

                send.setData(uri);
                startActivity(Intent.createChooser(send, "Send Mail Via : "));
            }
        });


    }

    private void checklogs() {

        BrowseAdapter adapter = new BrowseAdapter(this);
        String count = String.valueOf(adapter.getItemCount());
        //Toast.makeText(this, ""+count, Toast.LENGTH_SHORT).show();
        logs = Integer.parseInt(count);


        if (logs >= 1) {
            Intent startIntent = new Intent(IssueActivity.this, NewMainActivity.class);
            startActivity(startIntent);
        } else if (logs == 0) {
            Toast.makeText(this, "No Notifications Logged Yet !", Toast.LENGTH_SHORT).show();
        }
    }
}
