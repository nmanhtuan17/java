package main.java.com.TLU.studentmanagement.model;

public class Grade {
    private String id;
    private String studentId;
    private String studentName;
    private String transcriptId;
    private String courseId;
    private double midScore;
    private double finalScore;
    private double averageScore;
    private double gpa;
    private String status;
    private Course course; // Thông tin khóa học liên quan
    private String courseName;
    private String courseCode;

    // Constructor mặc định
    public Grade() {}

    public Grade(String id, String courseId, String transcriptId, double midScore, double finalScore) {
        this.id = id;
        this.courseId = courseId;
        this.transcriptId = transcriptId;
        this.midScore = midScore;
        this.finalScore = finalScore;
    }

    // Getter và setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public double getMidScore() {
        return midScore;
    }

    public void setMidScore(double midScore) {
        this.midScore = midScore;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id='" + id + '\'' +
                ", studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", transcriptId='" + transcriptId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", midScore=" + midScore +
                ", finalScore=" + finalScore +
                ", averageScore=" + averageScore +
                ", gpa=" + gpa +
                ", status='" + status + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseCode='" + courseCode + '\'' +
                '}';
    }

}
