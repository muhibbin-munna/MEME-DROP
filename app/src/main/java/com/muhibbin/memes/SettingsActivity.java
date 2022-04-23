package com.muhibbin.memes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    Button rate,share;
    ImageView facebook,instagram;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFE23B"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Support Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(Color.parseColor("#FFE23B"), PorterDuff.Mode.SRC_ATOP);

        rate = findViewById(R.id.rateButton);
        share = findViewById(R.id.shareAppButton);
        facebook = findViewById(R.id.facebookImageButton);
        instagram = findViewById(R.id.instagramImageButton);

        rate.setOnClickListener(this);
        share.setOnClickListener(this);
        facebook.setOnClickListener(this);
        instagram.setOnClickListener(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rateButton:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + getPackageName())));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                break;
            case R.id.shareAppButton:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                    String shareMessage = "https://play.google.com/store/apps/details?id=" + getPackageName();
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Share Meme Drop: W3MG"));
                } catch(ActivityNotFoundException e) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                    String shareMessage = "market://details?id=" + getPackageName();
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Share Meme Drop: W3MG"));
                }
                break;
            case R.id.facebookImageButton:
                openFacebookPage();
                break;

            case R.id.instagramImageButton:
                openInstagram();
                break;
        }

    }

    private void openFacebookPage() {
        String pageUrl = "https://www.facebook.com/we3memegods";

        try {
            ApplicationInfo applicationInfo = this.getPackageManager().getApplicationInfo("com.facebook.katana", 0);

            if (applicationInfo.enabled) {
                int versionCode = this.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                String url;

                if (versionCode >= 3002850) {
                    url = "fb://page/109446487275131";
                } else {
                    url = "fb://page/we3memegods";
                }
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } else {
                throw new Exception("Facebook is disabled");
            }
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(pageUrl)));
        }
    }
    private void openInstagram() {
        Uri uri = Uri.parse("https://instagram.com/we3memegods");
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.instagram.android");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }
}
