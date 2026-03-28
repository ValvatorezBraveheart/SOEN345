package com.example.soen345.service;

import com.example.soen345.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class UserSearchEventService {
    private final FirebaseFirestore db;
    private final CollectionReference eventsRef;

    public UserSearchEventService(FirebaseFirestore firestore) {
        this.db = firestore;
        this.eventsRef = db.collection("events");
    }

    public void getEvents(String category, String location, String date,
                          OnSuccessListener<List<Event>> onSuccess, OnFailureListener onFailure) {
        Query query = eventsRef;

        if (category != null) query = query.whereEqualTo("category", category);
        if (location != null) query = query.whereEqualTo("location", location);
        if (date != null)     query = query.whereEqualTo("date", date);

        query.get()
                .addOnSuccessListener(snapshot -> {
                    List<Event> events = snapshot.toObjects(Event.class);
                    onSuccess.onSuccess(events);
                })
                .addOnFailureListener(onFailure);
    }
}
