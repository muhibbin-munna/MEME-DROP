package com.muhibbin.memes.fragments;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muhibbin.memes.MainActivity;
import com.muhibbin.memes.R;
import com.muhibbin.memes.model.Upload;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Fragment_gamification extends Fragment implements View.OnClickListener {
    ImageView image1, image2;
    Button selectCategoryButton;
    LottieAnimationView upvoteAnimation1, upvoteAnimation2;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;
    private String categoryName;
    ProgressBar progressBar1, progressBar2;
    private InterstitialAd mInterstitialAd;
    private int count = 0;
    RequestBuilder<Drawable> imageLoad1;
    RequestBuilder<Drawable> imageLoad2;
    int randomIndex1,randomIndex2;
    private ProgressDialog mProgressDialog;
//    SwipeRefreshLayout swipeLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gamification, container, false);

        image1 = view.findViewById(R.id.gamificationImage1);
        image2 = view.findViewById(R.id.gamificationImage2);
        selectCategoryButton = view.findViewById(R.id.selectCategoryButtonG);
        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-6850923031141904/1427369450");
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");//Test Ads
//        swipeLayout = view.findViewById(R.id.gamification_swipe_container);
        upvoteAnimation1 = view.findViewById(R.id.upvoteAnimation1);
        upvoteAnimation2 = view.findViewById(R.id.upvoteAnimation2);
        progressBar1 = view.findViewById(R.id.progressBar1);
        progressBar2 = view.findViewById(R.id.progressBar2);
//        showLoading("Please Wait...");
        categoryName = "All";
        loadmUploads();

//        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                loadmUploads();
//                swipeLayout.setRefreshing(false);
//            }
//        });
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        selectCategoryButton.setOnClickListener(this);
        return view;
    }
    private void setImages(){
        if(mUploads.size()==0)
        {
            image1.setImageResource(R.drawable.no_image_icon);
            image2.setImageResource(R.drawable.no_image_icon);
            progressBar1.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
            image1.setEnabled(false);
            image2.setEnabled(false);
        }
        else {

            imageLoad1.placeholder(new ColorDrawable(Color.BLACK)).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    progressBar1.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progressBar1.setVisibility(View.GONE);
                    image1.setEnabled(true);

                    return false;
                }
            }).transition(DrawableTransitionOptions.withCrossFade(1000)).into(image1);
            imageLoad2.placeholder(new ColorDrawable(Color.BLACK)).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    progressBar2.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progressBar2.setVisibility(View.GONE);
                    image2.setEnabled(true);
                    return false;
                }
            }).transition(DrawableTransitionOptions.withCrossFade(1000)).into(image2);
        }
    }

    private void loadImages() {
        if (mUploads.size() != 0) {
            count++;
            if (count % 10 == 0) {
                count = 0;
                showAd();
            }
            Random r1 = new Random();
            randomIndex1 = r1.nextInt(mUploads.size());
            Random r2 = new Random();
            randomIndex2 = r2.nextInt(mUploads.size());
            imageLoad1 = Glide.with(this).load(mUploads.get(randomIndex1).getmImageUrl());
            imageLoad2 = Glide.with(this).load(mUploads.get(randomIndex2).getmImageUrl());

        }
        else {
            imageLoad1 = Glide.with(this).load(R.drawable.no_image_icon);
            imageLoad2 = Glide.with(this).load(R.drawable.no_image_icon);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gamificationImage1:
                image1.setEnabled(false);
                image2.setEnabled(false);
//                Toast.makeText(this, ""+mUploads.get(randomIndex1).getLikesCount(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(this, ""+mUploads.get(randomIndex1).getUploadId(), Toast.LENGTH_SHORT).show();
//                mDatabaseRef.child(mUploads.get(randomIndex1).getUploadId()).child("likesCount").setValue((mUploads.get(randomIndex1).getLikesCount())+1);
//                Toast.makeText(this, ""+mDatabaseRef.child(mUploads.get(randomIndex1).getUploadId()).child("likesCount"), Toast.LENGTH_SHORT).show();
                DatabaseReference likesNo1 = mDatabaseRef.child(mUploads.get(randomIndex1).getUploadId()).child("likesCount");
                likesNo1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int updatedLikes1 = dataSnapshot.getValue(Integer.class);
                        updatedLikes1+=1;
                        DatabaseReference mDatabaseRef;
                        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/");
                        mDatabaseRef.child(mUploads.get(randomIndex1).getUploadId()).child("likesCount").setValue(updatedLikes1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                loadImages();
                upvoteAnimation1.setVisibility(View.VISIBLE);
                upvoteAnimation1.playAnimation();
                upvoteAnimation1.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        try {
                            progressBar1.setVisibility(View.VISIBLE);
                            progressBar2.setVisibility(View.VISIBLE);
                            setImages();
                            upvoteAnimation1.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                break;
            case R.id.gamificationImage2:
                image1.setEnabled(false);
                image2.setEnabled(false);
//                Toast.makeText(this, ""+mUploads.get(randomIndex2).getUploadId(), Toast.LENGTH_SHORT).show();
//                mDatabaseRef.child(mUploads.get(randomIndex2).getUploadId()).child("likesCount").setValue((mUploads.get(randomIndex2).getLikesCount())+1);
//                Toast.makeText(this, ""+mDatabaseRef.child(mUploads.get(randomIndex2).getUploadId()).child("likesCount")., Toast.LENGTH_SHORT).show();
                DatabaseReference likesNo2 = mDatabaseRef.child(mUploads.get(randomIndex2).getUploadId()).child("likesCount");
                likesNo2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int updatedLikes2 = dataSnapshot.getValue(Integer.class);
                        updatedLikes2+=1;
                        DatabaseReference mDatabaseRef;
                        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/");
                        mDatabaseRef.child(mUploads.get(randomIndex2).getUploadId()).child("likesCount").setValue(updatedLikes2);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                loadImages();
                upvoteAnimation2.setVisibility(View.VISIBLE);
                upvoteAnimation2.playAnimation();
                upvoteAnimation2.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        try {
                            progressBar1.setVisibility(View.VISIBLE);
                            progressBar2.setVisibility(View.VISIBLE);
                            setImages();
                            upvoteAnimation2.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                break;
            case R.id.selectCategoryButtonG:
                loadCategoryList();
        }
    }
    public void showAd() {
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {
                super.onAdClosed();

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

        });
    }

    private void loadCategoryList() {
        Query categoryDatabaseRef;
        categoryDatabaseRef = FirebaseDatabase.getInstance().getReference("category").orderByValue();
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        builderSingle.setTitle("Filter By Category");

        final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getContext(), R.layout.category_layout);
        categoryDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryAdapter.clear();
                categoryAdapter.add("All");
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String areaName = postSnapshot.getValue(String.class);
                    categoryAdapter.add(areaName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(categoryAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                showLoading("Please Wait...");
                categoryName = categoryAdapter.getItem(which);
//                sectionsPagerAdapter.setmCategoryName(categoryName);
                if(which == 0){
                    loadmUploads();
                }
                else {
                    loadmUploadsWithCategory();
                }

            }
        });
        builderSingle.show();
    }

    private void loadmUploads() {
        progressBar1.setVisibility(View.VISIBLE);
        progressBar2.setVisibility(View.VISIBLE);
        mUploads = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }
                loadImages();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setImages();
                    }
                },50);
//                hideLoading();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                hideLoading();
            }
        });
    }

    private void loadmUploadsWithCategory() {
        progressBar1.setVisibility(View.VISIBLE);
        progressBar2.setVisibility(View.VISIBLE);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    List<String> categoryList = upload.getCategoryList();
                    if (categoryList.contains(categoryName)) {
                        mUploads.add(upload);
                    }
                }
                loadImages();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setImages();
                    }
                },50);
//                hideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
//    protected void showLoading(@NonNull String message) {
//        mProgressDialog = new ProgressDialog(getContext(), R.style.MyDialogTheme);
//        mProgressDialog.setMessage(message);
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
//    }
//
//    protected void hideLoading() {
//        if (mProgressDialog != null) {
//            mProgressDialog.dismiss();
//        }
//    }
}