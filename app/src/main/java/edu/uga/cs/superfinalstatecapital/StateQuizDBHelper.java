package edu.uga.cs.superfinalstatecapital;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StateQuizDBHelper extends SQLiteOpenHelper {
    private static final String DEBUG_TAG = "StateQuizDBHelper";
    private static final String DB_NAME = "statequiz.db";
    private static final int DB_VERSION = 1;

    // Define all table and column names
    public static final String TABLE_STATES = "states";
    public static final String STATES_COLUMN_ID = "_id";
    public static final String STATES_COLUMN_NAME = "name";
    public static final String STATES_COLUMN_CAPITAL = "capital";
    public static final String STATES_COLUMN_CITY2 = "city2";
    public static final String STATES_COLUMN_CITY3 = "city3";

    public static final String TABLE_QUIZZES = "quizzes";
    public static final String QUIZ_COLUMN_ID = "_id";
    public static final String QUIZ_COLUMN_DATE = "date";
    public static final String QUIZ_COLUMN_SCORE = "score";
    public static final String QUIZ_COLUMN_QUESTIONS_ANSWERED = "questions_answered";

    public static final String TABLE_QUIZ_QUESTIONS = "quiz_questions";
    public static final String QUESTION_COLUMN_ID = "_id";
    public static final String QUESTION_COLUMN_QUIZ_ID = "quiz_id";
    public static final String QUESTION_COLUMN_STATE_ID = "state_id";
    public static final String QUESTION_COLUMN_USER_ANSWER = "user_answer";

    private static StateQuizDBHelper helperInstance;

    private static final String CREATE_STATES =
            "CREATE TABLE " + TABLE_STATES + " ("
                    + STATES_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + STATES_COLUMN_NAME + " TEXT, "
                    + STATES_COLUMN_CAPITAL + " TEXT, "
                    + STATES_COLUMN_CITY2 + " TEXT, "
                    + STATES_COLUMN_CITY3 + " TEXT"
                    + ")";

    private static final String CREATE_QUIZZES =
            "CREATE TABLE " + TABLE_QUIZZES + " ("
                    + QUIZ_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + QUIZ_COLUMN_DATE + " TEXT, "
                    + QUIZ_COLUMN_SCORE + " INTEGER, "
                    + QUIZ_COLUMN_QUESTIONS_ANSWERED + " INTEGER"
                    + ")";

    private static final String CREATE_QUIZ_QUESTIONS =
            "CREATE TABLE " + TABLE_QUIZ_QUESTIONS + " ("
                    + QUESTION_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + QUESTION_COLUMN_QUIZ_ID + " INTEGER, "
                    + QUESTION_COLUMN_STATE_ID + " INTEGER, "
                    + QUESTION_COLUMN_USER_ANSWER + " TEXT, "
                    + "FOREIGN KEY(" + QUESTION_COLUMN_QUIZ_ID + ") REFERENCES " + TABLE_QUIZZES + "(" + QUIZ_COLUMN_ID + "), "
                    + "FOREIGN KEY(" + QUESTION_COLUMN_STATE_ID + ") REFERENCES " + TABLE_STATES + "(" + STATES_COLUMN_ID + ")"
                    + ")";

    private StateQuizDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized StateQuizDBHelper getInstance(Context context) {
        if (helperInstance == null) {
            helperInstance = new StateQuizDBHelper(context.getApplicationContext());
        }
        return helperInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STATES);
        db.execSQL(CREATE_QUIZZES);
        db.execSQL(CREATE_QUIZ_QUESTIONS);
        Log.d(DEBUG_TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZZES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATES);
        onCreate(db);
        Log.d(DEBUG_TAG, "Database tables upgraded");
    }
}