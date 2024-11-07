package edu.uga.cs.superfinalstatecapital;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Manages all database operations for the State Capitals Quiz app.
 * Handles states data, quiz tracking, and results storage/retrieval.
 */
public class QuizData {
    public static final String DEBUG_TAG = "QuizData";
    private static final int QUESTIONS_PER_QUIZ = 6;

    // Database instance and helper
    private SQLiteDatabase db;
    private SQLiteOpenHelper quizDbHelper;

    // Column names for state table queries
    private static final String[] allStateColumns = {
            StateQuizDBHelper.STATES_COLUMN_ID,
            StateQuizDBHelper.STATES_COLUMN_NAME,
            StateQuizDBHelper.STATES_COLUMN_CAPITAL,
            StateQuizDBHelper.STATES_COLUMN_CITY2,
            StateQuizDBHelper.STATES_COLUMN_CITY3
    };

    /**
     * Initializes database access helper
     */
    public QuizData(Context context) {
        this.quizDbHelper = StateQuizDBHelper.getInstance(context);
    }

    /**
     * Opens writable database connection
     */
    public void open() {
        db = quizDbHelper.getWritableDatabase();
        Log.d(DEBUG_TAG, "QuizData: db open");
    }

    /**
     * Closes database connection if open
     */
    public void close() {
        if (quizDbHelper != null) {
            quizDbHelper.close();
            Log.d(DEBUG_TAG, "QuizData: db closed");
        }
    }

    /**
     * Checks if database connection is active
     */
    public boolean isDBOpen() {
        return db != null && db.isOpen();
    }

    /**
     * Stores a new state in the database
     * @param state State object containing state data
     * @return Updated state object with database ID
     */
    public State storeState(State state) {
        if (state != null) {
            ContentValues values = new ContentValues();
            values.put(StateQuizDBHelper.STATES_COLUMN_NAME, state.getName());
            values.put(StateQuizDBHelper.STATES_COLUMN_CAPITAL, state.getCapital());
            values.put(StateQuizDBHelper.STATES_COLUMN_CITY2, state.getCity2());
            values.put(StateQuizDBHelper.STATES_COLUMN_CITY3, state.getCity3());

            try {
                long id = db.insert(StateQuizDBHelper.TABLE_STATES, null, values);
                state.setId(id);
                Log.d(DEBUG_TAG, "Stored new state with id: " + id);
            } catch (Exception e) {
                Log.e(DEBUG_TAG, "Error storing state: " + e.getMessage());
            }
        }
        return state;
    }

    /**
     * Creates new quiz entry with current timestamp
     * @return ID of created quiz or -1 if creation failed
     */
    public long startNewQuiz() {
        long id = -1;
        ContentValues values = new ContentValues();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(new Date());
        values.put(StateQuizDBHelper.QUIZ_COLUMN_DATE, currentDate);
        values.put(StateQuizDBHelper.QUIZ_COLUMN_SCORE, 0);
        values.put(StateQuizDBHelper.QUIZ_COLUMN_QUESTIONS_ANSWERED, 0);

        try {
            id = db.insert(StateQuizDBHelper.TABLE_QUIZZES, null, values);
            Log.d(DEBUG_TAG, "Created new quiz with id: " + id);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Error creating quiz: " + e.getMessage());
        }
        return id;
    }

    /**
     * Stores a quiz question answer
     * @param quizId ID of current quiz
     * @param stateId ID of state being asked about
     * @param userAnswer User's selected answer
     */
    public void storeQuizQuestion(long quizId, long stateId, String userAnswer) {
        try {
            ContentValues values = new ContentValues();
            values.put(StateQuizDBHelper.QUESTION_COLUMN_QUIZ_ID, quizId);
            values.put(StateQuizDBHelper.QUESTION_COLUMN_STATE_ID, stateId);
            values.put(StateQuizDBHelper.QUESTION_COLUMN_USER_ANSWER, userAnswer);

            db.insert(StateQuizDBHelper.TABLE_QUIZ_QUESTIONS, null, values);
            Log.d(DEBUG_TAG, "Stored quiz question for quiz: " + quizId);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Error storing quiz question: " + e.getMessage());
        }
    }

    /**
     * Updates quiz score and progress
     * @param quizId ID of quiz to update
     * @param score Current score
     * @param questionsAnswered Number of questions completed
     */
    public void updateQuizScore(long quizId, int score, int questionsAnswered) {
        try {
            ContentValues values = new ContentValues();
            values.put(StateQuizDBHelper.QUIZ_COLUMN_SCORE, score);
            values.put(StateQuizDBHelper.QUIZ_COLUMN_QUESTIONS_ANSWERED, questionsAnswered);

            String whereClause = StateQuizDBHelper.QUIZ_COLUMN_ID + "=?";
            String[] whereArgs = {String.valueOf(quizId)};

            int rowsUpdated = db.update(StateQuizDBHelper.TABLE_QUIZZES, values, whereClause, whereArgs);
            Log.d(DEBUG_TAG, "Updated quiz score. Rows affected: " + rowsUpdated);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Error updating quiz score: " + e.getMessage());
        }
    }

