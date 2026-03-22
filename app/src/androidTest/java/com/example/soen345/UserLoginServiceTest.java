package com.example.soen345;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soen345.service.AuthenticationException;
import com.example.soen345.service.UserLogInService;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class UserLoginServiceTest {
    private UserLogInService service;
    private String username = "username";
    private String password = "password";
    private String fullName = "Full Name";
    private String email  = "email@gmail.com";
    private String phone = "1234567890";
    private String role = "customer";
    @BeforeClass
    public static void setupClass() throws InterruptedException {
        FirebaseFirestore firestore = FirestoreInitializer.getInstance();

        // Clear existing users in emulator
        CountDownLatch latch = new CountDownLatch(1);
        firestore.collection("users").get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        firestore.collection("users").document(doc.getId()).delete();
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
    }
    @Before
    public void setUp() throws InterruptedException {
        service = new UserLogInService(FirebaseFirestore.getInstance());
        String userId = UUID.randomUUID().toString();
        User user = new User(userId,username,password,fullName,email, phone,role);

        CountDownLatch latch = new CountDownLatch(1);
        FirebaseFirestore.getInstance().collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(v -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
    }
    @Test
    public void userLogin_success() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        service.loginUser(username, password, new UserLogInService.UserLogInCallback() {
            @Override
            public void onSuccess(User user) {
                assertEquals(user.username, username);
                assertEquals(user.fullName, fullName);
                assertEquals(user.email, email);
                assertEquals(user.phone, phone);
                assertEquals(user.role, role);
                assertEquals(user.password, password);
                latch.countDown();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Callback not called in time");
        }
    }
    @Test
    public void userLogin_fail_wrong_username() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        service.loginUser(username+"1", password, new UserLogInService.UserLogInCallback() {
            @Override
            public void onSuccess(User user) {
            }

            @Override
            public void onFailure(Exception e) {
                assertTrue(e instanceof AuthenticationException);
                assertEquals("Invalid username or password", e.getMessage());
                latch.countDown();

            }
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Callback not called in time");
        }
    }
    @Test
    public void userLogin_fail_wrong_password() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        service.loginUser(username, password+"1", new UserLogInService.UserLogInCallback() {
            @Override
            public void onSuccess(User user) {
            }

            @Override
            public void onFailure(Exception e) {
                assertTrue(e instanceof AuthenticationException);
                assertEquals("Invalid username or password", e.getMessage());
                latch.countDown();

            }
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Callback not called in time");
        }
    }
    @After
    public void tearDown() throws InterruptedException {
        clearData();
    }
    private void clearData() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        FirebaseFirestore.getInstance().collection("users").get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        FirebaseFirestore.getInstance().collection("users").document(doc.getId()).delete();
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreClear", "Failed to clear: " + e.getMessage());
                    latch.countDown();
                });

        if (!latch.await(10, TimeUnit.SECONDS)) {
            Log.w("FirestoreClear", "Timeout while clearing Firestore");
        }
    }
}
