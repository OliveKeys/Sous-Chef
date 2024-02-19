package com.oliveskies.sous_chef.adapters;

import static android.view.View.GONE;

import android.content.Context;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.oliveskies.sous_chef.R;
import com.oliveskies.sous_chef.Utility;
import com.oliveskies.sous_chef.database_models.Ingredient;

import java.lang.ref.WeakReference;
import java.util.List;

public class IngredientsFormListAdapter extends ArrayAdapter<Ingredient> {
    WeakReference<List<Ingredient>> ingredientsReference;
    WeakReference<ListView> listReference;
    WeakReference<ConstraintLayout> buttonsReference = null;
    public IngredientsFormListAdapter(Context context, List<Ingredient> ingredients)
    {
        super(context, 0, ingredients);
    }

    public void setIngredientsReference(WeakReference<List<Ingredient>> iReference, WeakReference<ListView> lReference)
    {
        ingredientsReference = iReference;
        listReference = lReference;
    }
    public void setButtonsReference(WeakReference<ConstraintLayout> buttons)
    {
        buttonsReference = buttons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        Ingredient ingredient = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ingredient_item_form, parent, false);
        }
        EditText nameText = (EditText) convertView.findViewById(R.id.ingredient_form_item_name);
        EditText quantityText = (EditText) convertView.findViewById(R.id.ingredient_form_item_quantity);
        EditText notesText = (EditText) convertView.findViewById(R.id.ingredient_form_item_notes);
        ImageView deleteImg = (ImageView) convertView.findViewById(R.id.ingredient_form_delete);
        nameText.setText(ingredient.getName());
        quantityText.setText(ingredient.getQuantity());
        notesText.setText(ingredient.getNotes());
        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientsReference.get().remove(ingredient);
                notifyDataSetChanged();
                ViewGroup.LayoutParams layoutParams = listReference.get().getLayoutParams();
                layoutParams.height = Utility.calculateHeight(listReference.get());
                listReference.get().setLayoutParams(layoutParams);
            }
        });

        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ingredient.setName(charSequence.toString());
                ingredientsReference.get().get(position).setName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        quantityText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ingredient.setQuantity(charSequence.toString());
                ingredientsReference.get().get(position).setQuantity(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        notesText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ingredient.setNotes(charSequence.toString());
                ingredientsReference.get().get(position).setNotes(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    ViewGroup.LayoutParams layoutParams = listReference.get().getLayoutParams();
                    layoutParams.height = Utility.calculateHeight(listReference.get());
                    listReference.get().setLayoutParams(layoutParams);
                    if(buttonsReference != null)
                        buttonsReference.get().setVisibility(View.VISIBLE);
                }
                else
                    if(buttonsReference != null)
                        buttonsReference.get().setVisibility(View.GONE);
            }
        };

        nameText.setOnFocusChangeListener(focusListener);

        quantityText.setOnFocusChangeListener(focusListener);

        notesText.setOnFocusChangeListener(focusListener);

        return convertView;
    }
}
