package com.example.soen345;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soen345.service.UserRegisterService;
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
public class UserRegisterServiceTest {
    private UserRegisterService service;
    private String username = "username";
    private String password = "password";
    private String fullName = "Full Name";
    private String email  = "email@gmail.com";
    private String phone = "1234567890";

    @BeforeClass
    public static void setupClass() {
        FirebaseFirestore firestore = FirestoreInitializer.getInstance();
    }
    @Before
    public void setUp() throws InterruptedException {
        service = new UserRegisterService(FirebaseFirestore.getInstance());
        clearData();
    }

    @Test
    public void registerUser_success() throws InterruptedException {
        String userId = UUID.randomUUID().toString();
        User user = new User(userId,username,password,fullName,email, phone,"customer");

        CountDownLatch latch = new CountDownLatch(1);

        service.registerUser(user, new UserRegisterService.UserRegisterCallback() {
            @Override
            public void onSuccess() {
                // Assert correct data is inserted
                FirebaseFirestore.getInstance().collection("users").document(userId).get()
                        .addOnSuccessListener(doc -> {
                            assertTrue(doc.exists()); // Document exists

                            assertEquals(user.username, doc.getString("username"));
                            assertEquals(user.fullName, doc.getString("fullName"));
                            assertEquals(user.email, doc.getString("email"));
                            assertEquals(user.phone, doc.getString("phone"));
                            assertEquals(user.role, doc.getString("role"));
                            assertEquals(user.password, doc.getString("password"));
                            assertEquals(user.userId, doc.getString("userId"));

                            latch.countDown();
                        })
                        .addOnFailureListener(e -> fail("Failed to get document: " + e.getMessage()));
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
    public void registerUser_fail_input_missing() throws InterruptedException {
        User[] testUsers = {
                new User(UUID.randomUUID().toString(), null, password, fullName, email, phone, "customer"), // missing username
                new User(UUID.randomUUID().toString(), "username", null, fullName, email, phone, "customer"), // missing password
                new User(UUID.randomUUID().toString(), "username", password, null, email, phone, "customer"), // missing fullName
                new User(UUID.randomUUID().toString(), "username", password, fullName, null, null, "customer"), // missing email and phone
                new User(UUID.randomUUID().toString(), "username", password, fullName, email, phone, null), // missing role
                new User(null, "username", password, fullName, email, phone, "customer") // missing userId
        };
        for (User u : testUsers) {
            testMissingField(u);
        }
    }
    private void testMissingField(User user) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.registerUser(user, new UserRegisterService.UserRegisterCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
                assertEquals("Invalid input", e.getMessage());
                latch.countDown();
            }
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Callback not called in time");
        }
    }

    @Test
    public void registerUser_fail_existingUser() throws InterruptedException {
        User user = new User(UUID.randomUUID().toString(), username, password, fullName, email, phone, "customer"); // missing username
        CountDownLatch latch = new CountDownLatch(1);
        service.registerUser(user, new UserRegisterService.UserRegisterCallback() {
            @Override
            public void onSuccess() {
                testInsertDuplicatedUser();
                latch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                fail("Setup user failed: " + e.getMessage());
            }
        });

        if (!latch.await(15, TimeUnit.SECONDS)) {
            fail("Callback not called in time");
        }
    }
    private void testInsertDuplicatedUser(){
        User[] testDuplicateUser = {
                new User(UUID.randomUUID().toString(), username, password, fullName, email+"1", phone+"1", "customer"),// Same username
                new User(UUID.randomUUID().toString(), username+"1", password, fullName, email, phone+"1", "customer"), // Same email
                new User(UUID.randomUUID().toString(), username+"1", password, fullName, email+"1", phone, "customer") // Same phone
        };
        for (User duplicatedUser : testDuplicateUser){
            service.registerUser(duplicatedUser, new UserRegisterService.UserRegisterCallback() {
                @Override
                public void onSuccess() {
                    fail("Duplicate user registration should have failed");
                }

                @Override
                public void onFailure(Exception e) {
                    assertTrue(e.getMessage().contains("already taken"));
                }
            });
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