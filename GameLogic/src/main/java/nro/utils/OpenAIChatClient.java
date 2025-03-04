package nro.utils;

import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.HttpURLConnection;
import java.io.*;

public class OpenAIChatClient {
    private static final String OPEN_AI_ENDPOINT = "http://localhost:1234/v1/chat/completions";

    public static void main() {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(System.in);
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "Tôi là 1 lập trình viên Java tên là Chấn Béo Đù AI"));

        while (true) {
            System.out.print("Bạn: ");
            String userInput = scanner.nextLine().trim();
            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("Thoát chương trình...");
                break;
            }

            messages.add(Map.of("role", "user", "content", userInput));

            try {
                String responseMessage = sendMessage(messages);
                messages.add(Map.of("role", "assistant", "content", responseMessage));
                System.out.println("Assistant: " + responseMessage);
            } catch (Exception e) {
                System.err.println("Lỗi khi gửi yêu cầu API: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static String sendMessage(List<Map<String, String>> messages) throws Exception {
        String jsonInputString = "{\"model\": \"vistral-7b-chat\", \"messages\": " + new com.google.gson.Gson().toJson(messages) + ", \"temperature\": 0.7, \"max_tokens\": 2000}";

        @SuppressWarnings("deprecation")
        HttpURLConnection connection = (HttpURLConnection) new java.net.URL(OPEN_AI_ENDPOINT).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        JsonObject jsonResponse = getJsonObject(connection, jsonInputString);
        return jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject().get("message").getAsJsonObject().get("content").getAsString();
    }

    private static JsonObject getJsonObject(HttpURLConnection connection, String jsonInputString) throws IOException {
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        return new com.google.gson.JsonParser().parse(response.toString()).getAsJsonObject();
    }
}
