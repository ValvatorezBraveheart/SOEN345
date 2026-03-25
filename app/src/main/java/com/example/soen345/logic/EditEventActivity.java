package com.example.soen345.logic;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.R;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Locale;

public class EditEventActivity extends AppCompatActivity {

    private ImageView backButton;

    private EditText eventTitleEditText;
    private EditText eventOrganizerEditText;
    private AutoCompleteTextView categoryAutoComplete;
    private EditText eventDateEditText;
    private EditText eventTimeEditText;
    private EditText eventLocationEditText;
    private EditText eventDescriptionEditText;

    private MaterialButton updateEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

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
        eventTimeEditText = findViewById(R.id.eventTimeEditText);
        eventLocationEditText = findViewById(R.id.eventLocationEditText);
        eventDescriptionEditText = findViewById(R.id.eventDescriptionEditText);

        updateEventButton = findViewById(R.id.updateEventButton);
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
                    EditEventActivity.this,
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
                    EditEventActivity.this,
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

            Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}