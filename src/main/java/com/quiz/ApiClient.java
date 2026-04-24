package com.quiz;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.quiz.model.LeaderboardEntry;
import com.quiz.model.PollResponse;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {

    private static final String BASE_URL =
        "https://devapigw.vidalhealthtpa.com/srm-quiz-task";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private final String regNo;

    public ApiClient(String regNo) {
        this.regNo = regNo;
    }

    public PollResponse getPoll(int pollIndex) throws IOException {
        String url = BASE_URL + "/quiz/messages?regNo=" + regNo + "&poll=" + pollIndex;

        Request request = new Request.Builder()
            .url(url)
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            System.out.println("Poll " + pollIndex + " raw response: " + body);

            // Check if response is successful
            if (!response.isSuccessful()) {
                throw new IOException("API request failed with status " + response.code() + ": " + body);
            }

            // Check if the response body is empty or not JSON
            if (body == null || body.trim().isEmpty()) {
                throw new IOException("Empty response from API");
            }

            // Check if response starts with '{' (JSON object) or '[' (JSON array)
            if (!body.trim().startsWith("{") && !body.trim().startsWith("[")) {
                throw new IOException("API returned non-JSON response: " + body);
            }

            return gson.fromJson(body, PollResponse.class);
        }
    }

    public String submitLeaderboard(List<LeaderboardEntry> leaderboard) throws IOException {
        SubmitRequest submitRequest = new SubmitRequest(regNo, leaderboard);
        String json = gson.toJson(submitRequest);

        System.out.println("Submitting: " + json);

        RequestBody body = RequestBody.create(
            json,
            MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
            .url(BASE_URL + "/quiz/submit")
            .post(body)
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public void close() {
        try {
            client.dispatcher().executorService().shutdownNow();
            client.connectionPool().evictAll();
            client.dispatcher().cancelAll();
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    // Inner class to build the JSON payload for submission
    static class SubmitRequest {
        String regNo;
        List<LeaderboardEntry> leaderboard;

        SubmitRequest(String regNo, List<LeaderboardEntry> leaderboard) {
            this.regNo = regNo;
            this.leaderboard = leaderboard;
        }
    }
}