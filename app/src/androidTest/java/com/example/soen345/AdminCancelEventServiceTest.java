package com.example.soen345;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soen345.service.AdminCancelEventService;
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
public class AdminCancelEventServiceTest {

    private AdminCancelEventService service;
    private FirebaseFirestore firestore;

    private final String eventId = "test-event-admin-cancel";
    private final String adminId = "test-admin-cancel";

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
        service = new AdminCancelEventService(firestore);

        // Seed an event to cancel in the success test
        Event event = new Event(eventId, "Cancel Me", "2026-05-10", "10:00", "12:00", "Montreal", "Music",
                "Event to delete", adminId);

        CountDownLatch latch = new CountDownLatch(1);
        firestore.collection("events").document(eventId)
                .set(event)
                .addOnSuccessListener(v -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void cancelEvent_success() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.cancelEvent(eventId, new AdminCancelEventService.CancelEventCallback() {
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

        // Verify the event document is gone from Firestore
        CountDownLatch verifyLatch = new CountDownLatch(1);
        firestore.collection("events").document(eventId).get()
                .addOnSuccessListener(doc -> {
                    assertFalse("Event should have been deleted", doc.exists());
                    verifyLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    fail("Failed to verify deletion: " + e.getMessage());
                    verifyLatch.countDown();
                });

        if (!verifyLatch.await(5, TimeUnit.SECONDS)) {
            fail("Verify callback not called in time");
        }
    }

    @Test
    public void cancelEvent_fail_nullId() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.cancelEvent(null, new AdminCancelEventService.CancelEventCallback() {
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
    public void cancelEvent_fail_emptyId() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.cancelEvent("", new AdminCancelEventService.CancelEventCallback() {
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
