package com.quiz;

import java.util.List;

import com.quiz.model.LeaderboardEntry;
import com.quiz.model.PollResponse;

public class Main {

    // ⚠️ CHANGE THIS to your actual registration number!
    private static final String REG_NO = "YOUR_REG_NUMBER_HERE";

    public static void main(String[] args) throws Exception {

        // Check if registration number has been set
        if ("YOUR_REG_NUMBER_HERE".equals(REG_NO)) {
            System.out.println("WARNING: Using placeholder registration number. This may not work with the API.");
            System.out.println("Please update the REG_NO constant in Main.java with your actual registration number!");
            System.out.println("Continuing anyway to show API response...\n");
        }

        ApiClient apiClient = new ApiClient(REG_NO);
        LeaderboardService service = new LeaderboardService();

        List<LeaderboardEntry> leaderboard = null;
        int total = 0;

        try {
            System.out.println("========================================");
            System.out.println("  Quiz Leaderboard System Starting...");
            System.out.println("  Registration No: " + REG_NO);
            System.out.println("========================================");

            // Poll 10 times (index 0 to 9)
            for (int poll = 0; poll <= 9; poll++) {
                System.out.println("\n--- Fetching Poll " + poll + " ---");

                PollResponse response = apiClient.getPoll(poll);
                service.processResponse(response);

                // Wait 5 seconds between polls (not after the last one)
                if (poll < 9) {
                    System.out.println("Waiting 5 seconds before next poll...");
                    Thread.sleep(5000);
                }
            }

            // Build and display the final leaderboard
            leaderboard = service.getLeaderboard();
            total = service.getTotalScore();

            System.out.println("\n========================================");
            System.out.println("         FINAL LEADERBOARD");
            System.out.println("========================================");
            for (int i = 0; i < leaderboard.size(); i++) {
                LeaderboardEntry e = leaderboard.get(i);
                System.out.println((i + 1) + ". " + e.participant + " — " + e.totalScore);
            }
            System.out.println("----------------------------------------");
            System.out.println("Total score across all users: " + total);

            // Submit once
            System.out.println("\n--- Submitting leaderboard... ---");
            String result = apiClient.submitLeaderboard(leaderboard);
            System.out.println("API Response: " + result);
            System.out.println("========================================");
        } catch (Exception e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            if (e.getMessage().contains("503") || e.getMessage().contains("no available server")) {
                System.err.println("The quiz API server appears to be currently unavailable.");
                System.err.println("Please try again later when the server is back online.");
            } else if (e.getMessage().contains("non-JSON response")) {
                System.err.println("The API returned an unexpected response format.");
                System.err.println("Please check your registration number or contact support.");
            }
        } finally {
            // Ensure HTTP client resources are properly cleaned up
            apiClient.close();
        }
    }
}