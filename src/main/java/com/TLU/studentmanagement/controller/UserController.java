package main.java.com.TLU.studentmanagement.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.com.TLU.studentmanagement.model.User;
import main.java.com.TLU.studentmanagement.session.UserSession;
import main.java.com.TLU.studentmanagement.util.HttpUtil;
import main.java.com.TLU.studentmanagement.view.pages.Information.PersonalInfoPanel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import raven.toast.Notifications;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserController {

    private static final String BASE_URL = "http://localhost:8080/api/user/";

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            String response = HttpUtil.sendGet(BASE_URL + "getAll");
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray jsonArray = jsonResponse.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                User user = new User();
                user.setId(jsonObj.getString("_id"));
                user.setFullName(jsonObj.optString("fullname"));
                user.setMsv(jsonObj.getString("msv"));
                user.setYear(jsonObj.optString("year"));
                user.setGender(jsonObj.optString("gender"));
                user.setClassName(jsonObj.optString("class"));
                user.setEmail(jsonObj.optString("email"));
//                user.setMajorId(jsonObj.optString("majorId"));
                user.setAdmin(jsonObj.getBoolean("isAdmin"));
                user.setGv(jsonObj.optBoolean("isGV"));
                user.setDeleted(jsonObj.getBoolean("deleted"));

                // Kiểm tra và gán majorId nếu tồn tại và là đối tượng JSON
                if (jsonObj.has("majorId") && !jsonObj.isNull("majorId")) {
                    Object majorIdObj = jsonObj.get("majorId");
                    if (majorIdObj instanceof JSONObject) {
                        JSONObject majorIdJson = (JSONObject) majorIdObj;
                        if (majorIdJson.has("_id")) {
                            user.setMajorId(majorIdJson.getString("_id"));
                            user.setMajorName(majorIdJson.getString("name"));
                        } else {
                            user.setMajorId(""); // Gán giá trị mặc định là rỗng nếu không tìm thấy _id trong đối tượng JSON
                        }
                    } else if (majorIdObj instanceof String) {
                        user.setMajorId((String) majorIdObj);
                    } else {
                        user.setMajorId(""); // Gán giá trị mặc định là rỗng nếu majorId không phải là chuỗi hoặc đối tượng JSON
                    }
                } else {
                    user.setMajorId(""); // Gán giá trị mặc định là rỗng nếu majorId không tồn tại
                }

                // Kiểm tra và gán majorId nếu tồn tại và là đối tượng JSON
                if (jsonObj.has("gvcn") && !jsonObj.isNull("gvcn")) {
                    Object gvcnObj = jsonObj.get("gvcn");
                    if (gvcnObj instanceof JSONObject) {
                        JSONObject gvcnObjJson = (JSONObject) gvcnObj;
                        if (gvcnObjJson.has("_id")) {
                            user.setGvcn(gvcnObjJson.getString("_id"));
                            user.setGvcnName(gvcnObjJson.getString("fullname"));
                        } else {
                            user.setGvcn(""); // Gán giá trị mặc định là rỗng nếu không tìm thấy _id trong đối tượng JSON
                        }
                    } else if (gvcnObj instanceof String) {
                        user.setGvcn((String) gvcnObj);
                    } else {
                        user.setGvcn(""); // Gán giá trị mặc định là rỗng nếu majorId không phải là chuỗi hoặc đối tượng JSON
                    }
                } else {
                    user.setGvcn(""); // Gán giá trị mặc định là rỗng nếu majorId không tồn tại
                }


                users.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi phân tích dữ liệu JSON: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải danh sách người dùng: " + e.getMessage());
        }
        return users;
    }

    public static User fetchUserDetails(String userId) throws Exception {
        // Tạo đối tượng User để lưu thông tin người dùng
        User user = new User();

        // Gửi yêu cầu GET đến API để lấy dữ liệu người dùng
        String response = HttpUtil.sendGet(BASE_URL + userId);
        JSONObject jsonResponse = new JSONObject(response);
        JSONObject jsonObj = jsonResponse.getJSONObject("data"); // Lấy đối tượng dữ liệu chính

        // Kiểm tra xem đối tượng có id trùng với userId không
        if (jsonObj.optString("_id").equals(userId)) {
            user.setId(jsonObj.optString("_id"));
            user.setMsv(jsonObj.optString("msv", "N/A"));
            user.setFullName(jsonObj.optString("fullname", "N/A"));
            user.setGender(jsonObj.optString("gender", "N/A"));

            // Lấy thông tin mã ngành từ đối tượng
            JSONObject majorObject = jsonObj.optJSONObject("majorId");
            if (majorObject != null) {
                user.setMajorId(majorObject.optString("_id", "N/A"));
                user.setMajorName(majorObject.optString("name", "N/A"));
            } else {
                user.setMajorId("N/A");
            }

            user.setYear(jsonObj.optString("year", "N/A"));
            user.setClassName(jsonObj.optString("class", "N/A"));
            user.setAdmin(jsonObj.optBoolean("isAdmin", false));
            user.setGvcn(jsonObj.optString("gvcn", "N/A"));
            user.setGvcnName(jsonObj.optString("gvcnName", "N/A"));
            user.setDeleted(jsonObj.optBoolean("deleted", false));
            user.setGv(jsonObj.optBoolean("isGV", false));
            user.setEmail(jsonObj.optString("email", "N/A"));
            user.setDob(jsonObj.optString("dob", "N/A"));
            user.setPhone(jsonObj.optString("phone", "N/A"));
            user.setCountry(jsonObj.optString("country", "N/A"));
            user.setAddress(jsonObj.optString("address", "N/A"));
        } else {
            throw new Exception("User ID không khớp với dữ liệu");
        }

        // Trả về đối tượng User đã được cập nhật thông tin
        return user;
    }




    public JSONObject createUser(User user) throws Exception {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("fullname", user.getFullName());
            jsonObj.put("msv", user.getMsv());
            jsonObj.put("year", user.getYear());
            jsonObj.put("gvcn", user.getGvcn());
            jsonObj.put("gender", user.getGender());
            jsonObj.put("className", user.getClassName());
            jsonObj.put("email", user.getEmail());
            jsonObj.put("majorId", user.getMajorId());

//            System.out.println(jsonObj);
//            System.out.println("MajorId: " + user.getMajorId());
//            System.out.println("GVCN: " + user.getGvcn());

            String response = HttpUtil.sendPost(BASE_URL + "create-user", jsonObj.toString());
            JSONObject responseJson = new JSONObject(response);

            return responseJson;

//            System.out.println("API response: " + responseJson);

//            JOptionPane.showMessageDialog(null, responseJson.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateUser(String userId, User user) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("gvcn", user.getGvcn());
            jsonObj.put("majorId", user.getMajorId());

//            System.out.println("gvcnId: " + user.getGvcn());
//            System.out.println("majorId: " + user.getMajorId());

            String response = HttpUtil.sendPut(BASE_URL + "updateByAdmin/" + userId, jsonObj.toString());
            JSONObject responseJson = new JSONObject(response);

//            System.out.println("API response: " + response);
//            System.out.println("UserId: " + userId);

//            JOptionPane.showMessageDialog(null, responseJson.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật người dùng: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật người dùng: " + e.getMessage());
        }
    }

    public static void deleteUser(String userId) {
        try {
            String response = HttpUtil.sendDelete(BASE_URL + "delete/" + userId);
            JSONObject responseJson = new JSONObject(response);

//            JOptionPane.showMessageDialog(null, responseJson.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xóa người dùng: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xóa người dùng: " + e.getMessage());
        }
    }

    public void restoreUser(String msv) {
        try {
            String response = HttpUtil.sendPut(BASE_URL + "restore/" + msv, null);
            JSONObject responseJson = new JSONObject(response);

            JOptionPane.showMessageDialog(null, responseJson.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi khôi phục người dùng: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi khôi phục người dùng: " + e.getMessage());
        }
    }

    public static boolean updateUserInformation(String fullName, String email, String phone, String address, String dob, String gender) {
        try {
            // Get user ID from the session
            String userId = UserSession.getUser().getId();

            // Prepare data for the request
            JSONObject requestData = new JSONObject();
            requestData.put("fullname", fullName);
            requestData.put("email", email);
            requestData.put("phone", phone);
            requestData.put("address", address);
            requestData.put("dob", dob);
            requestData.put("gender", gender);

            // Make the API call
            String apiUrl = BASE_URL + "updateProfile/" + userId; // Use BASE_URL to form the complete URL
            String response = HttpUtil.sendPut(apiUrl, requestData.toString()); // Use sendPut for update requests

            // Process response
            JSONObject jsonResponse = new JSONObject(response);
            String message = jsonResponse.getString("message");

//            JOptionPane.showMessageDialog(null, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE); // Use null for parentComponent
            Notifications.getInstance().show(Notifications.Type.SUCCESS, message);
            // Return true if the update was successful
            return jsonResponse.getString("message").equals("Update success");

        } catch (JSONException e) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(null, "Cập nhật thông tin không thành công: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật thông tin người dùng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


    public static List<User> searchStudents(String keyword) {
        List<User> students = new ArrayList<>();
        try {
            // Mã hóa từ khóa
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.toString());

            // Thêm từ khóa vào URL
            String apiUrl = BASE_URL + "searchStudents/?keyword=" + encodedKeyword;

            String response = HttpUtil.sendPost(apiUrl, null);  // Không cần requestData cho phương thức GET
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray jsonArray = jsonResponse.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                User user = new User();
                user.setId(jsonObj.optString("_id"));
                user.setFullName(jsonObj.optString("fullname"));
                user.setMsv(jsonObj.getString("msv"));
                user.setYear(jsonObj.optString("year"));
                user.setClassName(jsonObj.optString("class"));
                user.setEmail(jsonObj.optString("email"));
                // Lấy majorId từ đối tượng JSON
                JSONObject majorIdObj = jsonObj.optJSONObject("majorId");
                if (majorIdObj != null) {
                    user.setMajorId(majorIdObj.optString("_id"));
                    // Nếu bạn cũng muốn lấy tên của chuyên ngành, có thể thêm như sau:
                    String majorName = majorIdObj.optString("name");
                    // Nếu bạn cần tên chuyên ngành, bạn có thể lưu nó vào một thuộc tính khác hoặc sử dụng theo cách khác.
                    user.setMajorName(majorName); // Ví dụ: nếu có thuộc tính này trong User class
                }
                user.setGvcn(jsonObj.optString("gvcn"));
                students.add(user);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error parsing JSON data: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error searching students: " + e.getMessage());
        }

        return students;
    }


}
