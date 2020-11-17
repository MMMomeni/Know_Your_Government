package com.mmomeni.know_your_government;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView officeName; // we can make these three public too, but we should never make them private
    TextView name;
    TextView party;


    MyViewHolder(View view){ //this objects will hold references to the items in our notes_list layout
        super(view);
        officeName = view.findViewById(R.id.OfficeName1);
        name = view.findViewById(R.id.Name1);
        party = view.findViewById(R.id.Party1);

    }
}
