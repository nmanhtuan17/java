package main.java.com.TLU.studentmanagement.controller.courses;

import main.java.com.TLU.studentmanagement.model.Course;
import main.java.com.TLU.studentmanagement.util.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CourseController {

  public static List<Course> getAllCourses() throws Exception {
    String apiUrl = "http://localhost:8080/api/course/getAll";
    String response = HttpUtil.sendGet(apiUrl);
    JSONObject jsonResponse = new JSONObject(response);
    JSONArray coursesArray = jsonResponse.getJSONArray("data");

    List<Course> courses = new ArrayList<>();
    for (int i = 0; i < coursesArray.length(); i++) {
      JSONObject courseObject = coursesArray.getJSONObject(i);
      Course course = new Course();
      course.setId(courseObject.getString("_id"));
      course.setName(courseObject.getString("name"));
      course.setCode(courseObject.getString("code"));
      course.setCredit(courseObject.getInt("credit"));

      // Kiểm tra và gán majorId nếu tồn tại và là đối tượng JSON
      if (courseObject.has("majorId") && !courseObject.isNull("majorId")) {
        Object majorIdObj = courseObject.get("majorId");
        if (majorIdObj instanceof JSONObject) {
          JSONObject majorIdJson = (JSONObject) majorIdObj;
          if (majorIdJson.has("_id")) {
            course.setMajorId(majorIdJson.getString("_id"));
          } else {
            course.setMajorId(""); // Gán giá trị mặc định là rỗng nếu không tìm thấy _id trong đối tượng JSON
          }
        } else if (majorIdObj instanceof String) {
          course.setMajorId((String) majorIdObj);
        } else {
          course.setMajorId(""); // Gán giá trị mặc định là rỗng nếu majorId không phải là chuỗi hoặc đối tượng JSON
        }
      } else {
        course.setMajorId(""); // Gán giá trị mặc định là rỗng nếu majorId không tồn tại
      }

      courses.add(course);
    }
    return courses;
  }

  public static Course getCourseById(String id) throws Exception {
    String apiUrl = "http://localhost:8080/api/course/" + id;
    String response = HttpUtil.sendGet(apiUrl);
    JSONObject jsonResponse = new JSONObject(response);
    JSONObject courseObject = jsonResponse.getJSONObject("data");

    Course course = new Course();
    course.setId(courseObject.getString("_id"));
    course.setName(courseObject.getString("name"));
    course.setCode(courseObject.getString("code"));
    course.setCredit(courseObject.getInt("credit"));

    // Kiểm tra và gán majorId nếu tồn tại và là đối tượng JSON
    if (courseObject.has("majorId") && !courseObject.isNull("majorId")) {
      Object majorIdObj = courseObject.get("majorId");
      if (majorIdObj instanceof JSONObject) {
        JSONObject majorIdJson = (JSONObject) majorIdObj;
        if (majorIdJson.has("_id")) {
          course.setMajorId(majorIdJson.getString("_id"));
        } else {
          course.setMajorId(""); // Gán giá trị mặc định là rỗng nếu không tìm thấy _id trong đối tượng JSON
        }
      } else if (majorIdObj instanceof String) {
        course.setMajorId((String) majorIdObj);
      } else {
        course.setMajorId(""); // Gán giá trị mặc định là rỗng nếu majorId không phải là chuỗi hoặc đối tượng JSON
      }
    } else {
      course.setMajorId(""); // Gán giá trị mặc định là rỗng nếu majorId không tồn tại
    }


    return course;
  }

  public JSONObject createCourse(String name, String code, int credit, String majorId) throws Exception {
    JSONObject jsonInput = new JSONObject();
    jsonInput.put("name", name);
    jsonInput.put("code", code);
    jsonInput.put("credit", credit);
    jsonInput.put("majorId", majorId != null ? majorId : ""); // Ensure majorId is never null

    String apiUrl = "http://localhost:8080/api/course/add-course";
    String res = HttpUtil.sendPost(apiUrl, jsonInput.toString());
    JSONObject resJson = new JSONObject(res);

    return resJson;
  }

  public static void updateCourse(String id, String name, String code, int credit, String majorId) throws Exception {
    JSONObject jsonInput = new JSONObject();
    jsonInput.put("name", name);
    jsonInput.put("code", code);
    jsonInput.put("credit", credit);
    jsonInput.put("majorId", majorId != null ? majorId : ""); // Ensure majorId is never null

    String apiUrl = "http://localhost:8080/api/course/update/" + id;
    HttpUtil.sendPut(apiUrl, jsonInput.toString());
  }

  public static void deleteCourse(String id) throws Exception {
    String apiUrl = "http://localhost:8080/api/course/delete/" + id;
    HttpUtil.sendDelete(apiUrl);
  }
}
