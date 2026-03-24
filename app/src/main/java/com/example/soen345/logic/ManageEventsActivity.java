package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.soen345.R;
import com.google.android.material.button.MaterialButton;

public class ManageEventsActivity extends AppCompatActivity {

    private ImageView navHome;
    private ImageView navTickets;
    private ImageView navProfile;
    private ImageView navManageEvents;
    private ImageView searchIcon;

    private MaterialButton addEventButton;
    private MaterialButton editEventButton1;
    private MaterialButton cancelEventButton1;
    private MaterialButton editEventButton2;
    private MaterialButton cancelEventButton2;

    private CardView manageEventCard1;
    private CardView manageEventCard2;

    private TextView chipAll;
    private TextView chipPublished;
    private TextView chipCancelled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);

        initViews();
        setupBottomNavigation();
        setupTopActions();
        setupChipSelection();
        setupCardActions();
        setupButtonActions();
    }

    private void initViews() {
        navHome = findViewById(R.id.navHome);
        navTickets = findViewById(R.id.navTickets);
        navProfile = findViewById(R.id.navProfile);
        navManageEvents = findViewById(R.id.navManageEvents);
        searchIcon = findViewById(R.id.searchIcon);

        addEventButton = findViewById(R.id.addEventButton);
        editEventButton1 = findViewById(R.id.editEventButton1);
        cancelEventButton1 = findViewById(R.id.cancelEventButton1);
        editEventButton2 = findViewById(R.id.editEventButton2);
        cancelEventButton2 = findViewById(R.id.cancelEventButton2);

        manageEventCard1 = findViewById(R.id.manageEventCard1);
        manageEventCard2 = findViewById(R.id.manageEventCard2);

        chipAll = findViewById(R.id.chipAll);
        chipPublished = findViewById(R.id.chipPublished);
        chipCancelled = findViewById(R.id.chipCancelled);
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventsActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
            finish();
        });

        navTickets.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventsActivity.this, RegisteredEventsActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventsActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        navManageEvents.setOnClickListener(v -> {
            // Already on this page
        });
    }

    private void setupTopActions() {
        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventsActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }

    private void setupChipSelection() {
        chipAll.setOnClickListener(v -> selectChip(chipAll));
        chipPublished.setOnClickListener(v -> selectChip(chipPublished));
        chipCancelled.setOnClickListener(v -> selectChip(chipCancelled));
    }

    private void selectChip(TextView selectedChip) {
        resetChip(chipAll);
        resetChip(chipPublished);
        resetChip(chipCancelled);

        selectedChip.setBackgroundResource(R.drawable.chip_selected_bg);
        selectedChip.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void resetChip(TextView chip) {
        chip.setBackgroundResource(R.drawable.chip_unselected_bg);
        chip.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void setupCardActions() {
        manageEventCard1.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventsActivity.this, EventDetailsActivity.class);
            startActivity(intent);
        });

        manageEventCard2.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventsActivity.this, EventDetailsActivity.class);
            startActivity(intent);
        });
    }

    private void setupButtonActions() {
        addEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventsActivity.this, AddEventActivity.class);
            startActivity(intent);
        });

        editEventButton1.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventsActivity.this, EditEventActivity.class);
            startActivity(intent);
        });

        cancelEventButton1.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventsActivity.this, DeleteEventActivity.class);
            startActivity(intent);
        });

        editEventButton2.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventsActivity.this, EditEventActivity.class);
            startActivity(intent);
        });

        cancelEventButton2.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventsActivity.this, DeleteEventActivity.class);
            startActivity(intent);
        });
    }
}