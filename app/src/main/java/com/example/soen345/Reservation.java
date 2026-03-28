package com.example.soen345;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Reservation implements Parcelable {
    public String reservationId;
    public String userId;
    public String eventId;

    public Reservation() {} // required for Firestore

    public Reservation(String reservationId, String userId, String eventId) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.eventId = eventId;
    }

    protected Reservation(Parcel in) {
        reservationId = in.readString();
        userId = in.readString();
        eventId = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeString(reservationId);
        parcel.writeString(userId);
        parcel.writeString(eventId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Reservation> CREATOR = new Creator<>() {
        @Override
        public Reservation createFromParcel(Parcel in) {
            return new Reservation(in);
        }

        @Override
        public Reservation[] newArray(int size) {
            return new Reservation[size];
        }
    };
}