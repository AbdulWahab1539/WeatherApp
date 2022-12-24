package com.example.final30;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.final30.models.Logs;
import com.example.final30.models.WeatherData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.installations.FirebaseInstallations;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etCity;
    Button btnSearch;
    String city = "abbottabad";
    ApiCalls apiCalls;
    TextView tvTemp, tvLocation, tvMinTemp, tvMaxTemp, tvPressure,
            tvWind, tvHumidity, tvSunset, tvSunrise, tvStatus, tvUpdateAt, tvFeelsLike;
    ImageView ivWeatherIcon;
    LinearLayout btnLogs;
    DatabaseReference databaseReference;
    static String installationId;
    FrameLayout progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        etCity = findViewById(R.id.et_city);
        btnSearch = findViewById(R.id.btn_search);
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