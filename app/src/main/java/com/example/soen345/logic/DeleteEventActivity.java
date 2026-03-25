package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.R;
import com.google.android.material.button.MaterialButton;

public class DeleteEventActivity extends AppCompatActivity {

    private ImageView backButton;

    private TextView eventDateDay;
    private TextView eventDateMonth;
    private TextView eventTitle;
    private TextView eventLocation;
    private TextView eventStatus;

    private MaterialButton confirmDeleteButton;
    private MaterialButton keepEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_event);

        initViews();
        loadEventData();
        setupActions();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);

        eventDateDay = findViewById(R.id.eventDateDay);
        eventDateMonth = findViewById(R.id.eventDateMonth);
        eventTitle = findViewById(R.id.eventTitle);
        eventLocation = findViewById(R.id.eventLocation);
        eventStatus = findViewById(R.id.eventStatus);

        confirmDeleteButton = findViewById(R.id.confirmDeleteButton);
        keepEventButton = findViewById(R.id.keepEventButton);
    }

    private void loadEventData() {
        Intent intent = getIntent();

        String day = intent.getStringExtra("event_day");
        String month = intent.getStringExtra("event_month");
        String title = intent.getStringExtra("event_title");
        String location = intent.getStringExtra("event_location");
        String status = intent.getStringExtra("event_status");

        if (day == null) day = "24";
        if (month == null) month = "SEP";
        if (title == null) title = "Summer Music Festival";
        if (location == null) location = "Bell Centre, Montreal";
        if (status == null) status = "Published";

        eventDateDay.setText(day);
        eventDateMonth.setText(month);
        eventTitle.setText(title);
        eventLocation.setText(location);
        eventStatus.setText(status);
    }

    private void setupActions() {
        backButton.setOnClickListener(v -> finish());

        keepEventButton.setOnClickListener(v -> finish());

        confirmDeleteButton.setOnClickListener(v -> {
            Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(DeleteEventActivity.this, ManageEventsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}