package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.R;
import com.google.android.material.button.MaterialButton;

public class ReserveEventActivity extends AppCompatActivity {

    private ImageView backButton;

    private TextView eventDateDay;
    private TextView eventDateMonth;
    private TextView eventTitle;
    private TextView eventLocation;
    private TextView eventTime;

    private EditText attendeeName;
    private EditText attendeeEmail;
    private EditText attendeePhone;

    private AutoCompleteTextView ticketTypeAutoComplete;

    private TextView ticketPriceText;
    private TextView serviceFeeText;
    private TextView totalAmountText;

    private MaterialButton confirmReservationButton;
    private MaterialButton cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_event);

        initViews();
        loadEventData();
        setupTicketTypeDropdown();
        setupActions();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);

        eventDateDay = findViewById(R.id.eventDateDay);
        eventDateMonth = findViewById(R.id.eventDateMonth);
        eventTitle = findViewById(R.id.eventTitle);
        eventLocation = findViewById(R.id.eventLocation);
        eventTime = findViewById(R.id.eventTime);

        attendeeName = findViewById(R.id.attendeeName);
        attendeeEmail = findViewById(R.id.attendeeEmail);
        attendeePhone = findViewById(R.id.attendeePhone);

        ticketTypeAutoComplete = findViewById(R.id.ticketTypeAutoComplete);

        ticketPriceText = findViewById(R.id.ticketPriceText);
        serviceFeeText = findViewById(R.id.serviceFeeText);
        totalAmountText = findViewById(R.id.totalAmountText);

        confirmReservationButton = findViewById(R.id.confirmReservationButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    private void loadEventData() {
        Intent intent = getIntent();

        String day = intent.getStringExtra("event_day");
        String month = intent.getStringExtra("event_month");
        String title = intent.getStringExtra("event_title");
        String location = intent.getStringExtra("event_location");
        String time = intent.getStringExtra("event_time");
        String price = intent.getStringExtra("event_price");

        if (day == null) day = "24";
        if (month == null) month = "SEP";
        if (title == null) title = "Summer Music Festival";
        if (location == null) location = "Bell Centre, Montreal";
        if (time == null) time = "7:00 PM";
        if (price == null) price = "$45.00";

        eventDateDay.setText(day);
        eventDateMonth.setText(month);
        eventTitle.setText(title);
        eventLocation.setText(location);
        eventTime.setText(time);

        ticketPriceText.setText(price);
        serviceFeeText.setText("$3.00");
        totalAmountText.setText(calculateTotal(price, 3.00));
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

        ticketTypeAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String selectedType = ticketTypes[position];

            switch (selectedType) {
                case "VIP":
                    ticketPriceText.setText("$80.00");
                    totalAmountText.setText(calculateTotal("$80.00", 3.00));
                    break;
                case "Student":
                    ticketPriceText.setText("$25.00");
                    totalAmountText.setText(calculateTotal("$25.00", 3.00));
                    break;
                default:
                    ticketPriceText.setText("$45.00");
                    totalAmountText.setText(calculateTotal("$45.00", 3.00));
                    break;
            }
        });
    }

    private String calculateTotal(String ticketPrice, double serviceFee) {
        double basePrice = parsePrice(ticketPrice);
        double total = basePrice + serviceFee;
        return String.format("$%.2f", total);
    }

    private double parsePrice(String priceText) {
        try {
            return Double.parseDouble(priceText.replace("$", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private void setupActions() {
        backButton.setOnClickListener(v -> finish());

        cancelButton.setOnClickListener(v -> finish());

        confirmReservationButton.setOnClickListener(v -> {
            String name = attendeeName.getText().toString().trim();
            String email = attendeeEmail.getText().toString().trim();
            String phone = attendeePhone.getText().toString().trim();
            String ticketType = ticketTypeAutoComplete.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || ticketType.isEmpty()) {
                Toast.makeText(this, "Please fill in all reservation details", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Reservation confirmed", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ReserveEventActivity.this, ReservedEventDetailsActivity.class);
            intent.putExtra("event_day", eventDateDay.getText().toString());
            intent.putExtra("event_month", eventDateMonth.getText().toString());
            intent.putExtra("event_title", eventTitle.getText().toString());
            intent.putExtra("event_organizer", "Velox Productions");
            intent.putExtra("ticket_status", "Reserved");
            intent.putExtra("event_date_full", eventDateDay.getText().toString() + " " + eventDateMonth.getText().toString() + " 2026");
            intent.putExtra("event_time", eventTime.getText().toString());
            intent.putExtra("event_location", eventLocation.getText().toString());
            intent.putExtra("event_category", ticketType);
            intent.putExtra("reservation_id", "VELX-2026-00124");
            intent.putExtra("seat_info", "General Admission");
            intent.putExtra("amount_paid", totalAmountText.getText().toString());
            startActivity(intent);
            finish();
        });
    }
}