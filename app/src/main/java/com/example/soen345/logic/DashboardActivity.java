package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.soen345.R;
import com.example.soen345.service.UserSearchEventService;
import com.example.soen345.service.UserSession;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView rvDashboardEvents;
    private EventAdapter adapter;
    private UserSearchEventService searchService;
    private TextView seeAllText, chipAll, chipConcerts, chipSports;
    private FrameLayout navManageEventsContainer;
    private ImageView navTickets, navProfile, navHome, navManageEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize the new Service
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        searchService = new UserSearchEventService(firestore);

        initViews();
        setupRecyclerView();
        setupFilters();
        setupNavigation();

        // Initial Load: Fetch all events for the dashboard
        loadDashboardData(null);
    }

    private void initViews() {
        rvDashboardEvents = findViewById(R.id.rvDashboardEvents);
        seeAllText = findViewById(R.id.seeAllText);

        // Filter Chips
        chipAll = findViewById(R.id.chipAll);
        chipConcerts = findViewById(R.id.chipConcerts);
        chipSports = findViewById(R.id.chipSports);

        // Navigation Icons
        navHome = findViewById(R.id.navHome);
        navTickets = findViewById(R.id.navTickets);
        navProfile = findViewById(R.id.navProfile);
        navManageEvents = findViewById(R.id.navManageEvents);
        navManageEventsContainer = findViewById(R.id.navManageEventsContainer);

        // Hide managed if user is customer
        if ("customer".equals(UserSession.getInstance().getUser().role)) {
            navManageEventsContainer.setVisibility(View.GONE);
        } else {
            navManageEventsContainer.setVisibility(View.VISIBLE);
        }
    }

    private void setupRecyclerView() {
        rvDashboardEvents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(new ArrayList<>(), event -> {
            Intent intent = new Intent(this, EventDetailsActivity.class);
            // This eventId is now automatically pulled via @DocumentId in your Event class
            intent.putExtra("EVENT_ID", event.eventId);
            startActivity(intent);
        });
        rvDashboardEvents.setAdapter(adapter);
    }

    private void setupFilters() {
        // "See All" takes you to the AllEventsActivity
        seeAllText.setOnClickListener(v -> {
            startActivity(new Intent(this, AllEventsActivity.class));
        });

        // Chip: All
        chipAll.setOnClickListener(v -> {
            updateChipUI(chipAll);
            loadDashboardData(null);
        });

        // Chip: Concerts
        chipConcerts.setOnClickListener(v -> {
            updateChipUI(chipConcerts);
            loadDashboardData("Concerts");
        });

        // Chip: Sports
        chipSports.setOnClickListener(v -> {
            updateChipUI(chipSports);
            loadDashboardData("Sports");
        });
    }

    private void loadDashboardData(String category) {
        // Pass the category to the service to filter the list dynamically
        searchService.getEvents(category, null, null,
                list -> {
                    if (list != null) {
                        adapter.updateData(list);
                    }
                },
                e -> Toast.makeText(this, "Error syncing events", Toast.LENGTH_SHORT).show()
        );
    }

    private void updateChipUI(TextView selectedChip) {
        // Reset all chips to unselected style
        chipAll.setBackgroundResource(R.drawable.chip_unselected_bg);
        chipAll.setTextColor(getResources().getColor(android.R.color.darker_gray));

        chipConcerts.setBackgroundResource(R.drawable.chip_unselected_bg);
        chipConcerts.setTextColor(getResources().getColor(android.R.color.darker_gray));

        chipSports.setBackgroundResource(R.drawable.chip_unselected_bg);
        chipSports.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // Apply selected style to the clicked chip
        selectedChip.setBackgroundResource(R.drawable.chip_selected_bg);
        selectedChip.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> loadDashboardData(null));
        navTickets.setOnClickListener(v -> startActivity(new Intent(this, RegisteredEventsActivity.class)));
        navManageEvents.setOnClickListener(v-> startActivity(new Intent(this, AdminManageEventsActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }
}