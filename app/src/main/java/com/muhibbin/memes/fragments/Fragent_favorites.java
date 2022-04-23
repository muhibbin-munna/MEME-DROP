package com.muhibbin.memes.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muhibbin.memes.R;
import com.muhibbin.memes.adapters.ImageAdapter;
import com.muhibbin.memes.database.MyDatabaseHelper;
import com.muhibbin.memes.model.SlideViewActivity;
import com.muhibbin.memes.model.Upload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Fragent_favorites extends Fragment implements ImageAdapter.OnItemClickListener {

    SwipeRefreshLayout swipeLayout;
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private LinearLayoutManager mlayoutManager;
    private List<Upload> mUploads;
    private TextView favText;

    MyDatabaseHelper myDatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=  inflater.inflate(R.layout.fragment_favorites_layout,container,false);

        swipeLayout = view.findViewById(R.id.fav_swipe_container);


        getFav(view);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFav(view);
                mAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            }
        });

        return view;
    }

    public void getFav(View view){
        mRecyclerView = view.findViewById(R.id.favrecyclerViewId);
        mlayoutManager = new GridLayoutManager(getContext(),3);
        mRecyclerView.setLayoutManager(mlayoutManager);
        mProgressCircle = view.findViewById(R.id.favprogressbarId);
        favText = view.findViewById(R.id.favText);
        myDatabaseHelper = new MyDatabaseHelper(getContext());
        mUploads = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
                SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Cursor cursor = myDatabaseHelper.read_all_favorites();
                    Upload upload = postSnapshot.getValue(Upload.class);
                    if (cursor.getCount() == 0) {
                        favText.setText("No favorites added yet!");
                        if (!cursor.isClosed())
                            cursor.close();
                        sqLiteDatabase.close();
                    } else {
                        favText.setVisibility(View.GONE);
                        try {
                            while (cursor.moveToNext()) {
                                if ((cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.IMAGE_URL))).equals (upload.getmImageUrl())) {
                                    mUploads.add(upload);
                                }
                            }
                        } finally {
                            if (!cursor.isClosed())
                                cursor.close();
                            sqLiteDatabase.close();
                        }
                    }
                }
                mAdapter = new ImageAdapter(getContext(), mUploads);

                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(Fragent_favorites.this);
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);

            }
        });

    }
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), SlideViewActivity.class);
        intent.putExtra("pos",position);
        intent.putExtra("list",(Serializable) mUploads);
        startActivity(intent);
    }

    @Override
    public void onWhatEverClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {

    }

}
