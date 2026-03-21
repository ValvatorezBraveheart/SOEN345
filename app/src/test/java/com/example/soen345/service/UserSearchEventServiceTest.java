package com.example.soen345.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.soen345.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserSearchEventServiceTest {

    @Mock
    private FirebaseFirestore mockDb;
    @Mock private CollectionReference mockEventsRef;
    @Mock private Query mockQuery;
    @Mock private Task<QuerySnapshot> mockTask;
    @Mock private QuerySnapshot mockSnapshot;

    @Mock private OnSuccessListener<List<Event>> mockOnSuccess;
    @Mock private OnFailureListener mockOnFailure;

    private UserSearchEventService service;

    @BeforeEach
    public void setUp() {
        when(mockDb.collection("events")).thenReturn(mockEventsRef);
        service = new UserSearchEventService(mockDb);
    }

    // No filter applied
    @Test
    public void getEvents_noFilters_queriesAllEvents() {
        when(mockEventsRef.get()).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenReturn(mockTask);
        when(mockTask.addOnFailureListener(any())).thenReturn(mockTask);

        service.getEvents(null, null, null, mockOnSuccess, mockOnFailure);

        verify(mockEventsRef).get();
        verify(mockEventsRef, never()).whereEqualTo(eq("category"), any());
        verify(mockEventsRef, never()).whereEqualTo(eq("location"), any());
        verify(mockEventsRef, never()).whereEqualTo(eq("date"), any());
    }

    // Filters
    @Test
    public void getEvents_categoryOnly_appliesCategoryFilter() {
        when(mockEventsRef.whereEqualTo("category", "Music")).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenReturn(mockTask);
        when(mockTask.addOnFailureListener(any())).thenReturn(mockTask);

        service.getEvents("Music", null, null, mockOnSuccess, mockOnFailure);

        verify(mockEventsRef).whereEqualTo("category", "Music");
        verify(mockQuery, never()).whereEqualTo(eq("location"), any());
        verify(mockQuery, never()).whereEqualTo(eq("date"), any());
    }

    @Test
    public void getEvents_locationOnly_appliesLocationFilter() {
        when(mockEventsRef.whereEqualTo("location", "Montreal")).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenReturn(mockTask);
        when(mockTask.addOnFailureListener(any())).thenReturn(mockTask);

        service.getEvents(null, "Montreal", null, mockOnSuccess, mockOnFailure);

        verify(mockEventsRef).whereEqualTo("location", "Montreal");
    }

    @Test
    public void getEvents_dateOnly_appliesDateFilter() {
        when(mockEventsRef.whereEqualTo("date", "2026-03-21")).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenReturn(mockTask);
        when(mockTask.addOnFailureListener(any())).thenReturn(mockTask);

        service.getEvents(null, null, "2026-03-21", mockOnSuccess, mockOnFailure);

        verify(mockEventsRef).whereEqualTo("date", "2026-03-21");
    }

    @Test
    public void getEvents_allFilters_appliesAllFilters() {
        when(mockEventsRef.whereEqualTo("category", "Music")).thenReturn(mockQuery);
        when(mockQuery.whereEqualTo("location", "Montreal")).thenReturn(mockQuery);
        when(mockQuery.whereEqualTo("date", "2026-03-21")).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenReturn(mockTask);
        when(mockTask.addOnFailureListener(any())).thenReturn(mockTask);

        service.getEvents("Music", "Montreal", "2026-03-21", mockOnSuccess, mockOnFailure);

        verify(mockEventsRef).whereEqualTo("category", "Music");
        verify(mockQuery).whereEqualTo("location", "Montreal");
        verify(mockQuery).whereEqualTo("date", "2026-03-21");
    }
    // On Sucess
    @Test
    public void getEvents_onSuccess_returnsEventList() {
        List<Event> fakeEvents = Arrays.asList(new Event(), new Event());

        when(mockEventsRef.get()).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<QuerySnapshot> listener = invocation.getArgument(0);
            when(mockSnapshot.toObjects(Event.class)).thenReturn(fakeEvents);
            listener.onSuccess(mockSnapshot);
            return mockTask;
        });
        when(mockTask.addOnFailureListener(any())).thenReturn(mockTask);

        service.getEvents(null, null, null, mockOnSuccess, mockOnFailure);

        verify(mockOnSuccess).onSuccess(fakeEvents);
    }
    // Fail
    @Test
    public void getEvents_onFailure_callsFailureListener() {
        Exception fakeException = new Exception("Firestore error");

        when(mockEventsRef.get()).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenReturn(mockTask);
        when(mockTask.addOnFailureListener(any())).thenAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(fakeException);
            return mockTask;
        });

        service.getEvents(null, null, null, mockOnSuccess, mockOnFailure);

        verify(mockOnFailure).onFailure(fakeException);
    }
}