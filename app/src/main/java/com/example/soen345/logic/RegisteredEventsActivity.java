package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soen345.Event;
import com.example.soen345.R;
import com.example.soen345.service.EventServiceInterface;
import com.example.soen345.service.UserSession;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RegisteredEventsActivity extends AppCompatActivity {

    private ImageView navHome;
    private ImageView navRegisteredEvents;
    private ImageView navProfile;
    private ImageView navManageEvents;
    private ImageView filterIcon;
    private FrameLayout navManageEventsContainer;

    private RecyclerView rvEvents;
    // FIXED: Use the standalone EventAdapter, not the one inside ReservedEventDetailsActivity
    private EventAdapter adapter;
    private EventRepository eventRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_events);

        initViews();
        setupBottomNavigation();
        setupTopActions();
        setupRecyclerView();
    }

    private void initViews() {
        navHome = findViewById(R.id.navHome);
        navRegisteredEvents = findViewById(R.id.navRegisteredEvents);
        navManageEvents = findViewById(R.id.navManageEvents);
        navManageEventsContainer = findViewById(R.id.navManageEventsContainer);

        if ("customer".equals(UserSession.getInstance().getUser().role)) {
            navManageEventsContainer.setVisibility(View.GONE);
        } else {
            navManageEventsContainer.setVisibility(View.VISIBLE);
        }


        navProfile = findViewById(R.id.navProfile);
        filterIcon = findViewById(R.id.filterIcon);


        rvEvents = findViewById(R.id.rvEvents);
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(RegisteredEventsActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        navRegisteredEvents.setOnClickListener(v -> {
            // Already on this page
        });
        navManageEvents.setOnClickListener(v->{
            Intent intent = new Intent(RegisteredEventsActivity.this, AdminManageEventsActivity.class);
            startActivity(intent);
            finish();
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

    public void setupRecyclerView(){

        rvEvents.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EventAdapter(new ArrayList<>(), event -> {
            Intent intent = new Intent(RegisteredEventsActivity.this, ReservedEventDetailsActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });

        rvEvents.setAdapter(adapter);

        eventRepository = new EventRepository(FirebaseFirestore.getInstance());
        loadEvents();
    }

    private void loadEvents() {
        String userId = UserSession.getInstance().getUser().userId;

        eventRepository.fetchEventsReservedByUser(userId, new EventServiceInterface.EventCallback() {
            @Override
            public void onCallback(List<Event> events) {
                adapter.updateData(events);
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(RegisteredEventsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}