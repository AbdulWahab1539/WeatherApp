package com.example.final30;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.final30.models.RouteData;

import java.util.List;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.ViewHolder> {

    private final List<RouteData> data; // Replace with your data model

    public RoutesAdapter(List<RouteData> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_weather_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RouteData item = data.get(position);
        holder.bind(item, data.size());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCityTemp, tvCityName, tvStatus, tvHumidity, tvPressure, tvWind, tvFeelsLike;
        LottieAnimationView LAVArrow, LAVWeather;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCityName = itemView.findViewById(R.id.tvCityName);
            tvCityTemp = itemView.findViewById(R.id.tvCityTemp);
            tvStatus = itemView.findViewById(R.id.tvCityStatus);
            tvHumidity = itemView.findViewById(R.id.tvHumidity);
            tvPressure = itemView.findViewById(R.id.tvPressure);
            tvWind = itemView.findViewById(R.id.tvWind);
            tvFeelsLike = itemView.findViewById(R.id.tvFeelsLike);

            LAVArrow = itemView.findViewById(R.id.LAVArrow);
            LAVWeather = itemView.findViewById(R.id.LAVWeather);
        }

        public void bind(RouteData item, int position) {
            tvCityName.setText(item.getCity());
            tvCityTemp.setText(item.getTemperature());
            tvStatus.setText(item.getStatus());
            LAVWeather.setAnimation(Utils.getAnimAccordingToStatus(item.getStatus()));
            tvHumidity.setText(item.getHumidity());
            tvPressure.setText(item.getPressure());
            tvFeelsLike.setText(item.getFeelsLike());
            tvWind.setText(item.getWind());
            Log.i("TAG", "bind: " + getAdapterPosition() + position);
            if (getAdapterPosition() >= position - 1) {
                LAVArrow.setVisibility(View.GONE);
            }
        }
    }
}


