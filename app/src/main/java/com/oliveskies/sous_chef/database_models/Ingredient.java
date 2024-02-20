package com.oliveskies.sous_chef.database_models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

public class Ingredient {
    private String mName = "";
    private String mQuantity = "";
    private String mNotes = "";

    public Ingredient() {}
    public Ingredient(String name, String quantity, String notes)
    {
        mName = name;
        mQuantity = quantity;
        mNotes = notes;
    }

    public Ingredient(DataSnapshot ds)
    {
        GenericTypeIndicator<String> stringIndicator = new GenericTypeIndicator<String>() {};
        mName = ds.child("name").getValue(stringIndicator);
        mQuantity = ds.child("quantity").getValue(stringIndicator);
        mNotes = ds.child("notes").getValue(stringIndicator);

    }

    public String getName() { return mName; }
    public void setName(String name) { mName = name; }
    public String getQuantity() { return mQuantity; }
    public void setQuantity(String quantity) { mQuantity = quantity; }
    public String getNotes() { return mNotes; }
    public void setNotes(String notes) { mNotes = notes; }
}
