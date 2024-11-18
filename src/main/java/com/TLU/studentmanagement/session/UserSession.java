package main.java.com.TLU.studentmanagement.session;

import main.java.com.TLU.studentmanagement.model.User;

public class UserSession {
    private static User user;
    private static String accessToken;
    private static String refreshToken;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        UserSession.user = user;
    }

    public static void clear() {
        user = null;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String token) {
        UserSession.accessToken = token;
    }

    public static String getRefreshToken() {
        return refreshToken;
    }

    public static void setRefreshToken(String token) {
        UserSession.refreshToken = token;
    }
}

