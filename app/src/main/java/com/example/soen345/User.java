package com.example.soen345;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {
    public String userId;
    public String username;
    public String password;
    public String fullName;
    public String email;
    public String phone;
    public String role; // "customer" or "admin"

    public User() {} // Required for Firebase

    public User(String userId, String username, String fullName, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public User(String userId, String username, String password, String fullName, String email, String phone, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    protected User(Parcel in) {
        userId = in.readString();
        username = in.readString();
        password = in.readString();
        fullName = in.readString();
        email = in.readString();
        phone = in.readString();
        role = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(fullName);
        parcel.writeString(email);
        parcel.writeString(phone);
        parcel.writeString(role);
    }
}