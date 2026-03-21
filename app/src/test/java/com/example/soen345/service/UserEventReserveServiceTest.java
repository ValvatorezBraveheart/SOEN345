package com.example.soen345.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Objects;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserEventReserveServiceTest {

    @Mock
    private FirebaseFirestore mockDb;
    @Mock private CollectionReference mockReservationRef;
    @Mock private CollectionReference mockUsersRef;
    @Mock private CollectionReference mockEventsRef;
    @Mock private Query mockQuery;
    @Mock private DocumentReference mockUsersDocRef;
    @Mock private DocumentReference mockEventsDocRef;
    @Mock private DocumentReference mockReservationsDocRef;
    @Mock private Task<QuerySnapshot> mockQueryTask;
    @Mock private Task<DocumentSnapshot> mockUsersDocTask;
    @Mock private Task<DocumentSnapshot> mockEventsDocTask;
    @Mock private Task<DocumentSnapshot> mockReservationsDocTask;
    @Mock private Task<Void> mockVoidTask;
    @Mock private QuerySnapshot mockQuerySnapshot;
    @Mock private DocumentSnapshot mockDocSnapshot;

    @Mock private UserEventReserveService.ReserveEventCallback mockCallback;
    @Mock private UserEventReserveService.AlreadyRegisteredCallback mockAlreadyRegisteredCallback;

    private UserEventReserveService service;

    private final String userId = "123";
    private final String eventId = "456";

    @BeforeEach
    public void setUp() {
        when(mockDb.collection("reservations")).thenReturn(mockReservationRef);
        when(mockDb.collection("users")).thenReturn(mockUsersRef);
        when(mockDb.collection("events")).thenReturn(mockEventsRef);
        service = new UserEventReserveService(mockDb);
    }


    @Test
    public void reserveEvent_nullUserId_callsFailure() {
        service.reserveEvent(null, eventId, mockCallback);
        verify(mockCallback).onFailure(argThat(e ->
                e instanceof IllegalArgumentException &&
                        Objects.equals(e.getMessage(), "Invalid userId or eventId")));
    }

    @Test
    public void reserveEvent_emptyUserId_callsFailure() {
        service.reserveEvent("", eventId, mockCallback);
        verify(mockCallback).onFailure(argThat(e ->
                e instanceof IllegalArgumentException &&
                        Objects.equals(e.getMessage(), "Invalid userId or eventId")));
    }

    @Test
    public void reserveEvent_nullEventId_callsFailure() {
        service.reserveEvent(userId, null, mockCallback);
        verify(mockCallback).onFailure(argThat(e ->
                e instanceof IllegalArgumentException &&
                        Objects.equals(e.getMessage(), "Invalid userId or eventId")));
    }

    @Test
    public void reserveEvent_emptyEventId_callsFailure() {
        service.reserveEvent(userId, "", mockCallback);
        verify(mockCallback).onFailure(argThat(e ->
                e instanceof IllegalArgumentException &&
                        Objects.equals(e.getMessage(), "Invalid userId or eventId")));
    }


    @Test
    public void reserveEvent_alreadyRegistered_callsFailure() {
        when(mockReservationRef.whereEqualTo("userId", userId)).thenReturn(mockQuery);
        when(mockQuery.whereEqualTo("eventId", eventId)).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockQueryTask);
        when(mockQueryTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<QuerySnapshot> listener = invocation.getArgument(0);
            when(mockQuerySnapshot.isEmpty()).thenReturn(false); // already registered
            listener.onSuccess(mockQuerySnapshot);
            return mockQueryTask;
        });
        when(mockQueryTask.addOnFailureListener(any())).thenReturn(mockQueryTask);

        service.reserveEvent(userId, eventId, mockCallback);

        verify(mockCallback).onFailure(argThat(e ->
                e instanceof IllegalStateException &&
                        Objects.equals(e.getMessage(), "User is already registered for this event")));
    }


    @Test
    public void reserveEvent_eventNotFound_callsFailure() {
        setupNotRegistered();

        when(mockEventsRef.document(eventId)).thenReturn(mockEventsDocRef);
        when(mockEventsDocRef.get()).thenReturn(mockEventsDocTask);
        when(mockEventsDocTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<DocumentSnapshot> listener = invocation.getArgument(0);
            when(mockDocSnapshot.exists()).thenReturn(false); // event not found
            listener.onSuccess(mockDocSnapshot);
            return mockEventsDocTask;
        });
        when(mockEventsDocTask.addOnFailureListener(any())).thenReturn(mockEventsDocTask);

        service.reserveEvent(userId, eventId, mockCallback);

        verify(mockCallback).onFailure(argThat(e ->
                e instanceof IllegalArgumentException &&
                        Objects.equals(e.getMessage(), "Event not found")));
    }


    @Test
    public void reserveEvent_userNotFound_callsFailure() {
        setupNotRegistered();
        setupEventExists();

        when(mockUsersRef.document(userId)).thenReturn(mockReservationsDocRef);
        when(mockReservationsDocRef.get()).thenReturn(mockReservationsDocTask);
        when(mockReservationsDocTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<DocumentSnapshot> listener = invocation.getArgument(0);
            when(mockDocSnapshot.exists()).thenReturn(false); // user not found
            listener.onSuccess(mockDocSnapshot);
            return mockReservationsDocTask;
        });
        when(mockReservationsDocTask.addOnFailureListener(any())).thenReturn(mockReservationsDocTask);

        service.reserveEvent(userId, eventId, mockCallback);

        verify(mockCallback).onFailure(argThat(e ->
                e instanceof IllegalArgumentException &&
                        Objects.equals(e.getMessage(), "User not found")));
    }


    @Test
    public void reserveEvent_success_callsOnSuccess() {
        setupNotRegistered();
        setupEventExists();
        setupUserExists();

        when(mockReservationRef.document(any())).thenReturn(mockReservationsDocRef);
        when(mockReservationsDocRef.set(any())).thenReturn(mockVoidTask);
        when(mockVoidTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockVoidTask;
        });
        when(mockVoidTask.addOnFailureListener(any())).thenReturn(mockVoidTask);

        service.reserveEvent(userId, eventId, mockCallback);

        verify(mockCallback).onSuccess(any());
    }


    @Test
    public void isAlreadyRegistered_true_whenReservationExists() {
        when(mockReservationRef.whereEqualTo("userId", userId)).thenReturn(mockQuery);
        when(mockQuery.whereEqualTo("eventId", eventId)).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockQueryTask);
        when(mockQueryTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<QuerySnapshot> listener = invocation.getArgument(0);
            when(mockQuerySnapshot.isEmpty()).thenReturn(false);
            listener.onSuccess(mockQuerySnapshot);
            return mockQueryTask;
        });
        when(mockQueryTask.addOnFailureListener(any())).thenReturn(mockQueryTask);

        service.isAlreadyRegistered(userId, eventId, mockAlreadyRegisteredCallback);

        verify(mockAlreadyRegisteredCallback).onResult(true, null);
    }

    @Test
    public void isAlreadyRegistered_false_whenNoReservation() {
        when(mockReservationRef.whereEqualTo("userId", userId)).thenReturn(mockQuery);
        when(mockQuery.whereEqualTo("eventId", eventId)).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockQueryTask);
        when(mockQueryTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<QuerySnapshot> listener = invocation.getArgument(0);
            when(mockQuerySnapshot.isEmpty()).thenReturn(true);
            listener.onSuccess(mockQuerySnapshot);
            return mockQueryTask;
        });
        when(mockQueryTask.addOnFailureListener(any())).thenReturn(mockQueryTask);

        service.isAlreadyRegistered(userId, eventId, mockAlreadyRegisteredCallback);

        verify(mockAlreadyRegisteredCallback).onResult(false, null);
    }


    private void setupNotRegistered() {
        when(mockReservationRef.whereEqualTo("userId", userId)).thenReturn(mockQuery);
        when(mockQuery.whereEqualTo("eventId", eventId)).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockQueryTask);
        when(mockQueryTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<QuerySnapshot> listener = invocation.getArgument(0);
            when(mockQuerySnapshot.isEmpty()).thenReturn(true);
            listener.onSuccess(mockQuerySnapshot);
            return mockQueryTask;
        });
        when(mockQueryTask.addOnFailureListener(any())).thenReturn(mockQueryTask);
    }

    private void setupEventExists() {
        when(mockEventsRef.document(eventId)).thenReturn(mockEventsDocRef);
        when(mockEventsDocRef.get()).thenReturn(mockEventsDocTask);
        when(mockEventsDocTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<DocumentSnapshot> listener = invocation.getArgument(0);
            when(mockDocSnapshot.exists()).thenReturn(true);
            listener.onSuccess(mockDocSnapshot);
            return mockEventsDocTask;
        });
        when(mockEventsDocTask.addOnFailureListener(any())).thenReturn(mockEventsDocTask);
    }

    private void setupUserExists() {
        when(mockUsersRef.document(userId)).thenReturn(mockUsersDocRef);
        when(mockUsersDocRef.get()).thenReturn(mockUsersDocTask);
        when(mockUsersDocTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<DocumentSnapshot> listener = invocation.getArgument(0);
            when(mockDocSnapshot.exists()).thenReturn(true);
            listener.onSuccess(mockDocSnapshot);
            return mockUsersDocTask;
        });
        when(mockUsersDocTask.addOnFailureListener(any())).thenReturn(mockUsersDocTask);
    }
}