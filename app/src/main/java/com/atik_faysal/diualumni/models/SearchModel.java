package com.atik_faysal.diualumni.models;

import ir.mirrajabi.searchdialog.core.Searchable;

public class SearchModel implements Searchable {
    private String stdId,name,imageUrl;

    public SearchModel(String stdId,String name,String imageUrl)
    {
        this.stdId = stdId;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public void setStdId(String stdId) {
        this.stdId = stdId;
    }
    public String getStdId() {
        return stdId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String getTitle() {
        return name;
    }
}
