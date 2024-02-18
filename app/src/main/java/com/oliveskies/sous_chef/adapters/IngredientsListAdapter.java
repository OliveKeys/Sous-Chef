package com.oliveskies.sous_chef.adapters;

import static android.view.View.GONE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.oliveskies.sous_chef.R;
import com.oliveskies.sous_chef.database_models.Ingredient;

import java.util.List;

public class IngredientsListAdapter extends ArrayAdapter<Ingredient> {

    public IngredientsListAdapter(Context context, List<Ingredient> ingredients)
    {
        super(context, 0, ingredients);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Ingredient ingredient = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.recipe_ingredient_item, parent, false);
        }
        TextView ingredientName = (TextView) convertView.findViewById(R.id.recipe_ingredient_item_name);
        TextView ingredientQuantity = (TextView) convertView.findViewById(R.id.recipe_ingredient_item_quantity);
        TextView ingredientNotes = (TextView) convertView.findViewById(R.id.recipe_ingredient_item_notes);
        // Populate the data into the template view using the data object
        ingredientName.setText(ingredient.getName());
        ingredientQuantity.setText(ingredient.getQuantity());
        if(!ingredient.getNotes().isEmpty())
            ingredientNotes.setText(ingredient.getNotes());
        else
            ingredientNotes.setVisibility(GONE);
        // Return the completed view to render on screen
        return convertView;
    }
}