    /**
     * Retrieves all states from database
     * @return List of all states
     */
    public List<State> retrieveAllStates() {
        ArrayList<State> states = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(StateQuizDBHelper.TABLE_STATES, allStateColumns,
                    null, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                int idIndex = cursor.getColumnIndex(StateQuizDBHelper.STATES_COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(StateQuizDBHelper.STATES_COLUMN_NAME);
                int capitalIndex = cursor.getColumnIndex(StateQuizDBHelper.STATES_COLUMN_CAPITAL);
                int city2Index = cursor.getColumnIndex(StateQuizDBHelper.STATES_COLUMN_CITY2);
                int city3Index = cursor.getColumnIndex(StateQuizDBHelper.STATES_COLUMN_CITY3);

                while (cursor.moveToNext()) {
                    State state = new State();
                    if (idIndex >= 0) state.setId(cursor.getLong(idIndex));
                    if (nameIndex >= 0) state.setName(cursor.getString(nameIndex));
                    if (capitalIndex >= 0) state.setCapital(cursor.getString(capitalIndex));
                    if (city2Index >= 0) state.setCity2(cursor.getString(city2Index));
                    if (city3Index >= 0) state.setCity3(cursor.getString(city3Index));
                    states.add(state);
                }
            }
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Error retrieving states: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return states;
    }

    /**
     * Retrieves all past quiz results ordered by date
     * @return List of quiz results
     */
    public List<QuizResult> getPastQuizResults() {
        ArrayList<QuizResult> results = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(StateQuizDBHelper.TABLE_QUIZZES,
                    null, null, null, null, null,
                    StateQuizDBHelper.QUIZ_COLUMN_DATE + " DESC");

            if (cursor != null && cursor.getCount() > 0) {
                int idIndex = cursor.getColumnIndex(StateQuizDBHelper.QUIZ_COLUMN_ID);
                int dateIndex = cursor.getColumnIndex(StateQuizDBHelper.QUIZ_COLUMN_DATE);
                int scoreIndex = cursor.getColumnIndex(StateQuizDBHelper.QUIZ_COLUMN_SCORE);

                while (cursor.moveToNext()) {
                    long id = idIndex >= 0 ? cursor.getLong(idIndex) : -1;
                    String date = dateIndex >= 0 ? cursor.getString(dateIndex) : "";
                    int score = scoreIndex >= 0 ? cursor.getInt(scoreIndex) : 0;

                    QuizResult result = new QuizResult(id, date, score);
                    results.add(result);
                }
            }
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Error retrieving quiz results: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return results;
    }

    /**
     * Retrieves most recent incomplete quiz
     * @return Quiz object or null if no quiz in progress
     */
    public Quiz getQuizInProgress() {
        Quiz quiz = null;
        Cursor cursor = null;

        try {
            Log.d(DEBUG_TAG, "Checking for quiz in progress...");

            // Modified query to find ONLY truly interrupted quizzes
            String selection = StateQuizDBHelper.QUIZ_COLUMN_QUESTIONS_ANSWERED + " > 0 AND " +
                    StateQuizDBHelper.QUIZ_COLUMN_QUESTIONS_ANSWERED + " < " + QUESTIONS_PER_QUIZ +
                    " AND " + StateQuizDBHelper.QUIZ_COLUMN_SCORE + " >= 0"; // Add this condition

            cursor = db.query(
                    StateQuizDBHelper.TABLE_QUIZZES,
                    null,
                    selection,  // Modified selection criteria
                    null,
                    null,
                    null,
                    StateQuizDBHelper.QUIZ_COLUMN_DATE + " DESC",
                    "1"
            );

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(StateQuizDBHelper.QUIZ_COLUMN_ID);
                int scoreIndex = cursor.getColumnIndex(StateQuizDBHelper.QUIZ_COLUMN_SCORE);
                int questionsIndex = cursor.getColumnIndex(StateQuizDBHelper.QUIZ_COLUMN_QUESTIONS_ANSWERED);
                int dateIndex = cursor.getColumnIndex(StateQuizDBHelper.QUIZ_COLUMN_DATE);

                long quizId = idIndex >= 0 ? cursor.getLong(idIndex) : -1;
                int score = scoreIndex >= 0 ? cursor.getInt(scoreIndex) : 0;
                int questionsAnswered = questionsIndex >= 0 ? cursor.getInt(questionsIndex) : 0;
                String date = dateIndex >= 0 ? cursor.getString(dateIndex) : "";

                // Additional verification that we only return actually interrupted quizzes
                if (questionsAnswered > 0 && questionsAnswered < QUESTIONS_PER_QUIZ) {
                    quiz = new Quiz(quizId, date, score, questionsAnswered);
                    Log.d(DEBUG_TAG, "Found interrupted quiz: ID=" + quizId +
                            ", Questions=" + questionsAnswered + "/" + QUESTIONS_PER_QUIZ);
                }
            }
            Log.d(DEBUG_TAG, "Quiz in progress check complete");
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Error getting quiz in progress: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return quiz;
    }

    /**
     * Retrieves states used in a specific quiz
     * @param quizId ID of quiz to retrieve states for
     * @return List of states used in quiz
     */
    public List<State> getQuizStates(long quizId) {
        List<State> states = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT DISTINCT s.* FROM " + StateQuizDBHelper.TABLE_STATES + " s " +
                    "JOIN " + StateQuizDBHelper.TABLE_QUIZ_QUESTIONS + " q " +
                    "ON s." + StateQuizDBHelper.STATES_COLUMN_ID + " = q." +
                    StateQuizDBHelper.QUESTION_COLUMN_STATE_ID +
                    " WHERE q." + StateQuizDBHelper.QUESTION_COLUMN_QUIZ_ID + " = ? " +
                    "ORDER BY q." + StateQuizDBHelper.QUESTION_COLUMN_ID;  // Fixed this line

            cursor = db.rawQuery(query, new String[]{String.valueOf(quizId)});

            Log.d(DEBUG_TAG, "Getting quiz states for quiz ID: " + quizId);
            Log.d(DEBUG_TAG, "Cursor count: " + (cursor != null ? cursor.getCount() : "null"));

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    State state = new State();
                    int idIndex = cursor.getColumnIndex(StateQuizDBHelper.STATES_COLUMN_ID);
                    int nameIndex = cursor.getColumnIndex(StateQuizDBHelper.STATES_COLUMN_NAME);
                    int capitalIndex = cursor.getColumnIndex(StateQuizDBHelper.STATES_COLUMN_CAPITAL);
                    int city2Index = cursor.getColumnIndex(StateQuizDBHelper.STATES_COLUMN_CITY2);
                    int city3Index = cursor.getColumnIndex(StateQuizDBHelper.STATES_COLUMN_CITY3);

                    if (idIndex >= 0) state.setId(cursor.getLong(idIndex));
                    if (nameIndex >= 0) state.setName(cursor.getString(nameIndex));
                    if (capitalIndex >= 0) state.setCapital(cursor.getString(capitalIndex));
                    if (city2Index >= 0) state.setCity2(cursor.getString(city2Index));
                    if (city3Index >= 0) state.setCity3(cursor.getString(city3Index));

                    states.add(state);
                }
            }
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Error getting quiz states: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Log.d(DEBUG_TAG, "Retrieved " + states.size() + " states for quiz");
        return states;
    }

    /**
     * Inner class representing Quiz state and metadata
     */
    public static class Quiz {
        private long id;              // Database ID
        private String date;          // Quiz date/time
        private int score;            // Current score
        private int questionsAnswered;// Number of completed questions

        public Quiz(long id, String date, int score, int questionsAnswered) {
            this.id = id;
            this.date = date;
            this.score = score;
            this.questionsAnswered = questionsAnswered;
        }

        // Getters
        public long getId() { return id; }
        public String getDate() { return date; }
        public int getScore() { return score; }
        public int getQuestionsAnswered() { return questionsAnswered; }
    }


    // Add this method after getQuizInProgress()
    public void saveQuizState(long quizId, int currentQuestion, int score, String selectedAnswer) {
        ContentValues values = new ContentValues();
        values.put(StateQuizDBHelper.QUIZ_COLUMN_QUESTIONS_ANSWERED, currentQuestion);
        values.put(StateQuizDBHelper.QUIZ_COLUMN_SCORE, score);
        values.put(StateQuizDBHelper.QUIZ_COLUMN_LAST_ANSWER, selectedAnswer);

        String whereClause = StateQuizDBHelper.QUIZ_COLUMN_ID + "=?";
        String[] whereArgs = {String.valueOf(quizId)};

        try {
            db.update(StateQuizDBHelper.TABLE_QUIZZES, values, whereClause, whereArgs);
            Log.d(DEBUG_TAG, "Saved quiz state: Question " + currentQuestion + ", Score " + score);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Error saving quiz state: " + e.getMessage());
        }
    }
}
