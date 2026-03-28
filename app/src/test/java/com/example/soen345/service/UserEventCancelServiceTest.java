package com.example.soen345.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserEventCancelServiceTest {

    private FirebaseFirestore mockDb;
    private CollectionReference mockReservationRef;
    private DocumentReference mockDocRef;

    private UserEventCancelService service;

    @BeforeEach
    void setUp() {
        mockDb = mock(FirebaseFirestore.class);
        mockReservationRef = mock(CollectionReference.class);
        mockDocRef = mock(DocumentReference.class);
        when(mockDb.collection("reservations")).thenReturn(mockReservationRef);
        service = new UserEventCancelService(mockDb);
    }


    @Test
    void testCancelFailsNullUserId() {
        UserEventCancelService.CancelReservationCallback callback = mock(UserEventCancelService.CancelReservationCallback.class);
        service.cancelReservation(null, "123", callback);
        verify(callback).onFailure(any(IllegalArgumentException.class));
        verify(callback, never()).onSuccess();
    }

    @Test
    void testCancelFailsEmptyUserId() {
        UserEventCancelService.CancelReservationCallback callback = mock(UserEventCancelService.CancelReservationCallback.class);
        service.cancelReservation("", "123", callback);
        verify(callback).onFailure(any(IllegalArgumentException.class));
        verify(callback, never()).onSuccess();
    }

    @Test
    void testCancelFailsNullReservationId() {
        UserEventCancelService.CancelReservationCallback callback = mock(UserEventCancelService.CancelReservationCallback.class);
        service.cancelReservation("123", null, callback);
        verify(callback).onFailure(any(IllegalArgumentException.class));
        verify(callback, never()).onSuccess();
    }

    @Test
    void testCancelFailsEmptyReservationId() {
        UserEventCancelService.CancelReservationCallback callback = mock(UserEventCancelService.CancelReservationCallback.class);
        service.cancelReservation("123", "", callback);
        verify(callback).onFailure(any(IllegalArgumentException.class));
        verify(callback, never()).onSuccess();
    }

    // ===== Firestore tests =====

    @Test
    void testCancelSuccess() {
        Task<Void> mockDeleteTask = mock(Task.class);

        when(mockReservationRef.document("res123")).thenReturn(mockDocRef);
        when(mockDocRef.delete()).thenReturn(mockDeleteTask);
        when(mockDeleteTask.addOnSuccessListener(any())).thenReturn(mockDeleteTask);
        when(mockDeleteTask.addOnFailureListener(any())).thenReturn(mockDeleteTask);
        doAnswer(inv -> {
            OnSuccessListener<Void> listener = inv.getArgument(0);
            listener.onSuccess(null);
            return mockDeleteTask;
        }).when(mockDeleteTask).addOnSuccessListener(any());

        UserEventCancelService.CancelReservationCallback callback = mock(UserEventCancelService.CancelReservationCallback.class);
        service.cancelReservation("user123", "res123", callback);

        verify(callback).onSuccess();
        verify(callback, never()).onFailure(any());
    }

    @Test
    void testCancelFirestoreError() {
        Task<Void> mockDeleteTask = mock(Task.class);

        when(mockReservationRef.document("res123")).thenReturn(mockDocRef);
        when(mockDocRef.delete()).thenReturn(mockDeleteTask);
        when(mockDeleteTask.addOnSuccessListener(any())).thenReturn(mockDeleteTask);
        doAnswer(inv -> {
            OnFailureListener listener = inv.getArgument(0);
            listener.onFailure(new Exception("Network error"));
            return mockDeleteTask;
        }).when(mockDeleteTask).addOnFailureListener(any());

        UserEventCancelService.CancelReservationCallback callback = mock(UserEventCancelService.CancelReservationCallback.class);
        service.cancelReservation("user123", "res123", callback);

        verify(callback).onFailure(any(Exception.class));
        verify(callback, never()).onSuccess();
    }
}