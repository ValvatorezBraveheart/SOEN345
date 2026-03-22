package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.R;
import com.example.soen345.User;
import com.example.soen345.service.UserLogInService;
import com.google.firebase.firestore.FirebaseFirestore;

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
        Button btnLogin = findViewById(R.id.loginButton);
        btnLogin.setOnClickListener(view -> onClickLogin());
    }
    private void onClickLogin(){
        EditText inputUsername = findViewById(R.id.loginUsername);
        EditText inputPassword = findViewById(R.id.loginPassword);
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();
        UserLogInService service = new UserLogInService(FirebaseFirestore.getInstance());
        service.loginUser(username, password, new UserLogInService.UserLogInCallback() {
            @Override
            public void onSuccess(User user) {
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {

                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });

    }


}