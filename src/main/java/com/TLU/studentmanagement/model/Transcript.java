package main.java.com.TLU.studentmanagement.model;

import java.util.List;

public class Transcript {
  private String id;
  private String studentId;
  private String studentName;
  private String studentCode;
  private String semesterId;
  private String semesterName;
  private String course;
  private double midScore;
  private double finalScore;
  private double averageScore;
  private String status;
  private boolean deleted;
  private String createdAt;
  private String updatedAt;
  private List<Grade> grades;

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

  public String getSemesterId() {
    return semesterId;
  }

  public void setSemesterId(String semesterId) {
    this.semesterId = semesterId;
  }

  public String getSemesterName() {
    return semesterName;
  }

  public void setSemesterName(String semesterName) {
    this.semesterName = semesterName;
  }

  public String getCourse() {
    return course;
  }

  public void setCourse(String course) {
    this.course = course;
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

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public List<Grade> getGrades() {
    return grades;
  }

  public void setGrades(List<Grade> grades) {
    this.grades = grades;
  }

  public String getStudentCode() {
    return studentCode;
  }

  public void setStudentCode(String studentCode) {
    this.studentCode = studentCode;
  }

  public Transcript() {
  }

  public Transcript(String studentId, String semesterId) {
    this.studentId = studentId;
    this.semesterId = semesterId;
  }


  @Override
  public String toString() {
    return "Transcript{" +
        "id='" + id + '\'' +
        ", studentId='" + studentId + '\'' +
        ", studentName='" + studentName + '\'' +
        ", semesterId='" + semesterId + '\'' +
        ", semesterName='" + semesterName + '\'' +
        ", course='" + course + '\'' +
        ", midScore=" + midScore +
        ", finalScore=" + finalScore +
        ", averageScore=" + averageScore +
        ", status='" + status + '\'' +
        ", deleted=" + deleted +
        ", createdAt='" + createdAt + '\'' +
        ", updatedAt='" + updatedAt + '\'' +
        ", grades=" + grades +
        '}';
  }


}
