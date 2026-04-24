Build a system that polls a quiz validator API 10 times, deduplicates
the responses, aggregates scores, and submits a correct leaderboard.

ITS WORKING--
1. Polls `/quiz/messages` 10 times (poll=0 to poll=9) with 5-second delays
2. Deduplicates events using `roundId + participant` as a unique composite key
3. Aggregates total score per participant
4. Sorts leaderboard by totalScore (descending)
5. Submits to `/quiz/submit` exactly once

KEY LOGIC (DUPLICATION)--
        ```java
        String key = event.roundId + "|" + event.participant;
        if (seenEvents.contains(key)) {
            // ignore duplicate
        } else {
            seenEvents.add(key);
            scores.merge(event.participant, event.score, Integer::sum);
        }
        ```

TECH STACK--
- Java 17
- Maven (build tool)
- OkHttp 4.12 (HTTP client)
- Gson 2.10 (JSON parsing)

How to Run-
- Open `Main.java` and set your `REG_NO`
- In terminal run:
```bash
mvn compile exec:java
```
- Wait ~50 seconds for all 10 polls to complete
- Check the submission result in terminal output
