package com.example.final30.models.openweather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Error {


    @SerializedName("cod")
    @Expose
    private String cod;
    @SerializedName("message")
    @Expose
    private String message;

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
