package com.oliveskies.sous_chef;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oliveskies.sous_chef.R;
import com.oliveskies.sous_chef.adapters.IngredientsListAdapter;
import com.oliveskies.sous_chef.database_models.Ingredient;
import com.oliveskies.sous_chef.database_models.Recipe;

import java.util.List;

public class RecipeFragment extends Fragment {
    Recipe recipe;
    ImageView recipeImage;
    TextView recipeTitle;
    TextView recipeDescription;
    TextView recipePrepTime;
    TextView recipeCookTime;
    TextView recipeTotalTime;
    TextView recipeServings;
    ListView recipeIngredientsList;
    ListView recipeStepsList;
    ImageView recipeBackButton;
    ImageView recipeStartButton;
    TextView recipeIngredientsLabel;
    TextView recipeStepsLabel;
    public RecipeFragment(Recipe model) {
        recipe = model;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        recipeImage = view.findViewById(R.id.recipe_page_image);
        recipeTitle = view.findViewById(R.id.recipe_page_title);
        recipeDescription = view.findViewById(R.id.recipe_page_description);
        recipePrepTime = view.findViewById(R.id.recipe_page_prep_time);
        recipeCookTime = view.findViewById(R.id.recipe_page_cook_time);
        recipeTotalTime = view.findViewById(R.id.recipe_page_total_time);
        recipeServings = view.findViewById(R.id.recipe_page_servings);
        recipeIngredientsList = view.findViewById(R.id.recipe_page_ingredients_listview);
        recipeStepsList = view.findViewById(R.id.recipe_page_steps_listview);
        recipeBackButton = view.findViewById(R.id.recipe_page_back_button);
        recipeStartButton = view.findViewById(R.id.recipe_page_start_button);
        recipeIngredientsLabel = view.findViewById(R.id.recipe_page_ingredients_label);
        recipeStepsLabel = view.findViewById(R.id.recipe_page_steps_label);

        recipeTitle.setText(recipe.getName());
        recipeDescription.setText(recipe.getDescription());
        recipePrepTime.setText(recipe.getPrepTime());
        recipeCookTime.setText(recipe.getCookingTime());
        recipeTotalTime.setText(recipe.getTotalCookingTime());
        recipeServings.setText(recipe.getServings());

        recipeIngredientsLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recipeIngredientsList.getVisibility() == View.VISIBLE)
                   recipeIngredientsList.setVisibility(View.GONE);
                else
                    recipeIngredientsList.setVisibility(View.VISIBLE);
            }
        });

        recipeBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        recipeStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Starting recipe", Toast.LENGTH_SHORT).show();
            }
        });

        List<Ingredient> ingredients = recipe.getIngredients();
        IngredientsListAdapter ingredientsAdapter = new IngredientsListAdapter(getContext(), ingredients);
        recipeIngredientsList.setAdapter(ingredientsAdapter);

        return view;
    }
}