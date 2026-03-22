package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.soen345.R;

public class AllEventsActivity extends AppCompatActivity {

    private ImageView navHome;
    private ImageView navTickets;
    private ImageView navProfile;
    private ImageView searchIcon;

    private TextView chipAll;
    private TextView chipConcerts;
    private TextView chipSports;
    private TextView chipTravel;
    private TextView chipTheater;

    private CardView eventCard1;
    private CardView eventCard2;
    private CardView eventCard3;
    private CardView eventCard4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events);

        initViews();
        setupBottomNavigation();
        setupTopActions();
        setupChipSelection();
        setupEventCards();
    }

    private void initViews() {
        navHome = findViewById(R.id.navHome);
        navTickets = findViewById(R.id.navTickets);
        navProfile = findViewById(R.id.navProfile);
        searchIcon = findViewById(R.id.searchIcon);

        chipAll = findViewById(R.id.chipAll);
        chipConcerts = findViewById(R.id.chipConcerts);
        chipSports = findViewById(R.id.chipSports);
        chipTravel = findViewById(R.id.chipTravel);
        chipTheater = findViewById(R.id.chipTheater);

        eventCard1 = findViewById(R.id.eventCard1);
        eventCard2 = findViewById(R.id.eventCard2);
        eventCard3 = findViewById(R.id.eventCard3);
        eventCard4 = findViewById(R.id.eventCard4);
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(AllEventsActivity.this, CustomerDashboardActivity.class);
            startActivity(intent);
            finish();
        });

        navTickets.setOnClickListener(v -> {
            Intent intent = new Intent(AllEventsActivity.this, RegisteredEventsActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(AllEventsActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupTopActions() {
        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(AllEventsActivity.this, SearchActivity.class);
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

    private void setupEventCards() {
        eventCard1.setOnClickListener(v -> {
            Intent intent = new Intent(AllEventsActivity.this, EventDetailsActivity.class);
            startActivity(intent);
        });

        eventCard2.setOnClickListener(v ->{
                    Intent intent = new Intent(AllEventsActivity.this, EventDetailsActivity.class);
                    startActivity(intent);
                }
        );

        eventCard3.setOnClickListener(v ->{
                    Intent intent = new Intent(AllEventsActivity.this, EventDetailsActivity.class);
                    startActivity(intent);
                }
        );

        eventCard4.setOnClickListener(v ->
                {
                    Intent intent = new Intent(AllEventsActivity.this, EventDetailsActivity.class);
                    startActivity(intent);
                }
        );
    }
}