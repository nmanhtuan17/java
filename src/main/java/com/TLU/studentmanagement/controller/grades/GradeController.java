package main.java.com.TLU.studentmanagement.controller.grades;

import main.java.com.TLU.studentmanagement.controller.courses.CourseController;
import main.java.com.TLU.studentmanagement.model.Grade;
import main.java.com.TLU.studentmanagement.model.Course;
import main.java.com.TLU.studentmanagement.util.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class GradeController {

  private static final String BASE_URL = "http://localhost:8080/api/grade";
  private CourseController courseController = new CourseController(); // Khởi tạo CourseController

  public List<Grade> getAllGrades() {
    List<Grade> grades = new ArrayList<>();
    try {
      String response = HttpUtil.sendGet(BASE_URL + "/getAll");
      JSONArray jsonArray = new JSONArray(response);
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonGrade = jsonArray.getJSONObject(i);
        Grade grade = new Grade(
            jsonGrade.getString("_id"),
            jsonGrade.getString("courseId"),
            jsonGrade.getString("transcriptId"),
            jsonGrade.getDouble("midScore"),
            jsonGrade.getDouble("finalScore")
        );
        // Lấy thông tin khóa học và gán vào đối tượng Grade
        Course course = courseController.getCourseById(grade.getCourseId());
        grade.setCourse(course);
        grades.add(grade);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return grades;
  }

  public Grade getGradeById(String id) {
    try {
      String response = HttpUtil.sendGet(BASE_URL + "/" + id);
      JSONObject jsonGrade = new JSONObject(response);
      Grade grade = new Grade(
          jsonGrade.getString("_id"),
          jsonGrade.getString("courseId"),
          jsonGrade.getString("transcriptId"),
          jsonGrade.getDouble("midScore"),
          jsonGrade.getDouble("finalScore")
      );
      // Lấy thông tin khóa học và gán vào đối tượng Grade
      Course course = courseController.getCourseById(grade.getCourseId());
      grade.setCourse(course);
      return grade;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public String createGrade(Grade grade) {
    try {
      JSONObject jsonGrade = new JSONObject();
      jsonGrade.put("courseId", grade.getCourseId());
      jsonGrade.put("transcriptId", grade.getTranscriptId());
      jsonGrade.put("midScore", grade.getMidScore());
      jsonGrade.put("finalScore", grade.getFinalScore());

      // Gửi yêu cầu POST và nhận phản hồi từ server
      String response = HttpUtil.sendPost(BASE_URL + "/create", jsonGrade.toString());

      System.out.println(response);

      // Chuyển đổi phản hồi thành JSONObject
      JSONObject jsonResponse = new JSONObject(response);

      // In phản hồi ra log
      System.out.println(jsonResponse.toString()); // In ra JSON với indent 2

      // Kiểm tra trạng thái phản hồi
      if (jsonResponse.has("status") && jsonResponse.getString("status").equals("error")) {
        // Xử lý lỗi từ server
        String errorMessage = jsonResponse.getString("message");
        return errorMessage;
      } else {
        // Xử lý thành công
        return "Grade created successfully";
      }
    } catch (Exception e) {
      // Xử lý ngoại lệ và hiển thị lỗi nếu cần thiết
      e.printStackTrace();
      return e.getMessage();
    }
  }


  public int updateGrade(String gradeId, Grade grade) {
    try {
      // Tạo đối tượng JSON để gửi đi
      JSONObject jsonGrade = new JSONObject();
      jsonGrade.put("midScore", grade.getMidScore());
      jsonGrade.put("finalScore", grade.getFinalScore());

      // Gửi yêu cầu PUT và nhận phản hồi
      String response = HttpUtil.sendPut(BASE_URL + "/update/" + gradeId, jsonGrade.toString());

      // Xử lý nội dung phản hồi JSON
      JSONObject jsonResponse = new JSONObject(response);
      String message = jsonResponse.optString("message");

      if ("Update successfully".equals(message)) {
        return HttpURLConnection.HTTP_OK; // Hoặc mã trạng thái thành công khác
      } else {
        // Xử lý các mã trạng thái khác nếu cần
        return HttpURLConnection.HTTP_INTERNAL_ERROR; // Ví dụ mã trạng thái lỗi
      }
    } catch (Exception e) {
      e.printStackTrace();
      return HttpURLConnection.HTTP_BAD_REQUEST; // Trả về mã trạng thái lỗi
    }
  }


  public void deleteGrade(String gradeId) {
    try {
      HttpUtil.sendDelete(BASE_URL + "/delete/" + gradeId);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
