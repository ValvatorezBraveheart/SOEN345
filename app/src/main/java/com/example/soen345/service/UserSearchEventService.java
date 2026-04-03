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

    public void getEvents(String category, String location, String date, EventSearchCallback callback) {
        Query query = eventsRef;

        if (category != null && !category.isEmpty()) query = query.whereEqualTo("category", category);
        if (location != null && !location.isEmpty()) query = query.whereEqualTo("location", location);
        if (date != null && !date.isEmpty())     query = query.whereEqualTo("date", date);

        query.get()
                .addOnSuccessListener(snapshot -> {
                    List<Event> events = snapshot.toObjects(Event.class);
                    callback.onSuccess(events);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public interface EventSearchCallback{
        void onSuccess(List<Event> events);
        void onFailure(Exception e);
    }
}
