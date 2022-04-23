package com.muhibbin.memes.adapters;

import android.content.Context;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;

import com.bumptech.glide.request.target.Target;

import com.github.chrisbanes.photoview.PhotoView;
import com.muhibbin.memes.R;
import com.muhibbin.memes.model.Upload;

import java.util.List;


public class SlideImageAdapter extends RecyclerView.Adapter<SlideImageAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Upload> mUploads;
    public SlideImageAdapter(Context mContext, List<Upload> mUploads) {
        this.mContext = mContext;
        this.mUploads = mUploads;
    }

    @NonNull
    @Override
    public SlideImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.slide, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SlideImageAdapter.ImageViewHolder holder, final int position) {
        Upload uploadCurrent = mUploads.get(position);
        Glide.with(mContext)
                .load(uploadCurrent.getmImageUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.mProgressCircle.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.mProgressCircle.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {

        PhotoView imageView;
        ProgressBar mProgressCircle = itemView.findViewById(R.id.slideprogressBar);
        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slideImage);
        }

    }

}
