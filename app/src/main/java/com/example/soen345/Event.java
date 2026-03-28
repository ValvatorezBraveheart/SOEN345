package com.example.soen345;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Event implements Parcelable {
    @DocumentId
    public String eventId;
    public String category;    // Matches "Concerts"
    public String date;        // Matches "22 August, 2026"
    public String description; // Matches "something somthing"
    public String endTime;     // Matches "10:00 PM"
    public String location;    // Matches "Eaton Center, Montreal"
    public String name;        // Matches "summerrrrr musiccz"
    public String startTime;   // Matches "8:00 PM"
    public String adminId;     // Matches "some_test_id"

    // Required for Firebase to map document data to this class
    public Event() {}

    // Full constructor for manual object creation
    public Event(String eventId, String name, String date, String startTime, String endTime, String location, String category, String description, String adminId) {
        this.eventId = eventId;
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.category = category;
        this.description = description;
        this.adminId = adminId;
    }


    protected Event(Parcel in) {
        eventId = in.readString();
        name = in.readString();
        date = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        location = in.readString();
        category = in.readString();
        description = in.readString();
        adminId = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeString(eventId);
        parcel.writeString(name);
        parcel.writeString(date);
        parcel.writeString(startTime);
        parcel.writeString(endTime);
        parcel.writeString(location);
        parcel.writeString(category);
        parcel.writeString(description);
        parcel.writeString(adminId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}