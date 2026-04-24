package com.quiz;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.quiz.model.Event;
import com.quiz.model.LeaderboardEntry;
import com.quiz.model.PollResponse;

public class LeaderboardService {

    // Tracks unique roundId + participant combos to prevent duplicates
    private final Set<String> seenEvents = new HashSet<>();

    // Stores total score per participant
    private final Map<String, Integer> scores = new HashMap<>();

    public void processResponse(PollResponse response) {
        if (response == null || response.events == null) {
            System.out.println("  (empty response, skipping)");
            return;
        }

        for (Event event : response.events) {
            // This key uniquely identifies each round entry
            String key = event.roundId + "|" + event.participant;

            if (seenEvents.contains(key)) {
                System.out.println("  DUPLICATE skipped: " + key);
            } else {
                seenEvents.add(key);
                scores.merge(event.participant, event.score, Integer::sum);
                System.out.println("  ADDED: " + event.participant
                    + " scored " + event.score + " in " + event.roundId);
            }
        }
    }

    public List<LeaderboardEntry> getLeaderboard() {
        return scores.entrySet().stream()
            .map(e -> new LeaderboardEntry(e.getKey(), e.getValue()))
            .sorted((a, b) -> b.totalScore - a.totalScore) // highest first
            .collect(Collectors.toList());
    }

    public int getTotalScore() {
        return scores.values().stream().mapToInt(Integer::intValue).sum();
    }
}