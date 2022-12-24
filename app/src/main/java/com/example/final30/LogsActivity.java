package com.example.final30;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.final30.models.Logs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LogsActivity extends AppCompatActivity {

    List<Logs> logsList;
    RelativeLayout rlNoLogs;
    RecyclerView rvLogs;
    FrameLayout loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        if (getSupportActionBar() == null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.setTitle("Logs Activity");
        rvLogs = findViewById(R.id.rv_logs);
        rlNoLogs = findViewById(R.id.no_logs_layout);
        loader = findViewById(R.id.loader);
        logsList = new ArrayList<>();

        if (DashboardActivity.installationId == null)
            configureAdapter();
        else if (Utils.isNetworkAvailable(this)) getLogsList();
        else
            Utils.showInternetDialog(this);

    }

    private void getLogsList() {
        loader.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference("Logs").child(DashboardActivity.installationId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot innerData : snapshot.getChildren()) {
                            logsList.add(innerData.getValue(Logs.class));
                        }
                        Log.i("mytag", "onDataChange: " + logsList.size());
                        configureAdapter();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LogsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        loader.setVisibility(View.GONE);
                        configureAdapter();
                    }
                });
    }


    public void configureAdapter() {
        if (!logsList.isEmpty())
            rlNoLogs.setVisibility(View.GONE);
        else
            rlNoLogs.setVisibility(View.VISIBLE);
        initAdapter();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initAdapter() {
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, logsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvLogs.setLayoutManager(linearLayoutManager);
        rvLogs.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
        loader.setVisibility(View.GONE);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}