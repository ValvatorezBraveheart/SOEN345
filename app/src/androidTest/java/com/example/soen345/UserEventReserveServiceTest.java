package com.example.soen345;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soen345.service.UserEventReserveService;
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
public class UserEventReserveServiceTest {

    private UserEventReserveService service;
    private FirebaseFirestore firestore;

    private final String userId = "test-user-reserve";
    private final String eventId = "test-event-reserve";

    @BeforeClass
    public static void setupClass() throws InterruptedException {
        FirebaseFirestore firestore = FirestoreInitializer.getInstance();

        CountDownLatch latch = new CountDownLatch(3);

        firestore.collection("users").get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        firestore.collection("users").document(doc.getId()).delete();
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());

        firestore.collection("events").get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        firestore.collection("events").document(doc.getId()).delete();
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());

        firestore.collection("reservations").get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        firestore.collection("reservations").document(doc.getId()).delete();
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());

        latch.await(10, TimeUnit.SECONDS);
    }

    @Before
    public void setUp() throws InterruptedException {
        firestore = FirebaseFirestore.getInstance();
        service = new UserEventReserveService(firestore);

        // Seed a real user and a real event for tests that need them
        User user = new User(userId, "reserveUser", "pass", "Reserve User", "reserve@email.com", "5555555555",
                "customer");
        Event event = new Event(eventId, "Test Event", "2026-05-01", "10:00", "12:00", "Montreal", "Music",
                "Test event", "admin-id");

        CountDownLatch latch = new CountDownLatch(2);

        firestore.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(v -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());

        firestore.collection("events").document(eventId)
                .set(event)
                .addOnSuccessListener(v -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void reserveEvent_success() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final String[] capturedReservationId = new String[1];

        service.reserveEvent(userId, eventId, new UserEventReserveService.ReserveEventCallback() {
            @Override
            public void onSuccess(String reservationId) {
                capturedReservationId[0] = reservationId;
                latch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                fail("Expected success but got failure: " + e.getMessage());
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Callback not called in time");
        }

        assertNotNull(capturedReservationId[0]);

        // Verify reservation actually exists in Firestore
        CountDownLatch verifyLatch = new CountDownLatch(1);
        firestore.collection("reservations").document(capturedReservationId[0]).get()
                .addOnSuccessListener(doc -> {
                    assertTrue(doc.exists());
                    assertEquals(userId, doc.getString("userId"));
                    assertEquals(eventId, doc.getString("eventId"));
                    verifyLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    fail("Failed to verify reservation: " + e.getMessage());
                    verifyLatch.countDown();
                });

        if (!verifyLatch.await(5, TimeUnit.SECONDS)) {
            fail("Verify callback not called in time");
        }
    }

    @Test
    public void reserveEvent_fail_alreadyRegistered() throws InterruptedException {
        // First reservation — should succeed
        CountDownLatch firstLatch = new CountDownLatch(1);
        service.reserveEvent(userId, eventId, new UserEventReserveService.ReserveEventCallback() {
            @Override
            public void onSuccess(String reservationId) {
                firstLatch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                fail("First reservation should succeed: " + e.getMessage());
            }
        });
        if (!firstLatch.await(5, TimeUnit.SECONDS)) {
            fail("First callback not called in time");
        }

        // Second reservation — should fail with duplicate
        CountDownLatch secondLatch = new CountDownLatch(1);
        service.reserveEvent(userId, eventId, new UserEventReserveService.ReserveEventCallback() {
            @Override
            public void onSuccess(String reservationId) {
                fail("Expected failure for duplicate reservation but got success");
            }

            @Override
            public void onFailure(Exception e) {
                assertTrue(e instanceof IllegalStateException);
                assertEquals("User is already registered for this event", e.getMessage());
                secondLatch.countDown();
            }
        });
        if (!secondLatch.await(5, TimeUnit.SECONDS)) {
            fail("Second callback not called in time");
        }
    }

    @Test
    public void reserveEvent_fail_eventNotFound() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.reserveEvent(userId, "nonexistent-event-id", new UserEventReserveService.ReserveEventCallback() {
            @Override
            public void onSuccess(String reservationId) {
                fail("Expected failure but got success");
            }

            @Override
            public void onFailure(Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
                assertEquals("Event not found", e.getMessage());
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Callback not called in time");
        }
    }

    @Test
    public void reserveEvent_fail_userNotFound() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.reserveEvent("nonexistent-user-id", eventId, new UserEventReserveService.ReserveEventCallback() {
            @Override
            public void onSuccess(String reservationId) {
                fail("Expected failure but got success");
            }

            @Override
            public void onFailure(Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
                assertEquals("User not found", e.getMessage());
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Callback not called in time");
        }
    }

    @Test
    public void reserveEvent_fail_nullUserId() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.reserveEvent(null, eventId, new UserEventReserveService.ReserveEventCallback() {
            @Override
            public void onSuccess(String reservationId) {
                fail("Expected failure but got success");
            }

            @Override
            public void onFailure(Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Callback not called in time");
        }
    }

    @Test
    public void reserveEvent_fail_nullEventId() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.reserveEvent(userId, null, new UserEventReserveService.ReserveEventCallback() {
            @Override
            public void onSuccess(String reservationId) {
                fail("Expected failure but got success");
            }

            @Override
            public void onFailure(Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
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
        CountDownLatch latch = new CountDownLatch(3);

        FirebaseFirestore.getInstance().collection("users").get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        FirebaseFirestore.getInstance().collection("users").document(doc.getId()).delete();
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreClear", "Failed to clear users: " + e.getMessage());
                    latch.countDown();
                });

        FirebaseFirestore.getInstance().collection("events").get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        FirebaseFirestore.getInstance().collection("events").document(doc.getId()).delete();
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreClear", "Failed to clear events: " + e.getMessage());
                    latch.countDown();
                });

        FirebaseFirestore.getInstance().collection("reservations").get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        FirebaseFirestore.getInstance().collection("reservations").document(doc.getId()).delete();
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreClear", "Failed to clear reservations: " + e.getMessage());
                    latch.countDown();
                });

        if (!latch.await(10, TimeUnit.SECONDS)) {
            Log.w("FirestoreClear", "Timeout while clearing Firestore");
        }
    }
}
