package com.example.soen345.service;

import com.example.soen345.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserLogInService {
    private FirebaseFirestore db;
    private final CollectionReference usersRef;
    public UserLogInService(FirebaseFirestore firestore){
        this.db = firestore;
        this.usersRef = db.collection("users");
    }

    public void loginUser(String username, String password, UserLogInCallback callback){
        usersRef
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener(query -> {
                    if (query.isEmpty()) {
                        callback.onFailure(new AuthenticationException("Invalid username or password"));
                        return;
                    }
                    User user = query.getDocuments().get(0).toObject(User.class);
                    callback.onSuccess(user);
                })
                .addOnFailureListener(callback::onFailure);
    }
    public interface UserLogInCallback{
        void onSuccess(User user);
        void onFailure(Exception e);
    }
}
