package com.example.soen345;

public class Event {
    public String eventId;
    public String name;
    public String date;
    public String startTime;
    public String endTime;
    public String location;
    public String category;
    public String description;
    public String adminId;

    public Event() {} // Required for Firebase

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