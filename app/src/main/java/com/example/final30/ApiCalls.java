package com.example.final30;

import com.example.final30.models.openweather.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiCalls {

    String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    @GET("weather?&units=metric&appid=d255f0314fe7db2a07902163828feaf3")
    Call<WeatherData> getWeatherData(@Query("q") String city);
}
