package com.example.final30.models;

import com.example.final30.models.maps.Route;
import com.example.final30.models.openweather.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MapsApi {

    String BASE_URL =" https://maps.googleapis.com/maps/api/directions/";

//    origin=33.5614357,72.8780628&destination=34.1750613,73.2858989
    @GET("json?&key=AIzaSyAgkbasawJgXgLaIAs838J7Rjd7JsE6FZE")
    Call<Route> getRoute(@Query("origin") String origin, @Query("destination") String destination);

}
