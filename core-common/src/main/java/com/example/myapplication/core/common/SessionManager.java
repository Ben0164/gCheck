package com.example.myapplication.core.common;

import com.example.myapplication.core.data.entity.UserEntity;

public class SessionManager {
    private static UserEntity currentUser;

    public static void setCurrentUser(UserEntity user) {
        currentUser = user;
    }

    public static UserEntity getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }
}
