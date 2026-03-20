package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.R;
import com.google.android.material.button.MaterialButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

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
        Intent intent = getIntent();

        String day = intent.getStringExtra("event_day");
        String month = intent.getStringExtra("event_month");
        String organizer = intent.getStringExtra("event_organizer");
        String title = intent.getStringExtra("event_title");
        String category = intent.getStringExtra("event_category");
        String dateFull = intent.getStringExtra("event_date_full");
        String time = intent.getStringExtra("event_time");
        String location = intent.getStringExtra("event_location");
        String description = intent.getStringExtra("event_description");

        if (day == null) day = "24";
        if (month == null) month = "SEP";
        if (organizer == null) organizer = "Velox Productions";
        if (title == null) title = "Summer Music Festival";
        if (category == null) category = "Concerts";
        if (dateFull == null) dateFull = "24 Sept 2026";
        if (time == null) time = "7:00 PM";
        if (location == null) location = "Bell Centre, Montreal";
        if (description == null) {
            description = "Enjoy an unforgettable evening filled with live performances, vibrant stage production, and an energetic atmosphere in the heart of Montreal. Join music lovers from across the city for a night of entertainment, food, and celebration.";
        }

        eventDateDay.setText(day);
        eventDateMonth.setText(month);
        eventOrganizer.setText(organizer);
        eventTitle.setText(title);
        eventCategory.setText(category);
        eventDateFull.setText(dateFull);
        eventTime.setText(time);
        eventLocation.setText(location);
        eventDescription.setText(description);
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
            Intent intent = new Intent(EventDetailsActivity.this, ReserveEventActivity.class);
            intent.putExtra("event_day", eventDateDay.getText().toString());
            intent.putExtra("event_month", eventDateMonth.getText().toString());
            intent.putExtra("event_title", eventTitle.getText().toString());
            intent.putExtra("event_location", eventLocation.getText().toString());
            intent.putExtra("event_time", eventTime.getText().toString());
            startActivity(intent);
        });
    }
}