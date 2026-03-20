package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.soen345.R;

public class SearchActivity extends AppCompatActivity {

    private ImageView navHome;
    private ImageView navTickets;
    private ImageView navProfile;
    private ImageView filterButton;

    private EditText searchEditText;

    private CardView dateFilterCard;
    private CardView locationFilterCard;
    private CardView searchResultCard1;
    private CardView searchResultCard2;

    private TextView dateFilterText;
    private TextView locationFilterText;

    private TextView chipAllSearch;
    private TextView chipConcertsSearch;
    private TextView chipSportsSearch;
    private TextView chipTheaterSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupBottomNavigation();
        setupChipSelection();
        setupFilterActions();
        setupResultCards();
    }

    private void initViews() {
        navHome = findViewById(R.id.navHome);
        navTickets = findViewById(R.id.navTickets);
        navProfile = findViewById(R.id.navProfile);
        filterButton = findViewById(R.id.searchPageIcon);

        searchEditText = findViewById(R.id.searchEditText);

        dateFilterCard = findViewById(R.id.dateFilterCard);
        locationFilterCard = findViewById(R.id.locationFilterCard);
        searchResultCard1 = findViewById(R.id.searchResultCard1);
        searchResultCard2 = findViewById(R.id.searchResultCard2);

        dateFilterText = findViewById(R.id.dateFilterText);
        locationFilterText = findViewById(R.id.locationFilterText);

        chipAllSearch = findViewById(R.id.chipAllSearch);
        chipConcertsSearch = findViewById(R.id.chipConcertsSearch);
        chipSportsSearch = findViewById(R.id.chipSportsSearch);
        chipTheaterSearch = findViewById(R.id.chipTheaterSearch);
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, CustomerDashboardActivity.class);
            startActivity(intent);
            finish();
        });

        navTickets.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, RegisteredEventsActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupChipSelection() {
        chipAllSearch.setOnClickListener(v -> selectChip(chipAllSearch));
        chipConcertsSearch.setOnClickListener(v -> selectChip(chipConcertsSearch));
        chipSportsSearch.setOnClickListener(v -> selectChip(chipSportsSearch));
        chipTheaterSearch.setOnClickListener(v -> selectChip(chipTheaterSearch));
    }

    private void selectChip(TextView selectedChip) {
        resetChip(chipAllSearch);
        resetChip(chipConcertsSearch);
        resetChip(chipSportsSearch);
        resetChip(chipTheaterSearch);

        selectedChip.setBackgroundResource(R.drawable.chip_selected_bg);
        selectedChip.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void resetChip(TextView chip) {
        chip.setBackgroundResource(R.drawable.chip_unselected_bg);
        chip.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void setupFilterActions() {
        filterButton.setOnClickListener(v -> {
            Toast.makeText(this, "Filter options coming soon", Toast.LENGTH_SHORT).show();
        });

        dateFilterCard.setOnClickListener(v -> {
            // Later you can replace this with a DatePickerDialog
            dateFilterText.setText("24 Sept");
            Toast.makeText(this, "Date filter selected", Toast.LENGTH_SHORT).show();
        });

        locationFilterCard.setOnClickListener(v -> {
            // Later you can replace this with a dialog/dropdown
            locationFilterText.setText("Quebec City");
            Toast.makeText(this, "Location filter selected", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupResultCards() {
        searchResultCard1.setOnClickListener(v -> {

                Intent intent = new Intent(SearchActivity.this, EventDetailsActivity.class);
                startActivity(intent);

        });

        searchResultCard2.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, EventDetailsActivity.class);
            startActivity(intent);
        });
    }
}