package edu.uga.cs.superfinalstatecapital;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * StateQuizDBHelper is a helper class for managing the SQLite database
 * used in the State Capitals Quiz application. It provides methods for
 * creating and upgrading the database schema.
 */
public class StateQuizDBHelper extends SQLiteOpenHelper {

    // Debug tag for logging
    private static final String DEBUG_TAG = "StateQuizDBHelper";

    // Database name and version
    private static final String DB_NAME = "statequiz.db";
    private static final int DB_VERSION = 2; // Updated version number for schema changes

    // Table and column names for "states" table
    public static final String TABLE_STATES = "states";
    public static final String STATES_COLUMN_ID = "_id";
    public static final String STATES_COLUMN_NAME = "name";
    public static final String STATES_COLUMN_CAPITAL = "capital";
    public static final String STATES_COLUMN_CITY2 = "city2";
    public static final String STATES_COLUMN_CITY3 = "city3";

    // Table and column names for "quizzes" table
    public static final String TABLE_QUIZZES = "quizzes";
    public static final String QUIZ_COLUMN_ID = "_id";
    public static final String QUIZ_COLUMN_DATE = "date";
    public static final String QUIZ_COLUMN_SCORE = "score";
    public static final String QUIZ_COLUMN_QUESTIONS_ANSWERED = "questions_answered";
    public static final String QUIZ_COLUMN_LAST_ANSWER = "last_answer"; // Newly added column

    // Table and column names for "quiz_questions" table
    public static final String TABLE_QUIZ_QUESTIONS = "quiz_questions";
    public static final String QUESTION_COLUMN_ID = "_id";
    public static final String QUESTION_COLUMN_QUIZ_ID = "quiz_id";
    public static final String QUESTION_COLUMN_STATE_ID = "state_id";
    public static final String QUESTION_COLUMN_USER_ANSWER = "user_answer";

    // Singleton instance
    private static StateQuizDBHelper helperInstance;

    // SQL statement to create "states" table
    private static final String CREATE_STATES =
            "CREATE TABLE " + TABLE_STATES + " ("
                    + STATES_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + STATES_COLUMN_NAME + " TEXT, "
                    + STATES_COLUMN_CAPITAL + " TEXT, "
                    + STATES_COLUMN_CITY2 + " TEXT, "
                    + STATES_COLUMN_CITY3 + " TEXT"
                    + ")";

    // SQL statement to create "quizzes" table
    private static final String CREATE_QUIZZES =
            "CREATE TABLE " + TABLE_QUIZZES + " ("
                    + QUIZ_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + QUIZ_COLUMN_DATE + " TEXT, "
                    + QUIZ_COLUMN_SCORE + " INTEGER, "
                    + QUIZ_COLUMN_QUESTIONS_ANSWERED + " INTEGER, "
                    + QUIZ_COLUMN_LAST_ANSWER + " TEXT"
                    + ")";

    // SQL statement to create "quiz_questions" table
    private static final String CREATE_QUIZ_QUESTIONS =
            "CREATE TABLE " + TABLE_QUIZ_QUESTIONS + " ("
                    + QUESTION_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + QUESTION_COLUMN_QUIZ_ID + " INTEGER, "
                    + QUESTION_COLUMN_STATE_ID + " INTEGER, "
                    + QUESTION_COLUMN_USER_ANSWER + " TEXT, "
                    + "FOREIGN KEY(" + QUESTION_COLUMN_QUIZ_ID + ") REFERENCES " + TABLE_QUIZZES + "(" + QUIZ_COLUMN_ID + "), "
                    + "FOREIGN KEY(" + QUESTION_COLUMN_STATE_ID + ") REFERENCES " + TABLE_STATES + "(" + STATES_COLUMN_ID + ")"
                    + ")";

    /**
     * Private constructor to enforce singleton pattern.
     *
     * @param context the application context
     */
    private StateQuizDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Returns the singleton instance of the database helper.
     *
     * @param context the application context
     * @return the singleton instance
     */
    public static synchronized StateQuizDBHelper getInstance(Context context) {
        if (helperInstance == null) {
            helperInstance = new StateQuizDBHelper(context.getApplicationContext());
        }
        return helperInstance;
    }

    /**
     * Creates the database tables.
     *
     * @param db the SQLiteDatabase object
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STATES);
        db.execSQL(CREATE_QUIZZES);
        db.execSQL(CREATE_QUIZ_QUESTIONS);
        Log.d(DEBUG_TAG, "Database tables created");
    }

    /**
     * Upgrades the database schema when the version number is increased.
     *
     * @param db         the SQLiteDatabase object
     * @param oldVersion the old database version
     * @param newVersion the new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add a new column to the "quizzes" table
            db.execSQL("ALTER TABLE " + TABLE_QUIZZES +
                    " ADD COLUMN " + QUIZ_COLUMN_LAST_ANSWER + " TEXT");
        }
        Log.d(DEBUG_TAG, "Database tables upgraded from version " + oldVersion +
                " to " + newVersion);
    }
}
