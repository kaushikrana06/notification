package com.appnotification.notificationhistorylog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appnotification.notificationhistorylog.ui.MainActivity;

import pl.droidsonroids.gif.GifTextView;

public class SplashScreen extends AppCompatActivity {

    GifTextView splashinternet, splash;
    Thread splashTread;

    ProgressDialog mprogreeinternet;

    TextView txt, txtversion;

    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;

    ImageButton btnretry;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        doFirstRunsecond();

        splash = findViewById(R.id.splashscreen);

        txt = findViewById(R.id.txtint);

        txtversion = findViewById(R.id.txtversion);

        txtversion.setText("v" + versionName);

        btnretry = findViewById(R.id.btnretry);

        txt.setVisibility(View.GONE);
        btnretry.setVisibility(View.GONE);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3600) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashScreen.this,
                            MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    SplashScreen.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashScreen.this.finish();
                }

            }
        };
        splashTread.start();
    }

    private void doFirstRunsecond() {
        SharedPreferences settings = getSharedPreferences("FIRSTRUNTEXT311", MODE_PRIVATE);
        if (settings.getBoolean("isFirstRunDialogBoxtext311", true)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunDialogBoxtext311", false);
            editor.commit();

        /*    AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);

            builder.setIcon(R.drawable.ic_gif_black_24dp);

            builder.setTitle("Welcome !");
            builder.setMessage("We Will Need Camera And Internal Storage Permission To Make GIFs");

            builder.setCancelable(false);

            builder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();


*/


          /*  AlertDialog.Builder builder = new AlertDialog.Builder(TextActivity.this);

            builder.setIcon(R.drawable.iconshandwrit52);

            builder.setTitle("Tip ");
            builder.setMessage("You Can Copy Your Response, You Can Share Your Response \n And You Can Copy Translated Text Also");

            builder.setCancelable(false);

            builder.setPositiveButton("DEMO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {



                    new FancyShowCaseView.Builder(TextActivity.this)
                            .focusOn(findViewById(R.id.action_copy_text))
                            .focusCircleRadiusFactor(1.0)
                            .title("\n\n\n Click Here To Copy Your Response")
                            .titleStyle(R.style.TextStyle, Gravity.BOTTOM| Gravity.BOTTOM)
                            .build()
                            .show();
                    dialogInterface.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();*/


        }
    }
}
