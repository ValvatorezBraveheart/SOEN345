package com.example.soen345.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.soen345.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserRegisterServiceTest {

    @Mock FirebaseFirestore mockDb;
    @Mock CollectionReference mockUsersRef;
    @Mock Query mockUsernameQuery, mockEmailQuery, mockPhoneQuery;
    @Mock Task<QuerySnapshot> mockUsernameTask, mockEmailTask, mockPhoneTask;
    @Mock Task<List<Object>> mockCombinedTask;
    @Mock DocumentReference mockDocRef;

    private UserRegisterService service;

    @BeforeEach
    void setUp() {
        when(mockDb.collection("users")).thenReturn(mockUsersRef);
        service = new UserRegisterService(mockDb);
    }

    // ===== validateInputs tests =====

    @ParameterizedTest
    @CsvSource(value = {
            "NULL",
            "''"
    }, nullValues = "NULL")
    void testRegisterFailsMissingUserId(String userId) {
        User user = buildValidUser();
        user.userId = userId;
        UserRegisterService.UserRegisterCallback callback = mock(UserRegisterService.UserRegisterCallback.class);
        service.registerUser(user, callback);
        verify(callback).onFailure(any(IllegalArgumentException.class));
        verify(callback, never()).onSuccess();
    }
    @ParameterizedTest
    @CsvSource(value = {
            "NULL",
            "''"
    }, nullValues = "NULL")
    void testRegisterFailsMissingFullName(String fullName) {
        User user = buildValidUser();
        user.fullName = fullName;
        UserRegisterService.UserRegisterCallback callback = mock(UserRegisterService.UserRegisterCallback.class);
        service.registerUser(user, callback);
        verify(callback).onFailure(any(IllegalArgumentException.class));
        verify(callback, never()).onSuccess();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL",
            "''"
    }, nullValues = "NULL")
    void testRegisterFailsMissingUsername(String name) {
        User user = buildValidUser();
        user.username = name;
        UserRegisterService.UserRegisterCallback callback = mock(UserRegisterService.UserRegisterCallback.class);
        service.registerUser(user, callback);
        verify(callback).onFailure(any(IllegalArgumentException.class));
        verify(callback, never()).onSuccess();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL, NULL",
            "NULL, ''",
            "'', NULL",
            "'', ''"
    }, nullValues = "NULL")
    void testRegisterFailsMissingEmailAndPhone(String email, String phone) {
        User user = buildValidUser();
        user.email = email;
        user.phone = phone;
        UserRegisterService.UserRegisterCallback callback = mock(UserRegisterService.UserRegisterCallback.class);
        service.registerUser(user, callback);
        verify(callback).onFailure(any(IllegalArgumentException.class));
        verify(callback, never()).onSuccess();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL",
            "''"
    }, nullValues = "NULL")
    void testRegisterFailsMissingPassword(String password) {
        User user = buildValidUser();
        user.password = password;
        UserRegisterService.UserRegisterCallback callback = mock(UserRegisterService.UserRegisterCallback.class);
        service.registerUser(user, callback);
        verify(callback).onFailure(any(IllegalArgumentException.class));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NULL",
            "''"
    }, nullValues = "NULL")
    void testRegisterFailsMissingRole(String role) {
        User user = buildValidUser();
        user.role = role;
        UserRegisterService.UserRegisterCallback callback = mock(UserRegisterService.UserRegisterCallback.class);
        service.registerUser(user, callback);
        verify(callback).onFailure(any(IllegalArgumentException.class));
    }

    // ===== validateExistingAccount tests =====

    @Test
    void testRegisterSuccessNoConflicts() {
        User user = buildValidUser();

        try (MockedStatic<Tasks> mockedTasks = mockStatic(Tasks.class)) {
            setupFirestoreQueries();
            setupTasksWhenAllSuccess(mockedTasks, false, false, false);

            Task mockSetTask = mock(Task.class);
            when(mockSetTask.addOnSuccessListener(any())).thenReturn(mockSetTask);
            doAnswer(inv -> {
                OnSuccessListener<Void> listener = inv.getArgument(0);
                listener.onSuccess(null);
                return mockSetTask;
            }).when(mockSetTask).addOnSuccessListener(any());
            when(mockSetTask.addOnFailureListener(any())).thenReturn(mockSetTask);

            when(mockUsersRef.document(user.userId)).thenReturn(mockDocRef);
            when(mockDocRef.set(any())).thenReturn(mockSetTask);

            UserRegisterService.UserRegisterCallback callback = mock(UserRegisterService.UserRegisterCallback.class);
            service.registerUser(user, callback);

            verify(callback).onSuccess();
            verify(callback, never()).onFailure(any());
        }
    }

    @Test
    void testRegisterFailsUsernameTaken() {
        User user = buildValidUser();

        try (MockedStatic<Tasks> mockedTasks = mockStatic(Tasks.class)) {
            setupFirestoreQueries();
            setupTasksWhenAllSuccess(mockedTasks, true, false, false);

            UserRegisterService.UserRegisterCallback callback = mock(UserRegisterService.UserRegisterCallback.class);
            service.registerUser(user, callback);

            verify(callback).onFailure(argThat(e ->
                    Objects.equals(e.getMessage(), "Username already taken")));
            verify(callback, never()).onSuccess();
        }
    }

    @Test
    void testRegisterFailsEmailTaken() {
        User user = buildValidUser();

        try (MockedStatic<Tasks> mockedTasks = mockStatic(Tasks.class)) {
            setupFirestoreQueries();
            setupTasksWhenAllSuccess(mockedTasks, false, true, false);

            UserRegisterService.UserRegisterCallback callback = mock(UserRegisterService.UserRegisterCallback.class);
            service.registerUser(user, callback);

            verify(callback).onFailure(argThat(e ->
                    Objects.equals(e.getMessage(), "Email already taken")));
        }
    }

    @Test
    void testRegisterFailsPhoneTaken() {
        User user = buildValidUser();

        try (MockedStatic<Tasks> mockedTasks = mockStatic(Tasks.class)) {
            setupFirestoreQueries();
            setupTasksWhenAllSuccess(mockedTasks, false, false, true);

            UserRegisterService.UserRegisterCallback callback = mock(UserRegisterService.UserRegisterCallback.class);
            service.registerUser(user, callback);

            verify(callback).onFailure(argThat(e ->
                    Objects.equals(e.getMessage(), "Phone already taken")));
        }
    }

    @Test
    void testRegisterFirestoreError() {
        User user = buildValidUser();

        try (MockedStatic<Tasks> mockedTasks = mockStatic(Tasks.class)) {
            setupFirestoreQueries();

            mockedTasks.when(() -> Tasks.whenAllSuccess(
                            mockUsernameTask, mockEmailTask, mockPhoneTask))
                    .thenReturn(mockCombinedTask);

            when(mockCombinedTask.addOnSuccessListener(any())).thenReturn(mockCombinedTask);
            doAnswer(inv -> {
                OnFailureListener listener = inv.getArgument(0);
                listener.onFailure(new Exception("Network error"));
                return mockCombinedTask;
            }).when(mockCombinedTask).addOnFailureListener(any());

            UserRegisterService.UserRegisterCallback callback = mock(UserRegisterService.UserRegisterCallback.class);
            service.registerUser(user, callback);

            verify(callback).onFailure(argThat(e ->
                    Objects.requireNonNull(e.getMessage()).contains("Failed to validate")));
        }
    }

    // ===== Helpers =====

    private void setupFirestoreQueries() {
        when(mockUsersRef.whereEqualTo(eq("username"), any())).thenReturn(mockUsernameQuery);
        when(mockUsersRef.whereEqualTo(eq("email"), any())).thenReturn(mockEmailQuery);
        when(mockUsersRef.whereEqualTo(eq("phone"), any())).thenReturn(mockPhoneQuery);
        when(mockUsernameQuery.get()).thenReturn(mockUsernameTask);
        when(mockEmailQuery.get()).thenReturn(mockEmailTask);
        when(mockPhoneQuery.get()).thenReturn(mockPhoneTask);
    }

    private void setupTasksWhenAllSuccess(MockedStatic<Tasks> mockedTasks,
                                          boolean usernameTaken,
                                          boolean emailTaken,
                                          boolean phoneTaken) {
        QuerySnapshot usernameSnapshot = mockSnapshot(usernameTaken);
        QuerySnapshot emailSnapshot    = mockSnapshot(emailTaken);
        QuerySnapshot phoneSnapshot    = mockSnapshot(phoneTaken);

        mockedTasks.when(() -> Tasks.whenAllSuccess(
                        mockUsernameTask, mockEmailTask, mockPhoneTask))
                .thenReturn(mockCombinedTask);

        doAnswer(inv -> {
            OnSuccessListener<List<Object>> listener = inv.getArgument(0);
            listener.onSuccess(Arrays.asList(usernameSnapshot, emailSnapshot, phoneSnapshot));
            return mockCombinedTask;
        }).when(mockCombinedTask).addOnSuccessListener(any());

        when(mockCombinedTask.addOnFailureListener(any())).thenReturn(mockCombinedTask);
    }

    private QuerySnapshot mockSnapshot(boolean nonEmpty) {
        QuerySnapshot snapshot = mock(QuerySnapshot.class);
        lenient().when(snapshot.isEmpty()).thenReturn(!nonEmpty); // Make it lenient so mockito doesn't complain unused
        return snapshot;
    }

    private User buildValidUser() {
        User user = new User();
        user.username = "john";
        user.password = "pass123";
        user.fullName = "John Doe";
        user.email = "jd@example.com";
        user.phone = "123456789";
        user.role = "admin";
        user.userId = "12345678990";
        return user;
    }
}