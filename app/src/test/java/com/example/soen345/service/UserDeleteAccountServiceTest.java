package com.example.soen345.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserDeleteAccountServiceTest {

    @Mock private FirebaseFirestore mockFirestore;
    @Mock private CollectionReference mockUsersRef;
    @Mock private DocumentReference mockDocumentRef;
    @Mock private Task<DocumentSnapshot> mockGetTask;
    @Mock private Task<Void> mockDeleteTask;
    @Mock private DocumentSnapshot mockDocumentSnapshot;
    @Mock private UserDeleteAccountService.UserDeleteCallback mockCallback;

    private UserDeleteAccountService service;

    @BeforeEach
    void setUp() {
        when(mockFirestore.collection("users")).thenReturn(mockUsersRef);
        when(mockUsersRef.document(anyString())).thenReturn(mockDocumentRef);
        when(mockDocumentRef.get()).thenReturn(mockGetTask);
        when(mockDocumentRef.delete()).thenReturn(mockDeleteTask);

        service = new UserDeleteAccountService(mockFirestore);
    }

    // Empty/Null
    @Test
    void deleteUser_nullUserId_callsOnFailureImmediately() {
        service.deleteUser(null, mockCallback);
        verify(mockCallback).onFailure(any(IllegalArgumentException.class));
        verify(mockCallback, never()).onSuccess();
    }

    @Test
    void deleteUser_emptyUserId_callsOnFailureImmediately() {
        service.deleteUser("", mockCallback);
        verify(mockCallback).onFailure(any(IllegalArgumentException.class));
        verify(mockCallback, never()).onSuccess();
    }

    // Account does not exist
    @Test
    void deleteUser_accountNotFound_callsOnFailureWithMessage() {
        when(mockGetTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<DocumentSnapshot> listener = invocation.getArgument(0);
            when(mockDocumentSnapshot.exists()).thenReturn(false);
            listener.onSuccess(mockDocumentSnapshot);
            return mockGetTask;
        });
        when(mockGetTask.addOnFailureListener(any())).thenReturn(mockGetTask);
        service.deleteUser("123", mockCallback);
        verify(mockCallback).onFailure(any(IllegalArgumentException.class));
        verify(mockDocumentRef, never()).delete();
    }

    // Firestore error during validation
    @Test
    void deleteUser_firestoreErrorDuringValidation_callsOnFailure() {
        when(mockGetTask.addOnSuccessListener(any())).thenReturn(mockGetTask);
        when(mockGetTask.addOnFailureListener(any())).thenAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(new Exception("Network error"));
            return mockGetTask;
        });
        service.deleteUser("123", mockCallback);
        verify(mockCallback).onFailure(any(IllegalArgumentException.class));
        verify(mockDocumentRef, never()).delete();
    }

    // Validate success but delete fails
    @Test
    void deleteUser_validUserButDeleteFails_callsOnFailure() {
        Exception deleteError = new Exception("Delete failed");

        when(mockGetTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<DocumentSnapshot> listener = invocation.getArgument(0);
            when(mockDocumentSnapshot.exists()).thenReturn(true);
            listener.onSuccess(mockDocumentSnapshot);
            return mockGetTask;
        });
        when(mockGetTask.addOnFailureListener(any())).thenReturn(mockGetTask);

        when(mockDeleteTask.addOnSuccessListener(any())).thenReturn(mockDeleteTask);
        when(mockDeleteTask.addOnFailureListener(any())).thenAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(deleteError);
            return mockDeleteTask;
        });

        service.deleteUser("123", mockCallback);

        verify(mockCallback).onFailure(deleteError);
        verify(mockCallback, never()).onSuccess();
    }

    // Success
    @Test
    void deleteUser_validUserAndDeleteSucceeds_callsOnSuccess() {
        when(mockGetTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<DocumentSnapshot> listener = invocation.getArgument(0);
            when(mockDocumentSnapshot.exists()).thenReturn(true);
            listener.onSuccess(mockDocumentSnapshot);
            return mockGetTask;
        });
        when(mockGetTask.addOnFailureListener(any())).thenReturn(mockGetTask);

        when(mockDeleteTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockDeleteTask;
        });
        when(mockDeleteTask.addOnFailureListener(any())).thenReturn(mockDeleteTask);

        service.deleteUser("123", mockCallback);

        verify(mockCallback).onSuccess();
        verify(mockCallback, never()).onFailure(any());
    }

}