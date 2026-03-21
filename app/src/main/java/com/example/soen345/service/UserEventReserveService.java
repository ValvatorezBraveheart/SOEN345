package com.example.soen345.service;

import com.example.soen345.Reservation;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class UserEventReserveService {

    private FirebaseFirestore db;
    private final CollectionReference reservationRef;

    public UserEventReserveService(FirebaseFirestore firestore) {
        this.db = firestore;
        this.reservationRef = db.collection("reservations");
    }

    public void reserveEvent(String userId, String eventId, ReserveEventCallback callback) {
        if (userId == null || userId.isEmpty() || eventId == null || eventId.isEmpty()) {
            callback.onFailure(new IllegalArgumentException("Invalid userId or eventId"));
            return;
        }

        // Check if already registered
        isAlreadyRegistered(userId, eventId, (isRegistered, error) -> {
            if (error != null) {
                callback.onFailure(error);
                return;
            }
            if (isRegistered) {
                callback.onFailure(new IllegalStateException("User is already registered for this event"));
                return;
            }

            // Check event exists
            db.collection("events").document(eventId).get()
                    .addOnSuccessListener(eventDoc -> {
                        if (!eventDoc.exists()) {
                            callback.onFailure(new IllegalArgumentException("Event not found"));
                            return;
                        }

                        // Check user exists
                        db.collection("users").document(userId).get()
                                .addOnSuccessListener(userDoc -> {
                                    if (!userDoc.exists()) {
                                        callback.onFailure(new IllegalArgumentException("User not found"));
                                        return;
                                    }

                                    String reservationId = UUID.randomUUID().toString();
                                    Reservation reservation = new Reservation(reservationId, userId, eventId);
                                    reservationRef.document(reservationId).set(reservation)
                                            .addOnSuccessListener(unused -> callback.onSuccess(reservationId))
                                            .addOnFailureListener(callback::onFailure);
                                })
                                .addOnFailureListener(callback::onFailure);
                    })
                    .addOnFailureListener(callback::onFailure);
        });
    }

    public void isAlreadyRegistered(String userId, String eventId, AlreadyRegisteredCallback callback) {
        reservationRef
                .whereEqualTo("userId", userId)
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(snapshot -> callback.onResult(!snapshot.isEmpty(), null))
                .addOnFailureListener(e -> callback.onResult(false, e));
    }

    public interface ReserveEventCallback {
        void onSuccess(String reservationId);
        void onFailure(Exception e);
    }

    public interface AlreadyRegisteredCallback {
        void onResult(boolean isRegistered, Exception error);
    }
}
