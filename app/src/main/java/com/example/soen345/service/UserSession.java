package com.example.soen345.service;

import com.example.soen345.User;

// Keep track of user across differen
public class UserSession {
    private static UserSession instance;
    private User currentUser;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }

    public void setUser(User user) { this.currentUser = user; }
    public User getUser() { return currentUser; }
    public void clear() { currentUser = null; instance = null; }
}
