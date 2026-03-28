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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AdminCancelEventServiceTest {

    @Mock FirebaseFirestore mockDb;
    @Mock CollectionReference mockEventsRef;
    @Mock DocumentReference mockDocRef;

    private AdminCancelEventService service;

    @BeforeEach
    void setUp() {
        when(mockDb.collection("events")).thenReturn(mockEventsRef);
        service = new AdminCancelEventService(mockDb);
    }

    // ===== Input validation tests =====

    @Test
    void testCancelFailsNullUserId() {
        Event event = buildValidEvent();
        AdminCancelEventService.CancelEventCallback callback = mock(AdminCancelEventService.CancelEventCallback.class);
        service.cancelEvent(null, event, callback);
        verify(callback).onFailure(any(IllegalArgumentException.class));
        verify(callback, never()).onSuccess();
    }

    @Test
    void testCancelFailsEmptyUserId() {
        Event event = buildValidEvent();
        AdminCancelEventService.CancelEventCallback callback = mock(AdminCancelEventService.CancelEventCallback.class);
        service.cancelEvent("", event, callback);
        verify(callback).onFailure(any(IllegalArgumentException.class));
        verify(callback, never()).onSuccess();
    }

    @Test
    void testCancelFailsNullEvent() {
        AdminCancelEventService.CancelEventCallback callback = mock(AdminCancelEventService.CancelEventCallback.class);
        service.cancelEvent("user123", null, callback);
        verify(callback).onFailure(any(IllegalArgumentException.class));
        verify(callback, never()).onSuccess();
    }

    @Test
    void testCancelFailsNullEventId() {
        Event event = buildValidEvent();
        event.eventId = null;
        AdminCancelEventService.CancelEventCallback callback = mock(AdminCancelEventService.CancelEventCallback.class);
        service.cancelEvent("user123", event, callback);
        verify(callback).onFailure(any(IllegalArgumentException.class));
        verify(callback, never()).onSuccess();
    }

    @Test
    void testCancelFailsEmptyEventId() {
        Event event = buildValidEvent();
        event.eventId = "";
        AdminCancelEventService.CancelEventCallback callback = mock(AdminCancelEventService.CancelEventCallback.class);
        service.cancelEvent("user123", event, callback);
        verify(callback).onFailure(any(IllegalArgumentException.class));
        verify(callback, never()).onSuccess();
    }

    // ===== Firestore tests =====

    @Test
    void testCancelSuccess() {
        Event event = buildValidEvent();
        Task<Void> mockDeleteTask = mock(Task.class);

        when(mockEventsRef.document(event.eventId)).thenReturn(mockDocRef);
        when(mockDocRef.delete()).thenReturn(mockDeleteTask);
        when(mockDeleteTask.addOnSuccessListener(any())).thenReturn(mockDeleteTask);
        when(mockDeleteTask.addOnFailureListener(any())).thenReturn(mockDeleteTask);
        doAnswer(inv -> {
            OnSuccessListener<Void> listener = inv.getArgument(0);
            listener.onSuccess(null);
            return mockDeleteTask;
        }).when(mockDeleteTask).addOnSuccessListener(any());

        AdminCancelEventService.CancelEventCallback callback = mock(AdminCancelEventService.CancelEventCallback.class);
        service.cancelEvent("user123", event, callback);

        verify(callback).onSuccess();
        verify(callback, never()).onFailure(any());
    }

    @Test
    void testCancelFirestoreError() {
        Event event = buildValidEvent();
        Task<Void> mockDeleteTask = mock(Task.class);

        when(mockEventsRef.document(event.eventId)).thenReturn(mockDocRef);
        when(mockDocRef.delete()).thenReturn(mockDeleteTask);
        when(mockDeleteTask.addOnSuccessListener(any())).thenReturn(mockDeleteTask);
        doAnswer(inv -> {
            OnFailureListener listener = inv.getArgument(0);
            listener.onFailure(new Exception("Network error"));
            return mockDeleteTask;
        }).when(mockDeleteTask).addOnFailureListener(any());

        AdminCancelEventService.CancelEventCallback callback = mock(AdminCancelEventService.CancelEventCallback.class);
        service.cancelEvent("user123", event, callback);

        verify(callback).onFailure(any(Exception.class));
        verify(callback, never()).onSuccess();
    }

    // ===== Helper =====

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