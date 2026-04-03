package com.example.soen345.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.soen345.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminEditEventServiceTest {

    @Mock
    FirebaseFirestore mockDb;
    @Mock
    CollectionReference mockEventsRef;
    @Mock
    DocumentReference mockEventDocRef;

    private AdminEditEventService service;

    @BeforeEach
    void setUp() {
        when(mockDb.collection("events")).thenReturn(mockEventsRef);
        service = new AdminEditEventService(mockDb);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "eventId",
            "name",
            "date",
            "location",
            "category",
            "adminId"
    }, nullValues = "NULL")
    void testValidateEventNullField(String nullField) {
        Event event = buildValidEvent();
        switch (nullField) {
            case "eventId":  event.eventId  = null; break;
            case "name":     event.name     = null; break;
            case "date":     event.date     = null; break;
            case "location": event.location = null; break;
            case "category": event.category = null; break;
            case "adminId":  event.adminId  = null; break;
        }

        AdminEditEventService.EditEventCallback callback = mock(AdminEditEventService.EditEventCallback.class);
        service.editEvent(event, callback);

        verify(callback).onFailure(any(IllegalArgumentException.class));
        verify(callback, never()).onSuccess();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "eventId",
            "name",
            "date",
            "location",
            "category",
            "adminId"
    })
    void testValidateEventEmptyField(String emptyField) {
        Event event = buildValidEvent();
        switch (emptyField) {
            case "eventId":  event.eventId  = ""; break;
            case "name":     event.name     = ""; break;
            case "date":     event.date     = ""; break;
            case "location": event.location = ""; break;
            case "category": event.category = ""; break;
            case "adminId":  event.adminId  = ""; break;
        }

        AdminEditEventService.EditEventCallback callback = mock(AdminEditEventService.EditEventCallback.class);
        service.editEvent(event, callback);

        verify(callback).onFailure(any(IllegalArgumentException.class));
        verify(callback, never()).onSuccess();
    }


    @Test
    void testEditSuccess() {
        Event event = buildValidEvent();
        Task<Void> mockSetTask = mock(Task.class);

        when(mockEventsRef.document(event.eventId)).thenReturn(mockEventDocRef);
        when(mockEventDocRef.set(any())).thenReturn(mockSetTask);
        when(mockSetTask.addOnSuccessListener(any())).thenReturn(mockSetTask);
        when(mockSetTask.addOnFailureListener(any())).thenReturn(mockSetTask);
        doAnswer(inv -> {
            OnSuccessListener<Void> listener = inv.getArgument(0);
            listener.onSuccess(null);
            return mockSetTask;
        }).when(mockSetTask).addOnSuccessListener(any());

        AdminEditEventService.EditEventCallback callback = mock(AdminEditEventService.EditEventCallback.class);
        service.editEvent( event, callback);

        verify(callback).onSuccess();
        verify(callback, never()).onFailure(any());
    }

    @Test
    void testEditFirestoreError() {
        Event event = buildValidEvent();
        Task mockSetTask = mock(Task.class);

        when(mockEventsRef.document(event.eventId)).thenReturn(mockEventDocRef);
        when(mockEventDocRef.set(any())).thenReturn(mockSetTask);
        when(mockSetTask.addOnSuccessListener(any())).thenReturn(mockSetTask);
        doAnswer(inv -> {
            OnFailureListener listener = inv.getArgument(0);
            listener.onFailure(new Exception("Network error"));
            return mockSetTask;
        }).when(mockSetTask).addOnFailureListener(any());

        AdminEditEventService.EditEventCallback callback = mock(AdminEditEventService.EditEventCallback.class);
        service.editEvent(event, callback);

        verify(callback).onFailure(any(Exception.class));
        verify(callback, never()).onSuccess();
    }


    private Event buildValidEvent() {
        Event event = new Event();
        event.eventId     = "eventId";
        event.name        = "event name";
        event.date        = "2024-06-01";
        event.startTime   = "01:00";
        event.endTime     = "11:00";
        event.location    = "Montreal";
        event.category    = "Technology";
        event.description = "Something something";
        event.adminId     = "adminId";
        return event;
    }
}