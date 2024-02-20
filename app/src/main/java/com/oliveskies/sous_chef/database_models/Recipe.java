package com.oliveskies.sous_chef.database_models;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Recipe {
    private String mName = "";
    private String mCookingTime = "";
    private List<Ingredient> mIngredients = new ArrayList<>();
    private String mPrepTime = "";
    private String mServings = "";
    private List<String> mTags = new ArrayList<>();
    private String mTotalCookingTime = "";
    private List<Step> mSteps = new ArrayList<>();
    private String mDescription = "";
    public Recipe() {}

    public Recipe(String name, String cookingTime, String prepTime, String totalCookingTime, String servings, List<Ingredient> ingredients, String description, List<String> tags, List<Step> steps)
    {
        mName = name;
        mCookingTime = cookingTime;
        mServings = servings;
        mPrepTime = prepTime;
        mTotalCookingTime = totalCookingTime;
        mIngredients = ingredients;
        mTags = tags;
        mDescription = description;
        mSteps = steps;
    }

    public String getName() { return mName; }
    public String getCookingTime() { return mCookingTime; }
    public String getPrepTime() { return mPrepTime; }
    public String getTotalCookingTime() { return mTotalCookingTime; }
    public String getDescription() { return mDescription; }
    public String getServings() { return mServings; }
    public int getIngredientCount() { return mIngredients.size(); }
    public int getTagsCount() { return mTags.size(); }
    public int getStepsCount() { return mSteps.size(); }
    public Ingredient getNthIngredient(int i) { return mIngredients.get(i); }
    public String getNthTag(int i) { return mTags.get(i); }
    public Step getNthStep(int i) { return mSteps.get(i); }
    public List<Ingredient> getIngredients() { return mIngredients; }
    public List<Step> getSteps() { return mSteps; }
}
