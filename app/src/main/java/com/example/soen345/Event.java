package com.example.soen345;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Event {
    @DocumentId
    public String eventId;
    public String category;    // Matches "Concerts"
    public String date;        // Matches "22 August, 2026"
    public String description; // Matches "something somthing"
    public String endTime;     // Matches "10:00 PM"
    public String location;    // Matches "Eaton Center, Montreal"
    public String name;        // Matches "summerrrrr musiccz"
    public String startTime;   // Matches "8:00 PM"
    public String adminId;     // Matches "some_test_id"

    // Required for Firebase to map document data to this class
    public Event() {}

    // Full constructor for manual object creation
    public Event(String eventId, String name, String date, String startTime, String endTime, String location, String category, String description, String adminId) {
        this.eventId = eventId;
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.category = category;
        this.description = description;
        this.adminId = adminId;
    }
}