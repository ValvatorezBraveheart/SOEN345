package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.R;
import com.example.soen345.User;
import com.example.soen345.service.UserLogInService;
import com.example.soen345.service.UserSession;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextView signUpText;
    private Button loginButton;
    private EditText inputUsername, inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Views
        signUpText = findViewById(R.id.signUpText);
        loginButton = findViewById(R.id.loginButton);
        inputUsername = findViewById(R.id.loginUsername);
        inputPassword = findViewById(R.id.loginPassword);

        // Navigate to Register
        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        // Trigger Login Logic
        loginButton.setOnClickListener(view -> onClickLogin());
    }

    private void onClickLogin() {
        String username = inputUsername.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        UserLogInService service = new UserLogInService(FirebaseFirestore.getInstance());
        service.loginUser(username, password, new UserLogInService.UserLogInCallback() {
            @Override
            public void onSuccess(User user) {
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                UserSession session = UserSession.getInstance();
                session.setUser(user);

                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish(); // Prevents user from going back to login screen via back button
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}