# Quiz Leaderboard System

## Overview

This project is a backend integration solution for a quiz leaderboard system. The application consumes API responses from a validator service, processes the data, handles duplicates, and generates a final leaderboard.

The challenge focuses on correctly handling duplicate API responses and computing accurate scores.

---

## Objective

* Poll API 10 times
* Handle duplicate data correctly
* Aggregate participant scores
* Generate a sorted leaderboard
* Compute total score
* Submit the result

---

## Tech Stack

* Java
* HTTPURLConnection (API calls)
* Gson (JSON parsing)
* Collections Framework (HashMap, HashSet, ArrayList)

---

## Approach

### 1. API Polling

* Called the API 10 times using poll values from 0 to 9
* Maintained a mandatory delay of 5 seconds between each call

### 2. Data Collection

* Parsed JSON responses using Gson
* Extracted event data (roundId, participant, score)

### 3. Deduplication

* Used a HashSet to track unique events
* Unique key: `(roundId + participant)`
* Ensured duplicate events across polls were ignored

### 4. Score Aggregation

* Used HashMap to store total scores per participant
* Added scores only for unique events

### 5. Leaderboard Generation

* Sorted participants by total score (descending order)
* Created structured JSON leaderboard

### 6. Total Score Calculation

* Computed sum of all participant scores

### 7. Submission

* Sent final leaderboard using POST request

---

## Sample Output

```
George -> 795  
Hannah -> 750  
Ivan -> 745  

Total Score: 2290
```

---

## Note on Submission Attempts

During development and testing, multiple submissions were made to the validator API. Due to this, the API stopped returning validation fields such as `"isCorrect"` in later responses.

However, the implementation strictly follows all problem requirements:

* 10 API polls with proper delay
* Correct deduplication using `(roundId + participant)`
* Accurate aggregation of scores
* Properly sorted leaderboard
* Consistent total score of **2290**

The results are reproducible and logically correct.

---

## How to Run

1. Clone the repository
2. Open in IntelliJ IDEA or any Java IDE
3. Add Gson dependency:

   ```
   com.google.code.gson:gson:2.10.1
   ```
4. Update your `regNo` in the code
5. Run the program

---

## Key Learnings

* Handling duplicate data in distributed systems
* API integration and polling strategies
* Data aggregation and sorting
* Writing clean and maintainable backend logic

---

## 👨‍💻 Author

Your Name
