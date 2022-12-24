package com.example.final30;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String convertUnixToHour(int timeInUnix, boolean is24HoursTime) {
        String pattern;
        if (is24HoursTime)
            pattern = "HH:mm";
        else
            pattern = "HH:mm a";
        Date date = new Date(timeInUnix * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        if (is24HoursTime)
            return convertTo12Hours(sdf.format(date));
        else
            return sdf.format(date);
    }

    public static String convertTo12Hours(String time) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
                .format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showInternetDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle("No Internet")
                .setMessage("Please check your internet connection")
                .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }

}
