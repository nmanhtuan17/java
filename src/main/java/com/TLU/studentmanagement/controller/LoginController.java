package main.java.com.TLU.studentmanagement.controller;

import main.java.com.TLU.studentmanagement.model.Teacher;
import main.java.com.TLU.studentmanagement.model.User;
import main.java.com.TLU.studentmanagement.session.TeacherSession;
import main.java.com.TLU.studentmanagement.session.UserSession;
import main.java.com.TLU.studentmanagement.util.HttpUtil;
import main.java.com.TLU.studentmanagement.view.Dashboard.HomeView;
import main.java.com.TLU.studentmanagement.view.login.Login;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

import raven.toast.Notifications;


public class LoginController {
  private Login loginView;

  public LoginController(Login loginView) {
    this.loginView = loginView;
    this.loginView.addLoginListener(new LoginListener());
  }

  class LoginListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      String username = loginView.getUsername();
      String password = loginView.getPassword();

      try {
        String apiUrl = "http://localhost:8080/api/auth/login";
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("msv", username);
        jsonInput.put("password", password);

        String response = HttpUtil.sendPost(apiUrl, jsonInput.toString());
        JSONObject jsonResponse = new JSONObject(response);

        if (jsonResponse.has("tokens")) {
          String accessToken = jsonResponse.getJSONObject("tokens").getString("accessToken");
          String refreshToken = jsonResponse.getJSONObject("tokens").getString("refreshToken");
          HttpUtil.setTokens(accessToken, refreshToken);
          UserSession.setAccessToken(accessToken);
          TeacherSession.setAccessToken(accessToken);

          String userId = jsonResponse.getJSONObject("data").getJSONObject("user").getString("_id");
          boolean isGV = jsonResponse.getJSONObject("data").getJSONObject("user").getBoolean("isGV");
          JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());

          if (isGV) {
            Teacher teacher = fetchTeacherDetails(userId);

            Notifications.getInstance().show(Notifications.Type.SUCCESS, "Login successful! Welcome " + teacher.getFullName());

            System.out.println("Teacher Info: " + teacher.toString());

            // Lưu thông tin giáo viên vào TeacherSession
            TeacherSession.setTeacher(teacher);

          } else {
            User user = fetchUserDetails(userId);
            Notifications.getInstance().show(Notifications.Type.SUCCESS, "Login successful! Welcome " + user.getFullName());

            // Lưu thông tin người dùng vào UserSession
            UserSession.setUser(user);
          }

          // Thay đổi nội dung của cửa sổ hiện tại sang HomeView
          HomeView homeView = new HomeView();
          currentFrame.setContentPane(homeView);
          currentFrame.revalidate();
          currentFrame.repaint();

        } else {
          Notifications.getInstance().show(Notifications.Type.ERROR, "Login failed: Missing token in response.");
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Đăng nhập thất bại!");
      }
    }


    private User fetchUserDetails(String userId) throws Exception {
      String apiUrl = "http://localhost:8080/api/user/" + userId;
      String response = HttpUtil.sendGet(apiUrl);
      JSONObject jsonResponse = new JSONObject(response);
      JSONObject userData = jsonResponse.getJSONObject("data");

      User user = new User();
      user.setId(userData.getString("_id"));
      user.setMsv(userData.getString("msv"));
      user.setFullName(userData.optString("fullname", "N/A"));
      user.setGender(userData.optString("gender", "N/A"));

      // Lấy thông tin mã ngành từ đối tượng
      JSONObject majorObject = userData.optJSONObject("majorId");
      if (majorObject != null) {
        user.setMajorId(majorObject.optString("_id", "N/A")); // Hoặc lấy tên hoặc mã ngành nếu cần
        user.setMajorName(majorObject.optString("name", "N/A"));
      } else {
        user.setMajorId("N/A");
      }

      user.setYear(userData.optString("year", "N/A"));
      user.setClassName(userData.optString("class", "N/A"));
      user.setAdmin(userData.getBoolean("isAdmin"));
      user.setGvcn(userData.optString("gvcn", "N/A"));
      user.setGvcnName(userData.optString("gvcnName", "N/A"));
      user.setDeleted(userData.optBoolean("deleted", false));
      user.setGv(userData.optBoolean("isGV", false));
      user.setAdmin(userData.optBoolean("isAdmin", false));
      user.setEmail(userData.optString("email", "N/A")); // Date of birth
      user.setDob(userData.optString("dob", "N/A")); // Date of birth
      user.setPhone(userData.optString("phone", "N/A")); // Phone number
      user.setCountry(userData.optString("country", "N/A")); // Country
      user.setAddress(userData.optString("address", "N/A")); // Address

      return user;
    }


    private Teacher fetchTeacherDetails(String userId) throws Exception {
      String apiUrl = "http://localhost:8080/api/teacher/" + userId;
      String response = HttpUtil.sendGet(apiUrl);
      JSONObject jsonResponse = new JSONObject(response);
      JSONObject teacherData = jsonResponse.getJSONObject("data");

      Teacher teacher = new Teacher();
      teacher.setId(teacherData.getString("_id"));
      teacher.setMgv(teacherData.getString("mgv"));  // Đọc trường mgv từ API
      teacher.setFullName(teacherData.optString("fullname", "Unknown"));  // Đọc trường fullname từ API
      teacher.setEmail(teacherData.optString("email", "No Email"));  // Bạn có thể thêm trường email nếu cần
      teacher.setGV(teacherData.getBoolean("isGV"));  // Đọc trường isGV từ API


      return teacher;
    }


  }
}
