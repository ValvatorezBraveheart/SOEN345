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
import com.example.soen345.service.AdminAddEventService;
import com.example.soen345.service.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class AdminAddEventActivity extends AppCompatActivity {

    private ImageView backButton;

    private EditText eventTitleEditText;
    private EditText eventOrganizerEditText;
    private AutoCompleteTextView categoryAutoComplete;
    private EditText eventDateEditText;
    private EditText eventStartTimeEditText;
    private EditText eventEndTimeEditText;
    private EditText eventLocationEditText;
    private EditText eventDescriptionEditText;

    private MaterialButton publishEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        initViews();
        setupCategoryDropdown();
        setupDatePicker();
        setupTimePicker();
        setupActions();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);

        eventTitleEditText = findViewById(R.id.eventTitleEditText);
        eventOrganizerEditText = findViewById(R.id.eventOrganizerEditText);
        categoryAutoComplete = findViewById(R.id.categoryAutoComplete);
        eventDateEditText = findViewById(R.id.eventDateEditText);
        eventStartTimeEditText = findViewById(R.id.startTimeEditText);
        eventEndTimeEditText = findViewById(R.id.endTimeEditText);
        eventLocationEditText = findViewById(R.id.eventLocationEditText);
        eventDescriptionEditText = findViewById(R.id.eventDescriptionEditText);

        publishEventButton = findViewById(R.id.publishEventButton);

    }

    private void setupCategoryDropdown() {
        String[] categories = {
                "Concerts",
                "Sports",
                "Theater"
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
                    AdminAddEventActivity.this,
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
        eventStartTimeEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    AdminAddEventActivity.this,
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
                        eventStartTimeEditText.setText(formattedTime);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
            );

            timePickerDialog.show();
        });
        eventEndTimeEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    AdminAddEventActivity.this,
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
                        eventEndTimeEditText.setText(formattedTime);
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

        publishEventButton.setOnClickListener(v -> {
            String title = eventTitleEditText.getText().toString().trim();
            String organizer = eventOrganizerEditText.getText().toString().trim();
            String category = categoryAutoComplete.getText().toString().trim();
            String date = eventDateEditText.getText().toString().trim();
            String startTime = eventStartTimeEditText.getText().toString().trim();
            String endTime = eventEndTimeEditText.getText().toString().trim();
            String location = eventLocationEditText.getText().toString().trim();
            String description = eventDescriptionEditText.getText().toString().trim();

            if (title.isEmpty() ||
                    organizer.isEmpty() ||
                    category.isEmpty() ||
                    date.isEmpty() ||
                    startTime.isEmpty() ||
                    endTime.isEmpty() ||
                    location.isEmpty() ||
                    description.isEmpty()) {

                Toast.makeText(this, "Please fill in all event details", Toast.LENGTH_SHORT).show();
                return;
            }
            String eventId = UUID.randomUUID().toString();
            String userId = UserSession.getInstance().getUser().userId;
            Event newEvent = new Event(eventId,title,date, startTime, endTime, location,category,description, userId);


            AdminAddEventService service = new AdminAddEventService(FirebaseFirestore.getInstance());
            service.addEvent(newEvent, new AdminAddEventService.AddEventCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AdminAddEventActivity.this, "Event published successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminAddEventActivity.this, AdminManageEventsActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Exception e) {

                    Toast.makeText(AdminAddEventActivity.this, "Failed to publish event, try again", Toast.LENGTH_SHORT).show();
                }
            });

            finish();
        });

    }
}