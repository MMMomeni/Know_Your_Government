package com.mmomeni.know_your_government;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "StockAdapter";
    private List<Official> officialList;
    private MainActivity mainAct;

    OfficialAdapter(List<Official> sList, MainActivity ma) {
        this.officialList = sList;
        mainAct = ma;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_box, parent, false);

        itemView.setOnClickListener(mainAct); // means that main activity owns the onClickListener


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Official s = officialList.get(position);

        holder.officeName.setText(s.getOfficeName());
        holder.name.setText(s.getName());
        holder.party.setText("(" + s.getParty() + ")");

        //double percent means that we want a percent symbol in our output



    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
