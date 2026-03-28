package com.example.soen345.service;

import com.example.soen345.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminEditEventService {

    private final FirebaseFirestore db;
    private final CollectionReference eventsRef;

    public AdminEditEventService(FirebaseFirestore firestore) {
        this.db = firestore;
        this.eventsRef = db.collection("events");
    }

    public void editEvent(Event event, EditEventCallback callback) {
        if (!validateNewEventData(event)){
            callback.onFailure(new IllegalArgumentException("Invalid event"));
            return;
        }

        // Basically overwrite it
        eventsRef.document(event.eventId)
                .set(event)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public boolean validateNewEventData(Event event){
        if (event == null || event.eventId == null || event.eventId.isEmpty()) {
            return false;
        }
        if (event.name == null || event.name.isEmpty()) {
            return false;
        }
        if (event.date == null || event.date.isEmpty()) {
            return false;
        }
        if (event.startTime == null || event.startTime.isEmpty()) {
            return false;
        }
        if (event.endTime == null || event.endTime.isEmpty()) {
            return false;
        }
        if (event.location == null || event.location.isEmpty()) {
            return false;
        }
        if (event.category == null || event.category.isEmpty()) {
            return false;
        }
        if (event.description == null || event.description.isEmpty()) {
            return false;
        }
        if (event.adminId == null || event.adminId.isEmpty()) {
            return false;
        }
        return true;
    }

    public interface EditEventCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}