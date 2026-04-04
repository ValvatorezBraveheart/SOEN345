package com.example.soen345;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soen345.service.AdminEditEventService;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class AdminEditEventServiceTest {

    private AdminEditEventService service;
    private FirebaseFirestore firestore;

    private final String eventId = "test-event-edit";
    private final String adminId = "test-admin-edit";

    @BeforeClass
    public static void setupClass() throws InterruptedException {
        FirebaseFirestore firestore = FirestoreInitializer.getInstance();

        CountDownLatch latch = new CountDownLatch(1);

        firestore.collection("events").get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        firestore.collection("events").document(doc.getId()).delete();
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
    }

    @Before
    public void setUp() throws InterruptedException {
        firestore = FirebaseFirestore.getInstance();
        service = new AdminEditEventService(firestore);

        // Seed the original event that will be edited
        Event original = buildEvent("Original Name", "2026-05-01", "Montreal");

        CountDownLatch latch = new CountDownLatch(1);
        firestore.collection("events").document(eventId)
                .set(original)
                .addOnSuccessListener(v -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void editEvent_success() throws InterruptedException {
        Event updated = buildEvent("Updated Name", "2026-06-15", "Toronto");

        CountDownLatch latch = new CountDownLatch(1);

        service.editEvent(updated, new AdminEditEventService.EditEventCallback() {
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

        // Verify the Firestore document has the updated values
        CountDownLatch verifyLatch = new CountDownLatch(1);
        firestore.collection("events").document(eventId).get()
                .addOnSuccessListener(doc -> {
                    assertTrue(doc.exists());
                    assertEquals("Updated Name", doc.getString("name"));
                    assertEquals("2026-06-15", doc.getString("date"));
                    assertEquals("Toronto", doc.getString("location"));
                    verifyLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    fail("Failed to verify edit: " + e.getMessage());
                    verifyLatch.countDown();
                });

        if (!verifyLatch.await(5, TimeUnit.SECONDS)) {
            fail("Verify callback not called in time");
        }
    }

    @Test
    public void editEvent_fail_nullName() throws InterruptedException {
        Event invalid = buildEvent("Original Name", "2026-05-01", "Montreal");
        invalid.name = null;

        CountDownLatch latch = new CountDownLatch(1);

        service.editEvent(invalid, new AdminEditEventService.EditEventCallback() {
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

    @Test
    public void editEvent_fail_nullEvent() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.editEvent(null, new AdminEditEventService.EditEventCallback() {
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

    private Event buildEvent(String name, String date, String location) {
        Event event = new Event();
        event.eventId = eventId;
        event.name = name;
        event.date = date;
        event.startTime = "09:00";
        event.endTime = "17:00";
        event.location = location;
        event.category = "Technology";
        event.description = "An edited event";
        event.adminId = adminId;
        return event;
    }

    @After
    public void tearDown() throws InterruptedException {
        clearData();
    }

    private void clearData() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

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

        if (!latch.await(10, TimeUnit.SECONDS)) {
            Log.w("FirestoreClear", "Timeout while clearing Firestore");
        }
    }
}
