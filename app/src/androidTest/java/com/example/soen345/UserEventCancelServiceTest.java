package com.example.soen345;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soen345.service.UserEventCancelService;
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
public class UserEventCancelServiceTest {

    private UserEventCancelService service;
    private FirebaseFirestore firestore;

    private final String reservationId = "test-reservation-cancel";
    private final String userId = "test-user-cancel";
    private final String eventId = "test-event-cancel";

    @BeforeClass
    public static void setupClass() throws InterruptedException {
        FirebaseFirestore firestore = FirestoreInitializer.getInstance();

        CountDownLatch latch = new CountDownLatch(1);

        firestore.collection("reservations").get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        firestore.collection("reservations").document(doc.getId()).delete();
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
    }

    @Before
    public void setUp() throws InterruptedException {
        firestore = FirebaseFirestore.getInstance();
        service = new UserEventCancelService(firestore);

        // Seed a reservation to cancel in the success test
        Reservation reservation = new Reservation(reservationId, userId, eventId);

        CountDownLatch latch = new CountDownLatch(1);
        firestore.collection("reservations").document(reservationId)
                .set(reservation)
                .addOnSuccessListener(v -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void cancelReservation_success() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.cancelReservation(reservationId, new UserEventCancelService.CancelReservationCallback() {
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

        // Verify the document is gone from Firestore
        CountDownLatch verifyLatch = new CountDownLatch(1);
        firestore.collection("reservations").document(reservationId).get()
                .addOnSuccessListener(doc -> {
                    assertFalse("Reservation should have been deleted", doc.exists());
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
    public void cancelReservation_fail_nullId() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.cancelReservation(null, new UserEventCancelService.CancelReservationCallback() {
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
    public void cancelReservation_fail_emptyId() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.cancelReservation("", new UserEventCancelService.CancelReservationCallback() {
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
