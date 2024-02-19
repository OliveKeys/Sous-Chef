package com.oliveskies.sous_chef.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.oliveskies.sous_chef.R;
import com.oliveskies.sous_chef.adapters.IngredientsListAdapter;
import com.oliveskies.sous_chef.database_models.Ingredient;
import com.oliveskies.sous_chef.database_models.Recipe;
import com.oliveskies.sous_chef.database_models.Step;

import java.text.DecimalFormat;
import java.util.List;

public class StepFragment extends Fragment {
    Recipe recipe;
    int currentStep;

    ImageView closeButton;
    TextView titleText;
    ProgressBar timerBar;
    TextView timerText;
    Button timerButton;
    TextView instructionsText;
    ListView ingredientsList;
    ImageButton proceedButton;
    ImageButton backButton;
    int timerHours, timerMinutes, timerSeconds;
    boolean isTimerRunning = false;
    Handler timerHandler;
    public StepFragment(Recipe recipe) {
        this.recipe = recipe;
        currentStep = 0;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        closeButton = view.findViewById(R.id.step_page_close_button);
        titleText = view.findViewById(R.id.step_page_recipe_title);
        timerBar = view.findViewById(R.id.step_page_timer_progress_bar);
        timerText = view.findViewById(R.id.step_page_timer_text);
        timerButton = view.findViewById(R.id.step_page_timer_button);
        instructionsText = view.findViewById(R.id.step_page_instructions);
        ingredientsList = view.findViewById(R.id.step_page_ingredients_list);
        proceedButton = view.findViewById(R.id.step_page_done_button);
        backButton = view.findViewById(R.id.step_page_back_button);

        titleText.setText(recipe.getName());
        setStep(recipe.getNthStep(currentStep));

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Close Recipe");
                builder.setMessage("Are you sure you want to quit the current recipe?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getParentFragmentManager().popBackStackImmediate();
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

        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTimerRunning)
                {
                    isTimerRunning = false;
                    timerButton.setText(getString(R.string.start_timer));
                }
                else
                {
                    isTimerRunning = true;
                    timerButton.setText(getString(R.string.pause_timer));
                    timerHandler = new Handler();
                    timerHandler.postDelayed(new Runnable() {
                        public void run() {
                            if(isTimerRunning == false)
                                return;
                            if(decreaseTimer()) {
                                timerText.setText(makeTimeString(timerHours, timerMinutes, timerSeconds));
                                timerBar.setProgress(timerBar.getProgress() + 1);

                                if(timerSeconds != 0 || timerMinutes != 0 || timerHours != 0) {
                                    timerHandler.postDelayed(this, 1000);
                                    return;
                                }
                            }
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
                            r.play();
                            isTimerRunning = false;
                            timerButton.setText(getString(R.string.start_timer));
                        }
                    }, 1000);
                }
            }
        });

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentStep + 1 < recipe.getStepsCount()) {
                    currentStep += 1;
                    setStep(recipe.getNthStep(currentStep));
                }
                else {
                    Toast.makeText(getContext(), "YIPPEEE RECIPE OVER", Toast.LENGTH_SHORT).show();
               }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentStep > 0) {
                    currentStep -= 1;
                    setStep(recipe.getNthStep(currentStep));
                }
                else {
                    Toast.makeText(getContext(), "Uhhh, you can't go back more", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private boolean decreaseTimer()
    {
        if(timerSeconds > 0) {
            timerSeconds = timerSeconds - 1;
            return true;
        }
        if(timerMinutes > 0) {
            timerMinutes = timerMinutes - 1;
            timerSeconds = 59;
            return true;
        }
        if(timerHours > 0) {
            timerHours = timerHours - 1;
            timerMinutes = 59;
            timerSeconds = 59;
            return true;
        }
        return false;
    }

    private String makeTimeString(int hours, int minutes, int seconds)
    {
        DecimalFormat format = new DecimalFormat("00");
        return format.format(hours) + ":" + format.format(minutes) + ":" + format.format(seconds);
    }

    private void setStep(Step step) {
        timerButton.setText(getString(R.string.start_timer));
        isTimerRunning = false;
        setTimer((int) step.getTimerDuration());

        instructionsText.setText(step.getInstructions());

        List<Ingredient> ingredients = step.getIngredients();
        IngredientsListAdapter ingredientsAdapter = new IngredientsListAdapter(getContext(), ingredients);
        ingredientsList.setAdapter(ingredientsAdapter);

        if(currentStep == 0)
            backButton.setVisibility(View.GONE);
        else
            backButton.setVisibility(View.VISIBLE);

        if(currentStep + 1 == recipe.getStepsCount())
            proceedButton.setVisibility(View.GONE);
        else
            proceedButton.setVisibility(View.VISIBLE);

    }

    private void setTimer(int time)
    {
        if(time == 0)
        {
            timerText.setText("--:--:--");
            timerButton.setVisibility(View.INVISIBLE);
            timerBar.setMax(1);
            timerBar.setProgress(1);
        }
        else
        {
            timerHours = time / 3600;
            timerMinutes = (time / 60) % 60;
            timerSeconds = time % 60;
            timerText.setText(makeTimeString(timerHours, timerMinutes, timerSeconds));
            timerBar.setMax(time);
            timerBar.setProgress(0);
            timerButton.setVisibility(View.VISIBLE);
        }
    }
}