package main.java.com.TLU.studentmanagement.model;

public class Semester {
  private String id;
  private String semester;
  private String group;
  private String year;

  public Semester() {
  }

  public Semester(String semester, String group, String year) {
    this.semester = semester;
    this.group = group;
    this.year = year;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSemester() {
    return semester;
  }

  public void setSemester(String semester) {
    this.semester = semester;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  @Override
  public String toString() {
    return "Semester{" +
        "id='" + id + '\'' +
        ", semester='" + semester + '\'' +
        ", group='" + group + '\'' +
        ", year='" + year + '\'' +
        '}';
  }
}
