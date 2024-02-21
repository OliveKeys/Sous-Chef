package com.oliveskies.sous_chef.fragments;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oliveskies.sous_chef.R;
import com.oliveskies.sous_chef.Utility;
import com.oliveskies.sous_chef.adapters.IngredientsListAdapter;
import com.oliveskies.sous_chef.adapters.StepListAdapter;
import com.oliveskies.sous_chef.database_models.Ingredient;
import com.oliveskies.sous_chef.database_models.Recipe;
import com.oliveskies.sous_chef.database_models.Step;

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

    ConstraintLayout imageContainer;
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
        imageContainer = view.findViewById(R.id.recipe_page_image_border);

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

        recipeStepsLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recipeStepsList.getVisibility() == View.VISIBLE)
                    recipeStepsList.setVisibility(View.GONE);
                else
                    recipeStepsList.setVisibility(View.VISIBLE);
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
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, new StepFragment(recipe)).addToBackStack(null).commit();
            }
        });

        if(recipe.getImageReference() != null)
        {
            Glide.with(getContext()).load(recipe.getImageReference()).into(recipeImage);
            imageContainer.setVisibility(View.VISIBLE);
        }
        else
        {
            imageContainer.setVisibility(View.INVISIBLE);
        }

        if(recipe.getStepsCount() == 0)
            recipeStartButton.setVisibility(View.INVISIBLE);

        List<Ingredient> ingredients = recipe.getIngredients();
        IngredientsListAdapter ingredientsAdapter = new IngredientsListAdapter(getContext(), ingredients);
        recipeIngredientsList.setAdapter(ingredientsAdapter);
        ViewGroup.LayoutParams layoutParams = recipeIngredientsList.getLayoutParams();
        layoutParams.height = Utility.calculateHeight(recipeIngredientsList);
        recipeIngredientsList.setLayoutParams(layoutParams);

        List<Step> steps = recipe.getSteps();
        StepListAdapter stepsAdapter = new StepListAdapter(getContext(), steps);
        recipeStepsList.setAdapter(stepsAdapter);
        return view;
    }
}