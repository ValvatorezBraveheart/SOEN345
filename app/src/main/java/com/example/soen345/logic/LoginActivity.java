package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.R;

public class LoginActivity extends AppCompatActivity {

    private TextView signUpText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUpText = findViewById(R.id.signUpText);
        loginButton = findViewById(R.id.loginButton);

        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CustomerDashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }
}