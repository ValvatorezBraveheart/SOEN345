package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.soen345.R;

public class RegisteredEventsActivity extends AppCompatActivity {

    private ImageView navHome;
    private ImageView navRegisteredEvents;
    private ImageView navProfile;
    private ImageView filterIcon;
    private CardView upcomingEventCard1;
    private CardView upcomingEventCard2;
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
        navProfile = findViewById(R.id.navProfile);
        filterIcon = findViewById(R.id.filterIcon);

        upcomingEventCard1 = findViewById(R.id.upcomingEventCard1);
        upcomingEventCard2 = findViewById(R.id.upcomingEventCard2);

    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(RegisteredEventsActivity.this, CustomerDashboardActivity.class);
            startActivity(intent);
            finish();
        });

        navRegisteredEvents.setOnClickListener(v -> {
            // Already on this page
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
        upcomingEventCard1.setOnClickListener(v -> {
            Intent intent = new Intent(RegisteredEventsActivity.this, ReservedEventDetailsActivity.class);
            intent.putExtra("event_day", "24");
            intent.putExtra("event_month", "SEP");
            intent.putExtra("event_title", "Summer Music Festival");
            intent.putExtra("event_organizer", "Velox Productions");
            intent.putExtra("ticket_status", "Reserved");
            intent.putExtra("event_date_full", "24 Sept 2026");
            intent.putExtra("event_time", "7:00 PM");
            intent.putExtra("event_location", "Bell Centre, Montreal");
            intent.putExtra("event_category", "Concerts");
            intent.putExtra("reservation_id", "VELX-2026-00124");
            intent.putExtra("seat_info", "Section A • Seat 14");
            intent.putExtra("amount_paid", "$45.00");
            startActivity(intent);
        });

        upcomingEventCard2.setOnClickListener(v -> {
            Intent intent = new Intent(RegisteredEventsActivity.this, ReservedEventDetailsActivity.class);
            intent.putExtra("event_day", "29");
            intent.putExtra("event_month", "SEP");
            intent.putExtra("event_title", "Spring Art Showcase");
            intent.putExtra("event_organizer", "Visionary Arts Group");
            intent.putExtra("ticket_status", "Reserved");
            intent.putExtra("event_date_full", "29 Sept 2026");
            intent.putExtra("event_time", "3:30 PM");
            intent.putExtra("event_location", "Place des Arts, Montreal");
            intent.putExtra("event_category", "Theater");
            intent.putExtra("reservation_id", "VELX-2026-00129");
            intent.putExtra("seat_info", "Section B • Seat 08");
            intent.putExtra("amount_paid", "$35.00");
            startActivity(intent);
        });


    }
}