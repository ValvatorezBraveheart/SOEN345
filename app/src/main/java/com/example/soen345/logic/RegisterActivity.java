package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.R;
import com.example.soen345.User;
import com.example.soen345.service.UserRegisterService;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;


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

        // Link to the "Sign In" text so users can switch back
        TextView signInText = findViewById(R.id.signInText);
        signInText.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        signupButton.setOnClickListener(v -> onClickSignUp());
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
        String roleText = roleAutoComplete.getText().toString().trim().toLowerCase();
        String userId = UUID.randomUUID().toString();

        User user = new User(userId,username, password, fullname, email, phone, roleText);
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
