package main.java.com.TLU.studentmanagement.controller.teacher;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.com.TLU.studentmanagement.model.Teacher;
import main.java.com.TLU.studentmanagement.model.User;
import main.java.com.TLU.studentmanagement.util.HttpUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;

public class TeacherController {

  private static final String BASE_URL = "http://localhost:8080/api/teacher/";

  public static List<Teacher> getAllTeachers() {
    try {
      String response = HttpUtil.sendGet(BASE_URL + "getAll");

      // Process JSON response
      JSONObject jsonResponse = new JSONObject(response);
      JSONArray teachersArray = jsonResponse.getJSONArray("data");
      List<Teacher> teachers = new ArrayList<>();

      for (int i = 0; i < teachersArray.length(); i++) {
        JSONObject teacherObject = teachersArray.getJSONObject(i);
        Teacher teacher = new Teacher();
        teacher.setId(teacherObject.getString("_id"));
        teacher.setMgv(teacherObject.getString("mgv"));
        teacher.setFullName(teacherObject.optString("fullname", "n/a"));
        teacher.setEmail(teacherObject.optString("email", "n/a"));
        teacher.setAdmin(teacherObject.getBoolean("isAdmin"));
        teacher.setGV(teacherObject.optBoolean("isGV"));
        // Add other properties as needed
        teachers.add(teacher);
      }

      return teachers;
    } catch (Exception e) {
      // Handle specific exceptions here
      e.printStackTrace(); // Log the exception or handle it according to your application's needs
      return new ArrayList<>(); // Return an empty list or handle gracefully in case of error
    }
  }

  public Teacher getTeacherById(String id) {
    try {
      String response = HttpUtil.sendGet(BASE_URL + id);

      JSONObject jsonResponse = new JSONObject(response);
      JSONObject data = jsonResponse.getJSONObject("data");

      Teacher teacher = new Teacher();
      teacher.setId(data.getString("_id"));
      teacher.setMgv(data.getString("mgv"));
      teacher.setFullName(data.getString("fullname"));
      teacher.setAdmin(data.getBoolean("isAdmin"));
      teacher.setGV(data.optBoolean("isGV"));
      return teacher;

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void createTeacher(String mgv, String fullName) throws Exception {
    JSONObject jsonInput = new JSONObject();
    jsonInput.put("mgv", mgv);
    jsonInput.put("fullname", fullName);

    HttpUtil.sendPost(BASE_URL + "create-teacher", jsonInput.toString());
  }

  public static void updateTeacher(String id, String mgv, String fullName) throws Exception {
    JSONObject jsonInput = new JSONObject();
    jsonInput.put("mgv", mgv);
    jsonInput.put("fullname", fullName);

    HttpUtil.sendPut(BASE_URL + "update/" + id, jsonInput.toString());
  }

  public static void deleteTeacher(String id) throws Exception {
    HttpUtil.sendDelete(BASE_URL + "delete/" + id);
  }

  public static List<Teacher> searchTeachers(String keyword) {
    List<Teacher> teachers = new ArrayList<>();
    try {
      // Mã hóa từ khóa
      String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.toString());

      // Thêm từ khóa vào URL
      String apiUrl = BASE_URL + "search/?keyword=" + encodedKeyword;

      String response = HttpUtil.sendPost(apiUrl, null);  // Không cần requestData cho phương thức GET
      JSONObject jsonResponse = new JSONObject(response);
      JSONArray jsonArray = jsonResponse.getJSONArray("data");

      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObj = jsonArray.getJSONObject(i);
        Teacher teacher = new Teacher();
        teacher.setId(jsonObj.optString("_id"));
        teacher.setMgv(jsonObj.getString("mgv"));
        teacher.setFullName(jsonObj.optString("fullname"));
        teacher.setGV(jsonObj.optBoolean("isGV"));
        teacher.setAdmin(jsonObj.optBoolean("isAdmin"));

        teachers.add(teacher);
      }

    } catch (JSONException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Error parsing JSON data: " + e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Error searching students: " + e.getMessage());
    }

    return teachers;
  }
}
