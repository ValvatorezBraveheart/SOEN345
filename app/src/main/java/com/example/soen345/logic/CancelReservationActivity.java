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
import com.example.soen345.service.ReservationRepository;
import com.example.soen345.service.UserEventCancelService;
import com.example.soen345.service.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class CancelReservationActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView eventTitle;
    private TextView eventLocation;

    private AutoCompleteTextView cancelReasonAutoComplete;

    private MaterialButton confirmCancelButton;
    private MaterialButton keepReservationButton;

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_reservation);

        event = getIntent().getParcelableExtra("event");

        initViews();
        loadReservationData();
        setupReasonDropdown();
        setupActions();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);

        eventTitle = findViewById(R.id.eventTitle);
        eventLocation = findViewById(R.id.eventLocation);

        cancelReasonAutoComplete = findViewById(R.id.cancelReasonAutoComplete);

        confirmCancelButton = findViewById(R.id.confirmCancelButton);
        keepReservationButton = findViewById(R.id.keepReservationButton);
    }

    private void loadReservationData() {
        eventTitle.setText(event.name);
        eventLocation.setText(event.location);
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
            String userId = UserSession.getInstance().getUser().userId;
            UserEventCancelService service = new UserEventCancelService(FirebaseFirestore.getInstance());
            ReservationRepository reservationRepository = new ReservationRepository(FirebaseFirestore.getInstance());

            reservationRepository.findReservationId(userId, event.eventId, new ReservationRepository.FindReservationCallback() {
                @Override
                public void onSuccess(String reservationId) {
                    service.cancelReservation(reservationId, new UserEventCancelService.CancelReservationCallback() {
                        @Override
                        public void onSuccess() {

                            Toast.makeText(CancelReservationActivity.this, "Reservation cancelled successfully", Toast.LENGTH_SHORT).show();
                            sendNotification(event);
                            Intent intent = new Intent(CancelReservationActivity.this, RegisteredEventsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(Exception e) {

                            Toast.makeText(CancelReservationActivity.this, "Fail to cancel reservation", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                }
            });

        });
    }

    private void sendNotification(Event event) {
        NotificationService service = new NotificationService();
        User user = UserSession.getInstance().getUser();
        String messageBody = "You canceled your reservation for " + event.name;
        if (user.phone != null && !user.phone.isEmpty()) {
            service.sendSmsMessage(user.phone, messageBody, new NotificationService.NotificationCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> Toast.makeText(CancelReservationActivity.this, "An sms confirmation message was sent", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onFailure(Exception e) {

                    runOnUiThread(() -> Toast.makeText(CancelReservationActivity.this, "Error sending sms notification", Toast.LENGTH_SHORT).show());
                }
            });
        }
        if (user.email != null && !user.email.isEmpty()) {
            service.sendEmail(user.email, "Reservation cancel confirmation", messageBody, new NotificationService.NotificationCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> Toast.makeText(CancelReservationActivity.this, "An email confirmation message was sent", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> Toast.makeText(CancelReservationActivity.this, "Error sending email notification", Toast.LENGTH_SHORT).show());
                }
            });

        }
    }
}