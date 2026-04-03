package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.Event;
import com.example.soen345.R;
import com.google.android.material.button.MaterialButton;

public class ReservedEventDetailsActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView eventTitle;
    private TextView eventOrganizer;
    private TextView ticketStatus;
    private TextView eventDateFull;
    private TextView eventTime;
    private TextView eventLocation;
    private TextView eventCategory;


    private Event event;

    private MaterialButton viewEventButton;
    private MaterialButton cancelReservationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved_event_details);
        event = getIntent().getParcelableExtra("event");
        initViews();
        loadReservationData();
        setupActions();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        eventTitle = findViewById(R.id.eventTitle);
        eventOrganizer = findViewById(R.id.eventOrganizer);
        ticketStatus = findViewById(R.id.ticketStatus);
        eventDateFull = findViewById(R.id.eventDateFull);
        eventTime = findViewById(R.id.eventTime);
        eventLocation = findViewById(R.id.eventLocation);
        eventCategory = findViewById(R.id.eventCategory);



        viewEventButton = findViewById(R.id.viewEventButton);
        cancelReservationButton = findViewById(R.id.cancelReservationButton);
    }

    private void loadReservationData() {
        if (event == null) return;

        eventTitle.setText(event.name);
        eventDateFull.setText(event.date);
        eventTime.setText(event.startTime);
        eventLocation.setText(event.location);
        eventCategory.setText(event.category);
        ticketStatus.setText("Reserved");

        eventOrganizer.setText(event.adminId);
    }

    private void setupActions() {
        backButton.setOnClickListener(v -> finish());

        viewEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReservedEventDetailsActivity.this, EventDetailsActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
            finish();
        });

        cancelReservationButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReservedEventDetailsActivity.this, CancelReservationActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
            finish();
        });
    }


}