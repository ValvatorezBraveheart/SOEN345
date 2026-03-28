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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CustomerDashboardActivity extends AppCompatActivity {

    private RecyclerView rvDashboardEvents;
    private EventAdapter adapter;
    private EventServiceInterface eventService;
    private TextView seeAllText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        eventService = new EventRepository(firestore);

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

        // Use the same Adapter pattern to ensure consistency
        adapter = new EventAdapter(new ArrayList<>(), event -> {
            Intent intent = new Intent(this, EventDetailsActivity.class);
            intent.putExtra("EVENT_ID", event.eventId);
            startActivity(intent);
        });

        rvDashboardEvents.setAdapter(adapter);
    }

    private void loadDashboardData() {
        eventService.fetchAllEvents(new EventServiceInterface.EventCallback() {
            @Override
            public void onCallback(List<Event> list) {
                android.util.Log.d("DEBUG_DATA", "Events received: " + list.size());
                if (list != null && !list.isEmpty()) {
                    adapter.updateData(list);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(CustomerDashboardActivity.this, "Error syncing events", Toast.LENGTH_SHORT).show();
            }
        });
    }
}