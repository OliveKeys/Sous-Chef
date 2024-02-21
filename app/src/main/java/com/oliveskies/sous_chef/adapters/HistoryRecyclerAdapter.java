package com.oliveskies.sous_chef.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oliveskies.sous_chef.R;
import com.oliveskies.sous_chef.database_models.History;
import com.oliveskies.sous_chef.database_models.Recipe;
import com.oliveskies.sous_chef.fragments.RecipeFragment;
import com.oliveskies.sous_chef.fragments.StepFragment;
import com.oliveskies.sous_chef.view_holders.HistoryViewHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryViewHolder> {
    Context context;

    List<History> recipes;

    public HistoryRecyclerAdapter(List<History> recipes)
    {
        this.recipes = recipes;
    }
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_card, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Recipe recipe = recipes.get(position).recipe;
        String date = recipes.get(position).date;
        int step = recipes.get(position).step;
        int index = recipes.get(position).index;
        holder.historyTitle.setText(recipe.getName());
        holder.historyDate.setText(date);
        holder.historyStep.setText("Step: " + Integer.toString(step + 1));
        if(recipe.getImageReference() != null) {
            Glide.with(context).load(recipe.getImageReference()).into(holder.historyImage);
        }
        else {
            holder.historyImage.setImageResource(R.drawable.ic_missing_gray_40dp);
        }
        holder.historyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity=(AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, new RecipeFragment(recipe)).addToBackStack(null).commit();
            }
        });

        holder.historyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("RecipeHistory", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int historyCount = sharedPreferences.getInt("history_id", 0);

                for(int i = index; i < historyCount - 1; ++ i)
                {
                    String nextKey = sharedPreferences.getString("record_" + Integer.toString(i + 1) + "_key", "");
                    editor.putString("record_" + Integer.toString(i) + "_key", nextKey);
                    int nextStep = sharedPreferences.getInt("record_" + Integer.toString(i + 1) + "_step", 0);
                    editor.putInt("record_" + Integer.toString(i) + "_step", nextStep);
                    String nextTime = sharedPreferences.getString("record_" + Integer.toString(i + 1) + "_time", "");
                    editor.putString("record_" + Integer.toString(i) + "_time", nextTime);
                }
                historyCount -= 1;
                editor.remove("record_" + Integer.toString(historyCount) + "_key");
                editor.remove("record_" + Integer.toString(historyCount) + "_step");
                editor.remove("record_" + Integer.toString(historyCount) + "_time");
                editor.putInt("history_id", historyCount);
                editor.apply();
                AppCompatActivity activity=(AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, new StepFragment(recipe, step)).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}
