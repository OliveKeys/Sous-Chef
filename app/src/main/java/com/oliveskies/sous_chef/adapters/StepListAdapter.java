package com.oliveskies.sous_chef.adapters;

import static android.view.View.GONE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oliveskies.sous_chef.R;
import com.oliveskies.sous_chef.database_models.Step;

import java.util.List;

public class StepListAdapter extends ArrayAdapter<Step> {
    public StepListAdapter(Context context, List<Step> steps)
    {
        super(context, 0, steps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Step step = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.recipe_step_item, parent, false);
        }
        TextView stepNumber = (TextView) convertView.findViewById(R.id.recipe_step_item_number);
        TextView stepInstructions = (TextView) convertView.findViewById(R.id.recipe_step_item_instructions);
        // Populate the data into the template view using the data object
        int stepId = position + 1;
        stepNumber.setText(Integer.toString(stepId));
        stepInstructions.setText(step.getInstructions());
        return convertView;
    }
}
