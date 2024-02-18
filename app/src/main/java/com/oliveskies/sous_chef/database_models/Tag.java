package com.oliveskies.sous_chef.database_models;

import com.google.firebase.firestore.DocumentReference;

public class Tag {
    private String mName;
    private String mCategory;

    public Tag() {}
    public Tag(String name, String category)
    {
        mName = name;
        mCategory = category;
    }
    public String getName() { return mName; }
    public void setName(String name) { mName = name; }
    public String getCategory() { return mCategory; }
    public void setCategory(String category) { mCategory = category; }
}
