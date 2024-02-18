package com.oliveskies.sous_chef.database_models;

public class TagCategory {
    private String mName;
    public TagCategory() {}

    public TagCategory(String name)
    {
        mName = name;
    }

    public String getName() { return mName; }

    public void setName(String name) { mName = name; }
}
