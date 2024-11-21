package main.java.com.TLU.studentmanagement.model;

public class Major {
  private String name;
  private String code;
  private String id;

  public Major(String name, String code) {
    this.name = name;
    this.code = code;
  }

  public Major() {
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public String getCode() {
    return code;
  }

  @Override
  public String toString() {
    return "Major{" +
        "name='" + name + '\'' +
        ", code='" + code + '\'' +
        '}';
  }
}
