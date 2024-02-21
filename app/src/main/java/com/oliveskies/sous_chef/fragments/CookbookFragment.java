package com.oliveskies.sous_chef.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.SearchView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.oliveskies.sous_chef.MainActivity;
import com.oliveskies.sous_chef.R;
import com.oliveskies.sous_chef.adapters.RecipeRecyclerAdapter;
import com.oliveskies.sous_chef.database_models.Ingredient;
import com.oliveskies.sous_chef.database_models.Recipe;
import com.oliveskies.sous_chef.database_models.Step;
import com.oliveskies.sous_chef.view_holders.RecipeViewHolder;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class CookbookFragment extends Fragment {

    Toolbar toolbar;

    RecyclerView recyclerView;
    private MenuItem searchItem;
    private SearchView searchView;

    RecipeRecyclerAdapter recipeRecyclerAdapter;

    StorageReference storageRootReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        storageRootReference = FirebaseStorage.getInstance(getActivity().getString(R.string.storage_link)).getReference();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.recipes_toolbar_menu, menu);
        searchItem = menu.findItem(R.id.recipes_toolbar_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setIconified(true);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCookbook(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                searchCookbook(query);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchCookbook(String search) {
        Query query = FirebaseDatabase.getInstance(getString(R.string.database_url))
                .getReference()
                .child("recipes").orderByChild("name_CaseInsensitive").startAt(search.toLowerCase()).endAt(search.toLowerCase() + "\uf8ff");
        FirebaseRecyclerOptions<Recipe> options = getFirebaseOptions(query);
        recipeRecyclerAdapter = new RecipeRecyclerAdapter(options);
        recipeRecyclerAdapter.startListening();
        recyclerView.setAdapter(recipeRecyclerAdapter);

    }

    private FirebaseRecyclerOptions<Recipe> getFirebaseOptions(Query query)
    {
        FirebaseRecyclerOptions<Recipe> options =
                new FirebaseRecyclerOptions.Builder<Recipe>()
                        .setQuery(query, new SnapshotParser<Recipe>() {
                            @NonNull
                            @Override
                            public Recipe parseSnapshot(@NonNull DataSnapshot snapshot) {
                                List<Ingredient> ingredientList = new ArrayList<>();
                                List<Step> stepList = new ArrayList<>();
                                List<String> tagList = new ArrayList<>();
                                DataSnapshot ingredients = snapshot.child("ingredients");
                                DataSnapshot steps = snapshot.child("steps");
                                DataSnapshot tags = snapshot.child("tags");

                                for(DataSnapshot ds : ingredients.getChildren())
                                    ingredientList.add(new Ingredient(ds));
                                for(DataSnapshot ds : steps.getChildren())
                                    stepList.add(new Step(ds));
                                for(DataSnapshot ds : tags.getChildren())
                                    tagList.add(ds.getKey());

                                GenericTypeIndicator<String> stringIndicator = new GenericTypeIndicator<String>() {};
                                String name = snapshot.child("name").getValue(stringIndicator);
                                String cookingTime = snapshot.child("cooking_time").getValue(stringIndicator);
                                String prepTime = snapshot.child("prep_time").getValue(stringIndicator);
                                String totalCookingTime = snapshot.child("total_cooking_time").getValue(stringIndicator);
                                String servings = snapshot.child("servings").getValue(stringIndicator);
                                String imageLink = snapshot.child("image_link").getValue(stringIndicator);
                                String description = snapshot.child("description").getValue(stringIndicator);
                                Recipe recipe = new Recipe(name, cookingTime, prepTime, totalCookingTime, servings, ingredientList, description, tagList, stepList, imageLink);
                                recipe.setKey(snapshot.getKey());
                                recipe.setImageReference(storageRootReference);
                                return recipe;
                            }
                        }).build();
        return options;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cookbook, container, false);

        recyclerView = view.findViewById(R.id.cookbook_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Query query = FirebaseDatabase.getInstance(getString(R.string.database_url))
                .getReference()
                .child("recipes").orderByChild("name");

        FirebaseRecyclerOptions<Recipe> options = getFirebaseOptions(query);
        //TODO: ViewHolder
        Log.d("Doing... something, I guess", "whatevs");
        recipeRecyclerAdapter = new RecipeRecyclerAdapter(options);
        recyclerView.setAdapter(recipeRecyclerAdapter);

        toolbar = view.findViewById(R.id.cookbook_toolbar);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("Your Cookbook");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recipeRecyclerAdapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        recipeRecyclerAdapter.stopListening();
    }
}