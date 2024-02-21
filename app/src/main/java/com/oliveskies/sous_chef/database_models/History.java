package com.oliveskies.sous_chef.database_models;

public class History {
    public Recipe recipe;

    public int step;
    public String date;
    public int index;
    public History() {}
    public History(Recipe recipe, int step, String date, int index)
    {
        this.recipe = recipe;
        this.step = step;
        this.date = date;
        this.index = index;
    }
}
