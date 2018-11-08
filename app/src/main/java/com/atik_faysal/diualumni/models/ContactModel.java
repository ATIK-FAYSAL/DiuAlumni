package com.atik_faysal.diualumni.models;

import ir.mirrajabi.searchdialog.core.Searchable;


/**
 * Created by MADNESS on 5/16/2017.
 */

public class ContactModel implements Searchable {
    private String mName;
    private String mImageUrl;
    private String stdId;

    public ContactModel(String name, String stdId,String imageUrl) {
        mName = name;
        mImageUrl = imageUrl;
        this.stdId = stdId;
    }

    public String getStdId() {
        return stdId;
    }

    public void setStdId(String stdId) {
        this.stdId = stdId;
    }

    @Override
    public String getTitle() {
        return mName;
    }

    public String getName() {
        return mName;
    }

    public ContactModel setName(String name) {
        mName = name;
        return this;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public ContactModel setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
        return this;
    }
}
