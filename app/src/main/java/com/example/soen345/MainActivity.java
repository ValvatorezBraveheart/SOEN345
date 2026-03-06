package com.example.soen345;


import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
import android.view.View;
import com.example.soen345.logic.LoginActivity;
import com.example.soen345.logic.RegisterActivity;
import com.example.soen345.User;

public class MainActivity extends AppCompatActivity {

    private DatabaseService dbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize our new service
        dbService = new DatabaseService();

        // 2. Create a test user object
        User testUser = new User(
                "12345",
                "test_user",
                "John Doe",
                "john@example.com",
                "admin"
        );

        // 3. Attempt to save it to Firestore
        saveTestUser(testUser);
        Button loginMainButton = findViewById(R.id.loginMainButton);
        Button signupMainButton = findViewById(R.id.signupMainButton);

        // Set up Login navigation
        loginMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigates to LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Set up Sign Up navigation
        signupMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigates to RegisterActivity
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveTestUser(User user) {
        dbService.saveUser(user);
        Log.d("FirestoreTest", "Attempting to save user: " + user.username);
    }
}