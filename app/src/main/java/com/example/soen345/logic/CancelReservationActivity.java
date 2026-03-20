package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.R;
import com.google.android.material.button.MaterialButton;

public class CancelReservationActivity extends AppCompatActivity {

    private ImageView backButton;

    private TextView eventDateDay;
    private TextView eventDateMonth;
    private TextView eventTitle;
    private TextView eventLocation;
    private TextView reservationId;

    private AutoCompleteTextView cancelReasonAutoComplete;

    private MaterialButton confirmCancelButton;
    private MaterialButton keepReservationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_reservation);

        initViews();
        loadReservationData();
        setupReasonDropdown();
        setupActions();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);

        eventDateDay = findViewById(R.id.eventDateDay);
        eventDateMonth = findViewById(R.id.eventDateMonth);
        eventTitle = findViewById(R.id.eventTitle);
        eventLocation = findViewById(R.id.eventLocation);
        reservationId = findViewById(R.id.reservationId);



        cancelReasonAutoComplete = findViewById(R.id.cancelReasonAutoComplete);

        confirmCancelButton = findViewById(R.id.confirmCancelButton);
        keepReservationButton = findViewById(R.id.keepReservationButton);
    }

    private void loadReservationData() {
        Intent intent = getIntent();

        String day = intent.getStringExtra("event_day");
        String month = intent.getStringExtra("event_month");
        String title = intent.getStringExtra("event_title");
        String location = intent.getStringExtra("event_location");
        String reservation = intent.getStringExtra("reservation_id");


        if (day == null) day = "24";
        if (month == null) month = "SEP";
        if (title == null) title = "Summer Music Festival";
        if (location == null) location = "Bell Centre, Montreal";
        if (reservation == null) reservation = "Reservation ID: VELX-2026-00124";


        eventDateDay.setText(day);
        eventDateMonth.setText(month);
        eventTitle.setText(title);
        eventLocation.setText(location);
        reservationId.setText(reservation);

    }

    private void setupReasonDropdown() {
        String[] reasons = {
                "Change of plans",
                "Booked by mistake",
                "Found another event",
                "Price is too high",
                "Travel issue",
                "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                reasons
        );

        cancelReasonAutoComplete.setAdapter(adapter);
        cancelReasonAutoComplete.setOnClickListener(v -> cancelReasonAutoComplete.showDropDown());
    }

    private void setupActions() {
        backButton.setOnClickListener(v -> finish());

        keepReservationButton.setOnClickListener(v -> finish());

        confirmCancelButton.setOnClickListener(v -> {
            String selectedReason = cancelReasonAutoComplete.getText().toString().trim();

            if (selectedReason.isEmpty()) {
                Toast.makeText(this, "Please select a cancellation reason", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Reservation cancelled successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(CancelReservationActivity.this, RegisteredEventsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}