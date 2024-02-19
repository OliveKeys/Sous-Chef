package com.oliveskies.sous_chef.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.oliveskies.sous_chef.R;
import com.oliveskies.sous_chef.fragments.RecipeFragment;
import com.oliveskies.sous_chef.database_models.Recipe;
import com.oliveskies.sous_chef.view_holders.RecipeViewHolder;

public class RecipeRecyclerAdapter extends FirebaseRecyclerAdapter<Recipe, RecipeViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RecipeRecyclerAdapter(@NonNull FirebaseRecyclerOptions<Recipe> options) {
        super(options);
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View recipeView = inflater.inflate(R.layout.recipe_card, parent, false);
        RecipeViewHolder recipeViewHolder = new RecipeViewHolder(recipeView);
        return recipeViewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull RecipeViewHolder holder, int position, @NonNull Recipe model) {
        SpannableString name = new SpannableString(model.getName());
        name.setSpan(new UnderlineSpan(), 0, model.getName().length(), 0);
        holder.RecipeTitle.setText(name);
        StringBuilder tags = new StringBuilder();
        for(int i = 0; i < model.getTagsCount(); ++ i)
        {
            tags.append(model.getNthTag(i));
            if(model.getTagsCount() - 1 > i)
                tags.append(", ");
        }
        holder.RecipeTags.setText(tags.toString());

        holder.card.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AppCompatActivity activity=(AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, new RecipeFragment(model)).addToBackStack(null).commit();
            }

        });
    }


}
