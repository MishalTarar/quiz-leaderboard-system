import java.net.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;

public class Main {

    public static void main(String[] args) {
        try {
            String baseUrl = "https://devapigw.vidalhealthtpa.com/srm-quiz-task/quiz/messages";
            String regNo = "RA2311003011406";

            Set<String> seen = new HashSet<>();
            Map<String, Integer> scores = new HashMap<>();

            for (int poll = 0; poll < 10; poll++) {
                String urlStr = baseUrl + "?regNo=" + regNo + "&poll=" + poll;
                URL pollUrl = new URL(urlStr);

                HttpURLConnection conn = (HttpURLConnection) pollUrl.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JsonObject jsonObj = JsonParser.parseString(response.toString()).getAsJsonObject();
                JsonArray events = jsonObj.getAsJsonArray("events");

                System.out.println("Poll " + poll + " | events: " + events.size());

                for (JsonElement e : events) {
                    JsonObject event = e.getAsJsonObject();

                    String roundId = event.get("roundId").getAsString();
                    String participant = event.get("participant").getAsString();
                    int score = event.get("score").getAsInt();

                    String key = roundId + "_" + participant;

                    if (!seen.contains(key)) {
                        seen.add(key);
                        scores.put(participant, scores.getOrDefault(participant, 0) + score);
                        System.out.println("  Added: " + participant + " | round: " + roundId + " | score: " + score);
                    } else {
                        System.out.println("  Duplicate skipped: " + key);
                    }
                }

                if (poll < 9) Thread.sleep(5000);
            }

            // Building leaderboard sorted by score descending
            List<Map.Entry<String, Integer>> list = new ArrayList<>(scores.entrySet());
            list.sort((a, b) -> b.getValue() - a.getValue());

            JsonArray leaderboard = new JsonArray();
            int total = 0;

            System.out.println("\n--- Leaderboard ---");
            for (Map.Entry<String, Integer> entry : list) {
                JsonObject item = new JsonObject();
                item.addProperty("participant", entry.getKey());
                item.addProperty("totalScore", entry.getValue());
                leaderboard.add(item);
                total += entry.getValue();
                System.out.println(entry.getKey() + " -> " + entry.getValue());
            }
            System.out.println("Total Score: " + total);

            // Building final payload
            JsonObject finalObj = new JsonObject();
            finalObj.addProperty("regNo", regNo);
            finalObj.add("leaderboard", leaderboard);

            System.out.println("\nSending payload: " + finalObj.toString());

            // Submit
            URL submitUrl = new URL("https://devapigw.vidalhealthtpa.com/srm-quiz-task/quiz/submit");
            HttpURLConnection submitConn = (HttpURLConnection) submitUrl.openConnection();
            submitConn.setRequestMethod("POST");
            submitConn.setRequestProperty("Content-Type", "application/json");
            submitConn.setRequestProperty("Accept", "application/json");
            submitConn.setDoOutput(true);

            byte[] payload = finalObj.toString().getBytes("UTF-8");
            submitConn.setRequestProperty("Content-Length", String.valueOf(payload.length));

            OutputStream os = submitConn.getOutputStream();
            os.write(payload);
            os.flush();
            os.close();

            int responseCode = submitConn.getResponseCode();
            System.out.println("HTTP Status: " + responseCode);

            InputStream is = (responseCode >= 200 && responseCode < 300)
                    ? submitConn.getInputStream()
                    : submitConn.getErrorStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            StringBuilder res = new StringBuilder();
            while ((line = br.readLine()) != null) {
                res.append(line);
            }
            br.close();

            System.out.println("Submission Response: " + res.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}