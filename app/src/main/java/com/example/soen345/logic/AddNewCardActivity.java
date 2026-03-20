package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.R;
import com.google.android.material.button.MaterialButton;

public class AddNewCardActivity extends AppCompatActivity {

    private ImageView backButton;

    private TextView cardPreviewNumber;
    private TextView cardPreviewName;
    private TextView cardPreviewExpiry;

    private EditText cardholderNameEditText;
    private EditText cardNumberEditText;
    private EditText expiryDateEditText;
    private EditText cvvEditText;
    private EditText postalCodeEditText;

    private CheckBox setDefaultCheckBox;

    private MaterialButton saveCardButton;
    private MaterialButton cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_card);

        initViews();
        setupLivePreview();
        setupActions();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);

        cardPreviewNumber = findViewById(R.id.cardPreviewNumber);
        cardPreviewName = findViewById(R.id.cardPreviewName);
        cardPreviewExpiry = findViewById(R.id.cardPreviewExpiry);

        cardholderNameEditText = findViewById(R.id.cardholderNameEditText);
        cardNumberEditText = findViewById(R.id.cardNumberEditText);
        expiryDateEditText = findViewById(R.id.expiryDateEditText);
        cvvEditText = findViewById(R.id.cvvEditText);
        postalCodeEditText = findViewById(R.id.postalCodeEditText);

        setDefaultCheckBox = findViewById(R.id.setDefaultCheckBox);

        saveCardButton = findViewById(R.id.saveCardButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    private void setupLivePreview() {
        cardholderNameEditText.addTextChangedListener(simpleWatcher(s -> {
            if (s.isEmpty()) {
                cardPreviewName.setText("Sarah Johnson");
            } else {
                cardPreviewName.setText(s);
            }
        }));

        cardNumberEditText.addTextChangedListener(simpleWatcher(s -> {
            if (s.isEmpty()) {
                cardPreviewNumber.setText("**** **** **** 2481");
            } else {
                cardPreviewNumber.setText(formatCardPreviewNumber(s));
            }
        }));

        expiryDateEditText.addTextChangedListener(simpleWatcher(s -> {
            if (s.isEmpty()) {
                cardPreviewExpiry.setText("08/28");
            } else {
                cardPreviewExpiry.setText(s);
            }
        }));
    }

    private TextWatcher simpleWatcher(OnTextChangedListener listener) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listener.onChanged(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };
    }

    private String formatCardPreviewNumber(String raw) {
        String digits = raw.replaceAll("\\s+", "");

        if (digits.length() >= 4) {
            String lastFour = digits.substring(digits.length() - 4);
            return "**** **** **** " + lastFour;
        } else {
            return "**** **** **** " + digits;
        }
    }

    private void setupActions() {
        backButton.setOnClickListener(v -> finish());

        cancelButton.setOnClickListener(v -> finish());

        saveCardButton.setOnClickListener(v -> {
            String cardholderName = cardholderNameEditText.getText().toString().trim();
            String cardNumber = cardNumberEditText.getText().toString().trim();
            String expiryDate = expiryDateEditText.getText().toString().trim();
            String cvv = cvvEditText.getText().toString().trim();
            String postalCode = postalCodeEditText.getText().toString().trim();

            if (cardholderName.isEmpty() ||
                    cardNumber.isEmpty() ||
                    expiryDate.isEmpty() ||
                    cvv.isEmpty() ||
                    postalCode.isEmpty()) {

                Toast.makeText(this, "Please fill in all card details", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cardNumber.length() < 12) {
                Toast.makeText(this, "Please enter a valid card number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (expiryDate.length() < 4) {
                Toast.makeText(this, "Please enter a valid expiry date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cvv.length() < 3) {
                Toast.makeText(this, "Please enter a valid CVV", Toast.LENGTH_SHORT).show();
                return;
            }

            String message = setDefaultCheckBox.isChecked()
                    ? "Card saved and set as default"
                    : "Card saved successfully";

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(AddNewCardActivity.this, PaymentMethodsActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private interface OnTextChangedListener {
        void onChanged(String text);
    }
}