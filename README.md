Build a system that checks a quiz validator API 10 times removes responses combines scores and submits a correct leaderboard.
Here's how it works:

1. It checks `/quiz/messages` 10 times (poll=0 to poll=9) with 5-second delays.
2. It removes events using `roundId + participant` as a unique key.

It does the following:
- Combines total score per participant.
- Sorts leaderboard by total score in descending order.
- Submits to `/quiz/submit` exactly once.

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

**Tech Stack Used**
- Java 17
- Maven for building
- OkHttp 4.12 for HTTP requests
- Gson 2.10 for JSON parsing

**Running the System**
- Main.java` and set your `REG_NO`.
- Run `mvn compile exec:java`, in the terminal.
- Wait 50 seconds for all 10 checks to complete.
- Check the submission result in the terminal output.
