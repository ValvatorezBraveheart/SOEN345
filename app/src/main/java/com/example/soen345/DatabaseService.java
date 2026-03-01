package com.example.soen345;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;

public class DatabaseService {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // References to your Collections
    private final CollectionReference usersRef = db.collection("users");
    private final CollectionReference eventsRef = db.collection("events");

    // Method to save a User
    public void saveUser(User user) {
        usersRef.document(user.userId).set(user);
    }

    // Method to save an Event
    public void saveEvent(Event event) {
        eventsRef.document(event.eventId).set(event);
    }

    // Method to get a User by ID
    public void getUser(String userId, final OnUserLoadedListener listener) {
        usersRef.document(userId).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            listener.onUserLoaded(user);
        });
    }

    public interface OnUserLoadedListener {
        void onUserLoaded(User user);
    }
}