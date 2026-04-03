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
import com.example.soen345.User;
import com.example.soen345.service.NotificationService;
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
                    runOnUiThread(() -> Toast.makeText(ReserveEventActivity.this, "Reservation confirmed", Toast.LENGTH_SHORT).show());
                    sendNotification(event);
                    Intent intent = new Intent(ReserveEventActivity.this, RegisteredEventsActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> Toast.makeText(ReserveEventActivity.this, "Reservation failed, try again", Toast.LENGTH_SHORT).show());
                }
            });

        });
    }

    private void sendNotification(Event event) {
        NotificationService service = new NotificationService();
        User user = UserSession.getInstance().getUser();
        String messageBody = "You reserved a spot for " + event.name;
        if (user.phone != null && !user.phone.isEmpty()) {
            service.sendSmsMessage(user.phone, messageBody, new NotificationService.NotificationCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> Toast.makeText(ReserveEventActivity.this, "An sms confirmation message was sent", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> Toast.makeText(ReserveEventActivity.this, "Error sending sms notification", Toast.LENGTH_SHORT).show());
                }
            });
        }

        if (user.email != null && !user.email.isEmpty()) {
            service.sendEmail(user.email, "Event reservation confirmation", messageBody, new NotificationService.NotificationCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> Toast.makeText(ReserveEventActivity.this, "An email confirmation message was sent", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> Toast.makeText(ReserveEventActivity.this, "Error sending email notification", Toast.LENGTH_SHORT).show());
                }
            });
        }
    }
}