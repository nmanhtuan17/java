package main.java.com.TLU.studentmanagement.session;

import main.java.com.TLU.studentmanagement.model.Major;

public class MajorSession {
    private static Major major;

    public static Major getMajor() {
        return major;
    }

    public static void setMajor(Major major) {
        MajorSession.major = major;
    }
}
