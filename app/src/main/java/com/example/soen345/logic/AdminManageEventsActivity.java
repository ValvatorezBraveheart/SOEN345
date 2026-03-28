package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminManageEventsActivity extends AppCompatActivity {

    private ImageView navHome;
    private ImageView navTickets;
    private ImageView navProfile;
    private ImageView navManageEvents;
    private ImageView searchIcon;
    private MaterialButton addEventButton;
    private EventRepository eventRepository;

    private TextView chipAll;
    private TextView chipPublished;
    private TextView chipCancelled;
    private RecyclerView rvEvents;

    private EventAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);

        initViews();
        setupBottomNavigation();
        setupTopActions();
        setupChipSelection();
        setupButtonActions();
        setupRecyclerView();
    }

    private void initViews() {
        navHome = findViewById(R.id.navHome);
        navTickets = findViewById(R.id.navTickets);
        navProfile = findViewById(R.id.navProfile);
        navManageEvents = findViewById(R.id.navManageEvents);
        searchIcon = findViewById(R.id.searchIcon);

        addEventButton = findViewById(R.id.addEventButton);

        chipAll = findViewById(R.id.chipAll);
        chipPublished = findViewById(R.id.chipPublished);
        chipCancelled = findViewById(R.id.chipCancelled);
        rvEvents = findViewById(R.id.rvEvents);
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(AdminManageEventsActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
            finish();
        });

        navTickets.setOnClickListener(v -> {
            Intent intent = new Intent(AdminManageEventsActivity.this, RegisteredEventsActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(AdminManageEventsActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        navManageEvents.setOnClickListener(v -> {
            // Already on this page
        });
    }

    private void setupTopActions() {
        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(AdminManageEventsActivity.this, SearchActivity.class);
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


    private void setupButtonActions() {
        addEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminManageEventsActivity.this, AdminAddEventActivity.class);
            startActivity(intent);
        });
    }
    public void setupRecyclerView(){

        rvEvents.setLayoutManager(new LinearLayoutManager(this));

        // FIXED: The constructor now correctly matches the standalone EventAdapter
        adapter = new EventAdapter(new ArrayList<>(), event -> {
            Intent intent = new Intent(AdminManageEventsActivity.this, AdminEditEventActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });

        rvEvents.setAdapter(adapter);

        eventRepository = new EventRepository(FirebaseFirestore.getInstance());
        loadEvents();
    }

    private void loadEvents() {
        String userId = UserSession.getInstance().getUser().userId;

        eventRepository.fetchEventsCreatedByUser(userId, new EventServiceInterface.EventCallback() {
            @Override
            public void onCallback(List<Event> events) {
                adapter.updateData(events);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminManageEventsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}