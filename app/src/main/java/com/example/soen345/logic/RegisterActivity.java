package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.R;


public class RegisterActivity extends AppCompatActivity {

    private EditText fullName;
    private EditText email;
    private EditText phone;
    private EditText password;
    private AutoCompleteTextView roleAutoComplete;
    private Button signupButton;
    private TextView signInText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        roleAutoComplete = findViewById(R.id.roleAutoComplete);
        signupButton = findViewById(R.id.signupButton);
        signInText = findViewById(R.id.signInText);

        String[] roles = {"Customer", "Admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                roles
        );

        roleAutoComplete.setAdapter(adapter);
        roleAutoComplete.setOnClickListener(v -> roleAutoComplete.showDropDown());

        signInText.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        signupButton.setOnClickListener(v -> {
            String fullNameText = fullName.getText().toString().trim();
            String emailText = email.getText().toString().trim();
            String phoneText = phone.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String roleText = roleAutoComplete.getText().toString().trim();

            if (TextUtils.isEmpty(fullNameText) ||
                    TextUtils.isEmpty(emailText) ||
                    TextUtils.isEmpty(phoneText) ||
                    TextUtils.isEmpty(passwordText) ||
                    TextUtils.isEmpty(roleText)) {

                Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegisterActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}