package com.muhibbin.memes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muhibbin.memes.adapters.TemplateAdapter;
import com.muhibbin.memes.model.Upload;
import com.muhibbin.memes.photoEditorMainClasses.PhotoEditorActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TemplateActivity extends AppCompatActivity implements TemplateAdapter.OnItemClickListener{

    Toolbar toolbar;
    SwipeRefreshLayout swipeLayout;
    private RecyclerView mRecyclerView;
    private TemplateAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private LinearLayoutManager mlayoutManager;
    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Templates");

        swipeLayout = findViewById(R.id.new_swipe_container);
        mRecyclerView = findViewById(R.id.recyclerViewId);
        mlayoutManager = new GridLayoutManager(this,3);
        mRecyclerView.setLayoutManager(mlayoutManager);

        mProgressCircle = findViewById(R.id.progressbarId);
        mUploads = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("template");
        loadTemplate();

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTemplate();
                mAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            }
        });
    }

    private void loadTemplate() {
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }
                Collections.reverse(mUploads);
                mAdapter = new TemplateAdapter(TemplateActivity.this, mUploads);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(TemplateActivity.this);
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TemplateActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(TemplateActivity.this, PhotoEditorActivity.class);
        intent.putExtra("mImageUrl",mUploads.get(position).getmImageUrl());
        intent.putExtra("option",1);
        startActivity(intent);
    }
    @Override
    public void onWhatEverClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {

    }

}