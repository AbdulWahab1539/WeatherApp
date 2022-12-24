package com.example.final30;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    TextView tvTemp,
            tvLocation,
            tvTime;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        tvTemp = itemView.findViewById(R.id.tv_temp);
        tvLocation = itemView.findViewById(R.id.location);
        tvTime = itemView.findViewById(R.id.timestamp);

    }

}
