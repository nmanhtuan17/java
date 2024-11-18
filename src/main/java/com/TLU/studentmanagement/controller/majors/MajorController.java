package main.java.com.TLU.studentmanagement.controller.majors;

import main.java.com.TLU.studentmanagement.model.Major;
import main.java.com.TLU.studentmanagement.util.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MajorController {

    public static List<Major> getAllMajors() throws Exception {
        String apiUrl = "http://localhost:8080/api/major/getAll";
        String response = HttpUtil.sendGet(apiUrl);
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray majorsArray = jsonResponse.getJSONArray("data");

        List<Major> majors = new ArrayList<>();
        for (int i = 0; i < majorsArray.length(); i++) {
            JSONObject majorObject = majorsArray.getJSONObject(i);
            Major major = new Major();
            major.setId(majorObject.getString("_id"));
            major.setName(majorObject.getString("name"));
            major.setCode(majorObject.getString("code"));
            majors.add(major);
        }
        return majors;
    }

    public static Major getMajorById(String id) throws Exception {
        String apiUrl = "http://localhost:8080/api/major/get/" + id; // Cập nhật URL API
        String response = HttpUtil.sendGet(apiUrl);
        JSONObject jsonResponse = new JSONObject(response);
        JSONObject majorObject = jsonResponse.getJSONObject("data");

        Major major = new Major();
        major.setId(majorObject.getString("_id"));
        major.setName(majorObject.getString("name"));
        major.setCode(majorObject.getString("code"));

        return major;
    }

    public JSONObject createMajor(String name, String code) throws Exception {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("name", name);
        jsonInput.put("code", code);

        String apiUrl = "http://localhost:8080/api/major/create";
        String response = HttpUtil.sendPost(apiUrl, jsonInput.toString());
        System.out.println(response);
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse;
    }

    public static void updateMajor(String id, String name, String code) throws Exception {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("name", name);
        jsonInput.put("code", code);

        String apiUrl = "http://localhost:8080/api/major/update/" + id;
        HttpUtil.sendPut(apiUrl, jsonInput.toString());
    }

    public static void deleteMajor(String id) throws Exception {
        String apiUrl = "http://localhost:8080/api/major/delete/" + id;
        HttpUtil.sendDelete(apiUrl);
    }
}
