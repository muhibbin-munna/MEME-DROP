package com.muhibbin.memes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Upload implements Serializable {

    private String uploadId,mImageUrl,category;
    private int likesCount;
    private long creationDate;
    private List<String> categoryList = new ArrayList<>();



    public Upload() {
    }

    public Upload(String uploadId, String mImageUrl, int likesCount, long creationDate, List<String> categoryList) {
        this.uploadId = uploadId;
        this.mImageUrl = mImageUrl;
        this.likesCount = likesCount;
        this.creationDate = creationDate;
        this.categoryList =categoryList;
    }
    public List<String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<String> categoryList) {
        this.categoryList = categoryList;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }
}
