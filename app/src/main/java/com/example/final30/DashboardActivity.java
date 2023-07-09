package com.example.final30;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.final30.models.openweather.Logs;
import com.example.final30.models.openweather.WeatherData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.installations.FirebaseInstallations;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    AutoCompleteTextView etCity;
    private ArrayAdapter<String> adapter;
    String[] cities;
    Button btnSearch, btnRoute;
    String city = "abbottabad";
    ApiCalls apiCalls;
    TextView tvTemp, tvLocation, tvMinTemp, tvMaxTemp, tvPressure,
            tvWind, tvHumidity, tvSunset, tvSunrise, tvStatus, tvUpdateAt, tvFeelsLike;
    ImageView ivWeatherIcon;
    LinearLayout btnLogs;
    DatabaseReference databaseReference;
    static String installationId;
    FrameLayout progressBar;

    LottieAnimationView LAVMainWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        cities = getResources().getStringArray(R.array.pakistan_cities);

        etCity = findViewById(R.id.et_city);
        btnSearch = findViewById(R.id.btn_search);
        btnRoute = findViewById(R.id.btn_route);
        btnRoute.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        tvTemp = findViewById(R.id.temp);
        tvLocation = findViewById(R.id.address);
        tvMaxTemp = findViewById(R.id.temp_max);
        tvMinTemp = findViewById(R.id.temp_min);
        tvWind = findViewById(R.id.wind);
        tvPressure = findViewById(R.id.pressure);
        tvSunrise = findViewById(R.id.sunrise);
        tvSunset = findViewById(R.id.sunset);
        tvHumidity = findViewById(R.id.humidity);
        tvStatus = findViewById(R.id.status);
        tvUpdateAt = findViewById(R.id.update_at);
        tvFeelsLike = findViewById(R.id.feels_like);

        ivWeatherIcon = findViewById(R.id.weather_icon);
        btnLogs = findViewById(R.id.btn_logs);
        btnLogs.setOnClickListener(this);
        apiCalls = ApiClient.getRetrofit();
        progressBar = findViewById(R.id.loader);
        LAVMainWeather = findViewById(R.id.LAVMainWeather);

        databaseReference = FirebaseDatabase.getInstance()
                .getReference();

        FirebaseInstallations.getInstance().getId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        installationId = task.getResult();
                        getWeatherData(city);
                        Log.d("Installations", "Installation ID: " + task.getResult());
                    } else Log.e("Installations", "Unable to get Installation ID");
                });

        // Create an ArrayAdapter with the suggestions
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);

        // Set the adapter on the AutoCompleteTextView
        etCity.setAdapter(adapter);
        etCity.setOnItemClickListener((parent, view, position, id) -> {
            city = adapter.getItem(position).trim();
            // Do something with the selected suggestion
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_search) {
            if (etCity.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please Enter City to Search", Toast.LENGTH_SHORT).show();
                return;
            }
            getWeatherData(etCity.getText().toString());
        } else if (id == R.id.btn_logs)
            startActivity(new Intent(this, LogsActivity.class));
        else if (id == R.id.btn_route)
            startActivity(new Intent(this, RouteActivity.class));
    }

    void getWeatherData(String city) {
        if (city.isEmpty()) city = this.city;
        if (!Utils.isNetworkAvailable(this)) {
            Utils.showInternetDialog(this);
            return;
        }
        Call<WeatherData> call = apiCalls.getWeatherData(city);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(@NonNull Call<WeatherData> call, @NonNull Response<WeatherData> response) {
                int statusCode = response.code();
                if (response.errorBody() != null) {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jObjError;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        String message = jObjError.getString("message");
                        String code = jObjError.getString("cod");
                        Toast.makeText(DashboardActivity.this, code + ": " + message,
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (response.body() == null | statusCode != 200) return;
                updateUI(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<WeatherData> call, @NonNull Throwable t) {
                Toast.makeText(DashboardActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updateUI(WeatherData weatherData) {
        Glide.with(DashboardActivity.this)
                .load("http://openweathermap.org/img/wn/" +
                        weatherData.getWeather().get(0).getIcon() + "@2x.png")
                .override(100, 100)
                .into(ivWeatherIcon);
        LAVMainWeather.setAnimation(Utils.getAnimAccordingToStatus(weatherData.getWeather().get(0).getMain()));
        LAVMainWeather.playAnimation();
        LAVMainWeather.setRepeatCount(9999999);
        tvTemp.setText(String.format("%s°C", Math.round(weatherData.getMain().getTemp())));
        tvMaxTemp.setText(String.format("Max Temp %s°C", Math.round(weatherData.getMain().getTempMax())));
        tvMinTemp.setText(String.format("Min Temp %s°C", Math.round(weatherData.getMain().getTempMin())));
        tvHumidity.setText(String.format("%s%%", weatherData.getMain().getHumidity()));
        tvPressure.setText(String.format("%s Pa", weatherData.getMain().getPressure()));
        tvSunrise.setText(Utils.convertUnixToHour(weatherData.getSys().getSunrise(), false));
        tvSunset.setText(Utils.convertUnixToHour(weatherData.getSys().getSunset(), true));
        tvLocation.setText(weatherData.getName());
        tvStatus.setText(weatherData.getWeather().get(0).getMain());
        tvUpdateAt.setText(String.format("Last Updated %s", Utils.convertUnixToHour(weatherData.getDt(), true)));
        tvWind.setText(String.format("%s Km/h", Math.round(weatherData.getWind().getSpeed())));
        tvFeelsLike.setText(String.format("Feels Like %s°C", Math.round(weatherData.getMain().getFeelsLike())));

        if (installationId != null)
            postLogs(weatherData);
        else
            FirebaseInstallations.getInstance().getId()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            installationId = task.getResult();
                            postLogs(weatherData);
                            Log.d("Installations", "Installation ID: " + task.getResult());
                        } else Log.e("Installations", "Unable to get Installation ID");
                    });
    }

    void postLogs(WeatherData weatherData) {
        Logs logs = new Logs(String.format("%s°C", Math.round(weatherData.getMain().getTemp())), weatherData.getName(),
                Utils.convertUnixToHour(weatherData.getDt(), true));
        databaseReference.child("Logs")
                .child(installationId)
                .child(Utils.convertUnixToHour(weatherData.getDt(), true))
                .setValue(logs);
        progressBar.setVisibility(View.GONE);
    }

}