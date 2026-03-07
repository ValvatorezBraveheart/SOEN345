package com.example.soen345.service;

import com.example.soen345.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class UserLogInService {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public void execute(String username, String password, UserLogInCallback callback){
        db.collection("users")
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener(query -> {
                    if (query.isEmpty()) {
                        callback.onFailure(new Exception("Invalid username or password"));
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
