package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.Event;
import com.example.soen345.R;
import com.example.soen345.service.EventServiceInterface;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailsActivity extends AppCompatActivity {

    private ImageView backButton;
    private ImageView shareButton;

    private TextView eventDateDay;
    private TextView eventDateMonth;
    private TextView eventOrganizer;
    private TextView eventTitle;
    private TextView eventCategory;
    private TextView eventDateFull;
    private TextView eventTime;
    private TextView eventLocation;
    private TextView eventDescription;

    private MaterialButton reserveButton;

    // SOLID: Using the Interface, not the concrete implementation directly where possible
    private EventServiceInterface eventService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Dependency Injection (Manual)
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        eventService = new EventRepository(firestore);

        initViews();
        loadEventData();
        setupActions();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        shareButton = findViewById(R.id.shareButton);

        eventDateDay = findViewById(R.id.eventDateDay);
        eventDateMonth = findViewById(R.id.eventDateMonth);
        eventOrganizer = findViewById(R.id.eventOrganizer);
        eventTitle = findViewById(R.id.eventTitle);
        eventCategory = findViewById(R.id.eventCategory);
        eventDateFull = findViewById(R.id.eventDateFull);
        eventTime = findViewById(R.id.eventTime);
        eventLocation = findViewById(R.id.eventLocation);
        eventDescription = findViewById(R.id.eventDescription);

        reserveButton = findViewById(R.id.reserveButton);
    }

    private void loadEventData() {
        // Retrieve the ID passed from AllEventsActivity
        String eventId = getIntent().getStringExtra("EVENT_ID");

        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Error: Event ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch dynamic data from Firebase via Repository
        eventService.fetchEventById(eventId, new EventServiceInterface.EventDetailsCallback() {
            @Override
            public void onCallback(Event event) {
                if (event != null) {
                    populateUI(event);
                } else {
                    Toast.makeText(EventDetailsActivity.this, "Event not found in database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(EventDetailsActivity.this, "Failed to load: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUI(Event event) {
        // Mapping Firebase Event object to UI Elements
        eventTitle.setText(event.name);
        eventCategory.setText(event.category);
        eventLocation.setText(event.location);
        eventDescription.setText(event.description);
        eventDateFull.setText(event.date);

        // Handling the time range from your Event.java fields
        String timeDisplay = event.startTime + " - " + event.endTime;
        eventTime.setText(timeDisplay);

        // Organizer is dynamic from Firebase
        eventOrganizer.setText(event.adminId != null ? "Organizer ID: " + event.adminId : "Public Event");

        // UI Design: Extracting Day and Month for the circular date icon
        // This assumes 'date' format is something like "24 Sept 2026"
        if (event.date != null && event.date.contains(" ")) {
            String[] parts = event.date.split(" ");
            if (parts.length >= 2) {
                eventDateDay.setText(parts[0]);
                eventDateMonth.setText(parts[1].toUpperCase());
            }
        }
    }

    private void setupActions() {
        backButton.setOnClickListener(v -> finish());

        shareButton.setOnClickListener(v -> {
            String shareText = eventTitle.getText().toString() + "\n"
                    + eventDateFull.getText().toString() + " at " + eventTime.getText().toString() + "\n"
                    + eventLocation.getText().toString();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Share event via"));
        });

        reserveButton.setOnClickListener(v -> {
            // Pass the EVENT_ID to the next activity so it remains dynamic
            Intent intent = new Intent(EventDetailsActivity.this, ReserveEventActivity.class);
            intent.putExtra("EVENT_ID", getIntent().getStringExtra("EVENT_ID"));

            // Also passing display strings for immediate UI feedback in ReserveEventActivity
            intent.putExtra("event_title", eventTitle.getText().toString());
            intent.putExtra("event_location", eventLocation.getText().toString());
            intent.putExtra("event_time", eventTime.getText().toString());

            startActivity(intent);
        });
    }
}