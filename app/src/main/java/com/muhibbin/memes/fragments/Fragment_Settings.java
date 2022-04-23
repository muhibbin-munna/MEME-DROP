package com.muhibbin.memes.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.muhibbin.memes.R;

public class Fragment_Settings extends Fragment implements View.OnClickListener {

    Button rate, share;
    ImageView facebook, instagram;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        rate = view.findViewById(R.id.rateButton);
        share = view.findViewById(R.id.shareAppButton);
        facebook = view.findViewById(R.id.facebookImageButton);
        instagram = view.findViewById(R.id.instagramImageButton);
        rate.setOnClickListener(this);
        share.setOnClickListener(this);
        facebook.setOnClickListener(this);
        instagram.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rateButton:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + getActivity().getPackageName())));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                }
                break;
            case R.id.shareAppButton:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                    String shareMessage = "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName();
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Share Meme Drop: W3MG"));
                } catch (ActivityNotFoundException e) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                    String shareMessage = "market://details?id=" + getActivity().getPackageName();
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
            ApplicationInfo applicationInfo = getActivity().getPackageManager().getApplicationInfo("com.facebook.katana", 0);

            if (applicationInfo.enabled) {
                int versionCode = getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
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