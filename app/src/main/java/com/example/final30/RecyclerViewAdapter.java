package com.example.final30;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final30.models.openweather.Logs;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {


    List<Logs> logsList;
    Context context;

    public RecyclerViewAdapter(Context context, List<Logs> listInstances) {
        this.logsList = listInstances;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.layout_log, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Logs logs = logsList.get(position);
        holder.tvLocation.setText(logs.getLocation());
        holder.tvTemp.setText(logs.getTemp());
        holder.tvTime.setText(logs.getTimestamp());
    }

    @Override
    public int getItemCount() {
        if (logsList != null) return logsList.size();
        else return 0;
    }
}
