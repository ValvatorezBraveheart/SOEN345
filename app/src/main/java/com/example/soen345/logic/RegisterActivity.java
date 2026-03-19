package com.example.soen345.logic;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.R;
import com.example.soen345.User;
import com.example.soen345.service.UserRegisterService;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This links the Java code to your register XML
        setContentView(R.layout.activity_register);

        // Link to the "Sign In" text so users can switch back
        TextView signInText = findViewById(R.id.signInText);
        signInText.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        Button btnSignUp = findViewById(R.id.signupButton);
        btnSignUp.setOnClickListener(view -> {
            onClickSignUp();
        });
    }

    private void onClickSignUp(){

        EditText inputFullname = findViewById(R.id.fullName);
        EditText inputEmail = findViewById(R.id.email);
        EditText inputPhone = findViewById(R.id.phone);
        EditText inputUsername = findViewById(R.id.username);
        EditText inputPassword = findViewById(R.id.password);
        String fullname = inputFullname.getText().toString();
        String email = inputEmail.getText().toString();
        String phone = inputPhone.getText().toString();
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();
        String role = null;
        RadioGroup group = findViewById(R.id.radioGroupRole);
        int selectedId = group.getCheckedRadioButtonId();
        RadioButton selected = findViewById(selectedId);
        if (selected == findViewById(R.id.radioOptionCustomer)){
            role = "customer";
        } else if (selected == findViewById(R.id.radioOptionAdmin)) {
            role = "admin";
        }
        String userId = UUID.randomUUID().toString();

        User user = new User(userId,username, password, fullname, email, phone, role);
        UserRegisterService userRegisterService = new UserRegisterService(FirebaseFirestore.getInstance());
        userRegisterService.registerUser(user, new UserRegisterService.UserRegisterCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(RegisterActivity.this,"Register user successfully",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                String message = e.getMessage();
                if (message == null || message.isEmpty()) {
                    message = "An error occurred"; // default fallback
                }
                Toast.makeText(RegisterActivity.this, message,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
