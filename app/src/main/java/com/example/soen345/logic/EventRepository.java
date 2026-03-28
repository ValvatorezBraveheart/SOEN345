package com.example.soen345.logic;


import com.example.soen345.Event;
import com.example.soen345.service.EventServiceInterface;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventRepository implements EventServiceInterface {
    private final FirebaseFirestore db;
    public EventRepository(FirebaseFirestore firestore) {
        this.db = firestore;
    }
    @Override
    public void fetchEventById(String eventId, EventDetailsCallback callback) {
        FirebaseFirestore.getInstance()
                .collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event != null) event.eventId = documentSnapshot.getId();
                    callback.onCallback(event);
                })
                .addOnFailureListener(callback::onError);
    }
    // Inside EventRepository.java
    @Override
    public void fetchAllEvents(EventCallback callback) {
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> eventList = new ArrayList<>();
                    for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            // This maps Firestore fields to your Event.java public fields
                            Event event = document.toObject(Event.class);
                            if (event != null) {
                                event.eventId = document.getId();
                                eventList.add(event);
                            }
                        } catch (Exception e) {
                            android.util.Log.e("DEBUG_DATA", "Mapping failed for doc " + document.getId() + ": " + e.getMessage());
                        }
                    }
                    android.util.Log.d("DEBUG_DATA", "Final List Size: " + eventList.size());
                    callback.onCallback(eventList);
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("DEBUG_DATA", "Error fetching: " + e.getMessage());
                    callback.onError(e);
                });
    }
    @Override
    public void fetchEventsByCategory(String category, EventCallback callback) {
        db.collection("events")
                .whereEqualTo("category", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Event> eventList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            event.eventId = document.getId();
                            eventList.add(event);
                        }
                        callback.onCallback(eventList);
                    } else {
                        callback.onError(task.getException());
                    }
                });
    }
    @Override
    public void fetchEventsCreatedByUser(String userId, EventCallback callback) {
        db.collection("events")
                .whereEqualTo("adminId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Event> eventList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            event.eventId = document.getId();
                            eventList.add(event);
                        }
                        callback.onCallback(eventList);
                    } else {
                        callback.onError(task.getException());
                    }
                });
    }
}
