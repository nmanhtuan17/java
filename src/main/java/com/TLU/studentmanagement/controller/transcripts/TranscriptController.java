package main.java.com.TLU.studentmanagement.controller.transcripts;

import com.google.gson.Gson;
import main.java.com.TLU.studentmanagement.model.Grade;
import main.java.com.TLU.studentmanagement.model.Transcript;
import main.java.com.TLU.studentmanagement.util.HttpUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import raven.toast.Notifications;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class TranscriptController {

    private static final String BASE_URL = "http://localhost:8080/api/transcript";
    private String transcriptId;

    public List<Transcript> getAllTranscripts() {
        List<Transcript> transcripts = new ArrayList<>();
        try {
            String response = HttpUtil.sendGet(BASE_URL + "/getAll");
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray jsonArray = jsonResponse.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTranscript = jsonArray.getJSONObject(i);
                Transcript transcript = new Transcript();
                transcript.setId(jsonTranscript.getString("_id"));
                transcript.setDeleted(jsonTranscript.getBoolean("deleted"));

                // Parsing studentId and studentName
                if (jsonTranscript.has("student") && !jsonTranscript.isNull("student")) {
                    JSONObject studentJson = jsonTranscript.getJSONObject("student");
                    if (studentJson.has("_id")) {
                        transcript.setStudentId(studentJson.getString("_id"));
                    }
                    if (studentJson.has("fullname")) {
                        transcript.setStudentName(studentJson.getString("fullname"));
                    }
                    if (studentJson.has("msv")) {
                        transcript.setStudentCode(studentJson.getString("msv"));
                    }
                }

                // Parsing semesterId and semesterName
                if (jsonTranscript.has("semester") && !jsonTranscript.isNull("semester")) {
                    JSONObject semesterJson = jsonTranscript.getJSONObject("semester");
                    if (semesterJson.has("_id")) {
                        transcript.setSemesterId(semesterJson.getString("_id"));
                    }
                    if (semesterJson.has("semester") && semesterJson.has("group") && semesterJson.has("year")) {
                        String semesterName = semesterJson.getString("semester") + " - " + semesterJson.getString("group") + " - Năm học: " + semesterJson.getString("year");
                        transcript.setSemesterName(semesterName);
                    }
                }
                transcripts.add(transcript);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(transcripts);
        return transcripts;
    }

    public Transcript getTranscriptById(String transcriptId) {
        try {
            String response = HttpUtil.sendGet("http://localhost:8080/api/transcript/" + transcriptId);
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject jsonTranscript = jsonResponse.getJSONObject("data");

            // Parse transcript details
            Transcript transcript = new Transcript();
            transcript.setId(jsonTranscript.getString("_id"));
            transcript.setDeleted(jsonTranscript.getBoolean("deleted"));
            transcript.setCreatedAt(jsonTranscript.getString("createdAt"));
            transcript.setUpdatedAt(jsonTranscript.getString("updatedAt"));

            // Parse student details
            JSONObject studentJson = jsonTranscript.getJSONObject("student");
            transcript.setStudentId(studentJson.getString("_id"));
            transcript.setStudentName(studentJson.getString("fullname"));
            transcript.setStudentCode(studentJson.getString("msv"));

            // Parse semester details
            JSONObject semesterJson = jsonTranscript.getJSONObject("semester");
            String semesterName = semesterJson.getString("semester") + " - " + semesterJson.getString("group") + " - Năm học: " + semesterJson.getString("year");
            transcript.setSemesterId(semesterJson.getString("_id"));
            transcript.setSemesterName(semesterName);

            // Parse grades
            JSONArray gradesArray = jsonTranscript.getJSONArray("grades");
            List<Grade> grades = new ArrayList<>();
            for (int i = 0; i < gradesArray.length(); i++) {
                JSONObject gradeJson = gradesArray.getJSONObject(i);
                Grade grade = new Grade();
                grade.setId(gradeJson.getString("_id"));
                grade.setCourseId(gradeJson.getString("course"));
                grade.setMidScore(gradeJson.getDouble("midScore"));
                grade.setFinalScore(gradeJson.getDouble("finalScore"));
                grade.setTranscriptId(gradeJson.getString("transcript"));
                grade.setAverageScore(gradeJson.getDouble("averageScore"));
                grade.setStatus(gradeJson.getString("status"));
                grades.add(grade);
            }
            transcript.setGrades(grades);

            return transcript;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Transcript> getTranscriptByStudentId(String studentId) {
        List<Transcript> transcripts = new ArrayList<>();
        try {
            String response = HttpUtil.sendGet("http://localhost:8080/api/transcript/student/" + studentId);
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray jsonArray = jsonResponse.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTranscript = jsonArray.getJSONObject(i);

                Transcript transcript = new Transcript();
                transcript.setCourse(jsonTranscript.getString("course"));
                transcript.setMidScore(jsonTranscript.getDouble("midScore"));
                transcript.setFinalScore(jsonTranscript.getDouble("finalScore"));
                transcript.setAverageScore(jsonTranscript.getDouble("averageScore"));
                transcript.setStatus(jsonTranscript.getString("status"));

                transcripts.add(transcript);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transcripts;
    }

    public Transcript getTranscriptBySemesterStudent(String studentId, String semesterId) {
        Transcript transcript = new Transcript();
        transcript.setStudentId(studentId);
        transcript.setSemesterId(semesterId);

        try {
            // Send GET request to the API
            String response = HttpUtil.sendGet("http://localhost:8080/api/transcript/student/" + studentId + "/semester/" + semesterId);

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray transcriptsArray = jsonResponse.getJSONArray("data");

            // Check if there is at least one transcript
            if (transcriptsArray.length() > 0) {
                JSONObject jsonTranscript = transcriptsArray.getJSONObject(0); // Assuming only one transcript per student and semester

                // Set the ID for the transcript
                String transcriptId = jsonTranscript.getString("_id");
                transcript.setId(transcriptId);

                // List to store grades
                List<Grade> grades = new ArrayList<>();

                // Loop through the grades array in the JSON object
                JSONArray gradesArray = jsonTranscript.getJSONArray("grades");
                for (int i = 0; i < gradesArray.length(); i++) {
                    JSONObject jsonGrade = gradesArray.getJSONObject(i);

                    Grade grade = new Grade();
                    grade.setId(jsonGrade.getString("gradeId"));
                    grade.setCourseName(jsonGrade.getString("courseName"));
                    grade.setMidScore(jsonGrade.getDouble("midScore"));
                    grade.setFinalScore(jsonGrade.getDouble("finalScore"));
                    grade.setAverageScore(jsonGrade.getDouble("averageScore"));
                    grade.setStatus(jsonGrade.getString("status"));

                    grades.add(grade);
                }

                // Assign the grades list to the Transcript object
                transcript.setGrades(grades);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return transcript;
    }


    public int createTranscript(Transcript transcript) {
        try {
            JSONObject jsonTranscript = new JSONObject();
            jsonTranscript.put("studentId", transcript.getStudentId());
            jsonTranscript.put("semesterId", transcript.getSemesterId());

            // Gửi yêu cầu POST
            String response = HttpUtil.sendPost("http://localhost:8080/api/transcript/create", jsonTranscript.toString());

            // In phản hồi để kiểm tra
            System.out.println("Response: " + response);

            // Kiểm tra phản hồi từ server
            if (response == null || response.isEmpty()) {
                Notifications.getInstance().show(Notifications.Type.ERROR, "Không nhận được phản hồi từ server.");
                return 0; // Lỗi do không có phản hồi
            }

            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("message")) {
                String message = jsonResponse.getString("message");
                if (message.equals("Transcript was deleted. Do you want to restore it?")) {
                    // Lấy transcriptId từ phản hồi
                    this.transcriptId = jsonResponse.getString("transcriptId");
                    // Trả về giá trị đặc biệt để cho biết cần khôi phục
                    return -2; // Trường hợp bảng điểm đã bị xóa và cần khôi phục
                } else if (message.equals("Transcript already exists")) {
                    return -1; // Trùng lặp
                } else {
                    Notifications.getInstance().show(Notifications.Type.ERROR, "Lỗi từ server: " + message);
                    return 0; // Lỗi khác
                }
            }

            return 1; // Thành công
        } catch (Exception e) {
            e.printStackTrace();
            Notifications.getInstance().show(Notifications.Type.ERROR, "Lỗi không xác định: " + e.getMessage());
            return 0; // Lỗi khác
        }
    }

    public String getTranscriptId() {
        return transcriptId;
    }



    public int restoreTranscript(String transcriptId) {
        try {
            JSONObject jsonTranscript = new JSONObject();
            jsonTranscript.put("transcriptId", transcriptId);

            String response = HttpUtil.sendPut("http://localhost:8080/api/transcript/restore", jsonTranscript.toString());

            if (response == null || response.isEmpty()) {
                Notifications.getInstance().show(Notifications.Type.ERROR, "Không nhận được phản hồi từ server.");
                return 0; // Lỗi do không có phản hồi
            }

            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("message") && jsonResponse.getString("message").equals("Transcript restored")) {
                return 1; // Thành công
            }

            return 0; // Lỗi khác
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Lỗi khác
        }
    }


    public int updateTranscript(String transcriptId, Transcript transcript) {
        try {
            // Tạo JSON object từ đối tượng Transcript
            JSONObject jsonTranscript = new JSONObject();
            jsonTranscript.put("studentId", transcript.getStudentId());
            jsonTranscript.put("semesterId", transcript.getSemesterId());

            System.out.println("transcriptId " + transcriptId);

            // Gửi yêu cầu PUT đến server để cập nhật bảng điểm
            String response = HttpUtil.sendPut(BASE_URL + "/update/" + transcriptId, jsonTranscript.toString());

            System.out.println("Rsp: " + response);

            // Xử lý phản hồi từ server
            if (response != null) {
                return 1; // Cập nhật thành công
            }
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse == null || jsonResponse.getInt("code") == 404 || jsonResponse.getString("status") == "error") {
                return -1; // Bảng điểm đã tồn tại
            } else {
                return 0; // Cập nhật thất bại
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Thất bại trong quá trình cập nhật
        }
    }


    public void deleteTranscript(String transcriptId) {
        try {
            HttpUtil.sendDelete(BASE_URL + "/delete/" + transcriptId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<Transcript> searchTranscripts(String keyword) {
        List<Transcript> transcripts = new ArrayList<>();

        try {
            String url = BASE_URL + "/search?keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8.toString());
            String response = HttpUtil.sendPost(url, null);
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray jsonArray = jsonResponse.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTranscript = jsonArray.getJSONObject(i);
                Transcript transcript = new Transcript();
                transcript.setId(jsonTranscript.getString("_id"));
                transcript.setDeleted(jsonTranscript.getBoolean("deleted"));

                // Lấy thông tin sinh viên
                JSONObject studentObj = jsonTranscript.getJSONObject("student");
                transcript.setStudentId(studentObj.getString("_id"));
                transcript.setStudentName(studentObj.optString("fullname", "Chưa có tên"));
                transcript.setStudentCode(studentObj.optString("msv", "Chưa có tên"));

                // Lấy thông tin học kỳ
                JSONObject semesterObj = jsonTranscript.getJSONObject("semester");
                transcript.setSemesterId(semesterObj.getString("_id"));
                transcript.setSemesterName(semesterObj.optString("semester", "Chưa có tên học kỳ") +
                        " - " + semesterObj.optString("group", "Chưa có nhóm") +
                        " - Năm học: " + semesterObj.optString("year", "Chưa có năm học"));

                transcripts.add(transcript);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transcripts;
    }


}
