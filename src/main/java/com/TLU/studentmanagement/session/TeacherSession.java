package main.java.com.TLU.studentmanagement.session;

import main.java.com.TLU.studentmanagement.model.Teacher;

public class TeacherSession {
    private static Teacher teacher;
    private static String accessToken;

    public static Teacher getTeacher() {
        return teacher;
    }

    public static void setTeacher(Teacher teacher) {
        TeacherSession.teacher = teacher;
    }

    public static void clear() {
        teacher = null;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String token) {
        TeacherSession.accessToken = token;
    }
}
