package com.oliveskies.sous_chef;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.oliveskies.sous_chef.adapters.HistoryRecyclerAdapter;
import com.oliveskies.sous_chef.database_models.History;
import com.oliveskies.sous_chef.database_models.Ingredient;
import com.oliveskies.sous_chef.database_models.Recipe;
import com.oliveskies.sous_chef.database_models.Step;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HistoryFragment extends Fragment {
    int historyCount = -1;
    List<String> historyKeys = new ArrayList<>();
    List<Integer> historySteps = new ArrayList<>();
    List<String> historyDates = new ArrayList<>();
    List<History> recipes = new ArrayList<>();
    StorageReference storageRootReference;
    HistoryRecyclerAdapter adapter;
    RecyclerView recyclerView;
    Toolbar toolbar;

    List<DatabaseReference> keyReferences = new ArrayList<>();
    List<ValueEventListener> listenerReferences = new ArrayList<>();
    private void refreshHistory()
    {
        for(int i = 0; i < keyReferences.size(); ++ i)
        {
            keyReferences.get(i).removeEventListener(listenerReferences.get(i));
        }
        historyCount = -1;
        historyKeys = new ArrayList<>();
        historySteps = new ArrayList<>();
        historyDates = new ArrayList<>();
        recipes = new ArrayList<>();
        keyReferences = new ArrayList<>();
        listenerReferences = new ArrayList<>();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("RecipeHistory", Context.MODE_PRIVATE);
        historyCount = sharedPreferences.getInt("history_id", 0);
        for(int i = 0; i < historyCount; ++ i)
        {
            historyKeys.add(sharedPreferences.getString("record_" + Integer.toString(i) + "_key", ""));
            historySteps.add(sharedPreferences.getInt("record_" + Integer.toString(i) + "_step", 0));
            historyDates.add(sharedPreferences.getString("record_" + Integer.toString(i) + "_time", ""));
        }

        DatabaseReference reference = FirebaseDatabase.getInstance(getActivity().getString(R.string.database_url)).getReference().child("recipes");
        for(int i = 0; i < historyCount; ++ i)
        {
            keyReferences.add(reference.child(historyKeys.get(i)));
            int index = i;
            ValueEventListener listener = keyReferences.get(i).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
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
                        recipes.add(new History(recipe, historySteps.get(index), historyDates.get(index), index));
                        recipes.sort(new Comparator<History>() {
                            @Override
                            public int compare(History history, History t1) {
                                SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/YYYY hh:mm:ss");
                                Date date1;
                                Date date2;
                                try {
                                    date1 = sdf.parse(history.date);
                                    date2 = sdf.parse(t1.date);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                return date2.compareTo(date1);
                            }
                        });
                        adapter.notifyDataSetChanged();
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            });

            listenerReferences.add(listener);
        }

        adapter = new HistoryRecyclerAdapter(recipes);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        storageRootReference = FirebaseStorage.getInstance(getActivity().getString(R.string.storage_link)).getReference();
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.history_toolbar_menu, menu);
        MenuItem clearItem = menu.findItem(R.id.history_toolbar_clear);
        clearItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Clear History");
                builder.setMessage("Are you sure you want to clear your recipe history?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clearHistory();
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
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void clearHistory()
    {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("RecipeHistory", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int historyCount = sharedPreferences.getInt("history_id", 0);

        for(int i = 0; i < historyCount; ++ i) {
            editor.remove("record_" + Integer.toString(i) + "_key");
            editor.remove("record_" + Integer.toString(i) + "_step");
            editor.remove("record_" + Integer.toString(i) + "_time");
        }
        historyCount = 0;
        editor.putInt("history_id", historyCount);
        editor.apply();
        refreshHistory();
    }
        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.history_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        toolbar = view.findViewById(R.id.history_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("History");

        refreshHistory();
        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        refreshHistory();
    }
}