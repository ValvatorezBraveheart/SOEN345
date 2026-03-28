package com.example.soen345.service;

import com.example.soen345.Event;
import java.util.List;

public interface EventServiceInterface {
    interface EventDetailsCallback {
        void onCallback(Event event);
        void onError(Exception e);
    }
    interface EventCallback {
        void onCallback(List<Event> list);
        void onError(Exception e);
    }
    void fetchAllEvents(EventCallback callback);
    void fetchEventById(String eventId, EventDetailsCallback callback);
    void fetchEventsByCategory(String category, EventCallback callback);
    void fetchEventsCreatedByUser(String userId, EventCallback callback);
}