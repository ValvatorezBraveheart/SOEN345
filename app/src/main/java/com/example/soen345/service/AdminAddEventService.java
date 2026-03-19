package com.example.soen345.service;

import com.example.soen345.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminAddEventService {

    private final FirebaseFirestore db;
    private final CollectionReference eventsRef;

    public AdminAddEventService(FirebaseFirestore firestore) {
        this.db = firestore;
        this.eventsRef = db.collection("events");
    }

    public void addEvent(Event event,AddEventCallback callback){
        if (!validateEvent(event)){
            callback.onFailure(new IllegalArgumentException("Invalid event data"));
            return;
        }

        // Verify adminId belongs to an actual admin
        db.collection("users").document(event.adminId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        callback.onFailure(new SecurityException("User not found"));
                        return;
                    }

                    String role = doc.getString("role");
                    if (role == null || !role.equals("admin")) {
                        callback.onFailure(new SecurityException("User is not an admin"));
                        return;
                    }

                    // Save the event
                    eventsRef.document(event.eventId).set(event)
                            .addOnSuccessListener(unused -> callback.onSuccess())
                            .addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }

    // Helper function to check if all field is set
    private boolean validateEvent(Event event){
        if (event.eventId == null || event.eventId.isEmpty()) return false;
        if (event.name == null || event.name.isEmpty()) return false;
        if (event.date == null || event.date.isEmpty()) return false;
        if (event.startTime == null || event.startTime.isEmpty()) return false;
        if (event.endTime == null || event.endTime.isEmpty()) return false;
        if (event.location == null || event.location.isEmpty()) return false;
        if (event.category == null || event.category.isEmpty()) return false;
        if (event.description == null || event.description.isEmpty()) return false;
        if (event.adminId == null || event.adminId.isEmpty()) return false;

        return true;
    }

    public interface AddEventCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}
