package com.oliveskies.sous_chef.fragments;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.j2objc.annotations.Weak;
import com.oliveskies.sous_chef.MainActivity;
import com.oliveskies.sous_chef.R;
import com.oliveskies.sous_chef.adapters.IngredientsFormListAdapter;
import com.oliveskies.sous_chef.adapters.IngredientsListAdapter;
import com.oliveskies.sous_chef.database_models.Ingredient;
import com.oliveskies.sous_chef.database_models.Step;
import com.oliveskies.sous_chef.Utility;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AddRecipeFragment extends Fragment {
    Toolbar toolbar;
    EditText generalTitleText;
    ImageView generalImageView;
    EditText generalPrepTimeText;
    EditText generalCookTimeText;
    EditText generalTotalTimeText;
    EditText generalDescriptionText;
    EditText generalServingsText;
    ListView generalIngredientsListView;
    Button generalAddIngredientButton;
    Button generalFirstStepButton;
    ScrollView generalView;

    ConstraintLayout stepView;
    EditText stepInstructionsText;
    EditText stepTimerHoursText;
    EditText stepTimerMinutesText;
    EditText stepTimerSecondsText;
    ListView stepIngredientsListView;
    Button stepAddIngredientButton;
    ImageButton stepBackButton;
    ImageButton stepNextButton;
    ImageButton stepAddButton;
    ImageButton stepDeleteButton;
    ConstraintLayout stepButtonsContainer;

    String title = "";
    Uri image = null;
    String description = "";
    String servings = "";
    String prepTime = "";
    String cookTime = "";
    String totalTime = "";
    List<Ingredient> ingredients = new ArrayList<>();
    List<Step> steps = new ArrayList<>();
    WeakReference<List<Ingredient>> generalIngredientsReference = new WeakReference<List<Ingredient>>(ingredients);
    WeakReference<ListView> generalIngredientsListReference;
    WeakReference<List<Ingredient>> stepIngredientsReference = null;
    WeakReference<ListView> stepIngredientsListReference;
    WeakReference<ConstraintLayout> stepButtonsReference;
    int currentStep = -1;
    MenuItem saveRecipe;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    image = uri;
                    generalImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    Glide.with(getContext()).load(image).into(generalImageView);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });
    AppCompatActivity activity;
    public AddRecipeFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.add_recipe_toolbar_menu, menu);
        saveRecipe = menu.findItem(R.id.add_recipe_toolbar_save);
        saveRecipe.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Save Recipe");
                builder.setMessage("Are you sure you want to save this recipe? You won't be able to modify it afterwards.");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveRecipeToDatabase();
                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        generalView = view.findViewById(R.id.add_recipe_general_info_container);
        generalTitleText = view.findViewById(R.id.add_recipe_title_form);
        generalImageView = view.findViewById(R.id.add_recipe_image_view);
        generalDescriptionText = view.findViewById(R.id.add_recipe_description_form);
        generalServingsText = view.findViewById(R.id.add_recipe_servings_form);
        generalPrepTimeText = view.findViewById(R.id.add_recipe_times_prep_form);
        generalCookTimeText = view.findViewById(R.id.add_recipe_times_cook_form);
        generalTotalTimeText = view.findViewById(R.id.add_recipe_times_total_form);
        generalIngredientsListView = view.findViewById(R.id.add_recipe_ingredients_list);
        generalAddIngredientButton = view.findViewById(R.id.add_recipe_ingredients_button);
        generalFirstStepButton = view.findViewById(R.id.add_recipe_first_step_button);

        stepView = view.findViewById(R.id.add_recipe_step_info_container);
        stepInstructionsText = view.findViewById(R.id.add_recipe_step_instructions_form);
        stepTimerHoursText = view.findViewById(R.id.add_recipe_step_timer_form_hours);
        stepTimerMinutesText = view.findViewById(R.id.add_recipe_step_timer_form_minutes);
        stepTimerSecondsText = view.findViewById(R.id.add_recipe_step_timer_form_seconds);
        stepIngredientsListView = view.findViewById(R.id.add_recipe_step_ingredients_list);
        stepAddIngredientButton = view.findViewById(R.id.add_recipe_step_ingredients_button);
        stepBackButton = view.findViewById(R.id.add_recipe_back_step_button);
        stepNextButton = view.findViewById(R.id.add_recipe_next_step_button);
        stepAddButton = view.findViewById(R.id.add_recipe_new_step_button);
        stepDeleteButton = view.findViewById(R.id.add_recipe_delete_step_button);
        stepButtonsContainer = view.findViewById(R.id.add_recipe_steps_buttons_container);

        generalIngredientsListReference = new WeakReference<ListView>(generalIngredientsListView);
        stepIngredientsListReference = new WeakReference<ListView>(stepIngredientsListView);
        stepButtonsReference = new WeakReference<ConstraintLayout>(stepButtonsContainer);

        //This shows up as an error but apparently it should just work? It's just an editor-side error. Weird??
        generalImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        stepNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentStep += 1;
                prepareStepScreen();
            }
        });

        stepAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                steps.add(new Step());
                currentStep += 1;
                prepareStepScreen();
            }
        });
        stepDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Delete Step");
                builder.setMessage("Are you sure you want to delete this step?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                steps.remove(steps.size() - 1);
                                currentStep -= 1;
                                if(steps.size() == 0)
                                {
                                    prepareGeneralInfoView();
                                }
                                else
                                {
                                    prepareStepScreen();
                                }
                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        stepBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentStep == 0)
                {
                    prepareGeneralInfoView();
                }
                else
                {
                    currentStep -= 1;
                    prepareStepScreen();
                }
            }
        });

        generalFirstStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(steps.size() == 0) {
                    steps.add(new Step());
                }
                currentStep = 0;
                prepareStepScreen();
            }
        });

        generalTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                title = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        generalPrepTimeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                prepTime = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        generalCookTimeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cookTime = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        generalTotalTimeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                totalTime = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        generalDescriptionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                description = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        generalServingsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                servings = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        generalAddIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredients.add(new Ingredient());
                IngredientsFormListAdapter ingredientsFormListAdapter = new IngredientsFormListAdapter(getContext(), ingredients);
                ingredientsFormListAdapter.setIngredientsReference(generalIngredientsReference, generalIngredientsListReference);
                generalIngredientsListView.setAdapter(ingredientsFormListAdapter);

                ViewGroup.LayoutParams layoutParams = generalIngredientsListView.getLayoutParams();
                layoutParams.height = Utility.calculateHeight(generalIngredientsListView);
                generalIngredientsListView.setLayoutParams(layoutParams);
            }
        });

        stepAddIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                steps.get(currentStep).mIngredients.add(new Ingredient());
                IngredientsFormListAdapter ingredientsFormListAdapter = new IngredientsFormListAdapter(getContext(), steps.get(currentStep).mIngredients);
                ingredientsFormListAdapter.setIngredientsReference(stepIngredientsReference, stepIngredientsListReference);
                ingredientsFormListAdapter.setButtonsReference(stepButtonsReference);
                stepIngredientsListView.setAdapter(ingredientsFormListAdapter);

                ViewGroup.LayoutParams layoutParams = stepIngredientsListView.getLayoutParams();
                layoutParams.height = Utility.calculateHeight(stepIngredientsListView);
                stepIngredientsListView.setLayoutParams(layoutParams);
            }
        });

        toolbar = view.findViewById(R.id.add_recipe_toolbar);
        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("Add a Recipe");
        return view;
    }

    public void prepareGeneralInfoView()
    {
        activity.getSupportActionBar().setTitle("Add a Recipe");
        stepView.setVisibility(View.GONE);
        generalView.setVisibility(View.VISIBLE);
        currentStep = -1;
        generalDescriptionText.setText(description);
        generalTitleText.setText(title);
        generalServingsText.setText(servings);
        if(image != null)
            Glide.with(getContext()).load(image).into(generalImageView);
        generalPrepTimeText.setText(prepTime);
        generalCookTimeText.setText(cookTime);
        generalTotalTimeText.setText(totalTime);


        IngredientsFormListAdapter ingredientsFormListAdapter = new IngredientsFormListAdapter(getContext(), ingredients);
        ingredientsFormListAdapter.setIngredientsReference(generalIngredientsReference, generalIngredientsListReference);
        generalIngredientsListView.setAdapter(ingredientsFormListAdapter);
        ViewGroup.LayoutParams layoutParams = generalIngredientsListView.getLayoutParams();
        layoutParams.height = Utility.calculateHeight(generalIngredientsListView);
        generalIngredientsListView.setLayoutParams(layoutParams);
    }
    public void updateTimer(double timerDuration)
    {
        int hours = (int) timerDuration / 3600;
        int minutes = (int) (timerDuration / 60) % 60;
        int seconds = (int) timerDuration % 60;
        stepTimerHoursText.setText(Integer.toString(hours));
        stepTimerMinutesText.setText(Integer.toString(minutes));
        stepTimerSecondsText.setText(Integer.toString(seconds));
    }
    public void toggleStepButtons(boolean visibility)
    {
        if(visibility)
        {
            if(currentStep + 1 < steps.size())
            {
                stepNextButton.setVisibility(View.VISIBLE);
                stepAddButton.setVisibility(View.GONE);
                stepDeleteButton.setVisibility(View.GONE);
            }
            else
            {
                stepNextButton.setVisibility(View.GONE);
                stepAddButton.setVisibility(View.VISIBLE);
                stepDeleteButton.setVisibility(View.VISIBLE);
            }
            stepBackButton.setVisibility(View.VISIBLE);
        }
        else
        {
            stepBackButton.setVisibility(View.GONE);
            stepNextButton.setVisibility(View.GONE);
            stepAddButton.setVisibility(View.GONE);
            stepDeleteButton.setVisibility(View.GONE);
        }
    }
    public void prepareStepScreen()
    {
        activity.getSupportActionBar().setTitle("Step " + Integer.toString(currentStep + 1));
        Step thisStep = steps.get(currentStep);
        stepView.setVisibility(View.VISIBLE);
        generalView.setVisibility(View.GONE);
        updateTimer(thisStep.getTimerDuration());
        stepInstructionsText.setText(thisStep.getInstructions());
        toggleStepButtons(true);
        stepInstructionsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                steps.get(currentStep).setInstructions(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        stepInstructionsText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    toggleStepButtons(true);
                }
                else {
                    toggleStepButtons(false);
                }
            }
        });
        stepTimerHoursText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    if (stepTimerHoursText.getText().toString().isEmpty()) {
                        stepTimerHoursText.setText("0");
                        return;
                    }
                    double hours;
                    try {
                        hours = Double.parseDouble(stepTimerHoursText.getText().toString());
                    }
                    catch (NumberFormatException e)  {
                        stepTimerHoursText.setText("0");
                        Toast.makeText(getContext(), "Invalid Hour Number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (hours < 0) {
                        stepTimerHoursText.setText("0");
                        Toast.makeText(getContext(), "Hours can not be negative", Toast.LENGTH_SHORT).show();
                    } else {
                        double minutes = Double.valueOf(stepTimerMinutesText.getText().toString());
                        double seconds = Double.valueOf(stepTimerSecondsText.getText().toString());
                        double timer = hours * 3600 + minutes * 60 + seconds;
                        steps.get(currentStep).setTimerDuration(timer);
                    }
                    toggleStepButtons(true);
                }
                else {
                    toggleStepButtons(false);
                }
            }
        });
        stepTimerMinutesText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    if (stepTimerMinutesText.getText().toString().isEmpty()) {
                        stepTimerMinutesText.setText("0");
                        return;
                    }
                    double minutes;
                    try {
                        minutes = Double.parseDouble(stepTimerMinutesText.getText().toString());
                    }
                    catch (NumberFormatException e)  {
                        stepTimerMinutesText.setText("0");
                        Toast.makeText(getContext(), "Invalid Minute Number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (minutes < 0) {
                        stepTimerHoursText.setText("0");
                        Toast.makeText(getContext(), "Minutes can not be negative", Toast.LENGTH_SHORT).show();
                    } else {
                        double hours = Double.valueOf(stepTimerHoursText.getText().toString());
                        double seconds = Double.valueOf(stepTimerSecondsText.getText().toString());
                        double timer = hours * 3600 + minutes * 60 + seconds;
                        steps.get(currentStep).setTimerDuration(timer);
                    }
                    toggleStepButtons(true);
                }
                else {
                    toggleStepButtons(false);
                }
            }
        });
        stepTimerSecondsText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    if (stepTimerSecondsText.getText().toString().isEmpty()) {
                        stepTimerSecondsText.setText("0");
                        return;
                    }
                    double seconds;
                    try {
                        seconds = Double.parseDouble(stepTimerSecondsText.getText().toString());
                    }
                    catch (NumberFormatException e)  {
                        stepTimerSecondsText.setText("0");
                        Toast.makeText(getContext(), "Invalid Seconds Number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (seconds < 0) {
                        stepTimerHoursText.setText("0");
                        Toast.makeText(getContext(), "Seconds can not be negative", Toast.LENGTH_SHORT).show();
                    } else {
                        double hours = Double.valueOf(stepTimerHoursText.getText().toString());
                        double minutes = Double.valueOf(stepTimerMinutesText.getText().toString());
                        double timer = hours * 3600 + minutes * 60 + seconds;
                        steps.get(currentStep).setTimerDuration(timer);
                    }
                    toggleStepButtons(true);
                }
                else {
                    toggleStepButtons(false);
                }
            }
        });




        stepIngredientsReference = new WeakReference<List<Ingredient>>(steps.get(currentStep).mIngredients);
        IngredientsFormListAdapter ingredientsFormListAdapter = new IngredientsFormListAdapter(getContext(), steps.get(currentStep).mIngredients);
        ingredientsFormListAdapter.setIngredientsReference(stepIngredientsReference, stepIngredientsListReference);
        ingredientsFormListAdapter.setButtonsReference(stepButtonsReference);
        stepIngredientsListView.setAdapter(ingredientsFormListAdapter);

        ViewGroup.LayoutParams layoutParams = stepIngredientsListView.getLayoutParams();
        layoutParams.height = Utility.calculateHeight(stepIngredientsListView);
        stepIngredientsListView.setLayoutParams(layoutParams);
    }

    void saveRecipeToDatabase()
    {
        if(title == null || title.isEmpty()) {
            Toast.makeText(activity, "Title cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        if(prepTime == null || cookTime == null || totalTime == null || prepTime.isEmpty() || cookTime.isEmpty() || totalTime.isEmpty()) {
            Toast.makeText(activity, "Please fill in prep time, cooking time and total time.", Toast.LENGTH_LONG).show();
            return;
        }

        if(steps.size() == 0) {
            Toast.makeText(activity, "Needs at least 1 step", Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance(getString(R.string.database_url));
        DatabaseReference reference = database.getReference().child("recipes");
        DatabaseReference newRecipeEntry = reference.push();
        newRecipeEntry.child("cooking_time").setValue(cookTime);
        newRecipeEntry.child("prep_time").setValue(prepTime);
        newRecipeEntry.child("total_cooking_time").setValue(totalTime);
        newRecipeEntry.child("description").setValue(description);
        newRecipeEntry.child("name").setValue(title);
        newRecipeEntry.child("name_CaseInsensitive").setValue(title.toLowerCase());
        newRecipeEntry.child("servings").setValue(servings);

        for(int i = 0; i < ingredients.size(); ++ i) {
            newRecipeEntry.child("ingredients").child(Integer.toString(i)).child("name").setValue(ingredients.get(i).getName());
            newRecipeEntry.child("ingredients").child(Integer.toString(i)).child("notes").setValue(ingredients.get(i).getNotes());
            newRecipeEntry.child("ingredients").child(Integer.toString(i)).child("quantity").setValue(ingredients.get(i).getQuantity());
        }

        for(int i = 0; i < steps.size(); ++ i) {
            newRecipeEntry.child("steps").child(Integer.toString(i)).child("instructions").setValue(steps.get(i).getInstructions());
            newRecipeEntry.child("steps").child(Integer.toString(i)).child("timer_duration").setValue(steps.get(i).getTimerDuration());
            for(int j = 0; j < steps.get(i).mIngredients.size(); ++ j) {
                newRecipeEntry.child("steps").child(Integer.toString(i))
                        .child("ingredients").child(Integer.toString(j))
                        .child("name").setValue(steps.get(i).mIngredients.get(j).getName());
                newRecipeEntry.child("steps").child(Integer.toString(i))
                        .child("ingredients").child(Integer.toString(j))
                        .child("notes").setValue(steps.get(i).mIngredients.get(j).getNotes());
                newRecipeEntry.child("steps").child(Integer.toString(i))
                        .child("ingredients").child(Integer.toString(j))
                        .child("quantity").setValue(steps.get(i).mIngredients.get(j).getQuantity());
            }
        }

        title = "";
        prepTime = "";
        cookTime = "";
        totalTime = "";
        description = "";
        servings = "";
        image = null;
        ingredients.clear();
        prepareGeneralInfoView();
        steps.clear();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Recipe Saved");
        builder.setMessage("Your recipe has been added successfully!");
        builder.setPositiveButton("Yippeeeee!!!",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton("...I think I forgot something", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}