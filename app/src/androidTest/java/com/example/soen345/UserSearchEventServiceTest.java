package com.example.soen345;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soen345.service.UserSearchEventService;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class UserSearchEventServiceTest {
    private UserSearchEventService service;
    private FirebaseFirestore firestore;

    private final String eventId1 = UUID.randomUUID().toString();
    private final String eventId2 = UUID.randomUUID().toString();
    private final String eventId3 = UUID.randomUUID().toString();

    @BeforeClass
    public static void setupClass() {
        FirebaseFirestore firestore = FirestoreInitializer.getInstance();
    }

    @Before
    public void setUp() throws InterruptedException {
        firestore = FirebaseFirestore.getInstance();
        service = new UserSearchEventService(firestore);
        clearData();
        seedEvents();
    }

    private void seedEvents() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);

        // Event 1: Music, Montreal, 2026-03-21
        Event e1 = new Event(eventId1, "Concert 1", "2026-03-21", "18:00", "21:00", "Montreal", "Music", "A music event", "host1");
        // Event 2: Sports, Toronto, 2026-03-22
        Event e2 = new Event(eventId2, "Game 2", "2026-03-22", "14:00", "17:00", "Toronto", "Sports", "A sports event", "host2");
        // Event 3: Music, Toronto, 2026-03-21
        Event e3 = new Event(eventId3, "Concert 3", "2026-03-21", "20:00", "23:00", "Toronto", "Music", "Another music event", "host3");

        firestore.collection("events").document(eventId1).set(e1).addOnCompleteListener(t -> latch.countDown());
        firestore.collection("events").document(eventId2).set(e2).addOnCompleteListener(t -> latch.countDown());
        firestore.collection("events").document(eventId3).set(e3).addOnCompleteListener(t -> latch.countDown());

        if (!latch.await(10, TimeUnit.SECONDS)) {
            fail("Seeding events timed out");
        }
    }

    // All events

    @Test
    public void getEvents_noFilters_returnsAllEvents() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.getEvents(null, null, null,
                new UserSearchEventService.EventSearchCallback() {
                    @Override
                    public void onSuccess(List<Event> events) {
                        assertEquals(3, events.size());
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Should not fail: " + e.getMessage());
                    }
                }
        );

        if (!latch.await(5, TimeUnit.SECONDS)) fail("Callback not called in time");
    }

    // Single filter

    @Test
    public void getEvents_categoryFilter_returnsMatchingEvents() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.getEvents("Music", null, null,
                new UserSearchEventService.EventSearchCallback() {
                    @Override
                    public void onSuccess(List<Event> events) {

                        assertEquals(2, events.size());
                        assertTrue(events.stream().allMatch(e -> e.category.equals("Music")));
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Should not fail: " + e.getMessage());
                    }
                }
        );

        if (!latch.await(5, TimeUnit.SECONDS)) fail("Callback not called in time");
    }

    @Test
    public void getEvents_locationFilter_returnsMatchingEvents() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.getEvents(null, "Toronto", null,
                new UserSearchEventService.EventSearchCallback() {
                    @Override
                    public void onSuccess(List<Event> events) {

                        assertEquals(2, events.size());
                        assertTrue(events.stream().allMatch(e -> e.location.equals("Toronto")));
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Should not fail: " + e.getMessage());
                    }
                }
        );

        if (!latch.await(5, TimeUnit.SECONDS)) fail("Callback not called in time");
    }

    @Test
    public void getEvents_dateFilter_returnsMatchingEvents() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.getEvents(null, null, "2026-03-21",
                new UserSearchEventService.EventSearchCallback() {
                    @Override
                    public void onSuccess(List<Event> events) {
                        assertEquals(2, events.size());
                        assertTrue(events.stream().allMatch(e -> e.date.equals("2026-03-21")));
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Should not fail: " + e.getMessage());
                    }
                }
        );

        if (!latch.await(5, TimeUnit.SECONDS)) fail("Callback not called in time");
    }

    // Multi-filter

    @Test
    public void getEvents_categoryAndLocation_returnsMatchingEvents() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.getEvents("Music", "Toronto", null,
                new UserSearchEventService.EventSearchCallback() {
                    @Override
                    public void onSuccess(List<Event> events) {

                        assertEquals(1, events.size());
                        assertEquals(eventId3, events.get(0).eventId);
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Should not fail: " + e.getMessage());
                    }
                }
        );

        if (!latch.await(5, TimeUnit.SECONDS)) fail("Callback not called in time");
    }

    @Test
    public void getEvents_allFilters_returnsMatchingEvents() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.getEvents("Music", "Montreal", "2026-03-21",
                new UserSearchEventService.EventSearchCallback() {
                    @Override
                    public void onSuccess(List<Event> events) {

                        assertEquals(1, events.size());
                        assertEquals(eventId1, events.get(0).eventId);
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Should not fail: " + e.getMessage());
                    }
                }
        );

        if (!latch.await(5, TimeUnit.SECONDS)) fail("Callback not called in time");
    }

    // No result

    @Test
    public void getEvents_noMatch_returnsEmptyList() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        service.getEvents("DoesNotExist", "NOwhere", "1888-11-11",
                new UserSearchEventService.EventSearchCallback() {
                    @Override
                    public void onSuccess(List<Event> events) {

                        assertTrue(events.isEmpty());
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Should not fail: " + e.getMessage());
                    }
                }
        );

        if (!latch.await(5, TimeUnit.SECONDS)) fail("Callback not called in time");
    }
    @After
    public void tearDown() throws InterruptedException {
        clearData();
    }

    private void clearData() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        firestore.collection("events").get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        firestore.collection("events").document(doc.getId()).delete();
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