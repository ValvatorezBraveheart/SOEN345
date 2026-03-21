package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.R;
import com.google.android.material.button.MaterialButton;

public class ReserveEventActivity extends AppCompatActivity {

    private ImageView backButton;

    private AutoCompleteTextView ticketTypeAutoComplete;

    private MaterialButton confirmReservationButton;
    private MaterialButton cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_event);

        initViews();
        setupTicketTypeDropdown();
        setupActions();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);


        ticketTypeAutoComplete = findViewById(R.id.ticketTypeAutoComplete);

        confirmReservationButton = findViewById(R.id.confirmReservationButton);
        cancelButton = findViewById(R.id.cancelButton);
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

            Intent intent = new Intent(ReserveEventActivity.this, ReservedEventDetailsActivity.class);
            startActivity(intent);
            finish();
        });
    }
}