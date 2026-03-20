package com.example.soen345.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.soen345.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserLogInServiceTest {

    @Mock
    FirebaseFirestore mockDb;
    @Mock
    CollectionReference mockUsersRef;
    @Mock
    Query mockQuery;
    @Mock
    Task<QuerySnapshot> mockTask;
    @Mock
    QuerySnapshot mockSnapshot;

    private UserLogInService service;

    @BeforeEach
    void setUp() {
        when(mockDb.collection("users")).thenReturn(mockUsersRef);
        when(mockUsersRef.whereEqualTo(eq("username"), any())).thenReturn(mockQuery);
        when(mockQuery.whereEqualTo(eq("password"), any())).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockTask);
        service = new UserLogInService(mockDb);
    }

    @Test
    void testLoginSuccess() {
        QueryDocumentSnapshot mockDoc = mock(QueryDocumentSnapshot.class);
        User expectedUser = new User("123", "john", "pass123", "John Doe", "jd@gmail.com", "123456", "admin");

        when(mockSnapshot.isEmpty()).thenReturn(false);
        when(mockSnapshot.getDocuments()).thenReturn(List.of(mockDoc));
        when(mockDoc.toObject(User.class)).thenReturn(expectedUser);

        doAnswer(invocation -> {
            OnSuccessListener<QuerySnapshot> listener = invocation.getArgument(0);
            listener.onSuccess(mockSnapshot);
            return mockTask;
        }).when(mockTask).addOnSuccessListener(any());
        when(mockTask.addOnFailureListener(any())).thenReturn(mockTask);

        UserLogInService.UserLogInCallback callback = mock(UserLogInService.UserLogInCallback.class);
        service.loginUser("john", "pass123", callback);

        verify(callback).onSuccess(expectedUser);
        verify(callback, never()).onFailure(any());
    }

    @Test
    void testLoginFailureInvalidCredentials() {
        when(mockSnapshot.isEmpty()).thenReturn(true);

        doAnswer(invocation -> {
            OnSuccessListener<QuerySnapshot> listener = invocation.getArgument(0);
            listener.onSuccess(mockSnapshot);
            return mockTask;
        }).when(mockTask).addOnSuccessListener(any());
        when(mockTask.addOnFailureListener(any())).thenReturn(mockTask);

        UserLogInService.UserLogInCallback callback = mock(UserLogInService.UserLogInCallback.class);
        service.loginUser("wrong", "wrong", callback);

        verify(callback).onFailure(any(AuthenticationException.class));
        verify(callback, never()).onSuccess(any());
    }

    @Test
    void testLoginFirestoreError() {
        Exception firestoreError = new Exception("Network error");

        when(mockTask.addOnSuccessListener(any())).thenReturn(mockTask);
        doAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(firestoreError);
            return mockTask;
        }).when(mockTask).addOnFailureListener(any());

        UserLogInService.UserLogInCallback callback = mock(UserLogInService.UserLogInCallback.class);
        service.loginUser("error", "error", callback);

        verify(callback).onFailure(firestoreError);
        verify(callback, never()).onSuccess(any());
    }
}