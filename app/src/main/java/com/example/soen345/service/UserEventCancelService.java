package com.example.soen345.service;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserEventCancelService {

    private final FirebaseFirestore db;
    private final CollectionReference reservationRef;

    public UserEventCancelService(FirebaseFirestore firestore) {
        this.db = firestore;
        this.reservationRef = db.collection("reservations");
    }

    public void cancelReservation(String userId, String reservationId, CancelReservationCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onFailure(new IllegalArgumentException("Invalid userId"));
            return;
        }
        if (reservationId == null || reservationId.isEmpty()) {
            callback.onFailure(new IllegalArgumentException("Invalid reservationId"));
            return;
        }

        reservationRef.document(reservationId).delete()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public interface CancelReservationCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}