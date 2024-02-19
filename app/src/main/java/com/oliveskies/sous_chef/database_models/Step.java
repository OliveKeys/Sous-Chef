package com.oliveskies.sous_chef.database_models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.List;

public class Step {
    public List<Ingredient> mIngredients;
    private String mInstructions;
    private double mTimerDuration;

    public Step() {
        mIngredients = new ArrayList<>();
    }
    public Step(String instructions, double timerDuration, List<Ingredient> ingredients)
    {
        mIngredients = ingredients;
        mInstructions = instructions;
        mTimerDuration = timerDuration;
    }

    public Step(DataSnapshot snapshot)
    {
        GenericTypeIndicator<String> stringIndicator = new GenericTypeIndicator<String>() {};
        GenericTypeIndicator<Double> doubleIndicator = new GenericTypeIndicator<Double>() {};
        mIngredients = new ArrayList<>();

        for(DataSnapshot ds : snapshot.child("ingredients").getChildren())
            mIngredients.add(new Ingredient(ds));
        mInstructions = snapshot.child("instructions").getValue(stringIndicator);
        mTimerDuration = snapshot.child("timer_duration").getValue(doubleIndicator);
    }

    public String getInstructions() { return mInstructions; }
    public void setInstructions(String instructions) { mInstructions = instructions; }
    public double getTimerDuration() { return mTimerDuration; }
    public void setTimerDuration(double timer) { mTimerDuration = timer; }
    public int getIngredientsCount() { return mIngredients.size(); }
    public Ingredient getNthIngredient(int i) { return mIngredients.get(i); }
    public void setNthIngredient(int i, Ingredient ingredient) { mIngredients.set(i, ingredient); }
    public List<Ingredient> getIngredients() { return mIngredients; }
    public void setIngredients(List<Ingredient> ingredients) { mIngredients = ingredients; }
    public void addEmptyIngredient() { mIngredients.add(new Ingredient()); }
}
