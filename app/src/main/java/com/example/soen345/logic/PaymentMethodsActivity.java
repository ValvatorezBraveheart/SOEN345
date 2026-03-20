package com.example.soen345.logic;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.soen345.R;
import com.google.android.material.button.MaterialButton;

public class PaymentMethodsActivity extends AppCompatActivity {

    private ImageView backButton;

    private CardView card1;
    private CardView card2;

    private TextView defaultBadge1;

    private MaterialButton addNewCardButton;
    private MaterialButton setDefaultButton;
    private MaterialButton removeCardButton;

    private CardView selectedCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        initViews();
        setupCardSelection();
        setupActions();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);

        defaultBadge1 = findViewById(R.id.defaultBadge1);

        addNewCardButton = findViewById(R.id.addNewCardButton);
        setDefaultButton = findViewById(R.id.setDefaultButton);
        removeCardButton = findViewById(R.id.removeCardButton);

        selectedCard = card1;
        highlightSelectedCard(card1);
    }

    private void setupCardSelection() {
        card1.setOnClickListener(v -> {
            selectedCard = card1;
            highlightSelectedCard(card1);
        });

        card2.setOnClickListener(v -> {
            selectedCard = card2;
            highlightSelectedCard(card2);
        });
    }

    private void highlightSelectedCard(CardView card) {
        card1.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        card2.setCardBackgroundColor(getResources().getColor(android.R.color.white));

        if (card == card1) {
            card1.setCardBackgroundColor(getResources().getColor(R.color.card_selected_bg, getTheme()));
        } else if (card == card2) {
            card2.setCardBackgroundColor(getResources().getColor(R.color.card_selected_bg, getTheme()));
        }
    }

    private void setupActions() {
        backButton.setOnClickListener(v -> finish());

        addNewCardButton.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentMethodsActivity.this, AddNewCardActivity.class);
            startActivity(intent);
        });

        setDefaultButton.setOnClickListener(v -> {
            if (selectedCard == card1) {
                Toast.makeText(this, "Visa ending in 2481 is already the default card", Toast.LENGTH_SHORT).show();
            } else if (selectedCard == card2) {
                defaultBadge1.setText("Saved");
                Toast.makeText(this, "Mastercard ending in 7824 set as default", Toast.LENGTH_SHORT).show();
            }
        });

        removeCardButton.setOnClickListener(v -> {
            if (selectedCard == card1) {
                Toast.makeText(this, "Default card cannot be removed right now", Toast.LENGTH_SHORT).show();
            } else if (selectedCard == card2) {
                card2.setVisibility(CardView.GONE);
                Toast.makeText(this, "Selected card removed", Toast.LENGTH_SHORT).show();
                selectedCard = card1;
                highlightSelectedCard(card1);
            }
        });
    }
}