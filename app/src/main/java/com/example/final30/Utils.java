package com.example.final30;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Window;

import com.google.android.material.dialog.InsetDialogOnTouchListener;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {
    static  String TAG = "myta";

    public static Dialog getLoading(Context context) {
        Log.i(TAG, "getLoading: ");
        Dialog customDialog = new Dialog(context);
        customDialog.setContentView(R.layout.loading);
        customDialog.setCancelable(false);
        Window window = customDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return customDialog;
    }

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
