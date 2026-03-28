package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
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

public class CustomerDashboardActivity extends AppCompatActivity {

    private RecyclerView rvDashboardEvents;
    private EventAdapter adapter;
    // Switch from Interface to the new Service
    private UserSearchEventService searchService;
    private TextView seeAllText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        // Initialize the new Service
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        searchService = new UserSearchEventService(firestore);

        initViews();
        setupRecyclerView();
        loadDashboardData();

        seeAllText.setOnClickListener(v -> {
            startActivity(new Intent(this, AllEventsActivity.class));
        });
    }

    private void initViews() {
        rvDashboardEvents = findViewById(R.id.rvDashboardEvents);
        seeAllText = findViewById(R.id.seeAllText);
    }

    private void setupRecyclerView() {
        rvDashboardEvents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(new ArrayList<>(), event -> {
            Intent intent = new Intent(this, EventDetailsActivity.class);
            intent.putExtra("EVENT_ID", event.eventId);
            startActivity(intent);
        });
        rvDashboardEvents.setAdapter(adapter);
    }

    private void loadDashboardData() {
        // Pass nulls to get all events (the "Dashboard" view)
        searchService.getEvents(null, null, null,
                list -> {
                    if (list != null && !list.isEmpty()) {
                        adapter.updateData(list);
                    }
                },
                e -> Toast.makeText(this, "Error syncing events", Toast.LENGTH_SHORT).show()
        );
    }
}