package com.example.final30.models;

public class RouteData {

    public RouteData(String temperature, String city, String status) {
        this.temperature = temperature;
        this.city = city;
        this.status = status;
    }

    public RouteData(String temperature, String city, String status, String pressure, String humidity) {
        this.temperature = temperature;
        this.city = city;
        this.status = status;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public RouteData(String temperature, String city, String status, String pressure,
                     String humidity, String feelsLike, String wind) {
        this.temperature = temperature;
        this.city = city;
        this.status = status;
        this.pressure = pressure;
        this.humidity = humidity;
        this.feelsLike = feelsLike;
        this.wind = wind;
    }

    public String getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(String feelsLike) {
        this.feelsLike = feelsLike;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    private String temperature;
    private String city;
    private String status;
    private String pressure;
    private String humidity;
    private String feelsLike;
    private String wind;


    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
