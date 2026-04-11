package com.example.soen345.service;


import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserDeleteAccountService {
    private final FirebaseFirestore db;
    private final CollectionReference usersRef;

    public UserDeleteAccountService(FirebaseFirestore firestore) {
        this.db = firestore;
        this.usersRef = db.collection("users");
    }

    public void deleteUser(String userId,UserDeleteCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onFailure(new IllegalArgumentException("Invalid input: userId is required"));
            return;
        }

        validateAccount(userId,(isValid, errorMessage) -> {
            if (isValid) {
                usersRef.document(userId).delete()
                        .addOnSuccessListener(unused -> {
                            Log.i("UserDeleteAccountService", "User deleted successfully: " + userId);
                            callback.onSuccess();
                        })
                        .addOnFailureListener(e -> {
                            Log.i("UserDeleteAccountService", "Failed to delete user: " + e.getMessage());
                            callback.onFailure(e);
                        });
            } else {
                Log.i("UserDeleteAccountService", "Validation failed: " + errorMessage);
                callback.onFailure(new IllegalArgumentException(errorMessage));
            }
        });
    }

    // Validate that account exist
    private void validateAccount(String userId, ValidationCallback callback) {
        usersRef.document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        callback.onResult(false, "Account not found");
                        return;
                    }
                    callback.onResult(true, null);
                })
                .addOnFailureListener(e -> {
                    Log.i("UserDeleteAccountService", "Error fetching user: " + e.getMessage());
                    callback.onResult(false, "Failed to validate account: " + e.getMessage());
                });
    }

    public interface UserDeleteCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    interface ValidationCallback {
        void onResult(boolean isValid, String errorMessage);
    }
}