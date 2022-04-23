package com.muhibbin.memes.fragments;

import android.content.Intent;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muhibbin.memes.MainActivity;
import com.muhibbin.memes.R;
import com.muhibbin.memes.adapters.ImageAdapter;
import com.muhibbin.memes.model.SlideViewActivity;
import com.muhibbin.memes.model.Upload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

    public class Fragment_new extends Fragment implements ImageAdapter.OnItemClickListener {

    SwipeRefreshLayout swipeLayout;
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private GridLayoutManager mlayoutManager;
    private List<Upload> mUploads;
    private String category;
    private Query query;
    private TextView newText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_layout, container, false);

        swipeLayout = view.findViewById(R.id.new_swipe_container);
        mRecyclerView = view.findViewById(R.id.recyclerViewId);
        mlayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(mlayoutManager);
        newText = view.findViewById(R.id.newText);

        mProgressCircle = view.findViewById(R.id.progressbarId);
        mUploads = new ArrayList<>();

        MainActivity activity = (MainActivity) getActivity();
        category = activity.getCategoryName();

        load();

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
                mAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void load() {
        if (category.equals("All")) {
            loadNew();
        } else {
//            query = FirebaseDatabase.getInstance().getReference("uploads").orderByChild("categoryList").equalTo(category);
            loadQuery();
        }
    }

    private void loadQuery() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    List<String> categoryList= upload.getCategoryList();
                    if(categoryList.contains(category))
                    {
                        mUploads.add(upload);
                    }
                }
                Collections.reverse(mUploads);
                if(mUploads.size()==0){
                    newText.setText("No Memes Available in this category");
                }
                else {
                    newText.setVisibility(View.GONE);
                    mAdapter = new ImageAdapter(getContext(), mUploads);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(Fragment_new.this);
                }
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void loadNew() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }
                Collections.reverse(mUploads);
                mAdapter = new ImageAdapter(getContext(), mUploads);

                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(Fragment_new.this);
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
        intent.putExtra("pos", position);
        intent.putExtra("list", (Serializable) mUploads);
        startActivity(intent);
    }

    @Override
    public void onWhatEverClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {

    }
}
