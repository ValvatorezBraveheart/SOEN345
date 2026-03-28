package com.example.soen345.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
import com.google.firebase.firestore.DocumentSnapshot;
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

import java.util.Objects;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AdminAddEventServiceTest {

    @Mock
    FirebaseFirestore mockDb;
    @Mock
    CollectionReference mockEventsRef;
    @Mock CollectionReference mockUsersRef;
    @Mock
    DocumentReference mockEventDocRef;
    @Mock
    DocumentSnapshot mockAdminDoc;
    @Mock
    Task<DocumentSnapshot> mockGetTask;
    @Mock Task<Void> mockSetTask;

    private AdminAddEventService service;

    @BeforeEach
    void setUp() {
        when(mockDb.collection("events")).thenReturn(mockEventsRef);
        when(mockDb.collection("users")).thenReturn(mockUsersRef);
        service = new AdminAddEventService(mockDb);
    }

    // Validate input fields
    // TODO: Update the UserRegisterServiceTest to look like this, it seems cleaner

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

        AdminAddEventService.AddEventCallback callback = mock(AdminAddEventService.AddEventCallback.class);
        service.addEvent(event, callback);

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

        AdminAddEventService.AddEventCallback callback = mock(AdminAddEventService.AddEventCallback.class);
        service.addEvent(event, callback);

        verify(callback).onFailure(any(IllegalArgumentException.class));
        verify(callback, never()).onSuccess();
    }

    // Fail due to admin check

    @Test
    void testAddEventFailsAdminNotFound() {
        Event event = buildValidEvent();
        setupAdminCheck(false, null);

        AdminAddEventService.AddEventCallback callback = mock(AdminAddEventService.AddEventCallback.class);
        service.addEvent(event, callback);

        verify(callback).onFailure(any(SecurityException.class));
        verify(callback, never()).onSuccess();
    }

    @Test
    void testAddEventFailsUserNotAdmin() {
        Event event = buildValidEvent();
        setupAdminCheck(true, "customer");

        AdminAddEventService.AddEventCallback callback = mock(AdminAddEventService.AddEventCallback.class);
        service.addEvent(event, callback);

        verify(callback).onFailure(argThat(e ->
                Objects.equals(e.getMessage(), "User is not an admin")));
        verify(callback, never()).onSuccess();
    }

    @Test
    void testAddEventFailsFirestoreError() {
        Event event = buildValidEvent();
        Exception firestoreError = new Exception("Network error");

        DocumentReference mockUserDocRef = mock(DocumentReference.class);
        when(mockUsersRef.document(event.adminId)).thenReturn(mockUserDocRef);
        when(mockUserDocRef.get()).thenReturn(mockGetTask);
        when(mockGetTask.addOnSuccessListener(any())).thenReturn(mockGetTask);
        doAnswer(inv -> {
            OnFailureListener listener = inv.getArgument(0);
            listener.onFailure(firestoreError);
            return mockGetTask;
        }).when(mockGetTask).addOnFailureListener(any());

        AdminAddEventService.AddEventCallback callback = mock(AdminAddEventService.AddEventCallback.class);
        service.addEvent(event, callback);

        verify(callback).onFailure(firestoreError);
        verify(callback, never()).onSuccess();
    }

    // Success

    @Test
    void testAddEventSuccess() {
        Event event = buildValidEvent();
        setupAdminCheck(true, "admin");

        when(mockEventsRef.document(event.eventId)).thenReturn(mockEventDocRef);
        doAnswer(inv -> {
            OnSuccessListener<Void> listener = inv.getArgument(0);
            listener.onSuccess(null);
            return mockSetTask;
        }).when(mockSetTask).addOnSuccessListener(any());
        when(mockSetTask.addOnFailureListener(any())).thenReturn(mockSetTask);
        when(mockEventDocRef.set(any())).thenReturn(mockSetTask);

        AdminAddEventService.AddEventCallback callback = mock(AdminAddEventService.AddEventCallback.class);
        service.addEvent(event, callback);

        verify(callback).onSuccess();
        verify(callback, never()).onFailure(any());
    }

    // Helper

    // Setup the mocking of getting admin check
    private void setupAdminCheck(boolean docExists, String role) {
        DocumentReference mockUserDocRef = mock(DocumentReference.class);
        when(mockUsersRef.document(any())).thenReturn(mockUserDocRef);
        when(mockUserDocRef.get()).thenReturn(mockGetTask);

        doAnswer(inv -> {
            OnSuccessListener<DocumentSnapshot> listener = inv.getArgument(0);
            when(mockAdminDoc.exists()).thenReturn(docExists);
            when(mockAdminDoc.getString("role")).thenReturn(role);
            listener.onSuccess(mockAdminDoc);
            return mockGetTask;
        }).when(mockGetTask).addOnSuccessListener(any());
        when(mockGetTask.addOnFailureListener(any())).thenReturn(mockGetTask);
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