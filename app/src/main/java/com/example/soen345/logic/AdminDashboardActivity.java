package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.soen345.R;

public class AdminDashboardActivity extends AppCompatActivity {

    private ImageView navHome;
    private ImageView navTickets;
    private ImageView navProfile;
    private ImageView navManageEvents;
    private ImageView searchIcon;
    private ImageView profileImage;

    private CardView eventCard1;
    private CardView eventCard2;

    private TextView chipAll;
    private TextView chipConcerts;
    private TextView chipSports;
    private TextView chipTravel;
    private TextView chipTheater;
    private TextView seeAllText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupBottomNavigation();
        setupTopActions();
        setupEventCards();
        setupChipSelection();
        setupSeeAllAction();
    }

    private void initViews() {
        navHome = findViewById(R.id.navHome);
        navTickets = findViewById(R.id.navTickets);
        navProfile = findViewById(R.id.navProfile);
        navManageEvents = findViewById(R.id.navManageEvents);
        searchIcon = findViewById(R.id.searchIcon);
        profileImage = findViewById(R.id.profileImage);

        eventCard1 = findViewById(R.id.eventCard1);
        eventCard2 = findViewById(R.id.eventCard2);

        chipAll = findViewById(R.id.chipAll);
        chipConcerts = findViewById(R.id.chipConcerts);
        chipSports = findViewById(R.id.chipSports);
        chipTravel = findViewById(R.id.chipTravel);
        chipTheater = findViewById(R.id.chipTheater);

        seeAllText = findViewById(R.id.seeAllText);
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            // Already on admin dashboard
        });

        navTickets.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, RegisteredEventsActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        navManageEvents.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminManageEventsActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupTopActions() {
        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void setupEventCards() {
        eventCard1.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, EventDetailsActivity.class);
            startActivity(intent);
        });

        eventCard2.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, EventDetailsActivity.class);
            startActivity(intent);
        });
    }

    private void setupChipSelection() {
        chipAll.setOnClickListener(v -> selectChip(chipAll));
        chipConcerts.setOnClickListener(v -> selectChip(chipConcerts));
        chipSports.setOnClickListener(v -> selectChip(chipSports));
        chipTravel.setOnClickListener(v -> selectChip(chipTravel));
        chipTheater.setOnClickListener(v -> selectChip(chipTheater));
    }

    private void setupSeeAllAction() {
        seeAllText.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AllEventsActivity.class);
            startActivity(intent);
        });
    }

    private void selectChip(TextView selectedChip) {
        resetChip(chipAll);
        resetChip(chipConcerts);
        resetChip(chipSports);
        resetChip(chipTravel);
        resetChip(chipTheater);

        selectedChip.setBackgroundResource(R.drawable.chip_selected_bg);
        selectedChip.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void resetChip(TextView chip) {
        chip.setBackgroundResource(R.drawable.chip_unselected_bg);
        chip.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }
}