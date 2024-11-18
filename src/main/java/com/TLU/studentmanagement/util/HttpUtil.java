package main.java.com.TLU.studentmanagement.util;

import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class HttpUtil {

    private static String accessToken = "";
    private static String refreshToken = "";
    private static boolean refreshingToken = false;
    private static String lastResponse = "";

    // Cập nhật accessToken và refreshToken
    public static void setTokens(String accessToken, String refreshToken) {
        HttpUtil.accessToken = accessToken;
        HttpUtil.refreshToken = refreshToken;
    }


    private static String sendRequest(String apiUrl, String method, String requestData, String token) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        if (token != null && !token.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }

        if (requestData != null) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        int responseCode = conn.getResponseCode();
        BufferedReader br;
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            String errorResponse = readErrorStream(conn);
            switch (responseCode) {
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    throw new RuntimeException(errorResponse);
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    throw new RuntimeException(errorResponse);
                case HttpURLConnection.HTTP_FORBIDDEN:
                    throw new RuntimeException(errorResponse);
                case HttpURLConnection.HTTP_NOT_FOUND:
                    throw new RuntimeException(errorResponse);
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    throw new RuntimeException(errorResponse);
                default:
                    throw new RuntimeException("Failed : HTTP error code : " + responseCode + " - " + conn.getResponseMessage() + " - " + errorResponse);
            }
        }

        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }
        br.close();
        lastResponse = response.toString(); // Cập nhật phản hồi cuối cùng
        return lastResponse;
    }

    private static String readErrorStream(HttpURLConnection conn) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        StringBuilder errorResponse = new StringBuilder();
        String errorLine;
        while ((errorLine = br.readLine()) != null) {
            errorResponse.append(errorLine);
        }
        return errorResponse.toString();
    }

    public static String getLastResponse() {
        return lastResponse;
    }


    // Phương thức chung cho tất cả các yêu cầu HTTP không cần token
    private static String sendRequestWithoutToken(String apiUrl, String method, String requestData) throws Exception {
        return sendRequest(apiUrl, method, requestData, null);
    }

    public static String sendRequestWithRefresh(String apiUrl, String method, String requestData) throws Exception {
        try {
            return sendRequest(apiUrl, method, requestData, accessToken);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Unauthorized")) {
                if (!refreshingToken && refreshAccessToken()) {
                    return sendRequest(apiUrl, method, requestData, accessToken);  // Thực hiện lại request với token mới
                } else {
                    throw new RuntimeException("Failed to refresh token.");
                }
            } else {
                throw e;
            }
        }
    }

    public static String sendGet(String apiUrl) throws Exception {
        return sendRequestWithRefresh(apiUrl, "GET", null);
    }

    public static String sendPost(String apiUrl, String requestData) throws Exception {
        try {
            return sendRequestWithRefresh(apiUrl, "POST", requestData);
        } catch (RuntimeException e) {
            // Xử lý lỗi cụ thể hoặc ghi log nếu cần
            return e.getMessage();
        }
    }


    public static String sendPut(String apiUrl, String requestData) throws Exception {
        try {
            return sendRequestWithRefresh(apiUrl, "PUT", requestData);
        } catch (RuntimeException e) {
            throw new RuntimeException("PUT request failed: " + e.getMessage());
        }
    }

    public static String sendDelete(String apiUrl) throws Exception {
        try {
            return sendRequestWithRefresh(apiUrl, "DELETE", null);
        } catch (RuntimeException e) {
            throw new RuntimeException("DELETE request failed: " + e.getMessage());
        }
    }


    // Phương thức làm mới accessToken
    private static boolean refreshAccessToken() throws Exception {
        refreshingToken = true;
        try {
            String apiUrl = "http://localhost:8080/api/auth/refresh";
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("refreshToken", refreshToken);

            System.out.println("Sending refresh token request with refreshToken: " + refreshToken);
            String response = sendRequestWithoutToken(apiUrl, "POST", jsonInput.toString());
            System.out.println("Response from refresh token request: " + response);

            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.has("tokens")) {
                String newAccessToken = jsonResponse.getJSONObject("tokens").getString("accessToken");
                String newRefreshToken = jsonResponse.getJSONObject("tokens").getString("refreshToken");

                // Cập nhật accessToken và refreshToken mới
                setTokens(newAccessToken, newRefreshToken);
                System.out.println("New accessToken: " + newAccessToken);
                System.out.println("New accessToken: " + accessToken);
                System.out.println("New refreshToken: " + refreshToken);

                return true;
            } else {
                System.out.println("Refresh token response does not contain tokens.");
                return false;
            }
        } finally {
            refreshingToken = false;
        }
    }
}
