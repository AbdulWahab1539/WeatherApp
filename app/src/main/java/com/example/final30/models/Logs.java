package com.example.final30.models;

public class Logs {
    String temp;
    String location;
    String timestamp;

    public Logs() {
    }

    public Logs(String temp, String location, String timestamp) {
        this.temp = temp;
        this.location = location;
        this.timestamp = timestamp;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
