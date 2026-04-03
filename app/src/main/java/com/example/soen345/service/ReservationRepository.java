package com.example.soen345.service;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReservationRepository {
    private final CollectionReference reservationRef;
    public ReservationRepository(FirebaseFirestore firestore) {
        this.reservationRef = firestore.collection("reservations");
    }
    public void findReservationId(String userId, String eventId, FindReservationCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onFailure(new IllegalArgumentException("Invalid userId"));
            return;
        }
        if (eventId == null || eventId.isEmpty()) {
            callback.onFailure(new IllegalArgumentException("Invalid eventId"));
            return;
        }

        reservationRef
                .whereEqualTo("userId", userId)
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        callback.onFailure(new IllegalArgumentException("Reservation not found"));
                        return;
                    }
                    String reservationId = snapshot.getDocuments().get(0).getId();
                    callback.onSuccess(reservationId);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public interface FindReservationCallback {
        void onSuccess(String reservationId);
        void onFailure(Exception e);
    }
}
