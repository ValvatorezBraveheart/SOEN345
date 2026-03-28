package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.R;
import com.example.soen345.service.UserSession;

public class RegisteredEventsActivity extends AppCompatActivity {

    private ImageView navHome;
    private ImageView navRegisteredEvents;
    private ImageView navProfile;
    private ImageView navManageEvents;
    private ImageView filterIcon;
    private FrameLayout navManageEventsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_events);

        initViews();
        setupBottomNavigation();
        setupTopActions();
        setupEventCardActions();
    }

    private void initViews() {
        navHome = findViewById(R.id.navHome);
        navRegisteredEvents = findViewById(R.id.navRegisteredEvents);
        navManageEvents = findViewById(R.id.navManageEvents);
        navManageEventsContainer = findViewById(R.id.navManageEventsContainer);

        if ("customer".equals(UserSession.getInstance().getUser().role)) {
            navManageEventsContainer.setVisibility(View.GONE);
        } else {
            navManageEventsContainer.setVisibility(View.VISIBLE);
        }


        navProfile = findViewById(R.id.navProfile);
        filterIcon = findViewById(R.id.filterIcon);

    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(RegisteredEventsActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        navRegisteredEvents.setOnClickListener(v -> {
            // Already on this page
        });
        navManageEvents.setOnClickListener(v->{
            Intent intent = new Intent(RegisteredEventsActivity.this, AdminManageEventsActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(RegisteredEventsActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupTopActions() {
        filterIcon.setOnClickListener(v -> {
            Intent intent = new Intent(RegisteredEventsActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }

    private void setupEventCardActions() {


    }
}