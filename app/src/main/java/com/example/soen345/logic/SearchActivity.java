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
    private ImageView searchButton;

    private EditText searchEditText;
    private EditText locationEditText;

    private CardView dateFilterCard;
    private CardView locationInputCard;
    private CardView searchResultCard1;
    private CardView searchResultCard2;

    private TextView dateFilterText;

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
        searchButton = findViewById(R.id.searchPageIcon);

        searchEditText = findViewById(R.id.searchEditText);
        locationEditText = findViewById(R.id.locationEditText);

        dateFilterCard = findViewById(R.id.dateFilterCard);
        locationInputCard = findViewById(R.id.locationInputCard);
        searchResultCard1 = findViewById(R.id.searchResultCard1);
        searchResultCard2 = findViewById(R.id.searchResultCard2);

        dateFilterText = findViewById(R.id.dateFilterText);

        chipAllSearch = findViewById(R.id.chipAllSearch);
        chipConcertsSearch = findViewById(R.id.chipConcertsSearch);
        chipSportsSearch = findViewById(R.id.chipSportsSearch);
        chipTheaterSearch = findViewById(R.id.chipTheaterSearch);
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {

            startActivity(new Intent(SearchActivity.this, CustomerDashboardActivity.class));
            finish();
        });

        navTickets.setOnClickListener(v -> {
            startActivity(new Intent(SearchActivity.this, RegisteredEventsActivity.class));
            finish();
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
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
        searchButton.setOnClickListener(v -> performSearch());

        dateFilterCard.setOnClickListener(v -> {
            dateFilterText.setText("24 Sept");
            Toast.makeText(this, "Date filter selected", Toast.LENGTH_SHORT).show();
        });

        locationInputCard.setOnClickListener(v -> locationEditText.requestFocus());
    }

    private void performSearch() {
        String keyword = searchEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String selectedCategory = getSelectedCategory();

        String message = "Searching";

        if (!keyword.isEmpty()) {
            message += ": " + keyword;
        }

        if (!location.isEmpty()) {
            message += " in " + location;
        }

        if (!selectedCategory.equals("All Events")) {
            message += " [" + selectedCategory + "]";
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String getSelectedCategory() {
        int selectedColor = getResources().getColor(android.R.color.white);

        if (chipConcertsSearch.getCurrentTextColor() == selectedColor) {
            return "Concerts";
        } else if (chipSportsSearch.getCurrentTextColor() == selectedColor) {
            return "Sports";
        } else if (chipTheaterSearch.getCurrentTextColor() == selectedColor) {
            return "Theater";
        } else {
            return "All Events";
        }
    }

    private void setupResultCards() {
        searchResultCard1.setOnClickListener(v ->
                startActivity(new Intent(SearchActivity.this, EventDetailsActivity.class)));

        searchResultCard2.setOnClickListener(v ->
                startActivity(new Intent(SearchActivity.this, EventDetailsActivity.class)));
    }
}