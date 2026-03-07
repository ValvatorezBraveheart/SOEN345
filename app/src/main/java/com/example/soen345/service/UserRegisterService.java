package com.example.soen345.service;

import android.util.Log;

import com.example.soen345.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class UserRegisterService {
    private final FirebaseFirestore db;
    private final CollectionReference usersRef;

    public UserRegisterService(FirebaseFirestore firestore) {
        this.db = firestore;
        this.usersRef = db.collection("users");
    }

    public void registerUser(User newUser, UserRegisterCallback callback){
        if (!validateInputs(newUser)){
            Log.i("UserRegister", "Invalid inputs");
            callback.onFailure(new IllegalArgumentException("Invalid input"));
            return;
        }
        validateExistingAccount(newUser, (isValid, errorMessage) -> {
            if (isValid){
                usersRef.document(newUser.userId).set(newUser);
                callback.onSuccess();
                db.collection("users").get().addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Log.i("FirestoreDebug", "DocId: " + doc.getId() + ", Username: " + doc.getString("username"));
                    }
                });
            } else {
                Log.i("UserRegister", "Error");
                callback.onFailure(new IllegalArgumentException(errorMessage));
                db.collection("users").get().addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Log.i("FirestoreDebug", "DocId: " + doc.getId() + ", Username: " + doc.getString("username"));
                    }
                });
            }
        });
    }

    private boolean validateInputs(User newUser){
        // Either email or phone number exist
        if (newUser.email == null && newUser.phone == null){
            Log.i("UserRegister", "Missing email or phone number");
            return false;
        }
        // Ensure all essential fields exist
        if (newUser.username == null || newUser.username.isEmpty()) {
            Log.i("UserRegister", "Missing username");
            return false;
        }
        if (newUser.fullName == null || newUser.fullName.isEmpty()){
            Log.i("UserRegister", "Missing full name");
            return false;
        }
        if (newUser.password == null || newUser.password.isEmpty()){
            Log.i("UserRegister", "Missing password");
            return false;
        }
        if (newUser.role == null || newUser.role.isEmpty()){
            Log.i("UserRegister", "Missing role");
            return false;
        }
        if (newUser.userId == null || newUser.userId.isEmpty()){
            Log.i("UserRegister", "Missing userId");
            return false;
        }
        return true;
    }

    private void validateExistingAccount(User newUser, ValidationCallback callback){
        Task<QuerySnapshot> usernameCheck = db.collection("users").whereEqualTo("username", newUser.username).get();
        Task<QuerySnapshot> emailCheck = newUser.email != null
                ? db.collection("users").whereEqualTo("email", newUser.email).get()
                : Tasks.forResult(null);
        Task<QuerySnapshot> phoneCheck = newUser.phone != null
                ? db.collection("users").whereEqualTo("phone", newUser.phone).get()
                : Tasks.forResult(null);

        Tasks.whenAllSuccess(usernameCheck, emailCheck, phoneCheck)
                .addOnSuccessListener(results -> {
                    QuerySnapshot usernameResult = (QuerySnapshot) results.get(0);
                    QuerySnapshot emailResult = (QuerySnapshot) results.get(1);
                    QuerySnapshot phoneResult = (QuerySnapshot) results.get(2);

                    if (usernameResult != null && !usernameResult.isEmpty()) {
                        callback.onResult(false, "Username already taken");
                    } else if (emailResult != null && !emailResult.isEmpty()) {
                        callback.onResult(false, "Email already taken");
                    } else if (phoneResult != null && !phoneResult.isEmpty()) {
                        callback.onResult(false, "Phone already taken");
                    } else {
                        callback.onResult(true, null);
                    }
                })
                .addOnFailureListener(e -> callback.onResult(false, "Failed to validate: " + e.getMessage()));
    }
    public interface UserRegisterCallback{
        void onSuccess();
        void onFailure(Exception e);
    }
    interface ValidationCallback {
        void onResult(boolean isValid, String errorMessage);
    }
}
