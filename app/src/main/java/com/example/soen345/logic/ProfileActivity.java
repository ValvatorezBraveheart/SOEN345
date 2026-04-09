package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.soen345.MainActivity;

import com.example.soen345.R;
import com.example.soen345.User;
import com.example.soen345.service.UserDeleteAccountService;
import com.example.soen345.service.UserSession;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private ImageView navHome;
    private ImageView navTickets;
    private ImageView navProfile;

    private ImageView navManageEvents;
    private FrameLayout navManageEventsContainer;
    private CardView editProfileCard;
    private CardView deleteAccountCard;

    
    private CardView logoutCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupBottomNavigation();
        setupProfileActions();
    }

    private void initViews() {
        navHome = findViewById(R.id.navHome);
        navTickets = findViewById(R.id.navTickets);
        navProfile = findViewById(R.id.navProfile);

        navManageEvents = findViewById(R.id.navManageEvents);
        navManageEventsContainer = findViewById(R.id.navManageEventsContainer);

        // Hide managed if user is customer
        if ("customer".equals(UserSession.getInstance().getUser().role)) {
            navManageEventsContainer.setVisibility(View.GONE);
        } else {
            navManageEventsContainer.setVisibility(View.VISIBLE);
        }

        editProfileCard = findViewById(R.id.editProfileCard);
        logoutCard = findViewById(R.id.logoutCard);
        deleteAccountCard = findViewById(R.id.deleteAccountCard);
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        navTickets.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, RegisteredEventsActivity.class);
            startActivity(intent);
            finish();
        });

        navProfile.setOnClickListener(v -> {
            // Already on this page
        });
    }

    private void setupProfileActions() {
        editProfileCard.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });


        logoutCard.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        deleteAccountCard.setOnClickListener( v->{
            UserDeleteAccountService service = new UserDeleteAccountService(FirebaseFirestore.getInstance());
            User user = UserSession.getInstance().getUser();
            service.deleteUser(user.userId, new UserDeleteAccountService.UserDeleteCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this,"Successfully delete your account",Toast.LENGTH_SHORT).show());
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this,"Unable to delete your account",Toast.LENGTH_SHORT).show());

                }
            });
        });
    }
}