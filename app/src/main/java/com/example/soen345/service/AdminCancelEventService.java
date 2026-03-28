package com.example.soen345.service;

import com.example.soen345.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminCancelEventService {

    private final FirebaseFirestore db;
    private final CollectionReference eventsRef;

    public AdminCancelEventService(FirebaseFirestore firestore) {
        this.db = firestore;
        this.eventsRef = db.collection("events");
    }
    public void cancelEvent(String eventId, CancelEventCallback callback) {
        if (eventId == null || eventId.isEmpty()) {
            callback.onFailure(new IllegalArgumentException("Invalid event"));
            return;
        }

        eventsRef.document(eventId)
                .delete()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public interface CancelEventCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}
