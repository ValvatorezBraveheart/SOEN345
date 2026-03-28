package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.Event;
import com.example.soen345.R;
import com.example.soen345.service.UserEventReserveService;
import com.example.soen345.service.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReserveEventActivity extends AppCompatActivity {

    private ImageView backButton;

    private AutoCompleteTextView ticketTypeAutoComplete;
    private Event event;
    private TextView eventDate;
    private TextView eventTitle;
    private TextView eventLocation;
    private TextView eventTime;

    private MaterialButton confirmReservationButton;
    private MaterialButton cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_event);
        event = getIntent().getParcelableExtra("event");

        if (event == null) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupTicketTypeDropdown();
        setupActions();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);

        ticketTypeAutoComplete = findViewById(R.id.ticketTypeAutoComplete);

        confirmReservationButton = findViewById(R.id.confirmReservationButton);
        cancelButton = findViewById(R.id.cancelButton);

        eventDate = findViewById(R.id.eventDate);
        eventDate.setText(event.date);

        eventTitle = findViewById(R.id.eventTitle);
        eventTitle.setText(event.name);

        eventLocation = findViewById(R.id.eventLocation);
        eventLocation.setText(event.location);

        eventTime = findViewById(R.id.eventTime);
        eventTime.setText(String.format("%s-%s", event.startTime, event.endTime));
    }

    private void setupTicketTypeDropdown() {
        String[] ticketTypes = {
                "General Admission",
                "VIP",
                "Student"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                ticketTypes
        );

        ticketTypeAutoComplete.setAdapter(adapter);
        ticketTypeAutoComplete.setOnClickListener(v -> ticketTypeAutoComplete.showDropDown());
    }

    private void setupActions() {
        backButton.setOnClickListener(v -> finish());

        cancelButton.setOnClickListener(v -> finish());

        confirmReservationButton.setOnClickListener(v -> {

            String ticketType = ticketTypeAutoComplete.getText().toString().trim();

            if (ticketType.isEmpty()) {
                Toast.makeText(this, "Please fill in all reservation details", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Reservation confirmed", Toast.LENGTH_SHORT).show();
            String userId = UserSession.getInstance().getUser().userId;
            UserEventReserveService service = new UserEventReserveService(FirebaseFirestore.getInstance());
            service.reserveEvent(userId, event.eventId, new UserEventReserveService.ReserveEventCallback() {
                @Override
                public void onSuccess(String reservationId) {
                    Toast.makeText(ReserveEventActivity.this, "Reservation confirmed", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ReserveEventActivity.this, RegisteredEventsActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ReserveEventActivity.this, "Reservation failed, try again", Toast.LENGTH_SHORT).show();
                }
            });

        });
    }
}