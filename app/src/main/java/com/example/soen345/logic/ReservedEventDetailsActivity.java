package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.R;
import com.google.android.material.button.MaterialButton;

public class ReservedEventDetailsActivity extends AppCompatActivity {

    private ImageView backButton;

    private TextView eventDateDay;
    private TextView eventDateMonth;
    private TextView eventTitle;
    private TextView eventOrganizer;
    private TextView ticketStatus;
    private TextView eventDateFull;
    private TextView eventTime;
    private TextView eventLocation;
    private TextView eventCategory;

    private TextView reservationId;
    private TextView seatInfo;
    private TextView amountPaid;

    private MaterialButton viewEventButton;
    private MaterialButton cancelReservationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved_event_details);

        initViews();
        loadReservationData();
        setupActions();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);

        eventDateDay = findViewById(R.id.eventDateDay);
        eventDateMonth = findViewById(R.id.eventDateMonth);
        eventTitle = findViewById(R.id.eventTitle);
        eventOrganizer = findViewById(R.id.eventOrganizer);
        ticketStatus = findViewById(R.id.ticketStatus);
        eventDateFull = findViewById(R.id.eventDateFull);
        eventTime = findViewById(R.id.eventTime);
        eventLocation = findViewById(R.id.eventLocation);
        eventCategory = findViewById(R.id.eventCategory);

        reservationId = findViewById(R.id.reservationId);
        seatInfo = findViewById(R.id.seatInfo);
        amountPaid = findViewById(R.id.amountPaid);

        viewEventButton = findViewById(R.id.viewEventButton);
        cancelReservationButton = findViewById(R.id.cancelReservationButton);
    }

    private void loadReservationData() {
        Intent intent = getIntent();

        String day = intent.getStringExtra("event_day");
        String month = intent.getStringExtra("event_month");
        String title = intent.getStringExtra("event_title");
        String organizer = intent.getStringExtra("event_organizer");
        String status = intent.getStringExtra("ticket_status");
        String dateFull = intent.getStringExtra("event_date_full");
        String time = intent.getStringExtra("event_time");
        String location = intent.getStringExtra("event_location");
        String category = intent.getStringExtra("event_category");
        String reservation = intent.getStringExtra("reservation_id");
        String seat = intent.getStringExtra("seat_info");
        String paid = intent.getStringExtra("amount_paid");

        if (day == null) day = "24";
        if (month == null) month = "SEP";
        if (title == null) title = "Summer Music Festival";
        if (organizer == null) organizer = "Velox Productions";
        if (status == null) status = "Reserved";
        if (dateFull == null) dateFull = "24 Sept 2026";
        if (time == null) time = "7:00 PM";
        if (location == null) location = "Bell Centre, Montreal";
        if (category == null) category = "Concerts";
        if (reservation == null) reservation = "VELX-2026-00124";
        if (seat == null) seat = "Section A • Seat 14";
        if (paid == null) paid = "$45.00";

        eventDateDay.setText(day);
        eventDateMonth.setText(month);
        eventTitle.setText(title);
        eventOrganizer.setText(organizer);
        ticketStatus.setText(status);
        eventDateFull.setText(dateFull);
        eventTime.setText(time);
        eventLocation.setText(location);
        eventCategory.setText(category);
        reservationId.setText(reservation);
        seatInfo.setText(seat);
        amountPaid.setText(paid);
    }

    private void setupActions() {
        backButton.setOnClickListener(v -> finish());

        viewEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReservedEventDetailsActivity.this, EventDetailsActivity.class);
            intent.putExtra("event_day", eventDateDay.getText().toString());
            intent.putExtra("event_month", eventDateMonth.getText().toString());
            intent.putExtra("event_organizer", eventOrganizer.getText().toString());
            intent.putExtra("event_title", eventTitle.getText().toString());
            intent.putExtra("event_category", eventCategory.getText().toString());
            intent.putExtra("event_date_full", eventDateFull.getText().toString());
            intent.putExtra("event_time", eventTime.getText().toString());
            intent.putExtra("event_location", eventLocation.getText().toString());
            intent.putExtra("event_price", amountPaid.getText().toString());
            intent.putExtra("event_availability", "Reserved");
            intent.putExtra("event_description", "This is your reserved event. More event details can be shown here later.");
            startActivity(intent);
        });

        cancelReservationButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReservedEventDetailsActivity.this, CancelReservationActivity.class);
            startActivity(intent);
        });
    }
}