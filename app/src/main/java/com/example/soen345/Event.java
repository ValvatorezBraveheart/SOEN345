package com.example.soen345;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Event {
    public String eventId;
    public String category;    // Matches "Concerts" in screen
    public String date;        // Matches "22 August, 2026"
    public String description; // Matches "something somthing"
    public String endTime;     // Matches "10:00 PM"
    public String location;    // Matches "Eaton Center, Montreal"
    public String name;        // Matches "summerrrrr musiccz"
    public String startTime;   // Matches "8:00 PM"
    public String adminId;

    public Event() {} // Required empty constructor
}