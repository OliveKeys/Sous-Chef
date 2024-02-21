package com.oliveskies.sous_chef.view_holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.oliveskies.sous_chef.R;


public class RecipeViewHolder extends RecyclerView.ViewHolder{
    public TextView RecipeTitle;
    public TextView RecipeTags;
    public ImageView RecipeImage;
    public CardView card;

    public View view;
    public View RecipeSeparator;
    public RecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        RecipeTitle = itemView.findViewById(R.id.recipe_card_title);
        RecipeTags = itemView.findViewById(R.id.recipe_card_tags);
        RecipeImage = itemView.findViewById(R.id.recipe_card_image);
        RecipeSeparator = itemView.findViewById(R.id.recipe_card_vertical_separator);
        card = itemView.findViewById(R.id.recipe_card_card);
        view = itemView;
    }
}
