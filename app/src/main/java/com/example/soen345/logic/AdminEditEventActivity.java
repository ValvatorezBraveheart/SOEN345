package com.example.soen345.logic;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.Event;
import com.example.soen345.R;
import com.example.soen345.service.AdminCancelEventService;
import com.example.soen345.service.AdminEditEventService;
import com.example.soen345.service.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class AdminEditEventActivity extends AppCompatActivity {
    private Event currentEvent;

    private ImageView backButton;
    private EditText eventTitleEditText;
    private EditText eventOrganizerEditText;
    private AutoCompleteTextView categoryAutoComplete;
    private EditText eventDateEditText;
    private EditText eventTimeEditText;
    private EditText eventLocationEditText;
    private EditText eventDescriptionEditText;

    private MaterialButton updateEventButton;

    private MaterialButton cancelEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        currentEvent = getIntent().getParcelableExtra("event");
        initViews();
        setupCategoryDropdown();
        setupDatePicker();
        setupTimePicker();
        setupActions();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);

        eventTitleEditText = findViewById(R.id.eventTitleEditText);
        eventTitleEditText.setText(currentEvent.name);

        eventOrganizerEditText = findViewById(R.id.eventOrganizerEditText);
        eventOrganizerEditText.setText(currentEvent.adminId);

        categoryAutoComplete = findViewById(R.id.categoryAutoComplete);
        categoryAutoComplete.setText(currentEvent.category);

        eventDateEditText = findViewById(R.id.eventDateEditText);
        eventDateEditText.setText(currentEvent.date);

        eventTimeEditText = findViewById(R.id.eventTimeEditText);
        eventTimeEditText.setText(currentEvent.startTime);

        eventLocationEditText = findViewById(R.id.eventLocationEditText);
        eventLocationEditText.setText(currentEvent.location);

        eventDescriptionEditText = findViewById(R.id.eventDescriptionEditText);
        eventDescriptionEditText.setText(currentEvent.description);

        updateEventButton = findViewById(R.id.updateEventButton);
        cancelEventButton = findViewById(R.id.cancelEventButton);
    }

    private void setupCategoryDropdown() {
        String[] categories = {
                "Concerts",
                "Sports",
                "Travel",
                "Theater",
                "Business",
                "Festival",
                "Exhibition"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                categories
        );

        categoryAutoComplete.setAdapter(adapter);
        categoryAutoComplete.setOnClickListener(v -> categoryAutoComplete.showDropDown());
    }

    private void setupDatePicker() {
        eventDateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AdminEditEventActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        String formattedDate = String.format(
                                Locale.getDefault(),
                                "%02d/%02d/%04d",
                                dayOfMonth,
                                month + 1,
                                year
                        );
                        eventDateEditText.setText(formattedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.show();
        });
    }

    private void setupTimePicker() {
        eventTimeEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    AdminEditEventActivity.this,
                    (view, hourOfDay, minute) -> {
                        String amPm = (hourOfDay >= 12) ? "PM" : "AM";
                        int formattedHour = hourOfDay % 12;
                        if (formattedHour == 0) {
                            formattedHour = 12;
                        }

                        String formattedTime = String.format(
                                Locale.getDefault(),
                                "%d:%02d %s",
                                formattedHour,
                                minute,
                                amPm
                        );
                        eventTimeEditText.setText(formattedTime);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
            );

            timePickerDialog.show();
        });
    }

    private void setupActions() {
        backButton.setOnClickListener(v -> finish());

        updateEventButton.setOnClickListener(v -> {
            String title = eventTitleEditText.getText().toString().trim();
            String organizer = eventOrganizerEditText.getText().toString().trim();
            String category = categoryAutoComplete.getText().toString().trim();
            String date = eventDateEditText.getText().toString().trim();
            String time = eventTimeEditText.getText().toString().trim();
            String location = eventLocationEditText.getText().toString().trim();
            String description = eventDescriptionEditText.getText().toString().trim();

            if (title.isEmpty() ||
                    organizer.isEmpty() ||
                    category.isEmpty() ||
                    date.isEmpty() ||
                    time.isEmpty() ||
                    location.isEmpty() ||
                    description.isEmpty()) {

                Toast.makeText(this, "Please fill in all event details", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = UserSession.getInstance().getUser().userId;
            Event newEvent = new Event(currentEvent.eventId ,title,date, time, time, location,category,description, userId);
            AdminEditEventService service = new AdminEditEventService(FirebaseFirestore.getInstance());
            service.editEvent(newEvent, new AdminEditEventService.EditEventCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AdminEditEventActivity.this, "Event published successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminEditEventActivity.this, AdminManageEventsActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(AdminEditEventActivity.this, "Failed to publish event, try again", Toast.LENGTH_SHORT).show();
                }
            });

            Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        });



        cancelEventButton.setOnClickListener(v->{

            AdminCancelEventService service = new AdminCancelEventService(FirebaseFirestore.getInstance());
            service.cancelEvent(currentEvent.eventId, new AdminCancelEventService.CancelEventCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> Toast.makeText(AdminEditEventActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show());
                    Intent intent = new Intent(AdminEditEventActivity.this, AdminManageEventsActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> Toast.makeText(AdminEditEventActivity.this, "Failed to delete event, try again", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }
}