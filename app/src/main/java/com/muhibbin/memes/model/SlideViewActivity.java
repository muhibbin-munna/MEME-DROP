package com.muhibbin.memes.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muhibbin.memes.R;
import com.muhibbin.memes.database.MyDatabaseHelper;
import com.muhibbin.memes.adapters.SlideImageAdapter;


import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SlideViewActivity extends AppCompatActivity {

    int pos,current_position;
    private ViewPager2 viewPager;
    MyDatabaseHelper myDatabaseHelper;

    TextView likesCount;
    ImageButton like;
    ImageButton dislike;
    ImageButton favorite;
    ImageButton download;
    ImageButton share;
    List<Upload> mUploads;

    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_view);

        like = findViewById(R.id.likebutton);
        likesCount = findViewById(R.id.likeTextView);
        dislike = findViewById(R.id.dislikebutton);
        favorite = findViewById(R.id.favbutton);
        download = findViewById(R.id.downloadbutton);
        share = findViewById(R.id.sharebutton);

        viewPager = findViewById(R.id.viewpager);
        myDatabaseHelper = new MyDatabaseHelper(this);
        Intent intent = getIntent();
        pos = intent.getIntExtra("pos", pos);
        mUploads = (List<Upload>) intent.getSerializableExtra("list");
        SlideImageAdapter mAdapter = new SlideImageAdapter(SlideViewActivity.this, mUploads);
        viewPager.setAdapter(mAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                current_position = position;
                String imageUrl = mUploads.get(position).getmImageUrl();
                likesCount.setText(String.valueOf(mUploads.get(position).getLikesCount()));
                myDatabaseHelper = new MyDatabaseHelper(SlideViewActivity.this);
                SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
                Cursor cursor = myDatabaseHelper.read_all_data(imageUrl);
                if (cursor.getCount() == 0) {
                    like.setBackgroundResource(R.drawable.outline_thumb_up_24);
                    dislike.setBackgroundResource(R.drawable.outline_thumb_down_24);
                    favorite.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                    if (!cursor.isClosed())
                        cursor.close();
                    sqLiteDatabase.close();
                } else {
                    try {
                        while (cursor.moveToNext()) {
                            int likestatus = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.LIKES));
                            int dislikestatus = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.DISLIKES));
                            int favoritestatus = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.FAVORITE));

                            if (likestatus == 1) {
                                like.setBackgroundResource(R.drawable.baseline_thumb_up_24);
                                cursor.close();
                            }
                            if (likestatus == 0) {
                                like.setBackgroundResource(R.drawable.outline_thumb_up_24);
                                cursor.close();
                            }
                            if (dislikestatus == 1) {
                                dislike.setBackgroundResource(R.drawable.baseline_thumb_down_24);
                                cursor.close();
                            }
                            if (dislikestatus == 0) {
                                dislike.setBackgroundResource(R.drawable.outline_thumb_down_24);
                                cursor.close();
                            }
                            if (favoritestatus == 1) {
                                favorite.setBackgroundResource(R.drawable.baseline_favorite_24);
                                cursor.close();
                            }
                            if (favoritestatus == 0) {
                                favorite.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                                cursor.close();
                            }

                        }

                    } finally {
                        if (!cursor.isClosed())
                            cursor.close();
                        sqLiteDatabase.close();
                    }

                }
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mDatabaseRef;
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/");
                String imageUrl = mUploads.get(current_position).getmImageUrl();
                SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
                Cursor cursor = myDatabaseHelper.read_all_data(imageUrl);
                if (cursor.getCount() == 0) {
                    myDatabaseHelper.insertData(imageUrl, 1, 0, 0);
                    int updatedlikes = mUploads.get(current_position).getLikesCount() + 1;
                    mUploads.get(current_position).setLikesCount(updatedlikes);
                    DatabaseReference likesNo = mDatabaseRef.child(mUploads.get(current_position).getUploadId()).child("likesCount");
                    likesNo.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int updatedlikes = dataSnapshot.getValue(Integer.class);
                            updatedlikes+=1;
                            DatabaseReference mDatabaseRef;
                            mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/");
                            mDatabaseRef.child(mUploads.get(current_position).getUploadId()).child("likesCount").setValue(updatedlikes);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    likesCount.setText(String.valueOf(updatedlikes));
                    like.setBackgroundResource(R.drawable.baseline_thumb_up_24);
                    pos=current_position;
                    cursor.close();
                    sqLiteDatabase.close();
                    DatabaseReference.goOffline();
                    mDatabaseRef.onDisconnect();
                } else {
                    try {
                        while (cursor.moveToNext()) {
                            int likestatus = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.LIKES));
                            int dislikestatus = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.DISLIKES));
                            int favoritestatus = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.FAVORITE));
                            if (likestatus == 1) {
                                myDatabaseHelper.updateData(imageUrl, 0, dislikestatus, favoritestatus);
                                int updatedlikes = mUploads.get(current_position).getLikesCount() - 1;
                                mUploads.get(current_position).setLikesCount(updatedlikes);
                                DatabaseReference likesNo = mDatabaseRef.child(mUploads.get(current_position).getUploadId()).child("likesCount");
                                likesNo.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int updatedlikes = dataSnapshot.getValue(Integer.class);
                                        updatedlikes -= 1;
                                        DatabaseReference mDatabaseRef;
                                        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/");
                                        mDatabaseRef.child(mUploads.get(current_position).getUploadId()).child("likesCount").setValue(updatedlikes);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                likesCount.setText(String.valueOf(updatedlikes));
                                like.setBackgroundResource(R.drawable.outline_thumb_up_24);
                                cursor.close();
                                DatabaseReference.goOffline();
                                mDatabaseRef.onDisconnect();
                            } else {
                                if (dislikestatus == 0) {
                                    myDatabaseHelper.updateData(imageUrl, 1, dislikestatus, favoritestatus);
                                    int updatedlikes = mUploads.get(current_position).getLikesCount() + 1;
                                    mUploads.get(current_position).setLikesCount(updatedlikes);
                                    likesCount.setText(String.valueOf(updatedlikes));
                                    like.setBackgroundResource(R.drawable.baseline_thumb_up_24);
                                    DatabaseReference likesNo = mDatabaseRef.child(mUploads.get(current_position).getUploadId()).child("likesCount");
                                    likesNo.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            int updatedlikes = dataSnapshot.getValue(Integer.class);
                                            updatedlikes += 1;
                                            DatabaseReference mDatabaseRef;
                                            mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/");
                                            mDatabaseRef.child(mUploads.get(current_position).getUploadId()).child("likesCount").setValue(updatedlikes);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    cursor.close();
                                    DatabaseReference.goOffline();
                                    mDatabaseRef.onDisconnect();
                                } else {
                                    myDatabaseHelper.updateData(imageUrl, 1, 0, favoritestatus);
                                    int updatedlikes = mUploads.get(current_position).getLikesCount() + 2;
                                    mUploads.get(current_position).setLikesCount(updatedlikes);
                                    DatabaseReference likesNo = mDatabaseRef.child(mUploads.get(current_position).getUploadId()).child("likesCount");
                                    likesCount.setText(String.valueOf(updatedlikes));
                                    like.setBackgroundResource(R.drawable.baseline_thumb_up_24);
                                    dislike.setBackgroundResource(R.drawable.outline_thumb_down_24);
                                    likesNo.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            int updatedlikes = dataSnapshot.getValue(Integer.class);
                                            updatedlikes += 2;
                                            DatabaseReference mDatabaseRef;
                                            mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/");
                                            mDatabaseRef.child(mUploads.get(current_position).getUploadId()).child("likesCount").setValue(updatedlikes);
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    cursor.close();
                                    DatabaseReference.goOffline();
                                    mDatabaseRef.onDisconnect();
                                }
                            }
                        }
                    } finally {
                        if (!cursor.isClosed())
                            cursor.close();
                        sqLiteDatabase.close();
                        pos=current_position;
                        DatabaseReference.goOffline();
                        mDatabaseRef.onDisconnect();
                    }
                }
            }
        });


        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageUrl = mUploads.get(current_position).getmImageUrl();
                DatabaseReference mDatabaseRef;
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
                SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
                Cursor cursor = myDatabaseHelper.read_all_data(imageUrl);
                if (cursor.getCount() == 0) {
                    myDatabaseHelper.insertData(imageUrl, 0, 1, 0);
                    int updateddislikes = mUploads.get(current_position).getLikesCount()-1;
                    mUploads.get(current_position).setLikesCount(updateddislikes);
                    likesCount.setText(String.valueOf(updateddislikes));
                    dislike.setBackgroundResource(R.drawable.baseline_thumb_down_24);
                    DatabaseReference likesNo = mDatabaseRef.child(mUploads.get(current_position).getUploadId()).child("likesCount");
                    likesNo.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int updatedlikes = dataSnapshot.getValue(Integer.class);
                            updatedlikes -= 1;
                            DatabaseReference mDatabaseRef;
                            mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/");
                            mDatabaseRef.child(mUploads.get(current_position).getUploadId()).child("likesCount").setValue(updatedlikes);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    pos = current_position;
                    cursor.close();
                    sqLiteDatabase.close();
                    DatabaseReference.goOffline();
                    mDatabaseRef.onDisconnect();
                } else {
                    try {
                        while (cursor.moveToNext()) {
                            int likestatus = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.LIKES));
                            int dislikestatus = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.DISLIKES));
                            int favoritestatus = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.FAVORITE));

                            if (dislikestatus == 1) {
                                myDatabaseHelper.updateData(imageUrl, likestatus, 0, favoritestatus);
                                int updateddislikes = mUploads.get(current_position).getLikesCount() + 1;
                                mUploads.get(current_position).setLikesCount(updateddislikes);
                                DatabaseReference likesNo = mDatabaseRef.child(mUploads.get(current_position).getUploadId()).child("likesCount");
                                likesNo.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int updatedlikes = dataSnapshot.getValue(Integer.class);
                                        updatedlikes += 1;
                                        DatabaseReference mDatabaseRef;
                                        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/");
                                        mDatabaseRef.child(mUploads.get(current_position).getUploadId()).child("likesCount").setValue(updatedlikes);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                dislike.setBackgroundResource(R.drawable.outline_thumb_down_24);
                                likesCount.setText(String.valueOf(updateddislikes));
                                cursor.close();
                                DatabaseReference.goOffline();
                                mDatabaseRef.onDisconnect();
                            } else {
                                if (likestatus == 0) {
                                    myDatabaseHelper.updateData(imageUrl, likestatus, 1, favoritestatus);
                                    int updateddislikes = mUploads.get(current_position).getLikesCount() - 1;
                                    mUploads.get(current_position).setLikesCount(updateddislikes);
                                    DatabaseReference likesNo = mDatabaseRef.child(mUploads.get(current_position).getUploadId()).child("likesCount");
                                    likesNo.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            int updatedlikes = dataSnapshot.getValue(Integer.class);
                                            updatedlikes -= 1;
                                            DatabaseReference mDatabaseRef;
                                            mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/");
                                            mDatabaseRef.child(mUploads.get(current_position).getUploadId()).child("likesCount").setValue(updatedlikes);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    dislike.setBackgroundResource(R.drawable.baseline_thumb_down_24);
                                    likesCount.setText(String.valueOf(updateddislikes));
                                    cursor.close();
                                    DatabaseReference.goOffline();
                                    mDatabaseRef.onDisconnect();
                                } else {
                                    myDatabaseHelper.updateData(imageUrl, 0, 1, favoritestatus);
                                    int updateddislikes = mUploads.get(current_position).getLikesCount() - 2;
                                    mUploads.get(current_position).setLikesCount(updateddislikes);
                                    DatabaseReference likesNo = mDatabaseRef.child(mUploads.get(current_position).getUploadId()).child("likesCount");
                                    likesNo.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            int updatedlikes = dataSnapshot.getValue(Integer.class);
                                            updatedlikes -= 2;
                                            DatabaseReference mDatabaseRef;
                                            mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/");
                                            mDatabaseRef.child(mUploads.get(current_position).getUploadId()).child("likesCount").setValue(updatedlikes);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    mUploads.get(current_position).setLikesCount(updateddislikes);
                                    likesCount.setText(String.valueOf(updateddislikes));
                                    dislike.setBackgroundResource(R.drawable.baseline_thumb_down_24);
                                    like.setBackgroundResource(R.drawable.outline_thumb_up_24);
                                    cursor.close();
                                    DatabaseReference.goOffline();
                                    mDatabaseRef.onDisconnect();
                                }
                            }
                        }
                    } finally {
                        if (!cursor.isClosed())
                            cursor.close();
                        sqLiteDatabase.close();
                        pos = current_position;
                        DatabaseReference.goOffline();
                        mDatabaseRef.onDisconnect();
                    }
                }
            }
        });

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageUrl = mUploads.get(current_position).getmImageUrl();
                SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
                Cursor cursor = myDatabaseHelper.read_all_data(imageUrl);
                if (cursor.getCount() == 0) {
                    myDatabaseHelper.insertData(imageUrl, 0, 0, 1);
                    favorite.setBackgroundResource(R.drawable.baseline_favorite_24);
                    pos = current_position;
                    cursor.close();
                    sqLiteDatabase.close();
                } else {
                    try {
                        while (cursor.moveToNext()) {
                            int likestatus = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.LIKES));
                            int dislikestatus = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.DISLIKES));
                            int favoritestatus = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.FAVORITE));
                            if (favoritestatus == 1) {
                                myDatabaseHelper.updateData(imageUrl, likestatus, dislikestatus, 0);
                                favorite.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                                cursor.close();
                            } else {
                                myDatabaseHelper.updateData(imageUrl, likestatus, dislikestatus, 1);
                                favorite.setBackgroundResource(R.drawable.baseline_favorite_24);
                                cursor.close();
                            }
                        }
                    } finally {
                        pos = current_position;
                        if (!cursor.isClosed())
                            cursor.close();
                        sqLiteDatabase.close();
                    }
                }
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()){

                }
                else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        requestPermissions( new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE},111);
                    }
                }
                Toast.makeText(SlideViewActivity.this, "downloading", Toast.LENGTH_SHORT).show();
                Glide.with(SlideViewActivity.this).asBitmap().load(mUploads.get(current_position).getmImageUrl()).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            String root = Environment.getExternalStorageDirectory().toString();
                            File myDir = new File(root + "/MEME DROPS");
                            if (!myDir.exists()) {
                                myDir.mkdirs();
                            }
                            String name = "memes_"+System.currentTimeMillis()%10000+ ".JPEG";
                            myDir = new File(myDir, name);
                            FileOutputStream out = new FileOutputStream(myDir);
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            Toast.makeText(SlideViewActivity.this, "downloaded", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            intent.setData(Uri.fromFile(myDir));
                            SlideViewActivity.this.sendBroadcast(intent);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            Toast.makeText(SlideViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
                pos = current_position;
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(SlideViewActivity.this).asBitmap().load(mUploads.get(current_position).getmImageUrl()).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {

                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());

                            File file =new File (SlideViewActivity.this.getExternalCacheDir(),"meme.JPEG");
                            FileOutputStream out = new FileOutputStream(file);
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                            file.setReadable(true,false);
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                            intent.setType("image/JPEG");
                            SlideViewActivity.this.startActivity(Intent.createChooser(intent,"Share meme via: "));
                        } catch (Exception e) {
                            Toast.makeText(SlideViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }

    private boolean checkPermission() {
        int write = ContextCompat.checkSelfPermission(SlideViewActivity.this,WRITE_EXTERNAL_STORAGE);
        int read =  ContextCompat.checkSelfPermission(SlideViewActivity.this,READ_EXTERNAL_STORAGE);
        return write== PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(pos, false);
            }
        }, 10);

    }
}