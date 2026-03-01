package com.example.soen345;


import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
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
    }

    private void saveTestUser(User user) {
        dbService.saveUser(user);
        Log.d("FirestoreTest", "Attempting to save user: " + user.username);
    }
}