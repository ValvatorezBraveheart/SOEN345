package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soen345.Event;
import com.example.soen345.R;
import com.example.soen345.service.EventServiceInterface;
import com.example.soen345.service.UserSearchEventService;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AllEventsActivity extends AppCompatActivity {

    private ImageView navHome, navTickets, navProfile, searchIcon;

    private UserSearchEventService searchService;
    private TextView chipAll, chipConcerts, chipSports, chipTheater;

    private RecyclerView rvEvents;
    // FIXED: Use the standalone EventAdapter, not the one inside ReservedEventDetailsActivity
    private EventAdapter adapter;

    private EventServiceInterface eventService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events);

        initViews();
        setupBottomNavigation();
        setupTopActions();
        setupChipSelection();
        setupRecyclerView();

        loadEvents("All");
    }

    private void initViews() {
        navHome = findViewById(R.id.navHome);
        navTickets = findViewById(R.id.navTickets);
        navProfile = findViewById(R.id.navProfile);
        searchIcon = findViewById(R.id.searchIcon);

        chipAll = findViewById(R.id.chipAll);
        chipConcerts = findViewById(R.id.chipConcerts);
        chipSports = findViewById(R.id.chipSports);
        chipTheater = findViewById(R.id.chipTheater);

        rvEvents = findViewById(R.id.rvEvents);
    }

    private void setupRecyclerView() {
        rvEvents.setLayoutManager(new LinearLayoutManager(this));

        // FIXED: The constructor now correctly matches the standalone EventAdapter
        adapter = new EventAdapter(new ArrayList<>(), event -> {
            Intent intent = new Intent(AllEventsActivity.this, EventDetailsActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });

        rvEvents.setAdapter(adapter);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        searchService = new UserSearchEventService(firestore);    }

    private void loadEvents(String category) {
        String filterCategory = category.equals("All") ? null : category;

        // Use the class-level searchService variable
        searchService.getEvents(filterCategory, null, null,
                new UserSearchEventService.EventSearchCallback() {
                    @Override
                    public void onSuccess(List<Event> events) {
                        adapter.updateData(events);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(AllEventsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupChipSelection() {
        chipAll.setOnClickListener(v -> { selectChip(chipAll); loadEvents("All"); });
        chipConcerts.setOnClickListener(v -> { selectChip(chipConcerts); loadEvents("Concerts"); });
        chipSports.setOnClickListener(v -> { selectChip(chipSports); loadEvents("Sports"); });
        chipTheater.setOnClickListener(v -> { selectChip(chipTheater); loadEvents("Theater"); });
    }

    private void selectChip(TextView selectedChip) {
        resetChip(chipAll);
        resetChip(chipConcerts);
        resetChip(chipSports);
        resetChip(chipTheater);

        selectedChip.setBackgroundResource(R.drawable.chip_selected_bg);
        selectedChip.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void resetChip(TextView chip) {
        chip.setBackgroundResource(R.drawable.chip_unselected_bg);
        chip.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        });
        navTickets.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisteredEventsActivity.class));
            finish();
        });
        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }

    private void setupTopActions() {
        searchIcon.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
    }
}