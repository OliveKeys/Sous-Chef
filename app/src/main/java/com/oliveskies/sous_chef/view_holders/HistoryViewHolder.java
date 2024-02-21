package com.oliveskies.sous_chef.view_holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.oliveskies.sous_chef.R;

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    public TextView historyTitle;
    public TextView historyStep;
    public TextView historyDate;
    public ImageView historyImage;
    public CardView historyCard;

    View view;
    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);

        historyTitle = itemView.findViewById(R.id.history_card_title);
        historyStep = itemView.findViewById(R.id.history_card_step);
        historyDate = itemView.findViewById(R.id.history_card_date);
        historyImage = itemView.findViewById(R.id.history_card_image);
        historyCard = itemView.findViewById(R.id.history_card_card);
        view = itemView;
    }
}
