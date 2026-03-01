package com.example.soen345;

public class User {
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
}