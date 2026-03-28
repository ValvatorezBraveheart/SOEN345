package com.example.soen345.logic;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soen345.Event;
import com.example.soen345.R;
import com.example.soen345.service.EventServiceInterface;
import com.example.soen345.service.UserSearchEventService;
import com.example.soen345.service.UserSession;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private ImageView navHome;
    private ImageView navTickets;
    private ImageView navProfile;
    private ImageView searchButton;

    private EditText searchEditText;
    private EditText locationEditText;

    private CardView dateFilterCard;
    private CardView locationInputCard;

    private TextView dateFilterText;

    private TextView chipAllSearch;
    private TextView chipConcertsSearch;
    private TextView chipSportsSearch;
    private TextView chipTheaterSearch;

    private EventRepository eventRepository;
    private RecyclerView rvEvents;
    private EventAdapter adapter;

    private String category;
    private String location;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupBottomNavigation();
        setupChipSelection();
        setupFilterActions();
        setupRecyclerView();
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

        dateFilterText = findViewById(R.id.dateFilterText);

        chipAllSearch = findViewById(R.id.chipAllSearch);
        chipConcertsSearch = findViewById(R.id.chipConcertsSearch);
        chipSportsSearch = findViewById(R.id.chipSportsSearch);
        chipTheaterSearch = findViewById(R.id.chipTheaterSearch);
        rvEvents = findViewById(R.id.rvEvents);

    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {

            startActivity(new Intent(SearchActivity.this, DashboardActivity.class));
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
        setupDatePicker();
        locationInputCard.setOnClickListener(v -> locationEditText.requestFocus());
    }

    private void performSearch() {
        location = locationEditText.getText().toString().trim();
        date = dateFilterText.getText().toString().trim();
        category = getSelectedCategory();

        Log.d("SearchActivity", "search terms:"+ location + date + category);
        loadEvents();
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
            return "";
        }
    }

    private void setupDatePicker() {
        dateFilterText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    SearchActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        String formattedDate = String.format(
                                Locale.getDefault(),
                                "%02d/%02d/%04d",
                                dayOfMonth,
                                month + 1,
                                year
                        );
                        dateFilterText.setText(formattedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.show();
        });
    }
    public void setupRecyclerView(){

        rvEvents.setLayoutManager(new LinearLayoutManager(this));

        // FIXED: The constructor now correctly matches the standalone EventAdapter
        adapter = new EventAdapter(new ArrayList<>(), event -> {
            Intent intent = new Intent(SearchActivity.this, EventDetailsActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });

        rvEvents.setAdapter(adapter);

        eventRepository = new EventRepository(FirebaseFirestore.getInstance());
        loadEvents();
    }

    private void loadEvents() {
        UserSearchEventService service = new UserSearchEventService(FirebaseFirestore.getInstance());

        service.getEvents(category, location, date, new UserSearchEventService.EventSearchCallback() {
                @Override
                public void onSuccess(List<Event> events) {
                    adapter.updateData(events);
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(SearchActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        );
    }


}