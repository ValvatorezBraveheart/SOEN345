package com.example.soen345.logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345.R;
import com.google.android.material.button.MaterialButton;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView backButton;
    private ImageView profileAvatar;
    private TextView changePhotoText;

    private EditText editFullName;
    private EditText editEmail;
    private EditText editPhone;
    private AutoCompleteTextView editRoleAutoComplete;

    private MaterialButton saveChangesButton;
    private MaterialButton cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();
        setupRoleDropdown();
        setupActions();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        profileAvatar = findViewById(R.id.profileAvatar);
        changePhotoText = findViewById(R.id.changePhotoText);

        editFullName = findViewById(R.id.editFullName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editRoleAutoComplete = findViewById(R.id.editRoleAutoComplete);

        saveChangesButton = findViewById(R.id.saveChangesButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    private void setupRoleDropdown() {
        String[] roles = {"Customer", "Admin"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                roles
        );

        editRoleAutoComplete.setAdapter(adapter);
        editRoleAutoComplete.setOnClickListener(v -> editRoleAutoComplete.showDropDown());
    }

    private void setupActions() {
        backButton.setOnClickListener(v -> finish());

        cancelButton.setOnClickListener(v -> finish());

        changePhotoText.setOnClickListener(v -> {
            Toast.makeText(this, "Change photo feature coming soon", Toast.LENGTH_SHORT).show();
        });

        profileAvatar.setOnClickListener(v -> {
            Toast.makeText(this, "Change photo feature coming soon", Toast.LENGTH_SHORT).show();
        });

        saveChangesButton.setOnClickListener(v -> {
            String fullName = editFullName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String role = editRoleAutoComplete.getText().toString().trim();

            if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || role.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }
}