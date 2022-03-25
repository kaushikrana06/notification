package com.appnotification.notificationhistorylog.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.appnotification.notificationhistorylog.BuildConfig;
import com.appnotification.notificationhistorylog.R;
import com.google.android.material.textfield.TextInputEditText;

public class HelpActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText editTextName, editTextEmail, editTextCountry, editTextQuery;
    private RadioGroup radioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView backButton = toolbar.findViewById(R.id.back_image);
        TextView titleTextView = toolbar.findViewById(R.id.title_text);
        ImageView searchButton = toolbar.findViewById(R.id.search_image);
        ImageView menuButton = toolbar.findViewById(R.id.menu_image);
        searchButton.setVisibility(View.GONE);
        menuButton.setVisibility(View.INVISIBLE);

        titleTextView.setText(getString(R.string.title_help));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editTextName = findViewById(R.id.name);
        editTextCountry = findViewById(R.id.country);
        editTextEmail = findViewById(R.id.email);
        editTextQuery = findViewById(R.id.query);
        radioGroup = findViewById(R.id.group_query);
        Button buttonSend = findViewById(R.id.button_send);
        buttonSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String textPattern = "^[\\p{L} .'-]+$";
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();
        String country = editTextCountry.getText().toString();
        String query = editTextQuery.getText().toString();
        int selectedRadio = radioGroup.getCheckedRadioButtonId();
        String queryType = "";
        if (selectedRadio == R.id.query_general) {
            queryType = "General";
        } else if (selectedRadio == R.id.query_technical) {
            queryType = "Technical";
        } else {
            queryType = "Other";
        }
        if (name.matches(textPattern)) {
            if (email.matches(emailPattern)) {
                if (country.matches(textPattern)) {
                    if (!query.equals("")) {
                        try {
                            Intent send = new Intent(Intent.ACTION_SENDTO);
                            String uriText = "mailto:" + Uri.encode("notificationappgp@gmail.com") +
                                    "?subject=" + Uri.encode(email + " - Notification Log App") +
                                    "&body=" + Uri.encode("" + "Name: " + name + "\n" + "Country: " + country + "\n" +
                                    "Query Type: " + queryType + "\n" + "Query: " + query + " \n\n\n ------------ \n\n Version Code : "
                                    + BuildConfig.VERSION_CODE + "\n Version Name : " + BuildConfig.VERSION_NAME
                                    + "\n Build : " + Build.BRAND + "\n" + Build.MODEL + "\n" + Build.DEVICE);
                            Uri uri = Uri.parse(uriText);
                            send.setData(uri);
                            send.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(Intent.createChooser(send, "Send Mail Via : "), 121);
                        } catch (Exception ignore) {

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please add some query to assist with!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Country!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Invalid Email!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Invalid name!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 121) {
//            finish();
////            if (resultCode == RESULT_OK) {
////                Toast.makeText(getApplicationContext(), getString(R.string.message_query_send), Toast.LENGTH_SHORT).show();
////                finish();
////            } else {
////                Toast.makeText(getApplicationContext(), getString(R.string.message_help_error), Toast.LENGTH_SHORT).show();
////            }
//        }
    }
}