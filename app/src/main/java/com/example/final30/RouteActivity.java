package com.example.final30;

import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.final30.models.MapsApi;
import com.example.final30.models.RouteData;
import com.example.final30.models.maps.Route;
import com.example.final30.models.maps.StepsItem;
import com.example.final30.models.openweather.WeatherData;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteActivity extends AppCompatActivity implements View.OnClickListener {
    static String TAG = "TAG";
    AutoCompleteTextView etOrigin, etDestination;
    Button btnRoute;
    MapsApi mapsApi;
    ApiCalls apiCalls;
    List<String> locations, statuses, temperatures;
    TextView textView, tvWeatherStatus;
    LottieAnimationView lottieAnimationView;
    String origin;
    String destination;
    String[] cities;
    String[] weatherConditions;
    int index = 0;
    Dialog customDialog;
    RecyclerView recyclerView;
    RoutesAdapter routesAdapter;
    List<RouteData> routeData;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        recyclerView = findViewById(R.id.rvRouteCities);

        customDialog = Utils.getLoading(this);

        etOrigin = findViewById(R.id.et_origin);
        etDestination = findViewById(R.id.et_dest);
        btnRoute = findViewById(R.id.btn_search);
        btnRoute.setOnClickListener(this);

        locations = new ArrayList<>();
        statuses = new ArrayList<>();
        temperatures = new ArrayList<>();
        routeData = new ArrayList<>();

        textView = findViewById(R.id.errortext);

        mapsApi = MapApiClient.getRetrofit();
        apiCalls = ApiClient.getRetrofit();

        cities = getResources().getStringArray(R.array.pakistan_cities);
        weatherConditions = getResources().getStringArray(R.array.weather_conditions);

        lottieAnimationView = findViewById(R.id.weather_anim);
        tvWeatherStatus = findViewById(R.id.tv_weather);

        // Create an ArrayAdapter with the suggestions
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);
        // Set the adapter on the AutoCompleteTextView
        etOrigin.setAdapter(adapter);
        etDestination.setAdapter(adapter);

        etOrigin.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSuggestion = adapter.getItem(position);
            // Do something with the selected suggestion
            origin = selectedSuggestion.trim();
        });
        etDestination.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSuggestion = adapter.getItem(position);
            // Do something with the selected suggestion
            destination = selectedSuggestion.trim();
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_search) {
            if (etDestination.getText().toString().isEmpty() || etOrigin.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please Enter Destination & Origin City to Search", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i(TAG, "onClick: " + etOrigin.getText().toString() + etDestination.getText().toString());
            getRouteWeather(etOrigin.getText().toString(), etDestination.getText().toString());
        }
    }

    public String getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        String p1;
        try {
            address = coder.getFromLocationName(strAddress, 5);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (address == null || address.isEmpty()) return "";
        Log.i(TAG, "getLocationFromAddress: " + address.get(0).getAddressLine(0));
        Address location = address.get(0);
        if (location == null) return null;
        p1 = location.getLatitude() + "," + location.getLongitude();
        return p1;
    }

    private void getRouteWeather(String origin, String destination) {
        locations.clear();
        statuses.clear();
        temperatures.clear();
        routeData.clear();
        index = 0;
        this.origin = origin.trim();
        this.destination = destination.trim();

        if (!Utils.isNetworkAvailable(this)) {
            Utils.showInternetDialog(this);
            return;
        }

        //Get Lat, Lng
        String originLatLng = getLocationFromAddress(this, origin.trim());
        String destLatLng = getLocationFromAddress(this, destination.trim());
        Log.i(TAG, "getRouteWeather: " + origin + originLatLng + destination + "::" + destLatLng);
        if (destLatLng.isEmpty() || originLatLng.isEmpty()) {
            Toast.makeText(this, "Check Your address try to refine your search more.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Call<Route> call = mapsApi.getRoute(originLatLng, destLatLng);
        runOnUiThread(() -> customDialog.show());
        call.enqueue(new Callback<Route>() {
            @Override
            public void onResponse(@NonNull Call<Route> call, @NonNull Response<Route> response) {
                int statusCode = response.code();
                runOnUiThread(() -> customDialog.dismiss());
                if (response.errorBody() != null) {
                    JSONObject jObjError;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        Log.i(TAG, "onResponse: " + jObjError);
                        Toast.makeText(RouteActivity.this, response.errorBody().string(),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (response.body() == null | statusCode != 200) return;
                processCitiesInRoute(response.body());
            }


            @Override
            public void onFailure(@NonNull Call<Route> call, @NonNull Throwable t) {
                Toast.makeText(RouteActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                tvWeatherStatus.setVisibility(View.GONE);
                lottieAnimationView.setVisibility(View.GONE);
                runOnUiThread(() -> customDialog.dismiss());

            }
        });

    }

    private void processCitiesInRoute(Route body) {
        Toast.makeText(this, body.getStatus(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, "processCitiesInRoute: " + body.getStatus());
        tvWeatherStatus.setVisibility(View.GONE);
        lottieAnimationView.setVisibility(View.GONE);
        if (body.getRoutes().isEmpty()) return;
        if (body.getRoutes().get(0).getLegs().isEmpty()) return;
        if (body.getRoutes().get(0).getLegs().get(0).getSteps().isEmpty()) return;
        for (StepsItem route : body.getRoutes().get(0).getLegs().get(0).getSteps()) {
            // Loop through the array and print each city name
            for (String city : cities) {
                if (containsWord(route.getHtmlInstructions().toLowerCase(), city.toLowerCase())
                        && !route.getHtmlInstructions().toLowerCase().contains("motorway")
                        && !route.getHtmlInstructions().toLowerCase().contains("rd")
//                        && !containsWord(route.getHtmlInstructions().toLowerCase(), city.toLowerCase() + " rd")
//                        && !containsWord(route.getHtmlInstructions().toLowerCase(), city.toLowerCase() + " motorway")
                ) {
                    Log.i(TAG, "processCitiesInRoute: " + city);
                    locations.add(city);
                }
            }
        }

        Log.i(TAG, "processCitiesInRoute: " + locations.size());
        Set<String> set = new LinkedHashSet<>(locations);
        set.remove(origin.trim());
        set.remove(destination.trim());
        Log.i(TAG, "processCitiesInRoute: " + set);
        locations.clear();
        locations.addAll(set);
        Log.i(TAG, "processCitiesInRoute: " + locations);
        locations.add(0, origin.trim());
        Log.i(TAG, "processCitiesInRoute: Origin Added" + locations);
        locations.add(locations.size(), destination.trim());
        Log.i(TAG, "processCitiesInRoute: Dest Added" + locations);
//        for (String location : locations) {
//            Log.i(TAG, "processCitiesInRoute: " + location + locations.size());
//            getWeatherData(locations);
//        }

        getWeatherData(locations.get(index));
    }

    public static boolean containsWord(String text, String word) {
        return Pattern.compile("\\b" + word + "\\b").matcher(text).find();
    }

    void getWeatherData(String city) {
        if (!Utils.isNetworkAvailable(this)) {
            Utils.showInternetDialog(this);
            return;
        }
//        if (city.equalsIgnoreCase("swat")) return;
        Call<WeatherData> call = apiCalls.getWeatherData(city);
        runOnUiThread(() -> customDialog.show());
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(@NonNull Call<WeatherData> call, @NonNull Response<WeatherData> response) {
                runOnUiThread(() -> customDialog.dismiss());
                int statusCode = response.code();
                if (response.errorBody() != null) {
                    JSONObject jObjError;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        String message = jObjError.getString("message");
                        String code = jObjError.getString("cod");
                        Toast.makeText(RouteActivity.this, code + ": " + city + message,
                                Toast.LENGTH_LONG).show();
                        updateUIOrProcessNextCity(city);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (response.body() == null | statusCode != 200) return;
                Log.i(TAG, "onResponse: " + response.body().getWeather().get(0).getMain() + city);
                routeData.add(new RouteData(
                                String.format("%s°C", Math.round(response.body().getMain().getTemp()))
                                , city,
                                response.body().getWeather().get(0).getMain(),
                                String.format("Pressure %s Pa", response.body().getMain().getPressure()),
                                String.format("Humidity %s%%", response.body().getMain().getHumidity()),
                                String.format("Feels Like %s°C", Math.round(response.body().getMain().getFeelsLike())),
                                String.format("%s Km/h", Math.round(response.body().getWind().getSpeed()))
                        )
                );
                statuses.add(response.body().getWeather().get(0).getMain());
                temperatures.add(String.format("%s°C", Math.round(response.body().getMain().getTemp())));
                updateUIOrProcessNextCity(city);
            }

            @Override
            public void onFailure(@NonNull Call<WeatherData> call, @NonNull Throwable t) {
                runOnUiThread(() -> customDialog.dismiss());
                Toast.makeText(RouteActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                tvWeatherStatus.setVisibility(View.GONE);
                lottieAnimationView.setVisibility(View.GONE);
            }
        });
    }

    private void updateUIOrProcessNextCity(String city) {
        if (!locations.isEmpty() && city.equals(locations.get(locations.size() - 1)))
            updateUI();
        else if (!locations.isEmpty()) {
            index++;
            getWeatherData(locations.get(index));
        }
    }

    private void updateUI() {
        Log.i(TAG, "updateUI:");
        String finalStatus;
        int dominance = -1;
        int anim;
        for (String status : statuses) {
            if (status.equals("Clouds") && dominance < 1) {
                dominance = 1;
            } else if (status.equals("Clear") && dominance < 0) {
                dominance = 0;
            } else if (status.equals("Rain") && dominance < 2) {
                dominance = 2;
            } else if (status.equals("Thunderstorm") && dominance < 4) {
                dominance = 3;
            } else if (status.equals("Snow")) {
                dominance = 4;
            }
        }

        switch (dominance) {
            case 1:
                finalStatus = "Clouds:  Bring an umbrella in case of rain, and dress in layers";
                anim = R.raw.clouds;
                break;
            case 2:
                finalStatus = "Rain: Wear waterproof clothing and shoes, carry an umbrella, and watch out for slippery surfaces.";
                anim = R.raw.rain;
                break;
            case 3:
                finalStatus = "Thunderstorm: Stay indoors, away from windows, and unplug electronic devices.";
                anim = R.raw.storm;
                break;
            case 4:
                finalStatus = "Snow: Wear warm, waterproof clothing and shoes, and drive slowly on slippery roads.";
                anim = R.raw.snow;
                break;
            default:
                finalStatus = "Clear: Wear sunscreen, stay hydrated, and seek shade";
                anim = R.raw.clear;
        }
        lottieAnimationView.setAnimation(anim);
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.playAnimation();
        lottieAnimationView.setRepeatCount(999999);
        tvWeatherStatus.setText(finalStatus);
        tvWeatherStatus.setVisibility(View.VISIBLE);

        displayAllCitiesWeather();
    }

    private void displayAllCitiesWeather() {
        for (RouteData routesData :
                routeData) {
            Log.i(TAG, "displayAllCitiesWeather: " + routesData.getCity());
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        routesAdapter = new RoutesAdapter(routeData);
        recyclerView.setAdapter(routesAdapter);
    }

}