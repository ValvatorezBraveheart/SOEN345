package com.example.soen345;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soen345.service.AdminAddEventService;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class AdminAddEventServiceTest {

    private AdminAddEventService service;
    private FirebaseFirestore firestore;

    private final String adminId = "12345678990";
    private final String eventId = "555555";

    @BeforeClass
    public static void setupClass() throws InterruptedException {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.useEmulator("10.0.2.2", 8080);

        // Clear existing events and users
        CountDownLatch latch = new CountDownLatch(2);

        firestore.collection("events").get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        firestore.collection("events").document(doc.getId()).delete();
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());

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
        firestore = FirebaseFirestore.getInstance();
        service = new AdminAddEventService(firestore);

        // Insert a real admin user into emulator
        User admin = new User(adminId, "adminUser", "pass123", "Admin Name", "admin@email.com", "0000000000", "admin");

        CountDownLatch latch = new CountDownLatch(1);
        firestore.collection("users").document(adminId)
                .set(admin)
                .addOnSuccessListener(v -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void addEvent_success() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.addEvent(buildValidEvent(), new AdminAddEventService.AddEventCallback() {
            @Override
            public void onSuccess() {
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

        // Verify event actually saved in Firestore
        CountDownLatch verifyLatch = new CountDownLatch(1);
        firestore.collection("events").document(eventId).get()
                .addOnSuccessListener(doc -> {
                    assertTrue(doc.exists());
                    assertEquals("Tech Conference", doc.getString("name"));
                    assertEquals("Montreal", doc.getString("location"));
                    verifyLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    fail("Failed to verify event: " + e.getMessage());
                    verifyLatch.countDown();
                });

        if (!verifyLatch.await(5, TimeUnit.SECONDS)) {
            fail("Verify callback not called in time");
        }
    }

    @Test
    public void addEvent_fail_admin_not_found() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Event event = buildValidEvent();
        event.adminId = "nonexistent_admin";

        service.addEvent(event, new AdminAddEventService.AddEventCallback() {
            @Override
            public void onSuccess() {
                fail("Expected failure but got success");
            }
            @Override
            public void onFailure(Exception e) {
                assertTrue(e instanceof SecurityException);
                assertEquals("User not found", e.getMessage());
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Callback not called in time");
        }
    }

    @Test
    public void addEvent_fail_user_not_admin() throws InterruptedException {
        // Insert a customer user
        String customerId = "123";
        User customer = new User(customerId, "customerUser", "pass123", "Customer Name", "customer@email.com", "1111111111", "customer");

        CountDownLatch setupLatch = new CountDownLatch(1);
        firestore.collection("users").document(customerId)
                .set(customer)
                .addOnSuccessListener(v -> setupLatch.countDown())
                .addOnFailureListener(e -> setupLatch.countDown());
        setupLatch.await(5, TimeUnit.SECONDS);

        CountDownLatch latch = new CountDownLatch(1);
        Event event = buildValidEvent();
        event.adminId = customerId;

        service.addEvent(event, new AdminAddEventService.AddEventCallback() {
            @Override
            public void onSuccess() {
                fail("Expected failure but got success");
            }
            @Override
            public void onFailure(Exception e) {
                assertTrue(e instanceof SecurityException);
                assertEquals("User is not an admin", e.getMessage());
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Callback not called in time");
        }
    }

    @Test
    public void addEvent_fail_invalid_event() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Event event = buildValidEvent();
        event.name = null;

        service.addEvent(event, new AdminAddEventService.AddEventCallback() {
            @Override
            public void onSuccess() {
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

    private Event buildValidEvent() {
        Event event = new Event();
        event.eventId     = eventId;
        event.name        = "Tech Conference";
        event.date        = "2024-06-01";
        event.startTime   = "09:00";
        event.endTime     = "17:00";
        event.location    = "Montreal";
        event.category    = "Technology";
        event.description = "A tech conference";
        event.adminId     = adminId;
        return event;
    }
}