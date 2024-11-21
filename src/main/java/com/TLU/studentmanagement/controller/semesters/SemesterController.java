package main.java.com.TLU.studentmanagement.controller.semesters;

import main.java.com.TLU.studentmanagement.model.Semester;
import main.java.com.TLU.studentmanagement.util.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SemesterController {
  private static final String BASE_URL = "http://localhost:8080/api/semester";

  public static List<Semester> getAllSemesters() throws Exception {
    String apiUrl = BASE_URL + "/getAll";
    String response = HttpUtil.sendGet(apiUrl);
    JSONObject jsonResponse = new JSONObject(response);
    JSONArray semestersArray = jsonResponse.getJSONArray("data");

    List<Semester> semesters = new ArrayList<>();
    for (int i = 0; i < semestersArray.length(); i++) {
      JSONObject semesterObject = semestersArray.getJSONObject(i);
      Semester semester = new Semester();
      semester.setId(semesterObject.getString("_id"));
      semester.setSemester(semesterObject.getString("semester"));
      semester.setGroup(semesterObject.getString("group"));
      semester.setYear(semesterObject.getString("year"));
      semesters.add(semester);
    }
    return semesters;
  }

  public JSONObject createSemester(String semester, String group, String year) {
    try {

      JSONObject jsonInput = new JSONObject();
      jsonInput.put("semester", semester);
      jsonInput.put("group", group);
      jsonInput.put("year", year);

      String apiUrl = BASE_URL + "/create";
      String response = HttpUtil.sendPost(apiUrl, jsonInput.toString());
      System.out.println(response);
      // Chuyển đổi phản hồi thành JSONObject
      JSONObject jsonResponse = new JSONObject(response);

      return jsonResponse;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

  }

  public static void updateSemester(String id, String semester, String group, String year) throws Exception {
    JSONObject jsonInput = new JSONObject();
    jsonInput.put("semester", semester);
    jsonInput.put("group", group);
    jsonInput.put("year", year);

    String apiUrl = BASE_URL + "/update/" + id;
    HttpUtil.sendPut(apiUrl, jsonInput.toString());
  }

  public static void deleteSemester(String id) throws Exception {
    String apiUrl = BASE_URL + "/delete/" + id;
    HttpUtil.sendDelete(apiUrl);
  }
}
