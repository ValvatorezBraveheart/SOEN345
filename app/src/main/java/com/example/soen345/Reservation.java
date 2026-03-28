package com.example.soen345;

public class Reservation {
    public String reservationId;
    public String userId;
    public String eventId;

    public Reservation() {} // required for Firestore

    public Reservation(String reservationId, String userId, String eventId) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.eventId = eventId;
    }
}